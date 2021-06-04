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

import soot.Type;
import soot.UnitBox;
import soot.UnitPrinter;
import soot.baf.Baf;

public abstract class AbstractOpTypeBranchInst extends AbstractBranchInst {

  protected Type opType;

  AbstractOpTypeBranchInst(Type opType, UnitBox targetBox) {
    super(targetBox);
    setOpType(opType);
  }

  public Type getOpType() {
    return this.opType;
  }

  public void setOpType(Type t) {
    this.opType = Baf.getDescriptorTypeOf(t);
  }

  @Override
  public int getInCount() {
    return 2;
  }

  @Override
  public int getOutCount() {
    return 0;
  }

  @Override
  public String toString() {
    // do stuff with opType later.
    return getName() + "." + Baf.bafDescriptorOf(opType) + " " + getTarget();
  }

  @Override
  public void toString(UnitPrinter up) {
    up.literal(getName());
    up.literal(".");
    up.literal(Baf.bafDescriptorOf(opType));
    up.literal(" ");
    targetBox.toString(up);
  }
}
