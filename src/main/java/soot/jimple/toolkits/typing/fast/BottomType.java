package soot.jimple.toolkits.typing.fast;

import soot.Type;

/**
 * @author Ben Bellamy
 */
public class BottomType extends Type {
  public static final BottomType INSTANCE = new BottomType();

  public static BottomType v() {
    return INSTANCE;
  }

  private BottomType() {
  }

  @Override
  public String toString() {
    return "bottom_type";
  }

  @Override
  public boolean equals(Object t) {
    return this == t;
  }
}
