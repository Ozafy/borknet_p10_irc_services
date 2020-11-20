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
# MERCHANTABotILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Botoston, MA  02111-1307, USA.
#
*/
import java.io.*;
import java.util.*;
import java.sql.*;
import borknet_services.core.*;

public class S implements Modules
{
	private Core C;
	private Server ser;
	private DBControl dbc;
	private DelPoints DP;
	private String description = "";
	private String nick = "";
	private String ident = "";
	private String host = "";
	private String pass = "";
	private String numeric = "";
	private String num = "";
	private String reportchan = "";
	private String rules = "";
	private ArrayList<Object> cmds = new ArrayList<Object>();
	private ArrayList<String> cmdn = new ArrayList<String>();
 
 private int spamtext=16;
 private int spamchan=64;
 private int spamweb=64;
 private int spamrepeat=24;
 private int spamwarning=80;
 private int spamkill=100;

	public S()
	{
	}

	public void start(Core C)
	{
		this.C = C;
		load_conf();
		numeric = C.get_numeric();
		dbc = new DBControl(C,this);
		ser = new Server(C,dbc,this);
		DP = new DelPoints(dbc);
		Thread th1 = new Thread(DP);
		th1.setDaemon(true);
		th1.start();
		C.cmd_create_service(num, nick, ident, host, "+oXwkgr", description);
		reportchan = C.get_reportchan();
		C.cmd_join(numeric, num, reportchan);
		List<String> channels = dbc.getChanTable();
		//join my channels and set my modes
		for(String channel : channels)
		{
			C.cmd_join(numeric,num,channel);
		}
	}

	public void setCmnds(ArrayList<Object> cmds,ArrayList<String> cmdn)
	{
		this.cmds = cmds;
		this.cmdn = cmdn;
	}

	public ArrayList<Object> getCmds()
	{
		return cmds;
	}

	public ArrayList<String> getCmdn()
	{
		return cmdn;
	}

	public void stop()
	{
		DP.stop();
		C.cmd_kill_service(numeric+num, "Quit: Spam away you trolls.");
	}

	public void hstop()
	{
		DP.stop();
		C.cmd_kill_service(numeric+num, "Quit: My bananas! Noes!");
	}

	private void load_conf()
	{
		try
		{
			ConfLoader loader = new ConfLoader(C,"core/modules/"+this.getClass().getName().toLowerCase()+"/"+this.getClass().getName().toLowerCase()+".conf");
			loader.load();
			Properties dataSrc = loader.getVars();
			//set all the config file vars
			description = dataSrc.getProperty("description");
			nick = dataSrc.getProperty("nick");
			ident = dataSrc.getProperty("ident");
			host = dataSrc.getProperty("host");
			pass = dataSrc.getProperty("pass");
			num = dataSrc.getProperty("numeric");
			rules = dataSrc.getProperty("rules");
   spamtext=Integer.parseInt(dataSrc.getProperty("spamtext"));
   spamchan=Integer.parseInt(dataSrc.getProperty("spamchan"));
   spamweb=Integer.parseInt(dataSrc.getProperty("spamweb"));
   spamrepeat=Integer.parseInt(dataSrc.getProperty("spamrepeat"));
   spamwarning=Integer.parseInt(dataSrc.getProperty("spamwarning"));
   spamkill=Integer.parseInt(dataSrc.getProperty("spamkill"));
		}
		catch(Exception e)
		{
			C.printDebug("Error loading configfile.");
			C.debug(e);
			C.die("SQL error, trying to die gracefully.");
		}
	}

	public void parse(ArrayList<String> params)
	{
		try
		{
			ser.parse(params);
		}
		catch(Exception e)
		{
			C.debug(e);
		}
	}

	public String get_num()
	{
		return numeric;
	}
	public String get_corenum()
	{
		return num;
	}
	public String get_nick()
	{
		return nick;
	}
	public String get_host()
	{
		return host;
	}
	public String getRules()
	{
		return rules;
	}
 
	public int getSpamtext()
	{
		return spamtext;
	}
	public int getSpamchan()
	{
		return spamchan;
	}
	public int getSpamweb()
	{
		return spamweb;
	}
	public int getSpamrepeat()
	{
		return spamrepeat;
	}
	public int getSpamwarning()
	{
		return spamwarning;
	}
	public int getSpamkill()
	{
		return spamkill;
	}
 
	public DBControl getDBC()
	{
		return dbc;
	}
	public void clean()
	{
		dbc.clean();
	}
}