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

import soot.IntType;
import soot.Type;
import soot.Value;
import soot.grimp.Grimp;
import soot.grimp.internal.AbstractGrimpIntBinopExpr;
import soot.jimple.CmplExpr;
import soot.jimple.ExprSwitch;
import soot.util.Switch;

public class DCmplExpr extends AbstractGrimpIntBinopExpr implements CmplExpr {
  public DCmplExpr(Value op1, Value op2) {
    super(op1, op2);
  }

  public final String getSymbol() {
    return " - ";
  }

  public final int getPrecedence() {
    return 700;
  }

  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseCmplExpr(this);
  }

  public Object clone() {
    return new DCmplExpr(Grimp.cloneIfNecessary(getOp1()), Grimp.cloneIfNecessary(getOp2()));
  }

  public Type getType() {
    if (getOp1().getType().equals(getOp2().getType())) {
      return getOp1().getType();
    }

    return IntType.v();
  }
}
