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
import soot.baf.*;
import soot.util.*;

public class JUshrExpr extends AbstractJimpleIntLongBinopExpr implements UshrExpr
{
    public JUshrExpr(Value op1, Value op2) { super(op1, op2); }
    public final String getSymbol() { return " >>> "; }
    public void apply(Switch sw) { ((ExprSwitch) sw).caseUshrExpr(this); }

    Object makeBafInst(Type opType) { return Baf.v().newUshrInst(this.getOp1().getType()); }

    public Type getType()
    {
        Value op1 = op1Box.getValue();
        Value op2 = op2Box.getValue();
        
        if (!isIntLikeType(op2.getType()))
        	return UnknownType.v();
        
        if (isIntLikeType(op1.getType()))
        	return IntType.v();
        if (op1.getType().equals(LongType.v()))
        	return LongType.v();
        
    	return UnknownType.v();
    }
    
    public Object clone() 
    {
        return new JUshrExpr(Jimple.cloneIfNecessary(getOp1()), Jimple.cloneIfNecessary(getOp2()));
    }


}
