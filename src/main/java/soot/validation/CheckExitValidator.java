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

import soot.Body;
import soot.Trap;
import soot.Unit;

public enum CheckExitValidator implements BodyValidator {
  INSTANCE;

  public static CheckExitValidator v() {
    return INSTANCE;
  }

  @Override
  public void validate(Body body, List<ValidationException> exception) {
    Unit last = body.getUnits().getLast();
    if (last.fallsThrough()) {
      for (Trap exc : body.getTraps()) {
        if (exc.getEndUnit() == last) {
          // That's fine.
          return;
        }
      }
      exception.add(new ValidationException(last,
          "Last statement is a fallthrough statement; it should be a non-fallthrough statement such as "
              + "a return or a throw."));
    }
  }

  @Override
  public boolean isBasicValidator() {
    return true;
  }
}
