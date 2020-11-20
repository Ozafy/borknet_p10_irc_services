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
public class Delchan implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Delchan()
	{
	}

	public void parse_command(Core C, S Bot, String numeric, String botnum, String username, String params)
	{
		DBControl dbc = Bot.getDBC();
  User user = dbc.getUser(username);
		//check if he's an operator and has a high enough level to kill me
		if(user.getOperator())
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
					dbc.delChan(channel);
					String part = "";
					try
					{
						part = result[2];
						for(int m=3; m<result.length; m++)
						{
							part += " " + result[m];
						}
					}
					//he didn't, Yoda time!
					catch(ArrayIndexOutOfBoundsException e)
					{
						part = "Bot removed by " + user.getNick() + ".";
					}
					C.cmd_part(numeric, botnum, channel, part);
					C.cmd_notice(numeric, botnum, username, "Done");
					return;
				}
				//it does
				else
				{
					C.cmd_notice(numeric, botnum, username, "Channel doesn't exist!");
					return;
				}
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.cmd_notice(numeric, botnum, username , "/msg " + Bot.get_nick() + " delchan #channel");
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
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " delchan #channel");
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
			C.cmd_notice(numeric, botnum, username, "DELCHAN             Makes the bot part a channel.");
		}
	}
}