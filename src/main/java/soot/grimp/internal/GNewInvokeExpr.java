package soot.grimp.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
 * Copyright (C) 2004 Ondrej Lhotak
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
import java.util.ListIterator;

import soot.RefType;
import soot.SootMethodRef;
import soot.Type;
import soot.UnitPrinter;
import soot.Value;
import soot.grimp.Grimp;
import soot.grimp.GrimpValueSwitch;
import soot.grimp.NewInvokeExpr;
import soot.grimp.Precedence;
import soot.jimple.internal.AbstractInvokeExpr;
import soot.util.Switch;

public class GNewInvokeExpr extends AbstractInvokeExpr implements NewInvokeExpr, Precedence {

  protected RefType type;

  public GNewInvokeExpr(RefType type, SootMethodRef methodRef, List<? extends Value> args) {
    super(methodRef, new ExprBox[args.size()]);

    if (methodRef != null && methodRef.isStatic()) {
      throw new RuntimeException("wrong static-ness");
    }

    this.type = type;

    final Grimp grmp = Grimp.v();
    for (ListIterator<? extends Value> it = args.listIterator(); it.hasNext();) {
      Value v = it.next();
      this.argBoxes[it.previousIndex()] = grmp.newExprBox(v);
    }
  }

  @Override
  public RefType getBaseType() {
    return type;
  }

  @Override
  public void setBaseType(RefType type) {
    this.type = type;
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public int getPrecedence() {
    return 850;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("new ");

    buf.append(type.toString()).append('(');
    if (argBoxes != null) {
      for (int i = 0, e = argBoxes.length; i < e; i++) {
        if (i != 0) {
          buf.append(", ");
        }
        buf.append(argBoxes[i].getValue().toString());
      }
    }
    buf.append(')');

    return buf.toString();
  }

  @Override
  public void toString(UnitPrinter up) {
    up.literal("new ");
    up.type(type);
    up.literal("(");
    if (argBoxes != null) {
      for (int i = 0, e = argBoxes.length; i < e; i++) {
        if (i != 0) {
          up.literal(", ");
        }
        argBoxes[i].toString(up);
      }
    }
    up.literal(")");
  }

  @Override
  public void apply(Switch sw) {
    ((GrimpValueSwitch) sw).caseNewInvokeExpr(this);
  }

  @Override
  public Object clone() {
    final int count = getArgCount();
    List<Value> clonedArgs = new ArrayList<Value>(count);
    for (int i = 0; i < count; i++) {
      clonedArgs.add(Grimp.cloneIfNecessary(getArg(i)));
    }
    return new GNewInvokeExpr(getBaseType(), methodRef, clonedArgs);
  }

  @Override
  public boolean equivTo(Object o) {
    if (o instanceof GNewInvokeExpr) {
      GNewInvokeExpr ie = (GNewInvokeExpr) o;
      if ((this.argBoxes == null ? 0 : this.argBoxes.length) != (ie.argBoxes == null ? 0 : ie.argBoxes.length)
          || !this.getMethod().equals(ie.getMethod()) || !this.type.equals(ie.type)) {
        return false;
      }
      if (this.argBoxes != null) {
        for (int i = 0, e = this.argBoxes.length; i < e; i++) {
          if (!this.argBoxes[i].getValue().equivTo(ie.argBoxes[i].getValue())) {
            return false;
          }
        }
      }
      return true;
    }
    return false;
  }

  /** Returns a hash code for this object, consistent with structural equality. */
  @Override
  public int equivHashCode() {
    return getMethod().equivHashCode();
  }
}
