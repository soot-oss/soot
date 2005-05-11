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

import java.io.*;

/** There should be exactly one code attribute in every method; there
 * may also be a code attribute associated with a field (as an
 * initializer).
 * @see attribute_info
 * @see method_info#attributes
 * @see field_info#attributes
 * @see ClassFile@attributes
 * @author Clark Verbrugge
 */
class Code_attribute extends attribute_info {
   /** Maximum size of the operand stack. */
   public int max_stack;
   /** Maximum number of locals required. */

   public int max_locals;
   /** Length of code array. */
   public long code_length;
   /** Actual array of bytecode. */
   public byte code[];
   /** Length of exception table array. */
   public int exception_table_length;
   /** Exception table array.
    * @see exception_table_entry
    */
   public exception_table_entry exception_table[];
   /** Length of attributes array. */
   int attributes_count;
   /** Array of attributes.
    * @see attribute_info
    */
   attribute_info attributes[];

   /** Locates the LocalVariableTable attribute, if one is present.
    * @return the local variable table attribute, or <i>null</i> if not found.
    * @see LocalVariableTable_attribute
    * @see method_info#makeLocals
    */
   public LocalVariableTable_attribute findLocalVariableTable() {
      int i;
      for (i=0;i<attributes_count;i++) {
         if (attributes[i] instanceof LocalVariableTable_attribute)
            return (LocalVariableTable_attribute)(attributes[i]);
      }
      return null;
   }
   
   /** Locates the LocalVariableTypeTable attribute, if one is present.
    * @return the local variable type table attribute, or <i>null</i> 
    * if not found.
    * @see LocalVariableTypeTable_attribute
    * @see method_info#makeLocals
    */
   public LocalVariableTypeTable_attribute findLocalVariableTypeTable() {
      int i;
      for (i=0;i<attributes_count;i++) {
         if (attributes[i] instanceof LocalVariableTypeTable_attribute)
            return (LocalVariableTypeTable_attribute)(attributes[i]);
      }
      return null;
   }
}
