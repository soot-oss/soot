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

import soot.Local;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.baf.IncInst;
import soot.baf.InstSwitch;
import soot.jimple.Constant;
import soot.util.Switch;

public class BIncInst extends AbstractInst implements IncInst {
  final ValueBox localBox;
  final ValueBox defLocalBox;
  final List<ValueBox> useBoxes;
  final List<ValueBox> mDefBoxes;
  Constant mConstant;

  public BIncInst(Local local, Constant constant) {
    mConstant = constant;

    localBox = new BafLocalBox(local);
    useBoxes = Collections.singletonList(localBox);

    defLocalBox = new BafLocalBox(local);
    mDefBoxes = Collections.singletonList(defLocalBox);

  }

  public int getInCount() {
    return 0;
  }

  public Object clone() {
    return new BIncInst(getLocal(), getConstant());
  }

  public int getInMachineCount() {
    return 0;
  }

  public int getOutCount() {
    return 0;
  }

  public int getOutMachineCount() {
    return 0;
  }

  public Constant getConstant() {
    return mConstant;
  }

  public void setConstant(Constant aConstant) {
    mConstant = aConstant;
  }

  final public String getName() {
    return "inc.i";
  }

  final String getParameters() {
    return " " + localBox.getValue().toString();
  }

  protected void getParameters(UnitPrinter up) {
    up.literal(" ");
    localBox.toString(up);
  }

  public void apply(Switch sw) {
    ((InstSwitch) sw).caseIncInst(this);
  }

  public void setLocal(Local l) {
    localBox.setValue(l);
  }

  public Local getLocal() {
    return (Local) localBox.getValue();
  }

  @Override
  public List<ValueBox> getUseBoxes() {
    return useBoxes;
  }

  @Override
  public List<ValueBox> getDefBoxes() {
    return mDefBoxes;
  }

  public String toString() {
    return "inc.i" + " " + getLocal() + " " + getConstant();
  }

  public void toString(UnitPrinter up) {
    up.literal("inc.i");
    up.literal(" ");
    localBox.toString(up);
    up.literal(" ");
    up.constant(mConstant);
  }

}
