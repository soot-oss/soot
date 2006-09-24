/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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

package soot.util;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Implements a hashset with comparison over identity.
 * @author Eric Bodden
 */
public class IdentityHashSet implements Set {

    protected IdentityHashMap delegate;
    
    /**
     * Creates a new, empty IdentityHashSet. 
     */
    public IdentityHashSet() {
        delegate = new IdentityHashMap();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return delegate.size();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Object o) {
        return delegate.containsKey(o);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator iterator() {
        return delegate.keySet().iterator();
    }

    /**
     * {@inheritDoc}
     */
    public Object[] toArray() {
        return delegate.keySet().toArray();
    }

    /**
     * {@inheritDoc}
     */
    public Object[] toArray(Object[] a) {
        return delegate.keySet().toArray(a);
    }

    /**
     * {@inheritDoc}
     */
    public boolean add(Object o) {
        return delegate.put(o, o)!=null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(Object o) {
        return delegate.remove(o)!=null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAll(Collection c) {
        return delegate.keySet().containsAll(c);
    }

    /**
     * {@inheritDoc}
     */
    public boolean addAll(Collection c) {
        boolean b = true;
        for (Iterator iter = c.iterator(); iter.hasNext();) {
            Object o = iter.next();
            b &= add(o);
        }
        return b;
    }

    /**
     * {@inheritDoc}
     */
    public boolean retainAll(Collection c) {
        boolean b = false;
        for (Iterator iter = iterator(); iter.hasNext();) {
            Object o = iter.next();
            if(!c.contains(o)) {
            	iter.remove();
            	b = true;
            }
        }
        return b;
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeAll(Collection c) {
        boolean b = true;
        for (Iterator iter = c.iterator(); iter.hasNext();) {
            Object o = iter.next();
            b &= remove(o);
        }
        return b;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        delegate.entrySet().clear();
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((delegate == null) ? 0 : delegate.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final IdentityHashSet other = (IdentityHashSet) obj;
		if (delegate == null) {
			if (other.delegate != null)
				return false;
		} else if (!delegate.equals(other.delegate))
			return false;
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return delegate.keySet().toString();
	}
    
}
