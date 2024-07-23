package soot.jimple;

import soot.Type;
import soot.ULongType;
import soot.util.Switch;

public class ULongConstant extends LongConstant {

  private static final long serialVersionUID = 1008501511477295944L;

  public static final ULongConstant ZERO = new ULongConstant(0);
  public static final ULongConstant ONE = new ULongConstant(1);

  private ULongConstant(long value) {
    super(value);
  }

  public static ULongConstant v(long value) {
    if (value == 0) {
      return ZERO;
    }
    if (value == 1) {
      return ONE;
    }
    return new ULongConstant(value);
  }

  @Override
  public boolean equals(Object c) {
    return c instanceof ULongConstant && ((ULongConstant) c).value == this.value;
  }

  /** Returns a hash code for this DoubleConstant object. */
  @Override
  public int hashCode() {
    return (int) (value ^ (value >>> 32));
  }

  @Override
  public NumericConstant add(NumericConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return ULongConstant.v(this.value + ((ULongConstant) c).value);
  }

  @Override
  public NumericConstant subtract(NumericConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return ULongConstant.v(this.value - ((ULongConstant) c).value);
  }

  @Override
  public NumericConstant multiply(NumericConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return ULongConstant.v(this.value * ((ULongConstant) c).value);
  }

  @Override
  public NumericConstant divide(NumericConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return ULongConstant.v(this.value / ((ULongConstant) c).value);
  }

  @Override
  public NumericConstant remainder(NumericConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return ULongConstant.v(this.value % ((ULongConstant) c).value);
  }

  @Override
  public NumericConstant equalEqual(NumericConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value == ((ULongConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant notEqual(NumericConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value != ((ULongConstant) c).value) ? 1 : 0);
  }

  @Override
  public boolean isLessThan(NumericConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return this.value < ((ULongConstant) c).value;
  }

  @Override
  public NumericConstant lessThan(NumericConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value < ((ULongConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant lessThanOrEqual(NumericConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value <= ((ULongConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant greaterThan(NumericConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value > ((ULongConstant) c).value) ? 1 : 0);
  }

  @Override
  public NumericConstant greaterThanOrEqual(NumericConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return IntConstant.v((this.value >= ((ULongConstant) c).value) ? 1 : 0);
  }

  public IntConstant cmp(ULongConstant c) {
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
    return ULongConstant.v(-(this.value));
  }

  @Override
  public ArithmeticConstant and(ArithmeticConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return ULongConstant.v(this.value & ((ULongConstant) c).value);
  }

  @Override
  public ArithmeticConstant or(ArithmeticConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return ULongConstant.v(this.value | ((ULongConstant) c).value);
  }

  @Override
  public ArithmeticConstant xor(ArithmeticConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("LongConstant expected");
    }
    return ULongConstant.v(this.value ^ ((ULongConstant) c).value);
  }

  @Override
  public ArithmeticConstant shiftLeft(ArithmeticConstant c) {
    // NOTE CAREFULLY: the RHS of a shift op is not (!)
    // of Long type. It is, in fact, an IntConstant.

    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("ULongConstant expected");
    }
    return ULongConstant.v(this.value << ((IntConstant) c).value);
  }

  @Override
  public ArithmeticConstant shiftRight(ArithmeticConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("ULongConstant expected");
    }
    return ULongConstant.v(this.value >> ((IntConstant) c).value);
  }

  @Override
  public ArithmeticConstant unsignedShiftRight(ArithmeticConstant c) {
    if (!(c instanceof ULongConstant)) {
      throw new IllegalArgumentException("ULongConstant expected");
    }
    return ULongConstant.v(this.value >>> ((IntConstant) c).value);
  }

  @Override
  public String toString() {
    return Long.toUnsignedString(value);
  }

  @Override
  public Type getType() {
    return ULongType.v();
  }

  @Override
  public void apply(Switch sw) {
    ((ConstantSwitch) sw).caseULongConstant(this);
  }
}
