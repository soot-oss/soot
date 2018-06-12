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

import soot.BooleanType;
import soot.Type;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ExprSwitch;
import soot.jimple.InstanceOfExpr;
import soot.jimple.Jimple;
import soot.util.Switch;

@SuppressWarnings("serial")
public abstract class AbstractInstanceOfExpr implements InstanceOfExpr {
  final ValueBox opBox;
  Type checkType;

  protected AbstractInstanceOfExpr(ValueBox opBox, Type checkType) {
    this.opBox = opBox;
    this.checkType = checkType;
  }

  public boolean equivTo(Object o) {
    if (o instanceof AbstractInstanceOfExpr) {
      AbstractInstanceOfExpr aie = (AbstractInstanceOfExpr) o;
      return opBox.getValue().equivTo(aie.opBox.getValue()) && checkType.equals(aie.checkType);
    }
    return false;
  }

  /** Returns a hash code for this object, consistent with structural equality. */
  public int equivHashCode() {
    return opBox.getValue().equivHashCode() * 101 + checkType.hashCode() * 17;
  }

  public abstract Object clone();

  public String toString() {
    return opBox.getValue().toString() + " " + Jimple.INSTANCEOF + " " + checkType.toString();
  }

  public void toString(UnitPrinter up) {
    opBox.toString(up);
    up.literal(" ");
    up.literal(Jimple.INSTANCEOF);
    up.literal(" ");
    up.type(checkType);
  }

  @Override
  public Value getOp() {
    return opBox.getValue();
  }

  @Override
  public void setOp(Value op) {
    opBox.setValue(op);
  }

  @Override
  public ValueBox getOpBox() {
    return opBox;
  }

  @Override
  public final List<ValueBox> getUseBoxes() {
    List<ValueBox> list = new ArrayList<ValueBox>();

    list.addAll(opBox.getValue().getUseBoxes());
    list.add(opBox);

    return list;
  }

  public Type getType() {
    return BooleanType.v();
  }

  public Type getCheckType() {
    return checkType;
  }

  public void setCheckType(Type checkType) {
    this.checkType = checkType;
  }

  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseInstanceOfExpr(this);
  }
}
