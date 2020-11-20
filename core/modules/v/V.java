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

/*
The actual module core.
It loads the config.
Creates all needed classes.

It can be used to create both servers & clients.
*/

import java.io.*;
import java.util.*;
import java.sql.*;
import borknet_services.core.*;

public class V implements Modules
{
	private Core C;
	private Server ser;
	private String description = "";
	private String nick = "";
	private String ident = "";
	private String host = "";
	private String numeric = "";
	private String num = "AAA";
	private String reportchan = "";
	private String vhost = "";
	private boolean automatic = true;
	private boolean qwebirc = false;
 private String qhost = "webchat@borknet.org";
 private String qident = "webchat";
	private ArrayList<Object> cmds = new ArrayList<Object>();
	private ArrayList<String> cmdn = new ArrayList<String>();

	public V()
	{
	}

	public void start(Core C)
	{
		this.C = C;
		load_conf();
		numeric = C.get_numeric();
		ser = new Server(C,this);
		C.cmd_create_service(num, nick, ident, host, "+oXwkgsr", description);
		reportchan = C.get_reportchan();
		C.cmd_join(numeric, num, reportchan);
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
		C.cmd_kill_service(numeric+num, "Quit: Soon will I rest, yes, forever sleep. Earned it I have. Twilight is upon me, soon night must fall.");
	}

	public void hstop()
	{
		C.cmd_kill_service(numeric+num, "Quit: Happens to every guy sometimes this does.");
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
			/* remove the next line if you build a client only */
			num = dataSrc.getProperty("numeric");
			automatic = Boolean.parseBoolean(dataSrc.getProperty("automatic"));
			qwebirc = Boolean.parseBoolean(dataSrc.getProperty("qwebirc"));
   qhost = dataSrc.getProperty("qhost");
   qident = dataSrc.getProperty("qident");
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
	public boolean get_automatic()
	{
		return automatic;
	}
	public String get_vhost()
	{
		return vhost;
	}
	public boolean get_qwebirc()
	{
		return qwebirc;
	}
	public String get_qhost()
	{
		return qhost;
	}
	public String get_qident()
	{
		return qident;
	}

	public void clean()
	{
		//no need
	}
}