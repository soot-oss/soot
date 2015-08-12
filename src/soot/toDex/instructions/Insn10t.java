package soot.toDex.instructions;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction10t;

import soot.toDex.LabelAssigner;

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
	protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
		if (target == null)
			throw new RuntimeException("Cannot jump to a NULL target");
		return new BuilderInstruction10t(opc, assigner.getOrCreateLabel(target));
	}

	@Override
	public int getMaxJumpOffset() {
		return Byte.MAX_VALUE;
	}

}