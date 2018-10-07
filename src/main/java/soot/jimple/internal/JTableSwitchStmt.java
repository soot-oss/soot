package soot.jimple.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
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
import java.util.List;

import soot.Unit;
import soot.UnitBox;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.baf.Baf;
import soot.baf.PlaceholderInst;
import soot.jimple.ConvertToBaf;
import soot.jimple.Jimple;
import soot.jimple.JimpleToBafContext;
import soot.jimple.StmtSwitch;
import soot.jimple.TableSwitchStmt;
import soot.util.Switch;

public class JTableSwitchStmt extends AbstractSwitchStmt implements TableSwitchStmt {
  int lowIndex;
  int highIndex;

  // This method is necessary to deal with constructor-must-be-first-ism.
  private static UnitBox[] getTargetBoxesArray(List<? extends Unit> targets) {
    UnitBox[] targetBoxes = new UnitBox[targets.size()];
    for (int i = 0; i < targetBoxes.length; i++) {
      targetBoxes[i] = Jimple.v().newStmtBox(targets.get(i));
    }
    return targetBoxes;
  }

  public Object clone() {
    return new JTableSwitchStmt(Jimple.cloneIfNecessary(getKey()), lowIndex, highIndex, getTargets(), getDefaultTarget());
  }

  public JTableSwitchStmt(Value key, int lowIndex, int highIndex, List<? extends Unit> targets, Unit defaultTarget) {
    this(Jimple.v().newImmediateBox(key), lowIndex, highIndex, getTargetBoxesArray(targets),
        Jimple.v().newStmtBox(defaultTarget));
  }

  public JTableSwitchStmt(Value key, int lowIndex, int highIndex, List<? extends UnitBox> targets, UnitBox defaultTarget) {
    this(Jimple.v().newImmediateBox(key), lowIndex, highIndex, targets.toArray(new UnitBox[targets.size()]), defaultTarget);
  }

  protected JTableSwitchStmt(ValueBox keyBox, int lowIndex, int highIndex, UnitBox[] targetBoxes, UnitBox defaultTargetBox) {
    super(keyBox, defaultTargetBox, targetBoxes);

    if (lowIndex > highIndex) {
      throw new RuntimeException(
          "Error creating tableswitch: lowIndex(" + lowIndex + ") can't be greater than highIndex(" + highIndex + ").");
    }

    this.lowIndex = lowIndex;
    this.highIndex = highIndex;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    String endOfLine = " ";

    buffer.append(Jimple.TABLESWITCH + "(" + keyBox.getValue().toString() + ")" + endOfLine);

    buffer.append("{" + endOfLine);

    // In this for-loop, we cannot use "<=" since 'i' would wrap around.
    // The case for "i == highIndex" is handled separately after the loop.
    for (int i = lowIndex; i < highIndex; i++) {
      Unit target = getTarget(i - lowIndex);
      buffer.append(
          "    " + Jimple.CASE + " " + i + ": " + Jimple.GOTO + " " + (target == this ? "self" : target) + ";" + endOfLine);
    }
    Unit target = getTarget(highIndex - lowIndex);
    buffer.append("    " + Jimple.CASE + " " + highIndex + ": " + Jimple.GOTO + " " + (target == this ? "self" : target)
        + ";" + endOfLine);

    target = getDefaultTarget();
    buffer.append("    " + Jimple.DEFAULT + ": " + Jimple.GOTO + " " + (target == this ? "self" : target) + ";" + endOfLine);

    buffer.append("}");

    return buffer.toString();
  }

  public void toString(UnitPrinter up) {
    up.literal(Jimple.TABLESWITCH);
    up.literal("(");
    keyBox.toString(up);
    up.literal(")");
    up.newline();
    up.literal("{");
    up.newline();
    // In this for-loop, we cannot use "<=" since 'i' would wrap around.
    // The case for "i == highIndex" is handled separately after the loop.
    for (int i = lowIndex; i < highIndex; i++) {
      printCaseTarget(up, i);
    }
    printCaseTarget(up, highIndex);

    up.literal("    ");
    up.literal(Jimple.DEFAULT);
    up.literal(": ");
    up.literal(Jimple.GOTO);
    up.literal(" ");
    defaultTargetBox.toString(up);
    up.literal(";");
    up.newline();
    up.literal("}");
  }

  private void printCaseTarget(UnitPrinter up, int targetIndex) {
    up.literal("    ");
    up.literal(Jimple.CASE);
    up.literal(" ");
    up.literal(Integer.toString(targetIndex));
    up.literal(": ");
    up.literal(Jimple.GOTO);
    up.literal(" ");
    targetBoxes[targetIndex - lowIndex].toString(up);
    up.literal(";");
    up.newline();
  }

  public void setLowIndex(int lowIndex) {
    this.lowIndex = lowIndex;
  }

  public void setHighIndex(int highIndex) {
    this.highIndex = highIndex;
  }

  public int getLowIndex() {
    return lowIndex;
  }

  public int getHighIndex() {
    return highIndex;
  }

  public void apply(Switch sw) {
    ((StmtSwitch) sw).caseTableSwitchStmt(this);
  }

  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    List<PlaceholderInst> targetPlaceholders = new ArrayList<PlaceholderInst>();

    ((ConvertToBaf) getKey()).convertToBaf(context, out);

    for (Unit target : getTargets()) {
      targetPlaceholders.add(Baf.v().newPlaceholderInst(target));
    }

    Unit u = Baf.v().newTableSwitchInst(Baf.v().newPlaceholderInst(getDefaultTarget()), lowIndex, highIndex,
        targetPlaceholders);
    u.addAllTagsOf(this);
    out.add(u);
  }

}
