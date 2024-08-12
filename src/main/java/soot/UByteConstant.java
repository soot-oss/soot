package soot;

import soot.jimple.ConstantSwitch;
import soot.jimple.IntConstant;
import soot.util.Switch;

public class UByteConstant extends IntConstant {
  private static final long serialVersionUID = 0L;
  public static final UByteConstant ZERO = new UByteConstant(0);
  public static final UByteConstant ONE = new UByteConstant(1);

  private static final int MAX_CACHE = 255;
  private static final int MIN_CACHE = 0;
  private static final int ABS_MIN_CACHE = Math.abs(MIN_CACHE);
  private static final UByteConstant[] CACHED = new UByteConstant[1 + MAX_CACHE + ABS_MIN_CACHE];
  private static final int MIN_VALUE = 0;
  private static final int MAX_VALUE = 255;

  public UByteConstant(int value) {
    super(value);
  }

  public static UByteConstant v(int value) {
    if (value < MIN_VALUE || value > MAX_VALUE) {
      throw new IllegalArgumentException("Out of range: " + value);
    }
    int idx = value + ABS_MIN_CACHE;
    UByteConstant c = CACHED[idx];
    if (c != null) {
      return c;
    }
    c = new UByteConstant(value);
    CACHED[idx] = c;
    return c;
  }

  public static UByteConstant v(byte value) {
    return new UByteConstant(value);
  }

  @Override
  public Type getType() {
    return UByteType.v();
  }

  @Override
  public void apply(Switch sw) {
    ((ConstantSwitch) sw).caseUByteConstant(this);
  }
}
