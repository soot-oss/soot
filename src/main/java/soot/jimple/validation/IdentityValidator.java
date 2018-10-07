package soot.jimple.validation;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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
import soot.Unit;
import soot.jimple.IdentityStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

/**
 * This validator checks whether each ParameterRef and ThisRef is used exactly once.
 *
 * @author Marc Miltenberger
 */
public enum IdentityValidator implements BodyValidator {
  INSTANCE;

  public static IdentityValidator v() {
    return INSTANCE;
  }

  /**
   * Checks whether each ParameterRef and ThisRef is used exactly once.
   */
  @Override
  public void validate(Body body, List<ValidationException> exceptions) {
    boolean hasThisLocal = false;
    int paramCount = body.getMethod().getParameterCount();
    boolean[] parameterRefs = new boolean[paramCount];

    for (Unit u : body.getUnits()) {
      if (u instanceof IdentityStmt) {
        IdentityStmt id = (IdentityStmt) u;
        if (id.getRightOp() instanceof ThisRef) {
          hasThisLocal = true;
        }
        if (id.getRightOp() instanceof ParameterRef) {
          ParameterRef ref = (ParameterRef) id.getRightOp();
          if (ref.getIndex() < 0 || ref.getIndex() >= paramCount) {
            if (paramCount == 0) {
              exceptions
                  .add(new ValidationException(id, "This method has no parameters, so no parameter reference is allowed"));
            } else {
              exceptions.add(new ValidationException(id,
                  String.format("Parameter reference index must be between 0 and %d (inclusive)", paramCount - 1)));
            }
            return;
          }
          if (parameterRefs[ref.getIndex()]) {
            exceptions.add(
                new ValidationException(id, String.format("Only one local for parameter %d is allowed", ref.getIndex())));
          }
          parameterRefs[ref.getIndex()] = true;
        }
      }
    }

    if (!body.getMethod().isStatic() && !hasThisLocal) {
      exceptions.add(new ValidationException(body,
          String.format("The method %s is not static, but does not have a this local", body.getMethod().getSignature())));
    }

    for (int i = 0; i < paramCount; i++) {
      if (!parameterRefs[i]) {
        exceptions
            .add(new ValidationException(body, String.format("There is no parameter local for parameter number %d", i)));
      }
    }
  }

  @Override
  public boolean isBasicValidator() {
    return true;
  }
}
