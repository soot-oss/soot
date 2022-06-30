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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A debugging attribute, this gives the names of local variables within blocks of bytecode.
 * 
 * @see attribute_info
 * @author Clark Verbrugge
 */
class LocalVariableTable_attribute extends attribute_info {
  private static final Logger logger = LoggerFactory.getLogger(LocalVariableTable_attribute.class);
  /** Length of the local variable table. */
  public int local_variable_table_length;
  /** Actual table of local variables. */
  public local_variable_table_entry local_variable_table[];

  /**
   * Locates the first name found for a given local variable.
   * 
   * @param constant_pool
   *          constant pool for the associated class.
   * @param idx
   *          local variable index.
   * @return name of the local variable, or <i>null</i> if not found.
   * @see LocalVariableTable_attribute#getLocalVariableName(cp_info[], int, int)
   */
  public String getLocalVariableName(cp_info constant_pool[], int idx) {
    return getLocalVariableName(constant_pool, idx, -1);
  }

  /**
   * Locates the name of the given local variable for the specified code offset.
   * 
   * @param constant_pool
   *          constant pool for the associated class.
   * @param idx
   *          local variable index.
   * @param code
   *          code offset for variable name; use -1 to return the first name found for that local variable.
   * @return name of the local variable, or <i>null</i> if not found.
   * @see LocalVariableTable_attribute#getLocalVariableName(cp_info[], int)
   */
  public String getLocalVariableName(cp_info constant_pool[], int idx, int code) {
    local_variable_table_entry e;
    int i;

    // logger.debug("searching for name of local: " + idx + "at: " + code);
    // now to find that variable
    for (i = 0; i < local_variable_table_length; i++) {
      e = local_variable_table[i];
      if (e.index == idx && (code == -1 || (code >= e.start_pc && code < e.start_pc + e.length))) {
        // (code>=e.start_pc && code<e.start_pc+e.length))) {
        // found the variable, now find its name.

        // logger.debug("found entry: " + i);

        if (constant_pool[e.name_index] instanceof CONSTANT_Utf8_info) {
          String n = ((CONSTANT_Utf8_info) (constant_pool[e.name_index])).convert();
          if (Util.v().isValidJimpleName(n)) {
            return n;
          } else {
            return null;
          }
        } else {
          throw new RuntimeException("What? A local variable table " + "name_index isn't a UTF8 entry?");
        }
      }
    }
    return null;
  }

  public String getLocalVariableDescriptor(cp_info constant_pool[], int idx, int code) {
    local_variable_table_entry e;
    int i;

    for (i = 0; i < local_variable_table_length; i++) {
      e = local_variable_table[i];
      if (e.index == idx && (code == -1 || (code >= e.start_pc && code < e.start_pc + e.length))) {
        if (constant_pool[e.descriptor_index] instanceof CONSTANT_Utf8_info) {
          String n = ((CONSTANT_Utf8_info) (constant_pool[e.descriptor_index])).convert();
          return n;
        } else {
          throw new RuntimeException("What? A local variable table " + "name_index isn't a UTF8 entry?");
        }
      }
    }
    return null;
  }

  public String getEntryName(cp_info constant_pool[], int entryIndex) {
    try {
      local_variable_table_entry e = local_variable_table[entryIndex];
      if (constant_pool[e.name_index] instanceof CONSTANT_Utf8_info) {
        String n = ((CONSTANT_Utf8_info) (constant_pool[e.name_index])).convert();
        if (Util.v().isValidJimpleName(n)) {
          return n;
        } else {
          return null;
        }
      } else {
        throw new RuntimeException("name_index not addressing an UTF8 entry.");
      }
    } catch (ArrayIndexOutOfBoundsException x) {
      return null;
    }
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();

    for (int i = 0; i < local_variable_table_length; i++) {
      buffer.append(local_variable_table[i].toString() + "\n");
    }

    return buffer.toString();
  }
}
