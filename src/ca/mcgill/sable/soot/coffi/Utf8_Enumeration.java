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

import java.util.Enumeration;

/** An enumeration of a Utf8 allows one to run through the characters in a 
 * unicode string; used primarily for comparing unicode strings.  Note that
 * unlike regular enumerations, and to be efficient (ie to avoid allocating
 * an object in each call to nextElement), the return value is accessed by
 * this object's 'c' field, and nextElement merely returns this.
 * @see CONSTANT_Utf8_info
 * @see Utf8_Enumeration#c
 * @see Utf8_Enumeration#nextElement
 * @author Clark Verbrugge
 */
class Utf8_Enumeration implements Enumeration {
   
   /** The latest character, as determined by nextElement.
    * @see Utf8_Enumeration#nextElement
    */
   public int c;            // latest character
   
   private short curindex;
   private short length;
   private byte bytes[];
   
   /** For creating an empty enumeration; you must use reset() after this
    * to initialize the enumeration.
    * @see Utf8_Enumeration#reset
    */
   public Utf8_Enumeration() {}
   /** For creating a normal enumeration of the given Utf8 string. 
    * @param b array of bytes in Utf8 format.
    */
   public Utf8_Enumeration(byte b[]) {
      bytes = b;
      curindex = (short)2;
      length = (short)(((((int)(bytes[0]))&0xff)<<8) + (((int)(bytes[1]))&0xff) + 2);
   }
   /** Resets this object to be an enumeration of the given Utf8 string.
    * @param b array of bytes in Utf8 format.
    */
   public void reset(byte b[]) {
      bytes = b;
      curindex = (short)2;
      length = (short)(((((int)(bytes[0]))&0xff)<<8) + (((int)(bytes[1]))&0xff) + 2);
   }
   
   /** <i>true</i> if the entire string hasn't been enumerated yet. */
   public boolean hasMoreElements() {
      if (curindex<length) return true;
      return false;
   }

   /** Determines the next Utf8 character, and stores it in c.
    * @return <i>this</i>
    * @see Utf8_Enumeration#c
    */
   public Object nextElement() {
      byte b;
      b = bytes[curindex++];
      if ((b&((byte)0x80))==0) { // one-byte character
         c = b;
      } else if ((b&((byte)0xe0))==0xc0) { // two-byte character
         c = ((int)(b&((byte)0x1f)))<<6;
         b = bytes[curindex++];
         c |= (int)(b&((byte)0x3f));
      } else {  // three-byte character
         c = ((int)(b&((byte)0x0f)))<<12;
         b = bytes[curindex++];
         c |= ((int)(b&((byte)0x3f)))<<6;
         b = bytes[curindex++];
         c |= (int)(b&((byte)0x3f));
      }
      return this;
   }
}
