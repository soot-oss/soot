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

import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.ShortType;
import soot.Type;
import soot.UnitPrinter;
import soot.UnknownType;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ExprSwitch;
import soot.jimple.Jimple;
import soot.jimple.NegExpr;
import soot.util.Switch;

@SuppressWarnings("serial")
public abstract class AbstractNegExpr extends AbstractUnopExpr implements NegExpr {
  protected AbstractNegExpr(ValueBox opBox) {
    super(opBox);
  }

  /** Compares the specified object with this one for structural equality. */
  public boolean equivTo(Object o) {
    if (o instanceof AbstractNegExpr) {
      return opBox.getValue().equivTo(((AbstractNegExpr) o).opBox.getValue());
    }
    return false;
  }

  /** Returns a hash code for this object, consistent with structural equality. */
  public int equivHashCode() {
    return opBox.getValue().equivHashCode();
  }

  public abstract Object clone();

  public String toString() {
    return Jimple.NEG + " " + opBox.getValue().toString();
  }

  public void toString(UnitPrinter up) {
    up.literal(Jimple.NEG);
    up.literal(" ");
    opBox.toString(up);
  }

  public Type getType() {
    Value op = opBox.getValue();

    if (op.getType().equals(IntType.v()) || op.getType().equals(ByteType.v()) || op.getType().equals(ShortType.v())
        || op.getType().equals(BooleanType.v()) || op.getType().equals(CharType.v())) {
      return IntType.v();
    } else if (op.getType().equals(LongType.v())) {
      return LongType.v();
    } else if (op.getType().equals(DoubleType.v())) {
      return DoubleType.v();
    } else if (op.getType().equals(FloatType.v())) {
      return FloatType.v();
    } else {
      return UnknownType.v();
    }
  }

  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseNegExpr(this);
  }
}
