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

import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.jimple.SwitchStmt;

@SuppressWarnings("serial")
public abstract class AbstractSwitchStmt extends AbstractStmt implements SwitchStmt {

  final UnitBox defaultTargetBox;

  final ValueBox keyBox;

  final List<UnitBox> stmtBoxes;

  final protected UnitBox[] targetBoxes;

  protected AbstractSwitchStmt(ValueBox keyBox, UnitBox defaultTargetBox, UnitBox... targetBoxes) {
    this.keyBox = keyBox;
    this.defaultTargetBox = defaultTargetBox;
    this.targetBoxes = targetBoxes;

    // Build up stmtBoxes
    List<UnitBox> list = new ArrayList<UnitBox>();
    stmtBoxes = Collections.unmodifiableList(list);

    Collections.addAll(list, targetBoxes);
    list.add(defaultTargetBox);
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
    List<ValueBox> list = new ArrayList<ValueBox>();

    list.addAll(keyBox.getValue().getUseBoxes());
    list.add(keyBox);

    return list;
  }

  final public int getTargetCount() {
    return targetBoxes.length;
  }

  @Override
  final public Unit getTarget(int index) {
    return targetBoxes[index].getUnit();
  }

  @Override
  final public UnitBox getTargetBox(int index) {
    return targetBoxes[index];
  }

  @Override
  final public void setTarget(int index, Unit target) {
    targetBoxes[index].setUnit(target);
  }

  @Override
  final public List<Unit> getTargets() {
    List<Unit> targets = new ArrayList<Unit>();

    for (UnitBox element : targetBoxes) {
      targets.add(element.getUnit());
    }

    return targets;
  }

  final public void setTargets(List<? extends Unit> targets) {
    for (int i = 0; i < targets.size(); i++) {
      targetBoxes[i].setUnit(targets.get(i));
    }
  }

  final public void setTargets(Unit[] targets) {
    for (int i = 0; i < targets.length; i++) {
      targetBoxes[i].setUnit(targets[i]);
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
