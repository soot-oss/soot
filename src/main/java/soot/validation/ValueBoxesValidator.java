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

import java.io.PrintStream;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import soot.Body;
import soot.Unit;
import soot.ValueBox;

/**
 * Verifies that a {@link ValueBox} is not used in more than one place.
 */
public enum ValueBoxesValidator implements BodyValidator {
  INSTANCE;

  public static ValueBoxesValidator v() {
    return INSTANCE;
  }

  @Override
  public void validate(Body body, List<ValidationException> exception) {
    boolean foundProblem = false;
    Set<ValueBox> set = Collections.newSetFromMap(new IdentityHashMap<ValueBox, Boolean>());
    for (ValueBox vb : body.getUseAndDefBoxes()) {
      if (!set.add(vb)) {
        foundProblem = true;
        exception.add(new ValidationException(vb, "Aliased value box : " + vb + " in " + body.getMethod()));
      }
    }

    if (foundProblem) {
      final PrintStream err = System.err;
      err.println("Found a ValueBox used more than once in body of " + body.getMethod() + ":");
      for (Unit u : body.getUnits()) {
        err.println("\t" + u);
      }
    }
  }

  @Override
  public boolean isBasicValidator() {
    return false;
  }
}
