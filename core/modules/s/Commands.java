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
	private S Bot;
	private DBControl dbc;
	private String numeric = "";
	private String botnum = "";
	private String network = "";
	private String rules = "";
 private Spamwords spamwords;
 public Commands(Core C, S Bot)
	{
		this.C = C;
		this.Bot = Bot;
		dbc = Bot.getDBC();
		numeric = Bot.get_num();
		botnum = Bot.get_corenum();
		cmds = Bot.getCmds();
		cmdn = Bot.getCmdn();
		rules = Bot.getRules();
		network = C.get_net();
  spamwords = new Spamwords(C);
	}

	public void privmsg(ArrayList<String> params)
	{
  String source = params.get(0);
  String target = params.get(2);
  String message = params.get(3);
		if(target.startsWith("#"))
		{
			if(dbc.SchanExists(target) && !dbc.isService(source))
			{
    User user = dbc.getUser(source);
    if(!user.getOperator())
    {
     String f = dbc.getChanFlags(target);
     if(f.equals("n"))
     {
      spamscan(source, message);
     }
     else if(f.equals("i"))
     {
      C.cmd_dis(source, "You are violating "+network+" rules. Please read "+rules+". ID: "+dbc.getID());
     }
    }
			}
		}
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
 private void showCommands(String source)
 {
  C.cmd_notice(numeric, botnum,source,"The following commands are available to you:");
  CoreDBControl dbc = C.get_dbc();
  User user = dbc.getUser(source);
  for(int n=0; n<cmds.size(); n++)
  {
   Command ccommand = (Command) cmds.get(n);
   ccommand.showcommand(C,Bot,numeric,botnum,source,user.getOperator());
  }
  C.cmd_notice(numeric, botnum,source,"End of list.");
 }
	private void spamscan(String source, String msg)
	{
		dbc.addPoints(source, Bot.getSpamtext());
		if(msg.contains("#"))
		{
			if(msg.indexOf("#",msg.indexOf("#")) > -1)
			{
				String chans[] = msg.split("\\s");
				for (String chan : chans)
				{
					if(chan.startsWith("#") && dbc.chanExists(chan))
					{
						dbc.addPoints(source, Bot.getSpamchan());
					}
				}
			}
			else
			{
				String chan = msg.substring(msg.indexOf("#"));
				if(chan.contains(" "))
				{
					chan = chan.substring(chan.indexOf("#"),chan.indexOf(" ",chan.indexOf("#")));
				}
				if(dbc.chanExists(chan))
				{
					dbc.addPoints(source, Bot.getSpamchan());
				}
			}
		}
		if(msg.contains("http://") || msg.contains("www."))
		{
			dbc.addPoints(source, Bot.getSpamweb());
		}
		if(dbc.repeat(source,msg))
		{
			dbc.addPoints(source, Bot.getSpamrepeat());
		}
  dbc.addPoints(source, spamwords.getPoints(msg));
		dbc.setMsg(source,msg);
		if(dbc.getPoints(source) >= Bot.getSpamwarning())
		{
			C.cmd_notice(numeric, botnum,source, "You are violating "+network+" rules. Please read "+rules+" and cease your abuse.");
		}
		if(dbc.getPoints(source) >= Bot.getSpamkill())
		{
			C.cmd_dis(source, "You are violating "+network+" rules. Please read "+rules+". ID: "+dbc.getID());
		}
	}
}