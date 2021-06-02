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

import soot.DoubleType;
import soot.LongType;
import soot.Type;
import soot.baf.Baf;
import soot.baf.InstSwitch;
import soot.baf.SwapInst;
import soot.util.Switch;

public class BSwapInst extends AbstractInst implements SwapInst {

  protected Type mFromType;
  protected Type mToType;

  public BSwapInst(Type fromType, Type toType) {
    if (fromType instanceof LongType || fromType instanceof DoubleType) {
      throw new RuntimeException("fromType is LongType or DoubleType !");
    }
    if (toType instanceof LongType || toType instanceof DoubleType) {
      throw new RuntimeException("toType is LongType or DoubleType !");
    }

    this.mFromType = Baf.getDescriptorTypeOf(fromType);
    this.mToType = Baf.getDescriptorTypeOf(toType);
  }

  @Override
  public Type getFromType() {
    return mFromType;
  }

  @Override
  public void setFromType(Type fromType) {
    mFromType = fromType;
  }

  @Override
  public Type getToType() {
    return mToType;
  }

  @Override
  public void setToType(Type toType) {
    mToType = toType;
  }

  @Override
  public int getInCount() {
    return 2;
  }

  @Override
  public int getInMachineCount() {
    return 2;
  }

  @Override
  public int getOutCount() {
    return 2;
  }

  @Override
  public int getOutMachineCount() {
    return 2;
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseSwapInst(this);
  }

  @Override
  public String toString() {
    return "swap." + Baf.bafDescriptorOf(mFromType) + Baf.bafDescriptorOf(mToType);
  }

  @Override
  public String getName() {
    return "swap";
  }
}
