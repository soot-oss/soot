package soot.jimple.internal;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.jimple.SwitchStmt;

@SuppressWarnings("serial")
public abstract class AbstractSwitchStmt extends AbstractStmt implements SwitchStmt {

  protected final ValueBox keyBox;
  protected final UnitBox defaultTargetBox;
  protected final List<UnitBox> targetBoxes;
  protected final List<UnitBox> stmtBoxes;

  protected AbstractSwitchStmt(ValueBox keyBox, UnitBox defaultTargetBox, UnitBox... targetBoxes) {
    this.keyBox = keyBox;
    this.defaultTargetBox = defaultTargetBox;
    this.targetBoxes = new ArrayList<>(targetBoxes.length);
    for (UnitBox t : targetBoxes) {
      this.targetBoxes.add(t);
    }

    // Build up stmtBoxes
    List<UnitBox> list = new ArrayList<UnitBox>(targetBoxes.length + 1);
    Collections.addAll(list, targetBoxes);
    list.add(defaultTargetBox);
    this.stmtBoxes = list;
  }

  // This method is necessary to deal with constructor-must-be-first-ism.
  protected static UnitBox[] getTargetBoxesArray(List<? extends Unit> targets, Function<Unit, UnitBox> stmtBoxWrap) {
    UnitBox[] targetBoxes = new UnitBox[targets.size()];
    for (ListIterator<? extends Unit> it = targets.listIterator(); it.hasNext();) {
      Unit u = it.next();
      targetBoxes[it.previousIndex()] = stmtBoxWrap.apply(u);
    }
    return targetBoxes;
  }

  @Override
  final public Unit getDefaultTarget() {
    return defaultTargetBox.getUnit();
  }

  @Override
  final public void setDefaultTarget(Unit defaultTarget) {
    defaultTargetBox.setUnit(defaultTarget);
  }

  @Override
  final public UnitBox getDefaultTargetBox() {
    return defaultTargetBox;
  }

  @Override
  final public Value getKey() {
    return keyBox.getValue();
  }

  @Override
  final public void setKey(Value key) {
    keyBox.setValue(key);
  }

  @Override
  final public ValueBox getKeyBox() {
    return keyBox;
  }

  @Override
  final public List<ValueBox> getUseBoxes() {
    List<ValueBox> list = new ArrayList<ValueBox>(keyBox.getValue().getUseBoxes());
    list.add(keyBox);
    return list;
  }

  final public int getTargetCount() {
    return targetBoxes.size();
  }

  @Override
  final public Unit getTarget(int index) {
    return targetBoxes.get(index).getUnit();
  }

  @Override
  final public UnitBox getTargetBox(int index) {
    return targetBoxes.get(index);
  }

  @Override
  final public void setTarget(int index, Unit target) {
    targetBoxes.get(index).setUnit(target);
  }

  @Override
  final public List<Unit> getTargets() {
    final List<UnitBox> boxes = this.targetBoxes;
    List<Unit> targets = new ArrayList<Unit>(boxes.size());
    for (UnitBox element : boxes) {
      targets.add(element.getUnit());
    }
    return targets;
  }

  final public void setTargets(List<? extends Unit> targets) {
    for (ListIterator<? extends Unit> it = targets.listIterator(); it.hasNext();) {
      Unit u = it.next();
      targetBoxes.get(it.previousIndex()).setUnit(u);
    }
  }

  final public void setTargets(Unit[] targets) {
    for (int i = 0, e = targets.length; i < e; i++) {
      targetBoxes.get(i).setUnit(targets[i]);
    }
  }

  @Override
  final public List<UnitBox> getUnitBoxes() {
    return stmtBoxes;
  }

  @Override
  public final boolean fallsThrough() {
    return false;
  }

  @Override
  public final boolean branches() {
    return true;
  }
}
