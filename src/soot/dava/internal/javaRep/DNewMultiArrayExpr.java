package soot.dava.internal.javaRep;

import soot.*;
import java.util.*;
import soot.grimp.*;
import soot.jimple.*;
import soot.jimple.internal.*;

public class DNewMultiArrayExpr extends AbstractNewMultiArrayExpr
{
    public DNewMultiArrayExpr(ArrayType type, List sizes)
    {
        super(type, new ValueBox[sizes.size()]);

        for(int i = 0; i < sizes.size(); i++)
            sizeBoxes[i] = Grimp.v().newExprBox((Value) sizes.get(i));
    }
    
    public Object clone() 
    {
        List clonedSizes =  new ArrayList(getSizeCount());

        for(int i = 0; i <  getSizeCount(); i++) {
            clonedSizes.add(i,  Grimp.cloneIfNecessary(getSize(i)));
        }

        return new DNewMultiArrayExpr(getBaseType(), clonedSizes);
    }

    public void toString( UnitPrinter up )
    {
	up.literal( "new" );
        up.literal( " " );
        up.type( getBaseType().baseType );
        for( int i = 0; i < sizeBoxes.length; i++ ) {
            up.literal( "[" );
            sizeBoxes[i].toString( up );
            up.literal( "]" );
        }

	for (int i=getSizeCount(); i<getBaseType().numDimensions; i++)
	    up.literal( "[]");
    }
    public String toString()
    {
	StringBuffer buffer = new StringBuffer();

	buffer.append( "new " + getBaseType().baseType);
	List sizes = getSizes();
	Iterator it = getSizes().iterator();
	while (it.hasNext())
	    buffer.append( "[" + it.next().toString() + "]");

	for (int i=getSizeCount(); i<getBaseType().numDimensions; i++)
	    buffer.append( "[]");

	return buffer.toString();
    }

}
