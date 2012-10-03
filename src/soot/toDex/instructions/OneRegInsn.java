package soot.toDex.instructions;

import soot.toDex.Register;

public interface OneRegInsn extends Insn {
	
	static final int REG_A_IDX = 0;

	Register getRegA();
}