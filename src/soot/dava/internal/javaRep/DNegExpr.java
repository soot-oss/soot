package soot.dava.internal.javaRep;

import soot.*;
import soot.grimp.*;
import soot.jimple.*;
import soot.jimple.internal.*;

public class DNegExpr extends AbstractNegExpr
{
    public DNegExpr(Value op)
    {
        super(Grimp.v().newExprBox(op));
    }
      
    public Object clone() 
    {
        return new DNegExpr(Grimp.cloneIfNecessary(getOp()));
    }

    public void toString( UnitPrinter up ) {
        up.literal( "(" );
        up.literal( "-" );
        up.literal( " " );
        up.literal( "(" );
        getOpBox().toString(up);
        up.literal( ")" );
        up.literal( ")" );
    }

    public String toString()
    {
	return "(- (" + ( getOpBox().getValue()).toString() + "))"; 
    }
}
