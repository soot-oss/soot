/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

package soot.javaToJimple.jj.ast;

import polyglot.ast.*;
import polyglot.visit.*;
import polyglot.ext.jl.ast.*;
import polyglot.types.*;
import polyglot.util.*;

public class JjUnary_c extends Unary_c {

    public JjUnary_c(Position pos, Unary.Operator op, Expr expr){
        super(pos, op, expr);
    }
    
    public Type childExpectedType(Expr child, AscriptionVisitor av){
        TypeSystem ts = av.typeSystem();

        if (child == expr) {
            if (op == POST_INC || op == POST_DEC ||
                op == PRE_INC || op == PRE_DEC) {
                //if (child.type().isByte() || child.type().isShort()){// || child.type().isChar()) {
                //    return ts.Int();
                //}
                return child.type();
            }
            else if (op == NEG || op == POS) {
                //if (child.type().isByte() || child.type().isShort() || child.type().isChar()) {
                //    return ts.Int();
                //}
                return child.type();
            }
            else if (op == BIT_NOT) {
                //if (child.type().isByte() || child.type().isShort() || child.type().isChar()) {
                //    return ts.Int();
                //}
                return child.type();
            }
            else if (op == NOT) {
                return ts.Boolean();
            }
        }

        return child.type();
        

    }
}
