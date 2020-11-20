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
import java.util.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class CmdLoader
{
	private String file;

    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public CmdLoader(String file)
	{
		this.file = file;
	}

    /**
     * Constructs a IRCClient.
     * @return all the variables gotten from the configuration file
     */
	public String[] getVars() throws Exception
	{
		ArrayList<String> cmds = new ArrayList<String>();
		FileReader fr = new FileReader(file);
		BufferedReader input = new BufferedReader(fr);
		String s = input.readLine();
		while(s instanceof String)
		{
			cmds.add(s);
			s = input.readLine();
		}
		String[] cmdns = (String[]) cmds.toArray( new String[ cmds.size() ] );
		return cmdns;
	}
}