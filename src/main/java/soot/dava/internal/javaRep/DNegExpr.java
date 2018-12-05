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
import soot.jimple.internal.AbstractNegExpr;

public class DNegExpr extends AbstractNegExpr {
  public DNegExpr(Value op) {
    super(Grimp.v().newExprBox(op));
  }

  public Object clone() {
    return new DNegExpr(Grimp.cloneIfNecessary(getOp()));
  }

  public void toString(UnitPrinter up) {
    up.literal("(");
    up.literal("-");
    up.literal(" ");
    up.literal("(");
    getOpBox().toString(up);
    up.literal(")");
    up.literal(")");
  }

  public String toString() {
    return "(- (" + (getOpBox().getValue()).toString() + "))";
  }
}
