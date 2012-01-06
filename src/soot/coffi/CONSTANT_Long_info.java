/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997 Clark Verbrugge
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */







package soot.coffi;

import soot.Value;
import soot.jimple.LongConstant;

/** A constant pool entry of type CONSTANT_Long
 * @see cp_info
 * @author Clark Verbrugge
 */
class CONSTANT_Long_info extends cp_info {
   /** the upper 32 bits of the long. */
   public long high;
   /** the lower 32 bits of the long. */
   public long low;
   /** Returns the size of this cp_info object.
    * @return number of bytes occupied by this object.
    * @see cp_info#size
    */
   public int size() { return 9; }
   /** Converts the internal two-int representation to an actual long. */
   public long convert() { return ints2long(high,low); }
   /** Returns a String representation of this entry.
    * @param constant_pool constant pool of ClassFile.
    * @return String representation of this entry.
    * @see cp_info#toString
    */
   public String toString(cp_info constant_pool[]) {
      return "(" + high + "," + low + ") = " + Long.toString(convert());
   }
   /** Returns a String description of what kind of entry this is.
    * @return the String "long".
    * @see cp_info#typeName
    */
   public String typeName() { return "long"; }
   /** Compares this entry with another cp_info object (which may reside
    * in a different constant pool).
    * @param constant_pool constant pool of ClassFile for this.
    * @param cp constant pool entry to compare against.
    * @param cp_constant_pool constant pool of ClassFile for cp.
    * @return a value <0, 0, or >0 indicating whether this is smaller,
    * the same or larger than cp.
    * @see cp_info#compareTo
    */
   public int compareTo(cp_info constant_pool[],cp_info cp,cp_info cp_constant_pool[]) {
      long d;
      if (tag!=cp.tag) return tag-cp.tag;
      CONSTANT_Long_info cu = (CONSTANT_Long_info)cp;
      d = convert()-cu.convert();
      return ((d>0) ? 1 : ((d<0) ? -1 : 0));
   }

   public Value createJimpleConstantValue(cp_info[] constant_pool) {
		return LongConstant.v(convert());
	}
}
