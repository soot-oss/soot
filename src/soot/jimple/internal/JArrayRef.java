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

import soot.*;
import soot.tagkit.*;
import soot.jimple.*;
import soot.baf.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

public class JArrayRef implements ArrayRef, ConvertToBaf
{
    protected ValueBox baseBox;
    protected ValueBox indexBox;

    public JArrayRef(Value base, Value index)
    {
        this(Jimple.v().newLocalBox(base),
             Jimple.v().newImmediateBox(index));
    }

    protected JArrayRef(ValueBox baseBox, ValueBox indexBox)
    {
        this.baseBox = baseBox;
        this.indexBox = indexBox;
    }
    
    public Object clone() 
    {
        return new JArrayRef(Jimple.cloneIfNecessary(getBase()), Jimple.cloneIfNecessary(getIndex()));
    }

    public boolean equivTo(Object o)
    {
        if (o instanceof ArrayRef)
          {
            return (getBase().equivTo(((ArrayRef)o).getBase())
                    && getIndex().equivTo(((ArrayRef)o).getIndex()));
          }
        return false;
    }

    /** Returns a hash code for this object, consistent with structural equality. */
    public int equivHashCode() 
    {
        return getBase().equivHashCode() * 101 + getIndex().equivHashCode() + 17;
    }

    public String toString()
    {
        return baseBox.getValue().toString() + "[" + indexBox.getValue().toString() + "]";
    }
    
    public void toString(UnitPrinter up) {
        baseBox.toString(up);
        up.literal("[");
        indexBox.toString(up);
        up.literal("]");
    }

    public Value getBase()
    {
        return baseBox.getValue();
    }

    public void setBase(Local base)
    {
        baseBox.setValue(base);
    }

    public ValueBox getBaseBox()
    {
        return baseBox;
    }

    public Value getIndex()
    {
        return indexBox.getValue();
    }

    public void setIndex(Value index)
    {
        indexBox.setValue(index);
    }

    public ValueBox getIndexBox()
    {
        return indexBox;
    }

    public List getUseBoxes()
    {
        List useBoxes = new ArrayList();

        useBoxes.addAll(baseBox.getValue().getUseBoxes());
        useBoxes.add(baseBox);

        useBoxes.addAll(indexBox.getValue().getUseBoxes());
        useBoxes.add(indexBox);

        return useBoxes;
    }

    public Type getType()
    {
        Value base = (Value) baseBox.getValue();
        Type type = base.getType();

        if(type.equals(UnknownType.v()))
            return UnknownType.v();
        else if(type.equals(NullType.v()))
            return NullType.v();
        else {
            ArrayType arrayType = (ArrayType) type;

            if(arrayType.numDimensions == 1)
                return arrayType.baseType;
            else
                return ArrayType.v(arrayType.baseType, arrayType.numDimensions - 1);
        }
    }

    public void apply(Switch sw)
    {
        ((RefSwitch) sw).caseArrayRef(this);
    }

    public void convertToBaf(JimpleToBafContext context, List out)
    {
        ((ConvertToBaf)getBase()).convertToBaf(context, out);
        ((ConvertToBaf)getIndex()).convertToBaf(context, out);
	
	Unit currentUnit = context.getCurrentUnit();

	Unit x;

        out.add(x = Baf.v().newArrayReadInst(getType()));

	Iterator it = currentUnit.getTags().iterator();
	while(it.hasNext()) {
	    x.addTag((Tag) it.next());
	}

    }
}



