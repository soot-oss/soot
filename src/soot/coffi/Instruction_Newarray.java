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
class Instruction_Newarray extends Instruction {
   public static final int T_BOOLEAN = 4;
   public static final int T_CHAR = 5;
   public static final int T_FLOAT = 6;
   public static final int T_DOUBLE = 7;
   public static final int T_BYTE = 8;
   public static final int T_SHORT = 9;
   public static final int T_INT = 10;
   public static final int T_LONG = 11;
   /** one of the T_* constants. */
   public byte atype;
   public Instruction_Newarray() { super((byte)ByteCode.NEWARRAY); name = "newarray"; }
   public String toString(cp_info constant_pool[]) {
      String args;
      switch((int)atype) {
      case T_BOOLEAN: args = "boolean"; break;
      case T_CHAR: args = "char"; break;
      case T_FLOAT: args = "float"; break;
      case T_DOUBLE: args = "double"; break;
      case T_BYTE: args = "byte"; break;
      case T_SHORT: args = "short"; break;
      case T_INT: args = "int"; break;
      case T_LONG: args = "long"; break;
      default: args = Integer.toString(atype); break;
      }
      return super.toString(constant_pool) + argsep + args;
   }
   public int nextOffset(int curr) { return curr+2; }
   public int parse(byte bc[],int index) {
      atype = bc[index];
      return index+1;
   }
   public int compile(byte bc[],int index) {
      bc[index++] = code;
      bc[index] = atype;
      return index+1;
   }
}
