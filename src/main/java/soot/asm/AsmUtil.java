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

import com.google.common.base.Optional;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.ModuleRefType;
import soot.ModuleUtil;
import soot.RefLikeType;
import soot.RefType;
import soot.ShortType;
import soot.SootClass;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.jimple.AssignStmt;
import soot.options.Options;

/**
 * Contains static utility methods.
 *
 * @author Aaloan Miftah
 */
/** @author eric */
public class AsmUtil {
  private static final Logger logger = LoggerFactory.getLogger(AsmUtil.class);

  private static RefType makeRefType(String className, Optional<String> moduleName) {
    if (ModuleUtil.module_mode()) {
      return ModuleRefType.v(className, moduleName);
    }
    return RefType.v(className);
  }

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
  public static Type toBaseType(String internal, Optional<String> moduleName) {
    if (internal.charAt(0) == '[') {
      /* [Ljava/lang/Object; */
      internal = internal.substring(internal.lastIndexOf('[') + 1);
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
        internal = internal.substring(1);
      }
      internal = toQualifiedName(internal);
      return makeRefType(internal, moduleName);
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
        return makeRefType(internal, moduleName);
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
  public static Type toJimpleRefType(String desc, Optional<String> moduleName) {
    return desc.charAt(0) == '[' ? toJimpleType(desc, moduleName) : makeRefType(toQualifiedName(desc), moduleName);
  }

  /**
   * Converts a type descriptor to a Jimple type.
   *
   * @param desc
   *          the descriptor.
   * @return equivalent Jimple type.
   */
  public static Type toJimpleType(String desc, Optional<String> moduleName) {
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
        baseType = makeRefType(name, moduleName);
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
  public static List<Type> toJimpleDesc(String desc, Optional<String> moduleName) {
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
            }
            String cls = desc.substring(begin, idx++);
            baseType = makeRefType(toQualifiedName(cls), moduleName);
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

  /** strips suffix for indicating an array type */
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

  public static int byteCodeToJavaVersion(int bytecodeVersion) {
    int javaVersion;

    switch (bytecodeVersion) {
      case (Opcodes.V1_5):
        javaVersion = Options.java_version_5;
        break;
      case (Opcodes.V1_6):
        javaVersion = Options.java_version_6;
        break;
      case (Opcodes.V1_7):
        javaVersion = Options.java_version_7;
        break;
      case (Opcodes.V1_8):
        javaVersion = Options.java_version_8;
        break;
      case (Opcodes.V9):
        javaVersion = Options.java_version_9;
        break;
      case (Opcodes.V10):
        javaVersion = Options.java_version_10;
        break;
      case (Opcodes.V11):
        javaVersion = Options.java_version_11;
        break;
      case (Opcodes.V12):
        javaVersion = Options.java_version_12;
        break;
      default:
        // we return 0 if we cannot determine the version to indicate that
        javaVersion = Options.java_version_default;
    }

    return javaVersion;
  }

  public static int javaToBytecodeVersion(int javaVersion) {
    int bytecodeVersion;

    switch (javaVersion) {
      case (Options.java_version_1):
        bytecodeVersion = Opcodes.V1_1;
        break;
      case (Options.java_version_2):
        bytecodeVersion = Opcodes.V1_2;
        break;
      case (Options.java_version_3):
        bytecodeVersion = Opcodes.V1_3;
        break;
      case (Options.java_version_4):
        bytecodeVersion = Opcodes.V1_4;
        break;
      case (Options.java_version_5):
        bytecodeVersion = Opcodes.V1_5;
        break;
      case (Options.java_version_6):
        bytecodeVersion = Opcodes.V1_6;
        break;
      case (Options.java_version_7):
        bytecodeVersion = Opcodes.V1_7;
        break;
      case (Options.java_version_8):
        bytecodeVersion = Opcodes.V1_8;
        break;
      case (Options.java_version_9):
        bytecodeVersion = Opcodes.V9;
        break;
      case (Options.java_version_10):
        bytecodeVersion = Opcodes.V10;
        break;
      case (Options.java_version_11):
        bytecodeVersion = Opcodes.V11;
        break;
      case (Options.java_version_12):
        bytecodeVersion = Opcodes.V12;
        break;
      default:
        bytecodeVersion = Opcodes.V1_7;
    }

    return bytecodeVersion;
  }

  static boolean alreadyExists(Unit prev, Object left, Object right) {
    if (prev instanceof AssignStmt) {
      AssignStmt prevAsign = (AssignStmt) prev;
      if (prevAsign.getLeftOp().equivTo(left) && prevAsign.getRightOp().equivTo(right)) {
        return true;
      }
    }
    return false;
  }


  public static Type[] jimpleTypesOfFieldOrMethodDescriptor(String descriptor) {
    Type[] ret = null;
    char[] d = descriptor.toCharArray();
    int p = 0;
    List<Type> conversionTypes = new ArrayList<Type>();

    outer: while (p < d.length) {
      boolean isArray = false;
      int numDimensions = 0;
      Type baseType = null;

      swtch: while (p < d.length) {
        switch (d[p]) {
          // Skip parenthesis
          case '(':
          case ')':
            p++;
            continue outer;

          case '[':
            isArray = true;
            numDimensions++;
            p++;
            continue swtch;
          case 'B':
            baseType = ByteType.v();
            p++;
            break swtch;
          case 'C':
            baseType = CharType.v();
            p++;
            break swtch;
          case 'D':
            baseType = DoubleType.v();
            p++;
            break swtch;
          case 'F':
            baseType = FloatType.v();
            p++;
            break swtch;
          case 'I':
            baseType = IntType.v();
            p++;
            break swtch;
          case 'J':
            baseType = LongType.v();
            p++;
            break swtch;
          case 'L':
            int index = p + 1;
            while (index < d.length && d[index] != ';') {
              if (d[index] == '/') {
                d[index] = '.';
              }
              index++;
            }
            if (index >= d.length) {
              throw new RuntimeException("Class reference has no ending ;");
            }
            String className = new String(d, p + 1, index - p - 1);
            baseType = RefType.v(className);
            p = index + 1;
            break swtch;
          case 'S':
            baseType = ShortType.v();
            p++;
            break swtch;
          case 'Z':
            baseType = BooleanType.v();
            p++;
            break swtch;
          case 'V':
            baseType = VoidType.v();
            p++;
            break swtch;
          default:
            throw new RuntimeException("Unknown field type!");
        }
      }
      if (baseType == null) {
        continue;
      }

      // Determine type
      Type t;
      if (isArray) {
        t = ArrayType.v(baseType, numDimensions);
      } else {
        t = baseType;
      }

      conversionTypes.add(t);
    }

    ret = conversionTypes.toArray(new Type[0]);
    return ret;
  }

  /**
   * Utility method; converts the given String into a utf8 encoded array of bytes.
   *
   * @param s
   *          String to encode.
   * @return array of bytes, utf8 encoded version of s.
   */
  public static byte[] toUtf8(String s) {
    try {
      ByteArrayOutputStream bs = new ByteArrayOutputStream(s.length());
      DataOutputStream d = new DataOutputStream(bs);
      d.writeUTF(s);
      return bs.toByteArray();
    } catch (IOException e) {
      logger.debug("Some sort of IO exception in toUtf8 with " + s);
    }
    return null;
  }


}
