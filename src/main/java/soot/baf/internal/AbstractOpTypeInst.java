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
import soot.Type;
import soot.UnitPrinter;
import soot.baf.Baf;

public abstract class AbstractOpTypeInst extends AbstractInst {

  protected Type opType;

  protected AbstractOpTypeInst(Type opType) {
    setOpType(opType);
  }

  public Type getOpType() {
    return opType;
  }

  public void setOpType(Type t) {
    this.opType = Baf.getDescriptorTypeOf(t);
  }

  /* override AbstractInst's toString with our own, including types */
  @Override
  public String toString() {
    return getName() + "." + Baf.bafDescriptorOf(opType) + getParameters();
  }

  @Override
  public void toString(UnitPrinter up) {
    up.literal(getName());
    up.literal(".");
    up.literal(Baf.bafDescriptorOf(opType));
    getParameters(up);
  }

  @Override
  public int getOutMachineCount() {
    return AbstractJasminClass.sizeOfType(getOpType());
  }
}
