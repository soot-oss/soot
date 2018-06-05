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
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

public enum InvokeValidator implements BodyValidator {
  INSTANCE;

  public static InvokeValidator v() {
    return INSTANCE;
  }

  @Override
  public void validate(Body body, List<ValidationException> exceptions) {
    if (true) {
      return;
    }
    SootClass bodyDeclaredClass = body.getMethod().getDeclaringClass();
    for (Unit unit : body.getUnits()) {
      if (unit instanceof Stmt) {
        Stmt statement = (Stmt) unit;
        if (statement.containsInvokeExpr()) {
          InvokeExpr invokeExpr = statement.getInvokeExpr();
          SootMethodRef referencedMethod = invokeExpr.getMethodRef();
          boolean shouldBeVirtual = true;

          if (referencedMethod.isStatic()) {
            shouldBeVirtual = false;
            if (!(invokeExpr instanceof StaticInvokeExpr)) {
              exceptions.add(new ValidationException(unit, "staticinvoke should be used."));
            }
          }

          try {
            SootMethod method = referencedMethod.resolve();
            SootClass clazzDeclaring = method.getDeclaringClass();
            boolean superClassMethod = false;
            SootClass clazzSearch = bodyDeclaredClass;
            while (clazzSearch.hasSuperclass()) {
              clazzSearch = clazzSearch.getSuperclass();
              // specialinvoke is also used at methods of superclasses.
              if (clazzSearch.getName().equals(clazzDeclaring.getName())) {
                superClassMethod = true;
                break;
              }
            }

            if (clazzDeclaring.isInterface()) {
              shouldBeVirtual = false;
              if (!(invokeExpr instanceof InterfaceInvokeExpr)) {
                exceptions
                    .add(new ValidationException(unit, "Invokes a interface method. Should be interfaceinvoke instead."));
              }
            }
            if (method.isEntryMethod()) {
              shouldBeVirtual = false;
              exceptions.add(new ValidationException(unit, "Call to <clinit> methods not allowed."));
            }

            if (method.isPrivate() || method.isConstructor() || superClassMethod) {
              shouldBeVirtual = false;
              if (!(invokeExpr instanceof SpecialInvokeExpr)) {
                exceptions.add(new ValidationException(unit,
                    "specialinvoke should be used on private or constructor methods. Should be specialinvoke instead."));
              }
            }
            if (shouldBeVirtual) {
              if (!(invokeExpr instanceof VirtualInvokeExpr)) {
                exceptions.add(new ValidationException(unit, "virtualinvoke should be used."));
              }
            }
          } catch (Exception e) {
            // Error on resolving
          }
        }
      }
    }
  }

  @Override
  public boolean isBasicValidator() {
    return false;
  }

}
