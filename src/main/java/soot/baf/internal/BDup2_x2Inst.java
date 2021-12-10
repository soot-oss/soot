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
import soot.baf.Dup2_x2Inst;
import soot.baf.InstSwitch;
import soot.util.Switch;

public class BDup2_x2Inst extends BDupInst implements Dup2_x2Inst {

  private final Type mOp1Type;
  private final Type mOp2Type;
  private final Type mUnder1Type;
  private final Type mUnder2Type;

  public BDup2_x2Inst(Type aOp1Type, Type aOp2Type, Type aUnder1Type, Type aUnder2Type) {
    this.mOp1Type = Baf.getDescriptorTypeOf(aOp1Type);
    this.mOp2Type = Baf.getDescriptorTypeOf(aOp2Type);
    this.mUnder1Type = Baf.getDescriptorTypeOf(aUnder1Type);
    this.mUnder2Type = Baf.getDescriptorTypeOf(aUnder2Type);
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
    return mUnder1Type;
  }

  @Override
  public Type getUnder2Type() {
    return mUnder2Type;
  }

  @Override
  public List<Type> getOpTypes() {
    List<Type> res = new ArrayList<Type>();
    res.add(mOp1Type);
    // 07-20-2006 Michael Batchelder
    // previously did not handle all types of dup2_x2 Now, will take null as mOp2Type, so don't add to overtypes if it is
    // null
    if (mOp2Type != null) {
      res.add(mOp2Type);
    }
    return res;
  }

  @Override
  public List<Type> getUnderTypes() {
    List<Type> res = new ArrayList<Type>();
    res.add(mUnder1Type);
    // 07-20-2006 Michael Batchelder
    // previously did not handle all types of dup2_x2 Now, will take null as mUnder2Type, so don't add to undertypes if it is
    // null
    if (mUnder2Type != null) {
      res.add(mUnder2Type);
    }
    return res;
  }

  @Override
  final public String getName() {
    return "dup2_x2";
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseDup2_x2Inst(this);
  }

  @Override
  public String toString() {
    // 07-20-2006 Michael Batchelder
    // previously did not handle all types of dup2_x2 Now, will take null as either mOp2Type or null as mUnder2Type to handle
    // ALL types of dup2_x2

    // old code:
    // return "dup2_x2." + Baf.bafDescriptorOf(mOp1Type) + "." + Baf.bafDescriptorOf(mOp2Type) + "_" +
    // Baf.bafDescriptorOf(mUnder1Type) + "." +
    // Baf.bafDescriptorOf(mUnder2Type);

    String optypes = Baf.bafDescriptorOf(mOp1Type);
    if (mOp2Type != null) {
      optypes += "." + Baf.bafDescriptorOf(mOp2Type);
    }

    String undertypes = Baf.bafDescriptorOf(mUnder1Type);
    if (mUnder2Type != null) {
      optypes += "." + Baf.bafDescriptorOf(mUnder2Type);
    }

    return "dup2_x2." + optypes + "_" + undertypes;
    // END 07-20-2006 Michael Batchelder
  }
}
