package soot.toolkits.scalar;

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

import soot.Unit;
import soot.ValueBox;

/**
 * Utility class used to package a Unit and a ValueBox together.
 */
public class UnitValueBoxPair {
  public Unit unit;
  public ValueBox valueBox;

  /**
   * Constructs a UnitValueBoxPair form a Unit object and a ValueBox object.
   * 
   * @param local
   *          some Local
   * @param unit
   *          some Unit.
   */
  public UnitValueBoxPair(Unit unit, ValueBox valueBox) {
    this.unit = unit;
    this.valueBox = valueBox;
  }

  /**
   * Two UnitValueBoxPairs are equal iff they the Unit they hold are 'equal' and the ValueBoxes they hold are 'equal'.
   * 
   * @param other
   *          another UnitValueBoxPair
   * @return true if equal.
   */
  public boolean equals(Object other) {
    if (other instanceof UnitValueBoxPair) {
      UnitValueBoxPair otherPair = (UnitValueBoxPair) other;

      if (unit.equals(otherPair.unit) && valueBox.equals(otherPair.valueBox)) {
        return true;
      }
    }

    return false;
  }

  public int hashCode() {
    return unit.hashCode() + valueBox.hashCode();
  }

  public String toString() {
    return valueBox + " in " + unit;
  }

  public Unit getUnit() {
    return unit;
  }

  public ValueBox getValueBox() {
    return valueBox;
  }

}
