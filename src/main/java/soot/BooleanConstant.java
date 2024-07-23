package soot;

import soot.jimple.IntConstant;

public class BooleanConstant extends IntConstant {
  private static final String FALSE_CONSTANT = "false";
  private static final String TRUE_CONSTANT = "true";
  private static final long serialVersionUID = 0L;
  private static final int TRUE = 1;
  private static final int FALSE = 0;

  public static final BooleanConstant TRUE_C = new BooleanConstant(true);
  public static final BooleanConstant FALSE_C = new BooleanConstant(false);

  public BooleanConstant(boolean value) {
    super(value == true ? TRUE : FALSE);
  }

  public static BooleanConstant v(boolean b) {
    if (b) {
      return TRUE_C;
    } else {
      return FALSE_C;
    }
  }

  @Override
  public String toString() {
    if (value == TRUE) {
      return TRUE_CONSTANT;
    } else {
      return FALSE_CONSTANT;
    }
  }

  public boolean getBoolean() {
    // A value other than 0 or 1 shouldn't be possible
    return value == TRUE;
  }

  @Override
  public Type getType() {
    return BooleanType.v();
  }
}
