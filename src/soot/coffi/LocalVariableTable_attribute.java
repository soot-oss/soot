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
import soot.*;

import java.io.*;

/** A debugging attribute, this gives the names of local variables
 * within blocks of bytecode.
 * @see attribute_info
 * @author Clark Verbrugge
 */
class LocalVariableTable_attribute extends attribute_info {
   /** Length of the local variable table. */
   public int local_variable_table_length;
   /** Actual table of local variables. */
   public local_variable_table_entry local_variable_table[];

   /** Locates the first name found for a given local variable.
    * @param constant_pool constant pool for the associated class.
    * @param idx local variable index.
    * @return name of the local variable, or <i>null</i> if not found.
    * @see LocalVariableTable_attribute#getLocalVariableName(cp_info[], int, int)
    */
   public String getLocalVariableName(cp_info constant_pool[],int idx) {
      return getLocalVariableName(constant_pool,idx,-1);
   }
   /** Locates the name of the given local variable for the specified code offset.
    * @param constant_pool constant pool for the associated class.
    * @param idx local variable index.
    * @param code code offset for variable name; use -1 to return the first name found
    * for that local variable.
    * @return name of the local variable, or <i>null</i> if not found.
    * @see LocalVariableTable_attribute#getLocalVariableName(cp_info[], int)
    */
   public String getLocalVariableName(cp_info constant_pool[],int idx,int code) {
      local_variable_table_entry e;
      CONSTANT_Utf8_info cu;
      int i;

      // G.v().out.println("searching for name of local: " + idx + "at: " + code);
      // now to find that variable
      for (i=0;i<local_variable_table_length;i++) {
         e = local_variable_table[i];
         if (e.index==idx &&
             (code==-1 ||
	      (code>=e.start_pc && code<=e.start_pc+e.length))){
	      //  (code>=e.start_pc && code<e.start_pc+e.length))) {
            // found the variable, now find its name.
            
            //G.v().out.println("found entry: " + i);

            if (constant_pool[e.name_index] instanceof CONSTANT_Utf8_info)
	    {
	       String n = ((CONSTANT_Utf8_info)(constant_pool[e.name_index])).convert();
	       if (Util.v().isValidJimpleName(n))
		   return n;
	       else
		   return null;
	    }
            else {
               throw new RuntimeException( "What? A local variable table "
                       +"name_index isn't a UTF8 entry?");
            }
         }
      }
      return null;
   }
   
   public String toString()
   {
        StringBuffer buffer = new StringBuffer();
        
        for(int i = 0; i < local_variable_table_length; i++)
        {
            buffer.append(local_variable_table[i].toString() + "\n");
        }
        
        return buffer.toString();
   }
}

