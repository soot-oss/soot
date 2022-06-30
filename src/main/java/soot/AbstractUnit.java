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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import soot.tagkit.AbstractHost;
import soot.util.Switch;

/**
 * Provides default implementations for the methods in Unit.
 */
@SuppressWarnings("serial")
public abstract class AbstractUnit extends AbstractHost implements Unit {

  /**
   * List of UnitBoxes pointing to this Unit.
   */
  protected List<UnitBox> boxesPointingToThis = null;

  /**
   * Returns a deep clone of this object.
   */
  @Override
  public abstract Object clone();

  /**
   * Returns a list of Boxes containing Values used in this Unit. The list of boxes is dynamically updated as the structure
   * changes. Note that they are returned in usual evaluation order. (this is important for aggregation)
   */
  @Override
  public List<ValueBox> getUseBoxes() {
    return Collections.emptyList();
  }

  /**
   * Returns a list of Boxes containing Values defined in this Unit. The list of boxes is dynamically updated as the
   * structure changes.
   */
  @Override
  public List<ValueBox> getDefBoxes() {
    return Collections.emptyList();
  }

  /**
   * Returns a list of Boxes containing Units defined in this Unit; typically branch targets. The list of boxes is
   * dynamically updated as the structure changes.
   */
  @Override
  public List<UnitBox> getUnitBoxes() {
    return Collections.emptyList();
  }

  /**
   * Returns a list of Boxes pointing to this Unit.
   */
  @Override
  public List<UnitBox> getBoxesPointingToThis() {
    List<UnitBox> ref = boxesPointingToThis;
    return (ref == null) ? Collections.emptyList() : Collections.unmodifiableList(ref);
  }

  @Override
  public void addBoxPointingToThis(UnitBox b) {
    List<UnitBox> ref = boxesPointingToThis;
    if (ref == null) {
      boxesPointingToThis = ref = new ArrayList<UnitBox>();
    }
    ref.add(b);
  }

  @Override
  public void removeBoxPointingToThis(UnitBox b) {
    List<UnitBox> ref = boxesPointingToThis;
    if (ref != null) {
      ref.remove(b);
    }
  }

  @Override
  public void clearUnitBoxes() {
    for (UnitBox ub : getUnitBoxes()) {
      ub.setUnit(null);
    }
  }

  /**
   * Returns a list of ValueBoxes, either used or defined in this Unit.
   */
  @Override
  public List<ValueBox> getUseAndDefBoxes() {
    List<ValueBox> useBoxes = getUseBoxes();
    List<ValueBox> defBoxes = getDefBoxes();
    if (useBoxes.isEmpty()) {
      return defBoxes;
    } else if (defBoxes.isEmpty()) {
      return useBoxes;
    } else {
      List<ValueBox> valueBoxes = new ArrayList<ValueBox>(defBoxes.size() + useBoxes.size());
      valueBoxes.addAll(defBoxes);
      valueBoxes.addAll(useBoxes);
      return valueBoxes;
    }
  }

  /**
   * Used to implement the Switchable construct.
   */
  @Override
  public void apply(Switch sw) {
  }

  @Override
  public void redirectJumpsToThisTo(Unit newLocation) {
    // important to make a copy to prevent concurrent modification
    for (UnitBox box : new ArrayList<>(getBoxesPointingToThis())) {
      if (box.getUnit() != this) {
        throw new RuntimeException("Something weird's happening");
      }

      if (box.isBranchTarget()) {
        box.setUnit(newLocation);
      }
    }
  }
}
