package soot.toDex.instructions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.toDex.Register;

/**
 * Interface for instructions that need three registers.
 */
public interface ThreeRegInsn extends TwoRegInsn {
	
	static final int REG_C_IDX = REG_B_IDX + 1;

	Register getRegC();
}
