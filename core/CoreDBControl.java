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
package borknet_services.core;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.security.*;
import borknet_services.core.*;

public class CoreDBControl
{
	private Core C;

	private HashMap<String,User> usersByNumeric = new HashMap<String,User>();
	private HashMap<String,User> usersByNick = new HashMap<String,User>();
	private HashMap<String,ArrayList<User>> usersByAuth = new HashMap<String,ArrayList<User>>();
	private HashMap<String,ArrayList<User>> usersByHost = new HashMap<String,ArrayList<User>>();
	private HashMap<String,ArrayList<User>> usersByIP = new HashMap<String,ArrayList<User>>();

	private HashMap<String,Server> serversByNumeric = new HashMap<String,Server>();
	private HashMap<String,ArrayList<Server>> serversByHub = new HashMap<String,ArrayList<Server>>();
	private HashMap<String,Server> serversByHost = new HashMap<String,Server>();
 
	private HashMap<String,Channel> channels = new HashMap<String,Channel>();

	public CoreDBControl(Core C)
	{
		this.C = C;
	}

	public void cleanDB()
	{
  usersByNumeric = new HashMap<String,User>();
  usersByNick = new HashMap<String,User>();
  usersByAuth = new HashMap<String,ArrayList<User>>();
  usersByHost = new HashMap<String,ArrayList<User>>();
  usersByIP = new HashMap<String,ArrayList<User>>();
	}

	public boolean chanExists(String chan)
	{
  Channel c = channels.get(chan.toLowerCase());
		return (c instanceof Channel);
	}
 
	public Channel getChannel(String chan)
	{
		return channels.get(chan.toLowerCase());
	}

	public boolean authOnline(String auth)
	{
		return usersByAuth.containsKey(auth.toLowerCase());
	}

	public String getNumViaAuth(String auth)
	{
		User u = usersByAuth.get(auth.toLowerCase()).get(0);
		if(u instanceof User)
		{
			return u.getNumeric();
		}
		else
		{
			return "0";
		}
	}
 
	public String getNumViaNick(String nick)
	{
		User u = usersByNick.get(nick.toLowerCase());
		if(u instanceof User)
		{
			return u.getNumeric();
		}
		else
		{
			return "0";
		}
	}

	public boolean isService(String numeric)
	{
		Server s = serversByNumeric.get(numeric.substring(0,2));
		if(s instanceof Server)
		{
			return s.getService();
		}
		else
		{
			return false;
		}
	}

	public String getServer(String numeric)
	{
		Server s = serversByNumeric.get(numeric.substring(0,2));
		if(s instanceof Server)
		{
			return s.getHost();
		}
		else
		{
			return "unknown";
		}
	}

	public int getServerCount()
	{
		return serversByNumeric.size();
	}

