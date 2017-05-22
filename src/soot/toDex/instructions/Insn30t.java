package soot.toDex.instructions;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction30t;

import soot.toDex.LabelAssigner;

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
	protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
		return new BuilderInstruction30t(opc, assigner.getOrCreateLabel(target));
	}

	@Override
	public int getMaxJumpOffset() {
		return Integer.MAX_VALUE;
	}

}