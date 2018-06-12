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
  final UnitBox targetBox;
  final List<UnitBox> targetBoxes;

  public JGotoStmt(Unit target) {
    this(Jimple.v().newStmtBox(target));
  }

  public JGotoStmt(UnitBox box) {
    targetBox = box;
    targetBoxes = Collections.singletonList(box);
  }

  public Object clone() {
    return new JGotoStmt(getTarget());
  }

  public String toString() {
    Unit t = getTarget();
    String target = "(branch)";
    if (!t.branches()) {
      target = t.toString();
    }
    return Jimple.GOTO + " [?= " + target + "]";
  }

  public void toString(UnitPrinter up) {
    up.literal(Jimple.GOTO);
    up.literal(" ");
    targetBox.toString(up);
  }

  public Unit getTarget() {
    return targetBox.getUnit();
  }

  public void setTarget(Unit target) {
    targetBox.setUnit(target);
  }

  public UnitBox getTargetBox() {
    return targetBox;
  }

  public List<UnitBox> getUnitBoxes() {
    return targetBoxes;
  }

  public void apply(Switch sw) {
    ((StmtSwitch) sw).caseGotoStmt(this);
  }

  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    Unit u = Baf.v().newGotoInst(Baf.v().newPlaceholderInst(getTarget()));
    u.addAllTagsOf(this);
    out.add(u);
  }

  public boolean fallsThrough() {
    return false;
  }

  public boolean branches() {
    return true;
  }

}
