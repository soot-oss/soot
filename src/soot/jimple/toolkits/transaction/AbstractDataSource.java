package soot.jimple.toolkits.transaction;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.toolkits.mhp.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.jimple.toolkits.callgraph.*;
import soot.tagkit.*;
import soot.jimple.internal.*;
import soot.jimple.*;
import soot.jimple.spark.sets.*;
import soot.jimple.spark.pag.*;
import soot.toolkits.scalar.*;

// A value used to represent shared/local data in a LocalObjectsMethodAnalysis

public class AbstractDataSource implements Value
{
	String sourcename;
	
	public AbstractDataSource(String sourcename)
	{
		this.sourcename = sourcename;
	}
	
    public List getUseBoxes()
    {
        return AbstractUnit.emptyList;
    }

    /** Clones the object.  Not implemented here. */
    public Object clone() 
    {
        return new AbstractDataSource(sourcename);
    }

    /** Returns true if this object is structurally equivalent to c. 
     * AbstractDataSources are equal and equivalent if their sourcename is the same */
    public boolean equivTo(Object c)
    {
        return equals(c);
    }
    
    public boolean equals(Object c)
    {
        return (c instanceof AbstractDataSource && ((AbstractDataSource)c).sourcename.equals(sourcename));
    }

    /** Returns a hash code consistent with structural equality for this object. */
    public int equivHashCode()
    {
        return sourcename.hashCode();
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
    	return sourcename + " data source";
    }
}
