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
  public final long value;

  private LongConstant(long value) {
    this.value = value;
  }

  public static LongConstant v(long value) {
    return new LongConstant(value);
  }

  public boolean equals(Object c) {
    return c instanceof LongConstant && ((LongConstant) c).value == this.value;
  }

  /** Returns a hash code for this DoubleConstant object. */
  public int hashCode() {
    return (int) (value ^ (value >>> 32));
  }

  // PTC 1999/06/28
  public NumericConstant add(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return LongConstant.v(this.value + ((LongConstant) c).value);
  }

  public NumericConstant subtract(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return LongConstant.v(this.value - ((LongConstant) c).value);
  }

  public NumericConstant multiply(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return LongConstant.v(this.value * ((LongConstant) c).value);
  }

  public NumericConstant divide(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return LongConstant.v(this.value / ((LongConstant) c).value);
  }

  public NumericConstant remainder(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return LongConstant.v(this.value % ((LongConstant) c).value);
  }

  public NumericConstant equalEqual(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value == ((LongConstant) c).value) ? 1 : 0);
  }

  public NumericConstant notEqual(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value != ((LongConstant) c).value) ? 1 : 0);
  }

  public NumericConstant lessThan(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value < ((LongConstant) c).value) ? 1 : 0);
  }

  public NumericConstant lessThanOrEqual(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value <= ((LongConstant) c).value) ? 1 : 0);
  }

  public NumericConstant greaterThan(NumericConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value > ((LongConstant) c).value) ? 1 : 0);
  }

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

  public NumericConstant negate() {
    return LongConstant.v(-(this.value));
  }

  public ArithmeticConstant and(ArithmeticConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return LongConstant.v(this.value & ((LongConstant) c).value);
  }

  public ArithmeticConstant or(ArithmeticConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return LongConstant.v(this.value | ((LongConstant) c).value);
  }

  public ArithmeticConstant xor(ArithmeticConstant c) {
    if (!(c instanceof LongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return LongConstant.v(this.value ^ ((LongConstant) c).value);
  }

  public ArithmeticConstant shiftLeft(ArithmeticConstant c) {
    // NOTE CAREFULLY: the RHS of a shift op is not (!)
    // of Long type. It is, in fact, an IntConstant.

    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return LongConstant.v(this.value << ((IntConstant) c).value);
  }

  public ArithmeticConstant shiftRight(ArithmeticConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return LongConstant.v(this.value >> ((IntConstant) c).value);
  }

  public ArithmeticConstant unsignedShiftRight(ArithmeticConstant c) {
    if (!(c instanceof IntConstant)) {
      throw new IllegalArgumentException("IntConstant expected");
    }
    return LongConstant.v(this.value >>> ((IntConstant) c).value);
  }

  public String toString() {
    return new Long(value).toString() + "L";
  }

  public Type getType() {
    return LongType.v();
  }

  public void apply(Switch sw) {
    ((ConstantSwitch) sw).caseLongConstant(this);
  }
}
