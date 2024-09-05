package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2015 Steven Arzt
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
import soot.Type;
import soot.UIntType;
import soot.util.Switch;

/**
 * .NET unsigned integer constant
 */
public class UIntConstant extends IntConstant {

  private static final long serialVersionUID = 8622167089453261784L;

  public static final long MAX_VALUE = 4294967295L;
  public static final long MIN_VALUE = 0L;

  private static final int MAX_CACHE = 128;
  private static final int MIN_CACHE = 0;
  private static final int ABS_MIN_CACHE = Math.abs(MIN_CACHE);
  private static final UIntConstant[] CACHED = new UIntConstant[MAX_CACHE + ABS_MIN_CACHE];

  protected UIntConstant(int value) {
    super(value);
  }

  public static UIntConstant v(int value) {
    if (value > MIN_CACHE && value < MAX_CACHE) {
      int idx = (int) (value + ABS_MIN_CACHE);
      UIntConstant c = CACHED[idx];
      if (c != null) {
        return c;
      }
      c = new UIntConstant(value);
      CACHED[idx] = c;
      return c;
    }
    return new UIntConstant(value);
  }

  @Override
  public boolean equals(Object c) {
    return c instanceof UIntConstant && ((UIntConstant) c).value == value;
  }

  @Override
  public int hashCode() {
    return (int) value;
  }

  // PTC 1999/06/28
  @Override
  public NumericConstant add(NumericConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v(this.value + ((UIntConstant) c).value);
  }

  @Override
  public NumericConstant subtract(NumericConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v(this.value - ((UIntConstant) c).value);
  }

  @Override
  public NumericConstant multiply(NumericConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v(this.value * ((UIntConstant) c).value);
  }

  @Override
  public NumericConstant divide(NumericConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v(this.value / ((UIntConstant) c).value);
  }

  @Override
  public NumericConstant remainder(NumericConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v(this.value % ((UIntConstant) c).value);
  }

  @Override
  public NumericConstant equalEqual(NumericConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v((this.value == ((UIntConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant notEqual(NumericConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v((this.value != ((UIntConstant) c).value) ? 1 : 0);
  }

  @Override
  public boolean isLessThan(NumericConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return this.value < ((UIntConstant) c).value;
  }

  @Override
  public NumericConstant lessThan(NumericConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v((this.value < ((UIntConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant lessThanOrEqual(NumericConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v((this.value <= ((UIntConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant greaterThan(NumericConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v((this.value > ((UIntConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant greaterThanOrEqual(NumericConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v((this.value >= ((UIntConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant negate() {
    return UIntConstant.v(-(this.value));
  }

  @Override
  public ArithmeticConstant and(ArithmeticConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v(this.value & ((UIntConstant) c).value);
  }

  @Override
  public ArithmeticConstant or(ArithmeticConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v(this.value | ((UIntConstant) c).value);
  }

  @Override
  public ArithmeticConstant xor(ArithmeticConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v(this.value ^ ((UIntConstant) c).value);
  }

  @Override
  public ArithmeticConstant shiftLeft(ArithmeticConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v(this.value << ((UIntConstant) c).value);
  }

  @Override
  public ArithmeticConstant shiftRight(ArithmeticConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v(this.value >> ((UIntConstant) c).value);
  }

  @Override
  public ArithmeticConstant unsignedShiftRight(ArithmeticConstant c) {
    if (!(c instanceof UIntConstant)) {
      throw new IllegalArgumentException("UIntConstant expected");
    }
    return UIntConstant.v(this.value >>> ((UIntConstant) c).value);
  }

  @Override
  public String toString() {
    return Integer.toUnsignedString(value);
  }

  @Override
  public Type getType() {
    return UIntType.v();
  }

  @Override
  public void apply(Switch sw) {
    ((ConstantSwitch) sw).caseUIntConstant(this);
  }
}
