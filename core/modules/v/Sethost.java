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


/*

A very basic command, replies to /msg moo with /notice Moo!

*/


import java.io.*;
import java.util.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Sethost implements Command
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Sethost()
	{
	}

	public void parse_command(Core C, V Bot, String numeric, String botnum, String username, String params)
	{
		User user = C.get_dbc().getUser(username);
		if(user.getOperator())
		{
			String[] result = params.split("\\s");
			try
			{
    String forcenick = user.getNick();
    String ident = result[1];
    String host = result[2];
    if(result.length>3)
    {
     forcenick = result[1];
     ident = result[2];
     host = result[3];
    }
				User forceuser = C.get_dbc().getUserViaNick(forcenick);
				if(forceuser instanceof User)
				{
     if(ident.matches("[\\w]*") && host.matches("[\\w.]*"))
     {
      C.cmd_sethost(forceuser.getNumeric(), ident, host);
      C.cmd_notice(numeric, botnum, username, "Done.");
     }
     else
     {
      C.cmd_notice(numeric, botnum, username, "Please only use word characters.");
     }
    }
    else
    {
					C.cmd_notice(numeric, botnum, username, "Who on earth is that?");
				}
			}
			//he didn't, Yoda time!
			catch(Exception e)
			{
				C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" sethost [nickname] <ident> <host>");
				return;
			}
		}
		//user doesn't have access, that bastard!
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
			return;
		}
	}

	public void parse_help(Core C, V Bot, String numeric, String botnum, String username, boolean operator)
	{
		if(operator)
		{
			C.cmd_notice(numeric, botnum, username, "/msg "+Bot.get_nick()+" sethost [nickname] <ident> <host>");
		}
		else
		{
			C.cmd_notice(numeric, botnum, username, "This command is either unknown, or you need to be opered up to use it.");
		}
	}
	public void showcommand(Core C, V Bot, String numeric, String botnum, String username, boolean operator)
	{
		if(operator)
		{
			C.cmd_notice(numeric, botnum, username, "SETHOST             Makes the bot set an ident and host on a user or yourself if nu user is supplied.");
		}
	}
}