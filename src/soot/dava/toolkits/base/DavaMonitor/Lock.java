/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.dava.toolkits.base.DavaMonitor;

import java.util.LinkedList;

class Lock
{
    public Thread owner;
    public int level;

    private LinkedList q;

    Lock()
    {
	level = 0;
	owner = null;
	q = new LinkedList();
    }

    public Thread nextThread()
    {
	return (Thread) q.getFirst();
    }

    public Thread deQ( Thread t) throws IllegalMonitorStateException
    {
	if (t != q.getFirst())
	    throw new IllegalMonitorStateException();

	return (Thread) q.removeFirst();
    }

    public void enQ( Thread t)
    {
	q.add( t);
    }
}
