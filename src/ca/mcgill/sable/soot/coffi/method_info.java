/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Coffi, a bytecode parser for the Java(TM) language.               *
 * Copyright (C) 1996, 1997 Clark Verbrugge (clump@sable.mcgill.ca). *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.
 The reference version is: $CoffiVersion: 1.1 $

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.coffi;

import java.io.*;
import java.util.Vector;

/** Represents a single method_info object.
 * @see ClassFile
 * @author Clark Verbrugge
 */
public class method_info {
   /** Access flags for this field. */
    short access_flags;
   /** Constant pool index of the name of this method.
    * @see ClassFile#constant_pool
    * @see CONSTANT_Utf8_info
    */
    short name_index;
   /** Constant pool index of the type descriptor of this method.
    * @see ClassFile#constant_pool
    * @see CONSTANT_Utf8_info
    */
    short descriptor_index;
   /** Count of attributes this method contains. */
    short attributes_count;
   /** Array of attribute_info objects for this method.
    * @see attribute_info
    */
    attribute_info attributes[];
   /** List of Instructions constructed when the method is parsed.
    * @see ClassFile@parse
    * @see ClassFile@parseMethod
    * @see Instruction
    */
    Instruction instructions;
   /** Control Flow Graph constructed when the method is parsed.
    * @see ClassFile@parse
    * @see CFG
    */
    public CFG cfg;

    ca.mcgill.sable.soot.SootMethod jmethod;

    ca.mcgill.sable.util.List instructionList;

   /** Returns the name of this method.
    * @param constant_pool the constant_pool for this class.
    * @return the name of this method.
    */
    String toName(cp_info constant_pool[]) {
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
    String prototype(cp_info constant_pool[]) {
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
      System.out.println(prototype(constant_pool));
      ByteCode.showCode(instructions,constant_pool);
   }
}
