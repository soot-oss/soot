package soot.javaToJimple.jj.ast;

import polyglot.ast.*;
import polyglot.visit.*;
import polyglot.ext.jl.ast.*;
import polyglot.types.*;
import polyglot.util.*;

public class JjLocalAssign_c extends LocalAssign_c {

    public JjLocalAssign_c(Position pos, Local left, Operator op, Expr right){
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
