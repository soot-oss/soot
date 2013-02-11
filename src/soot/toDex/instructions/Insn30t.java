package soot.toDex.instructions;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction30t;

import soot.toDex.SootToDexUtils;

/**
 * The "30t" instruction format: It needs three 16-bit code units, does not have any registers
 * and is used for jump targets (hence the "t").<br>
 * <br>
 * It is used by the "goto/32" opcode for jumps to a 32-bit wide offset.
 */
public class Insn30t extends InsnWithOffset {
	
	public Insn30t(Opcode opc) {
		super(opc);
	}

	@Override
	protected Instruction getRealInsn0() {
		int offA = getRelativeOffset();
		return new Instruction30t(opc, offA);
	}
	
	@Override
	public boolean offsetFit() {
		int offA = getRelativeOffset();
		return SootToDexUtils.fitsSigned32(offA);
	}
}