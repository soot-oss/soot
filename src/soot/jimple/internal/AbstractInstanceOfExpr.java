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
import soot.jimple.*;
import soot.util.*;
import java.util.*;

public abstract class AbstractInstanceOfExpr implements InstanceOfExpr
{
    ValueBox opBox;
    Type checkType;

    protected AbstractInstanceOfExpr(ValueBox opBox, Type checkType)
    {
        this.opBox = opBox; this.checkType = checkType;
    }
    
    public boolean equivTo(Object o)
    {
        if (o instanceof AbstractInstanceOfExpr)
        {
            AbstractInstanceOfExpr aie = (AbstractInstanceOfExpr)o;
            return opBox.getValue().equivTo(aie.opBox.getValue()) &&
                checkType.equals(aie.checkType);
        }
        return false;
    }

    /** Returns a hash code for this object, consistent with structural equality. */
    public int equivHashCode() 
    {
        return opBox.getValue().equivHashCode() * 101 + checkType.hashCode() * 17;
    }

    public abstract Object clone();
    
    public String toString()
    {
        return opBox.getValue().toString() + " " + Jimple.v().INSTANCEOF + " " + checkType.toString();
    }
    
    public void toString( UnitPrinter up ) {
        opBox.toString(up);
        up.literal(" ");
        up.literal(Jimple.v().INSTANCEOF);
        up.literal(" ");
        up.type(checkType);
    }

    public Value getOp()
    {
        return opBox.getValue();
    }

    public void setOp(Value op)
    {
        opBox.setValue(op);
    }

    public ValueBox getOpBox()
    {
        return opBox;
    }

    public List getUseBoxes()
    {
        List list = new ArrayList();

        list.addAll(opBox.getValue().getUseBoxes());
        list.add(opBox);

        return list;
    }

    public Type getType()
    {
        return BooleanType.v();
    }

    public Type getCheckType()
    {
        return checkType;
    }

    public void setCheckType(Type checkType)
    {
        this.checkType = checkType;
    }

    public void apply(Switch sw)
    {
        ((ExprSwitch) sw).caseInstanceOfExpr(this);
    }
}
