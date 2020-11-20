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

public class ChannelUser
{
 private String user;
 private boolean op;
 private boolean voice;
 public ChannelUser(String user, boolean isop, boolean isvoice)
 {
  this.user=user;
  this.op=isop;
  this.voice=isvoice;
 }
 
 public String getNumeric()
 {
  return user;
 }
 
 public boolean isop()
 {
  return op;
 }

 public void isop(boolean isop)
 {
  this.op = isop;
 }
 
 public boolean isvoice()
 {
  return voice;
 }

 public void isvoice(boolean isvoice)
 {
  this.voice = isvoice;
 }
}