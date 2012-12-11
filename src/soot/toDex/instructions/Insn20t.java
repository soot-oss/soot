package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction20t;

import soot.toDex.Register;
import soot.toDex.SootToDexUtils;

/**
 * The "20t" instruction format: It needs two 16-bit code units, does not have any registers
 * and is used for jump targets (hence the "t").<br>
 * <br>
 * It is used by the "goto/16" opcode for jumps to a 16-bit wide offset.
 */
public class Insn20t extends InsnWithOffset {
	
	public Insn20t(Opcode opc) {
		super(opc);
	}
	
	@Override
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		return new BitSet(0);
	}

	@Override
	protected Instruction getRealInsn0() {
		int offA = getRelativeOffset();
		return new Instruction20t(opc, offA);
	}
	@Override
	public boolean offsetFit() {
		int offA = getRelativeOffset();
		return SootToDexUtils.fitsSigned16(offA);
	}
	
	public Insn20t shallowCloneWithRegs(List<Register> newRegs) {
		Insn20t shallowClone = new Insn20t(getOpcode());
		shallowClone.setInsnOffset(getInsnOffset());
		shallowClone.offset = offset;
		shallowClone.offsetAddress = offsetAddress;
		return shallowClone;
	}
}