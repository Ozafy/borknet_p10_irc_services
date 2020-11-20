/**
#
# BorkNet Services Core
#

#
# Copyright (C) 2004 Ozafy - ozafy@borknet.org - http://www.borknet.org
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
#
*/
import java.sql.*;
import java.util.*;
import java.io.*;
import java.security.*;
import borknet_services.core.*;

/**
 * The database communication class of the Q IRC C.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class DBControl
{
	/** Main bot */
	private Core C;
 
 private String dbname = "services.db";

	private CoreDBControl dbc;

	private S Bot;

	private HashMap<String,String> channels = new HashMap<String,String>();
	private HashMap<String,String> userMsg = new HashMap<String,String>();
	private HashMap<String,Integer> userPoints = new HashMap<String,Integer>();
	private int kills = 1;

	/**
	 * Constructs a Database connection.
	 * @param server		Database server
	 * @param user			Database user
	 * @param pass			Database password
	 * @param db			Database
	 * @param debug			Are we debugging?
	 * @param B				Main bot
	 */
	public DBControl(Core C, S Bot)
	{
		try
		{
			this.C = C;
			this.Bot = Bot;
			this.dbc = C.get_dbc();
			testDriver();
   C.printDebug("[>---<] >> *** Checking SQL db...");
   createTables();
			C.printDebug( "[>---<] >> *** Done." );
			load();
		}
		catch(Exception e)
		{
			C.printDebug("Database error!");
   C.debug(e);
			C.die("SQL error, trying to die gracefully.");
		}
	}
 
	private void testDriver ( )
	{
		try
		{
			Class.forName ( "org.sqlite.JDBC" );
			C.printDebug( "[>---<] >> *** SQL Driver Found" );
		}
		catch ( java.lang.ClassNotFoundException e )
		{
			C.printDebug("SQL JDBC Driver not found!");
			C.die("SQL error, trying to die gracefully.");
		}
	}
 
 private void createTables()
 {
  Connection con = null;
  try
  {
   con = DriverManager.getConnection("jdbc:sqlite:"+dbname);
   Statement statement = con.createStatement();
   statement.setQueryTimeout(30);
   statement.executeUpdate("CREATE TABLE IF NOT EXISTS s_channels (name string, flags string)");
  }
		catch ( Exception e )
		{
			C.printDebug("Database error!");
   C.debug(e);
			C.die("SQL error, trying to die gracefully.");
		}
  finally
  {
   try
   {
    if(con != null)
    {
     con.close();
    }
   }
   catch(Exception e)
   {
    C.printDebug("Database error!");
    C.debug(e);
    C.die("SQL error, trying to die gracefully.");
   }
  }
 }
  
 private void executeUpdate(String query, String[] params)
 {
  Connection con = null;
  try
  {
   con = DriverManager.getConnection("jdbc:sqlite:"+dbname);
   PreparedStatement pstmt = con.prepareStatement(query);
   for(int i=0;i<params.length;i++)
   {
    pstmt.setString((i+1),params[i]);
   }
   pstmt.executeUpdate();
  }
		catch ( Exception e )
		{
			C.printDebug("Database error!");
   C.debug(e);
			C.die("SQL error, trying to die gracefully.");
		}
  finally
  {
   try
   {
    if(con != null)
    {
     con.close();
    }
   }
   catch(Exception e)
   {
    C.printDebug("Database error!");
    C.debug(e);
    C.die("SQL error, trying to die gracefully.");
   }
  }
 }

	public boolean chanExists(String chan)
	{
		return dbc.chanExists(chan);
	}

	public boolean SchanExists(String chan)
	{
		return channels.containsKey(chan.toLowerCase());
	}
 
	public List<String> getChanTable()
	{
		List<String> keys = new ArrayList<String>(channels.keySet());
		return keys;
	}

	public void load()
	{
  Connection con = null;
  try
  {
   con = DriverManager.getConnection("jdbc:sqlite:"+dbname);
   Statement statement = con.createStatement();
   statement.setQueryTimeout(30);
   ResultSet rs = statement.executeQuery("SELECT name,flags FROM s_channels");
			ArrayList<String> a = new ArrayList<String>();
			while(rs.next())
			{
				channels.put(rs.getString("name").toLowerCase(),rs.getString("flags"));
			}
  }
		catch ( Exception e )
		{
			C.printDebug("Database error!");
   C.debug(e);
			C.die("SQL error, trying to die gracefully.");
		}
  finally
  {
   try
   {
    if(con != null)
    {
     con.close();
    }
   }
   catch(Exception e)
   {
    C.printDebug("Database error!");
    C.debug(e);
    C.die("SQL error, trying to die gracefully.");
   }
  }
	}

	public String getChanFlags(String channel)
	{
		return channels.get(channel.toLowerCase());
	}

	public int getPoints(String user)
	{
		return userPoints.get(user);
	}

	public int getID()
	{
		return kills++;
	}

	public boolean isService(String numeric)
	{
		return dbc.isService(numeric);
	}

	public boolean repeat(String user, String msg)
	{
		try
		{
			String m = userMsg.get(user);
			return m.equals(msg);
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public void delChan(String chan)
	{
  executeUpdate("DELETE FROM s_channels WHERE name = ?", new String[] {chan});
  channels.remove(chan.toLowerCase());
	}

	public void delPoints(int points)
	{
		List<String> users = new ArrayList<String>(userPoints.keySet());
		for(String user : users)
		{
			int p = userPoints.get(user);
			p -= points;
			if(p<0)
			{
				userPoints.remove(user);
				userMsg.remove(user);
			}
			else
			{
				userPoints.put(user,p);
			}
		}
	}

	public void addPoints(String user, int points)
	{
		Integer p = userPoints.get(user);
		if(p instanceof Integer)
		{
			userPoints.put(user,(points+p));
		}
		else
		{
			userPoints.put(user,points);
		}
	}

	public void addChan(String chan, String flags)
	{
  executeUpdate("INSERT INTO s_channels VALUES (?,?)", new String[] {chan, flags});
		channels.put(chan.toLowerCase(),flags);
	}

	public boolean setChanFlags(String chan, String flags)
	{
		try
		{
			executeUpdate("UPDATE s_channels SET flags = ? WHERE name = ?", new String[] {flags, chan});
			channels.put(chan.toLowerCase(),flags);
			return true;
		}
		catch ( Exception e )
		{
			return false;
		}
	}

	public void setMsg(String user, String msg)
	{
		userMsg.put(user,msg);
	}

	public int getChanUsers(String channel)
	{
		return dbc.getChanUsers(channel);
	}

	public void clean()
	{
		List<String> channelkeys = new ArrayList<String>(channels.keySet());
		for(String channel : channelkeys)
		{
			if(getChanUsers(channel) < 1)
			{
				delChan(channel);
			}
		}
	}

	public User getUser(String numer)
	{
		return dbc.getUser(numer);
	}
}