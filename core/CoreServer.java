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
package borknet_services.core;
import java.util.*;
import java.net.*;
import borknet_services.core.*;

/**
 * The server communication class of the Q IRC Bot.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class CoreServer
{
	/** the main bot */
	private Core C;
	/** the connection to the database */
	private CoreDBControl dbc;
	/** Core commands */
	private CoreCommands CC;
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
 /** Report connections? */
	private boolean reportconn = false;
 /** ip's to ignore */
	private ArrayList<String> reportignore = new ArrayList<String>();
	/** our version reply */
	private String version;
 
 private boolean EA = false;
 private boolean EB = false;

	/**
	 * Constructs a Server communicator.
	 * @param B		The main bot
	 * @param dbc	The connection to the database
	 */
 public CoreServer(Core C, CoreDBControl dbc)
	{
		this.C = C;
		this.dbc = dbc;
		CC = new CoreCommands(C);
		nick = C.get_nick();
		host = C.get_host();
		numeric = C.get_numeric();
		corenum = C.get_corenum();
		version = C.get_version();
		reportchan = C.get_reportchan();
  reportconn = C.get_reportconn();
  String[] ignore = C.get_reportignore().split(",");
  reportignore = new ArrayList<String>(Arrays.asList(ignore)); 
	}
 
 public void setEA(boolean EA)
 {
  this.EA = EA;
 }
 
 public void setEB(boolean EB)
 {
  this.EB = EB;
 }
 
 public void parseLine(String line)
 {
  ArrayList<String> params = splitLine(line);
  String source = params.get(0);
  String command = params.get(1);
  //parse all server commands (that i needed)
  //bursts
  //AB G !1123885135.436177 releases.borknet.org 1123885135.436177
  if(command.equals("G"))
  {
   C.cmd_pong();
   return;
  }
  if(source.equals("SERVER"))
  {
   mserver(params);
  }
  else if(source.equals("PASS"))
  {
   //ignored not like we accept connections anyway
  }
  else if(source.equals("ERROR"))
  {
   C.die("ERROR from other server, trying to die gracefully.");
  }
  else
  {
   if(command.equals("EA"))
   {
    if(!EA)
    {
     C.printDebug("[>---<] >> *** Completed net.burst...");
     EA = true;
    }
   }
   else if(command.equals("EB"))
   {
    if(!EB)
    {
     C.cmd_EB();
     EB = true;
    }
   }
   else if(command.equals("P"))
   {
    //OzDFD P ]OAAA :showcommands
    privmsg(params);
   }
   else if(command.equals("N"))
   {
    //AB N Ozafy 1 1119649303 ozafy bob.be.borknet.org +oiwkgrxXnIh Ozafy Darth@Vader B]AAAB ABAXs :Laurens Panier
    nickchange(params);
   }
   else if(command.equals("Q"))
   {
    //ACAAF Q :Quit: [SearchIRC] Indexed 16 channels in 3 secs @ Aug 12th, 2005, 6:46 pm
    quit(params);
   }
   else if(command.equals("D"))
   {
    //ACAAF D ABBRC :hub.uk.borknet.org!hub.uk.borknet.org!xirtwons (Now I've done a kill :p)
    kill(params);
   }
   else if(command.equals("SQ"))
   {
    //AB SQ eclipse.il.us.borknet.org 1123885086 :Read error: Broken pipe
    squit(params);
   }
   else if(command.equals("S"))
   {
    //AB S lightweight.borknet.org 2 0 1123847781 P10 [lAAD +s :The lean, mean opping machine.
    server(params);
   }
   else if(command.equals("J"))
   {
    //[>in <] >> ABAXs J #BorkNet 949217470
    //[>in <] >> ABARL J 0
    if(params.get(2).equals("0"))
    {
     partAll(params);
    }
    else
    {
     join(params);
    }
   }
   else if(command.equals("L"))
   {
    //ABBRG L #404forums
    //[>in <] >> ABCVC L #advice :Leaving
    //[>in <] >> ADABd L #lol,#zomg
    //[>in <] >> ADABd L #lol,#zomg :Leaving
    part(params);
   }
   else if(command.equals("K"))
   {
    //[>in <] >> ABAXs K #BorkNet ABBrj :bleh OC12B?O63C12B?O
    kick(params);
   }
   else if(command.equals("C"))
   {
    //ABAAA C #Feds 1119880843
    //[>in <] >> ABAXs C #bla,#bli,#blo 1125181542
    create(params);
   }
   else if(command.equals("B"))
   {
    //[>in <] >> AB B #BorkNet 949217470 +tncCNu ABBh8,ABBhz,ABBhn:v,ACAAT:o,ACAAV,ABAXs :%InsanitySane!*@sexplay.dk *!juliusjule@sexplay.dk naimex!*@sexplay.dk
    //[>in <] >> AB B #BorkNet 949217470 ABBh8,ABBhz,ABBhn:v,ACAAT:o,ACAAV,ABAXs :%InsanitySane!*@sexplay.dk *!juliusjule@sexplay.dk naimex!*@sexplay.dk
    //wtf at these:
    //[>in <] >> AD B #Tutorial 0 +mtinDCN ADAAA
    //[>in <] >> AD B #avpoe 0 ADATI
    //[>in <] >> AD B #help 1 ADAAA:o
    /* a problem arises if a server splits, services (Q) get restarted during the split,
       and they join a (now) empty channel (because of the split). the TS on our link will be
       younger then the ts on the rejoining server, so we lose our modes.
       Possible solutions:
       a) if this happens, the B line will have a mode string, so we can find the channels that way,
          we parse the users/modes, and rejoin the channel.
       b) we save the timestamps of known channels, and if we get one that doesn't equal ours, we
          parse it like p10 discribes.
       c) we trust services not to get restarted during splits ;p

       a would be the simple dirty solution, b would require more work, and slow us down a bit more
       but be correct


       We're going for a.

       another problem surfaced where the timestamp being burst was a 0 or a 1. I have no idea why, however
       this causes deops aswell. another fix was put inplace.
    if(EA && (params.get(4).startsWith("+") || params.get(3).length() < 2))
    {
     reop(chan);
    }
    */
    bline(params);
   }
   else if(command.equals("M"))
   {
    //[>in <] >> ABAXs M #BorkNet -ov+ov ABBlK ABBli ABBly ABBlb
    //[>in <] >> ABASv M Ozafy +h moo@moop
    mode(params);
   }
   else if(command.equals("OM"))
   {
    //AW OM #coder-com +ov AWAAA AWAAA
    omode(params);
   }
   else if(command.equals("CM"))
   {
    //AQ CM #BorkNet ovpsmikblrcCNDu
    cmode(params);
   }
   else if(command.equals("AC"))
   {
    //AQ AC ABBRG Froberg
    auth(params);
   }
  }
  if(EA)
  {
   C.getCoreModControl().parse(params);
  }
  /*
  AC	ACCOUNT
  AD	ADMIN
  LL	ASLL
  A	AWAY
  B	BURST
  CM	CLEARMODE
  CLOSE	CLOSE
  CN	CNOTICE
  CO	CONNECT
  CP	CPRIVMSG
  C	CREATE
  DE	DESTRUCT
  DS	DESYNCH
  DIE	DIE
  DNS	DNS
  EB	END_OF_BURST
  EA	EOB_ACK
  Y	ERROR
  GET	GET
  GL	GLINE
  HASH	HASH
  HELP	HELP
  F	INFO
  I	INVITE
  ISON	ISON
  J	JOIN
  JU	JUPE
  K	KICK
  D	KILL
  LI	LINKS
  LIST	LIST
  LU	LUSERS
  MAP	MAP
  M	MODE
  MO	MOTD
  E	NAMES
  N	NICK
  O	NOTICE
  OPER	OPER
  OM	OPMODE
  L	PART
  PA	PASS
  G	PING
  Z	PONG
  POST	POST
  P	PRIVMSG
  PRIVS	PRIVS
  PROTO	PROTO
  Q	QUIT
  REHASH	REHASH
  RESET	RESET
  RESTART	RESTART
  RI	RPING
  RO	RPONG
  S	SERVER
  SET	SET
  SE	SETTIME
  U	SILENCE
  SQ	SQUIT
  R	STATS
  TI	TIME
  T	TOPIC
  TR	TRACE
  UP	UPING
  USER	USER
  USERHOST USERHOST
  USERIP	USERIP
  V	VERSION
  WC	WALLCHOPS
  WA	WALLOPS
  WU	WALLUSERS
  WV	WALLVOICES
  H	WHO
  W	WHOIS
  X	WHOWAS
  SN	SVSNICK
  SJ	SVSJOIN
  */
 }

 private ArrayList<String> splitLine(String msg)
 {
  /*
   * This function splits lines on each space
   * a parameter beginning with ':' will be treated as the last.
   * The inevitable exception to this is the _first_ parameter...
   */
  ArrayList<String> params = new ArrayList<String>();
  String[] result = msg.split("\\s");
  for(int i=0; i<result.length; i++)
  {
   if(i>0 && result[i].startsWith(":"))
   {
    //rest is one param
    String param = result[i].substring(1);
    i++;
    for(; i<result.length; i++)
    {
     param += " " + result[i];
    }
    params.add(param);
   }
   else
   {
    params.add(result[i]);
   }
  }
  return params;
 }
 
	public void privmsg(ArrayList<String> params)
	{
		CC.privmsg(params);
	}

	/**
	 * Handles a clearmode
	 * @param chan		channel that get's cleared
	 * @param modes		modes that get cleared
	 */
	public void cmode(ArrayList<String> params)
	{
  try
  {
   //clear all usermodes
   //AQ CM #BorkNet ovpsmikblrcCNDu
   //find users and remove o/v
   String target = params.get(2);
   String modes = params.get(3);
   if(target.startsWith("#"))
   {
    boolean v = modes.contains("v");
    boolean o = modes.contains("o");
    Channel channel = dbc.getChannel(target);
    ArrayList<String> users = channel.getUserlist();
    for(String user : users)
    {
     if(v)
     {
      dbc.setUserChanMode(user, target, "-v");
     }
     if(o)
     {
      dbc.setUserChanMode(user, target, "-o");
     }
    }
   }
  }
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.printDebug("ArrayIndexOutOfBoundsException in cmode!\n");
			C.debug(e);
			C.report("ArrayIndexOutOfBoundsException in cmode!");
		}
	}

	/**
	 * Handles a clearmode
	 * @param opernume		numeric of the operator that clears the channel
	 * @param params		channel and modes that changed
	 */
	public void omode(ArrayList<String> params)
	{
		//[>in <] >> ABAXs OM #BorkNet -ov+ov ABBlK ABBli ABBly ABBlb
		try
		{
   String source = params.get(0);
   String target = params.get(2);
   String modes = params.get(3);
			if(target.startsWith("#"))
			{
    chanMode(params, source, target, modes);
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.printDebug("ArrayIndexOutOfBoundsException in srv_omode!\n");
			C.debug(e);
			C.report("ArrayIndexOutOfBoundsException in srv_mode!");
		}
	}

	public void mode(ArrayList<String> params)
	{
		//[>in <] >> ABAXs M #BorkNet -ov+ov ABBlK ABBli ABBly ABBlb
		//or a mode hack fix
		//[>in <] >> AB M #programming.help -oo ABAyJ AQAAA 1134578560
		//[>in <] >> ABArk M #FLE +oo ABAvx ABAv0
		//[>in <] >> ADACf M #elitesabbers +tnCN

		//usermodes
		//[>in <] >> ABASv M Ozafy +h moo@moop
  //[>in <] >> OzDc] M WebBorker553 +x
		try
		{
   String source = params.get(0);
   String target = params.get(2);
   String modes = params.get(3);
			if(target.startsWith("#"))
			{
    chanMode(params, source, target, modes);
			}
   else
   {
    if(modes.contains("+"))
    {
     String flags = plus_flags(modes);
     User user = dbc.getUser(source);
     String usermodes = user.getModes();
     if(usermodes.equals(""))
     {
      dbc.setUserField(source,3, "+"+flags);
     }
     else
     {
      if(flags.equals("h"))
      {
       if (!usermodes.contains("h"))
       {
        dbc.setUserField(source,3 , usermodes + flags);
       }
      }
      else
      {
       dbc.setUserField(source,3 , usermodes + flags);
      }
     }
     if(flags.contains("o"))
     {
      dbc.setUserField(source,5 , "true");
     }
     if(flags.contains("h"))
     {
      dbc.setUserField(source,8,params.get(4));
     }
    }
    if(modes.contains("-"))
    {
     String flags = min_flags(modes);
     User user = dbc.getUser(source);
     if(flags.contains("o"))
     {
      dbc.setUserField(source, 5 , "false");
     }
     if(flags.contains("h"))
     {
      dbc.setUserField(source,8,"0");
     }
     char c[] = flags.toCharArray();
     String usermodes = user.getModes();
     for(int i =0; i < c.length; i++)
     {
       usermodes = usermodes.replace(c[i],' ');
     }
     String[] mod = usermodes.split("\\s");
     String mods = "";
     for(int i =0; i < mod.length; i++)
     {
      mods += mod[i];
     }
     dbc.setUserField(source, 3, mods);
    }
   }
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.printDebug("ArrayIndexOutOfBoundsException in srv_mode!\n");
			C.debug(e);
			C.report("ArrayIndexOutOfBoundsException in srv_mode!");
		}
	}
 
 private void chanMode(ArrayList<String> params, String source, String target, String modes)
 {
  String change[] = mode_array(modes);
  if(params.size() > 4)
  {
   if(source.equals(numeric + corenum) || params.get(4).equals(numeric + corenum))
   {
    return;
   }
   dbc.setUserChanMode(params.get(4), target, change[0]);
  }
  if(params.size() > 5 && change.length > 1)
  {
   dbc.setUserChanMode(params.get(5), target, change[1]);
  }
  if(params.size() > 6 && change.length > 2)
  {
   dbc.setUserChanMode(params.get(6), target, change[2]);
  }
  if(params.size() > 7 && change.length > 3)
  {
   dbc.setUserChanMode(params.get(7), target, change[3]);
  }
  if(params.size() > 8 && change.length > 4)
  {
   dbc.setUserChanMode(params.get(8), target, change[4]);
  }
  if(params.size() > 9 && change.length > 5)
  {
   dbc.setUserChanMode(params.get(9), target, change[5]);
  }
 }

	/**
	 * Creates an array of mode flags for easy parsing
	 * @param fl		set of flags
	 *
	 * @return	an array of seperate flags
	 */
	private String[] mode_array(String fl)
	{
		String plus = plus_flags(fl);
		String min = min_flags(fl);
		ArrayList<String> flags = new ArrayList<String>();
		if(min.length()>0)
		{
			char c[] = min.toCharArray();
			for(int i =0; i < c.length; i++)
			{
				if(c[i] == 'o' || c[i] == 'v')
				{
					flags.add("-"+c[i]);
				}
			}
		}
		if(plus.length()>0)
		{
			char c[] = plus.toCharArray();
			for(int i =0; i < c.length; i++)
			{
				if(c[i] == 'o' || c[i] == 'v')
				{
					flags.add("+"+c[i]);
				}
			}
		}
		if(flags.size()>0)
		{
			String[] r = (String[]) flags.toArray(new String[ flags.size() ]);
			return r;
		}
		else
		{
			return new String[]{"0","0","0","0","0","0","0","0","0","0"};
		}
	}

	/**
	 * returns the gained flags of a flag list
	 * @param flags		set of flags
	 *
	 * @return	the gained flags
	 */
	private String plus_flags(String flags)
	{
		String rflags = "";
		if(flags.startsWith("+"))
		{
			if(flags.contains("-"))
			{
				rflags += flags.substring(1, flags.indexOf("-")) + plus_flags(flags.substring(flags.indexOf("-")));
			}
			else
			{
				rflags += flags.substring(1);
			}
		}
		else if(flags.contains("+"))
		{
			rflags+= plus_flags(flags.substring(flags.indexOf("+")));
		}
		return rflags;
	}

	/**
	 * returns the lost flags of a flag list
	 * @param flags		set of flags
	 *
	 * @return	the lost flags
	 */
	private static String min_flags(String flags)
	{
		String rflags = "";
		if(flags.startsWith("-"))
		{
			if(flags.contains("+"))
			{
				rflags += flags.substring(1, flags.indexOf("+")) + min_flags(flags.substring(flags.indexOf("+")));
			}
			else
			{
				rflags += flags.substring(1);
			}
		}
		else if(flags.contains("-"))
		{
			rflags+= min_flags(flags.substring(flags.indexOf("-")));
		}
		return rflags;
	}

	/**
	 * handles the mother server connection
	 *
	 * @param msg		raw data gotten from the SERVER line
	 */
	public void mserver(ArrayList<String> params)
	{
		//add the server or cry
  //SERVER mooses.fr.borknet.org 1 1362604614 1386544050 J10 OzAP] +h6n :BorkNet IRC Server
		try
		{
			dbc.addServer(params.get(6).substring(0,2),params.get(1),numeric,false);
			return;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.printDebug("ArrayIndexOutOfBoundsException in srv_mserver!");
			C.debug(e);
			C.report("ArrayIndexOutOfBoundsException in srv_mserver!");
		}
	}

	/**
	 * handles a new server connection
	 *
	 * @param msg		raw data gotten from the S line
	 */
	public void server(ArrayList<String> params)
	{
		//add the server or cry
		//AB S lightweight.borknet.org 2 0 1123847781 P10 [lAAD +s :The lean, mean opping machine.
		try
		{
			boolean service = false;
			if(params.get(8).contains("s"))
			{
				service = true;
			}
			dbc.addServer(params.get(7).substring(0,2),params.get(2),params.get(0),service);
			C.del_split(params.get(2));
			return;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.printDebug("ArrayIndexOutOfBoundsException in srv_server!");
			C.debug(e);
			C.report("ArrayIndexOutOfBoundsException in srv_server!");
		}
	}

	/**
	 * Handles user quits
	 *
	 * @param params		raw irc data
	 */
	public void quit(ArrayList<String> params)
	{
  removeUser(params.get(0),params.get(params.size()-1));
	}
 
	public void kill(ArrayList<String> params)
	{
  removeUser(params.get(2),params.get(params.size()-1));
	}
 
 private void removeUser(String numeric, String message)
 {
		if(reportconn && EA)
		{
			User user = dbc.getUser(numeric);
   String ipv4 = C.longToIp(C.base64Decode(user.getIp()));
   if(!reportignore.contains(ipv4))
   {
    C.report("User: [" + user.getNick() + "] ["+user.getIdent()+"@"+user.getHost()+"] has quit ["+message+"]");
   }
		}
		//remove the disconnected user and deauth him
		dbc.delUser(numeric);
 }

	/**
	 * Handles server quits
	 *
	 * @param quit		raw irc data
	 */
	public void squit(ArrayList<String> params)
	{
		try
		{
   String host = params.get(2);
			dbc.delServer(host);
			C.add_split(host);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			C.printDebug("ArrayIndexOutOfBoundsException in srv_squit!");
			C.debug(e);
			C.report("ArrayIndexOutOfBoundsException in srv_squit!");
		}
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
  //Oz N Ozafy 1 1383584965 ozafy 91.121.106.220 +oiwkgrXnIhP Ozafy:1383586003:1 Gaius@Julius.Caesar BbeWrc OzDFD :Laurens Panier
  //NN N P 2 1383901011 P proxyscan.borknet.org +odk B]AAED NNAAC :Proxyscan
  //NN N N 2 1383901011 control services.borknet.org +oidkr C:1383901011 B]AAEE NNAAD :NewServ Control Service
  
  //Oz N Ozafy- 1 1386625413 ~ozafy 78.21.219.243 +i BOFdvz OzDdp :Laurens Panier
  //Oz N WebBorker197 1 1386625506 ~4e15dbf3 91.121.106.220 +i BbeWrc OzDdq :http://webchat.borknet.org/

		String source = params.get(0);
  //new user
		if(source.length() < 3)
		{
			try
			{
				boolean isop = false;
				String nickname = params.get(2);
				String modes = params.get(7);
				if(!modes.startsWith("+"))
				{
					//doesn't start with a + so he/she had no modes set
					modes = "";
				}
				String host = params.get(5) + "@" + params.get(6);
    String ip = params.get(params.size()-3);
    String ipv4 = C.longToIp(C.base64Decode(ip));
				String numeric = params.get(params.size()-2);
				//it's an oper
				if(modes.contains("o"))
				{
					isop = true;
				}
				String auth = "0";
				String fake = "0";
				if(modes.contains("r"))
				{
					auth = params.get(8);
     if(auth.contains(":"))
     {
      auth = auth.split(":")[0];
     }
				}
				if(modes.contains("h"))
				{
					if(modes.contains("r"))
					{
						fake = params.get(9);
					}
					else
					{
						fake = params.get(8);
					}
				}
				dbc.addUser(numeric,nickname,host,modes,auth,isop,numeric.substring(0,2),ip,fake);
				if(reportconn && EA )
				{
					//user [scrawl43] [dwelabbric@data.searchirc.org] has connected on [hub.webbirc.se]
     //72.64.145.20 searchirc
     //85.25.141.52 netsplit
     if(!reportignore.contains(ipv4))
     {
      C.report("User: [" + nickname + "] ["+host+"] has connected on ["+dbc.getServer(numeric)+"]");
     }
				}
				return;
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.printDebug("ArrayIndexOutOfBoundsException in srv_nickchange! (1)");
				C.debug(e);
				C.report("ArrayIndexOutOfBoundsException in srv_nickchange! (1)");
			}
		}
		//an actual nickchange and not a server sinc
		else
		{
			try
			{
				dbc.setUserField(source,1, params.get(2));
				return;
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				C.printDebug("ArrayIndexOutOfBoundsException in srv_nickchange! (2)");
				C.debug(e);
				C.report("ArrayIndexOutOfBoundsException in srv_nickchange! (2)");
			}
		}
	}

	/**
	 * Handles channeljoins
	 *
	 * @param username		the user's numeric
	 * @param channel		the channel getting joined
	 */
	public void join(ArrayList<String> params)
	{
  String numeric = params.get(0);
		String[] channellist = params.get(2).split(",");
  String timestamp = params.get(3);
		for(String channel : channellist)
  {
   dbc.addUserChan(channel, numeric, timestamp, false, false);
  }
	}

	/**
	 * Handles channel burst lines
	 *
	 * @param channel		channel getting bursted
	 * @param users			the users currently on the channel, with their modes
	 */
	public void bline(ArrayList<String> params)
	{
		/*
		[>in <] >> AB B #BorkNet 949217470 +tncCNul 14 ABBly,ABBlb,ABAXs:ov,ABBli:v,ABBjL,ACAAi:o,ABBlK,ACAAT
  [>in <] >> Oz B #fle 1362604640 +tnCNul 10 OzDd7,OzAAC:v,OzDd9:o,NCAAC,OzDFD :%*!*@212.29.194.253
		ABBly,ABBlb == no modes
		ABAXs:ov == +ov
		ABBli:v,ABBjL == +v
		ACAAi:o,ABBlK,ACAAT == +o
  <wiebe> modes are only once in burst
  <wiebe> and apply to all users after it
  
  bans start with :%! so filter the % of the first ban if ever need be
		*/
  try
  {
   String channel = params.get(2);
   String timestamp = params.get(3);
   String lastparam = params.get(params.size()-1);
   if(lastparam.startsWith("%"))
   {
    lastparam = params.get(params.size()-2);
   }
   String[] userlist = lastparam.split(",");
   String lastfound = "";
   for(String user : userlist)
   {
    if(user.contains(":ov") || user.contains(":vo") || lastfound.equals(":ov"))
    {
     lastfound=":ov";
     String usernumeric = user;
     if(usernumeric.indexOf(":")>0)
     {
      usernumeric = usernumeric.substring(0,(usernumeric.indexOf(":")));
     }
     dbc.addUserChan(channel, usernumeric, timestamp, true, true);
    }
    else if(user.contains(":o") || lastfound.equals(":o"))
    {
     lastfound=":o";
     String usernumeric = user;
     if(usernumeric.indexOf(":")>0)
     {
      usernumeric = usernumeric.substring(0,(usernumeric.indexOf(":")));
     }
     dbc.addUserChan(channel, usernumeric,timestamp, true, false);
    }
    else if(user.contains(":v") || lastfound.equals(":v"))
    {
     lastfound=":v";
     String usernumeric = user;
     if(usernumeric.indexOf(":")>0)
     {
      usernumeric = usernumeric.substring(0,(usernumeric.indexOf(":")));
     }
     dbc.addUserChan(channel, usernumeric,timestamp, false, true);
    }
    else
    {
     lastfound="";
     dbc.addUserChan(channel, user,timestamp, false, false);
    }
   }
  }
  catch(ArrayIndexOutOfBoundsException e)
  {
   C.printDebug("ArrayIndexOutOfBoundsException in bline!");
   C.debug(e);
   C.report("ArrayIndexOutOfBoundsException in bline!");
  }
	}

	/**
	 * Handles channel creations
	 *
	 * @param channel		channel getting created
	 * @param user			the user who created it
	 */
	public void create(ArrayList<String> params)
	{
  String numeric = params.get(0);
		String[] channellist = params.get(2).split(",");
  String timestamp = params.get(3);
		for(String channel : channellist)
		{
			dbc.addUserChan(channel, numeric, timestamp, true, false);
		}
	}

	/**
	 * Handles channel parts
	 *
	 * @param chan		channel getting parted
	 * @param user		the user parting
	 */
	public void part(ArrayList<String> params)
	{
  String numeric = params.get(0);
		String[] channellist = params.get(2).split(",");
		for(String channel : channellist)
		{
			dbc.delUserChan(channel, numeric);
		}
	}

	/**
	 * Handles channel parts
	 *
	 * @param chan		channel getting parted
	 * @param user		the user parting
	 */
	public void partAll(ArrayList<String> params)
	{
  String numeric = params.get(0);
		ArrayList<String> channels = new ArrayList<String>();
  channels.addAll(dbc.getUserChans(numeric));
		for(String channel : channels)
		{
			dbc.delUserChan(channel, numeric);
		}
	}

	/**
	 * Handles channel kicks
	 *
	 * @param chan		channel where the kick occurs
	 * @param user		the user getting kicked
	 */
	public void kick(ArrayList<String> params)
	{
  String numeric = params.get(0);
  String channel = params.get(2);
		dbc.delUserChan(channel, numeric);
	}

	public void auth(ArrayList<String> params)
	{
		//[>out<] >> ]Q AC ABAlA Nesjamag
		try
		{
			String numeric = params.get(2);
			User user = dbc.getUser(numeric);
			String auth = params.get(3);
   if(auth.contains(":"))
   {
    auth = auth.split(":")[0];
   }
			dbc.setUserField(numeric,4, auth);
			//add the authed flag (r) to his saved umodes
			dbc.setUserField(numeric,3,user.getModes()+"r");
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
				C.printDebug("ArrayIndexOutOfBoundsException in srv_auth!");
				C.debug(e);
				C.report("ArrayIndexOutOfBoundsException in srv_auth!");
		}
	}
}