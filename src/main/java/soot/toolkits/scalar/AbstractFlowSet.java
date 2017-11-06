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

import java.util.Iterator;
import java.util.List;

/**
 * provides functional code for most of the methods. Subclasses are invited to
 * provide a more efficient version. Most often this will be done in the
 * following way:<br>
 * 
 * <pre>
 * public void yyy(FlowSet dest) {
 *   if (dest instanceof xxx) {
 *     blahblah;
 *   } else
 *     super.yyy(dest)
 * }
 * </pre>
 */
public abstract class AbstractFlowSet<T> implements FlowSet<T> {
	public abstract AbstractFlowSet<T> clone();

	/**
	 * implemented, but inefficient.
	 */
	public FlowSet<T> emptySet() {
		FlowSet<T> t = clone();
		t.clear();
		return t;
	}

	public void copy(FlowSet<T> dest) {
		if (this == dest)
			return;
		dest.clear();
		for (T t : this)
			dest.add(t);
	}

	/**
	 * implemented, but *very* inefficient.
	 */
	public void clear() {
		for (T t : this)
			remove(t);
	}

	public void union(FlowSet<T> other) {
		if (this == other)
			return;
		union(other, this);
	}

	public void union(FlowSet<T> other, FlowSet<T> dest) {
		if (dest != this && dest != other)
			dest.clear();

		if (dest != null && dest != this) {
			for (T t : this)
				dest.add(t);
		}

		if (other != null && dest != other) {
			for (T t : other)
				dest.add(t);
		}
	}

	public void intersection(FlowSet<T> other) {
		if (this == other)
			return;
		intersection(other, this);
	}

	public void intersection(FlowSet<T> other, FlowSet<T> dest) {
		if (dest == this && dest == other)
			return;
		FlowSet<T> elements = null;
		FlowSet<T> flowSet = null;
		if (dest == this) {
			/*
			 * makes automaticly a copy of <code>this</code>, as it will be
			 * cleared
			 */
			elements = this;
			flowSet = other;
		} else {
			/* makes a copy o <code>other</code>, as it might be cleared */
			elements = other;
			flowSet = this;
		}
		dest.clear();
		for (T t : elements) {
			if (flowSet.contains(t))
				dest.add(t);
		}
	}

	public void difference(FlowSet<T> other) {
		difference(other, this);
	}

	public void difference(FlowSet<T> other, FlowSet<T> dest) {
		if (dest == this && dest == other) {
			dest.clear();
			return;
		}

		FlowSet<T> flowSet = (other == dest) ? other.clone() : other;
		dest.clear(); // now safe, since we have copies of this & other

		for (T t : this)
			if (!flowSet.contains(t))
				dest.add(t);
	}

	public abstract boolean isEmpty();

	public abstract int size();

	public abstract void add(T obj);

	public void add(T obj, FlowSet<T> dest) {
		if (dest != this)
			copy(dest);
		dest.add(obj);
	}

	public abstract void remove(T obj);

	public void remove(T obj, FlowSet<T> dest) {
		if (dest != this)
			copy(dest);
		dest.remove(obj);
	}

	@Override
	public boolean isSubSet(FlowSet<T> other) {
		if (other == this)
			return true;
		
		for (T t : other) {
			if (!contains(t))
				return false;
		}
		return true;
	}

	public abstract boolean contains(T obj);

	public abstract Iterator<T> iterator();

	public abstract List<T> toList();

	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (!(o instanceof FlowSet))
			return false;
		FlowSet<T> other = (FlowSet<T>) o;
		if (size() != other.size())
			return false;
		for (T t : this)
			if (!other.contains(t))
				return false;
		return true;
	}

	public int hashCode() {
		int result = 1;
		for (T t : this)
			result += t.hashCode();
		return result;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("{");

		boolean isFirst = true;
		for (T t : this) {
			if (!isFirst)
				buffer.append(", ");
			isFirst = false;

			buffer.append(t);
		}
		buffer.append("}");
		return buffer.toString();
	}
}
