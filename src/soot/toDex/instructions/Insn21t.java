package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction21t;

import soot.toDex.Register;
import soot.toDex.SootToDexUtils;

public class Insn21t extends InsnWithOffset implements OneRegInsn {
	
	public Insn21t(Opcode opc, Register regA) {
		super(opc);
		regs.add(regA);
	}
	
	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}

	@Override
	protected Instruction getRealInsn0() {
		int offB = getRelativeOffset();
		return new Instruction21t(opc, (short) getRegA().getNumber(), (short) offB);
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
	public boolean offsetFit() {
		int offB = getRelativeOffset();
		return SootToDexUtils.fitsSigned16(offB);
	}

	public Insn21t shallowCloneWithRegs(List<Register> newRegs) {
		Register newRegA = newRegs.get(REG_A_IDX);
		Insn21t shallowClone = new Insn21t(getOpcode(), newRegA);
		shallowClone.setInsnOffset(getInsnOffset());
		shallowClone.offset = offset;
		shallowClone.offsetAddress = offsetAddress;
		return shallowClone;
	}
}