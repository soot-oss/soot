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
import java.util.Enumeration;

/** A constant pool entry of type CONSTANT_Utf8; note this is <b>not</b>
 * multithread safe.  It is, however, immutable.
 * @see cp_info
 * @author Clark Verbrugge
 */
public class CONSTANT_Utf8_info extends cp_info {
   // Some local private objects to help with efficient comparisons.
   private int sHashCode;
   // for caching the conversion.
   private String s;
   /** Byte array of actual utf8 string. */
   private byte bytes[];
   /** Constructor from a DataInputSream */
   public CONSTANT_Utf8_info(DataInputStream d) throws IOException {
          int len;
          len = d.readUnsignedShort();
          bytes = new byte[len+2];
          bytes[0] = (byte)(len>>8);
          bytes[1] = (byte)(len & 0xff);
          if (len>0) {
          int j;
                 for (j=0; j<len;j++)
                    bytes[j+2] = (byte)d.readUnsignedByte();
          }
   }
   /** For writing out the byte stream for this utf8 properly (incl size). */
   public void writeBytes(DataOutputStream dd) throws IOException {
          int len;
          len = bytes.length;
          dd.writeShort(len-2);
          dd.write(bytes,2,len-2);
   }
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
            String buf = d.readUTF();
            sHashCode = buf.hashCode();
            return buf;
         } catch(IOException e) {
            return "!!IOException!!";
         }
      }
      return s;
   }
   /** Fixes the actual String used to represent the internal representation.
    * We must have rep == convert(); we verify hashCodes() to spot-check this.
    * No user-visible effects.
    */
   public void fixConversion(String rep) {
      if (sHashCode != rep.hashCode())
         throw new RuntimeException("bad use of fixConversion!");

      if (s == null) {
         s = rep;
      }
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
      G.v().coffi_CONSTANT_Utf8_info_e1.reset(bytes);
      G.v().coffi_CONSTANT_Utf8_info_e2.reset(cu.bytes);
      for (;G.v().coffi_CONSTANT_Utf8_info_e1.hasMoreElements() && G.v().coffi_CONSTANT_Utf8_info_e2.hasMoreElements();) {
         G.v().coffi_CONSTANT_Utf8_info_e1.nextElement();
         G.v().coffi_CONSTANT_Utf8_info_e2.nextElement();
         if (G.v().coffi_CONSTANT_Utf8_info_e1.c<G.v().coffi_CONSTANT_Utf8_info_e2.c) return -1;
         if (G.v().coffi_CONSTANT_Utf8_info_e2.c<G.v().coffi_CONSTANT_Utf8_info_e1.c) return 1;
      }
      if (G.v().coffi_CONSTANT_Utf8_info_e1.hasMoreElements()) return -1;
      if (G.v().coffi_CONSTANT_Utf8_info_e2.hasMoreElements()) return 1;
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
         G.v().out.println("Some sort of IO exception in toUtf8 with " + s);
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
