package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction11n;

import soot.toDex.Register;

public class Insn11n extends AbstractInsn implements OneRegInsn {
	
	private byte litB;

	public Insn11n(Opcode opc, Register regA, byte litB) {
		super(opc);
		regs.add(regA);
		this.litB = litB;
	}

	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}
	
	public byte getLitB() {
		return litB;
	}

	@Override
	protected Instruction getRealInsn0() {
		return new Instruction11n(opc, (byte) getRegA().getNumber(), getLitB());
	}
	
	@Override
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		BitSet incompatRegs = new BitSet(1);
		if (!curRegs.get(REG_A_IDX).fitsByte()) {
			incompatRegs.set(REG_A_IDX);
		}
		return incompatRegs;
	}
	
	@Override
	public String toString() {
		return super.toString() + " lit: " + getLitB();
	}
	
	public Insn11n shallowCloneWithRegs(List<Register> newRegs) {
		Register newRegA = newRegs.get(REG_A_IDX);
		Insn11n shallowClone = new Insn11n(getOpcode(), newRegA, getLitB());
		shallowClone.setInsnOffset(getInsnOffset());
		return shallowClone;
	}
}