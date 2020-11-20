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
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.util.logging.*;
import java.util.regex.*;
import java.sql.*;

/**
 * The main Bot Class.
 * This class creates and manages the connection between the IRC Server and The Bot.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Core
{
	/** Version reply sent to users */
	private String version = "The BorkNet Services Core (C) Laurens Panier (Ozafy) & BorkNet Dev-Com - http://www.borknet.org";

	/** Reads the IRC Server */
	private BufferedReader IRCir;
	/** Writes to the IRC Server */
	private BufferedWriter IRCor;
	/** Socket to connect to the IRC Server*/
	private Socket IRCServerS;
	/** Variable to keep the bot alive */
	public boolean running;
	/** Internal Timer */
	private CoreTimer timer;
	/** Create mail daemon */
	private Thread timerThread;

	/** Bot Description */
	private String description = "";
	/** Bot Nick */
	private String nick = "";
	/** Bot Ident */
	private String ident = "";
	/** Bot Host */
	private String host = "";
	/** Server to connect to */
	private String server = "";
	/** Port to connect on */
	private int port = 0;
	/** Password to connect */
	private String pass = "";
	/** Server numeric */
	private String numeric = "";
	/** Channel to report to */
	private String reportchan = "";
 /** Report connections? */
	private boolean reportconn = false;
 /** ip's to ignore */
	private String reportignore = "";
	/** Name of the network the bot is connecting to */
	private String network = "";
 
 private int ticks = 0;

	/** Controls all data received from the IRC Server */
	private CoreServer ser;
	/** Controls all communication to the Database */
	private CoreDBControl dbc;
	private CoreModControl mod;
	private ArrayList<String> modules = new ArrayList<String>();
 private ArrayList<String> developers = new ArrayList<String>();

	/** active netsplits */
	private boolean split = false;
	/** list of splitted servers */
	private ArrayList<String> splits = new ArrayList<String>();
	/** Core's numeric */
	private String corenum = "AAA";

	private boolean debug = false;

	/** logging stuff */
 private Logger logger = Logger.getLogger("");
 private FileHandler fh;
 private SimpleDateFormat format = new SimpleDateFormat("EEE dd/MM/yyyy HH:mm:ss");
 /**
  * Constructs an IRCClient.
  * @param dataSrc	Holds all the configuration file settings.
  * @param debug		If we're running in debug.
  */
	public Core(boolean debug)
	{
		this.debug = debug;
		if(debug)
		{
			try
			{
				fh = new FileHandler("debug.%g", 1000000, 10, true);
				fh.setFormatter(new ShortFormatter());
				logger.addHandler(fh);
				Handler handlers[] = logger.getHandlers();
				for (int i = 0; i < handlers.length; i++)
				{
					if(handlers[i] instanceof ConsoleHandler)
					{
						logger.removeHandler(handlers[i]);
					}
				}
				logger.setLevel(Level.ALL);
			}
			catch(Exception e)
			{
				System.out.println("Error creating logfile!");
				System.exit(1);
			}
		}
		load_conf();
		create_coremodules();
		//connect to the irc server
		connect(server, port);
		logon();

		//keep running till we're told otherwise
		running = true;
		while(running)
		{
			service();
		}

		//disconnect
		logoff();
		disconnect();
	}

	public void printDebug(String s)
	{
		if(debug)
		{
			java.util.Date now = new java.util.Date();
   logger.info("["+format.format(now)+"]"+s);
		}
	}

	public void debug(Exception e)
	{
		if(debug)
		{
			StackTraceElement[] te = e.getStackTrace();
			logger.info(e.toString());
			for(StackTraceElement el : te)
			{
				logger.info("\tat "+el.getClassName()+"."+el.getMethodName()+"("+el.getFileName()+":"+el.getLineNumber()+")");
			}
		}
	}

	/**
	 * Load the config file
	 */
	private void load_conf()
	{
		ConfLoader loader = new ConfLoader(this);
		try
		{
			loader.load();
		}
		catch(Exception e)
		{
			debug(e);
			System.exit(1);
		}
		Properties dataSrc = loader.getVars();
		try
		{
			//set all the config file vars
			description = dataSrc.getProperty("description");
			nick = dataSrc.getProperty("nick");
			ident = dataSrc.getProperty("ident");
			host = dataSrc.getProperty("host");
			server = dataSrc.getProperty("server");
			port = Integer.parseInt(dataSrc.getProperty("port"));
			pass = dataSrc.getProperty("pass");
			numeric = dataSrc.getProperty("numeric");
			reportchan = dataSrc.getProperty("reportchan");
   reportconn = Boolean.parseBoolean(dataSrc.getProperty("reportconn"));
   reportignore = dataSrc.getProperty("reportignore");
			network = dataSrc.getProperty("network");
			String mods[] = dataSrc.getProperty("modules").split(",");
			for(int n=0; n<mods.length; n++)
			{
				modules.add(mods[n]);
			}
			String devs[] = dataSrc.getProperty("developers").split(",");
			for(int n=0; n<devs.length; n++)
			{
				developers.add(devs[n].toLowerCase());
			}
		}
		catch(Exception e)
		{
			printDebug("Error loading configfile.");
			debug(e);
			System.exit(1);
		}
	}

	/**
	 * Create the modules
	 */
	private void create_coremodules()
	{
		//create the db control class
		dbc = new CoreDBControl(this);
		//create the server communication class
		ser = new CoreServer(this, dbc);
		timer = new CoreTimer(this);
		Thread timerThread = new Thread(timer);
		timerThread.setDaemon(true);
		timerThread.start();
	}

    /**
     * Connects the bot to the given IRC Server
     * @param serverHostname	IP/host to connect to.
     * @param serverPort		Port to connect on.
     */
	private void connect(String serverHostname, int serverPort)
	{
		InputStream IRCis = null;
		OutputStream IRCos = null;
		//check for input output streams
		try
		{
			IRCServerS = new Socket(serverHostname, serverPort);
			IRCis = IRCServerS.getInputStream();
			IRCos = IRCServerS.getOutputStream();
			//make the buffers
			IRCir = new BufferedReader(new InputStreamReader(IRCis,"ISO-8859-1"));
			IRCor = new BufferedWriter(new OutputStreamWriter(IRCos,"ISO-8859-1"));
		}
		catch(Exception e)
		{
			printDebug("error opening streams to IRC server");
			debug(e);
			System.exit(1);
		}
		return;
	}

    /**
     * Kill the connection to the server.
     */
	private void disconnect()
	{
		try
		{
			IRCir.close();
			IRCor.close();
		}
		catch(IOException e)
		{
			printDebug("Error disconnecting from IRC server");
			debug(e);
		}
	}

    /**
     * Log off clean.
     */
	private void logoff()
	{
		BufferedReader br = IRCir;
		BufferedWriter bw = IRCor;
		try
		{
			if(!ircsend("quit :Shutting down."));
			bw.write("quit :Shutting down.");
			bw.newLine();
			bw.flush();
		}
		catch(Exception e)
		{
			printDebug("logoff error: " + e);
			System.exit(1);
		}
	}

    /**
     * Start our connection burst
     */
	private void logon()
	{
		BufferedReader br = IRCir;
		BufferedWriter bw = IRCor;
		try
		{
			// send user info
			printDebug("[>---<] >> *** Connecting to IRC server...");
			printDebug("[>---<] >> *** Sending password...");
			printDebug("[>out<] >> PASS " + pass);
			bw.write("PASS " + pass);
			bw.newLine();
			printDebug("[>---<] >> *** Identify the Service...");
			String time = get_time();
			//itroduce myself properly
			printDebug("[>out<] >> SERVER " + host + " 1 " + time + " " + time + " J10 " + numeric + "]]] +s :" + description);
			bw.write("SERVER " + host + " 1 " + time + " " + time + " J10 " + numeric + "]]] +s :" + description);
			bw.newLine();
			dbc.addServer(numeric,host,"0",true);
			printDebug("[>---<] >> *** Sending EB");
			printDebug("[>out<] >> " + numeric + " EB");
			bw.write(numeric + " EB");
			bw.newLine();
			bw.flush();
		}
		catch(Exception e)
		{
			printDebug("logon error: " + e);
			System.exit(1);
		}
		return;
	}

    /**
     * Send raw data to the IRC Server
     */
	public boolean ircsend(String message)
	{
		printDebug("[>out<] >> " + message);
		try
		{
			IRCor.write(message);
			IRCor.newLine();
			IRCor.flush();
		}
		catch(IOException e)
		{
			return false;
		}
		return true;
	}

    /**
     * Parse raw server data.
     */
	private void service()
	{
		try
		{
			if(IRCir.ready())
			{
				String msg = IRCir.readLine();
				printDebug("[>in <] >> " + msg);
    ser.parseLine(msg);
			}
			//nothing to do, nap
			else
			{
				try
				{
					Thread.sleep(100);
				}
				catch(InterruptedException e)
				{
				}
			}
		}
		//dun dun dun
		catch(IOException e)
		{
			debug(e);
			System.exit(1);
		}
	}
 
	/**
	 * rehash & reconnect to our server.
	 */
	public void rehash()
	{
		mod.stop();
		timer.stop();
		logoff();
		disconnect();
		ser.setEA(false);
		ser.setEB(false);
		modules.clear();
		load_conf();
		create_coremodules();
		connect(server,port);
		logon();
	}
 
 public void die(String quit)
 {
  mod.stop();
  cmd_quit(corenum, quit);
  running = false;
 }


    /**
     * Get the bot's nick
     * @return bot's nick
     */
	public String get_nick()
	{
		return nick;
	}

    /**
     * Get the bot's host
     * @return bot's host
     */
	public String get_host()
	{
		return host;
	}

    /**
     * Get the bot's ident
     * @return bot's ident
     */
	public String get_ident()
	{
		return ident;
	}

    /**
     * Get the bot's numeric
     * @return bot's numeric
     */
	public String get_numeric()
	{
		return numeric;
	}

    /**
     * Get the bot's numeric
     * @return bot's numeric
     */
	public String get_corenum()
	{
		return corenum;
	}

    /**
     * Get the version reply
     * @return the version reply
     */
	public String get_version()
	{
		return version;
	}

 /**
  * Get the reportchannel
  * @return reportchannel
  */
	public String get_reportchan()
	{
		return reportchan;
	}
 
 /**
  * Get the reportchannel
  * @return reportchannel
  */
	public boolean get_reportconn()
	{
		return reportconn;
	}
 
 /**
  * Get the reportchannel
  * @return reportchannel
  */
	public String get_reportignore()
	{
		return reportignore;
	}

    /**
     * Get the network name
     * @return network name
     */
	public String get_net()
	{
		return network;
	}

    /**
     * Get the current netsplit status
     * @return if the net's split or not
     */
	public boolean get_split()
	{
		return split;
	}

    /**
     * Get the current netsplit servers
     * @return list of servers
     */
	public ArrayList<String> get_splitList()
	{
		return splits;
	}

    /**
     * Set the current netsplit status
     * @param s		if the net's split or not
     */
	public void set_split(boolean s)
	{
		split = s;
	}

	/**
	 * Add a splitted server
	 * @param host	splitted server's host
	 */
	public void add_split(String host)
	{
		splits.add(host);
		set_split(true);
	}

	/**
	 * Delete a splitted server
	 * @param host	joined server's host
	 */
	public void del_split(String host)
	{
		if(splits.indexOf(host) != -1)
		{
			splits.remove(splits.indexOf(host));
		}
		if(splits.size()<1)
		{
			set_split(false);
		}
	}

    /**
     * Get the current defcon level
     * @return current defcon level
     */
	public CoreDBControl get_dbc()
	{
		return dbc;
	}

	public CoreModControl getCoreModControl()
	{
		return mod;
	}

	public boolean get_debug()
	{
		return debug;
	}
 
	public boolean isDeveloper(String auth)
	{
		return developers.contains(auth.toLowerCase());
	}

    /**
     * Report directly to the reportchan
     * @param s		what to report
     */
	public void report(String s)
	{
		cmd_privmsg(corenum, reportchan, s);
	}

	public void cmd_pong()
	{
		ircsend(numeric + " Z :" + host);
	}
 
 /**
 * Make the bot send <raw>
 * @param raw		string to send
 */
	public void cmd_raw(String raw)
	{
		ircsend(raw);
	} 
 
    /**
     * Make the bot join a channel
     * @param channel		channel to join
     */
	public void cmd_join(String num, String channel)
	{
  if(dbc.chanExists(channel))
  {
   ircsend(numeric + num + " J " + channel);
   cmd_mode(numeric + num , channel , "+o");
   dbc.addUserChan(channel, numeric + num, "0", true, false);
  }
  else
  {
   ircsend(numeric + num + " C " + channel + " " + get_time());
   dbc.addUserChan(channel, numeric + num, get_time(), true, false);
  }
	}

    /**
     * Make the bot join a channel
     * @param channel		channel to join
     */
	public void cmd_join(String numeric, String num, String channel)
	{
  if(dbc.chanExists(channel))
  {
   ircsend(numeric + num + " J " + channel);
   cmd_mode(numeric + num , channel , "+o");
   dbc.addUserChan(channel, numeric + num, "0", true, false);
  }
  else
  {
   ircsend(numeric + num + " C " + channel + " " + get_time());
   dbc.addUserChan(channel, numeric + num, get_time(), true, false);
  }
	}

    /**
     * Make the bot join a channel
     * @param channel		channel to join
     */
	public void cmd_join(String numeric, String num, String channel, boolean noop)
	{
  if(dbc.chanExists(channel))
  {
   ircsend(numeric + num + " J " + channel);
   dbc.addUserChan(channel, numeric + num, "0", false, false);
  }
  else
  {
   ircsend(numeric + num + " C " + channel + " " + get_time());
   dbc.addUserChan(channel, numeric + num, get_time(), false, false);
  }
	}

    /**
     * Make the server change a user's mode
     * @param user		user's numeric
     * @param channel	channel to change modes on
     * @param mode		mode to change
     */
	public void cmd_mode(String user , String channel , String mode)
	{
		ircsend(numeric + " OM " + channel + " " + mode + " " + user);
		dbc.setUserChanMode(user, channel, mode);
	}

    /**
     * Make the server change a user's mode
     * @param user		user's numeric
     * @param channel	channel to change modes on
     * @param mode		mode to change
     */
	public void cmd_mode(String numeric, String user , String channel , String mode)
	{
		ircsend(numeric + " OM " + channel + " " + mode + " " + user);
		dbc.setUserChanMode(user, channel, mode);
	}

    /**
     * Make the bot change a user's mode
     * @param user		user's numeric
     * @param channel	channel to change modes on
     * @param mode		mode to change
     */
	public void cmd_mode_me(String num, String user, String channel, String mode)
	{
		ircsend(numeric + num + " M " + channel + " " + mode + " " + user);
		dbc.setUserChanMode(user, channel, mode);
	}

    /**
     * Make the bot change a user's mode
     * @param user		user's numeric
     * @param channel	channel to change modes on
     * @param mode		mode to change
     */
	public void cmd_mode_me(String numeric, String num, String user, String channel, String mode)
	{
		ircsend(numeric + num + " M " + channel + " " + mode + " " + user);
		dbc.setUserChanMode(user, channel, mode);
	}

    /**
     * Make the bot part a channel
     * @param channel	channel to part
     * @param reason	say why we're leaving
     */
	public void cmd_part(String num, String channel, String reason)
	{
		ircsend(numeric + num + " L " + channel + " :" + reason);
		dbc.delUserChan(channel, numeric + num);
	}

    /**
     * Make the bot part a channel
     * @param channel	channel to part
     * @param reason	say why we're leaving
     */
	public void cmd_part(String numeric, String num, String channel, String reason)
	{
		ircsend(numeric + num + " L " + channel + " :" + reason);
		dbc.delUserChan(channel, numeric + num);
	}

    /**
     * Make the server send a privmsg
     * @param user		user's numeric (or channel) where to privmsg to
     * @param msg		what to say
     */
	public void cmd_sprivmsg(String user, String msg)
	{
		ircsend(numeric + " P " + user + " :" + msg);
	}

    /**
     * Make the server send a privmsg
     * @param user		user's numeric (or channel) where to privmsg to
     * @param msg		what to say
     */
	public void cmd_sprivmsg(String numeric, String user, String msg)
	{
		ircsend(numeric + " P " + user + " :" + msg);
	}

    /**
     * Make the bot send a privmsg
     * @param user		user (or channel) where to privmsg to
     * @param msg		what to say
     */
	public void cmd_privmsg(String num, String user, String msg)
	{
		ircsend(numeric + num + " P " + user + " :" + msg);
	}

    /**
     * Make the bot send a privmsg
     * @param user		user (or channel) where to privmsg to
     * @param msg		what to say
     */
	public void cmd_privmsg(String numeric, String num, String user, String msg)
	{
		ircsend(numeric + num + " P " + user + " :" + msg);
	}

    /**
     * Make the bot invite a user to a channel
     * @param user		nick of user to invite
     * @param chan		channel where we're inviting him to (we need op there)
     */
	public void cmd_invite(String num, String user, String chan)
	{
		ircsend(numeric + num + " I " + user + " :" + chan);
	}

    /**
     * Make the bot invite a user to a channel
     * @param user		nick of user to invite
     * @param chan		channel where we're inviting him to (we need op there)
     */
	public void cmd_invite(String numeric, String num, String user, String chan)
	{
		ircsend(numeric + num + " I " + user + " :" + chan);
	}

    /**
     * send a notice as bot
     * @param user		user's numeric to notice
     * @param msg		what to say
     */
	public void cmd_notice(String num, String user, String msg)
	{
		ircsend(numeric + num + " O " + user + " :" + msg);
	}

    /**
     * send a notice as bot
     * @param user		user's numeric to notice
     * @param msg		what to say
     */
	public void cmd_notice(String numeric, String num, String user, String msg)
	{
		ircsend(numeric + num + " O " + user + " :" + msg);
	}

    /**
     * set a G-Line
     * @param host		host to ban
     * @param duration	duration of the ban, in seconds
     * @param reason	why we're banning him/her/it
     */
	public void cmd_gline(String host, String duration, String reason)
	{
		ircsend(numeric + " GL * +" + host + " " + duration + " :" + reason);
	}

    /**
     * set a G-Line
     * @param host		host to ban
     * @param duration	duration of the ban, in seconds
     * @param reason	why we're banning him/her/it
     */
	public void cmd_gline(String numeric, String host, String duration, String reason)
	{
		ircsend(numeric + " GL * +" + host + " " + duration + " :" + reason);
	}

    /**
     * remove a G-Line
     * @param host		host to unban
     */
	public void cmd_ungline(String host)
	{
		ircsend(numeric + " GL * -" + host);
	}

    /**
     * remove a G-Line
     * @param host		host to unban
     */
	public void cmd_ungline(String numeric, String host)
	{
		ircsend(numeric + " GL * -" + host);
	}
 
    /**
     * Kill a user
     * @param user			user's numeric
     * @param why		reason why we're killing him/her/it
     */
     public void cmd_dis(String user, String why)
	{
		ircsend(numeric + " D " + user + " : ("  + why + ")");
		//dbc.delUser(user);
	}

    /**
     * Kill a user
     * @param user			user's numeric
     * @param why		reason why we're killing him/her/it
     */
     public void cmd_dis(String numeric, String user, String why)
	{
		ircsend(numeric + " D " + user + " : ("  + why + ")");
		//dbc.delUser(user);
	}

    /**
     * Make the bot quit IRC
     * @param quit	message to give when quitting
     */
	public void cmd_quit(String num,String quit)
	{
		ircsend(numeric + num + " Q :Quit: " + quit);
		dbc.delUser(numeric + num);
	}

    /**
     * Make the bot quit IRC
     * @param quit	message to give when quitting
     */
	public void cmd_quit(String numeric, String num,String quit)
	{
		ircsend(numeric + num + " Q :Quit: " + quit);
		dbc.delUser(numeric + num);
	}

    /**
     * set the topic as bot
     * @param chan		channel to change topic on
     * @param topic		new topic
     */
	public void cmd_topic(String num,String chan, String topic)
	{
		//ircsend(numeric + "AAA T " + chan + " " + 0 + " " + get_time() + " :" + topic);
		ircsend(numeric + num + " T " + chan + " :" + topic);
	}

    /**
     * set the topic as bot
     * @param chan		channel to change topic on
     * @param topic		new topic
     */
	public void cmd_topic(String numeric, String num,String chan, String topic)
	{
		//ircsend(numeric + "AAA T " + chan + " " + 0 + " " + get_time() + " :" + topic);
		ircsend(numeric + num + " T " + chan + " :" + topic);
	}

    /**
     * kick as bot
     * @param chan		channel to kick from
     * @param user		user's numeric to kick
     * @param msg		reason why we're kicking
     */
	public void cmd_kick_me(String num,String chan, String user, String msg)
	{
		ircsend(numeric + num + " K " + chan + " " + user + " :" + msg);
		dbc.delUserChan(chan, user);
	}

    /**
     * kick as bot
     * @param chan		channel to kick from
     * @param user		user's numeric to kick
     * @param msg		reason why we're kicking
     */
	public void cmd_kick_me(String numeric,String num,String chan, String user, String msg)
	{
		ircsend(numeric + num + " K " + chan + " " + user + " :" + msg);
		dbc.delUserChan(chan, user);
	}

    /**
     * kick as server
     * @param chan		channel to kick from
     * @param user		user's numeric to kick
     * @param msg		reason why we're kicking
     */
	public void cmd_kick(String chan, String user, String msg)
	{
		ircsend(numeric + " K " + chan + " " + user + " :" + msg);
		dbc.delUserChan(chan, user);
	}

    /**
     * kick as server
     * @param chan		channel to kick from
     * @param user		user's numeric to kick
     * @param msg		reason why we're kicking
     */
	public void cmd_kick(String numeric, String chan, String user, String msg)
	{
		ircsend(numeric + " K " + chan + " " + user + " :" + msg);
		dbc.delUserChan(chan, user);
	}

    /**
     * change the channel limit
     * @param chan		channel to set a new limit
     * @param lim		new limit
     */
	public void cmd_limit(String num, String chan, int lim)
	{
		ircsend(numeric + num + " M " + chan + " +l " + lim);
	}

    /**
     * change the channel limit
     * @param chan		channel to set a new limit
     * @param lim		new limit
     */
	public void cmd_limit(String numeric, String num, String chan, int lim)
	{
		ircsend(numeric + num + " M " + chan + " +l " + lim);
	}

    /**
     * change the channel key
     * @param chan		channel to set a new key
     * @param key		new key
     */
	public void cmd_key(String num, String chan, String key)
	{
		ircsend(numeric + num + " M " + chan + " +k " + key);
	}

    /**
     * change the channel key
     * @param chan		channel to set a new key
     * @param key		new key
     */
	public void cmd_key(String numeric, String num, String chan, String key)
	{
		ircsend(numeric + num + " M " + chan + " +k " + key);
	}

    /**
     * create a fake user
     * @param nick		fake nickname
     * @param ident		fake ident
     * @param host		fake host
     * @param desc		fake description
     */
	public void cmd_create_service(String num, String nick, String ident, String host, String modes, String desc)
	{
		String time = get_time();
		ircsend(numeric + " N " + nick + " 1 " + time + " " + ident + " " + host + " "+modes+" " + nick + " B]AAAB " + numeric+num+" :" + desc);
		dbc.addUser(numeric+num,nick,ident+"@"+host,modes,nick,true,numeric,"0.0.0.0","0");
	}
 
	public void cmd_create_service(String num, String nick, String ident, String host, String modes, String auth, String desc, boolean customauth)
	{
		String time = get_time();
		ircsend(numeric + " N " + nick + " 1 " + time + " " + ident + " " + host + " "+modes+" " + auth + " B]AAAB " + numeric+num+" :" + desc);
		dbc.addUser(numeric+num,nick,ident+"@"+host,modes,auth,true,numeric,"0.0.0.0","0");
	}

    /**
     * create a fake user
     * @param nick		fake nickname
     * @param ident		fake ident
     * @param host		fake host
     * @param desc		fake description
     */
	public void cmd_create_service(String numeric, String num, String nick, String ident, String host, String modes, String desc)
	{
		String time = get_time();
		ircsend(numeric + " N " + nick + " 1 " + time + " " + ident + " " + host + " "+modes+" " + nick + " B]AAAB " + numeric+num+" :" + desc);
		dbc.addUser(numeric+num,nick,ident+"@"+host,modes,nick,true,numeric,"0.0.0.0","0");
	}
 

    /**
     * create a fake user
     * @param nick		fake nickname
     * @param ident		fake ident
     * @param host		fake host
     * @param desc		fake description
     */
	public void cmd_create_service(String numeric, String num, String nick, String ident, String host, String ip, String modes, String desc)
	{
		String time = get_time();
		ircsend(numeric + " N " + nick + " 1 " + time + " " + ident + " " + host + " "+modes+" " + nick + " "+base64Encode(ipToLong(ip))+" " + numeric+num+" :" + desc);
		dbc.addUser(numeric+num,nick,ident+"@"+host,modes,nick,true,numeric,"0.0.0.0","0");
	}

    /**
     * kill a fake user
     * @param nume	fake numeric to kill
     */
	public void cmd_kill_service(String nume, String msg)
	{
		ircsend(nume + " Q :"+msg);
		dbc.delUser(nume);
	}

    /**
     * create a fake user
[>in <] >> AB SQ spamscan.borknet.org 1135697097 :EOF from client
[>in <] >> AB S spamscan.borknet.org 2 0 1135956212 J10 ]S]]] +s :BorkNet Spamscan
[>in <] >> ]S EB
[>in <] >> ]S N S 2 1135956212 TheSBot spamscan.borknet.org +owkgrX S B]AAAB ]SAAA :BorkNet Spamscan
[>in <] >> ]SAAA J #coder-com 949217470
[>out<] >> ]QAAA M #coder-com +v ]SAAA
[>in <] >> ]S OM #coder-com +ov ]SAAA ]SAAA
[>in <] >> ]S EA
     */
	public void cmd_create_server(String host, String num, String desc)
	{
		String time = get_time();
		ircsend(numeric + " S " + host + " 2 0 " + time + " J10 " + num + "]]] +s :"+desc);
		dbc.addServer(num,host,numeric,true);
	}

    /**
     * kill a fake user
     * @param nume	fake numeric to kill
     */
	public void cmd_kill_server(String host, String msg)
	{
		String time = get_time();
		ircsend(numeric + " SQ "+host+ " 0 :"+msg);
		dbc.delServer(host);
	}

	public void cmd_sethost(String num, String ident, String host)
	{
		ircsend(numeric + " SH " + num + " " + ident + " " + host);
  User user = dbc.getUser(num);
  String modes = user.getModes();
		if (!modes.contains("h"))
		{
			dbc.setUserField(num, 3, modes + "h");
		}
		dbc.setUserField(num, 8, ident + "@" + host);
	}

    /**
     * get the system time
     *
     * @return	the system time
     */
	public String get_time()
	{
		Calendar cal = Calendar.getInstance();
		long l = (cal.getTimeInMillis() / 1000);
		return l+"";
	}
 
 public void timerTick()
	{
		//ticks every minute
  ticks++;
  if(ticks>=1440)
  {
   mod.clean();
   ticks = 0;
  }
	}

	/**
	 * Base64 decoding
	 */
	public long base64Decode(String numer)
	{
		char num[] = numer.toCharArray();
		long base64n = 0;
		int pwr = num.length-1;
		for(char c : num)
		{
			int d = 0;
			switch (c) {
				case 'A': d =  0; break; case 'B': d =  1; break;
				case 'C': d =  2; break; case 'D': d =  3; break;
				case 'E': d =  4; break; case 'F': d =  5; break;
				case 'G': d =  6; break; case 'H': d =  7; break;
				case 'I': d =  8; break; case 'J': d =  9; break;
				case 'K': d = 10; break; case 'L': d = 11; break;
				case 'M': d = 12; break; case 'N': d = 13; break;
				case 'O': d = 14; break; case 'P': d = 15; break;
				case 'Q': d = 16; break; case 'R': d = 17; break;
				case 'S': d = 18; break; case 'T': d = 19; break;
				case 'U': d = 20; break; case 'V': d = 21; break;
				case 'W': d = 22; break; case 'X': d = 23; break;
				case 'Y': d = 24; break; case 'Z': d = 25; break;
				case 'a': d = 26; break; case 'b': d = 27; break;
				case 'c': d = 28; break; case 'd': d = 29; break;
				case 'e': d = 30; break; case 'f': d = 31; break;
				case 'g': d = 32; break; case 'h': d = 33; break;
				case 'i': d = 34; break; case 'j': d = 35; break;
				case 'k': d = 36; break; case 'l': d = 37; break;
				case 'm': d = 38; break; case 'n': d = 39; break;
				case 'o': d = 40; break; case 'p': d = 41; break;
				case 'q': d = 42; break; case 'r': d = 43; break;
				case 's': d = 44; break; case 't': d = 45; break;
				case 'u': d = 46; break; case 'v': d = 47; break;
				case 'w': d = 48; break; case 'x': d = 49; break;
				case 'y': d = 50; break; case 'z': d = 51; break;
				case '0': d = 52; break; case '1': d = 53; break;
				case '2': d = 54; break; case '3': d = 55; break;
				case '4': d = 56; break; case '5': d = 57; break;
				case '6': d = 58; break; case '7': d = 59; break;
				case '8': d = 60; break; case '9': d = 61; break;
				case '[': d = 62; break; case ']': d = 63; break;
				default: break;
			}
			if(pwr != 0)
			{
				base64n += (d*Math.pow(64,pwr));
			}
			else
			{
				base64n += d;
			}
			pwr--;
		}
		return base64n;
	}

	public String base64Encode(long numer)
	{
		String base64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789[]";
		char base[] = base64.toCharArray();
		String encoded = "";
		while(numer>0)
		{
   long i = numer%64;
			int index = (int) i;
			encoded = base[index] + encoded;
			numer = (int) Math.ceil(numer/64);
		}
		return encoded;
	}

	public long ipToLong(String addr)
	{
		String[] addrArray = addr.split("\\.");
		long num = 0;
		for (int i=0;i<addrArray.length;i++)
		{
			int power = 3-i;
			num += ((Long.parseLong(addrArray[i])%256 * Math.pow(256,power)));
		}
		return num;
	}

	public String longToIp(long i)
	{
		return ((i >> 24 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) + "." + ((i >> 8 ) & 0xFF) + "." + (i & 0xFF);
	}
 
 public String longToHost(long ip)
 {
  String dotIp = longToIp(ip);
  try
  {
   InetAddress ia = InetAddress.getByName(dotIp);
   return ia.getCanonicalHostName();
  }
  catch(Exception ex)
  {
   return dotIp;
  }
 }

	public void cmd_EB()
	{
		//get the time
		String time = get_time();
		//create myself
		ircsend(numeric + " N " + nick + " 1 " + time + " " + ident + " " + host + " +oXwkgdr " + nick + " B]AAAB " + numeric+corenum+" :" + description);
		dbc.addUser(numeric+corenum, nick,ident+"@"+host,"+oXwkgdr",nick,true,numeric,"127.0.0.1","0");
		//join my debugchannel
		cmd_join(corenum,reportchan);
		//i'm done
		ircsend(numeric + " EA");
		mod = new CoreModControl(this, modules);
	}
}