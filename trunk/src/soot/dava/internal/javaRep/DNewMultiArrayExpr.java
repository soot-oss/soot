/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.dava.internal.javaRep;

import soot.*;

import java.util.*;
import soot.grimp.*;
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
        for (ValueBox element : sizeBoxes) {
            up.literal( "[" );
            element.toString( up );
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
