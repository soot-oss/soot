package soot.jimple.internal;

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

import java.util.List;
import java.util.ListIterator;

import soot.SootMethodRef;
import soot.Unit;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.baf.Baf;
import soot.jimple.ConvertToBaf;
import soot.jimple.ExprSwitch;
import soot.jimple.Jimple;
import soot.jimple.JimpleToBafContext;
import soot.jimple.StaticInvokeExpr;
import soot.util.Switch;

@SuppressWarnings("serial")
public abstract class AbstractStaticInvokeExpr extends AbstractInvokeExpr implements StaticInvokeExpr, ConvertToBaf {

  AbstractStaticInvokeExpr(SootMethodRef methodRef, List<Value> args) {
    this(methodRef, new ValueBox[args.size()]);

    final Jimple jimp = Jimple.v();
    for (ListIterator<Value> it = args.listIterator(); it.hasNext();) {
      Value v = it.next();
      this.argBoxes[it.previousIndex()] = jimp.newImmediateBox(v);
    }
  }

  protected AbstractStaticInvokeExpr(SootMethodRef methodRef, ValueBox[] argBoxes) {
    super(methodRef, argBoxes);
    if (!methodRef.isStatic()) {
      throw new RuntimeException("wrong static-ness");
    }
  }

  @Override
  public boolean equivTo(Object o) {
    if (o instanceof AbstractStaticInvokeExpr) {
      AbstractStaticInvokeExpr ie = (AbstractStaticInvokeExpr) o;
      if ((this.argBoxes == null ? 0 : this.argBoxes.length) != (ie.argBoxes == null ? 0 : ie.argBoxes.length)
          || !this.getMethod().equals(ie.getMethod())) {
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

  /**
   * Returns a hash code for this object, consistent with structural equality.
   */
  @Override
  public int equivHashCode() {
    return getMethod().equivHashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(Jimple.STATICINVOKE + " ");

    buf.append(methodRef.getSignature()).append('(');
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
    up.literal(Jimple.STATICINVOKE + " ");
    up.methodRef(methodRef);
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
    ((ExprSwitch) sw).caseStaticInvokeExpr(this);
  }

  @Override
  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    if (argBoxes != null) {
      for (ValueBox element : argBoxes) {
        ((ConvertToBaf) (element.getValue())).convertToBaf(context, out);
      }
    }

    Unit u = Baf.v().newStaticInvokeInst(methodRef);
    out.add(u);
    u.addAllTagsOf(context.getCurrentUnit());
  }
}
