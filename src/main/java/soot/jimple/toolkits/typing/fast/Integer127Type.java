package soot.jimple.toolkits.typing.fast;

import soot.ByteType;
import soot.IntegerType;
import soot.PrimType;
import soot.Type;

/**
 * @author Ben Bellamy
 */
public class Integer127Type extends PrimType implements IntegerType {
  public static final Integer127Type INSTANCE = new Integer127Type();

  public static Integer127Type v() {
    return INSTANCE;
  }

  private Integer127Type() {
  }

  @Override
  public String toString() {
    return "[0..127]";
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
    return ByteType.v();
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
