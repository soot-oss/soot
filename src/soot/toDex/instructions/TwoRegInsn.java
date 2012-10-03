package soot.toDex.instructions;

import soot.toDex.Register;

public interface TwoRegInsn extends OneRegInsn {
	
	static final int REG_B_IDX = REG_A_IDX + 1;
	
	Register getRegB();
}