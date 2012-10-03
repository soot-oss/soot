package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction21s;

import soot.toDex.Register;

public class Insn21s extends AbstractInsn implements OneRegInsn {
	
	private short litB;

	public Insn21s(Opcode opc, Register regA, short litB) {
		super(opc);
		regs.add(regA);
		this.litB = litB;
	}
	
	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}
	
	public short getLitB() {
		return litB;
	}

	@Override
	protected Instruction getRealInsn0() {
		return new Instruction21s(opc, (short) getRegA().getNumber(), getLitB());
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

	public Insn21s shallowCloneWithRegs(List<Register> newRegs) {
		Register newRegA = newRegs.get(REG_A_IDX);
		Insn21s shallowClone = new Insn21s(getOpcode(), newRegA, getLitB());
		shallowClone.setInsnOffset(getInsnOffset());
		return shallowClone;
	}
}