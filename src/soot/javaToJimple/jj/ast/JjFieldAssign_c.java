package soot.javaToJimple.jj.ast;

import polyglot.ast.*;
import polyglot.visit.*;
import polyglot.ext.jl.ast.*;
import polyglot.types.*;
import polyglot.util.*;

public class JjFieldAssign_c extends FieldAssign_c {

    public JjFieldAssign_c(Position pos, Field left, Operator op, Expr right){
        super(pos, left, op, right);
    }
    
    public Type childExpectedType(Expr child, AscriptionVisitor av){
      if (op == SHL_ASSIGN || op == SHR_ASSIGN || op == USHR_ASSIGN) {
          return child.type();
      }
      
      if (child == right) {
          return left.type();
      }

      return child.type();
        

    }
}
