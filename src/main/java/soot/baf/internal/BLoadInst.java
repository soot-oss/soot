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
import soot.Type;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.baf.InstSwitch;
import soot.baf.LoadInst;
import soot.util.Switch;

public class BLoadInst extends AbstractOpTypeInst implements LoadInst {

  ValueBox localBox;
  List<ValueBox> useBoxes;

  public BLoadInst(Type opType, Local local) {
    super(opType);
    this.localBox = new BafLocalBox(local);
    this.useBoxes = Collections.singletonList(localBox);
  }

  @Override
  public Object clone() {
    return new BLoadInst(getOpType(), getLocal());
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
    return 1;
  }

  @Override
  final public String getName() {
    return "load";
  }

  @Override
  final String getParameters() {
    return " " + localBox.getValue().toString();
  }

  @Override
  protected void getParameters(UnitPrinter up) {
    up.literal(" ");
    localBox.toString(up);
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseLoadInst(this);
  }

  @Override
  public void setLocal(Local l) {
    localBox.setValue(l);
  }

  @Override
  public Local getLocal() {
    return (Local) localBox.getValue();
  }

  @Override
  public List<ValueBox> getUseBoxes() {
    return useBoxes;
  }
}