	public boolean isServerNumeric(String numer)
	{
		Server s = serversByNumeric.get(numer);
		if(s instanceof Server)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean isOpChan(String user, String channel)
	{
		Channel c = channels.get(channel.toLowerCase());
		if(c instanceof Channel)
		{
			return c.isop(user);
		}
		else
		{
			return false;
		}
	}

	public boolean isNickUsed(String nick)
	{
		return usersByNick.containsKey(nick.toLowerCase());
	}

	public boolean isOnChan(String user, String channel)
	{
		Channel c = channels.get(channel.toLowerCase());
		if(c instanceof Channel)
		{
			return c.ison(user);
		}
		else
		{
			return false;
		}
	}

	public boolean chanHasOps(String channel)
	{
		Channel c = channels.get(channel.toLowerCase());
		if(c instanceof Channel)
		{
			return c.hasop();
		}
		else
		{
			return false;
		}
	}

	public int getChanUsers(String channel)
	{
		Channel c = channels.get(channel.toLowerCase());
		if(c instanceof Channel)
		{
			return c.getUsercount();
		}
		else
		{
			return 0;
		}
	}

	public int getAuthUsers(String auth)
	{
		try
		{
			return usersByAuth.get(auth.toLowerCase()).size();
		}
		catch(Exception e)
		{
			return 0;
		}
	}

	public int getHostCount(String host)
	{
		try
		{
			return usersByHost.get(host.toLowerCase()).size();
		}
		catch(Exception e)
		{
			return 0;
		}
	}

	public int getIpCount(String ip)
	{
		try
		{
			return usersByIP.get(ip).size();
		}
		catch(Exception e)
		{
			return 0;
		}
	}

	public HashMap<String,User> getUsers()
	{
		return usersByNumeric;
	}

	public User getUserViaAuth(String auth)
	{
		return usersByAuth.get(auth.toLowerCase()).get(0);
	}

	public User getUserViaHost(String host)
	{
		return usersByHost.get(host.toLowerCase()).get(0);
	}

	public User getUserViaNick(String nick)
	{
		return usersByNick.get(nick.toLowerCase());
	}

	public User getUser(String user)
	{
  return usersByNumeric.get(user);
	}

	public ArrayList<String> getUserChans(String user)
	{
  User u = usersByNumeric.get(user);
  if(u instanceof User)
  {
   return u.getChannels();
  }
  else
  {
   return new ArrayList<String>();
  }
	}
 
	public int getChannelCount()
	{
  return channels.size();
	}
 
	public ArrayList<User> getUserRowsViaAuth(String auth)
	{
		return usersByAuth.get(auth.toLowerCase());
	}

	public void setUserField(String numer, int colum, String info)
	{
		User u = usersByNumeric.get(numer);
		if(u instanceof User)
		{
			try
			{
				switch(colum)
				{
					case 0:
						u.setNumeric(info);
						usersByNumeric.remove(numer);
						usersByNumeric.put(info,u);
						break;
					case 1:
						String oldkey = u.getNick();
						u.setNick(info);
						if(usersByNick.containsKey(oldkey.toLowerCase()))
						{
							usersByNick.remove(oldkey.toLowerCase());
						}
						usersByNick.put(info.toLowerCase(),u);
						break;
					case 2:
						String[] splithost = info.split("@");
						u.setIdent(splithost[0]);
						u.setHost(splithost[1]);
						if(usersByHost.containsKey(info.toLowerCase()))
						{
							ArrayList<User> users = usersByHost.get(info.toLowerCase());
							users.add(u);
							usersByHost.put(info.toLowerCase(),users);
						}
						else
						{
							ArrayList<User> users = new ArrayList<User>();
							users.add(u);
							usersByHost.put(info.toLowerCase(),users);
						}
						break;
					case 3:
						u.setModes(info);
						break;
					case 4:
						u.setAuth(info);
						if(usersByAuth.containsKey(info.toLowerCase()))
						{
							ArrayList<User> users = usersByAuth.get(info.toLowerCase());
							users.add(u);
							usersByAuth.put(info.toLowerCase(),users);
						}
						else
						{
							ArrayList<User> users = new ArrayList<User>();
							users.add(u);
							usersByAuth.put(info.toLowerCase(),users);
						}
						break;
					case 5:
							u.setOperator(Boolean.parseBoolean(info));
						break;
					case 6:
						u.setServer(info);
						break;
					case 7:
						u.setIp(info);
						if(usersByIP.containsKey(info))
						{
							ArrayList<User> users = usersByIP.get(info);
							users.add(u);
							usersByIP.put(info.toLowerCase(),users);
						}
						else
						{
							ArrayList<User> users = new ArrayList<User>();
							users.add(u);
							usersByIP.put(info,users);
						}
						break;
					case 8:
						u.setFakehost(info);
						break;
				}
			}
			catch ( Exception e )
			{
				System.out.println( "Error finding user." );
				C.debug(e);
				C.die("SQL error, trying to die gracefully.");
			}
		}
		else
		{
			System.out.println( "Error finding user." );
			C.printDebug("Error finding user.");
		}
	}

	public void setUserChanMode(String user, String chan, String mode)
	{
  Channel c = channels.get(chan.toLowerCase());
  if(c instanceof Channel)
  {
   c.setUserChanMode(user,mode);
  }
	}

	public void delUser(String numer)
	{
		try
		{
			User u = usersByNumeric.get(numer);
			usersByNick.remove(u.getNick().toLowerCase());
			ArrayList<User> users = usersByAuth.get(u.getAuth().toLowerCase());
			if(users instanceof ArrayList)
			{
				users.remove(u);
				if(users.size()>0)
				{
					usersByAuth.put(u.getAuth(),users);
				}
				else
				{
					usersByAuth.remove(u.getAuth());
				}
			}
			users = usersByHost.get(u.getHost());
			users.remove(u);
			if(users.size()>0)
			{
				usersByHost.put(u.getHost(),users);
			}
			else
			{
				usersByHost.remove(u.getHost());
			}
			users = usersByIP.get(u.getIp());
			users.remove(u);
			if(users.size()>0)
			{
				usersByIP.put(u.getIp(),users);
			}
			else
			{
				usersByIP.remove(u.getIp());
			}
			usersByNumeric.remove(numer);
			System.gc();
   ArrayList<String> userchannels = u.getChannels();
   for(String channel: userchannels)
   {
    delUserChan(channel, numer);
   }
		}
		catch ( Exception e )
		{
		}
	}

	public void delServer(String host)
	{
		Server s = serversByHost.get(host.toLowerCase());
		if(s instanceof Server)
		{
   delChildren(host);
			String numer = s.getNumeric();
   String hub = s.getHub();
   ArrayList<Server> servers = serversByHub.get(hub);
			if(servers instanceof ArrayList)
			{
				for(int i=0; i<servers.size(); i++)
				{
					if(servers.get(i).equals(s))
     {
      servers.remove(i);
      break;
     }
				}
			}
		}
		else
		{
			System.out.println ( "Error Removing server: "+host );
			C.die("SQL error, trying to die gracefully.");
		}
	}
  
	public void delChildren(String host)
	{
		Server s = serversByHost.get(host.toLowerCase());
		if(s instanceof Server)
		{
			String numer = s.getNumeric();
			ArrayList<Server> servers = serversByHub.get(numer);
			if(servers instanceof ArrayList)
			{
    for(Server ser : servers)
    {
     delChildren(ser.getHost());
    }
			}
			ArrayList<String> numerics = new ArrayList<String>(usersByNumeric.keySet());
			for(String n : numerics)
			{
				if(n.startsWith(numer))
				{
					delUser(n);
				}
			}
			serversByHub.remove(numer);
			serversByNumeric.remove(numer);
			serversByHost.remove(host.toLowerCase());
		}
		else
		{
			System.out.println ( "Error Removing server: "+host );
			C.die("SQL error, trying to die gracefully.");
		}
	}

	public void delUserChan(String chan, String user)
	{
  Channel c = channels.get(chan.toLowerCase());
  if(c instanceof Channel)
  {
   c.delUser(user);
  }
  if(c.getUsercount()<=0)
  {
   channels.remove(chan.toLowerCase());
  }
  User u = usersByNumeric.get(user);
  if(u instanceof User)
  {
   u.partChannel(chan);
  }
	}

	public void addUser(String nume,String nick, String host, String mode, String auth, boolean isop, String server, String ip, String fake)
	{
		try
		{
			User u = new User(nume);
			u.setNick(nick);
			String[] splithost = host.split("@");
			u.setIdent(splithost[0]);
			u.setHost(splithost[1]);
			u.setModes(mode);
			u.setAuth(auth);
			u.setOperator(isop);
			u.setServer(server);
			u.setIp(ip);
			u.setFakehost(fake);
			usersByNumeric.put(nume,u);
			usersByNick.put(nick.toLowerCase(),u);
			if(!auth.equals("0"))
			{
				if(usersByAuth.containsKey(auth.toLowerCase()))
				{
					ArrayList<User> users = usersByAuth.get(auth.toLowerCase());
					users.add(u);
					usersByAuth.put(auth.toLowerCase(),users);
				}
				else
				{
					ArrayList<User> users = new ArrayList<User>();
					users.add(u);
					usersByAuth.put(auth.toLowerCase(),users);
				}
			}
			if(usersByHost.containsKey(splithost[1]))
			{
				ArrayList<User> users = usersByHost.get(splithost[1]);
				users.add(u);
				usersByHost.put(splithost[1],users);
			}
			else
			{
				ArrayList<User> users = new ArrayList<User>();
				users.add(u);
				usersByHost.put(splithost[1],users);
			}
			if(usersByIP.containsKey(ip))
			{
				ArrayList<User> users = usersByIP.get(ip);
				users.add(u);
				usersByIP.put(ip,users);
			}
			else
			{
				ArrayList<User> users = new ArrayList<User>();
				users.add(u);
				usersByIP.put(ip,users);
			}
		}
		catch ( Exception e )
		{
			System.out.println ( "Error executing statement" );
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
		}
	}

	public void addServer(String numer, String host ,String hub, boolean service)
	{
		try
		{
			Server s = new Server(numer);
			s.setHost(host.toLowerCase());
			s.setHub(hub);
			s.setService(service);
			serversByHost.put(host.toLowerCase(),s);
			serversByNumeric.put(numer,s);
			if(serversByHub.containsKey(hub))
			{
				ArrayList<Server> servers = serversByHub.get(hub);
				servers.add(s);
				serversByHub.put(hub,servers);
			}
			else
			{
				ArrayList<Server> servers = new ArrayList<Server>();
				servers.add(s);
				serversByHub.put(hub,servers);
			}
		}
		catch (Exception e)
		{
			System.out.println ( "Error executing statement" );
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
		}
	}

	public void addUserChan(String channel,String user,String timestamp, Boolean isop, Boolean isvoice)
	{
  Channel c = channels.get(channel.toLowerCase());
  if(c instanceof Channel)
  {
   c.addUser(user,isop,isvoice);
  }
  else
  {
   c = new Channel(channel,timestamp,user,isop,isvoice);
   channels.put(channel.toLowerCase(),c);
  }
  User u = usersByNumeric.get(user);
  if(u instanceof User)
  {
   u.joinChannel(channel);
  }
	}
}