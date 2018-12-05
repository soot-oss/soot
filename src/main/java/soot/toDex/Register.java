package soot.toDex;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
import soot.FloatType;
import soot.IntType;
import soot.Type;

/**
 * A register for the Dalvik VM. It has a number and a type.
 */
public class Register implements Cloneable {

  public static final int MAX_REG_NUM_UNCONSTRAINED = 65535;

  public static final int MAX_REG_NUM_SHORT = 255;

  public static final int MAX_REG_NUM_BYTE = 15;

  public static final Register EMPTY_REGISTER = new Register(IntType.v(), 0);

  private static boolean fitsInto(int regNumber, int maxNumber, boolean isWide) {
    if (isWide) {
      // reg occupies number and number + 1, hence the "<"
      return regNumber >= 0 && regNumber < maxNumber;
    }
    return regNumber >= 0 && regNumber <= maxNumber;
  }

  public static boolean fitsUnconstrained(int regNumber, boolean isWide) {
    return fitsInto(regNumber, MAX_REG_NUM_UNCONSTRAINED, isWide);
  }

  public static boolean fitsShort(int regNumber, boolean isWide) {
    return fitsInto(regNumber, MAX_REG_NUM_SHORT, isWide);
  }

  public static boolean fitsByte(int regNumber, boolean isWide) {
    return fitsInto(regNumber, MAX_REG_NUM_BYTE, isWide);
  }

  private final Type type;

  private int number;

  public Register(Type type, int number) {
    this.type = type;
    this.number = number;
  }

  public boolean isEmptyReg() {
    return this == EMPTY_REGISTER;
  }

  public boolean isWide() {
    return SootToDexUtils.isWide(type);
  }

  public boolean isObject() {
    return SootToDexUtils.isObject(type);
  }

  public boolean isFloat() {
    return type instanceof FloatType;
  }

  public boolean isDouble() {
    return type instanceof DoubleType;
  }

  public Type getType() {
    return type;
  }

  public String getTypeString() {
    return type.toString();
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    if (isEmptyReg()) {
      // number of empty register stays at zero - that's part of its purpose
      return;
    }
    this.number = number;
  }

  private boolean fitsInto(int maxNumber) {
    if (isEmptyReg()) {
      // empty reg fits into anything
      return true;
    }
    return fitsInto(number, maxNumber, isWide());
  }

  public boolean fitsUnconstrained() {
    return fitsInto(MAX_REG_NUM_UNCONSTRAINED);
  }

  public boolean fitsShort() {
    return fitsInto(MAX_REG_NUM_SHORT);
  }

  public boolean fitsByte() {
    return fitsInto(MAX_REG_NUM_BYTE);
  }

  @Override
  public Register clone() {
    return new Register(this.type, this.number);
  }

  @Override
  public String toString() {
    if (isEmptyReg()) {
      return "the empty reg";
    }
    return "reg(" + number + "):" + type.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + number;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Register other = (Register) obj;
    if (number != other.number) {
      return false;
    }
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!type.equals(other.type)) {
      return false;
    }
    return true;
  }

}
