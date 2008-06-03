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
class Instruction_Tableswitch extends Instruction {
   public Instruction_Tableswitch() {
      super((byte)ByteCode.TABLESWITCH);
      name = "tableswitch";
      branches = true;
   }
   public byte pad;  // number of bytes used for padding
   public int default_offset;
   public int low;
   public int high;
   public int jump_offsets[];
   public Instruction default_inst;
   public Instruction jump_insts[];
   public String toString(cp_info constant_pool[]) {
      String args;
      int i;
      args = super.toString(constant_pool) + argsep + "(" +
         Integer.toString(pad) + ")";
      args = args + argsep + "label_" + Integer.toString(default_inst.label);
      args = args + argsep + Integer.toString(low);
      args = args + argsep + Integer.toString(high) + ": ";
      for (i=0;i<high-low+1;i++) {
         args = args + argsep + "label_" + Integer.toString(jump_insts[i].label);
      }
      return args;
   }
   public int parse(byte bc[],int index) {
      // first figure out padding to next 4-byte quantity
      int i,j;
      i = index % 4;
      if (i != 0)
         pad = (byte)(4 - i);
      else
         pad = (byte)0;
      index += pad;
      default_offset = getInt(bc,index);
      index += 4;
      low = getInt(bc,index);
      index += 4;
      high = getInt(bc,index);
      index += 4;
      i = high-low+1;
      if (i>0) {
         jump_offsets = new int[i];
         j = 0;
         do {
            jump_offsets[j] = getInt(bc,index);
            index += 4;
            j++;
         } while(j<i);
      }
      return index;
   }
   public int nextOffset(int curr) {
      int i,siz=0;
      i = (curr+1) % 4;
      if (i != 0)
         siz = (4 - i);
      return (curr + siz + 13 + (high-low+1)*4);
   }
   public int compile(byte bc[],int index) {
      int i;
      bc[index++] = code;
      // insert padding so next instruction is on a 4-byte boundary
      for (i=0;i<pad;i++)
         bc[index++] = 0;
      if (default_inst!=null)
         index = intToBytes(default_inst.label-label,bc,index);
      else
         index = intToBytes(default_offset,bc,index);
      index = intToBytes(low,bc,index);
      index = intToBytes(high,bc,index);
      for (i=0;i<=high-low;i++) {
         if (jump_insts[i]!=null)
            index = intToBytes((jump_insts[i]).label-label,bc,index);
         else
            index = intToBytes(jump_offsets[i],bc,index);
      }
      return index;
   }
   public void offsetToPointer(ByteCode bc) {
      int i;
      default_inst = bc.locateInst(default_offset+label);
      if (default_inst==null) {
         G.v().out.println("Warning: can't locate target of instruction");
         G.v().out.println(" which should be at byte address " + (label+default_offset));
      } else
         default_inst.labelled = true;
      if (high-low+1>0) {
         jump_insts = new Instruction[high-low+1];
         for (i=0;i<high-low+1;i++) {
            jump_insts[i] = bc.locateInst(jump_offsets[i]+label);
            if (jump_insts[i]==null) {
               G.v().out.println("Warning: can't locate target of instruction");
               G.v().out.println(" which should be at byte address " +
                                  (label+jump_offsets[i]));
            } else
               jump_insts[i].labelled = true;
         }
      }
   }
   public Instruction[] branchpoints(Instruction next) {
      Instruction i[] = new Instruction[high-low+2];
      int j;
      i[0] = default_inst;
      for (j=1;j<high-low+2;j++)
         i[j] = jump_insts[j-1];
      return i;
   }
}
