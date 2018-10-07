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
import soot.baf.Dup2Inst;
import soot.baf.InstSwitch;
import soot.util.Switch;

public class BDup2Inst extends BDupInst implements Dup2Inst {

  private final Type mOp1Type;
  private final Type mOp2Type;

  public BDup2Inst(Type aOp1Type, Type aOp2Type) {
    mOp1Type = Baf.getDescriptorTypeOf(aOp1Type);
    mOp2Type = Baf.getDescriptorTypeOf(aOp2Type);
  }

  public Type getOp1Type() {
    return mOp1Type;
  }

  public Type getOp2Type() {
    return mOp2Type;
  }

  public List<Type> getOpTypes() {
    List<Type> res = new ArrayList<Type>();
    res.add(mOp1Type);
    res.add(mOp2Type);
    return res;
  }

  public List<Type> getUnderTypes() {
    return new ArrayList<Type>();
  }

  final public String getName() {
    return "dup2";
  }

  public void apply(Switch sw) {
    ((InstSwitch) sw).caseDup2Inst(this);
  }

  public String toString() {
    return "dup2." + Baf.bafDescriptorOf(mOp1Type) + Baf.bafDescriptorOf(mOp2Type);
  }

}
