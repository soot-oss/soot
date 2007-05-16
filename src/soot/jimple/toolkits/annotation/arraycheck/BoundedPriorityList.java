/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Feng Qian
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.jimple.toolkits.annotation.arraycheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/** BoundedPriorityList keeps a list in a priority queue.
 * The order is decided by the initial list. 
 * 
 * @author Eric Bodden (adapted from Feng Qian's code)
 */
public class BoundedPriorityList implements Collection
{
    protected final List fulllist;
    protected ArrayList worklist; 

    public BoundedPriorityList(List list) {
		this.fulllist = list;
		this.worklist = new ArrayList(list);
	}

	public boolean isEmpty() {
		return worklist.isEmpty();
	}

	public Object removeFirst() {
		return worklist.remove(0);
	}

	public boolean add(Object toadd) {
		if(contains(toadd)) {
			return false;
		}
		
		/* it is not added to the end, but keep it in the order */
		int index = fulllist.indexOf(toadd);

		for (ListIterator worklistIter = worklist.listIterator(); worklistIter
				.hasNext();) {
			Object tocomp = worklistIter.next();
			int tmpidx = fulllist.indexOf(tocomp);
			if (index < tmpidx) {
				worklistIter.add(toadd);
				return true;
			}
		}

		return false;
	}

	//rest is only necessary to implement the Collection interface 

	/**
	 * {@inheritDoc}
	 */
	public boolean addAll(Collection c) {
		boolean addedSomething = false;
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			Object o = iter.next();
			addedSomething |= add(o);			
		}
		return addedSomething;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addAll(int index, Collection c) {
		throw new RuntimeException("Not supported. You should use addAll(Collection) to keep priorities.");
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		worklist.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(Object o) {
		return worklist.contains(o);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsAll(Collection c) {
		return worklist.containsAll(c);
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator iterator() {
		return worklist.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean remove(Object o) {
		return worklist.remove(o);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeAll(Collection c) {
		return worklist.removeAll(c);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean retainAll(Collection c) {
		return worklist.retainAll(c);
	}

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return worklist.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object[] toArray() {
		return worklist.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object[] toArray(Object[] a) {
		return worklist.toArray(a);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return worklist.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		return worklist.equals(obj);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return worklist.hashCode();
	}
}
