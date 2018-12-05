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
import soot.UnitPrinter;
import soot.baf.Baf;
import soot.baf.Dup1Inst;
import soot.baf.InstSwitch;
import soot.util.Switch;

public class BDup1Inst extends BDupInst implements Dup1Inst {

  private final Type mOpType;

  public BDup1Inst(Type aOpType) {
    mOpType = Baf.getDescriptorTypeOf(aOpType);
  }

  public Type getOp1Type() {
    return mOpType;
  }

  public List<Type> getOpTypes() {
    List<Type> res = new ArrayList<Type>();
    res.add(mOpType);
    return res;
  }

  public List<Type> getUnderTypes() {
    return new ArrayList<Type>();
  }

  final public String getName() {
    return "dup1";
  }

  public void apply(Switch sw) {
    ((InstSwitch) sw).caseDup1Inst(this);
  }

  public String toString() {
    return "dup1." + Baf.bafDescriptorOf(mOpType);
  }

  public void toString(UnitPrinter up) {
    up.literal("dup1");
    up.literal(".");
    up.literal(Baf.bafDescriptorOf(mOpType));
  }

}
