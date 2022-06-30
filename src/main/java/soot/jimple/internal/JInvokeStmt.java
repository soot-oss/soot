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

import soot.Type;
import soot.Unit;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.baf.Baf;
import soot.jimple.ConvertToBaf;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleToBafContext;
import soot.jimple.StmtSwitch;
import soot.util.Switch;

public class JInvokeStmt extends AbstractStmt implements InvokeStmt {

  protected final ValueBox invokeExprBox;

  public JInvokeStmt(Value c) {
    this(Jimple.v().newInvokeExprBox(c));
  }

  protected JInvokeStmt(ValueBox invokeExprBox) {
    this.invokeExprBox = invokeExprBox;
  }

  @Override
  public Object clone() {
    return new JInvokeStmt(Jimple.cloneIfNecessary(getInvokeExpr()));
  }

  @Override
  public boolean containsInvokeExpr() {
    return true;
  }

  @Override
  public String toString() {
    return invokeExprBox.getValue().toString();
  }

  @Override
  public void toString(UnitPrinter up) {
    invokeExprBox.toString(up);
  }

  @Override
  public void setInvokeExpr(Value invokeExpr) {
    invokeExprBox.setValue(invokeExpr);
  }

  @Override
  public InvokeExpr getInvokeExpr() {
    return (InvokeExpr) invokeExprBox.getValue();
  }

  @Override
  public ValueBox getInvokeExprBox() {
    return invokeExprBox;
  }

  @Override
  public List<ValueBox> getUseBoxes() {
    List<ValueBox> list = new ArrayList<ValueBox>(invokeExprBox.getValue().getUseBoxes());
    list.add(invokeExprBox);
    return list;
  }

  @Override
  public void apply(Switch sw) {
    ((StmtSwitch) sw).caseInvokeStmt(this);
  }

  @Override
  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    InvokeExpr ie = getInvokeExpr();

    context.setCurrentUnit(this);

    ((ConvertToBaf) ie).convertToBaf(context, out);

    Type returnType = ie.getMethodRef().returnType();
    if (!VoidType.v().equals(returnType)) {
      Unit u = Baf.v().newPopInst(returnType);
      u.addAllTagsOf(this);
      out.add(u);
    }
  }

  @Override
  public boolean fallsThrough() {
    return true;
  }

  @Override
  public boolean branches() {
    return false;
  }
}
