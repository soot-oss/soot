package soot.coffi;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 Clark Verbrugge
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

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Value;

/**
 * Base abstract class for constant pool entries; includes some utility methods.
 *
 * @see ClassFile#constant_pool
 * @author Clark Verbrugge
 */
abstract class cp_info {
  private static final Logger logger = LoggerFactory.getLogger(cp_info.class);

  public static final byte CONSTANT_Utf8 = 1;
  public static final byte CONSTANT_Integer = 3;
  public static final byte CONSTANT_Float = 4;
  public static final byte CONSTANT_Long = 5;
  public static final byte CONSTANT_Double = 6;
  public static final byte CONSTANT_Class = 7;
  public static final byte CONSTANT_String = 8;
  public static final byte CONSTANT_Fieldref = 9;
  public static final byte CONSTANT_Methodref = 10;
  public static final byte CONSTANT_InterfaceMethodref = 11;
  public static final byte CONSTANT_NameAndType = 12;
  public static final byte CONSTANT_MethodHandle = 15;
  public static final byte CONSTANT_MethodType = 16; // TODO
  public static final byte CONSTANT_InvokeDynamic = 18;

  /* constants for method handle kinds */
  public static final byte REF_getField = 1;
  public static final byte REF_getStatic = 2;
  public static final byte REF_putField = 3;
  public static final byte REF_putStatic = 4;
  public static final byte REF_invokeVirtual = 5;
  public static final byte REF_invokeStatic = 6;
  public static final byte REF_invokeSpecial = 7;
  public static final byte REF_newInvokeSpecial = 8;
  public static final byte REF_invokeInterface = 9;

  // mapping from the above to the kinds of members they refer to
  public static final byte[] REF_TO_CONSTANT = { -1, CONSTANT_Fieldref, // getField...
      CONSTANT_Fieldref, //
      CONSTANT_Fieldref, //
      CONSTANT_Fieldref, //
      CONSTANT_Methodref, // invokeVirtual...
      CONSTANT_Methodref, CONSTANT_Methodref, CONSTANT_Methodref, CONSTANT_InterfaceMethodref,// invokeInterface
  };

  /** One of the CONSTANT_* constants. */
  public byte tag;

  /**
   * Returns the size of this entry.
   *
   * @return size (in bytes) of this entry.
   */
  public abstract int size();

  /**
   * Returns a String representation of this entry.
   *
   * @param constant_pool
   *          constant pool of ClassFile.
   * @return String representation of this entry.
   */
  public abstract String toString(cp_info constant_pool[]);

  /**
   * Returns a String description of what kind of entry this is.
   *
   * @return String representation of this kind of entry.
   */
  public abstract String typeName();

  /**
   * Compares this entry with another cp_info object (which may reside in a different constant pool).
   *
   * @param constant_pool
   *          constant pool of ClassFile for this.
   * @param cp
   *          constant pool entry to compare against.
   * @param cp_constant_pool
   *          constant pool of ClassFile for cp.
   * @return a value <0, 0, or >0 indicating whether this is smaller, the same or larger than cp.
   */
  public abstract int compareTo(cp_info constant_pool[], cp_info cp, cp_info cp_constant_pool[]);

  /**
   * Utility method, converts two integers into a single long.
   *
   * @param high
   *          upper 32 bits of the long.
   * @param low
   *          lower 32 bits of the long.
   * @return a long value composed from the two ints.
   */
  public static long ints2long(long high, long low) {
    long h, l;
    h = high;
    l = low;
    return ((h << 32) + l);
  }

  /**
   * Utility method, returns a String binary representation of the given integer.
   *
   * @param i
   *          the integer in question.
   * @return a String of 0's and 1's.
   * @see cp_info#printBits(long)
   */
  public static String printBits(int i) {
    String s = "";
    int j, k;
    k = 1;
    for (j = 0; j < 32; j++) {
      if ((i & k) != 0) {
        s = "1" + s;
      } else {
        s = "0" + s;
      }
      k = k << 1;
    }
    return s;
  }

  /**
   * Utility method, returns a String binary representation of the given long.
   *
   * @param i
   *          the long in question.
   * @return a String of 0's and 1's.
   * @see cp_info#printBits(int)
   */
  public static String printBits(long i) {
    String s = "";
    long j, k;
    k = 1;
    for (j = 0; j < 64; j++) {
      if ((i & k) != 0) {
        s = "1" + s;
      } else {
        s = "0" + s;
      }
      k = k << 1;
    }
    return s;
  }

