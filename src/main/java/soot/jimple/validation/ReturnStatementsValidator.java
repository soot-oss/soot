package soot.jimple.validation;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.List;

import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.VoidType;
import soot.baf.GotoInst;
import soot.baf.ReturnInst;
import soot.baf.ReturnVoidInst;
import soot.baf.ThrowInst;
import soot.jimple.GotoStmt;
import soot.jimple.NopStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.ThrowStmt;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

/**
 * Checks that this Body actually contains a throw or return statement, and that the return statement is of the appropriate
 * type (i.e. void/non-void).
 */
public enum ReturnStatementsValidator implements BodyValidator {
  INSTANCE;

  public static ReturnStatementsValidator v() {
    return INSTANCE;
  }

  @Override
  public void validate(Body body, List<ValidationException> exceptions) {
    final SootMethod method = body.getMethod();

    // Checks that this Body actually contains a throw or return statement, and
    // that the return statement is of the appropriate type (i.e. void/non-void)
    for (Unit u : body.getUnits()) {
      if (u instanceof ThrowStmt || u instanceof ThrowInst) {
        return;
      } else if (u instanceof ReturnStmt || u instanceof ReturnInst) {
        if (!(method.getReturnType() instanceof VoidType)) {
          return;
        }
      } else if (u instanceof ReturnVoidStmt || u instanceof ReturnVoidInst) {
        if (method.getReturnType() instanceof VoidType) {
          return;
        }
      }
    }

    // A method can have an infinite loop and no return statement:
    // public static void main(String[] args) {
    // int i = 0; while (true) {i += 1;}
    // }
    //
    // Only check that the execution cannot fall off the code.
    Unit last = body.getUnits().getLast();
    while (last instanceof NopStmt) {
      // this can be fine since we can have this as a trap end statement
      last = body.getUnits().getPredOf(last);
    }
    if (last instanceof GotoStmt || last instanceof GotoInst || last instanceof ThrowStmt || last instanceof ThrowInst) {
      return;
    }

    exceptions.add(new ValidationException(method,
        "The method does not contain a return statement, or the return statement is not of the appropriate type",
        "Body of method " + method.getSignature()
            + " does not contain a return statement, or the return statement is not of the appropriate type"));
  }

  @Override
  public boolean isBasicValidator() {
    return true;
  }
}
