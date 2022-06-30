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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import soot.Unit;
import soot.UnitBox;
import soot.baf.Baf;
import soot.baf.SwitchInst;

public abstract class AbstractSwitchInst extends AbstractInst implements SwitchInst {

  UnitBox defaultTargetBox;
  UnitBox[] targetBoxes;
  List<UnitBox> unitBoxes;

  public AbstractSwitchInst(Unit defaultTarget, List<? extends Unit> targets) {
    this.defaultTargetBox = Baf.v().newInstBox(defaultTarget);

    // Build up 'targetBoxes'
    final int numTargets = targets.size();
    UnitBox[] tgts = new UnitBox[numTargets];
    for (int i = 0; i < numTargets; i++) {
      tgts[i] = Baf.v().newInstBox(targets.get(i));
    }
    this.targetBoxes = tgts;

    // Build up 'unitBoxes'
    List<UnitBox> unitBoxes = new ArrayList<UnitBox>(numTargets + 1);
    for (UnitBox element : tgts) {
      unitBoxes.add(element);
    }
    unitBoxes.add(defaultTargetBox);
    this.unitBoxes = Collections.unmodifiableList(unitBoxes);
  }

  @Override
  public int getInCount() {
    return 1;
  }

  @Override
  public int getInMachineCount() {
    return 1;
  }

  @Override
  public int getOutCount() {
    return 0;
  }

  @Override
  public int getOutMachineCount() {
    return 0;
  }

  @Override
  public List<UnitBox> getUnitBoxes() {
    return unitBoxes;
  }

  @Override
  public boolean fallsThrough() {
    return false;
  }

  @Override
  public boolean branches() {
    return true;
  }

  @Override
  public Unit getDefaultTarget() {
    return defaultTargetBox.getUnit();
  }

  @Override
  public void setDefaultTarget(Unit defaultTarget) {
    defaultTargetBox.setUnit(defaultTarget);
  }

  @Override
  public UnitBox getDefaultTargetBox() {
    return defaultTargetBox;
  }

  @Override
  public int getTargetCount() {
    return targetBoxes.length;
  }

  @Override
  public Unit getTarget(int index) {
    return targetBoxes[index].getUnit();
  }

  @Override
  public void setTarget(int index, Unit target) {
    targetBoxes[index].setUnit(target);
  }

  @Override
  public void setTargets(List<Unit> targets) {
    final UnitBox[] targetBoxes = this.targetBoxes;
    for (int i = 0; i < targets.size(); i++) {
      targetBoxes[i].setUnit(targets.get(i));
    }
  }

  @Override
  public UnitBox getTargetBox(int index) {
    return targetBoxes[index];
  }

  @Override
  public List<Unit> getTargets() {
    List<Unit> targets = new ArrayList<Unit>();
    for (UnitBox element : targetBoxes) {
      targets.add(element.getUnit());
    }
    return targets;
  }
}
