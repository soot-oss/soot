package soot.asm;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2014 Raja Vallee-Rai and others
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

import java.util.ArrayList;
import java.util.List;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.RefLikeType;
import soot.RefType;
import soot.ShortType;
import soot.SootClass;
import soot.Type;
import soot.VoidType;

/**
 * Contains static utility methods.
 * 
 * @author Aaloan Miftah
 */
/**
 * @author eric
 *
 */
public class AsmUtil {

  /**
   * Determines if a type is a dword type.
   * 
   * @param type
   *          the type to check.
   * @return {@code true} if its a dword type.
   */
  public static boolean isDWord(Type type) {
    return type instanceof LongType || type instanceof DoubleType;
  }

  /**
   * Converts an internal class name to a Type.
   * 
   * @param internal
   *          internal name.
   * @return type
   */
  public static Type toBaseType(String internal) {
    if (internal.charAt(0) == '[') {
      /* [Ljava/lang/Object; */
      internal = internal.substring(internal.lastIndexOf('[') + 1, internal.length());
      /* Ljava/lang/Object */
    }
    if (internal.charAt(internal.length() - 1) == ';') {
      internal = internal.substring(0, internal.length() - 1);
      // we need to have this guarded by a ; check as you can have a situation
      // were a call is called Lxxxxx with now leading package name. Rare, but it
      // happens. However, you need to strip the leading L it will always be
      // followed by a ; per
      // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
      if (internal.charAt(0) == 'L') {
        internal = internal.substring(1, internal.length());
      }
      internal = toQualifiedName(internal);
      return RefType.v(internal);
    }
    switch (internal.charAt(0)) {
      case 'Z':
        return BooleanType.v();
      case 'B':
        return ByteType.v();
      case 'C':
        return CharType.v();
      case 'S':
        return ShortType.v();
      case 'I':
        return IntType.v();
      case 'F':
        return FloatType.v();
      case 'J':
        return LongType.v();
      case 'D':
        return DoubleType.v();
      default:
        internal = toQualifiedName(internal);
        return RefType.v(internal);
    }
  }

  /**
   * Converts an internal class name to a fully qualified name.
   * 
   * @param internal
   *          internal name.
   * @return fully qualified name.
   */
  public static String toQualifiedName(String internal) {
    return internal.replace('/', '.');
  }

  /**
   * Converts a fully qualified class name to an internal name.
   * 
   * @param qual
   *          fully qualified class name.
   * @return internal name.
   */
  public static String toInternalName(String qual) {
    return qual.replace('.', '/');
  }

  /**
   * Determines and returns the internal name of a class.
   * 
   * @param cls
   *          the class.
   * @return corresponding internal name.
   */
  public static String toInternalName(SootClass cls) {
    return toInternalName(cls.getName());
  }

  /**
   * Converts a type descriptor to a Jimple reference type.
   * 
   * @param desc
   *          the descriptor.
   * @return the reference type.
   */
  public static Type toJimpleRefType(String desc) {
    return desc.charAt(0) == '[' ? toJimpleType(desc) : RefType.v(toQualifiedName(desc));
  }

  /**
   * Converts a type descriptor to a Jimple type.
   * 
   * @param desc
   *          the descriptor.
   * @return equivalent Jimple type.
   */
  public static Type toJimpleType(String desc) {
    int idx = desc.lastIndexOf('[');
    int nrDims = idx + 1;
    if (nrDims > 0) {
      if (desc.charAt(0) != '[') {
        throw new AssertionError("Invalid array descriptor: " + desc);
      }
      desc = desc.substring(idx + 1);
    }
    Type baseType;
    switch (desc.charAt(0)) {
      case 'Z':
        baseType = BooleanType.v();
        break;
      case 'B':
        baseType = ByteType.v();
        break;
      case 'C':
        baseType = CharType.v();
        break;
      case 'S':
        baseType = ShortType.v();
        break;
      case 'I':
        baseType = IntType.v();
        break;
      case 'F':
        baseType = FloatType.v();
        break;
      case 'J':
        baseType = LongType.v();
        break;
      case 'D':
        baseType = DoubleType.v();
        break;
      case 'L':
        if (desc.charAt(desc.length() - 1) != ';') {
          throw new AssertionError("Invalid reference descriptor: " + desc);
        }
        String name = desc.substring(1, desc.length() - 1);
        name = toQualifiedName(name);
        baseType = RefType.v(name);
        break;
      default:
        throw new AssertionError("Unknown descriptor: " + desc);
    }
    if (!(baseType instanceof RefLikeType) && desc.length() > 1) {
      throw new AssertionError("Invalid primitive type descriptor: " + desc);
    }
    return nrDims > 0 ? ArrayType.v(baseType, nrDims) : baseType;
  }

  /**
   * Converts a method signature to a list of types, with the last entry in the returned list denoting the return type.
   * 
   * @param desc
   *          method signature.
   * @return list of types.
   */
  public static List<Type> toJimpleDesc(String desc) {
    ArrayList<Type> types = new ArrayList<Type>(2);
    int len = desc.length();
    int idx = 0;
    all: while (idx != len) {
      int nrDims = 0;
      Type baseType = null;
      this_type: while (idx != len) {
        char c = desc.charAt(idx++);
        switch (c) {
          case '(':
          case ')':
            continue all;
          case '[':
            ++nrDims;
            continue this_type;
          case 'Z':
            baseType = BooleanType.v();
            break this_type;
          case 'B':
            baseType = ByteType.v();
            break this_type;
          case 'C':
            baseType = CharType.v();
            break this_type;
          case 'S':
            baseType = ShortType.v();
            break this_type;
          case 'I':
            baseType = IntType.v();
            break this_type;
          case 'F':
            baseType = FloatType.v();
            break this_type;
          case 'J':
            baseType = LongType.v();
            break this_type;
          case 'D':
            baseType = DoubleType.v();
            break this_type;
          case 'V':
            baseType = VoidType.v();
            break this_type;
          case 'L':
            int begin = idx;
            while (desc.charAt(++idx) != ';') {
              ;
            }
            String cls = desc.substring(begin, idx++);
            baseType = RefType.v(toQualifiedName(cls));
            break this_type;
          default:
            throw new AssertionError("Unknown type: " + c);
        }
      }
      if (baseType != null && nrDims > 0) {
        types.add(ArrayType.v(baseType, nrDims));
      } else {
        types.add(baseType);
      }
    }
    return types;
  }

  /**
   * strips suffix for indicating an array type
   */
  public static String baseTypeName(String s) {
    int index = s.indexOf("[");
    if (index < 0) {
      return s;
    } else {
      return s.substring(0, index);
    }
  }

  private AsmUtil() {
  }

}
