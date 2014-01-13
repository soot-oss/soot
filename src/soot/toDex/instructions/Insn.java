package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;

import soot.toDex.Register;

/**
 * Interface for the dalvik instruction formats.
 */
public interface Insn extends Cloneable {
	
	Opcode getOpcode();

	void setInsnOffset(int insnOffset);

	int getInsnOffset();

	List<Register> getRegs();
	
	BitSet getIncompatibleRegs();

	boolean hasIncompatibleRegs();
	
	int getMinimumRegsNeeded();
	
	Instruction getRealInsn();

	int getSize();
}