package soot.dexpler;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 *
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

import org.jf.dexlib2.iface.reference.TypeReference;
import org.jf.dexlib2.immutable.reference.ImmutableTypeReference;

import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.ShortType;
import soot.Type;
import soot.UnknownType;
import soot.VoidType;

/**
 * Wrapper for a dexlib TypeIdItem.
 *
 */
public class DexType {

  protected String name;

  protected TypeReference type;

  public DexType(TypeReference type) {
    if (type == null) {
      throw new RuntimeException("error: type ref is null!");
    }
    this.type = type;
    this.name = type.getType();
  }

  public DexType(String type) {
    if (type == null) {
      throw new RuntimeException("error: type is null!");
    }
    this.type = new ImmutableTypeReference(type);
    this.name = type;
  }

  public String getName() {
    return name;
  }

  public boolean overwriteEquivalent(DexType field) {
    return name.equals(field.getName());
  }

  public TypeReference getType() {
    return type;
  }

  /**
   * Return the appropriate Soot Type for this DexType.
   *
   * @return the Soot Type
   */
  public Type toSoot() {
    return toSoot(type.getType(), 0);
  }

  /**
   * Return the appropriate Soot Type for the given TypeReference.
   *
   * @param type
   *          the TypeReference to convert
   * @return the Soot Type
   */
  public static Type toSoot(TypeReference type) {
    return toSoot(type.getType(), 0);
  }

  public static Type toSoot(String type) {
    return toSoot(type, 0);
  }

  /**
   * Return if the given TypeIdItem is wide (i.e. occupies 2 registers).
   *
   * @param typeReference.getType()
   *          the TypeIdItem to analyze
   * @return if type is wide
   */
  public static boolean isWide(TypeReference typeReference) {
    String t = typeReference.getType();
    return isWide(t);
  }

  public static boolean isWide(String type) {
    return type.startsWith("J") || type.startsWith("D");
  }

  /**
   * Determine the soot type from a byte code type descriptor.
   *
   */
  private static Type toSoot(String typeDescriptor, int pos) {
    Type type;
    char typeDesignator = typeDescriptor.charAt(pos);
    // see https://code.google.com/p/smali/wiki/TypesMethodsAndFields
    switch (typeDesignator) {
      case 'Z': // boolean
        type = BooleanType.v();
        break;
      case 'B': // byte
        type = ByteType.v();
        break;
      case 'S': // short
        type = ShortType.v();
        break;
      case 'C': // char
        type = CharType.v();
        break;
      case 'I': // int
        type = IntType.v();
        break;
      case 'J': // long
        type = LongType.v();
        break;
      case 'F': // float
        type = FloatType.v();
        break;
      case 'D': // double
        type = DoubleType.v();
        break;
      case 'L': // object
        type = RefType.v(Util.dottedClassName(typeDescriptor));
        break;
      case 'V': // void
        type = VoidType.v();
        break;
      case '[': // array
        type = toSoot(typeDescriptor, pos + 1).makeArrayType();
        break;
      default:
        type = UnknownType.v();
    }

    return type;
  }

  /**
   * Seems that representation of Annotation type in Soot is not consistent with the normal type representation. Normal type
   * representation would be a.b.c.ClassName Java bytecode representation is La/b/c/ClassName; Soot Annotation type
   * representation (and Jasmin's) is a/b/c/ClassName.
   *
   * This method transforms the Java bytecode representation into the Soot annotation type representation.
   *
   * Ljava/lang/Class<Ljava/lang/Enum<*>;>; becomes java/lang/Class<java/lang/Enum<*>>
   *
   * @param type
   * @param pos
   * @return
   */
  public static String toSootICAT(String type) {
    type = type.replace(".", "/");

    String r = "";
    String[] split1 = type.split(";");
    for (String s : split1) {
      if (s.startsWith("L")) {
        s = s.replaceFirst("L", "");
      }
      if (s.startsWith("<L")) {
        s = s.replaceFirst("<L", "<");
      }
      r += s;
    }
    return r;
  }

  public static String toDalvikICAT(String type) {
    type = type.replaceAll("<", "<L");
    type = type.replaceAll(">", ">;");
    type = "L" + type; // a class name cannot be a primitive
    type = type.replaceAll("L\\*;", "*");
    if (!type.endsWith(";")) {
      type += ";";
    }
    return type;
  }

  /**
   * Types read from annotations should be converted to Soot type. However, to maintain compatibility with Soot code most
   * type will not be converted.
   *
   * @param type
   * @return
   */
  public static String toSootAT(String type) {
    return type;
  }

  @Override
  public String toString() {
    return name;
  }
}
