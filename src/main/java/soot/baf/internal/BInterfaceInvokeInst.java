package soot.baf.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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

import soot.SootMethodRef;
import soot.UnitPrinter;
import soot.baf.InstSwitch;
import soot.baf.InterfaceInvokeInst;
import soot.util.Switch;

public class BInterfaceInvokeInst extends AbstractInvokeInst implements InterfaceInvokeInst {
  int argCount;

  public int getInCount() {
    return methodRef.parameterTypes().size() + 1;

  }

  public int getInMachineCount() {
    return super.getInMachineCount() + 1;
  }

  public BInterfaceInvokeInst(SootMethodRef methodRef, int argCount) {
    if (methodRef.isStatic()) {
      throw new RuntimeException("wrong static-ness");
    }
    this.methodRef = methodRef;
    this.argCount = argCount;
  }

  public Object clone() {
    return new BInterfaceInvokeInst(methodRef, getArgCount());
  }

  final public String getName() {
    return "interfaceinvoke";
  }

  final String getParameters() {
    return super.getParameters() + " " + argCount;
  }

  protected void getParameters(UnitPrinter up) {
    super.getParameters(up);
    up.literal(" ");
    up.literal(new Integer(argCount).toString());
  }

  public int getArgCount() {
    return argCount;
  }

  public void setArgCount(int x) {
    argCount = x;
  }

  public void apply(Switch sw) {
    ((InstSwitch) sw).caseInterfaceInvokeInst(this);
  }
}
