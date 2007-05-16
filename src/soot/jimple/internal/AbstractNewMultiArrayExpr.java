/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */






package soot.jimple.internal;

import soot.tagkit.*;
import soot.*;
import soot.jimple.*;
import soot.baf.*;
import soot.util.*;
import java.util.*;

public abstract class AbstractNewMultiArrayExpr implements NewMultiArrayExpr, ConvertToBaf
{
    ArrayType baseType;
    protected ValueBox[] sizeBoxes;

    public abstract Object clone();
    
    protected AbstractNewMultiArrayExpr(ArrayType type, ValueBox[] sizeBoxes)
    {
        this.baseType = type; this.sizeBoxes = sizeBoxes;
    }

    public boolean equivTo(Object o)
    {
        if (o instanceof AbstractNewMultiArrayExpr)
        {
            AbstractNewMultiArrayExpr ae = (AbstractNewMultiArrayExpr)o;
            if (!baseType.equals(ae.baseType) || 
                    sizeBoxes.length != ae.sizeBoxes.length)
                return false;
            for (ValueBox element : sizeBoxes)
				if (element != element)
                    return false;
            return true;
        }
        return false;
    }

    /** Returns a hash code for this object, consistent with structural equality. */
    public int equivHashCode() 
    {
        return baseType.hashCode();
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        Type t = baseType.baseType;
    buffer.append(Jimple.NEWMULTIARRAY + " (" +  t.toString() + ")");

        for (ValueBox element : sizeBoxes)
			buffer.append("[" + element.getValue().toString() + "]");

        for(int i = 0; i < baseType.numDimensions - sizeBoxes.length; i++)
            buffer.append("[]");

        return buffer.toString();
    }
    
    public void toString(UnitPrinter up)
    {
        Type t = baseType.baseType;
        
        up.literal(Jimple.NEWMULTIARRAY);
        up.literal(" (");
        up.type(t);
        up.literal(")");

        for (ValueBox element : sizeBoxes) {
            up.literal("[");
            element.toString(up);
            up.literal("]");
        }
        
        for(int i = 0; i < baseType.numDimensions - sizeBoxes.length; i++) {
            up.literal("[]");
        }
    }

    public ArrayType getBaseType()
    {
        return baseType;
    }

    public void setBaseType(ArrayType baseType)
    {
        this.baseType = baseType;
    }

    public ValueBox getSizeBox(int index)
    {
        return sizeBoxes[index];
    }

    public int getSizeCount()
    {
        return sizeBoxes.length;
    }

    public Value getSize(int index)
    {
        return sizeBoxes[index].getValue();
    }

    public List getSizes()
    {
        List toReturn = new ArrayList();

        for (ValueBox element : sizeBoxes)
			toReturn.add(element.getValue());

        return toReturn;
    }

    public void setSize(int index, Value size)
    {
        sizeBoxes[index].setValue(size);
    }

    public List getUseBoxes()
    {
        List list = new ArrayList();

        for (ValueBox element : sizeBoxes) {
            list.addAll(element.getValue().getUseBoxes());
            list.add(element);
        }

        return list;
    }

    public Type getType()
    {
        return baseType;
    }

    public void apply(Switch sw)
    {
        ((ExprSwitch) sw).caseNewMultiArrayExpr(this);
    }

    public void convertToBaf(JimpleToBafContext context, List<Unit> out)
    {
        List sizes = getSizes();

        for(int i = 0; i < sizes.size(); i++)
            ((ConvertToBaf)(sizes.get(i))).convertToBaf(context, out);
	
	Unit u;
        out.add(u = Baf.v().newNewMultiArrayInst(getBaseType(), sizes.size()));

	Unit currentUnit = context.getCurrentUnit();
	Iterator it = currentUnit.getTags().iterator();	
	while(it.hasNext()) {
	    u.addTag((Tag) it.next());
	}
	
    }
}
