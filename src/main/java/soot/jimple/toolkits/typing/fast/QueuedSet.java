/* Soot - a J*va Optimization Framework
 * Copyright (C) 2008 Ben Bellamy 
 * 
 * All rights reserved.
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
package soot.jimple.toolkits.typing.fast;

import java.util.*;

/**
 * @author Ben Bellamy
 */
public class QueuedSet<E>
{
	private Set<E> hs;
	private LinkedList<E> ll;
	
	public QueuedSet()
	{
		this.hs = new HashSet<E>();
		this.ll = new LinkedList<E>();
	}
	
	public QueuedSet(List<E> os)
	{
		this();	
		for ( E o : os )
		{
			this.ll.addLast(o);
			this.hs.add(o);
		}
	}
	
	public QueuedSet(QueuedSet<E> qs)
	{
		this(qs.ll);
	}
	
	public boolean isEmpty()
	{
		return this.ll.isEmpty();
	}
	
	public boolean addLast(E o)
	{
		boolean r = this.hs.contains(o);
		if ( !r )
		{
			this.ll.addLast(o);
			this.hs.add(o);
		}
		return r;
	}
	
	public int addLast(List<E> os)
	{
		int r = 0;
		for ( E o : os )
			if ( this.addLast(o) )
				r++;
		return r;
	}
	
	public E removeFirst()
	{
		E r = this.ll.removeFirst();
		this.hs.remove(r);
		return r;
	}
}