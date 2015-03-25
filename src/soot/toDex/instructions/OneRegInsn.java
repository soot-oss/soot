package soot.toDex.instructions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.toDex.Register;

/**
 * Interface for instructions that need one register.
 */
public interface OneRegInsn extends Insn {
	
	static final int REG_A_IDX = 0;

	Register getRegA();
}