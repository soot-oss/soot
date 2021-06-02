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

import java.util.ArrayList;
import java.util.List;

import soot.ArrayType;
import soot.RefType;
import soot.Type;
import soot.Unit;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.baf.Baf;
import soot.grimp.PrecedenceTest;
import soot.jimple.CastExpr;
import soot.jimple.ConvertToBaf;
import soot.jimple.ExprSwitch;
import soot.jimple.Jimple;
import soot.jimple.JimpleToBafContext;
import soot.util.Switch;

@SuppressWarnings("serial")
public abstract class AbstractCastExpr implements CastExpr, ConvertToBaf {

  protected final ValueBox opBox;
  protected Type type;

  AbstractCastExpr(Value op, Type type) {
    this(Jimple.v().newImmediateBox(op), type);
  }

  @Override
  public abstract Object clone();

  protected AbstractCastExpr(ValueBox opBox, Type type) {
    this.opBox = opBox;
    this.type = type;
  }

  @Override
  public boolean equivTo(Object o) {
    if (o instanceof AbstractCastExpr) {
      AbstractCastExpr ace = (AbstractCastExpr) o;
      return this.opBox.getValue().equivTo(ace.opBox.getValue()) && this.type.equals(ace.type);
    }
    return false;
  }

  /** Returns a hash code for this object, consistent with structural equality. */
  @Override
  public int equivHashCode() {
    return opBox.getValue().equivHashCode() * 101 + type.hashCode() + 17;
  }

  @Override
  public String toString() {
    return "(" + type.toString() + ") " + opBox.getValue().toString();
  }

  @Override
  public void toString(UnitPrinter up) {
    up.literal("(");
    up.type(type);
    up.literal(") ");
    final boolean needsBrackets = PrecedenceTest.needsBrackets(opBox, this);
    if (needsBrackets) {
      up.literal("(");
    }
    opBox.toString(up);
    if (needsBrackets) {
      up.literal(")");
    }
  }

  @Override
  public Value getOp() {
    return opBox.getValue();
  }

  @Override
  public void setOp(Value op) {
    opBox.setValue(op);
  }

  @Override
  public ValueBox getOpBox() {
    return opBox;
  }

  @Override
  public final List<ValueBox> getUseBoxes() {
    List<ValueBox> list = new ArrayList<ValueBox>(opBox.getValue().getUseBoxes());
    list.add(opBox);
    return list;
  }

  @Override
  public Type getCastType() {
    return type;
  }

  @Override
  public void setCastType(Type castType) {
    this.type = castType;
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseCastExpr(this);
  }

  @Override
  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    ((ConvertToBaf) getOp()).convertToBaf(context, out);

    Unit u;
    final Type toType = getCastType();
    if (toType instanceof ArrayType || toType instanceof RefType) {
      u = Baf.v().newInstanceCastInst(toType);
    } else {
      final Type fromType = getOp().getType();
      if (!fromType.equals(toType)) {
        u = Baf.v().newPrimitiveCastInst(fromType, toType);
      } else {
        u = Baf.v().newNopInst();
      }
    }

    out.add(u);

    u.addAllTagsOf(context.getCurrentUnit());
  }
}
