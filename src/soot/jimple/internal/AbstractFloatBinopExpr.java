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

@SuppressWarnings("serial")
public abstract class AbstractFloatBinopExpr extends AbstractBinopExpr
{
	public Type getType()
    {
        Value op1 = op1Box.getValue();
        Value op2 = op2Box.getValue();
		Type op1t = op1.getType();
		Type op2t = op2.getType();
        if((op1t.equals(IntType.v()) || 
            op1t.equals(ByteType.v()) ||
            op1t.equals(ShortType.v()) ||
            op1t.equals(CharType.v()) ||
            op1t.equals(BooleanType.v())) &&
           (op2t.equals(IntType.v()) ||
            op2t.equals(ByteType.v()) ||
            op2t.equals(ShortType.v()) ||
            op2t.equals(CharType.v()) ||
            op2t.equals(BooleanType.v())))
          return IntType.v();
        else if(op1t.equals(LongType.v()) || op2t.equals(LongType.v()))
          return LongType.v();
        else if(op1t.equals(DoubleType.v()) || op2t.equals(DoubleType.v()))
          return DoubleType.v();
        else if(op1t.equals(FloatType.v()) || op2t.equals(FloatType.v()))
          return FloatType.v();
        else
          return UnknownType.v();
    }
}
