package soot.javaToJimple.jj.ast;

import polyglot.ast.*;
import polyglot.visit.*;
import polyglot.ext.jl.ast.*;
import polyglot.types.*;
import polyglot.util.*;

public class JjLocalDecl_c extends LocalDecl_c {

    public JjLocalDecl_c(Position pos, Flags flags, TypeNode type, String name, Expr init){
        super(pos, flags, type, name, init);
    }
    
    public Type childExpectedType(Expr child, AscriptionVisitor av){
      
        return type().type();
    }
}
