package soot;

import soot.jimple.IntConstant;

public class CharConstant extends IntConstant {
  private static final long serialVersionUID = 0L;

  private static final int MAX_CACHE = 128;
  private static final int MIN_CACHE = -127;
  private static final int ABS_MIN_CACHE = Math.abs(MIN_CACHE);
  private static final CharConstant[] CACHED = new CharConstant[1 + MAX_CACHE + ABS_MIN_CACHE];

  public CharConstant(char value) {
    super(value);
  }

  public CharConstant(int value) {
    super((char) value);
  }

  public static CharConstant v(int value) {
    if (value >= MIN_CACHE && value <= MAX_CACHE) {
      int idx = value + ABS_MIN_CACHE;
      CharConstant c = CACHED[idx];
      if (c != null) {
        return c;
      }
      c = new CharConstant(value);
      CACHED[idx] = c;
      return c;
    }
    return new CharConstant(value);
  }

  public char getChar() {
    return (char) value;
  }

  @Override
  public Type getType() {
    return CharType.v();
  }
}
