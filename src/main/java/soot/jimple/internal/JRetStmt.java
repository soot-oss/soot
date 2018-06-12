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

import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Jimple;
import soot.jimple.RetStmt;
import soot.jimple.StmtSwitch;
import soot.util.Switch;

public class JRetStmt extends AbstractStmt implements RetStmt {
  final ValueBox stmtAddressBox;
  // List useBoxes;

  public JRetStmt(Value stmtAddress) {
    this(Jimple.v().newLocalBox(stmtAddress));
  }

  protected JRetStmt(ValueBox stmtAddressBox) {
    this.stmtAddressBox = stmtAddressBox;

  }

  public Object clone() {
    return new JRetStmt(Jimple.cloneIfNecessary(getStmtAddress()));
  }

  public String toString() {
    return Jimple.RET + " " + stmtAddressBox.getValue().toString();
  }

  public void toString(UnitPrinter up) {
    up.literal(Jimple.RET);
    up.literal(" ");
    stmtAddressBox.toString(up);
  }

  public Value getStmtAddress() {
    return stmtAddressBox.getValue();
  }

  public ValueBox getStmtAddressBox() {
    return stmtAddressBox;
  }

  public void setStmtAddress(Value stmtAddress) {
    stmtAddressBox.setValue(stmtAddress);
  }

  public List<ValueBox> getUseBoxes() {
    List<ValueBox> useBoxes = new ArrayList<ValueBox>();

    useBoxes.addAll(stmtAddressBox.getValue().getUseBoxes());
    useBoxes.add(stmtAddressBox);

    return useBoxes;
  }

  public void apply(Switch sw) {
    ((StmtSwitch) sw).caseRetStmt(this);
  }

  public boolean fallsThrough() {
    return true;
  }

  public boolean branches() {
    return false;
  }

}
