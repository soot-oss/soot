package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction10t;

import soot.toDex.Register;
import soot.toDex.SootToDexUtils;

/**
 * The "10t" instruction format: It needs one 16-bit code unit, does not have any registers
 * and is used for jump targets (hence the "t").<br>
 * <br>
 * It is used by the "goto" opcode for jumps to offsets up to 8 bits away.
 */
public class Insn10t extends InsnWithOffset {

	public Insn10t(Opcode opc) {
		super(opc);
	}

	@Override
	protected Instruction getRealInsn0() {
		int offA = getRelativeOffset();
		return new Instruction10t(opc, offA);
	}

	@Override
	public boolean offsetFit() {
		int offA = getRelativeOffset();
		return SootToDexUtils.fitsSigned8(offA);
	}
	
	@Override
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		return new BitSet(0);
	}

	public Insn10t shallowCloneWithRegs(List<Register> newRegs) {
		Insn10t shallowClone = new Insn10t(getOpcode());
		shallowClone.setInsnOffset(getInsnOffset());
		shallowClone.offset = offset;
		shallowClone.offsetAddress = offsetAddress;
		return shallowClone;
	}
}