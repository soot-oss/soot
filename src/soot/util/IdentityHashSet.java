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
import java.util.AbstractSet;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Implements a hashset with comparison over identity.
 * 
 * @author Eric Bodden
 * @deprecated can be replaced 
 * 	with <code>java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<E,Boolean>())</code>
 */
@Deprecated
public class IdentityHashSet<E> extends AbstractSet<E> implements Set<E> {

    protected IdentityHashMap<E,E> delegate;
    
    /**
     * Creates a new, empty IdentityHashSet. 
     */
    public IdentityHashSet() {
        delegate = new IdentityHashMap<E, E>();
    }

    /**
     * Creates a new IdentityHashSet containing the same elements as the given
     * collection.
     * @param original The original collection whose elements to inherit
     */
    public IdentityHashSet(Collection<E> original) {
        delegate = new IdentityHashMap<E, E>();
        addAll(original);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return delegate.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o) {
        return delegate.containsKey(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator() {
        return delegate.keySet().iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(E o) {
        return delegate.put(o, o)==null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object o) {
        return delegate.remove(o)!=null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        delegate.entrySet().clear();
    }

	/* 
	 * Equality based on identity.
	 */
    @Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((delegate == null) ? 0 : delegate.hashCode());
		return result;
	}

	/* 
	 * Hash code based on identity.
	 */
    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final IdentityHashSet<?> other = (IdentityHashSet<?>) obj;
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
    @Override
	public String toString() {
		return delegate.keySet().toString();
	}
    
}
