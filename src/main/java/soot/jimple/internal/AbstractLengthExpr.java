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

import soot.IntType;
import soot.Type;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.jimple.ExprSwitch;
import soot.jimple.Jimple;
import soot.jimple.LengthExpr;
import soot.util.Switch;

@SuppressWarnings("serial")
public abstract class AbstractLengthExpr extends AbstractUnopExpr implements LengthExpr {
  protected AbstractLengthExpr(ValueBox opBox) {
    super(opBox);
  }

  public boolean equivTo(Object o) {
    if (o instanceof AbstractLengthExpr) {
      return opBox.getValue().equivTo(((AbstractLengthExpr) o).opBox.getValue());
    }
    return false;
  }

  /** Returns a hash code for this object, consistent with structural equality. */
  public int equivHashCode() {
    return opBox.getValue().equivHashCode();
  }

  public abstract Object clone();

  public String toString() {
    return Jimple.LENGTHOF + " " + opBox.getValue().toString();
  }

  public void toString(UnitPrinter up) {
    up.literal(Jimple.LENGTHOF);
    up.literal(" ");
    opBox.toString(up);
  }

  public Type getType() {
    return IntType.v();
  }

  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseLengthExpr(this);
  }
}
