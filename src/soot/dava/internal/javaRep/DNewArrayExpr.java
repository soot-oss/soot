package soot.dava.internal.javaRep;

import soot.*;
import soot.grimp.*;
import soot.jimple.*;
import soot.jimple.internal.*;

public class DNewArrayExpr extends AbstractNewArrayExpr implements Precedence
{
    public DNewArrayExpr(Type type, Value size)
    {
	super(type, Grimp.v().newExprBox(size));
    }
    
    public int getPrecedence() { return 850; }
    
    public Object clone() 
    {
        return new DNewArrayExpr(getBaseType(), Grimp.cloneIfNecessary(getSize()));
    }

    public void toString( UnitPrinter up ) {
        up.literal( "new" );
        up.literal( " " );
        up.type( getBaseType() );
        up.literal( "[" );
        getSizeBox().toString( up );
        up.literal( "]" );
    }
    public String toString()
    {
	return "new " + getBaseType() + "[" + getSize() + "]";
    }
    
}
