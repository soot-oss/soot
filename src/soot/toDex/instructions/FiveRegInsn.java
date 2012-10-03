package soot.toDex.instructions;

import soot.toDex.Register;

public interface FiveRegInsn {
	
	static final int REG_D_IDX = 0;
	
	static final int REG_E_IDX = REG_D_IDX + 1;
	
	static final int REG_F_IDX = REG_E_IDX + 1;
	
	static final int REG_G_IDX = REG_F_IDX + 1;
	
	static final int REG_A_IDX = REG_G_IDX + 1;
	
	Register getRegD();

	Register getRegE();

	Register getRegF();

	Register getRegG();

	Register getRegA();
}