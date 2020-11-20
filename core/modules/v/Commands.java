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
This class handles private messages.

It checks if the message was sent to this bot/server,
then checks if it was help/showcommands or a command
and sends it to the correct class.
*/

import java.util.*;
import java.net.*;
import java.io.*;
import borknet_services.core.*;


/**
 * The server communication class of the Q IRC Botot.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Commands
{
	private ArrayList<Object> cmds = new ArrayList<Object>();
	private ArrayList<String> cmdn = new ArrayList<String>();
	private Core C;
	private V Bot;
	private String numeric = "";
	private String botnum = "";
    public Commands(Core C, V Bot)
	{
		this.C = C;
		this.Bot = Bot;
		numeric = Bot.get_num();
		botnum = Bot.get_corenum();
		cmds = Bot.getCmds();
		cmdn = Bot.getCmdn();
	}

	public void privmsg(ArrayList<String> params)
	{
  String source = params.get(0);
  String target = params.get(2);
  String message = params.get(3);
		if(!target.equals(numeric) && !target.equals(numeric+botnum) && !target.equalsIgnoreCase(Bot.get_nick()+"@"+Bot.get_host())) return;
		String command = "";
		try
		{
			String[] result = message.split("\\s");
			command = result[0].toLowerCase();
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			command = message.toLowerCase();
		}
		if(command.equals("help"))
		{
			String cmd = "";
			try
			{
				String[] result = message.split("\\s");
				cmd = result[1].toLowerCase();
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				showCommands(source);
				return;
			}
			int compo = cmdn.indexOf(cmd);
			if(compo > -1)
			{
				Command ccommand = (Command) cmds.get(compo);
				CoreDBControl dbc = C.get_dbc();
				User user = dbc.getUser(source);
				ccommand.parse_help(C,Bot,numeric,botnum,source,user.getOperator());
			}
			else
			{
				C.cmd_notice(numeric, botnum,source,"This command is either unknown, or you need to be opered up to use it.");
			}
			return;
		}
		if(command.equals("showcommands"))
		{
			showCommands(source);
			return;
		}
		int compo = cmdn.indexOf(command);
		if(command.startsWith("\1"))
		{
			compo = cmdn.indexOf(command.replace("\1",""));
		}
		if(compo > -1)
		{
			Command ccommand = (Command) cmds.get(compo);
			ccommand.parse_command(C,Bot,numeric,botnum,source,message);
		}
		else
		{
			C.cmd_notice(numeric, botnum,source,"This command is either unknown, or you need to be opered up to use it.");
			C.cmd_notice(numeric, botnum,source,"/msg "+Bot.get_nick()+" showcommands");
		}
	}
 private void showCommands(String usernumeric)
 {
  C.cmd_notice(numeric, botnum,usernumeric,"The following commands are available to you:");
  CoreDBControl dbc = C.get_dbc();
  User user = dbc.getUser(usernumeric);
  for(int n=0; n<cmds.size(); n++)
  {
   Command ccommand = (Command) cmds.get(n);
   ccommand.showcommand(C,Bot,numeric,botnum,usernumeric,user.getOperator());
  }
  C.cmd_notice(numeric, botnum,usernumeric,"End of list.");
 }
}