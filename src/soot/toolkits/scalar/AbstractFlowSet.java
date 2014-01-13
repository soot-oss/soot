/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Florian Loitsch
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


package soot.toolkits.scalar;

import java.util.*;

/** 
 * provides functional code for most of the methods. Subclasses are invited to
 * provide a more efficient version. Most often this will be done in the
 * following way:<br>
 * <pre>
 * public void yyy(FlowSet dest) {
 *   if (dest instanceof xxx) {
 *     blahblah;
 *   } else
 *     super.yyy(dest)
 * }
 * </pre>
 */
public abstract class AbstractFlowSet implements FlowSet, Iterable {
  public abstract AbstractFlowSet clone();

  /**
   * implemented, but inefficient.
   */
  public Object emptySet() {
    FlowSet t = clone();
    t.clear();
    return t;
  }

  public void copy(FlowSet dest) {
    List elements = toList();
    Iterator it = elements.iterator();
    dest.clear();
    while (it.hasNext())
      dest.add(it.next());
  }

  /**
   * implemented, but *very* inefficient.
   */
  public void clear() {
    Iterator it = toList().iterator();
    while (it.hasNext())
      remove(it.next());
  }

  public void union(FlowSet other) {
    union(other, this);
  }

  public void union(FlowSet other, FlowSet dest) {
    if (dest != this && dest != other)
      dest.clear();

    if (dest != this) {
      Iterator thisIt = toList().iterator();
      while (thisIt.hasNext())
        dest.add(thisIt.next());
    }

    if (dest != other) {
      Iterator otherIt = other.toList().iterator();
      while (otherIt.hasNext())
        dest.add(otherIt.next());
    }
  }

  public void intersection(FlowSet other) {
    intersection(other, this);
  }

  public void intersection(FlowSet other, FlowSet dest) {
    if (dest == this && dest == other) return;
    List elements = null;
    FlowSet flowSet = null;
    if (dest == this) {
      /* makes automaticly a copy of <code>this</code>, as it will be cleared */
      elements = toList();
      flowSet = other;
    } else {
      /* makes a copy o <code>other</code>, as it might be cleared */
      elements = other.toList();
      flowSet = this;
    }
    dest.clear();
    Iterator it = elements.iterator();
    while (it.hasNext()) {
      Object o = it.next();
      if (flowSet.contains(o))
        dest.add(o);
    }
  }

  public void difference(FlowSet other) {
    difference(other, this);
  }

  public void difference(FlowSet other, FlowSet dest) {
    if (dest == this && dest == other) {
      dest.clear();
      return;
    }

    Iterator it = this.toList().iterator();
    FlowSet flowSet = (other == dest)? (FlowSet)other.clone(): other;
    dest.clear(); // now safe, since we have copies of this & other

    while (it.hasNext()) {
      Object o = it.next();
      if (!flowSet.contains(o))
        dest.add(o);
    }
  }

  public abstract boolean isEmpty();

  public abstract int size();

  public abstract void add(Object obj);

  public void add(Object obj, FlowSet dest) {
    if (dest != this)
      copy(dest);
    dest.add(obj);
  }
  
  public abstract void remove(Object obj);

  public void remove(Object obj, FlowSet dest) {
    if (dest != this)
      copy(dest);
    dest.remove(obj);
  }

  public abstract boolean contains(Object obj);

  public Iterator iterator() {
    return toList().iterator();
  }

  public abstract List toList();

  public boolean equals(Object o) {
    if (o.getClass()!=getClass()) return false;
    FlowSet other = (FlowSet)o;
    if (size() != other.size()) return false;
    Iterator it = toList().iterator();
    while (it.hasNext())
      if (!other.contains(it.next())) return false;
    return true;
  }
  
	public int hashCode() {
		int result = 1;
		Iterator iter = iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			result += o.hashCode();
		}
		return result;
	}

  public String toString() {
    StringBuffer buffer = new StringBuffer("{");
    Iterator it = toList().iterator();
    if (it.hasNext()) {
      buffer.append(it.next());

      while(it.hasNext())
        buffer.append(", " + it.next());
    }
    buffer.append("}");
    return buffer.toString();
  }
}

