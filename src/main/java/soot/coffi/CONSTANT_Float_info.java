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

import soot.Value;
import soot.jimple.FloatConstant;

/**
 * A constant pool entry of type CONSTANT_Float
 * 
 * @see cp_info
 * @author Clark Verbrugge
 */
class CONSTANT_Float_info extends cp_info {
  /** Internal representation of the float. */
  public long bytes;

  /**
   * Returns the size of this cp_info object.
   * 
   * @return number of bytes occupied by this object.
   * @see cp_info#size
   */
  public int size() {
    return 5;
  }

  /** Converts the internal representation to an actual float. */
  public float convert() {
    return Float.intBitsToFloat((int) bytes);
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
    return Float.toString(bytes);
  }

  /**
   * Returns a String description of what kind of entry this is.
   * 
   * @return the String "float".
   * @see cp_info#typeName
   */
  public String typeName() {
    return "float";
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
    float d;
    if (tag != cp.tag) {
      return tag - cp.tag;
    }
    CONSTANT_Float_info cu = (CONSTANT_Float_info) cp;
    d = convert() - cu.convert();
    return ((d > 0.0) ? 1 : ((d < 0.0) ? -1 : 0));
  }

  public Value createJimpleConstantValue(cp_info[] constant_pool) {
    return FloatConstant.v(convert());
  }
}
