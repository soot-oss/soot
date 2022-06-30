package soot.dava.internal.javaRep;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.internal.AbstractDefinitionStmt;

public class DAssignStmt extends AbstractDefinitionStmt implements AssignStmt {

  public DAssignStmt(ValueBox left, ValueBox right) {
    super(left, right);
  }

  public Object clone() {
    return new DAssignStmt(leftBox, rightBox);
  }

  @Override
  public void setLeftOp(Value variable) {
    leftBox.setValue(variable);
  }

  @Override
  public void setRightOp(Value rvalue) {
    rightBox.setValue(rvalue);
  }

  public void toString(UnitPrinter up) {
    leftBox.toString(up);
    up.literal(" = ");
    rightBox.toString(up);
  }

  public String toString() {
    return leftBox.getValue().toString() + " = " + rightBox.getValue().toString();
  }

}
