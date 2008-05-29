
package soot.jimple.toolkits.thread.synchronization;

import java.util.*;

import soot.*;
import soot.util.*;

// Written by Richard L. Halpert on August 11, 2007
// Acts as a dummy value that gets put in a transaction's lockset,
// indicating that a new static object needs to be inserted into the
// program for use as a lock.

public class NewStaticLock implements Value
{
	SootClass sc; // The class to which to add a static lock.
	static int nextidnum = 1;
	int idnum;
	
	public NewStaticLock(SootClass sc)
	{
		this.sc = sc;
		this.idnum = nextidnum++;
	}
	
	public SootClass getLockClass()
	{
		return sc;
	}

    public List getUseBoxes()
    {
        return AbstractUnit.emptyList;
    }

    /** Clones the object.  Not implemented here. */
    public Object clone() 
    {
        return new NewStaticLock(sc);
    }

    /** Returns true if this object is structurally equivalent to c. 
     * AbstractDataSources are equal and equivalent if their sourcename is the same */
    public boolean equivTo(Object c)
    {
    	return equals(c);
    }
    
    public boolean equals(Object c)
    {
    	if(c instanceof NewStaticLock)
    	{
    		return ((NewStaticLock) c).idnum == idnum;
    	}
    	return false;
    }
    
    /** Returns a hash code consistent with structural equality for this object. */
    public int equivHashCode()
    {
        return hashCode();
    }
    
    public int hashCode()
    {
    	return idnum;
    }
    
    public void toString( UnitPrinter up ) {}
    
    public Type getType()
    {
    	return NullType.v();
    }
    
    public void apply(Switch sw)
    {
    	throw new RuntimeException("Not Implemented");
    }
    
    public String toString()
    {
    	return "<new static lock in " + sc.toString() + ">";
    }
}
