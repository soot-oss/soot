package soot.baf.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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

import soot.AbstractJasminClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.UnitPrinter;
import soot.VoidType;

public abstract class AbstractInvokeInst extends AbstractInst {

  SootMethodRef methodRef;

  public SootMethodRef getMethodRef() {
    return methodRef;
  }

  public SootMethod getMethod() {
    return methodRef.resolve();
  }

  public Type getType() {
    return methodRef.getReturnType();
  }

  @Override
  public String toString() {
    return getName() + getParameters();
  }

  @Override
  abstract public String getName();

  @Override
  String getParameters() {
    return " " + methodRef.getSignature();
  }

  @Override
  protected void getParameters(UnitPrinter up) {
    up.literal(" ");
    up.methodRef(methodRef);
  }

  @Override
  public int getInCount() {
    return getMethodRef().getParameterTypes().size();
  }

  @Override
  public int getOutCount() {
    return (getMethodRef().getReturnType() instanceof VoidType) ? 0 : 1;
  }

  @Override
  public int getInMachineCount() {
    int count = 0;
    for (Type t : getMethodRef().getParameterTypes()) {
      count += AbstractJasminClass.sizeOfType(t);
    }
    return count;
  }

  @Override
  public int getOutMachineCount() {
    final Type returnType = getMethodRef().getReturnType();
    return (returnType instanceof VoidType) ? 0 : AbstractJasminClass.sizeOfType(returnType);
  }

  @Override
  public boolean containsInvokeExpr() {
    return true;
  }
}
