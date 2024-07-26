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

  protected int lowIndex;
  protected int highIndex;

  public JTableSwitchStmt(Value key, int lowIndex, int highIndex, List<? extends Unit> targets, Unit defaultTarget) {
    this(Jimple.v().newImmediateBox(key), lowIndex, highIndex, getTargetBoxesArray(targets, Jimple.v()::newStmtBox),
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

  @Override
  public Object clone() {
    return new JTableSwitchStmt(Jimple.cloneIfNecessary(getKey()), lowIndex, highIndex, getTargets(), getDefaultTarget());
  }

  @Override
  public String toString() {
    final char endOfLine = ' ';
    StringBuilder buf = new StringBuilder(Jimple.TABLESWITCH + "(");

    buf.append(keyBox.getValue().toString()).append(')').append(endOfLine);
    buf.append('{').append(endOfLine);

    // In this for-loop, we cannot use "<=" since 'i' would wrap around.
    // The case for "i == highIndex" is handled separately after the loop.
    final int low = lowIndex, high = highIndex;
    for (int i = low; i < high; i++) {
      buf.append("    " + Jimple.CASE + " ").append(i).append(": " + Jimple.GOTO + " ");
      Unit target = getTarget(i - low);
      buf.append(target == this ? "self" : target).append(';').append(endOfLine);
    }
    {
      buf.append("    " + Jimple.CASE + " ").append(high).append(": " + Jimple.GOTO + " ");
      Unit target = getTarget(high - low);
      buf.append(target == this ? "self" : target).append(';').append(endOfLine);
    }
    {
      Unit target = getDefaultTarget();
      buf.append("    " + Jimple.DEFAULT + ": " + Jimple.GOTO + " ");
      buf.append(target == this ? "self" : target).append(';').append(endOfLine);
    }
    buf.append('}');

    return buf.toString();
  }

  @Override
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
    final int high = highIndex;
    for (int i = lowIndex; i < high; i++) {
      printCaseTarget(up, i);
    }
    printCaseTarget(up, high);
    up.literal("    " + Jimple.DEFAULT + ": " + Jimple.GOTO + " ");
    defaultTargetBox.toString(up);
    up.literal(";");
    up.newline();
    up.literal("}");
  }

  private void printCaseTarget(UnitPrinter up, int targetIndex) {
    up.literal("    " + Jimple.CASE + " ");
    up.literal(Integer.toString(targetIndex));
    up.literal(": " + Jimple.GOTO + " ");
    targetBoxes.get(targetIndex - lowIndex).toString(up);
    up.literal(";");
    up.newline();
  }

  public List<UnitBox> getTargetBoxes() {
    return targetBoxes;
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
  public void apply(Switch sw) {
    ((StmtSwitch) sw).caseTableSwitchStmt(this);
  }

  @Override
  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    ((ConvertToBaf) getKey()).convertToBaf(context, out);

    final Baf vaf = Baf.v();
    final List<Unit> targets = getTargets();
    List<PlaceholderInst> targetPlaceholders = new ArrayList<PlaceholderInst>(targets.size());
    for (Unit target : targets) {
      targetPlaceholders.add(vaf.newPlaceholderInst(target));
    }

    Unit u = vaf.newTableSwitchInst(vaf.newPlaceholderInst(getDefaultTarget()), lowIndex, highIndex, targetPlaceholders);
    u.addAllTagsOf(this);
    out.add(u);
  }

  @Override
  public Unit getTargetForValue(int value) {

    final int high = highIndex;
    int tgtIdx = 0;
    // In this for-loop, we cannot use "<=" since 'i' would wrap around.
    // The case for "i == highIndex" is handled separately after the loop.
    for (int i = lowIndex; i < high; i++) {
      if (value == i) {
        return getTarget(tgtIdx);
      }
      tgtIdx++;
    }
    if (high == value) {
      return getTarget(tgtIdx);
    }
    return getDefaultTarget();
  }
}
