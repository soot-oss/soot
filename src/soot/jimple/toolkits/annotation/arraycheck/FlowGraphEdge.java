package soot.jimple.toolkits.annotation.arraycheck;

import soot.*;
import soot.toolkits.scalar.*;
import soot.util.*;
import java.util.*;

class FlowGraphEdge
{
    Object from;
    Object to;

    public FlowGraphEdge()
    {
        this.from = null; 
	this.to = null;
    }
    
    public FlowGraphEdge(Object from, Object to)
    {
        this.from = from;
	this.to = to;
    }

    public int hashCode()
    {
        return this.from.hashCode()^this.to.hashCode();
    }

    public Object getStartUnit()
    {
        return this.from;
    }

    public Object getTargetUnit()
    {
        return this.to;
    }

    public void changeEndUnits(Object from, Object to)
    {
        this.from = from;
	this.to = to;
    }
    
    public boolean equals(Object other)
    {
	if (other == null)
	    return false;

        if (other instanceof FlowGraphEdge)
	{
	    Object otherstart = ((FlowGraphEdge)other).getStartUnit();
	    Object othertarget = ((FlowGraphEdge)other).getTargetUnit();

	    return (this.from.equals(otherstart)&&this.to.equals(othertarget));
	}
	else
	    return false;
    }
}
