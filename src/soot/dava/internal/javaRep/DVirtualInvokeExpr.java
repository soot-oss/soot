package soot.dava.internal.javaRep;

import soot.*;
import java.util.*;
import soot.grimp.*;
import soot.grimp.internal.*;

public class DVirtualInvokeExpr extends GVirtualInvokeExpr
{
    private HashSet thisLocals;

    public DVirtualInvokeExpr( Value base, SootMethod method, java.util.List args, HashSet thisLocals) 
    {
	super( base, method, args);

	this.thisLocals = thisLocals;
    }

    public String toBriefString()
    {
	return toString();
    }
    

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
	
	if (thisLocals.contains( getBase()) == false)
	    buffer.append( ((ToBriefString) getBase()).toBriefString() + "." );

	buffer.append( getMethod().getName() + "(");
	
        for(int i = 0; i < argBoxes.length; i++) {
	    if(i != 0)
		buffer.append(", ");
	    
	    buffer.append( ((ToBriefString) argBoxes[i].getValue()).toBriefString());
	}
	
        buffer.append(")");
	
        return buffer.toString();
    }

    public Object clone() 
    {
        ArrayList clonedArgs = new ArrayList( getArgCount());

        for(int i = 0; i < getArgCount(); i++) 
            clonedArgs.add(i, Grimp.cloneIfNecessary(getArg(i)));
        
        return new  DVirtualInvokeExpr(Grimp.cloneIfNecessary(getBase()), getMethod(), clonedArgs, thisLocals);
    }
}
