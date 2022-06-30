package soot.grimp.internal;

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

import soot.UnitPrinter;
import soot.Value;
import soot.grimp.Grimp;
import soot.grimp.Precedence;
import soot.grimp.PrecedenceTest;
import soot.jimple.internal.JArrayRef;

public class GArrayRef extends JArrayRef implements Precedence {

  public GArrayRef(Value base, Value index) {
    super(Grimp.v().newObjExprBox(base), Grimp.v().newExprBox(index));
  }

  @Override
  public int getPrecedence() {
    return 950;
  }

  @Override
  public void toString(UnitPrinter up) {
    final boolean needsBrackets = PrecedenceTest.needsBrackets(baseBox, this);
    if (needsBrackets) {
      up.literal("(");
    }
    baseBox.toString(up);
    if (needsBrackets) {
      up.literal(")");
    }
    up.literal("[");
    indexBox.toString(up);
    up.literal("]");
  }

  @Override
  public String toString() {
    final Value op1 = getBase();
    String leftOp = op1.toString();
    if (op1 instanceof Precedence && ((Precedence) op1).getPrecedence() < getPrecedence()) {
      leftOp = "(" + leftOp + ")";
    }
    return leftOp + "[" + getIndex().toString() + "]";
  }

  @Override
  public Object clone() {
    return new GArrayRef(Grimp.cloneIfNecessary(getBase()), Grimp.cloneIfNecessary(getIndex()));
  }
}
