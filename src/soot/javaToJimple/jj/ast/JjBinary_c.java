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
            //if (other.type().isNumeric()) {
            if (other.type().isChar() || other.type().isByte() || other.type().isShort()) {
                //System.out.println("other type: "+other.type());
                return ts.Int();
            }
            else {
                //System.out.println("other type: "+other.type());
                //System.out.println("child type: "+other.type());
                return child.type();
            }
            //}
            //return ts.Long();
        }

        return child.type();


    }
}

