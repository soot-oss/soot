package soot.toDex.instructions;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction10t;
import org.jf.dexlib.Code.Format.Instruction30t;

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
		
		// If offset equals 0, must use goto/32 opcode
		if (offA == 0)
		  return new Instruction30t(Opcode.GOTO_32, offA);
		
		return new Instruction10t(opc, offA);
	}

	@Override
	public boolean offsetFit() {
		int offA = getRelativeOffset();
		return SootToDexUtils.fitsSigned8(offA);
	}
}