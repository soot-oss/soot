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

import soot.SootMethod;
import soot.SootMethodRef;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.grimp.Grimp;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.ExprSwitch;
import soot.jimple.Jimple;
import soot.jimple.internal.AbstractInvokeExpr;
import soot.util.Switch;

@SuppressWarnings("serial")
public class GDynamicInvokeExpr extends AbstractInvokeExpr implements DynamicInvokeExpr {

  protected final SootMethodRef bsmRef;
  protected final ValueBox[] bsmArgBoxes;
  protected final int tag;

  public GDynamicInvokeExpr(SootMethodRef bootStrapMethodRef, List<? extends Value> bootstrapArgs, SootMethodRef methodRef,
      int tag, List<? extends Value> methodArgs) {
    super(methodRef, new ValueBox[methodArgs.size()]);

    this.bsmRef = bootStrapMethodRef;
    this.bsmArgBoxes = new ValueBox[bootstrapArgs.size()];
    this.tag = tag;

    final Grimp grmp = Grimp.v();
    for (ListIterator<? extends Value> it = bootstrapArgs.listIterator(); it.hasNext();) {
      Value v = it.next();
      this.bsmArgBoxes[it.previousIndex()] = grmp.newExprBox(v);
    }
    for (ListIterator<? extends Value> it = methodArgs.listIterator(); it.hasNext();) {
      Value v = it.next();
      this.argBoxes[it.previousIndex()] = grmp.newExprBox(v);
    }
  }

  @Override
  public Object clone() {
    List<Value> clonedBsmArgs = new ArrayList<Value>(bsmArgBoxes.length);
    for (ValueBox box : bsmArgBoxes) {
      clonedBsmArgs.add(box.getValue());
    }

    final int count = getArgCount();
    List<Value> clonedArgs = new ArrayList<Value>(count);
    for (int i = 0; i < count; i++) {
      clonedArgs.add(Grimp.cloneIfNecessary(getArg(i)));
    }

    return new GDynamicInvokeExpr(bsmRef, clonedBsmArgs, methodRef, tag, clonedArgs);
  }

  @Override
  public int getBootstrapArgCount() {
    return bsmArgBoxes.length;
  }

  @Override
  public Value getBootstrapArg(int i) {
    return bsmArgBoxes[i].getValue();
  }

  @Override
  public List<Value> getBootstrapArgs() {
    List<Value> l = new ArrayList<Value>();
    for (ValueBox element : bsmArgBoxes) {
      l.add(element.getValue());
    }
    return l;
  }

  @Override
  public SootMethodRef getBootstrapMethodRef() {
    return bsmRef;
  }

  public SootMethod getBootstrapMethod() {
    return bsmRef.resolve();
  }

  @Override
  public int getHandleTag() {
    return tag;
  }

  @Override
  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseDynamicInvokeExpr(this);
  }

  @Override
  public boolean equivTo(Object o) {
    if (o instanceof GDynamicInvokeExpr) {
      GDynamicInvokeExpr ie = (GDynamicInvokeExpr) o;
      if ((this.argBoxes == null ? 0 : this.argBoxes.length) != (ie.argBoxes == null ? 0 : ie.argBoxes.length)
          || this.bsmArgBoxes.length != ie.bsmArgBoxes.length || !this.getMethod().equals(ie.getMethod())
          || !this.methodRef.equals(ie.methodRef) || !this.bsmRef.equals(ie.bsmRef)) {
        return false;
      }
      int i = 0;
      for (ValueBox element : this.bsmArgBoxes) {
        if (!element.getValue().equivTo(ie.getBootstrapArg(i))) {
          return false;
        }
        i++;
      }
      if (this.argBoxes != null) {
        i = 0;
        for (ValueBox element : this.argBoxes) {
          if (!element.getValue().equivTo(ie.getArg(i))) {
            return false;
          }
          i++;
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public int equivHashCode() {
    return getBootstrapMethod().equivHashCode() * getMethod().equivHashCode() * 17;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(Jimple.DYNAMICINVOKE + " \"");

    buf.append(methodRef.name()); // quoted method name (can be any UTF8 string)
    buf.append("\" <");
    buf.append(SootMethod.getSubSignature(""/* no method name here */, methodRef.parameterTypes(), methodRef.returnType()));
    buf.append(">(");

    if (argBoxes != null) {
      for (int i = 0, e = argBoxes.length; i < e; i++) {
        if (i != 0) {
          buf.append(", ");
        }
        buf.append(argBoxes[i].getValue().toString());
      }
    }
    buf.append(") ");

    buf.append(bsmRef.getSignature());
    buf.append('(');
    for (int i = 0, e = bsmArgBoxes.length; i < e; i++) {
      if (i != 0) {
        buf.append(", ");
      }
      buf.append(bsmArgBoxes[i].getValue().toString());
    }
    buf.append(')');

    return buf.toString();
  }

  @Override
  public void toString(UnitPrinter up) {
    up.literal(Jimple.DYNAMICINVOKE + " \"" + methodRef.name() + "\" <"
        + SootMethod.getSubSignature(""/* no method name here */, methodRef.parameterTypes(), methodRef.returnType())
        + ">(");

    if (argBoxes != null) {
      for (int i = 0, e = argBoxes.length; i < e; i++) {
        if (i != 0) {
          up.literal(", ");
        }
        argBoxes[i].toString(up);
      }
    }
    up.literal(") ");

    up.methodRef(bsmRef);
    up.literal("(");
    for (int i = 0, e = bsmArgBoxes.length; i < e; i++) {
      if (i != 0) {
        up.literal(", ");
      }
      bsmArgBoxes[i].toString(up);
    }
    up.literal(")");
  }
}
