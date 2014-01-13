package soot.toDex.instructions;

import java.util.BitSet;

import org.jf.dexlib.Item;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction22c;

import soot.toDex.Register;

/**
 * The "22c" instruction format: It needs two 16-bit code units, has two registers
 * and is used for class/type items (hence the "c" for "constant pool").<br>
 * <br>
 * It is used e.g. by the opcodes "instance-of", "new-array" and "iget".
 */
public class Insn22c extends AbstractInsn implements TwoRegInsn {
	
	private Item<?> referencedItem;

	public Insn22c(Opcode opc, Register regA, Register regB, Item<?> referencedItem) {
		super(opc);
		regs.add(regA);
		regs.add(regB);
		this.referencedItem = referencedItem;
	}

	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}
	
	public Register getRegB() {
		return regs.get(REG_B_IDX);
	}

	@Override
	protected Instruction getRealInsn0() {
		return new Instruction22c(opc, (byte) getRegA().getNumber(), (byte) getRegB().getNumber(), referencedItem);
	}
	
	@Override
	public BitSet getIncompatibleRegs() {
		BitSet incompatRegs = new BitSet(2);
		if (!getRegA().fitsByte()) {
			incompatRegs.set(REG_A_IDX);
		}
		if (!getRegB().fitsByte()) {
			incompatRegs.set(REG_B_IDX);
		}
		return incompatRegs;
	}
	
	@Override
	public String toString() {
		return super.toString() + " ref: " + referencedItem;
	}
}