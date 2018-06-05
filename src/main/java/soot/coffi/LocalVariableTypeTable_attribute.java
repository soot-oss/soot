package soot.coffi;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Jennifer Lhotak
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A debugging attribute, this gives the types of local variables within blocks of bytecode. - for java 1.5
 * 
 * @see attribute_info
 * @author Jennifer Lhotak modified from LocalVariableTable_attribute
 */
class LocalVariableTypeTable_attribute extends attribute_info {
  private static final Logger logger = LoggerFactory.getLogger(LocalVariableTypeTable_attribute.class);
  /** Length of the local variable type table. */
  public int local_variable_type_table_length;
  /** Actual table of local variable types. */
  public local_variable_type_table_entry local_variable_type_table[];

  /**
   * Locates the first type found for a given local variable.
   * 
   * @param constant_pool
   *          constant pool for the associated class.
   * @param idx
   *          local variable type index.
   * @return type of the local variable, or <i>null</i> if not found.
   * @see LocalVariableTypeTable_attribute#getLocalVariableType(cp_info[], int, int)
   */
  public String getLocalVariableType(cp_info constant_pool[], int idx) {
    return getLocalVariableType(constant_pool, idx, -1);
  }

  /**
   * Locates the type of the given local variable for the specified code offset.
   * 
   * @param constant_pool
   *          constant pool for the associated class.
   * @param idx
   *          local variable type index.
   * @param code
   *          code offset for variable name; use -1 to return the first name found for that local variable.
   * @return type of the local variable, or <i>null</i> if not found.
   * @see LocalVariableTypeTable_attribute#getLocalVariableType(cp_info[], int)
   */
  public String getLocalVariableType(cp_info constant_pool[], int idx, int code) {
    local_variable_type_table_entry e;
    int i;

    // logger.debug("searching for type of local: " + idx + "at: " + code);
    // now to find that variable
    for (i = 0; i < local_variable_type_table_length; i++) {
      e = local_variable_type_table[i];
      if (e.index == idx && (code == -1 || (code >= e.start_pc && code <= e.start_pc + e.length))) {
        // (code>=e.start_pc && code<e.start_pc+e.length))) {
        // found the variable, now find its name.

        // logger.debug("found entry: " + i);

        if (constant_pool[e.signature_index] instanceof CONSTANT_Utf8_info) {
          String n = ((CONSTANT_Utf8_info) (constant_pool[e.signature_index])).convert();
          // logger.debug("found type: "+n);
          // if (Util.v().isValidJimpleName(n))
          // return n;
          // else
          // return null;
        } else {
          throw new RuntimeException("What? A local variable type table " + "signature_index isn't a UTF8 entry?");
        }
      }
    }
    return null;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();

    for (int i = 0; i < local_variable_type_table_length; i++) {
      buffer.append(local_variable_type_table[i].toString() + "\n");
    }

    return buffer.toString();
  }
}
