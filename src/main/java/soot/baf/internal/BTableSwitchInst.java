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

import java.util.List;

import soot.Unit;
import soot.UnitPrinter;
import soot.baf.InstSwitch;
import soot.baf.TableSwitchInst;
import soot.util.Switch;

public class BTableSwitchInst extends AbstractSwitchInst implements TableSwitchInst {

  int lowIndex;
  int highIndex;

  public BTableSwitchInst(Unit defaultTarget, int lowIndex, int highIndex, List<? extends Unit> targets) {
    super(defaultTarget, targets);
    this.lowIndex = lowIndex;
    this.highIndex = highIndex;
  }

  @Override
  public Object clone() {
    return new BTableSwitchInst(getDefaultTarget(), lowIndex, highIndex, getTargets());
  }

  @Override
  public void setLowIndex(int lowIndex) {
    this.lowIndex = lowIndex;
  }

  @Override
  public void setHighIndex(int highIndex) {
    this.highIndex = highIndex;
  }

  @Override
  public int getLowIndex() {
    return lowIndex;
  }

  @Override
  public int getHighIndex() {
    return highIndex;
  }

  @Override
  public String getName() {
    return "tableswitch";
  }

  @Override
  public String toString() {
    final String endOfLine = " ";

    StringBuilder buffer = new StringBuilder();

    buffer.append("tableswitch").append(endOfLine);
    buffer.append('{').append(endOfLine);

    // In this for-loop, we cannot use "<=" since 'i' would wrap around.
    // The case for "i == highIndex" is handled separately after the loop.
    final int low = lowIndex;
    final int high = highIndex;
    for (int i = low; i < high; i++) {
      buffer.append("    case ").append(i).append(": goto ").append(getTarget(i - low)).append(';').append(endOfLine);
    }
    buffer.append("    case ").append(high).append(": goto ").append(getTarget(high - low)).append(';').append(endOfLine);

    buffer.append("    default: goto ").append(getDefaultTarget()).append(';').append(endOfLine);
    buffer.append('}');

    return buffer.toString();
  }

  @Override
  public void toString(UnitPrinter up) {
    up.literal("tableswitch");
    up.newline();
    up.literal("{");
    up.newline();

    // In this for-loop, we cannot use "<=" since 'i' would wrap around.
    // The case for "i == highIndex" is handled separately after the loop.
    for (int i = lowIndex; i < highIndex; i++) {
      printCaseTarget(up, i);
    }
    printCaseTarget(up, highIndex);

    up.literal("    default: goto ");
    defaultTargetBox.toString(up);
    up.literal(";");
    up.newline();
    up.literal("}");
  }

  private void printCaseTarget(UnitPrinter up, int targetIndex) {
    up.literal("    case ");
    up.literal(Integer.toString(targetIndex));
    up.literal(": goto ");
    targetBoxes[targetIndex - lowIndex].toString(up);
    up.literal(";");
    up.newline();
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseTableSwitchInst(this);
  }
}
