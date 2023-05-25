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

import soot.AbstractUnit;
import soot.UnitPrinter;
import soot.baf.Inst;

public abstract class AbstractInst extends AbstractUnit implements Inst {

  public abstract String getName();

  @Override
  public Object clone() {
    throw new RuntimeException("undefined clone for: " + this.toString());
  }

  @Override
  public String toString() {
    return getName() + getParameters();
  }

  @Override
  public void toString(UnitPrinter up) {
    up.literal(getName());
    getParameters(up);
  }

  @Override
  public int getInCount() {
    throw new RuntimeException("undefined " + toString() + "!");
  }

  @Override
  public int getOutCount() {
    throw new RuntimeException("undefined " + toString() + "!");
  }

  @Override
  public int getNetCount() {
    return getOutCount() - getInCount();
  }

  @Override
  public boolean fallsThrough() {
    return true;
  }

  @Override
  public boolean branches() {
    return false;
  }

  @Override
  public int getInMachineCount() {
    throw new RuntimeException("undefined" + toString() + "!");
  }

  @Override
  public int getOutMachineCount() {
    throw new RuntimeException("undefined" + toString() + "!");
  }

  @Override
  public int getNetMachineCount() {
    return getOutMachineCount() - getInMachineCount();
  }

  String getParameters() {
    return "";
  }

  protected void getParameters(UnitPrinter up) {
  }

  @Override
  public boolean containsInvokeExpr() {
    return false;
  }

  @Override
  public boolean containsArrayRef() {
    return false;
  }

  @Override
  public boolean containsFieldRef() {
    return false;
  }

  @Override
  public boolean containsNewExpr() {
    return false;
  }
}
