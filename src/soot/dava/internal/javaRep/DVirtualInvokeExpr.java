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

    public void toString( UnitPrinter up ) {
	if (getBase().getType() instanceof NullType) {
        // OL: I don't know what this is for; I'm just refactoring the
        // original code. An explanation here would be welcome.
            up.literal( "((" );
            up.type( getMethod().getDeclaringClass().getType() );
            up.literal( ") " );
	    
            if( PrecedenceTest.needsBrackets( baseBox, this ) ) up.literal("(");
            baseBox.toString( up );
            if( PrecedenceTest.needsBrackets( baseBox, this ) ) up.literal(")");

	    up.literal( ")" );
            up.literal( "." );

            up.method( getMethod() );
            up.literal( "(" );

	    for (int i=0; i<argBoxes.length; i++) {
		if(i != 0)
                    up.literal( ", " );
		
                argBoxes[i].toString(up);
	    }

            up.literal( ")" );
	} else {
            super.toString( up );
        }
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
        
        return new  DVirtualInvokeExpr(Grimp.cloneIfNecessary(getBase()), getMethod(), clonedArgs, thisLocals);
    }
}
