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
import java.util.Enumeration;

/** A constant pool entry of type CONSTANT_Utf8; note this is <b>not</b>
 * multithread safe.
 * @see cp_info
 * @author Clark Verbrugge
 */
public class CONSTANT_Utf8_info extends cp_info {
   // Some local private objects to help with efficient comparisons.
   private static Utf8_Enumeration e1 = new Utf8_Enumeration();
   private static Utf8_Enumeration e2 = new Utf8_Enumeration();
   // for caching the conversion.
   private String s;
   /** Byte array of actual utf8 string. */
   public byte bytes[];
   /** Length in bytes of byte array. */
   public int length() { 
      return (((((int)(bytes[0]))&0xff)<<8) + (((int)(bytes[1]))&0xff)); 
   }
   /** Returns the size of this cp_info object.
    * @return number of bytes occupied by this object.
    * @see cp_info#size
    */
   public int size() { return length()+3; } 
   /** Converts internal representation into an actual String. 
    * @return String version of this utf8 object.
    */
   public String convert() {
      if (s==null) {
         try {
            ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
            DataInputStream d = new DataInputStream(bs);
            s = d.readUTF();
         } catch(IOException e) {
            return "!!IOException!!";
         }
      } 
      return s;
   }
   /** Answers whether this utf8 string is the same as a given one.
    * @param cu utf8 object with which to compare.
    * @return <i>true</i> if they are equal, <i>false</i> if they are not.
    */
   public boolean equals(CONSTANT_Utf8_info cu) {
      int i,j;
      j = bytes.length;
      if (j!=cu.bytes.length) return false;
      for (i=0; i<j; i++) {
         if (bytes[i]!=cu.bytes[i]) return false;
      }
      return true;
   }
   /** Compares this entry with another cp_info object (which may reside
    * in a different constant pool).
    * @param constant_pool constant pool of ClassFile for this.
    * @param cp constant pool entry to compare against.
    * @param cp_constant_pool constant pool of ClassFile for cp.
    * @return a value <0, 0, or >0 indicating whether this is smaller,
    * the same or larger than cp.
    * @see cp_info#compareTo
    * @see CONSTANT_Utf8_info#compareTo(cp_info)
    */
   public int compareTo(cp_info constant_pool[],cp_info cp,cp_info cp_constant_pool[]) {
      return compareTo(cp);
   }
   /** Compares this entry with another cp_info object; note that for Utf8
    * object it really doesn't matter whether they're in the same or a different
    * constant pool, since they really do carry all their data.
    * @param cp constant pool entry to compare against.
    * @return a value <0, 0, or >0 indicating whether this is smaller,
    * the same or larger than cp.
    * @see cp_info#compareTo
    * @see CONSTANT_Utf8_info#compareTo(cp_info[],cp_info,cp_info[])
    */
   public int compareTo(cp_info cp) {
      if (tag!=cp.tag) return tag-cp.tag;
      CONSTANT_Utf8_info cu = (CONSTANT_Utf8_info)cp;
      e1.reset(bytes);
      e2.reset(cu.bytes);
      for (;e1.hasMoreElements() && e2.hasMoreElements();) {
         e1.nextElement();
         e2.nextElement();
         if (e1.c<e2.c) return -1;
         if (e2.c<e1.c) return 1;
      }
      if (e1.hasMoreElements()) return -1;
      if (e2.hasMoreElements()) return 1;
      return 0;
   }
   /** Utility method; converts the given String into a utf8 encoded array 
    * of bytes.
    * @param s String to encode.
    * @return array of bytes, utf8 encoded version of s.
    */
   public static byte[] toUtf8(String s) {
      try {
         ByteArrayOutputStream bs = new ByteArrayOutputStream(s.length());
         DataOutputStream d = new DataOutputStream(bs);
         d.writeUTF(s);
         return bs.toByteArray();
      } catch(IOException e) {
         System.out.println("Some sort of IO exception in toUtf8 with " + s);
      }
      return null;
   }
   /** Returns a String representation of this entry.
    * @param constant_pool constant pool of ClassFile.
    * @return String representation of this entry.
    * @see cp_info#toString
    */
   public String toString(cp_info constant_pool[]) {
      return convert();
   }
   /** Returns a String description of what kind of entry this is.
    * @return the String "utf8".
    * @see cp_info#typeName
    */
   public String typeName() { return "utf8"; }
}
