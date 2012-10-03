package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;

import soot.toDex.Register;

public interface Insn extends Cloneable {
	
	Opcode getOpcode();

	void setInsnOffset(int insnOffset);

	int getInsnOffset();

	List<Register> getRegs();
	
	BitSet getIncompatibleRegs();
	
	BitSet getIncompatibleRegs(List<Register> curRegs);

	boolean hasIncompatibleRegs();
	
	int getMinimumRegsNeeded(BitSet incompatRegs);
	
	Instruction getRealInsn();

	int getSize(int codeAddress);
	
	Insn shallowCloneWithRegs(List<Register> newRegs);
}