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
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

/**
 * A basic validator that checks whether the length of the invoke statement's argument list matches the length of the target
 * methods's parameter type list.
 *
 * @author Steven Arzt
 */
public enum InvokeArgumentValidator implements BodyValidator {
  INSTANCE;

  public static InvokeArgumentValidator v() {
    return INSTANCE;
  }

  @Override
  public void validate(Body body, List<ValidationException> exceptions) {
    for (Unit u : body.getUnits()) {
      Stmt s = (Stmt) u;
      if (s.containsInvokeExpr()) {
        InvokeExpr iinvExpr = s.getInvokeExpr();
        SootMethod callee = iinvExpr.getMethod();
        if (callee != null && iinvExpr.getArgCount() != callee.getParameterCount()) {
          exceptions.add(new ValidationException(s, "Invalid number of arguments"));
        }
      }
    }
  }

  @Override
  public boolean isBasicValidator() {
    return true;
  }

}
