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
import soot.FastHierarchy;
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
import soot.dotnet.types.DotNetBasicTypes;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.options.Options;

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
      if ((leftType instanceof IntType && rightType instanceof IntType)
          || (leftType instanceof LongType && rightType instanceof LongType)) {
        return;
      }
      if (leftType instanceof FloatType && rightType instanceof FloatType) {
        return;
      }
      if (leftType instanceof DoubleType && rightType instanceof DoubleType) {
        return;
      }

      if (Options.v().src_prec() == Options.src_prec_dotnet) {

        // if left/right type type of System.ValueType == primtype, is ok

        if ((leftType instanceof RefType && ((RefType) leftType).getClassName().equals(DotNetBasicTypes.SYSTEM_INTPTR))
            || (rightType instanceof RefType
                && ((RefType) rightType).getClassName().equals(DotNetBasicTypes.SYSTEM_INTPTR))) {
          return;
        }
        if (leftType instanceof RefType) {
          FastHierarchy fastHierarchy = Scene.v().getFastHierarchy();
          if (fastHierarchy.canStoreClass(((RefType) leftType).getSootClass(),
              Scene.v().getSootClass(DotNetBasicTypes.SYSTEM_VALUETYPE))) {
            return;
          }

          // if lefttype is base class, all right types are legal
          if (((RefType) leftType).getSootClass().getName().equals(DotNetBasicTypes.SYSTEM_OBJECT)) {
            return;
          }

          // if righttype is primtype - primitive structs inherits from ValueType and
          // implements IComparable, IComparable<T>, IConvertible, IEquatable<T>, IFormattable
          if (leftType.equals(RefType.v(DotNetBasicTypes.SYSTEM_ICOMPARABLE))
              || leftType.equals(RefType.v(DotNetBasicTypes.SYSTEM_ICOMPARABLE_1))
              || leftType.equals(RefType.v(DotNetBasicTypes.SYSTEM_ICONVERTIBLE))
              || leftType.equals(RefType.v(DotNetBasicTypes.SYSTEM_IEQUATABLE_1))
              || leftType.equals(RefType.v(DotNetBasicTypes.SYSTEM_IFORMATTABLE))) {
            return;
          }

        }
        if (rightType instanceof RefType) {
          FastHierarchy fastHierarchy = Scene.v().getFastHierarchy();
          if (fastHierarchy.canStoreClass(((RefType) rightType).getSootClass(),
              Scene.v().getSootClass(DotNetBasicTypes.SYSTEM_VALUETYPE))) {
            return;
          }
        }
      }
      exception.add(new ValidationException(stmt, "Warning: Bad use of primitive type" + errorSuffix + " - LeftType is "
          + leftType.getClass().getName() + " and RightType is " + rightType.getClass().getName()));
      return;
    }

    if ((rightType instanceof NullType) || (leftType instanceof RefType
        && Scene.v().getObjectType().toString().equals(((RefType) leftType).getClassName()))) {
      return;
    }

    if (leftType instanceof ArrayType || rightType instanceof ArrayType) {
      if (leftType instanceof ArrayType && rightType instanceof ArrayType) {
        return;
      }
      // it is legal to assign arrays to variables of type Serializable, Cloneable or Object
      if (rightType instanceof ArrayType) {
        // Dotnet: it is legal to assign arrays to System.Array, because it is base class in CLR
        if (leftType.equals(RefType.v("java.io.Serializable")) || leftType.equals(RefType.v("java.lang.Cloneable"))
            || leftType.equals(Scene.v().getObjectType()) || leftType.equals(RefType.v(DotNetBasicTypes.SYSTEM_ARRAY))) {
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
      } else if (Options.v().src_prec() == Options.src_prec_dotnet) {
        // if dotnet check for ValueTypes, assignment can only be correct from compiler
        FastHierarchy fastHierarchy = Scene.v().getFastHierarchy();
        boolean lTypeIsChild = fastHierarchy.canStoreClass(((RefType) leftType).getSootClass(),
            Scene.v().getSootClass(DotNetBasicTypes.SYSTEM_VALUETYPE));
        boolean rTypeIsChild = fastHierarchy.canStoreClass(((RefType) rightType).getSootClass(),
            Scene.v().getSootClass(DotNetBasicTypes.SYSTEM_VALUETYPE));
        if (lTypeIsChild && rTypeIsChild) {
          return;
        }
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