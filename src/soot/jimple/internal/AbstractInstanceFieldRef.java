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

public abstract class AbstractInstanceFieldRef implements InstanceFieldRef, ConvertToBaf, EquivTo
{
    SootField field;
    ValueBox baseBox;

    protected AbstractInstanceFieldRef(ValueBox baseBox, SootField field)
    {
        this.baseBox = baseBox;
        this.field = field;
    }

    public abstract Object clone();

    public String toString()
    {
        return baseBox.getValue().toString() + "." + field.getJimpleStyleSignature();
    }

    public String toBriefString()
    {
        return ((ToBriefString) baseBox.getValue()).toBriefString() + "." + field.getName() + "";
    }
    
    public Value getBase()
    {
        return baseBox.getValue();
    }

    public ValueBox getBaseBox()
    {
        return baseBox;
    }

    public void setBase(Value base)
    {
        baseBox.setValue(base);
    }

    public SootField getField()
    {
        return field;
    }

    public void setField(SootField field)
    {
        this.field = field;
    }

    public List getUseBoxes()
    {
        List useBoxes = new ArrayList();

        useBoxes.addAll(baseBox.getValue().getUseBoxes());
        useBoxes.add(baseBox);

        return useBoxes;
    }

    public Type getType()
    {
        return field.getType();
    }

    public void apply(Switch sw)
    {
        ((RefSwitch) sw).caseInstanceFieldRef(this);
    }
    
    public boolean equivTo(Object o)
    {
        if (o instanceof AbstractInstanceFieldRef)
        {
            AbstractInstanceFieldRef fr = (AbstractInstanceFieldRef)o;
            return fr.field.equivTo(field) &&
                fr.baseBox.getValue().equivTo(baseBox.getValue());
        }
        return false;
    }

    /** Returns a hash code for this object, consistent with structural equality. */
    public int equivHashCode() 
    {
        return field.equivHashCode() * 101 + baseBox.getValue().equivHashCode() + 17;
    }

    public void convertToBaf(JimpleToBafContext context, List out)
    {
        ((ConvertToBaf)getBase()).convertToBaf(context, out);
        out.add(Baf.v().newFieldGetInst(field));
    }
}
