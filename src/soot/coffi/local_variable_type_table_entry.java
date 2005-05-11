/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Jennifer Lhotak
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

/** An entry in a local variable type table.
 * @see LocalVariableTypeTable_attribute
 * @author Jennifer Lhotak
 * based on local_variabl_table_entry
 */
class local_variable_type_table_entry {
   /** Code offset of start of code wherein this entry applies. */
   public int start_pc;
   /** Length of code sequence in which this name applies. */
   public int length;
   /** Constant pool index of string giving this local variable's name.
    * @see CONSTANT_Utf8_info
    */
   public int name_index;
   /** Constant pool index of string giving this local variable's 
    * signature
    * @see CONSTANT_Utf8_info
    */
   public int signature_index;
   /** The index in the local variable array of this local variable. */
   public int index;
   
   public String toString()
   {
        return "start: " + start_pc + "length: " + length + "name_index: " + name_index + "signature_index: " + signature_index + "index: " + index ;
        
        
   }
}
