package soot.toDex.instructions;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;

import soot.toDex.Register;
import soot.toDex.SootToDexUtils;

public abstract class AbstractInsn implements Insn {
	
	protected Opcode opc;
	
	protected int insnOffset;
	
	protected List<Register> regs;
	
	public AbstractInsn(Opcode opc) {
		if (opc == null) {
			throw new IllegalArgumentException("opcode must not be null");
		}
		this.opc = opc;
		insnOffset = -1;
		regs = new ArrayList<Register>();
	}
	
	public Opcode getOpcode() {
		return opc;
	}
	
	public void setInsnOffset(int insnOffset) {
		this.insnOffset = insnOffset;
	}
	
	public int getInsnOffset() {
		return insnOffset;
	}
	
	public List<Register> getRegs() {
		return regs;
	}
	
	public boolean hasIncompatibleRegs() {
		return getIncompatibleRegs().cardinality() > 0;
	}
	
	public BitSet getIncompatibleRegs() {
		return getIncompatibleRegs(getRegs());
	}
	
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		return new BitSet(0);
	}
	
	public int getMinimumRegsNeeded(BitSet incompatRegs) {
		int resultNeed = 0;
		int miscRegsNeed = 0;
		boolean hasResult = opc.setsRegister();
		if (hasResult && incompatRegs.get(0)) {
			resultNeed = SootToDexUtils.toDexWords(regs.get(0).getType());
		}
		for (int i = hasResult ? 1 : 0; i < regs.size(); i++) {
			if (incompatRegs.get(i)) {
				miscRegsNeed += SootToDexUtils.toDexWords(regs.get(i).getType());
			}
		}
		return Math.max(resultNeed, miscRegsNeed);
	}
	
	public Instruction getRealInsn() {
		if (hasIncompatibleRegs()) {
			throw new RuntimeException("the instruction still has incompatible registers: " + getIncompatibleRegs());
		}
		return getRealInsn0();
	}
	
	protected abstract Instruction getRealInsn0();
	
	@Override
	public String toString() {
		return opc.toString() + " @" + insnOffset + " " + regs;
	}

	public int getSize(int codeAddress) {
		return opc.format.size / 2; // the format size is in byte count, we need word count
	}
}