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






package soot.grimp.internal;

import soot.*;
import soot.grimp.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.util.*;
import java.util.*;

public class GCastExpr extends AbstractCastExpr implements Precedence
{
    public GCastExpr(Value op, Type type)
    {
        super(Grimp.v().newExprBox(op), type);
    }

    public int getPrecedence() { return 850; }

    private String toString(String leftString, Value op, String opString)
    {
        String rightOp = opString;

        if (op instanceof Precedence && 
            ((Precedence)op).getPrecedence() < getPrecedence()) 
            rightOp = "(" + rightOp + ")";
        return leftString + rightOp;
    }

    public String toString()
    {
        return toString("(" + getCastType().toString() + ") ", getOp(),
                        getOp().toString());
    }

    public Object clone()
    {
        return new GCastExpr(Grimp.cloneIfNecessary(getOp()), getCastType());
    }

}
