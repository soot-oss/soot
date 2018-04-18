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

/** Represents a single field_info object.
 * @see ClassFile
 * @author Clark Verbrugge
 */
public class field_info {
   /** Access flags for this field. */
   public int access_flags;
   /** Constant pool index of the name of this field.
    * @see ClassFile#constant_pool
    * @see CONSTANT_Utf8_info
    */
   public int name_index;
   /** Constant pool index of the type descriptor of this field.
    * @see ClassFile#constant_pool
    * @see CONSTANT_Utf8_info
    */
   public int descriptor_index;
   /** Count of attributes this field contains. */
   public int attributes_count;
   /** Array of attribute_info objects for this field.
    * @see attribute_info
    */
   public attribute_info attributes[];

   /** Returns the name of this field.
    * @param constant_pool the constant_pool for this class.
    * @return the name of this field.
    */
   public String toName(cp_info constant_pool[]) {
      CONSTANT_Utf8_info ci;
      ci = (CONSTANT_Utf8_info)(constant_pool[name_index]);
      return ci.convert();
   }

   /** Returns the prototype of this field.
    * @param constant_pool the constant_pool for this class.
    * @return the prototype (access + type + name) of this field.
    */
   public String prototype(cp_info constant_pool[]) {
      CONSTANT_Utf8_info cm,dm;
      String s;

      cm = (CONSTANT_Utf8_info)(constant_pool[name_index]);
      dm = (CONSTANT_Utf8_info)(constant_pool[descriptor_index]);
      s = ClassFile.access_string(access_flags," ");
      if (s.compareTo("")!=0) s = s + " ";
      return s + ClassFile.parseDesc(dm.convert(),"") + " " + cm.convert();
   }

   /** Locates a constant value attribute if one exists.
     * @return the constant value attribute or <i>null</i>.
     * @see ConstantValue_attribute
     */
   public ConstantValue_attribute findConstantValue_attribute() {
      int i;
      for (i=0;i<attributes_count;i++) {
         if ((attributes[i]) instanceof ConstantValue_attribute)
            return (ConstantValue_attribute)(attributes[i]);
      }
      return null;
   }

}
