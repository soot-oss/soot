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

import java.util.*;
import java.io.*;
import java.util.Vector;
import soot.util.*;

/** Represents a single method_info object.
 * @see ClassFile
 * @author Clark Verbrugge
 */
public class method_info {
   /** Access flags for this field. */
    public int access_flags;
   /** Constant pool index of the name of this method.
    * @see ClassFile#constant_pool
    * @see CONSTANT_Utf8_info
    */
    public int name_index;
   /** Constant pool index of the type descriptor of this method.
    * @see ClassFile#constant_pool
    * @see CONSTANT_Utf8_info
    */
    public int descriptor_index;
   /** Count of attributes this method contains. */
    public int attributes_count;
   /** Array of attribute_info objects for this method.
    * @see attribute_info
    */
    public attribute_info attributes[];

    /** A shortcut into attributes array for Code_attribute
     * @see Code_attribute
     */
    public Code_attribute code_attr;

   /** List of Instructions constructed when the method is parsed.
    * @see Instruction
    */
    public Instruction instructions;
   /** Control Flow Graph constructed when the method is parsed.
    * @see CFG
    */
    public CFG cfg;

    public soot.SootMethod jmethod;

    List instructionList;

   /** Returns the name of this method.
    * @param constant_pool the constant_pool for this class.
    * @return the name of this method.
    */
    public String toName(cp_info constant_pool[]) {
      CONSTANT_Utf8_info ci;
      ci = (CONSTANT_Utf8_info)(constant_pool[name_index]);
      return ci.convert();
   }

   /** Locates and returns the code attribute for this method.
    * @return the single code attribute, or null if not found.
    * @see Code_attribute
    */
    Code_attribute locate_code_attribute() {
      attribute_info ai;
      int i;

      for (i=0; i<attributes_count; i++) {
         ai = attributes[i];
         if (ai instanceof Code_attribute)
            return (Code_attribute)ai;
      }
      return null;
   }

   /** Returns the prototype of this field.
    * @param constant_pool the constant_pool for this class.
    * @return the prototype (access + return + name + parameters) of this method.
    */
    public String prototype(cp_info constant_pool[]) {
      String access,rt,name,params;
      Code_attribute c = locate_code_attribute();

      access = ClassFile.access_string(access_flags," ");
      rt = ClassFile.parseMethodDesc_return(cp_info.getTypeDescr(constant_pool,
                                                                 descriptor_index));
      name = toName(constant_pool);
      params = ClassFile.parseMethodDesc_params(cp_info.
                                                getTypeDescr(constant_pool,
                                                             descriptor_index));
      if (access.length()>0)
         return access + " " + rt + " " + name + "(" + params + ")";
      return rt + " " + name + "(" + params + ")";
   }

   /** Displays this method, printing a prototype followed by list of Instructions.
    * @param constant_pool the constant_pool for this class.
    * @see prototype
    * @see ByteCode#showCode
    */
    void print(cp_info constant_pool[]) {
      G.v().out.println(prototype(constant_pool));
      ByteCode.showCode(instructions,constant_pool);
   }
}
