package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction51l;

import soot.toDex.Register;

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
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		BitSet incompatRegs = new BitSet(1);
		if (!curRegs.get(REG_A_IDX).fitsShort()) {
			incompatRegs.set(REG_A_IDX);
		}
		return incompatRegs;
	}
	
	@Override
	public String toString() {
		return super.toString() + " lit: " + getLitB();
	}

	public Insn51l shallowCloneWithRegs(List<Register> newRegs) {
		Register newRegA = newRegs.get(REG_A_IDX);
		Insn51l shallowClone = new Insn51l(getOpcode(), newRegA, getLitB());
		shallowClone.setInsnOffset(getInsnOffset());
		return shallowClone;
	}
}