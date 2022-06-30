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
import java.util.Collections;
import java.util.List;

import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InvokeExpr;

@SuppressWarnings("serial")
public abstract class AbstractInvokeExpr implements InvokeExpr {

  protected SootMethodRef methodRef;
  protected final ValueBox[] argBoxes;

  protected AbstractInvokeExpr(SootMethodRef methodRef, ValueBox[] argBoxes) {
    this.methodRef = methodRef;
    this.argBoxes = argBoxes.length == 0 ? null : argBoxes;
  }

  @Override
  public void setMethodRef(SootMethodRef methodRef) {
    this.methodRef = methodRef;
  }

  @Override
  public SootMethodRef getMethodRef() {
    return methodRef;
  }

  @Override
  public SootMethod getMethod() {
    return methodRef.resolve();
  }

  @Override
  public abstract Object clone();

  @Override
  public Value getArg(int index) {
    if (argBoxes == null) {
      return null;
    }
    ValueBox vb = argBoxes[index];
    return vb == null ? null : vb.getValue();
  }

  @Override
  public List<Value> getArgs() {
    final ValueBox[] boxes = this.argBoxes;
    final List<Value> r;
    if (boxes == null) {
      r = new ArrayList<>(0);
    } else {
      r = new ArrayList<>(boxes.length);
      for (ValueBox element : boxes) {
        r.add(element == null ? null : element.getValue());
      }
    }
    return r;
  }

  @Override
  public int getArgCount() {
    return argBoxes == null ? 0 : argBoxes.length;
  }

  @Override
  public void setArg(int index, Value arg) {
    argBoxes[index].setValue(arg);
  }

  @Override
  public ValueBox getArgBox(int index) {
    return argBoxes[index];
  }

  @Override
  public Type getType() {
    return methodRef.returnType();
  }

  @Override
  public List<ValueBox> getUseBoxes() {
    final ValueBox[] boxes = argBoxes;
    if (boxes == null) {
      return Collections.emptyList();
    }

    List<ValueBox> list = new ArrayList<ValueBox>();
    Collections.addAll(list, boxes);
    for (ValueBox element : boxes) {
      list.addAll(element.getValue().getUseBoxes());
    }
    return list;
  }
}
