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
                if (child.type().isByte() || child.type().isShort() || child.type().isChar()) {
                    return ts.Int();
                }
                return child.type();
            }
            else if (op == NEG || op == POS) {
                if (child.type().isByte() || child.type().isShort() || child.type().isChar()) {
                    return ts.Int();
                }
                return child.type();
            }
            else if (op == BIT_NOT) {
                if (child.type().isByte() || child.type().isShort() || child.type().isChar()) {
                    return ts.Int();
                }
                return child.type();
            }
            else if (op == NOT) {
                return ts.Boolean();
            }
        }

        return child.type();
        

    }
}
