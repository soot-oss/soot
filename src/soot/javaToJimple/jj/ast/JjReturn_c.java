package soot.javaToJimple.jj.ast;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.ext.jl.ast.*;
import polyglot.util.*;

public class JjReturn_c extends Return_c {

    public JjReturn_c(Position pos, Expr expr){
        super(pos, expr);
    }

    public Type childExpectedType(Expr child, AscriptionVisitor av){
        if (child == expr) {
            Context c = av.context();
            CodeInstance ci = c.currentCode();
                            
            if (ci instanceof MethodInstance) {
                MethodInstance mi = (MethodInstance) ci;
                return mi.returnType();                                                     }
               
        }
        return child.type();

    }
}

