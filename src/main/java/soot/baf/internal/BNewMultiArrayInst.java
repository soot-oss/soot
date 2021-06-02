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
import soot.UnitPrinter;
import soot.baf.InstSwitch;
import soot.baf.NewMultiArrayInst;
import soot.util.Switch;

public class BNewMultiArrayInst extends AbstractInst implements NewMultiArrayInst {

  int dimensionCount;
  ArrayType baseType;

  public BNewMultiArrayInst(ArrayType opType, int dimensionCount) {
    this.dimensionCount = dimensionCount;
    this.baseType = opType;
  }

  @Override
  public Object clone() {
    return new BNewMultiArrayInst(getBaseType(), getDimensionCount());
  }

  @Override
  public int getInCount() {
    return dimensionCount;
  }

  @Override
  public int getOutCount() {
    return 1;
  }

  @Override
  public int getInMachineCount() {
    return dimensionCount;
  }

  @Override
  public int getOutMachineCount() {
    return 1;
  }

  @Override
  final public String getName() {
    return "newmultiarray";
  }

  @Override
  final String getParameters() {
    return " " + dimensionCount;
  }

  @Override
  protected void getParameters(UnitPrinter up) {
    up.literal(" ");
    up.literal(Integer.toString(dimensionCount));
  }

  @Override
  public ArrayType getBaseType() {
    return baseType;
  }

  @Override
  public void setBaseType(ArrayType type) {
    this.baseType = type;
  }

  @Override
  public int getDimensionCount() {
    return dimensionCount;
  }

  @Override
  public void setDimensionCount(int x) {
    this.dimensionCount = x;
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseNewMultiArrayInst(this);
  }

  @Override
  public boolean containsNewExpr() {
    return true;
  }
}
