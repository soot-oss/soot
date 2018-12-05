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

import soot.SootMethodRef;
import soot.Unit;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.baf.Baf;
import soot.jimple.ConvertToBaf;
import soot.jimple.ExprSwitch;
import soot.jimple.Jimple;
import soot.jimple.JimpleToBafContext;
import soot.jimple.VirtualInvokeExpr;
import soot.util.Switch;

@SuppressWarnings("serial")
public abstract class AbstractVirtualInvokeExpr extends AbstractInstanceInvokeExpr
    implements VirtualInvokeExpr, ConvertToBaf {
  protected AbstractVirtualInvokeExpr(ValueBox baseBox, SootMethodRef methodRef, ValueBox[] argBoxes) {
    super(methodRef, baseBox, argBoxes);
    if (methodRef.isStatic()) {
      throw new RuntimeException("wrong static-ness");
    }
  }

  public boolean equivTo(Object o) {
    if (o instanceof AbstractVirtualInvokeExpr) {
      AbstractVirtualInvokeExpr ie = (AbstractVirtualInvokeExpr) o;
      if (!(baseBox.getValue().equivTo(ie.baseBox.getValue()) && getMethod().equals(ie.getMethod())
          && (argBoxes == null ? 0 : argBoxes.length) == (ie.argBoxes == null ? 0 : ie.argBoxes.length))) {
        return false;
      }
      if (argBoxes != null) {
        for (int i = 0; i < argBoxes.length; i++) {
          if (!(argBoxes[i]).getValue().equivTo(ie.argBoxes[i].getValue())) {
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
  public int equivHashCode() {
    return baseBox.getValue().equivHashCode() * 101 + getMethod().equivHashCode() * 17;
  }

  public abstract Object clone();

  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseVirtualInvokeExpr(this);
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();

    buffer.append(Jimple.VIRTUALINVOKE + " " + baseBox.getValue().toString() + "." + methodRef.getSignature() + "(");

    if (argBoxes != null) {
      for (int i = 0; i < argBoxes.length; i++) {
        if (i != 0) {
          buffer.append(", ");
        }

        buffer.append(argBoxes[i].getValue().toString());
      }
    }

    buffer.append(")");

    return buffer.toString();
  }

  public void toString(UnitPrinter up) {
    up.literal(Jimple.VIRTUALINVOKE);
    up.literal(" ");
    baseBox.toString(up);
    up.literal(".");
    up.methodRef(methodRef);
    up.literal("(");

    if (argBoxes != null) {
      for (int i = 0; i < argBoxes.length; i++) {
        if (i != 0) {
          up.literal(", ");
        }

        argBoxes[i].toString(up);
      }
    }

    up.literal(")");
  }

  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    ((ConvertToBaf) (getBase())).convertToBaf(context, out);

    if (argBoxes != null) {
      for (ValueBox element : argBoxes) {
        ((ConvertToBaf) (element.getValue())).convertToBaf(context, out);
      }
    }

    Unit u = Baf.v().newVirtualInvokeInst(methodRef);
    out.add(u);
    u.addAllTagsOf(context.getCurrentUnit());
  }
}
