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

public abstract class AbstractNegExpr extends AbstractUnopExpr implements NegExpr
{
    protected AbstractNegExpr(ValueBox opBox) { super(opBox); }

    /** Compares the specified object with this one for structural equality. */
    public boolean equivTo(Object o)
    {
        if (o instanceof AbstractNegExpr)
        {
            return opBox.getValue().equivTo(((AbstractNegExpr)o).opBox.getValue());
        }
        return false;
    }

    /** Returns a hash code for this object, consistent with structural equality. */
    public int equivHashCode() 
    {
        return opBox.getValue().equivHashCode();
    }

    public abstract Object clone();

    public String toString()
    {
        return Jimple.NEG + " " + opBox.getValue().toString();
    }
    
    public void toString(UnitPrinter up) {
        up.literal(Jimple.NEG);
        up.literal(" ");
        opBox.toString(up);
    }

    public Type getType()
    {
        Value op = opBox.getValue();

        if(op.getType().equals(IntType.v()) || op.getType().equals(ByteType.v()) ||
            op.getType().equals(ShortType.v()) || op.getType().equals(BooleanType.v()) || 
            op.getType().equals(CharType.v()))
            return IntType.v();
        else if(op.getType().equals(LongType.v()))
            return LongType.v();
        else if(op.getType().equals(DoubleType.v()))
            return DoubleType.v();
        else if(op.getType().equals(FloatType.v()))
            return FloatType.v();
        else
            return UnknownType.v();
    }

    public void apply(Switch sw)
    {
        ((ExprSwitch) sw).caseNegExpr(this);
    }
}
