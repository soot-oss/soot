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

import java.util.Collections;
import java.util.List;

import soot.Unit;
import soot.UnitBox;
import soot.UnitPrinter;
import soot.baf.Baf;
import soot.jimple.GotoStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleToBafContext;
import soot.jimple.StmtSwitch;
import soot.util.Switch;

public class JGotoStmt extends AbstractStmt implements GotoStmt {

  protected final UnitBox targetBox;
  protected final List<UnitBox> targetBoxes;

  public JGotoStmt(Unit target) {
    this(Jimple.v().newStmtBox(target));
  }

  public JGotoStmt(UnitBox box) {
    this.targetBox = box;
    this.targetBoxes = Collections.singletonList(box);
  }

  @Override
  public Object clone() {
    return new JGotoStmt(getTarget());
  }

  @Override
  public String toString() {
    Unit t = getTarget();
    String target = t.branches() ? "(branch)" : t.toString();
    return Jimple.GOTO + " [?= " + target + "]";
  }

  @Override
  public void toString(UnitPrinter up) {
    up.literal(Jimple.GOTO + " ");
    targetBox.toString(up);
  }

  @Override
  public Unit getTarget() {
    return targetBox.getUnit();
  }

  @Override
  public void setTarget(Unit target) {
    targetBox.setUnit(target);
  }

  @Override
  public UnitBox getTargetBox() {
    return targetBox;
  }

  @Override
  public List<UnitBox> getUnitBoxes() {
    return targetBoxes;
  }

  @Override
  public void apply(Switch sw) {
    ((StmtSwitch) sw).caseGotoStmt(this);
  }

  @Override
  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    final Baf vaf = Baf.v();
    Unit u = vaf.newGotoInst(vaf.newPlaceholderInst(getTarget()));
    u.addAllTagsOf(this);
    out.add(u);
  }

  @Override
  public boolean fallsThrough() {
    return false;
  }

  @Override
  public boolean branches() {
    return true;
  }
}
