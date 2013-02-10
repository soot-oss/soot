package soot.toDex.instructions;

import soot.toDex.Register;

/**
 * Interface for instructions that need five registers.<br>
 * <br>
 * Note that the interface does not inherit from {@link ThreeRegInsn} due to the unusual register naming - the register indices cannot be overwritten here.
 */
public interface FiveRegInsn extends Insn {
	
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