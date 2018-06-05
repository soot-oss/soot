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

import soot.RefType;
import soot.SootMethodRef;
import soot.Type;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.grimp.Grimp;
import soot.grimp.GrimpValueSwitch;
import soot.grimp.NewInvokeExpr;
import soot.grimp.Precedence;
import soot.jimple.internal.AbstractInvokeExpr;
import soot.util.Switch;

public class GNewInvokeExpr extends AbstractInvokeExpr implements NewInvokeExpr, Precedence {
  RefType type;

  public GNewInvokeExpr(RefType type, SootMethodRef methodRef, List args) {
    super(methodRef, new ExprBox[args.size()]);

    if (methodRef != null && methodRef.isStatic()) {
      throw new RuntimeException("wrong static-ness");
    }

    this.methodRef = methodRef;
    this.type = type;

    for (int i = 0; i < args.size(); i++) {
      this.argBoxes[i] = Grimp.v().newExprBox((Value) args.get(i));
    }
  }

  /*
   * protected GNewInvokeExpr(RefType type, ExprBox[] argBoxes) { this.type = type; this.argBoxes = argBoxes; }
   */

  public RefType getBaseType() {
    return type;
  }

  public void setBaseType(RefType type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public int getPrecedence() {
    return 850;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();

    buffer.append("new " + type.toString() + "(");

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
    up.literal("new");
    up.literal(" ");
    up.type(type);
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

  public void apply(Switch sw) {
    ((GrimpValueSwitch) sw).caseNewInvokeExpr(this);
  }

  public Object clone() {
    ArrayList clonedArgs = new ArrayList(getArgCount());

    for (int i = 0; i < getArgCount(); i++) {
      clonedArgs.add(i, Grimp.cloneIfNecessary(getArg(i)));

    }

    return new GNewInvokeExpr(getBaseType(), methodRef, clonedArgs);
  }

  public boolean equivTo(Object o) {
    if (o instanceof GNewInvokeExpr) {
      GNewInvokeExpr ie = (GNewInvokeExpr) o;
      if (!(getMethod().equals(ie.getMethod())
          && (argBoxes == null ? 0 : argBoxes.length) == (ie.argBoxes == null ? 0 : ie.argBoxes.length))) {
        return false;
      }
      if (argBoxes != null) {
        for (ValueBox element : argBoxes) {
          if (!(element.getValue().equivTo(element.getValue()))) {
            return false;
          }
        }
      }
      if (!type.equals(ie.type)) {
        return false;
      }
      return true;
    }
    return false;
  }

  /** Returns a hash code for this object, consistent with structural equality. */
  public int equivHashCode() {
    return getMethod().equivHashCode();
  }
}
