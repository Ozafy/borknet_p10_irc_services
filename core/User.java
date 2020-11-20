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
import borknet_services.core.*;
import java.util.*;

public class User
{
	private String numeric;
	private String nick;
	private String ident;
	private String host;
	private String modes;
	private String auth;
	private boolean operator;
	private String server;
	private String ip;
	private String fakehost;
 private ArrayList<String> channels = new ArrayList<String>();

	public User(String numeric)
	{
		this.numeric = numeric;
	}

	public String getNumeric()
	{
		return numeric;
	}

	public void setNumeric(String s)
	{
		numeric = s;
	}

	public String getNick()
	{
		return nick;
	}

	public void setNick(String s)
	{
		nick = s;
	}

	public String getIdent()
	{
		return ident;
	}

	public void setIdent(String s)
	{
		ident = s;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String s)
	{
		host = s;
	}

	public String getModes()
	{
		return modes;
	}

	public void setModes(String s)
	{
		modes = s;
	}

	public String getAuth()
	{
		return auth;
	}

	public void setAuth(String s)
	{
		auth = s;
	}

	public boolean getOperator()
	{
		return operator;
	}

	public void setOperator(boolean s)
	{
		operator = s;
	}

	public String getServer()
	{
		return server;
	}

	public void setServer(String s)
	{
		server = s;
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String s)
	{
		ip = s;
	}

	public String getFakehost()
	{
		return fakehost;
	}

	public void setFakehost(String s)
	{
		fakehost = s;
	}
 
 public void joinChannel(String channel)
 {
  channels.add(channel.toLowerCase());
 }
 
 public void partChannel(String channel)
 {
  channels.remove(channel.toLowerCase());
 }
 
 public ArrayList<String> getChannels()
 {
  return channels;
 }
}