/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Sable Research Group
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


package soot.util;

import java.util.Collection;
import java.util.Set;

public class IterableSet<T> extends HashChain<T> implements Set<T>
{
    public IterableSet( Collection<T> c)
    {
	super();
	addAll( c);
    }

    public IterableSet()
    {
	super();
    }

    public boolean add( T o)
    {
	if (o == null)
	    throw new IllegalArgumentException( "Cannot add \"null\" to an IterableSet.");

	if (contains( o))
	    return false;

	return super.add(o);
    }

    public boolean remove( Object o)
    {
	if ((o == null) || (contains( o) == false))
	    return false;

	return super.remove( o);
    }
    
    public boolean equals( Object o)
    {
	if (o == null)
	    return false;

	if (this == o)
	    return true;

	if ((o instanceof IterableSet) == false)
	    return false;

	@SuppressWarnings("unchecked")
	IterableSet<T> other = (IterableSet<T>) o;

	if (size() != other.size())
	    return false;
	
	for (T t : this)
	    if (!other.contains(t))
	    	return false;
	
	return true;
    }
    
    @Override
    public int hashCode() {
    	int code = 23;
    	for (T t : this) {
    		//use addition here to have hash code independent of order
    		code += t.hashCode();
    	}
    	return code;
    }
    
    public Object clone()
    {
	IterableSet<T> s = new IterableSet<T>();
	s.addAll( this);
	return s;
    }    

    public boolean isSubsetOf( IterableSet<T> other)
    {
	if (other == null)
	    throw new IllegalArgumentException( "Cannot set compare an IterableSet with \"null\".");

	if (size() > other.size())
	    return false;

	for (T t : this)
	    if (!other.contains(t))
	    	return false;

	return true;
    }
    
    public boolean isSupersetOf( IterableSet<T> other)
    {
	if (other == null)
	    throw new IllegalArgumentException( "Cannot set compare an IterableSet with \"null\".");

	if (size() < other.size())
	    return false;

	for (T t : other)
	    if (!contains(t))
	    	return false;
	
	return true;
    }

    public boolean isStrictSubsetOf( IterableSet<T> other)
    {
	if (other == null)
	    throw new IllegalArgumentException( "Cannot set compare an IterableSet with \"null\".");

	if (size() >= other.size())
	    return false;

	return isSubsetOf( other);
    }
    
    public boolean isStrictSupersetOf( IterableSet<T> other)
    {
	if (other == null)
	    throw new IllegalArgumentException( "Cannot set compare an IterableSet with \"null\".");

	if (size() <= other.size())
	    return false;

	return isSupersetOf( other);
    }


    public boolean intersects( IterableSet<T> other)
    {
	if (other == null)
	    throw new IllegalArgumentException( "Cannot set intersect an IterableSet with \"null\".");

	if (other.size() < size()) {
	    for (T t : other)
	    	if (contains(t))
	    		return true;
	}
	else {
	    for (T t : this)
			if (other.contains(t))
			    return true;
	}

	return false;
    }

    public IterableSet<T> intersection( IterableSet<T> other)
    {
	if (other == null)
	    throw new IllegalArgumentException( "Cannot set intersect an IterableSet with \"null\".");

	IterableSet<T> c = new IterableSet<T>();

	if (other.size() < size()) {
	    for (T t : other)
			if (contains(t))
			    c.add(t);
	}
	else {
	    for (T t : this)
	    	if (other.contains(t))
	    		c.add(t);
	}
	return c;
    }

    public IterableSet<T> union( IterableSet<T> other)
    {
	if (other == null)
	    throw new IllegalArgumentException( "Cannot set union an IterableSet with \"null\".");

	IterableSet<T> c = new IterableSet<T>();

	c.addAll( this);
	c.addAll( other);

	return c;
    }

    public String toString()
    {
	StringBuffer b = new StringBuffer();

	for (T t : this) {
	    b.append( t.toString());
	    b.append( "\n");
	}

	return b.toString();
    }
}
