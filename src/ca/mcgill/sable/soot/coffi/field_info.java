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
                           $JimpleVersion: 0.5 $

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

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.coffi;

import java.io.*;

/** Represents a single field_info object. 
 * @see ClassFile
 * @author Clark Verbrugge
 */
class field_info {
   /** Access flags for this field. */
   public short access_flags;
   /** Constant pool index of the name of this field. 
    * @see ClassFile#constant_pool
    * @see CONSTANT_Utf8_info 
    */
   public short name_index;
   /** Constant pool index of the type descriptor of this field.
    * @see ClassFile#constant_pool
    * @see CONSTANT_Utf8_info 
    */
   public short descriptor_index;
   /** Count of attributes this field contains. */
   public short attributes_count;
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
      ConstantValue_attribute cva;
      CONSTANT_Utf8_info cm,dm;
      int i,j;
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
      ConstantValue_attribute ca;
      int i;
      for (i=0;i<attributes_count;i++) {
         if ((attributes[i]) instanceof ConstantValue_attribute)
            return (ConstantValue_attribute)(attributes[i]);
      }
      return null;
   }
   
}
