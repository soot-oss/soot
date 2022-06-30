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
import soot.UnitPrinter;
import soot.baf.InstSwitch;
import soot.baf.LookupSwitchInst;
import soot.jimple.Constant;
import soot.jimple.IntConstant;
import soot.util.Switch;

public class BLookupSwitchInst extends AbstractSwitchInst implements LookupSwitchInst {

  List<IntConstant> lookupValues;

  public BLookupSwitchInst(Unit defaultTarget, List<IntConstant> lookupValues, List<? extends Unit> targets) {
    super(defaultTarget, targets);
    setLookupValues(lookupValues);
  }

  @Override
  public Object clone() {
    return new BLookupSwitchInst(getDefaultTarget(), lookupValues, getTargets());
  }

  @Override
  public void setLookupValues(List<IntConstant> lookupValues) {
    this.lookupValues = new ArrayList<IntConstant>(lookupValues);
  }

  @Override
  public void setLookupValue(int index, int value) {
    this.lookupValues.set(index, IntConstant.v(value));
  }

  @Override
  public int getLookupValue(int index) {
    return lookupValues.get(index).value;
  }

  @Override
  public List<IntConstant> getLookupValues() {
    return Collections.unmodifiableList(lookupValues);
  }

  @Override
  public String getName() {
    return "lookupswitch";
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    String endOfLine = " ";

    buffer.append("lookupswitch").append(endOfLine);

    buffer.append("{").append(endOfLine);

    for (int i = 0; i < lookupValues.size(); i++) {
      buffer.append("    case ").append(lookupValues.get(i)).append(": goto ").append(getTarget(i)).append(";")
          .append(endOfLine);
    }

    buffer.append("    default: goto ").append(getDefaultTarget()).append(";").append(endOfLine);
    buffer.append("}");

    return buffer.toString();
  }

  @Override
  public void toString(UnitPrinter up) {
    up.literal("lookupswitch");
    up.newline();
    up.literal("{");
    up.newline();

    for (int i = 0; i < lookupValues.size(); i++) {
      up.literal("    case ");
      up.constant((Constant) lookupValues.get(i));
      up.literal(": goto ");
      targetBoxes[i].toString(up);
      up.literal(";");
      up.newline();
    }

    up.literal("    default: goto ");
    defaultTargetBox.toString(up);
    up.literal(";");
    up.newline();
    up.literal("}");
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseLookupSwitchInst(this);
  }
}
