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

import java.util.*;

public class IterableSet extends HashChain implements Set
{
    public IterableSet( Collection c)
    {
	super();
	addAll( c);
    }

    public IterableSet()
    {
	super();
    }

    public boolean add( Object o)
    {
	if (o == null)
	    throw new IllegalArgumentException( "Cannot add \"null\" to an IterableSet.");

	if (contains( o))
	    return false;

	return super.add( o);
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

	IterableSet other = (IterableSet) o;

	if (size() != other.size())
	    return false;
	
	Iterator it = iterator();
	while (it.hasNext()) 
	    if (other.contains( it.next()) == false)
		return false;
	
	return true;
    }
    
    public Object clone()
    {
	IterableSet s = new IterableSet();
	s.addAll( this);
	return s;
    }    

    public boolean isSubsetOf( IterableSet other)
    {
	if (other == null)
	    throw new IllegalArgumentException( "Cannot set compare an IterableSet with \"null\".");

	if (size() > other.size())
	    return false;

	Iterator it = iterator();
	while (it.hasNext())
	    if (other.contains( it.next()) == false)
		return false;

	return true;
    }
    
    public boolean isSupersetOf( IterableSet other)
    {
	if (other == null)
	    throw new IllegalArgumentException( "Cannot set compare an IterableSet with \"null\".");

	if (size() < other.size())
	    return false;

	Iterator it = other.iterator();
	while (it.hasNext())
	    if (contains( it.next()) == false)
		return false;
	
	return true;
    }

    public boolean isStrictSubsetOf( IterableSet other)
    {
	if (other == null)
	    throw new IllegalArgumentException( "Cannot set compare an IterableSet with \"null\".");

	if (size() >= other.size())
	    return false;

	return isSubsetOf( other);
    }
    
    public boolean isStrictSupersetOf( IterableSet other)
    {
	if (other == null)
	    throw new IllegalArgumentException( "Cannot set compare an IterableSet with \"null\".");

	if (size() <= other.size())
	    return false;

	return isSupersetOf( other);
    }


    public boolean intersects( IterableSet other)
    {
	if (other == null)
	    throw new IllegalArgumentException( "Cannot set intersect an IterableSet with \"null\".");

	if (other.size() < size()) {
	    Iterator it = other.iterator();
	    while (it.hasNext())
		if (contains( it.next()))
		    return true;
	}
	else {
	    Iterator it = iterator();
	    while (it.hasNext())
		if (other.contains( it.next()))
		    return true;
	}

	return false;
    }

    public IterableSet intersection( IterableSet other)
    {
	if (other == null)
	    throw new IllegalArgumentException( "Cannot set intersect an IterableSet with \"null\".");

	IterableSet c = new IterableSet();

	if (other.size() < size()) {
	    Iterator it = other.iterator();
	    while (it.hasNext()) {
		Object o = it.next();
		
		if (contains( o))
		    c.add( o);
	    }
	}
	else {
	    Iterator it = iterator();
	    while (it.hasNext()) {
		Object o = it.next();
		
		if (other.contains( o))
		    c.add( o);
	    }
	}
	return c;
    }

    public IterableSet union( IterableSet other)
    {
	if (other == null)
	    throw new IllegalArgumentException( "Cannot set union an IterableSet with \"null\".");

	IterableSet c = new IterableSet();

	c.addAll( this);
	c.addAll( other);

	return c;
    }

    public String toString()
    {
	StringBuffer b = new StringBuffer();

	Iterator it = iterator();
	while (it.hasNext()) {
	    b.append( it.next().toString());
	    b.append( "\n");
	}

	return b.toString();
    }
}
