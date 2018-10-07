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

import java.util.List;

import soot.Unit;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.baf.Baf;
import soot.jimple.ConvertToBaf;
import soot.jimple.Jimple;
import soot.jimple.JimpleToBafContext;
import soot.jimple.StmtSwitch;
import soot.jimple.ThrowStmt;
import soot.util.Switch;

public class JThrowStmt extends AbstractOpStmt implements ThrowStmt {

  public JThrowStmt(Value op) {
    this(Jimple.v().newImmediateBox(op));
  }

  protected JThrowStmt(ValueBox opBox) {
    super(opBox);
  }

  public Object clone() {
    return new JThrowStmt(Jimple.cloneIfNecessary(getOp()));
  }

  public String toString() {
    return "throw " + opBox.getValue().toString();
  }

  public void toString(UnitPrinter up) {
    up.literal(Jimple.THROW);
    up.literal(" ");
    opBox.toString(up);
  }

  public void apply(Switch sw) {
    ((StmtSwitch) sw).caseThrowStmt(this);
  }

  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    ((ConvertToBaf) getOp()).convertToBaf(context, out);

    Unit u = Baf.v().newThrowInst();
    u.addAllTagsOf(this);
    out.add(u);
  }

  public boolean fallsThrough() {
    return false;
  }

  public boolean branches() {
    return false;
  }

}
