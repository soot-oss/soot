package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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

import soot.AbstractValueBox;
import soot.EquivTo;
import soot.Unit;
import soot.UnitBox;
import soot.UnitPrinter;
import soot.Value;
import soot.jimple.Jimple;

/**
 * Utility class used to package a Value and a Unit together.
 *
 * @author Navindra Umanee
 **/
public class ValueUnitPair extends AbstractValueBox implements UnitBox, EquivTo {
  // oub was initially a private inner class. ended up being a
  // *bad* *bad* idea with endless opportunity for *evil* *evil*
  // pointer bugs. in the end so much code needed to be copy pasted
  // that using an innerclass to reuse code from AbstractUnitBox was
  // a losing proposition.
  // protected UnitBox oub;
  protected Unit unit;

  /**
   * Constructs a ValueUnitPair from a Unit object and a Value object.
   *
   * @param value
   *          some Value
   * @param unit
   *          some Unit.
   **/
  public ValueUnitPair(Value value, Unit unit) {
    setValue(value);
    setUnit(unit);
  }

  @Override
  public boolean canContainValue(Value value) {
    return true;
  }

  /**
   * @see soot.UnitBox#setUnit(Unit)
   **/
  @Override
  public void setUnit(Unit unit) {
    /* Code copied from AbstractUnitBox */

    if (!canContainUnit(unit)) {
      throw new RuntimeException("Cannot put " + unit + " in this box");
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

  /**
   * @see soot.UnitBox#getUnit()
   **/
  @Override
  public Unit getUnit() {
    return unit;
  }

  /**
   * @see soot.UnitBox#canContainUnit(Unit)
   **/
  @Override
  public boolean canContainUnit(Unit u) {
    return true;
  }

  /**
   * @see soot.UnitBox#isBranchTarget()
   **/
  @Override
  public boolean isBranchTarget() {
    return true;
  }

  @Override
  public String toString() {
    return "Value = " + getValue() + ", Unit = " + getUnit();
  }

  @Override
  public void toString(UnitPrinter up) {
    super.toString(up);

    up.literal(isBranchTarget() ? ", " : " #");
    up.startUnitBox(this);
    up.unitRef(unit, isBranchTarget());
    up.endUnitBox(this);
  }

  @Override
  public int hashCode() {
    // If you need to change this implementation, please change it in
    // a subclass. Otherwise, Shimple is likely to break in evil ways.
    return super.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    // If you need to change this implementation, please change it in
    // a subclass. Otherwise, Shimple is likely to break in evil ways.
    return super.equals(other);
  }

  /**
   * Two ValueUnitPairs are equivTo iff they hold the same Unit objects and equivalent Value objects within them.
   *
   * @param other
   *          another ValueUnitPair
   * @return true if other contains the same objects as this.
   **/
  @Override
  public boolean equivTo(Object other) {
    return (other instanceof ValueUnitPair) && ((ValueUnitPair) other).getValue().equivTo(this.getValue())
        && ((ValueUnitPair) other).getUnit().equals(getUnit());
  }

  /**
   * Non-deterministic hashcode consistent with equivTo() implementation.
   *
   * <p>
   *
   * <b>Note:</b> If you are concerned about non-determinism, remember that current implementations of equivHashCode() in
   * other parts of Soot are non-deterministic as well (see Constant.java for example).
   **/
  @Override
  public int equivHashCode() {
    // this is not deterministic because a Unit's hash code is
    // non-deterministic.
    return (getUnit().hashCode() * 17) + (getValue().equivHashCode() * 101);
  }

  @Override
  public Object clone() {
    // Note to self: Do not try to "fix" this. Yes, it should be
    // a shallow copy in order to conform with the rest of Soot.
    // When a body is cloned, the Values are cloned explicitly and
    // replaced, and UnitBoxes are explicitly patched. See
    // Body.importBodyContentsFrom for details.
    Value cv = Jimple.cloneIfNecessary(getValue());
    Unit cu = getUnit();
    return new ValueUnitPair(cv, cu);
  }
}
