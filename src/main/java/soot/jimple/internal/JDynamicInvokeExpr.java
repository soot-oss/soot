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

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;

import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.baf.Baf;
import soot.jimple.ConvertToBaf;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.ExprSwitch;
import soot.jimple.Jimple;
import soot.jimple.JimpleToBafContext;
import soot.util.Switch;

@SuppressWarnings("serial")
public class JDynamicInvokeExpr extends AbstractInvokeExpr implements DynamicInvokeExpr, ConvertToBaf {
  protected SootMethodRef bsmRef;
  protected ValueBox[] bsmArgBoxes;
  protected int tag;

  public JDynamicInvokeExpr(SootMethodRef bootstrapMethodRef, List<? extends Value> bootstrapArgs, SootMethodRef methodRef,
      int tag, List<? extends Value> methodArgs) {
    super(methodRef, new ValueBox[methodArgs.size()]);

    if (!methodRef.getSignature().startsWith("<" + SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME + ": ")) {
      throw new IllegalArgumentException(
          "Receiver type of JDynamicInvokeExpr must be " + SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME + "!");
    }

    this.bsmRef = bootstrapMethodRef;
    this.bsmArgBoxes = new ValueBox[bootstrapArgs.size()];
    this.tag = tag;

    for (int i = 0; i < bootstrapArgs.size(); i++) {
      this.bsmArgBoxes[i] = Jimple.v().newImmediateBox(bootstrapArgs.get(i));
    }
    for (int i = 0; i < methodArgs.size(); i++) {
      this.argBoxes[i] = Jimple.v().newImmediateBox(methodArgs.get(i));
    }
  }

  public JDynamicInvokeExpr(SootMethodRef bootstrapMethodRef, List<? extends Value> bootstrapArgs, SootMethodRef methodRef,
      List<? extends Value> methodArgs) {
    /*
     * Here the static-handle is chosen as default value, because this works for Java.
     */
    this(bootstrapMethodRef, bootstrapArgs, methodRef, Opcodes.H_INVOKESTATIC, methodArgs);
  }

  public int getBootstrapArgCount() {
    return bsmArgBoxes.length;
  }

  public Value getBootstrapArg(int index) {
    return bsmArgBoxes[index].getValue();
  }

  public Object clone() {
    List<Value> clonedBsmArgs = new ArrayList<Value>(getBootstrapArgCount());
    for (int i = 0; i < getBootstrapArgCount(); i++) {
      clonedBsmArgs.add(i, getBootstrapArg(i));
    }

    List<Value> clonedArgs = new ArrayList<Value>(getArgCount());
    for (int i = 0; i < getArgCount(); i++) {
      clonedArgs.add(i, getArg(i));
    }

    return new JDynamicInvokeExpr(bsmRef, clonedBsmArgs, methodRef, tag, clonedArgs);
  }

  public boolean equivTo(Object o) {
    if (o instanceof JDynamicInvokeExpr) {
      JDynamicInvokeExpr ie = (JDynamicInvokeExpr) o;
      if (!(getMethod().equals(ie.getMethod()) && bsmArgBoxes.length == ie.bsmArgBoxes.length)) {
        return false;
      }
      int i = 0;
      for (ValueBox element : bsmArgBoxes) {
        if (!(element.getValue().equivTo(ie.getBootstrapArg(i)))) {
          return false;
        }
        i++;
      }
      if (!(getMethod().equals(ie.getMethod())
          && (argBoxes == null ? 0 : argBoxes.length) == (ie.argBoxes == null ? 0 : ie.argBoxes.length))) {
        return false;
      }
      if (argBoxes != null) {
        i = 0;
        for (ValueBox element : argBoxes) {
          if (!(element.getValue().equivTo(ie.getArg(i)))) {
            return false;
          }
          i++;
        }
      }
      if (!methodRef.equals(ie.methodRef)) {
        return false;
      }
      if (!bsmRef.equals(ie.bsmRef)) {
        return false;
      }
      return true;
    }
    return false;
  }

  public SootMethod getBootstrapMethod() {
    return bsmRef.resolve();
  }

  /**
   * Returns a hash code for this object, consistent with structural equality.
   */
  public int equivHashCode() {
    return getBootstrapMethod().equivHashCode() * getMethod().equivHashCode() * 17;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();

    buffer.append(Jimple.DYNAMICINVOKE);
    buffer.append(" \"");
    buffer.append(methodRef.name()); // quoted method name (can be any UTF8
    // string)
    buffer.append("\" <");
    buffer
        .append(SootMethod.getSubSignature(""/* no method name here */, methodRef.parameterTypes(), methodRef.returnType()));
    buffer.append(">(");

    if (argBoxes != null) {
      for (int i = 0; i < argBoxes.length; i++) {
        if (i != 0) {
          buffer.append(", ");
        }

        buffer.append(argBoxes[i].getValue().toString());
      }
    }

    buffer.append(") ");

    buffer.append(bsmRef.getSignature());
    buffer.append("(");
    for (int i = 0; i < bsmArgBoxes.length; i++) {
      if (i != 0) {
        buffer.append(", ");
      }

      buffer.append(bsmArgBoxes[i].getValue().toString());
    }
    buffer.append(")");

    return buffer.toString();
  }

  public void toString(UnitPrinter up) {
    up.literal(Jimple.DYNAMICINVOKE);
    up.literal(" \"" + methodRef.name() + "\" <"
        + SootMethod.getSubSignature(""/* no method name here */, methodRef.parameterTypes(), methodRef.returnType())
        + ">(");

    if (argBoxes != null) {
      for (int i = 0; i < argBoxes.length; i++) {
        if (i != 0) {
          up.literal(", ");
        }

        argBoxes[i].toString(up);
      }
    }

    up.literal(") ");
    up.methodRef(bsmRef);
    up.literal("(");

    for (int i = 0; i < bsmArgBoxes.length; i++) {
      if (i != 0) {
        up.literal(", ");
      }

      bsmArgBoxes[i].toString(up);
    }

    up.literal(")");
  }

  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseDynamicInvokeExpr(this);
  }

  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    if (argBoxes != null) {
      for (ValueBox element : argBoxes) {
        ((ConvertToBaf) (element.getValue())).convertToBaf(context, out);
      }
    }

    List<Value> bsmArgs = new ArrayList<Value>();
    for (ValueBox argBox : bsmArgBoxes) {
      bsmArgs.add(argBox.getValue());
    }

    Unit u = Baf.v().newDynamicInvokeInst(bsmRef, bsmArgs, methodRef, tag);
    u.addAllTagsOf(context.getCurrentUnit());
    out.add(u);
  }

  public SootMethodRef getBootstrapMethodRef() {
    return bsmRef;
  }

  public List<Value> getBootstrapArgs() {
    List<Value> l = new ArrayList<Value>();
    for (ValueBox element : bsmArgBoxes) {
      l.add(element.getValue());
    }

    return l;
  }

  @Override
  public int getHandleTag() {
    return tag;
  }
}