  /**
   * Locates the name of the corresponding class, given the constant pool index of either a CONSTANT_Class, _Fieldref,
   * Methodref or InterfaceMethodref.
   *
   * @param constant_pool
   *          constant pool of ClassFile.
   * @param i
   *          index of cp_info entry in question.
   * @return name of the associated class.
   */
  public static String getClassname(cp_info constant_pool[], int i) {
    cp_info c = constant_pool[i];
    switch (c.tag) {
      case cp_info.CONSTANT_Class:
        return c.toString(constant_pool);
      case cp_info.CONSTANT_Fieldref:
        return getClassname(constant_pool, ((CONSTANT_Fieldref_info) c).class_index);
      case cp_info.CONSTANT_Methodref:
        return getClassname(constant_pool, ((CONSTANT_Methodref_info) c).class_index);
      case cp_info.CONSTANT_InterfaceMethodref:
        return getClassname(constant_pool, ((CONSTANT_InterfaceMethodref_info) c).class_index);
    }
    logger.debug("Request for classname for non-class object!");
    return "Can't find classname. Sorry.";
  }

  /**
   * Returns the name of the given constant pool object, assuming it is of type CONSTANT_NameAndType, _FieldRef, _Methodref
   * or _InterfaceMethodref.
   *
   * @param constant_pool
   *          constant pool of ClassFile.
   * @param i
   *          index of cp_info entry in question.
   * @return name of the associated object.
   * @see CONSTANT_Utf8_info
   */
  public static String getName(cp_info constant_pool[], int i) {
    cp_info c = constant_pool[i];
    switch (c.tag) {
      case cp_info.CONSTANT_Utf8:
        return c.toString(constant_pool);
      case cp_info.CONSTANT_NameAndType:
        return getName(constant_pool, ((CONSTANT_NameAndType_info) c).name_index);
      case cp_info.CONSTANT_Fieldref:
        return getName(constant_pool, ((CONSTANT_Fieldref_info) c).name_and_type_index);
      case cp_info.CONSTANT_Methodref:
        return getName(constant_pool, ((CONSTANT_Methodref_info) c).name_and_type_index);
      case cp_info.CONSTANT_InterfaceMethodref:
        return getName(constant_pool, ((CONSTANT_InterfaceMethodref_info) c).name_and_type_index);
    }
    logger.debug("Request for name for non-named object!");
    return "Can't find name of that object. Sorry.";
  }

  /**
   * Counts the number of parameters of the given method.
   *
   * @param constant_pool
   *          constant pool of ClassFile.
   * @param m
   *          a constant pool index as accepted by getTypeDescr.
   * @return the number of parameters.
   * @see cp_info#getTypeDescr
   */
  public static int countParams(cp_info constant_pool[], int m) {
    StringTokenizer st;
    String s = getTypeDescr(constant_pool, m);
    s = ClassFile.parseMethodDesc_params(s);
    st = new StringTokenizer(s, ",", false);
    return st.countTokens();
  }

  /**
   * Returns the type descriptor for the given constant pool object, which must be a CONSTANT_Utf8, CONSTANT_NameAndType,
   * CONSTANT_Fieldref, CONSTANT_MethodRef, or CONSTANT_InterfaceMethodRef.
   *
   * @param constant_pool
   *          constant pool of ClassFile.
   * @param i
   *          a constant pool index for an entry of type CONSTANT_Utf8, CONSTANT_NameAndType, CONSTANT_MethodRef, or
   *          CONSTANT_InterfaceMethodRef.
   * @return the type descriptor.
   * @see CONSTANT_Utf8_info
   */
  public static String getTypeDescr(cp_info constant_pool[], int i) {
    cp_info c = constant_pool[i];
    if (c instanceof CONSTANT_Utf8_info) {
      return c.toString(constant_pool);
    }
    if (c instanceof CONSTANT_NameAndType_info) {
      return getTypeDescr(constant_pool, ((CONSTANT_NameAndType_info) c).descriptor_index);
    }
    if (c instanceof CONSTANT_Methodref_info) {
      return getTypeDescr(constant_pool, ((CONSTANT_Methodref_info) c).name_and_type_index);
    }
    if (c instanceof CONSTANT_InterfaceMethodref_info) {
      return getTypeDescr(constant_pool, ((CONSTANT_InterfaceMethodref_info) c).name_and_type_index);
    }
    if (c instanceof CONSTANT_Fieldref_info) {
      return getTypeDescr(constant_pool, ((CONSTANT_Fieldref_info) c).name_and_type_index);
    }
    logger.debug("Invalid request for type descr!");
    return "Invalid type descriptor request.";
  }

  /**
   * Returns the name of the field type of the given constant pool object.
   *
   * @param constant_pool
   *          constant pool of ClassFile.
   * @param i
   *          a constant pool index for an entry of type CONSTANT_Utf8, CONSTANT_NameAndType, or CONSTANT_FieldRef.
   * @return the type of the field.
   * @see CONSTANT_Utf8_info
   * @see cp_info#getTypeDescr
   */
  public static String fieldType(cp_info constant_pool[], int i) {
    return ClassFile.parseDesc(getTypeDescr(constant_pool, i), "");
  }

  /**
   * Creates an appropriate jimple representation of this constant. Field and method constants are assumed to point to static
   * fields/methods.
   */
  public abstract Value createJimpleConstantValue(cp_info[] constant_pool);
}
