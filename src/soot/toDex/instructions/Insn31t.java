package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction31t;

import soot.toDex.Register;
import soot.toDex.SootToDexUtils;

public class Insn31t extends InsnWithOffset implements OneRegInsn {
	
	public Insn31t(Opcode opc, Register regA) {
		super(opc);
		regs.add(regA);
	}

	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}

	@Override
	protected Instruction getRealInsn0() {
		int offB = getRelativeOffset();
		return new Instruction31t(opc, (short) getRegA().getNumber(), offB);
	}
	
	@Override
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		BitSet incompatRegs = new BitSet(1);
		if (!regs.get(REG_A_IDX).fitsShort()) {
			incompatRegs.set(REG_A_IDX);
		}
		return incompatRegs;
	}
	
	@Override
	public boolean offsetFit() {
		int offB = getRelativeOffset();
		return SootToDexUtils.fitsSigned32(offB);
	}

	public Insn31t shallowCloneWithRegs(List<Register> newRegs) {
		Register newRegA = newRegs.get(REG_A_IDX);
		Insn31t shallowClone = new Insn31t(getOpcode(), newRegA);
		shallowClone.setInsnOffset(getInsnOffset());
		shallowClone.offset = offset;
		shallowClone.offsetAddress = offsetAddress;
		return shallowClone;
	}
}