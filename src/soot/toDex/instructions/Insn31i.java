package soot.toDex.instructions;

import java.util.BitSet;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction31i;

import soot.toDex.Register;

/**
 * The "31i" instruction format: It needs three 16-bit code units, has one register
 * and is used for a 32-bit literal (hence the "i" for "integer").<br>
 * <br>
 * It is used by the opcodes "const" and "const-wide/32".
 */
public class Insn31i extends AbstractInsn implements OneRegInsn {
	
	private int litB;

	public Insn31i(Opcode opc, Register regA, int litB) {
		super(opc);
		regs.add(regA);
		this.litB = litB;
	}
	
	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}
	
	public int getLitB() {
		return litB;
	}

	@Override
	protected Instruction getRealInsn0() {
		return new Instruction31i(opc, (short) getRegA().getNumber(), getLitB());
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
		return super.toString() + " lit: " + getLitB();
	}
}