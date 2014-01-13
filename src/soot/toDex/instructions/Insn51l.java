package soot.toDex.instructions;

import java.util.BitSet;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction51l;

import soot.toDex.Register;

/**
 * The "51l" instruction format: It needs five 16-bit code units, has one register
 * and is used for a 64-bit literal (hence the "l" for "long").<br>
 * <br>
 * It is used by the opcode "const-wide".
 */
public class Insn51l extends AbstractInsn implements OneRegInsn {
	
	private long litB;
	
	public Insn51l(Opcode opc, Register regA, long litB) {
		super(opc);
		regs.add(regA);
		this.litB = litB;
	}	
	
	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}
	
	public long getLitB() {
		return litB;
	}

	@Override
	protected Instruction getRealInsn0() {
		return new Instruction51l(opc, (short) getRegA().getNumber(), getLitB());
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