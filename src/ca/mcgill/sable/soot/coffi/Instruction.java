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
                           $SootVersion$

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

 - Modified on September 3, by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Installed code to print original index of bytecodes from classfile
   for easy debugging.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.coffi;
import java.io.*;
/** Instruction subclasses are used to represent parsed bytecode; each
 * bytecode operation has a corresponding subclass of Instruction.
 * <p>
 * Each subclass is derived from one of
 * <ul><li>Instruction</li>
 * <li>Instruction_noargs (an Instruction with no embedded arguments)</li>
 * <li>Instruction_byte (an Instruction with a single byte data argument)</li>
 * <li>Instruction_bytevar (a byte argument specifying a local variable)</li>
 * <li>Instruction_byteindex (a byte argument specifying a constant pool index)</li>
 * <li>Instruction_int (an Instruction with a single short data argument)</li>
 * <li>Instruction_intvar (a short argument specifying a local variable)</li>
 * <li>Instruction_intindex (a short argument specifying a constant pool index)</li>
 * <li>Instruction_intbranch (a short argument specifying a code offset)</li>
 * <li>Instruction_longbranch (an int argument specifying a code offset)</li>
 * </ul>
 * @author Clark Verbrugge
 * @see Instruction
 * @see Instruction_noargs
 * @see Instruction_byte
 * @see Instruction_bytevar
 * @see Instruction_byteindex
 * @see Instruction_int
 * @see Instruction_intvar
 * @see Instruction_intindex
 * @see Instruction_intbranch
 * @see Instruction_longbranch
 * @see Instruction_Unknown
 */
 abstract class Instruction {

   /** String used to separate arguments in printing. */
   public static final String argsep = " ";
   /** String used to construct names for local variables. */
   public static final String LOCALPREFIX = "local_";
   // public static int w; // set by the wide instr. and used by other instrs

   /** Actual byte code of this instruction. */
   public byte code;
   /** Offset of this instruction from the start of code.
    * @see ClassFile#relabel
    */
   public int label;
   /** Name of this instruction.
    * @see Instruction#toString
    */
   public String name;

   /** Reference for chaining. */
   public Instruction next;
   /** Whether this instruction is the target of a branch. */
   public boolean labelled;
   /** Whether this instruction branches. */
   public boolean branches;
   /** Whether this instruction is a method invocation. */
   public boolean calls;
   /** Whether this instruction is a return. */
   public boolean returns;

   int originalIndex;

   /** Constructs a new Instruction for this bytecode.
    * @param c bytecode of the instruction.
    */
   public Instruction(byte c) {
      code = c;
      next = null;
      branches = false;
      calls = false;
      returns = false;
   }

   public String toString()
   {
      return name + "[" + originalIndex + "]";
   }

   /** Assuming the actual bytecode for this instruction has been extracted already,
    * and index is the offset of the next byte, this method parses whatever
    * arguments the instruction requires and return the offset of the next
    * available byte.
    * @param bc complete array of bytecode.
    * @param index offset of remaining bytecode after this instruction's
    * bytecode was parsed.
    * @return offset of the next available bytecode.
    * @see ByteCode#disassemble_bytecode
    * @see Instruction#compile
    */
   public abstract int parse(byte bc[],int index);

   /** Writes out the sequence of bytecodes represented by this instruction, including
    * any arguments.
    * @param bc complete array of bytecode.
    * @param index offset of remaining bytecode at which to start writing.
    * @return offset of the next available bytecode.
    * @see ClassFile#unparseMethod
    * @see Instruction#parse
    */
   public abstract int compile(byte bc[],int index);

   /** Changes offset values in this instruction to Instruction references;
    * default behaviour is to do nothing.
    * @param bc complete array of bytecode.
    * @see ByteCode#build
    */
   public void offsetToPointer(ByteCode bc) { }


   /** Returns the next available offset assuming this instruction begins
    * on the indicated offset; default assumes no arguments.
    * @param curr offset this instruction would be on.
    * @return next available offset.
    * @see ClassFile#relabel
    */
   public int nextOffset(int curr) { return curr+1; }

   /** Returns an array of the instructions to which this instruction
    * might branch (only valid if branches==<i>true</i>; default action is
    * to return <i>null</i>).
    * @param next the instruction following this one, in case of default flow through.
    * @return array of instructions which may be targets of this instruction.
    * @see Instruction#branches
    */
   public Instruction[] branchpoints(Instruction next) { return null; }

   /** Marks the appropriate spot if that constant_pool entry is used by this instr.
    * For every constant pool entry used (referenced) by this instruction, the
    * corresponding boolean in the given array is set to <i>true</i>.
    * @param refs array of booleans the same size as the constant pool array.
    * @see ClassFile#constant_pool
    */
   public void markCPRefs(boolean[] refs) { }

   /** Updates all constant pool references within this instruction to use
    * new indices, based on the given redirection array.
    * @param redirect array of new indices of constant pool entries.
    * @see ClassFile#constant_pool
    */
   public void redirectCPRefs(short redirect[]) { }

   /** For storing in a Hashtable.
    * @return unique hash code for this instruction, assuming labels are unique.
    */
   public int hashCode() {
      return (new Integer(label)).hashCode();
   }

   /** For storing in a Hashtable.
    * @param i the Instruction to which this is compared.
    * @return <i>true</i> if <i>i</i> is the same, <i>false</i> otherwise.
    */
   public boolean equals(Instruction i) {
      if (label == i.label) return true;
      return false;
   }

   /** Utility routines, used mostly by the parse routines of various
    * Instruction subclasses;
    * this method converts two bytes into a short.
    * @param bc complete array of bytecode.
    * @param index offset of data in bc.
    * @return the short constructed from the two bytes.
    * @see Instruction#parse
    * @see Instruction#shortToBytes
    */
   public static short getShort(byte bc[],int index) {
      short s,bh,bl;
      bh = (short)(bc[index]); bl = (short)(bc[index+1]);
      s = (short)(((bh<<8)&0xff00) | (bl&0xff));
      //s = (short)((int)(bc[index])<<8 + bc[index+1]);
      return s;
   }
   /** Utility routines, used mostly by the parse routines of various
    * Instruction subclasses;
    * this method converts four bytes into an int.
    * @param bc complete array of bytecode.
    * @param index offset of data in bc.
    * @return the int constructed from the four bytes.
    * @see Instruction#parse
    * @see Instruction#intToBytes
    */
   public static int getInt(byte bc[],int index) {
      int i,bhh,bhl,blh,bll;
      bhh = (((int)(bc[index]))<<24)&0xff000000;
      bhl = (((int)(bc[index+1]))<<16)&0xff0000;
      blh = (((int)(bc[index+2]))<<8)&0xff00;
      bll = ((int)(bc[index+3]))&0xff;
      i = bhh | bhl | blh | bll;
      return i;
   }

   /** Utility routines, used mostly by the compile routines of various
    * Instruction subclasses;
    * this method converts a short into two bytes.
    * @param bc complete array of bytecode in which to store the short.
    * @param index next available offset in bc.
    * @return the next available offset in bc
    * @see Instruction#compile
    * @see Instruction#getShort
    */
   public static int shortToBytes(short s,byte bc[],int index) {
      bc[index++] = (byte)((s>>8)&0xff);
      bc[index++] = (byte)(s&0xff);
      return index;
   }

   /** Utility routines, used mostly by the compile routines of various
    * Instruction subclasses;
    * this method converts an int into four bytes.
    * @param bc complete array of bytecode in which to store the int.
    * @param index next available offset in bc.
    * @return the next available offset in bc
    * @see Instruction#compile
    * @see Instruction#getInt
    */
   public static int intToBytes(int s,byte bc[],int index) {
      bc[index++] = (byte)((s>>24)&0xff);
      bc[index++] = (byte)((s>>16)&0xff);
      bc[index++] = (byte)((s>>8)&0xff);
      bc[index++] = (byte)(s&0xff);
      return index;
   }

   /** For displaying instructions.
    * @param constant_pool constant pool of associated ClassFile
    * @return String representation of this instruction.
    */
   public String toString(cp_info constant_pool[]) {
      int i = ((int)code)&0xff;
      if (name==null) name = "null???=" + Integer.toString(i);
      return name;
   }
}
