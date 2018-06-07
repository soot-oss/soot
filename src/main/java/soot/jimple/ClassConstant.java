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
import soot.util.StringTools;
import soot.util.Switch;

public class ClassConstant extends Constant {
  public final String value;

  private ClassConstant(String s) {
    this.value = s;
  }

  public static ClassConstant v(String value) {
    if (value.contains(".")) {
      throw new RuntimeException("ClassConstants must use class names separated by '/', not '.'!");
    }
    return new ClassConstant(value);
  }

  public static ClassConstant fromType(Type tp) {
    return v(sootTypeToString(tp));
  }

  private static String sootTypeToString(Type tp) {
    if (tp instanceof RefType) {
      return "L" + ((RefType) tp).getClassName().replaceAll("\\.", "/") + ";";
    } else if (tp instanceof ArrayType) {
      ArrayType at = (ArrayType) tp;
      return "[" + sootTypeToString(at.getElementType());
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
        return "L";
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
    return value.startsWith("L") && value.endsWith(";");
  }

  public Type toSootType() {
    int numDimensions = 0;
    String tmp = value;
    while (tmp.startsWith("[")) {
      numDimensions++;
      tmp = tmp.substring(1);
    }

    Type baseType = null;
    if (tmp.startsWith("L")) {
      tmp = tmp.substring(1);
      if (tmp.endsWith(";")) {
        tmp = tmp.substring(0, tmp.length() - 1);
      }
      tmp = tmp.replace("/", ".");
      baseType = RefType.v(tmp);
    } else if (tmp.equals("I")) {
      baseType = IntType.v();
    } else if (tmp.equals("B")) {
      baseType = ByteType.v();
    } else if (tmp.equals("C")) {
      baseType = CharType.v();
    } else if (tmp.equals("D")) {
      baseType = DoubleType.v();
    } else if (tmp.equals("F")) {
      baseType = FloatType.v();
    } else if (tmp.equals("L")) {
      baseType = LongType.v();
    } else if (tmp.equals("S")) {
      baseType = ShortType.v();
    } else if (tmp.equals("Z")) {
      baseType = BooleanType.v();
    } else {
      throw new RuntimeException("Unsupported class constant: " + value);
    }

    return numDimensions > 0 ? ArrayType.v(baseType, numDimensions) : baseType;
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

  public Type getType() {
    return RefType.v("java.lang.Class");
  }

  public void apply(Switch sw) {
    ((ConstantSwitch) sw).caseClassConstant(this);
  }
}
