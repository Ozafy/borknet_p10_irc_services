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
Handles all raw data.

This one only parses private messages and relays them to commands.

However, it can parse any message on the network, and could infact
be adjusted to support diffrent protocols (in theory ;)
*/

import java.util.*;
import java.net.*;
import java.security.*;
import borknet_services.core.*;

/**
 * The server communication class of the Q IRC Bot.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Server
{
	/** the main bot */
	private Core C;
	/** Core commands */
	private Commands CC;
	/** the bot's nick */
	private String nick;
	/** the bot's host */
	private String host;
	/** the server's numeric */
	private String numeric;
	/** the bot's numeric */
	private String corenum;
	/** the channel we report to */
	private String reportchan;
	/** our version reply */
	private String version;
	/**  counts the number of received pings, used as a timer for channel limits */
	private int limit = 0;

	private V Bot;


	/**
	 * Constructs a Server communicator.
	 * @param B		The main bot
	 * @param dbc	The connection to the database
	 */
 public Server(Core C, V Bot)
	{
		this.C = C;
		this.Bot = Bot;
		CC = new Commands(C,Bot);
		nick = C.get_nick();
		host = C.get_host();
		numeric = C.get_numeric();
		corenum = C.get_corenum();
		version = C.get_version();
		reportchan = C.get_reportchan();
	}

	public void parse(ArrayList<String> params)
	{
  String source = params.get(0);
  String command = params.get(1);
  if(command.equals("P"))
  {
   privmsg(params);
  }
		else if(command.equals("N"))
		{
			if(Bot.get_qwebirc() || Bot.get_automatic())
			{
				nickchange(params);
			}
		}
	}

	/**
	 * Handles a privmsg
	 * @param me		Server it's going to.
	 * @param username	numeric of the user talking to me
	 * @param message	the message we got from the user
	 */
	public void privmsg(ArrayList<String> params)
	{
		CC.privmsg(params);
	}

	/**
	 * Handles N lines, these can be a user nickchange, or new clients connecting
	 *
	 * @param usernumeric	the user's numeric
	 * @param params		raw irc data
	 */
	public void nickchange(ArrayList<String> params)
	{
		//AB N Ozafy 1 1119649303 ozafy bob.be.borknet.org +oiwkgrxXnIh Ozafy Darth@Vader B]AAAB ABAXs :Laurens Panier
  String source = params.get(0);
		if(source.length() < 3)
		{
			try
			{
				String ident = params.get(5);
				String ip;
				try
				{
					ip = InetAddress.getByName(params.get(6)).getHostAddress();
				}
				catch(UnknownHostException e)
				{
					ip = "0.0.0.0";
				}
				String numeric = params.get(params.size()-2);
    if(Bot.get_qwebirc() && params.get(6).equals(Bot.get_qhost()))
    {
     try
     {
      long realip = Long.parseLong(ident.substring(1),16);
      setHost(numeric, Bot.get_qident(), C.longToHost(realip));
     }
     catch(Exception longex)
     {
      //do nothing
     }
    }
    else if(Bot.get_automatic())
    {
     String vhost = encrypt(ip) + "." + Bot.get_vhost();
     setHost(numeric, ident, vhost);
    }
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.printDebug("ArrayIndexOutOfBoundsException in srv_nickchange! (1)");
				C.debug(e);
				C.report("ArrayIndexOutOfBoundsException in srv_nickchange! (1)");
			}
		}
	}

	public void setHost(String numeric, String ident, String vhost)
	{
		C.cmd_sethost(numeric, ident, vhost);
	}
 
	public String encrypt(String str)
	{
		long hash = 0;
		long x    = 0;
		for(int i = 0; i < str.length(); i++)
		{
			hash = (hash << 4) + str.charAt(i);
			if((x = hash & 0xF0000000L) != 0)
			{
				hash ^= (x >> 24);
			}
			hash &= ~x;
		}
		return hash+"";
	}
}