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
import soot.baf.InstSwitch;
import soot.baf.VirtualInvokeInst;
import soot.util.Switch;

public class BVirtualInvokeInst extends AbstractInvokeInst implements VirtualInvokeInst {

  public BVirtualInvokeInst(SootMethodRef methodRef) {
    if (methodRef.isStatic()) {
      throw new RuntimeException("wrong static-ness");
    }
    super.methodRef = methodRef;
  }

  @Override
  public Object clone() {
    return new BVirtualInvokeInst(methodRef);
  }

  @Override
  public int getInMachineCount() {
    return super.getInMachineCount() + 1;
  }

  @Override
  public int getInCount() {
    return super.getInCount() + 1;
  }

  @Override
  final public String getName() {
    return "virtualinvoke";
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseVirtualInvokeInst(this);
  }
}
