package soot.jimple.toolkits.typing.fast;

import soot.BooleanType;
import soot.ByteType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.ShortType;
import soot.Type;

public class TypeUtils {

  /**
   * Returns the bit size of a given primitive type. Note that it returns 1 for boolean albeit not being possible on a real
   * machine.
   * 
   * @param type
   * @return the size
   */
  public static int getValueBitSize(Type type) {
    if (type instanceof BooleanType) {
      return 1;
    }
    if (type instanceof ByteType) {
      return 8;
    }
    if (type instanceof ShortType) {
      return 16;
    }
    if (type instanceof IntType) {
      return 32;
    }
    if (type instanceof LongType) {
      return 64;
    }
    if (type instanceof FloatType) {
      return 32;
    }
    if (type instanceof DoubleType) {
      return 64;
    }

    throw new IllegalArgumentException(type + " not supported.");
  }

}
