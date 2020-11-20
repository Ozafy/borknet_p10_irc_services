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

public class Channel
{
	private String channel;
	private String creationtime;
	private HashMap<String,ChannelUser> users = new HashMap<String,ChannelUser>();

	public Channel(String channel,String creationtime,String user, boolean isop, boolean isvoice)
	{
		this.channel = channel;
  this.creationtime = creationtime;
  ChannelUser chanuser = new ChannelUser(user,isop,isvoice);
  users.put(user,chanuser);
	}

	public String getChannel()
	{
		return channel;
	}

	public void setChannel(String s)
	{
		channel = s;
	}

	public String getCreationtime()
	{
		return creationtime;
	}

	public void setCreationtime(String s)
	{
		creationtime = s;
	}

	public Integer getUsercount()
	{
		return users.size();
	}
 
 public ArrayList<String> getUserlist()
 {
  return new ArrayList<String>(users.keySet());
 }
 
 public ArrayList<ChannelUser> getChannelUserlist()
 {
  ArrayList<String> keys =  new ArrayList<String>(users.keySet());
  ArrayList<ChannelUser> channelusers = new ArrayList<ChannelUser>();
  for(String user : keys)
  {
   channelusers.add(users.get(user));
  }
  return channelusers;
 }
 
 public void setUserChanMode(String user, String mode)
 {
  if(mode.contains("+"))
  {
   if(mode.contains("o"))
   {
    setUserOp(user,true);
   }
   else if(mode.contains("v"))
   {
    setUserVoice(user,true);
   }
  }
  else
  {
   if(mode.contains("o"))
   {
    setUserOp(user,false);
   }
   else if(mode.contains("v"))
   {
    setUserVoice(user,false);
   }
  }
 }
 
 public void setUserOp(String user, Boolean isop)
 {
  ChannelUser cu=users.get(user);
  if(cu instanceof ChannelUser)
  {
   cu.isop(isop);
  }
 }
 
 public void setUserVoice(String user, boolean isvoice)
 {
  ChannelUser cu=users.get(user);
  if(cu instanceof ChannelUser)
  {
   cu.isvoice(isvoice);
  }
 }
 
 public void setClearMode(String mode)
 {
  if(mode.contains("o") && mode.contains("v"))
  {
			ArrayList<String> userlist = new ArrayList<String>(users.keySet());
			for(String user : userlist)
			{
				ChannelUser cu=users.get(user);
    cu.isop(false);
    cu.isvoice(false);
			}
  }
  else if(mode.contains("o"))
  {
			ArrayList<String> userlist = new ArrayList<String>(users.keySet());
			for(String user : userlist)
			{
				ChannelUser cu=users.get(user);
    cu.isop(false);
			}
  }
  else if(mode.contains("v"))
  {
			ArrayList<String> userlist = new ArrayList<String>(users.keySet());
			for(String user : userlist)
			{
				ChannelUser cu=users.get(user);
    cu.isvoice(false);
			}
  }
 }
 
 public void addUser(String user, boolean isop, boolean isvoice)
 {
  ChannelUser chanuser = new ChannelUser(user,isop,isvoice);
  users.put(user,chanuser);
 }
 
 public void delUser(String user)
 {
  users.remove(user);
 }
 
 public boolean ison(String user)
 {
  ChannelUser cu=users.get(user);
  return (cu instanceof ChannelUser);
 }
 
 public boolean isop(String user)
 {
  ChannelUser cu=users.get(user);
  if(cu instanceof ChannelUser)
  {
   return cu.isop();
  }
  return false;
 }
 
 public boolean isvoice(String user)
 {
  ChannelUser cu=users.get(user);
  if(cu instanceof ChannelUser)
  {
   return cu.isvoice();
  }
  return false;
 }
 
 public boolean hasop()
 {
  ArrayList<String> userlist = new ArrayList<String>(users.keySet());
  for(String user : userlist)
  {
   ChannelUser cu=users.get(user);
   if(cu.isop())
   {
    return true;
   }
  }
  return false;
 }
}