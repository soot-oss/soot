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

import soot.ArrayType;
import soot.RefType;
import soot.Type;
import soot.baf.InstSwitch;
import soot.baf.InstanceCastInst;
import soot.util.Switch;

public class BInstanceCastInst extends AbstractInst implements InstanceCastInst {

  protected Type castType;

  public BInstanceCastInst(Type opType) {
    if (!(opType instanceof RefType) && !(opType instanceof ArrayType)) {
      throw new RuntimeException("invalid InstanceCastInst: " + opType);
    }
    this.castType = opType;
  }

  @Override
  public Object clone() {
    return new BInstanceCastInst(castType);
  }

  @Override
  public int getInCount() {
    return 1;
  }

  @Override
  public int getInMachineCount() {
    return 1;
  }

  @Override
  public int getOutCount() {
    return 1;
  }

  @Override
  public int getOutMachineCount() {
    return 1;
  }

  @Override
  final public String getName() {
    return "checkcast";
  }

  @Override
  public Type getCastType() {
    return castType;
  }

  @Override
  public void setCastType(Type t) {
    castType = t;
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseInstanceCastInst(this);
  }
}
