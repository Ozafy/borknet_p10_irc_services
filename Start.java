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
package borknet_services;
import java.util.*;
import borknet_services.core.Core;

/**
 * The main class of the BorkNet Services Core.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 * @version 1.0
 */
public class Start
{
    /**
     * Program execution starts here.
     * @param args The arguments passed to the program.
     */
	public static void main(String[] args)
	{
		try
		{
			// The main Bot
			if(args[0].contains("d"))
			{
				Core C = new Core(true);
			}
			else
			{
				Core C = new Core(false);
			}
		}
		catch(Exception e)
		{
			Core C = new Core(false);
		}
	}
}