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
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;

public class JjBinary_c extends Binary_c {

    public JjBinary_c(Position pos, Expr left, Binary.Operator op, Expr right){
        super(pos, left, op, right);
    }

    public Type childExpectedType(Expr child, AscriptionVisitor av){
        Expr other;

        //System.out.println("child: "+child+" op: "+op);
        if (child == left) {
            other = right;
        }
        else if (child == right) {
            other = left;
        }
        else {
            return child.type();
        }

        TypeSystem ts = av.typeSystem();

	if (op == EQ || op == NE) {
            // Coercion to compatible types.
            if (other.type().isReference() || other.type().isNull()) {
                return ts.Object();
            }

            if (other.type().isBoolean()) {
                return ts.Boolean();
            }

            if (other.type().isNumeric()) {
                if (other.type().isDouble() || child.type().isDouble()) {
                    return ts.Double();
                }
                else if (other.type().isFloat() || child.type().isFloat()) {
                    return ts.Float();
                }
                else if (other.type().isLong() || child.type().isLong()) {
                    return ts.Long();
                }
                else {
                    return ts.Int();
                }
            }
        }

        if (op == ADD && ts.equals(type, ts.String())) {
            // Implicit coercion to String. 
            return ts.String();
        }

        if (op == GT || op == LT || op == GE || op == LE) {
            if (other.type().isBoolean()) {
                return ts.Boolean();
            }
            if (other.type().isNumeric()) {
                if (other.type().isDouble() || child.type().isDouble()) {
                    return ts.Double();
                }
                else if (other.type().isFloat() || child.type().isFloat()) {
                    return ts.Float();
                }
                else if (other.type().isLong() || child.type().isLong()) {
                    return ts.Long();
                }
                else {
                    return ts.Int();
                }
            }
        }

        if (op == COND_OR || op == COND_AND) {
            return ts.Boolean();
        }

	    if (op == BIT_AND || op == BIT_OR || op == BIT_XOR) {
            if (other.type().isBoolean()) {
                return ts.Boolean();
            }
            if (other.type().isNumeric()) {
                if (other.type().isLong() || child.type().isLong()) {
                    return ts.Long();
                }
                else {
                    return ts.Int();
                }
            }
        }

        if (op == ADD || op == SUB || op == MUL || op == DIV || op == MOD) {
            //System.out.println("other: "+other+" type: "+other.type());
            //System.out.println("child: "+child+" child: "+child.type());
            
            if (other.type().isNumeric()) {
                if (other.type().isDouble() || child.type().isDouble()) {
                    return ts.Double();
                }
                else if (other.type().isFloat() || child.type().isFloat()) {
                    return ts.Float();
                }
                else if (other.type().isLong() || child.type().isLong()) {
                    return ts.Long();
                }
                else {
                    return ts.Int();
                }
            }
        }
        
        if (op == SHL || op == SHR || op == USHR) {
            if (child == right || !child.type().isLong()) {
                return ts.Int();
            } else {
                return child.type();
            }
        }

        return child.type();


    }

    /*public Node foldConstants(ConstantFolder cf) {
        if (left instanceof Binary || left instanceof Field){
            left = left.del.foldConstants(cf);
        }
        if (right instanceof Binary || right instanceof Field){
            right = right.del.foldConstants(cf);
        }
        if (left instanceof StringLit && right instanceof StringLit) {
            String l = ((StringLit) left).value();
            String r = ((StringLit) right).value();
            if (op == ADD) return nf.StringLit(position(), l + r);
        }
        else if (left instanceof 
    }*/
}

