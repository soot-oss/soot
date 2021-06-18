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

import soot.Type;
import soot.Unit;
import soot.Value;
import soot.baf.Baf;
import soot.jimple.CmpExpr;
import soot.jimple.ExprSwitch;
import soot.jimple.Jimple;
import soot.util.Switch;

public class JCmpExpr extends AbstractJimpleIntBinopExpr implements CmpExpr {

  public JCmpExpr(Value op1, Value op2) {
    super(op1, op2);
  }

  @Override
  public final String getSymbol() {
    return " " + Jimple.CMP + " ";
  }

  @Override
  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseCmpExpr(this);
  }

  @Override
  protected Unit makeBafInst(Type opType) {
    return Baf.v().newCmpInst(this.getOp1().getType());
  }

  @Override
  public Object clone() {
    return new JCmpExpr(Jimple.cloneIfNecessary(getOp1()), Jimple.cloneIfNecessary(getOp2()));
  }
}
