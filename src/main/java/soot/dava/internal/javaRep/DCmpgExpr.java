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
import soot.util.*;
import soot.grimp.*;
import soot.jimple.*;
import soot.grimp.internal.*;

public class DCmpgExpr extends AbstractGrimpIntBinopExpr implements CmpgExpr
{
    public DCmpgExpr(Value op1, Value op2) { super(op1, op2); }
    public final String getSymbol() { return " - "; }
    public final int getPrecedence() { return 700; }
    public void apply(Switch sw) { ((ExprSwitch) sw).caseCmpgExpr(this); }

    public Object clone() 
    {
        return new DCmpgExpr(Grimp.cloneIfNecessary(getOp1()), Grimp.cloneIfNecessary(getOp2()));
    }
    
    public Type getType(){
    	if (getOp1().getType().equals(getOp2().getType())){
    		return getOp1().getType();
    	}
    		
        return IntType.v();
    }
}
