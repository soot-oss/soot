package soot.validation;

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

import soot.ArrayType;
import soot.Body;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.NullType;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

public enum CheckTypesValidator implements BodyValidator {
  INSTANCE;

  public static CheckTypesValidator v() {
    return INSTANCE;
  }

  @Override
  public void validate(Body body, List<ValidationException> exception) {
    final String methodSuffix = " in " + body.getMethod();
    for (Unit u : body.getUnits()) {
      String errorSuffix = " at " + u + methodSuffix;

      if (u instanceof DefinitionStmt) {
        DefinitionStmt astmt = (DefinitionStmt) u;
        if (!(astmt.getRightOp() instanceof CaughtExceptionRef)) {
          Type leftType = Type.toMachineType(astmt.getLeftOp().getType());
          Type rightType = Type.toMachineType(astmt.getRightOp().getType());

          checkCopy(astmt, exception, leftType, rightType, errorSuffix);
        }
      }

      if (u instanceof Stmt) {
        Stmt stmt = (Stmt) u;
        if (stmt.containsInvokeExpr()) {
          InvokeExpr iexpr = stmt.getInvokeExpr();
          SootMethodRef called = iexpr.getMethodRef();

          if (iexpr instanceof InstanceInvokeExpr) {
            InstanceInvokeExpr iiexpr = (InstanceInvokeExpr) iexpr;
            checkCopy(stmt, exception, called.getDeclaringClass().getType(), iiexpr.getBase().getType(),
                " in receiver of call" + errorSuffix);
          }

          final int argCount = iexpr.getArgCount();
          if (called.getParameterTypes().size() != argCount) {
            exception.add(new ValidationException(stmt, "Argument count does not match the signature of the called function",
                "Warning: Argument count doesn't match up with signature in call" + errorSuffix));
          } else {
            for (int i = 0; i < argCount; i++) {
              checkCopy(stmt, exception, Type.toMachineType(called.getParameterType(i)),
                  Type.toMachineType(iexpr.getArg(i).getType()),
                  " in argument " + i + " of call" + errorSuffix + " (Note: Parameters are zero-indexed)");
            }
          }
        }
      }
    }
  }

  private void checkCopy(Unit stmt, List<ValidationException> exception, Type leftType, Type rightType, String errorSuffix) {
    if (leftType instanceof PrimType || rightType instanceof PrimType) {
      if (leftType instanceof IntType && rightType instanceof IntType) {
        return;
      }
      if (leftType instanceof LongType && rightType instanceof LongType) {
        return;
      }
      if (leftType instanceof FloatType && rightType instanceof FloatType) {
        return;
      }
      if (leftType instanceof DoubleType && rightType instanceof DoubleType) {
        return;
      }
      exception.add(new ValidationException(stmt, "Warning: Bad use of primitive type" + errorSuffix));
      return;
    }

    if (rightType instanceof NullType) {
      return;
    }

    if (leftType instanceof RefType && "java.lang.Object".equals(((RefType) leftType).getClassName())) {
      return;
    }

    if (leftType instanceof ArrayType || rightType instanceof ArrayType) {
      if (leftType instanceof ArrayType && rightType instanceof ArrayType) {
        return;
      }
      // it is legal to assign arrays to variables of type Serializable, Cloneable or Object
      if (rightType instanceof ArrayType) {
        if (leftType.equals(RefType.v("java.io.Serializable")) || leftType.equals(RefType.v("java.lang.Cloneable"))
            || leftType.equals(RefType.v("java.lang.Object"))) {
          return;
        }
      }

      exception.add(new ValidationException(stmt, "Warning: Bad use of array type" + errorSuffix));
      return;
    }

    if (leftType instanceof RefType && rightType instanceof RefType) {
      SootClass leftClass = ((RefType) leftType).getSootClass();
      SootClass rightClass = ((RefType) rightType).getSootClass();
      if (leftClass.isPhantom() || rightClass.isPhantom()) {
        return;
      }

      if (leftClass.isInterface()) {
        if (rightClass.isInterface()) {
          if (!(leftClass.getName().equals(rightClass.getName())
              || Scene.v().getActiveHierarchy().isInterfaceSubinterfaceOf(rightClass, leftClass))) {
            exception.add(new ValidationException(stmt, "Warning: Bad use of interface type" + errorSuffix));
          }
        } else {
          // No quick way to check this for now.
        }
      } else if (rightClass.isInterface()) {
        exception.add(new ValidationException(stmt,
            "Warning: trying to use interface type where non-Object class expected" + errorSuffix));
      } else if (!Scene.v().getActiveHierarchy().isClassSubclassOfIncluding(rightClass, leftClass)) {
        exception.add(new ValidationException(stmt, "Warning: Bad use of class type" + errorSuffix));
      }
      return;
    }

    exception.add(new ValidationException(stmt, "Warning: Bad types" + errorSuffix));
  }

  @Override
  public boolean isBasicValidator() {
    return false;
  }
}
