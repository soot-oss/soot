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

import java.util.ArrayList;
import java.util.List;

import soot.Type;
import soot.baf.Baf;
import soot.baf.Dup2_x1Inst;
import soot.baf.InstSwitch;
import soot.util.Switch;

public class BDup2_x1Inst extends BDupInst implements Dup2_x1Inst {

  private final Type mOp1Type;
  private final Type mOp2Type;
  private final Type mUnderType;

  public BDup2_x1Inst(Type aOp1Type, Type aOp2Type, Type aUnderType) {
    this.mOp1Type = Baf.getDescriptorTypeOf(aOp1Type);
    this.mOp2Type = Baf.getDescriptorTypeOf(aOp2Type);
    this.mUnderType = Baf.getDescriptorTypeOf(aUnderType);
  }

  @Override
  public Type getOp1Type() {
    return mOp1Type;
  }

  @Override
  public Type getOp2Type() {
    return mOp2Type;
  }

  @Override
  public Type getUnder1Type() {
    return mUnderType;
  }

  @Override
  public List<Type> getOpTypes() {
    List<Type> res = new ArrayList<Type>();
    res.add(mOp1Type);
    res.add(mOp2Type);
    return res;
  }

  @Override
  public List<Type> getUnderTypes() {
    List<Type> res = new ArrayList<Type>();
    res.add(mUnderType);
    return res;
  }

  @Override
  final public String getName() {
    return "dup2_x1";
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseDup2_x1Inst(this);
  }

  @Override
  public String toString() {
    return "dup2_x1." + Baf.bafDescriptorOf(mOp1Type) + "." + Baf.bafDescriptorOf(mOp2Type) + "_"
        + Baf.bafDescriptorOf(mUnderType);
  }
}
