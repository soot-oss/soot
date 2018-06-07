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

import soot.RefType;
import soot.Type;
import soot.UnitPrinter;

public abstract class AbstractRefTypeInst extends AbstractInst {
  Type opType;

  protected AbstractRefTypeInst(RefType opType) {
    this.opType = opType;
  }

  public Type getOpType() {
    return opType;
  }

  public void setOpType(Type t) {
    opType = t;
  }

  public RefType getBaseType() {
    return (RefType) opType;
  }

  public void setBaseType(RefType type) {
    this.opType = type;
  }

  String getParameters() {
    return " " + opType.toString();
  }

  protected void getParameters(UnitPrinter up) {
    up.literal(" ");
    up.type(opType);
  }
}
