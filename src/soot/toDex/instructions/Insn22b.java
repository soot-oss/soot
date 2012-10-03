package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction22b;

import soot.toDex.Register;

public class Insn22b extends AbstractInsn implements TwoRegInsn {
	
	private byte litC;

	public Insn22b(Opcode opc, Register regA, Register regB, byte litC) {
		super(opc);
		regs.add(regA);
		regs.add(regB);
		this.litC = litC;
	}
	
	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}
	
	public Register getRegB() {
		return regs.get(REG_B_IDX);
	}
	
	public byte getLitC() {
		return litC;
	}

	@Override
	protected Instruction getRealInsn0() {
		return new Instruction22b(opc, (short) getRegA().getNumber(), (short) getRegB().getNumber(), getLitC());
	}
	
	@Override
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		BitSet incompatRegs = new BitSet(2);
		if (!curRegs.get(REG_A_IDX).fitsShort()) {
			incompatRegs.set(REG_A_IDX);
		}
		if (!curRegs.get(REG_B_IDX).fitsShort()) {
			incompatRegs.set(REG_B_IDX);
		}
		return incompatRegs;
	}
	
	@Override
	public String toString() {
		return super.toString() + " lit: " + getLitC();
	}

	public Insn22b shallowCloneWithRegs(List<Register> newRegs) {
		Register newRegA = newRegs.get(REG_A_IDX);
		Register newRegB = newRegs.get(REG_B_IDX);
		Insn22b shallowClone = new Insn22b(getOpcode(), newRegA, newRegB, getLitC());
		shallowClone.setInsnOffset(getInsnOffset());
		return shallowClone;
	}
}