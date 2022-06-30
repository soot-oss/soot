package soot.coffi;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Eric Bodden
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

import soot.Value;

/**
 * A constant pool entry of type CONSTANT_InvokeDynamic
 *
 * @see cp_info
 * @author Eric Bodden
 */
class CONSTANT_InvokeDynamic_info extends cp_info {

  public int bootstrap_method_index;

  public int name_and_type_index;

  public int size() {
    return 5;
  }

  /**
   * Returns a String representation of this entry.
   *
   * @param constant_pool
   *          constant pool of ClassFile.
   * @return String representation of this entry.
   * @see cp_info#toString
   */
  public String toString(cp_info constant_pool[]) {
    // currently neglects field "kind"
    cp_info bsm = constant_pool[bootstrap_method_index];
    cp_info nat = constant_pool[name_and_type_index];
    return nat.toString(constant_pool) + " - " + bsm.toString(constant_pool);
  }

  /**
   * Returns a String description of what kind of entry this is.
   *
   * @return the String "methodhandle".
   * @see cp_info#typeName
   */
  public String typeName() {
    return "invokedynamic";
  }

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
   * @see cp_info#compareTo
   */
  public int compareTo(cp_info constant_pool[], cp_info cp, cp_info cp_constant_pool[]) {
    int i;
    if (tag != cp.tag) {
      return tag - cp.tag;
    }
    CONSTANT_InvokeDynamic_info cu = (CONSTANT_InvokeDynamic_info) cp;
    i = constant_pool[bootstrap_method_index].compareTo(constant_pool, cp_constant_pool[cu.bootstrap_method_index],
        cp_constant_pool);
    if (i != 0) {
      return i;
    }
    i = constant_pool[name_and_type_index].compareTo(constant_pool, cp_constant_pool[cu.name_and_type_index],
        cp_constant_pool);
    return i;
  }

  public Value createJimpleConstantValue(cp_info[] constant_pool) {
    throw new UnsupportedOperationException("cannot convert to Jimple: " + typeName());
  }
}
