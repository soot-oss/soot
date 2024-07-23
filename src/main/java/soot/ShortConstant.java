package soot;

import soot.jimple.ConstantSwitch;
import soot.jimple.IntConstant;
import soot.util.Switch;

public class ShortConstant extends IntConstant {
  private static final long serialVersionUID = 0L;
  private static final int MAX_CACHE = 128;
  private static final int MIN_CACHE = -127;
  private static final int ABS_MIN_CACHE = Math.abs(MIN_CACHE);
  private static final ShortConstant[] CACHED = new ShortConstant[1 + MAX_CACHE + ABS_MIN_CACHE];

  public ShortConstant(short value) {
    super(value);
  }

  public ShortConstant(int value) {
    super((short) value);
  }

  public static ShortConstant v(int value) {
    if (value >= MIN_CACHE && value <= MAX_CACHE) {
      int idx = value + ABS_MIN_CACHE;
      ShortConstant c = CACHED[idx];
      if (c != null) {
        return c;
      }
      c = new ShortConstant(value);
      CACHED[idx] = c;
      return c;
    }
    return new ShortConstant(value);
  }

  public short getShort() {
    return (short) value;
  }

  @Override
  public Type getType() {
    return ShortType.v();
  }

  @Override
  public void apply(Switch sw) {
    ((ConstantSwitch) sw).caseShortConstant(this);
  }
}
