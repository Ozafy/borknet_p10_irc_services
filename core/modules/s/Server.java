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
import java.util.*;
import java.net.*;
import borknet_services.core.*;

/**
 * The server communication class of the Q IRC Bot.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Server
{
	/** the main bot */
	private Core C;
	/** the connection to the database */
	private DBControl dbc;
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

	private S Bot;


	/**
	 * Constructs a Server communicator.
	 * @param B		The main bot
	 * @param dbc	The connection to the database
	 */
    public Server(Core C, DBControl dbc, S Bot)
	{
		this.C = C;
		this.Bot = Bot;
		this.dbc = dbc;
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
}