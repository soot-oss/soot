package soot.dava.internal.javaRep;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
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
import soot.jimple.internal.AbstractLengthExpr;

public class DLengthExpr extends AbstractLengthExpr implements Precedence {
  public DLengthExpr(Value op) {
    super(Grimp.v().newObjExprBox(op));
  }

  public int getPrecedence() {
    return 950;
  }

  public Object clone() {
    return new DLengthExpr(Grimp.cloneIfNecessary(getOp()));
  }

  public void toString(UnitPrinter up) {
    if (PrecedenceTest.needsBrackets(getOpBox(), this)) {
      up.literal("(");
    }
    getOpBox().toString(up);
    if (PrecedenceTest.needsBrackets(getOpBox(), this)) {
      up.literal(")");
    }
    up.literal(".");
    up.literal("length");
  }

  public String toString() {
    StringBuffer b = new StringBuffer();
    if (PrecedenceTest.needsBrackets(getOpBox(), this)) {
      b.append("(");
    }
    b.append(getOpBox().getValue().toString());
    if (PrecedenceTest.needsBrackets(getOpBox(), this)) {
      b.append(")");
    }
    b.append(".length");

    return b.toString();
  }
}
