package soot.util.backend;

import java.util.List;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import org.objectweb.asm.ByteVector;
import org.objectweb.asm.ClassWriter;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.ShortType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethodRef;
import soot.Type;
import soot.TypeSwitch;
import soot.VoidType;
import soot.baf.DoubleWordType;
import soot.options.Options;
import soot.tagkit.Attribute;
import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;
import soot.tagkit.StringConstantValueTag;
import soot.tagkit.Tag;

/**
 * Utility class for ASM-based back-ends.
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class ASMBackendUtils {

  /**
   * Convert class identifiers and signatures by replacing dots by slashes.
   *
   * @param s
   *          String to convert
   * @return Converted identifier
   */
  public static String slashify(String s) {
    if (s == null) {
      return null;
    }
    return s.replace('.', '/');
  }

  /**
   * Compute type description for methods, comprising parameter types and return type.
   *
   * @param m
   *          Method to determine type description
   * @return Method type description
   */
  public static String toTypeDesc(SootMethodRef m) {
    return toTypeDesc(m.parameterTypes(), m.returnType());
  }

  /**
   * Compute type description for methods, comprising parameter types and return type.
   * 
   * @param parameterTypes
   *          The parameters for some method
   * @param returnType
   *          The return type for some method
   * @return Method type description
   */
  public static String toTypeDesc(List<Type> parameterTypes, Type returnType) {
    StringBuilder sb = new StringBuilder();
    sb.append('(');
    for (Type t : parameterTypes) {
      sb.append(toTypeDesc(t));
    }
    sb.append(')');
    sb.append(toTypeDesc(returnType));
    return sb.toString();
  }

  /**
   * Convert type to JVM style type description
   *
   * @param type
   *          Type to convert
   * @return JVM style type description
   */
  public static String toTypeDesc(Type type) {
    final StringBuilder sb = new StringBuilder(1);
    type.apply(new TypeSwitch() {

      @Override
      public void defaultCase(Type t) {
        throw new RuntimeException("Invalid type " + t.toString());
      }

      @Override
      public void caseDoubleType(DoubleType t) {
        sb.append('D');
      }

      @Override
      public void caseFloatType(FloatType t) {
        sb.append('F');
      }

      @Override
      public void caseIntType(IntType t) {
        sb.append('I');
      }

      @Override
      public void caseByteType(ByteType t) {
        sb.append('B');
      }

      @Override
      public void caseShortType(ShortType t) {
        sb.append('S');
      }

      @Override
      public void caseCharType(CharType t) {
        sb.append('C');
      }

      @Override
      public void caseBooleanType(BooleanType t) {
        sb.append('Z');
      }

      @Override
      public void caseLongType(LongType t) {
        sb.append('J');
      }

      @Override
      public void caseArrayType(ArrayType t) {
        sb.append('[');
        t.getElementType().apply(this);
      }

      @Override
      public void caseRefType(RefType t) {
        sb.append('L');
        sb.append(slashify(t.getClassName()));
        sb.append(';');
      }

      @Override
      public void caseVoidType(VoidType t) {
        sb.append('V');
      }
    });
    return sb.toString();
  }

  /**
   * Get default value of a field for constant pool
   *
   * @param field
   *          Field to get default value for
   * @return Default value or <code>null</code> if there is no default value.
   */
  public static Object getDefaultValue(SootField field) {
    for (Tag t : field.getTags()) {
      switch (t.getName()) {
        case IntegerConstantValueTag.NAME:
          return ((IntegerConstantValueTag) t).getIntValue();
        case LongConstantValueTag.NAME:
          return ((LongConstantValueTag) t).getLongValue();
        case FloatConstantValueTag.NAME:
          return ((FloatConstantValueTag) t).getFloatValue();
        case DoubleConstantValueTag.NAME:
          return ((DoubleConstantValueTag) t).getDoubleValue();
        case StringConstantValueTag.NAME:
          // Default value for string may only be returned if the field is of type String or a sub-type.
          if (acceptsStringInitialValue(field)) {
            return ((StringConstantValueTag) t).getStringValue();
          }
      }
    }
    return null;
  }

  /**
   * Determine if the field accepts a string default value, this is only true for fields of type String or a sub-type of
   * String
   *
   * @param field
   *          Field
   * @return <code>true</code> if the field is of type String or sub-type, <code>false</code> otherwise.
   */
  public static boolean acceptsStringInitialValue(SootField field) {
    if (field.getType() instanceof RefType) {
      SootClass fieldClass = ((RefType) field.getType()).getSootClass();
      return fieldClass.getName().equals("java.lang.String");
    }
    return false;
  }

  /**
   * Get the size in words for a type.
   *
   * @param t
   *          Type
   * @return Size in words
   */
  public static int sizeOfType(Type t) {
    if (t instanceof DoubleWordType || t instanceof LongType || t instanceof DoubleType) {
      return 2;
    } else if (t instanceof VoidType) {
      return 0;
    } else {
      return 1;
    }
  }

  /**
   * Create an ASM attribute from an Soot attribute
   *
   * @param attr
   *          Soot attribute
   * @return ASM attribute
   */
  public static org.objectweb.asm.Attribute createASMAttribute(Attribute attr) {
    final Attribute a = attr;
    return new org.objectweb.asm.Attribute(attr.getName()) {
      @Override
      protected ByteVector write(final ClassWriter cw, final byte[] code, final int len, final int maxStack,
          final int maxLocals) {
        ByteVector result = new ByteVector();
        result.putByteArray(a.getValue(), 0, a.getValue().length);
        return result;
      }
    };
  }

  /**
   * Translate internal numbering of java versions to real version for debug messages.
   *
   * @param javaVersion
   *          Internal java version number
   * @return Java version in the format "1.7"
   */
  public static String translateJavaVersion(int javaVersion) {
    if (javaVersion == Options.java_version_default) {
      return "1.0";
    } else {
      return "1." + (javaVersion - 1);
    }
  }
}
