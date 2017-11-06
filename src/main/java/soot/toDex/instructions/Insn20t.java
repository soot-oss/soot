package soot.toDex.instructions;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction20t;

import soot.toDex.LabelAssigner;

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
	protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
		return new BuilderInstruction20t(opc, assigner.getOrCreateLabel(target));
	}

	@Override
	public int getMaxJumpOffset() {
		return Short.MAX_VALUE;
	}
	
}