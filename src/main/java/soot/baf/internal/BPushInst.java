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

import soot.UnitPrinter;
import soot.baf.InstSwitch;
import soot.baf.PushInst;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.LongConstant;
import soot.util.Switch;

public class BPushInst extends AbstractInst implements PushInst {
  private Constant constant;

  public BPushInst(Constant c) {
    this.constant = c;
  }

  public Object clone() {
    return new BPushInst(getConstant());
  }

  final public String getName() {
    return "push";
  }

  final String getParameters() {
    return " " + constant.toString();
  }

  protected void getParameters(UnitPrinter up) {
    up.literal(" ");
    up.constant(constant);
  }

  public int getInCount() {
    return 0;
  }

  public int getInMachineCount() {
    return 0;
  }

  public int getOutCount() {
    return 1;
  }

  public int getOutMachineCount() {
    if (constant instanceof LongConstant || constant instanceof DoubleConstant) {
      return 2;
    } else {
      return 1;
    }
  }

  public void apply(Switch sw) {
    ((InstSwitch) sw).casePushInst(this);
  }

  public Constant getConstant() {
    return constant;
  }

  public void setConstant(Constant c) {
    this.constant = c;
  }
}
