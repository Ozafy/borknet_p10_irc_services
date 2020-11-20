/**
#
# The Q bot
# Channelservice
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
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The mail class of the Q IRC Bot.
 * @author Ozafy - ozafy@borknet.org - http://www.borknet.org
 */
public class CoreTimer implements Runnable
{
	private Core C;

    Timer timer;

    /**
     * Runnable programs need to define this class.
     */
	public void run()
	{
		int delay = 60*1000; //milliseconds
		ActionListener taskPerformer = new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				C.timerTick();
			}
		};
		timer = new Timer(delay, taskPerformer);
		timer.start();
	}

    /**
     * Set the tutorial
     */
	public CoreTimer(Core C)
	{
		this.C = C;
	}

	public void stop()
	{
		timer.stop();
	}
}//end of class