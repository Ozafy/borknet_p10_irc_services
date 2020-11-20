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
import java.io.*;
import java.util.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Addchan implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Addchan()
	{
	}

	public void parse_command(Core C, S Bot, String numeric, String botnum, String username, String params)
	{
		DBControl dbc = Bot.getDBC();
		//check if he's an operator and has a high enough level to kill me
		if(dbc.getUser(username).getOperator())
		{
			String[] result = params.split("\\s");
			try
			{
				//get the stuff
				String channel = result[1];
				//bad, bad coding
				if(!channel.startsWith("#")) throw new ArrayIndexOutOfBoundsException();
				String flags = dbc.getChanFlags(channel);
				if(flags instanceof String)
				{
					C.cmd_notice(numeric,botnum,username, "Channel is already registered.");
					return;
				}
				dbc.addChan(channel,"n");
				C.cmd_join(numeric,botnum,channel);
				C.cmd_notice(numeric,botnum,username, "Done");
				return;
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.cmd_notice(numeric,botnum,username , "/msg " + Bot.get_nick() + " addchan #channel");
				return;
			}
		}
		//user doesn't have access, that bastard!
		else
		{
			C.cmd_notice(numeric,botnum,username, "This command is either unknown, or you need to be opered up to use it.");
			return;
		}
	}

	public void parse_help(Core C, S Bot, String numeric, String botnum, String username, boolean operator)
	{
		if(operator)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " addchan #channel");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
			return;
		}
	}
	public void showcommand(Core C, S Bot, String numeric, String botnum, String username, boolean operator)
	{
		if(operator)
		{
			C.cmd_notice(numeric, botnum, username, "ADDCHAN             Makes the bot join a channel.");
		}
	}
}