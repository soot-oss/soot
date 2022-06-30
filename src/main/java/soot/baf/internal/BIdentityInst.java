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

import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.baf.Baf;
import soot.baf.IdentityInst;
import soot.baf.InstSwitch;
import soot.util.Switch;

public class BIdentityInst extends AbstractInst implements IdentityInst {

  ValueBox leftBox;
  ValueBox rightBox;
  List<ValueBox> defBoxes;

  protected BIdentityInst(ValueBox localBox, ValueBox identityValueBox) {
    this.leftBox = localBox;
    this.rightBox = identityValueBox;
    this.defBoxes = Collections.singletonList(localBox);
  }

  public BIdentityInst(Value local, Value identityValue) {
    this(Baf.v().newLocalBox(local), Baf.v().newIdentityRefBox(identityValue));
  }

  @Override
  public Object clone() {
    return new BIdentityInst(getLeftOp(), getRightOp());
  }

  @Override
  public int getInCount() {
    return 0;
  }

  @Override
  public int getInMachineCount() {
    return 0;
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
  public Value getLeftOp() {
    return leftBox.getValue();
  }

  @Override
  public Value getRightOp() {
    return rightBox.getValue();
  }

  @Override
  public ValueBox getLeftOpBox() {
    return leftBox;
  }

  @Override
  public ValueBox getRightOpBox() {
    return rightBox;
  }

  @Override
  public List<ValueBox> getDefBoxes() {
    return defBoxes;
  }

  @Override
  public List<ValueBox> getUseBoxes() {
    List<ValueBox> list = new ArrayList<ValueBox>();

    list.addAll(rightBox.getValue().getUseBoxes());
    list.add(rightBox);
    list.addAll(leftBox.getValue().getUseBoxes());

    return list;
  }

  @Override
  public String toString() {
    return leftBox.getValue().toString() + " := " + rightBox.getValue().toString();
  }

  @Override
  public void toString(UnitPrinter up) {
    leftBox.toString(up);
    up.literal(" := ");
    rightBox.toString(up);
  }

  @Override
  final public String getName() {
    return ":=";
  }

  @Override
  public void setLeftOp(Value local) {
    leftBox.setValue(local);
  }

  @Override
  public void setRightOp(Value identityRef) {
    rightBox.setValue(identityRef);
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseIdentityInst(this);
  }
}
