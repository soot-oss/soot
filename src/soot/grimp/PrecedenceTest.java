/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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






package soot.grimp;
import soot.*;
import soot.jimple.*;

public class PrecedenceTest
{
    public static boolean needsBrackets( ValueBox subExprBox, Value expr ) {
        Value sub = subExprBox.getValue();
        if( !(sub instanceof Precedence) ) return false;
        Precedence subP = (Precedence) sub;
        Precedence exprP = (Precedence) expr;
        return subP.getPrecedence() < exprP.getPrecedence();
    }
    public static boolean needsBracketsRight( ValueBox subExprBox, Value expr ) {
        Value sub = subExprBox.getValue();
        if( !(sub instanceof Precedence) ) return false;
        Precedence subP = (Precedence) sub;
        Precedence exprP = (Precedence) expr;
        if( subP.getPrecedence() < exprP.getPrecedence() ) return true;
        if( subP.getPrecedence() == exprP.getPrecedence() ) {
            if( subP instanceof SubExpr ) return true;
            if( subP instanceof DivExpr ) return true;
            if( subP instanceof CmpExpr ) return true;
            if( subP instanceof CmpgExpr ) return true;
            if( subP instanceof CmplExpr ) return true;
        }
        return false;
    }
}
