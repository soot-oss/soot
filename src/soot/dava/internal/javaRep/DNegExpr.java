package soot.dava.internal.javaRep;

import soot.*;
import soot.grimp.*;
import soot.jimple.*;
import soot.jimple.internal.*;

public class DNegExpr extends AbstractNegExpr implements NegExpr
{
    public DNegExpr(Value op)
    {
        super(Grimp.v().newExprBox(op));
    }
      
    public Object clone() 
    {
        return new DNegExpr(Grimp.cloneIfNecessary(getOp()));
    }

    public String toString()
    {
	return "(- (" + ((ToBriefString) getOpBox().getValue()).toBriefString() + "))"; 
    }

    public String toBriefString()
    {
	return toString();
    }
}
