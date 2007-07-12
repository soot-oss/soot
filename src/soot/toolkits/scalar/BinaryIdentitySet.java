/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Eric Bodden
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
package soot.toolkits.scalar;

import soot.util.IdentityHashSet;

/**
 * An optimized kind of {@link IdentityHashSet} that only holds two objects. (Allows for faster comparison.) 
 * 
 * @author Eric Bodden
 */
public class BinaryIdentitySet<T> {
    protected final T o1;
    protected final T o2;
    protected final int hashCode;

    public BinaryIdentitySet(T o1, T o2) {
        this.o1 = o1;
        this.o2 = o2;
        this.hashCode = computeHashCode();
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * {@inheritDoc}
     */
    private int computeHashCode() {
        int result = 1;
        //must be commutative
        result += System.identityHashCode(o1);
        result += System.identityHashCode(o2);
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final BinaryIdentitySet other = (BinaryIdentitySet) obj;
        //must be commutative
        if (o1 != other.o1 && o1 != other.o2)
            return false;
        if (o2 != other.o2 && o2 != other.o1)
            return false;
        return true;
    }

    public T getO1() {
        return o1;
    }

    public T getO2() {
        return o2;
    }

    public String toString() {
        return "IdentityPair " + o1 + "," + o2;
    }
}