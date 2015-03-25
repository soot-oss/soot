package soot.jimple.toolkits.infoflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.*;
import soot.util.*;

import java.util.*;

// Wraps any object as a Value

public class AbstractDataSource implements Value
{

	private static final Logger logger =LoggerFactory.getLogger(AbstractDataSource.class);
	Object sourcename;
	
	public AbstractDataSource(Object sourcename)
	{
		this.sourcename = sourcename;
	}
	
    @Override
    public List<ValueBox> getUseBoxes()
    {
        return Collections.emptyList();
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
    	if(sourcename instanceof Value)
    		return (c instanceof AbstractDataSource && ((Value) sourcename).equivTo( ((AbstractDataSource)c).sourcename ));
        return (c instanceof AbstractDataSource && ((AbstractDataSource)c).sourcename.equals(sourcename));
    }
    
    public boolean equals(Object c)
    {
        return (c instanceof AbstractDataSource && ((AbstractDataSource)c).sourcename.equals(sourcename));
    }

    /** Returns a hash code consistent with structural equality for this object. */
    public int equivHashCode()
    {
    	if(sourcename instanceof Value)
    		return ((Value) sourcename).equivHashCode();
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
    	return "sourceof<" + sourcename.toString() + ">";
    }
}
