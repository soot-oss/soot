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

import soot.LongType;
import soot.Type;
import soot.util.Switch;

public class LongConstant extends ArithmeticConstant {

  private static final long serialVersionUID = 1008501511477295944L;

  public final long value;

  public static final LongConstant ZERO = new LongConstant(0);
  public static final LongConstant ONE = new LongConstant(1);

  protected LongConstant(long value) {
    this.value = value;
  }

  public static LongConstant v(long value) {
    if (value == 0) {
      return ZERO;
    }
    if (value == 1) {
      return ONE;
    }
    return new LongConstant(value);
  }

  @Override
  public boolean equals(Object c) {
    return c instanceof LongConstant && ((LongConstant) c).value == this.value;
  }

  /** Returns a hash code for this DoubleConstant object. */
  @Override
  public int hashCode() {
    return (int) (value ^ (value >>> 32));
  }

  // PTC 1999/06/28
  @Override
  public NumericConstant add(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return LongConstant.v(this.value + ((LongConstant) c).value);
  }

  @Override
  public NumericConstant subtract(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return LongConstant.v(this.value - ((LongConstant) c).value);
  }

  @Override
  public NumericConstant multiply(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return LongConstant.v(this.value * ((LongConstant) c).value);
  }

  @Override
  public NumericConstant divide(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return LongConstant.v(this.value / ((LongConstant) c).value);
  }

  @Override
  public NumericConstant remainder(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return LongConstant.v(this.value % ((LongConstant) c).value);
  }

  @Override
  public NumericConstant equalEqual(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value == ((LongConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant notEqual(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value != ((LongConstant) c).value) ? 1 : 0);
  }

  @Override
  public boolean isLessThan(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return this.value < ((LongConstant) c).value;
  }

  @Override
  public NumericConstant lessThan(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value < ((LongConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant lessThanOrEqual(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value <= ((LongConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant greaterThan(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value > ((LongConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant greaterThanOrEqual(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value >= ((LongConstant) c).value) ? 1 : 0);
  }

  public IntConstant cmp(LongConstant c) {
    if (this.value > c.value) {
      return IntConstant.v(1);
    } else if (this.value == c.value) {
      return IntConstant.v(0);
    } else {
      return IntConstant.v(-1);
    }
  }

  @Override
  public NumericConstant negate() {
    return LongConstant.v(-(this.value));
  }

  @Override
  public ArithmeticConstant and(ArithmeticConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return LongConstant.v(this.value & ((LongConstant) c).value);
  }

  @Override
  public ArithmeticConstant or(ArithmeticConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return LongConstant.v(this.value | ((LongConstant) c).value);
  }

  @Override
  public ArithmeticConstant xor(ArithmeticConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return LongConstant.v(this.value ^ ((LongConstant) c).value);
  }

  @Override
  public ArithmeticConstant shiftLeft(ArithmeticConstant c) {
    // NOTE CAREFULLY: the RHS of a shift op is not (!)
    // of Long type. It is, in fact, an IntConstant.

    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return LongConstant.v(this.value << ((IntConstant) c).value);
  }

  @Override
  public ArithmeticConstant shiftRight(ArithmeticConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return LongConstant.v(this.value >> ((IntConstant) c).value);
  }

  @Override
  public ArithmeticConstant unsignedShiftRight(ArithmeticConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return LongConstant.v(this.value >>> ((IntConstant) c).value);
  }

  @Override
  public String toString() {
    return Long.toString(value) + "L";
  }

  @Override
  public Type getType() {
    return LongType.v();
  }

  @Override
  public void apply(Switch sw) {
    ((ConstantSwitch) sw).caseLongConstant(this);
  }

  @Override
  public Number getNumericValue() {
    return value;
  }
}
