package soot.dava.internal.javaRep;

import soot.*;
import java.util.*;
import soot.grimp.*;
import soot.grimp.internal.*;

public class DStaticInvokeExpr extends GStaticInvokeExpr
{
    public DStaticInvokeExpr( SootMethod method, java.util.List args) 
    {
	super( method, args);
    }

    public void toString( UnitPrinter up ) {
        up.type( getMethod().getDeclaringClass().getType() );
        up.literal( "." );
        super.toString(up);
    }

    public Object clone() 
    {
        ArrayList clonedArgs = new ArrayList( getArgCount());

        for(int i = 0; i < getArgCount(); i++) 
            clonedArgs.add(i, Grimp.cloneIfNecessary(getArg(i)));
        
        return new DStaticInvokeExpr( getMethod(), clonedArgs);
    }
}
