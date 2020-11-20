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
//package borknet_services.core.commands;
import java.io.*;
import java.util.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class Help implements Cmds
{
    /**
     * Constructs a Loader
     * @param debug		If we're running in debug.
     */
	public Help()
	{
	}

	public void parse_command(Core C, String bot, String target, String username, String params)
	{
	}

	public void parse_help(Core C, String bot, String username)
	{
		C.cmd_notice(bot, username, ":(");
	}
	public void showcommand(Core C, String bot, String username)
	{
		C.cmd_notice(bot, username, "HELP                Shows help on command.");
	}
}