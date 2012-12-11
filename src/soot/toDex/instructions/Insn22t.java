package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction22t;

import soot.toDex.Register;
import soot.toDex.SootToDexUtils;

/**
 * The "22t" instruction format: It needs two 16-bit code units, has two registers
 * and is used for jump targets (hence the "t").<br>
 * <br>
 * It is used e.g. by the opcode "if-eq" for conditional jumps to a 16-bit wide offset.
 */
public class Insn22t extends InsnWithOffset implements TwoRegInsn {

	public Insn22t(Opcode opc, Register regA, Register regB) {
		super(opc);
		regs.add(regA);
		regs.add(regB);
	}
	
	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}
	
	public Register getRegB() {
		return regs.get(REG_B_IDX);
	}

	@Override
	protected Instruction getRealInsn0() {
		int offC = getRelativeOffset();
		return new Instruction22t(opc, (byte) getRegA().getNumber(), (byte) getRegB().getNumber(), (short) offC);
	}
	
	@Override
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		BitSet incompatRegs = new BitSet(2);
		if (!curRegs.get(REG_A_IDX).fitsByte()) {
			incompatRegs.set(REG_A_IDX);
		}
		if (!curRegs.get(REG_B_IDX).fitsByte()) {
			incompatRegs.set(REG_B_IDX);
		}
		return incompatRegs;
	}
	
	@Override
	public boolean offsetFit() {
		int offC = getRelativeOffset();
		return SootToDexUtils.fitsSigned16(offC);
	}

	public Insn22t shallowCloneWithRegs(List<Register> newRegs) {
		Register newRegA = newRegs.get(REG_A_IDX);
		Register newRegB = newRegs.get(REG_B_IDX);
		Insn22t shallowClone = new Insn22t(getOpcode(), newRegA, newRegB);
		shallowClone.setInsnOffset(getInsnOffset());
		shallowClone.offset = offset;
		shallowClone.offsetAddress = offsetAddress;
		return shallowClone;
	}
}