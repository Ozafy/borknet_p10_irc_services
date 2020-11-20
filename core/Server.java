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

public class Server
{
	private String numeric;
	private String host;
	private String hub;
	private boolean service;

	public Server(String numeric)
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

	public String getHost()
	{
		return host;
	}

	public void setHost(String s)
	{
		host = s;
	}

	public String getHub()
	{
		return hub;
	}

	public void setHub(String s)
	{
		hub = s;
	}

	public Boolean getService()
	{
		return service;
	}

	public void setService(Boolean s)
	{
		service = s;
	}
}