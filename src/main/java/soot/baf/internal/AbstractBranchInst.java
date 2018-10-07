package soot.baf.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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

import java.util.Collections;
import java.util.List;

import soot.Unit;
import soot.UnitBox;
import soot.UnitPrinter;
import soot.util.Switch;

public abstract class AbstractBranchInst extends AbstractInst {
  UnitBox targetBox;

  final List<UnitBox> targetBoxes;

  AbstractBranchInst(UnitBox targetBox) {
    this.targetBox = targetBox;

    targetBoxes = Collections.singletonList(targetBox);
  }

  abstract public String getName();

  public String toString() {
    String target = "";
    Unit targetUnit = getTarget();
    if (this == targetUnit) {
      target = getName();
    } else {
      target = getTarget().toString();
    }
    return getName() + " " + target;
  }

  public void toString(UnitPrinter up) {
    up.literal(getName());
    up.literal(" ");
    targetBox.toString(up);
  }

  public Unit getTarget() {
    return targetBox.getUnit();
  }

  public void setTarget(Unit target) {
    targetBox.setUnit(target);
  }

  public UnitBox getTargetBox() {
    return targetBox;
  }

  public List<UnitBox> getUnitBoxes() {
    return targetBoxes;
  }

  abstract public void apply(Switch sw);

  public boolean branches() {
    return true;
  }

}
