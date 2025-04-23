package soot.jimple.toolkits.typing.fast;

import soot.BooleanType;
import soot.IntegerType;
import soot.PrimType;
import soot.Type;

/**
 * @author Ben Bellamy
 */
public class Integer1Type extends PrimType implements IntegerType {
  public static final Integer1Type INSTANCE = new Integer1Type();

  public static Integer1Type v() {
    return INSTANCE;
  }

  private Integer1Type() {
  }

  @Override
  public String toString() {
    return "[0..1]";
  }

  @Override
  public boolean equals(Object t) {
    return this == t;
  }

  @Override
  public boolean isAllowedInFinalCode() {
    return false;
  }

  @Override
  public String getTypeAsString() {
    return "java.lang.Integer";
  }

  @Override
  public Type getDefaultFinalType() {
    return BooleanType.v();
  }

  @Override
  public Class<?> getJavaBoxedType() {
    return Integer.class;
  }

  @Override
  public Class<?> getJavaPrimitiveType() {
    return int.class;
  }

}
