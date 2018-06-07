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

import soot.Value;
import soot.grimp.Grimp;
import soot.jimple.ExprSwitch;
import soot.jimple.LeExpr;
import soot.util.Switch;

public class GLeExpr extends AbstractGrimpIntBinopExpr implements LeExpr {
  public GLeExpr(Value op1, Value op2) {
    super(op1, op2);
  }

  public final String getSymbol() {
    return " <= ";
  }

  public final int getPrecedence() {
    return 600;
  }

  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseLeExpr(this);
  }

  public Object clone() {
    return new GLeExpr(Grimp.cloneIfNecessary(getOp1()), Grimp.cloneIfNecessary(getOp2()));
  }

}
