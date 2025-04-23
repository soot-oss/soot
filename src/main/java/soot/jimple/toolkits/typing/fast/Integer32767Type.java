package soot.jimple.toolkits.typing.fast;

import soot.IntegerType;
import soot.PrimType;
import soot.ShortType;
import soot.Type;

/**
 * @author Ben Bellamy
 */
public class Integer32767Type extends PrimType implements IntegerType {
  public static final Integer32767Type INSTANCE = new Integer32767Type();

  public static Integer32767Type v() {
    return INSTANCE;
  }

  private Integer32767Type() {
  }

  @Override
  public String toString() {
    return "[0..32767]";
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
    return ShortType.v();
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
