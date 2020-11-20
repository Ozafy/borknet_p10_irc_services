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
import java.text.*;
import java.util.regex.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Chanflags implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Chanflags()
	{
	}

	public void parse_command(Core C, S Bot, String numeric, String botnum, String username, String params)
	{
		DBControl dbc = Bot.getDBC();
		if(dbc.getUser(username).getOperator())
		{
			String[] result = params.split("\\s");
			try
			{
				//get some info from what the user asks
				String channel = result[1];
				String flags = result[2];
				if(flags.length() > 1)
				{
					C.cmd_notice(numeric, botnum, username, "You can only set one flag.");
					return;
				}
				//need to check they don't remove the a flag
				Pattern pat = Pattern.compile("[^din]");
				Matcher m = pat.matcher(flags);
				StringBuffer sb = new StringBuffer();
				boolean nok = m.find();
				while(nok)
				{
					m.appendReplacement(sb, "");
					nok = m.find();
				}
				m.appendTail(sb);
				flags = sb.toString();
				//he has access
				if(flags.length() < 1)
				{
					C.cmd_notice(numeric, botnum, username, "Invalid flag.");
					return;
				}
				if(dbc.setChanFlags(channel,flags))
				{
					if(flags.equals("i"))
					{
						C.cmd_privmsg(numeric, botnum, channel, "Instagib is now enabled so please refrain from talking.");
					}
					else if(flags.equals("n"))
					{
						C.cmd_privmsg(numeric, botnum, channel, "Scanning normally.");
					}
					else if(flags.equals("d"))
					{
						C.cmd_privmsg(numeric, botnum, channel, "Deaf mode is now enabled, spam away!");
					}
					C.cmd_notice(numeric, botnum, username, "Done.");
				}
				else
				{
					C.cmd_notice(numeric, botnum, username, "Can't find that channel!");
				}
				return;
			}
			//he asked to see the flags, or asked nothing
			catch(ArrayIndexOutOfBoundsException e)
			{
				try
				{
					//get the channel
					String channel = result[1];
					//check which channel
					String flags = dbc.getChanFlags(channel);
					if(flags.equals("0"))
					{
						C.cmd_notice(numeric, botnum, username, "Can't find that channel!");
						return;
					}
					//it does, return the flags set
					else
					{
						C.cmd_notice(numeric, botnum, username, "Current channel flags are: +" + flags);
						return;
					}
				}
				//he asked nothing
				catch(ArrayIndexOutOfBoundsException f)
				{
					C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " chanflags <#channel> [flag]");
					C.cmd_notice(numeric, botnum, username, "Possible flags are:");
					C.cmd_notice(numeric, botnum, username, "d: Deaf.");
					C.cmd_notice(numeric, botnum, username, "i: Instagib.");
					C.cmd_notice(numeric, botnum, username, "n: Normal.");
					return;
				}
			}
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
			return;
		}
	}

	public void parse_help(Core C, S Bot, String numeric, String botnum, String username, boolean operator)
	{
		if(operator)
		{
			C.cmd_notice(numeric, botnum, username, "/msg " + Bot.get_nick() + " chanflags <#channel> [flag]");
			C.cmd_notice(numeric, botnum, username, "Possible flags are:");
			C.cmd_notice(numeric, botnum, username, "d: Deaf.");
			C.cmd_notice(numeric, botnum, username, "i: Instagib.");
			C.cmd_notice(numeric, botnum, username, "n: Normal.");
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
			C.cmd_notice(numeric, botnum, username, "CHANFLAGS           Will change/display a channel's flags.");
		}
	}
}