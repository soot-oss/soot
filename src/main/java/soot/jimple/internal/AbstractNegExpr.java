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
  @Override
  public boolean equivTo(Object o) {
    if (o instanceof AbstractNegExpr) {
      return this.opBox.getValue().equivTo(((AbstractNegExpr) o).opBox.getValue());
    }
    return false;
  }

  /** Returns a hash code for this object, consistent with structural equality. */
  @Override
  public int equivHashCode() {
    return opBox.getValue().equivHashCode();
  }

  @Override
  public String toString() {
    return Jimple.NEG + " " + opBox.getValue().toString();
  }

  @Override
  public void toString(UnitPrinter up) {
    up.literal(Jimple.NEG + " ");
    opBox.toString(up);
  }

  @Override
  public Type getType() {
    final Type type = opBox.getValue().getType();

    final IntType tyInt = IntType.v();
    final ByteType tyByte = ByteType.v();
    final ShortType tyShort = ShortType.v();
    final CharType tyChar = CharType.v();
    final BooleanType tyBool = BooleanType.v();
    if (tyInt.equals(type) || tyByte.equals(type) || tyShort.equals(type) || tyChar.equals(type) || tyBool.equals(type)) {
      return tyInt;
    }
    final LongType tyLong = LongType.v();
    if (tyLong.equals(type)) {
      return tyLong;
    }
    final DoubleType tyDouble = DoubleType.v();
    if (tyDouble.equals(type)) {
      return tyDouble;
    }
    final FloatType tyFloat = FloatType.v();
    if (tyFloat.equals(type)) {
      return tyFloat;
    }
    return UnknownType.v();
  }

  @Override
  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseNegExpr(this);
  }
}
