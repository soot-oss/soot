package soot.dava.internal.javaRep;

import soot.*;
import java.util.*;
import soot.grimp.*;
import soot.grimp.internal.*;

public class DNewInvokeExpr extends GNewInvokeExpr
{
    public DNewInvokeExpr( RefType type, SootMethod method, java.util.List args) 
    {
	super( type, method, args);
    }

    public Object clone() 
    {
        ArrayList clonedArgs = new ArrayList( getArgCount());

        for(int i = 0; i < getArgCount(); i++) 
            clonedArgs.add(i, Grimp.cloneIfNecessary(getArg(i)));
        
        return new DNewInvokeExpr( (RefType) getType(), getMethod(), clonedArgs);
    }
}
