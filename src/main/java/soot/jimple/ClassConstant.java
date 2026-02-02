package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 - Jennifer Lhotak
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.PrimType;
import soot.RefType;
import soot.ShortType;
import soot.Type;
import soot.dotnet.types.DotNetBasicTypes;
import soot.options.Options;
import soot.util.StringTools;
import soot.util.Switch;

public class ClassConstant extends Constant {

  public final String value;

  protected ClassConstant(String s) {
    this.value = s;
  }

  public static ClassConstant v(String value) {
    if (value.indexOf('.') > -1) {
      throw new RuntimeException("ClassConstants must use class names separated by '/', not '.'!");
    }
    return new ClassConstant(value);
  }

  public static ClassConstant fromType(Type tp) {
    return v(sootTypeToString(tp));
  }

  private static String sootTypeToString(Type tp) {
    if (tp instanceof RefType) {
      return "L" + ((RefType) tp).getClassName().replace('.', '/') + ";";
    } else if (tp instanceof ArrayType) {
      return "[" + sootTypeToString(((ArrayType) tp).getElementType());
    } else if (tp instanceof PrimType) {
      if (tp instanceof IntType) {
        return "I";
      } else if (tp instanceof ByteType) {
        return "B";
      } else if (tp instanceof CharType) {
        return "C";
      } else if (tp instanceof DoubleType) {
        return "D";
      } else if (tp instanceof FloatType) {
        return "F";
      } else if (tp instanceof LongType) {
        return "J";
      } else if (tp instanceof ShortType) {
        return "S";
      } else if (tp instanceof BooleanType) {
        return "Z";
      } else {
        throw new RuntimeException("Unsupported primitive type");
      }
    } else {
      throw new RuntimeException("Unsupported type" + tp);
    }
  }

  /**
   * Gets whether this class constant denotes a reference type. This does not check for arrays.
   *
   * @return True if this class constant denotes a reference type, otherwise false
   */
  public boolean isRefType() {
    String tmp = this.value;
    return !tmp.isEmpty() && tmp.charAt(0) == 'L' && tmp.charAt(tmp.length() - 1) == ';';
  }

  public Type toSootType() {
    int numDimensions = 0;
    String tmp = this.value;
    while (!tmp.isEmpty() && tmp.charAt(0) == '[') {
      numDimensions++;
      tmp = tmp.substring(1);
    }

    Type baseType = null;
    if (!tmp.isEmpty() && tmp.charAt(0) == 'L') {
      tmp = tmp.substring(1);
      int lastIdx = tmp.length() - 1;
      if (!tmp.isEmpty() && tmp.charAt(lastIdx) == ';') {
        tmp = tmp.substring(0, lastIdx);
      }
      tmp = tmp.replace('/', '.');
      baseType = RefType.v(tmp);
    } else {
      switch (tmp) {
        case "I":
          baseType = IntType.v();
          break;
        case "B":
          baseType = ByteType.v();
          break;
        case "C":
          baseType = CharType.v();
          break;
        case "D":
          baseType = DoubleType.v();
          break;
        case "F":
          baseType = FloatType.v();
          break;
        case "J":
          baseType = LongType.v();
          break;
        case "S":
          baseType = ShortType.v();
          break;
        case "Z":
          baseType = BooleanType.v();
          break;
        default:
          throw new RuntimeException("Unsupported class constant: " + value);
      }
    }

    return numDimensions > 0 ? ArrayType.v(baseType, numDimensions) : baseType;
  }

  /**
   * Gets an internal representation of the class used in Java bytecode. The returned string is similar to the fully
   * qualified name but with '/' instead of '.'. Example: "java/lang/Object". See
   * https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.2.1
   */
  public String toInternalString() {
    String internal = this.value;
    while (!internal.isEmpty() && internal.charAt(0) == '[') {
      internal = internal.substring(1);
    }
    int lastIdx = internal.length() - 1;
    if (!internal.isEmpty() && internal.charAt(lastIdx) == ';') {
      internal = internal.substring(0, lastIdx);
      if (!internal.isEmpty() && internal.charAt(0) == 'L') {
        internal = internal.substring(1);
      }
    }
    return internal;
  }

  // In this case, equals should be structural equality.
  @Override
  public boolean equals(Object c) {
    return (c instanceof ClassConstant && ((ClassConstant) c).value.equals(this.value));
  }

  /** Returns a hash code for this ClassConstant object. */
  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return "class " + StringTools.getQuotedStringOf(value);
  }

  public String getValue() {
    return value;
  }

  @Override
  public Type getType() {
    if (Options.v().src_prec() == Options.src_prec_dotnet) {
      return RefType.v(DotNetBasicTypes.SYSTEM_RUNTIMETYPEHANDLE);
    }
    return RefType.v("java.lang.Class");
  }

  @Override
  public void apply(Switch sw) {
    ((ConstantSwitch) sw).caseClassConstant(this);
  }
}
