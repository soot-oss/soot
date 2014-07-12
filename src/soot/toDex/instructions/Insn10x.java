package soot.toDex.instructions;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction10x;

import soot.toDex.LabelAssigner;

/**
 * The "10x" instruction format: It needs one 16-bit code unit, does not have any registers
 * and is used for general purposes (hence the "x").<br>
 * <br>
 * It is used by the opcodes "nop" and "return-void".
 */
public class Insn10x extends AbstractInsn {

	public Insn10x(Opcode opc) {
		super(opc);
	}

	@Override
	protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
		return new BuilderInstruction10x(opc);
	}
}