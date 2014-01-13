package soot.toDex.instructions;

import soot.toDex.Register;

/**
 * Interface for instructions that need three registers.
 */
public interface ThreeRegInsn extends TwoRegInsn {
	
	static final int REG_C_IDX = REG_B_IDX + 1;

	Register getRegC();
}
