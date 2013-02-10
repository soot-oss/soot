package soot.toDex.instructions;

import java.util.BitSet;

import org.jf.dexlib.Item;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction21c;

import soot.toDex.Register;

/**
 * The "21c" instruction format: It needs two 16-bit code units, has one register
 * and is used for class/string/type items (hence the "c" for "constant pool").<br>
 * <br>
 * It is used e.g. by the opcodes "check-cast", "new-instance" and "const-string".
 */
public class Insn21c extends AbstractInsn implements OneRegInsn {
	
	private Item<?> referencedItem;

	public Insn21c(Opcode opc, Register regA, Item<?> referencedItem) {
		super(opc);
		regs.add(regA);
		this.referencedItem = referencedItem;
	}

	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}

	@Override
	protected Instruction getRealInsn0() {
		return new Instruction21c(opc, (short) getRegA().getNumber(), referencedItem);
	}
	
	@Override
	public BitSet getIncompatibleRegs() {
		BitSet incompatRegs = new BitSet(1);
		if (!getRegA().fitsShort()) {
			incompatRegs.set(REG_A_IDX);
		}
		return incompatRegs;
	}
	
	@Override
	public String toString() {
		return super.toString() + " ref: " + referencedItem;
	}
}