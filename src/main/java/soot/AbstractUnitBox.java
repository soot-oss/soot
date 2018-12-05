package soot;

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

/**
 * Reference implementation for UnitBox; just add a canContainUnit method.
 */
@SuppressWarnings("serial")
public abstract class AbstractUnitBox implements UnitBox {
  protected Unit unit;

  public abstract boolean canContainUnit(Unit u);

  public boolean isBranchTarget() {
    return true;
  }

  public void setUnit(Unit unit) {
    if (!canContainUnit(unit)) {
      throw new RuntimeException("attempting to put invalid unit in UnitBox");
    }

    // Remove this from set of back pointers.
    if (this.unit != null) {
      this.unit.removeBoxPointingToThis(this);
    }

    // Perform link
    this.unit = unit;

    // Add this to back pointers
    if (this.unit != null) {
      this.unit.addBoxPointingToThis(this);
    }
  }

  public Unit getUnit() {
    return unit;
  }

  public void toString(UnitPrinter up) {
    up.startUnitBox(this);
    up.unitRef(unit, isBranchTarget());
    up.endUnitBox(this);
  }
}
