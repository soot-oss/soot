package soot.coffi;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 Clark Verbrugge
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

/**
 * Instruction subclasses are used to represent parsed bytecode; each bytecode operation has a corresponding subclass of
 * Instruction.
 * <p>
 * Each subclass is derived from one of
 * <ul>
 * <li>Instruction</li>
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
 *
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
class Instruction_Iand extends Instruction_noargs {
  public Instruction_Iand() {
    super((byte) ByteCode.IAND);
    name = "iand";
  }
}
