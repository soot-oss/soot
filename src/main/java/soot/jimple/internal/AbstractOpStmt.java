package soot.jimple.internal;

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

import java.util.ArrayList;
import java.util.List;

import soot.Value;
import soot.ValueBox;

@SuppressWarnings("serial")
public abstract class AbstractOpStmt extends AbstractStmt {

  final ValueBox opBox;

  protected AbstractOpStmt(ValueBox opBox) {
    this.opBox = opBox;
  }

  final public Value getOp() {
    return opBox.getValue();
  }

  final public void setOp(Value op) {
    opBox.setValue(op);
  }

  final public ValueBox getOpBox() {
    return opBox;
  }

  @Override
  final public List<ValueBox> getUseBoxes() {
    List<ValueBox> list = new ArrayList<ValueBox>();

    list.addAll(opBox.getValue().getUseBoxes());
    list.add(opBox);

    return list;
  }
}
