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

abstract public class AbstractIntLongBinopExpr extends AbstractBinopExpr
{
    public Type getType()
    {
        Value op1 = op1Box.getValue();
        Value op2 = op1Box.getValue();

        if((op1.getType().equals(IntType.v()) || 
            op1.getType().equals(ByteType.v()) ||
            op1.getType().equals(ShortType.v()) ||
            op1.getType().equals(CharType.v()) ||
            op1.getType().equals(BooleanType.v())) &&
           (op2.getType().equals(IntType.v()) ||
            op2.getType().equals(ByteType.v()) ||
            op2.getType().equals(ShortType.v()) ||
            op2.getType().equals(CharType.v()) ||
            op2.getType().equals(BooleanType.v())))
          return IntType.v();
        else if(op1.getType().equals(LongType.v()) && op2.getType().equals(LongType.v()))
            return LongType.v();
        else
            return UnknownType.v();
    }
}
