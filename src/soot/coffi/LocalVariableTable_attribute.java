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

import java.util.*;

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
   
   private List<ProcessedLocalName> m_processedNames;
   
   public void processNames(cp_info[] constant_pool){
     if(m_processedNames != null){
       return; 
     }
     m_processedNames = new ArrayList<ProcessedLocalName>();
     Map<String, List<Integer>> type_map = new HashMap<String, List<Integer>>();
     
     for(int i=0; i < local_variable_table_length; i++) {
       local_variable_table_entry e = local_variable_table[i];
    
       if (constant_pool[e.name_index] instanceof CONSTANT_Utf8_info) {
	       String name = ((CONSTANT_Utf8_info)(constant_pool[e.name_index])).convert();
	       if(type_map.containsKey(name)){
	         List<Integer> types = type_map.get(name);
	         int type = e.descriptor_index;
	         if(types.contains(type) == false){
	           types.add(type);
	         }
	       } else {
	         List<Integer> types = new ArrayList<Integer>();
	         types.add(e.descriptor_index);
	         type_map.put(name, types);
	       }   
	     }
	   }
     
     for(int i=0; i < local_variable_table_length; i++) {
       local_variable_table_entry e = local_variable_table[i];
    
       if (constant_pool[e.name_index] instanceof CONSTANT_Utf8_info) {
	       String name = ((CONSTANT_Utf8_info)(constant_pool[e.name_index])).convert();
	       String processed_name = name;
	       if(type_map.containsKey(name)){
	         List<Integer> types = type_map.get(name);
	         if(types.size() > 1){
	           int type = e.descriptor_index;
	           int index = types.indexOf(type);
	           processed_name += index;
	         }
	       }
	       ProcessedLocalName curr = new ProcessedLocalName();
	       curr.index = e.index;
	       curr.start_pc = e.start_pc;
	       curr.end_pc = e.start_pc+e.length;
	       curr.name = processed_name;
	       m_processedNames.add(curr);
	     }
	   }
   }
   
   private class ProcessedLocalName {
     int index;
     int start_pc;
     int end_pc;
     String name;
   }

   /** Locates the first name found for a given local variable.
    * @param constant_pool constant pool for the associated class.
    * @param idx local variable index.
    * @return name of the local variable, or <i>null</i> if not found.
    * @see LocalVariableTable_attribute#getLocalVariableName(cp_info[], int, int)
    */
   public String getLocalVariableName(int idx) {
      return getLocalVariableName(idx,-1);
   }
   /** Locates the name of the given local variable for the specified code offset.
    * @param constant_pool constant pool for the associated class.
    * @param idx local variable index.
    * @param code code offset for variable name; use -1 to return the first name found
    * for that local variable.
    * @return name of the local variable, or <i>null</i> if not found.
    * @see LocalVariableTable_attribute#getLocalVariableName(cp_info[], int)
    */
   public String getLocalVariableName(int idx,int code) {
   
      for(ProcessedLocalName curr : m_processedNames){
        if(curr.index == idx && (code == -1 || (code >= curr.start_pc && code < curr.end_pc))){
          String n = curr.name;
          if (Util.v().isValidJimpleName(n))
		        return n;
	        else
		        return null;
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

