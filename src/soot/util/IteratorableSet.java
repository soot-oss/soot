package soot.util;

import java.util.*;

public class IteratorableSet extends HashChain implements Set
{
    public boolean equals( IteratorableSet other)
    {
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
	IteratorableSet s = new IteratorableSet();
	s.addAll( this);
	return s;
    }    

    public boolean isSubsetOf( IteratorableSet other)
    {
	if (size() > other.size())
	    return false;

	Iterator it = iterator();
	while (it.hasNext())
	    if (other.contains( it.next()) == false)
		return false;

	return true;
    }
    
    public boolean isSupersetOf( IteratorableSet other)
    {
	if (size() < other.size())
	    return false;

	Iterator it = other.iterator();
	while (it.hasNext())
	    if (contains( it.next()) == false)
		return false;
	
	return true;
    }

    public boolean isStrictSubsetOf( IteratorableSet other)
    {
	if (size() >= other.size())
	    return false;

	return isSubsetOf( other);
    }
    
    public boolean isStrictSupersetOf( IteratorableSet other)
    {
	if (size() <= other.size())
	    return false;

	return isSupersetOf( other);
    }


    public boolean intersects( IteratorableSet other)
    {
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

    public IteratorableSet intersection( IteratorableSet other)
    {
	IteratorableSet c = new IteratorableSet();

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

    public IteratorableSet union( IteratorableSet other)
    {
	IteratorableSet c = new IteratorableSet();
	c.addAll( this);

	Iterator it = other.iterator();
	while (it.hasNext()) {
	    Object o = it.next();

	    if (c.contains( o) == false)
		c.add( o);
	}

	return c;
    }
}
