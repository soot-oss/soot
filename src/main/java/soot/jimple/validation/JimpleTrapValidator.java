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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.Body;
import soot.Trap;
import soot.Unit;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.IdentityStmt;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

/**
 * This validator checks whether the jimple traps are correct. It does not perform the same checks as
 * {@link soot.validation.TrapsValidator}
 *
 * @see JimpleTrapValidator#validate(Body, List)
 * @author Marc Miltenberger
 */
public enum JimpleTrapValidator implements BodyValidator {
  INSTANCE;

  public static JimpleTrapValidator v() {
    return INSTANCE;
  }

  /**
   * Checks whether all Caught-Exception-References are associated to traps.
   */
  @Override
  public void validate(Body body, List<ValidationException> exceptions) {
    Set<Unit> caughtUnits = new HashSet<Unit>();
    for (Trap trap : body.getTraps()) {
      caughtUnits.add(trap.getHandlerUnit());

      if (!(trap.getHandlerUnit() instanceof IdentityStmt)) {
        exceptions.add(new ValidationException(trap, "Trap handler does not start with caught " + "exception reference"));
      } else {
        IdentityStmt is = (IdentityStmt) trap.getHandlerUnit();
        if (!(is.getRightOp() instanceof CaughtExceptionRef)) {
          exceptions.add(new ValidationException(trap, "Trap handler does not start with caught " + "exception reference"));
        }
      }
    }
    for (Unit u : body.getUnits()) {
      if (u instanceof IdentityStmt) {
        IdentityStmt id = (IdentityStmt) u;
        if (id.getRightOp() instanceof CaughtExceptionRef) {
          if (!caughtUnits.contains(id)) {
            exceptions.add(new ValidationException(id, "Could not find a corresponding trap using this statement as handler",
                "Body of method " + body.getMethod().getSignature() + " contains a caught exception reference,"
                    + "but not a corresponding trap using this statement as handler"));
          }
        }
      }
    }
  }

  @Override
  public boolean isBasicValidator() {
    return true;
  }
}
