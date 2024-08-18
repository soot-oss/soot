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
import soot.LongType;
import soot.Type;
import soot.ULongType;
import soot.Unit;
import soot.UnknownType;
import soot.Value;
import soot.baf.Baf;
import soot.jimple.ExprSwitch;
import soot.jimple.Jimple;
import soot.jimple.ShlExpr;
import soot.util.Switch;

public class JShlExpr extends AbstractJimpleIntLongBinopExpr implements ShlExpr {

  public JShlExpr(Value op1, Value op2) {
    super(op1, op2);
  }

  @Override
  public String getSymbol() {
    return " << ";
  }

  @Override
  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseShlExpr(this);
  }

  @Override
  protected Unit makeBafInst(Type opType) {
    return Baf.v().newShlInst(this.getOp1().getType());
  }

  @Override
  public Type getType() {
    if (isIntLikeType(op2Box.getValue().getType())) {
      final Type t1 = op1Box.getValue().getType();
      if (isIntLikeType(t1)) {
        return IntType.v();
      }
      final LongType tyLong = LongType.v();
      if (tyLong.equals(t1)) {
        return tyLong;
      }
      final ULongType tyuLong = ULongType.v();
      if (tyuLong.equals(t1)) {
        return tyuLong;
      }
    }
    return UnknownType.v();
  }

  @Override
  public Object clone() {
    return new JShlExpr(Jimple.cloneIfNecessary(getOp1()), Jimple.cloneIfNecessary(getOp2()));
  }
}
