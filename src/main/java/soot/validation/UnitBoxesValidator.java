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
import soot.UnitBox;

public enum UnitBoxesValidator implements BodyValidator {
  INSTANCE;

  public static UnitBoxesValidator v() {
    return INSTANCE;
  }

  @Override
  /** Verifies that the UnitBoxes of this Body all point to a Unit contained within this body. */
  public void validate(Body body, List<ValidationException> exception) {
    for (UnitBox ub : body.getAllUnitBoxes()) {
      if (!body.getUnits().contains(ub.getUnit())) {
        throw new RuntimeException(
            "Unitbox points outside unitChain! to unit : " + ub.getUnit() + " in " + body.getMethod());
      }
    }
  }

  @Override
  public boolean isBasicValidator() {
    return true;
  }
}
