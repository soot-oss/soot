package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import soot.IntType;
import soot.Type;
import soot.util.Switch;

public class IntConstant extends ArithmeticConstant {

  private static final long serialVersionUID = 8622167089453261784L;

  public final int value;

  private static final int MAX_CACHE = 128;
  private static final int MIN_CACHE = -127;
  private static final int ABS_MIN_CACHE = Math.abs(MIN_CACHE);
  private static final IntConstant[] CACHED = new IntConstant[MAX_CACHE + ABS_MIN_CACHE];

  protected IntConstant(int value) {
    this.value = value;
  }

  public static IntConstant v(int value) {
    if (value > MIN_CACHE && value < MAX_CACHE) {
      int idx = value + ABS_MIN_CACHE;
      IntConstant c = CACHED[idx];
      if (c != null) {
        return c;
      }
      c = new IntConstant(value);
      CACHED[idx] = c;
      return c;
    }
    return new IntConstant(value);
  }

  @Override
  public boolean equals(Object c) {
    return c instanceof IntConstant && ((IntConstant) c).value == value;
  }

  @Override
  public int hashCode() {
    return value;
  }

  // PTC 1999/06/28
  @Override
  public NumericConstant add(NumericConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v(this.value + ((IntConstant) c).value);
  }

  @Override
  public NumericConstant subtract(NumericConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v(this.value - ((IntConstant) c).value);
  }

  @Override
  public NumericConstant multiply(NumericConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v(this.value * ((IntConstant) c).value);
  }

  @Override
  public NumericConstant divide(NumericConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v(this.value / ((IntConstant) c).value);
  }

  @Override
  public NumericConstant remainder(NumericConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v(this.value % ((IntConstant) c).value);
  }

  @Override
  public NumericConstant equalEqual(NumericConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v((this.value == ((IntConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant notEqual(NumericConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v((this.value != ((IntConstant) c).value) ? 1 : 0);
  }

  @Override
  public boolean isLessThan(NumericConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return this.value < ((IntConstant) c).value;
  }

  @Override
  public NumericConstant lessThan(NumericConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v((this.value < ((IntConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant lessThanOrEqual(NumericConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v((this.value <= ((IntConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant greaterThan(NumericConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v((this.value > ((IntConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant greaterThanOrEqual(NumericConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v((this.value >= ((IntConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant negate() {
    return IntConstant.v(-(this.value));
  }

  @Override
  public ArithmeticConstant and(ArithmeticConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v(this.value & ((IntConstant) c).value);
  }

  @Override
  public ArithmeticConstant or(ArithmeticConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v(this.value | ((IntConstant) c).value);
  }

  @Override
  public ArithmeticConstant xor(ArithmeticConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v(this.value ^ ((IntConstant) c).value);
  }

  @Override
  public ArithmeticConstant shiftLeft(ArithmeticConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v(this.value << ((IntConstant) c).value);
  }

  @Override
  public ArithmeticConstant shiftRight(ArithmeticConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v(this.value >> ((IntConstant) c).value);
  }

  @Override
  public ArithmeticConstant unsignedShiftRight(ArithmeticConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return IntConstant.v(this.value >>> ((IntConstant) c).value);
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }

  @Override
  public Type getType() {
    return IntType.v();
  }

  @Override
  public void apply(Switch sw) {
    ((ConstantSwitch) sw).caseIntConstant(this);
  }
}
