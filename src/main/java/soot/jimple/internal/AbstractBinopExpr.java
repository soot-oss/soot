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
import soot.grimp.PrecedenceTest;
import soot.jimple.Expr;

@SuppressWarnings("serial")
public abstract class AbstractBinopExpr implements Expr {

  protected final ValueBox op1Box;
  protected final ValueBox op2Box;

  protected AbstractBinopExpr(ValueBox op1Box, ValueBox op2Box) {
    this.op1Box = op1Box;
    this.op2Box = op2Box;
  }

  /** Returns the unique symbol for an operator. */
  protected abstract String getSymbol();

  @Override
  public abstract Object clone();

  public Value getOp1() {
    return op1Box.getValue();
  }

  public Value getOp2() {
    return op2Box.getValue();
  }

  public ValueBox getOp1Box() {
    return op1Box;
  }

  public ValueBox getOp2Box() {
    return op2Box;
  }

  public void setOp1(Value op1) {
    op1Box.setValue(op1);
  }

  public void setOp2(Value op2) {
    op2Box.setValue(op2);
  }

  @Override
  public final List<ValueBox> getUseBoxes() {
    List<ValueBox> list = new ArrayList<ValueBox>();

    list.addAll(op1Box.getValue().getUseBoxes());
    list.add(op1Box);

    list.addAll(op2Box.getValue().getUseBoxes());
    list.add(op2Box);

    return list;
  }

  @Override
  public boolean equivTo(Object o) {
    if (o instanceof AbstractBinopExpr) {
      AbstractBinopExpr abe = (AbstractBinopExpr) o;
      return this.op1Box.getValue().equivTo(abe.op1Box.getValue()) && this.op2Box.getValue().equivTo(abe.op2Box.getValue())
          && this.getSymbol().equals(abe.getSymbol());
    }
    return false;
  }

  /** Returns a hash code for this object, consistent with structural equality. */
  @Override
  public int equivHashCode() {
    return op1Box.getValue().equivHashCode() * 101 + op2Box.getValue().equivHashCode() + 17 ^ getSymbol().hashCode();
  }

  @Override
  public String toString() {
    return op1Box.getValue().toString() + getSymbol() + op2Box.getValue().toString();
  }

  @Override
  public void toString(UnitPrinter up) {
    {
      final boolean needsBrackets = PrecedenceTest.needsBrackets(op1Box, this);
      if (needsBrackets) {
        up.literal("(");
      }
      op1Box.toString(up);
      if (needsBrackets) {
        up.literal(")");
      }
    }

    up.literal(getSymbol());

    {
      final boolean needsBrackets = PrecedenceTest.needsBracketsRight(op2Box, this);
      if (needsBrackets) {
        up.literal("(");
      }
      op2Box.toString(up);
      if (needsBrackets) {
        up.literal(")");
      }
    }
  }
}
