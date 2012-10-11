package soot.toDex.instructions;

import soot.toDex.Register;

/**
 * Interface for instructions that need two registers.
 */
public interface TwoRegInsn extends OneRegInsn {
	
	static final int REG_B_IDX = REG_A_IDX + 1;
	
	Register getRegB();
}