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
import java.io.*;
import borknet_services.core.*;

/**
 * Class to load configuration files.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class CoreModControl
{
	/*private ArrayList<Modules> modules = new ArrayList<Modules>();
	private ArrayList<String> modlist = new ArrayList<String>();*/
	private HashMap<String, Modules> modules = new HashMap<String,Modules>();
	private Core C;
	private String bot = "";

 public CoreModControl(Core C, ArrayList<String> modulelist)
	{
		this.C = C;
		bot = C.get_corenum();
		for(int n=0; n<modulelist.size(); n++)
		{
			try
			{
				URL[] urls = null;
				try
				{
					// Convert the file object to a URL
					File dir = new File(System.getProperty("user.dir")+File.separator+"core"+File.separator+"modules"+File.separator+modulelist.get(n).toLowerCase()+File.separator);
					URI uri = dir.toURI();
					URL url = uri.toURL();
					urls = new URL[]{url};
				}
				catch (Exception e)
				{
     C.report("[CORE] Error opening module directory");
					C.debug(e);
				}
				// Create a new class loader with the directory
				ClassLoader clsl = new URLClassLoader(urls);
				// Load in the class
				Class cls = clsl.loadClass(modulelist.get(n));
				// Create a new instance of the new class
				Modules m = (Modules) cls.newInstance();
				modules.put(modulelist.get(n).toLowerCase(),m);
				ArrayList<Object> modcmds = new ArrayList<Object>();
				ArrayList<String> modcmdn = new ArrayList<String>();
				CmdLoader cl = new CmdLoader("core/modules/"+modulelist.get(n).toLowerCase()+"/cmds");
				String[] commandlist = cl.getVars();
				for(int a=0; a<commandlist.length; a++)
				{
					try
					{
						// Load in the class
						Class clss = clsl.loadClass(commandlist[a]);
						// Create a new instance of the new class
						modcmds.add(clss.newInstance());
						modcmdn.add(commandlist[a].toLowerCase());
					}
					catch (Exception e)
					{
						C.debug(e);
					}
				}
				m.setCmnds(modcmds,modcmdn);
				m.start(C);
			}
			catch (Exception e)
			{
				C.debug(e);
			}
		}
	}

	public void rehash(String username, String module)
	{
		if(modules.containsKey(module.toLowerCase()))
		{
			modules.get(module.toLowerCase()).hstop();
			modules.remove(module.toLowerCase());
   load(username,module);
		}
		else
		{
			C.cmd_notice(bot,username, module + " isn't loaded, so can't be rehashed.");
		}
	}

	public void load(String username, String module)
	{
		if(!modules.containsKey(module.toLowerCase()))
		{
			try
			{
				URL[] urls = null;
				// Convert the file object to a URL
				File dir = new File(System.getProperty("user.dir")+File.separator+"core"+File.separator+"modules"+File.separator+module.toLowerCase()+File.separator);
				URI uri = dir.toURI();
				URL url = uri.toURL();
				urls = new URL[]{url};
				// Create a new class loader with the directory
				ClassLoader clsl = new URLClassLoader(urls);
				// Load in the class
				Class cls = clsl.loadClass(initialUpper(module));
				// Create a new instance of the new class
				Modules m = (Modules) cls.newInstance();
				modules.put(module.toLowerCase(),m);
				ArrayList<Object> modcmds = new ArrayList<Object>();
				ArrayList<String> modcmdn = new ArrayList<String>();
				CmdLoader cl = new CmdLoader("core/modules/"+module.toLowerCase()+"/cmds");
				String[] commandlist = cl.getVars();
				for(int a=0; a<commandlist.length; a++)
				{
					// Load in the class
					Class clss = clsl.loadClass(commandlist[a]);
					// Create a new instance of the new class
					modcmds.add(clss.newInstance());
					modcmdn.add(commandlist[a].toLowerCase());
				}
				m.setCmnds(modcmds,modcmdn);
				m.start(C);
			}
			catch (Exception e)
			{
				C.debug(e);
				C.report("Failed loading module \""+module+"\".");
				return;
			}
		}
		else
		{
			C.cmd_notice(bot,username, module + " is loaded, use the rehash command.");
		}
	}

	public void unload(String username, String module)
	{
		if(modules.containsKey(module.toLowerCase()))
		{
			modules.get(module.toLowerCase()).stop();
			modules.remove(module.toLowerCase());
		}
		else
		{
			C.cmd_notice(bot,username, module + " isn't loaded.");
		}
	}

	public void stop()
	{
		C.report("Stopping modules...");
		Set<String> keys = modules.keySet();
		for(String key: keys)
		{
			modules.get(key).stop();
		}
		modules.clear();
		C.report("Modules stopped.");
	}

	private String initialUpper(String s)
	{
		if(s.length() <= 1) return s.toUpperCase();
		char[] letters = s.toCharArray();
		letters[0] = Character.toUpperCase(letters[0]);
		return new String(letters);
	}

	public void parse(ArrayList<String> params)
	{
		Set<String> keys = modules.keySet();
		for(String key: keys)
		{
			modules.get(key).parse(params);
		}
	}
 
	public void clean()
	{
		Set<String> keys = modules.keySet();
		for(String key: keys)
		{
			modules.get(key).clean();
		}
	}
}