package soot.dava.internal.javaRep;

import soot.*;
import java.util.*;
import soot.grimp.*;
import soot.grimp.internal.*;

public class DInterfaceInvokeExpr extends GInterfaceInvokeExpr
{
    public DInterfaceInvokeExpr( Value base, SootMethod method, java.util.List args) 
    {
	super( base, method, args);
    }

    public String toBriefString()
    {
	return toString();
    }

    public String toString()
    {
	if (getBase().getType() instanceof NullType) {
	    StringBuffer b = new StringBuffer();

	    b.append( "((");
	    b.append( getMethod().getDeclaringClass().getJavaStyleName());
	    b.append( ") ");
	    
	    String baseStr = ((ToBriefString) getBase()).toBriefString();
	    if ((getBase() instanceof Precedence) && ( ((Precedence) getBase()).getPrecedence() < getPrecedence()))
		baseStr = "(" + baseStr + ")";

	    b.append( baseStr);
	    b.append( ").");

	    b.append( getMethod().getName());
	    b.append( "(");

	    for (int i=0; i<argBoxes.length; i++) {
		if(i != 0)
		    b.append(", ");
		
		b.append( ((ToBriefString) argBoxes[i].getValue()).toBriefString());
	    }

	    b.append(")");

	    return b.toString();
	}

	return super.toBriefString();
    }

    public Object clone() 
    {
        ArrayList clonedArgs = new ArrayList( getArgCount());

        for(int i = 0; i < getArgCount(); i++) 
            clonedArgs.add(i, Grimp.cloneIfNecessary(getArg(i)));
        
        return new DInterfaceInvokeExpr( getBase(), getMethod(), clonedArgs);
    }
}
