/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
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
import soot.jimple.*;
import soot.baf.*;
import soot.jimple.*;
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

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

	Type t = baseType.baseType;
	if(Jimple.isJavaKeywordType(t))
	  buffer.append(Jimple.v().NEWMULTIARRAY + " (" + "." + t.toString() + ")");
	else
	  buffer.append(Jimple.v().NEWMULTIARRAY + " (" +  t.toString() + ")");

        for(int i = 0; i < sizeBoxes.length; i++)
            buffer.append("[" + sizeBoxes[i].getValue().toString() + "]");

        for(int i = 0; i < baseType.numDimensions - sizeBoxes.length; i++)
            buffer.append("[]");

        return buffer.toString();
    }

    public String toBriefString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append(Jimple.v().NEWMULTIARRAY + " (" + baseType.baseType.toBriefString() + ")");

        for(int i = 0; i < sizeBoxes.length; i++)
            buffer.append("[" + ((ToBriefString) sizeBoxes[i].getValue()).toBriefString() + "]");

        for(int i = 0; i < baseType.numDimensions - sizeBoxes.length; i++)
            buffer.append("[]");

        return buffer.toString();
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

        for(int i = 0; i < sizeBoxes.length; i++)
            toReturn.add(sizeBoxes[i].getValue());

        return toReturn;
    }

    public void setSize(int index, Value size)
    {
        sizeBoxes[index].setValue(size);
    }

    public List getUseBoxes()
    {
        List list = new ArrayList();

        for(int i = 0; i < sizeBoxes.length; i++)
        {
            list.addAll(sizeBoxes[i].getValue().getUseBoxes());
            list.add(sizeBoxes[i]);
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

    public void convertToBaf(JimpleToBafContext context, List out)
    {
        List sizes = getSizes();

        for(int i = 0; i < sizes.size(); i++)
            ((ConvertToBaf)(sizes.get(i))).convertToBaf(context, out);

        out.add(Baf.v().newNewMultiArrayInst(getBaseType(), sizes.size()));
    }
}
