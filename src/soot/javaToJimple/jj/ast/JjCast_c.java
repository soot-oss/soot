package soot.javaToJimple.jj.ast;

import polyglot.ast.*;
import polyglot.visit.*;
import polyglot.ext.jl.ast.*;
import polyglot.types.*;
import polyglot.util.*;

public class JjCast_c extends Cast_c {

    public JjCast_c(Position pos, TypeNode castType, Expr expr){
        super(pos, castType, expr);
    }
    
    public Type childExpectedType(Expr child, AscriptionVisitor av){
        TypeSystem ts = av.typeSystem();

        if (child == expr) {
            if (castType.type().isReference()) {
                return ts.Object();
            }
            else if (castType.type().isNumeric()) {
                return castType.type();
                //return ts.Double();
            }
            else if (castType.type().isBoolean()) {
                return ts.Boolean();
            }
        }

        return child.type();
        

    }
}
