package soot.dava.internal.javaRep;

import soot.*;
import soot.jimple.*;

public class DStaticFieldRef extends StaticFieldRef 
{
    private boolean supressDeclaringClass;

    public void toString( UnitPrinter up ) {
        if( !supressDeclaringClass ) {
            up.type( getField().getDeclaringClass().getType() );
            up.literal( "." );
        }
        up.fieldRef( getField() );
    }

    public String toBriefString()
    {
	if (supressDeclaringClass)
	    return getField().getName();
	else
	    return super.toBriefString();
    }

    public String toString()
    {
	return this.toBriefString();
    }

    public DStaticFieldRef( SootField field, String myClassName)
    {
	super( field);
	supressDeclaringClass = myClassName.equals( getField().getDeclaringClass().getFullName());
    }

    public DStaticFieldRef( SootField field, boolean supressDeclaringClass)
    {
	super( field);
	this.supressDeclaringClass = supressDeclaringClass;
    }

    public Object clone()
    {
	return new DStaticFieldRef( getField(), supressDeclaringClass);
    }
}
