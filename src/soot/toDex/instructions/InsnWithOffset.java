package soot.toDex.instructions;

import java.util.List;

import org.jf.dexlib.Code.Opcode;

import soot.toDex.SootToDexUtils;

/**
 * An abstract implementation for instructions that have an offset.<br>
 * An offset references an object and has an address, that is the point in the bytecode
 * where that object is located at.
 */
public abstract class InsnWithOffset extends AbstractInsn {
	
	protected Object offset;
	
	protected int offsetAddress;
	
	public InsnWithOffset(Opcode opc) {
		super(opc);
	}
	
	public void setOffset(Object offset) {
		this.offset = offset;
	}
	
	public Object getOffset() {
		return offset;
	}
	
	public void setOffsetAddress(List<Insn> insns) {
		offsetAddress = SootToDexUtils.getOffset(offset, insns);
	}
	
	public int getRelativeOffset() {
		int ourOffset = getInsnOffset();
		return offsetAddress - ourOffset;
	}
	
	public abstract boolean offsetFit();
}