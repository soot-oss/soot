package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Item;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction3rc;

import soot.toDex.Register;
import soot.toDex.SootToDexUtils;

public class Insn3rc extends AbstractInsn {
	
	private short regCount;
	
	private Item<?> referencedItem;

	public Insn3rc(Opcode opc, List<Register> regs, short regCount, Item<?> referencedItem) {
		super(opc);
		this.regs = regs;
		this.regCount = regCount;
		this.referencedItem = referencedItem;
	}

	public Item<?> getReferencedItem() {
		return referencedItem;
	}
	
	@Override
	protected Instruction getRealInsn0() {
		Register startReg = regs.get(0);
		return new Instruction3rc(opc, regCount, startReg.getNumber(), referencedItem);
	}
	
	@Override
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		// if there is one problem -> all regs are incompatible (this could be optimized in reg allocation, probably)
		int curRegCount = SootToDexUtils.getRealRegCount(curRegs);
		if (hasHoleInRange(curRegs)) {
			return getAllIncompatible(curRegCount);
		}
		for (Register curReg : curRegs) {
			if (!curReg.fitsUnconstrained()) {
				return getAllIncompatible(curRegCount);
			}
			if (curReg.isWide()) {
				boolean secondWideHalfFits = Register.fitsUnconstrained(curReg.getNumber() + 1, false);
				if (!secondWideHalfFits) {
					return getAllIncompatible(curRegCount);
				}
			}
		}
		return new BitSet(curRegCount);
	}
	
	private static BitSet getAllIncompatible(int regCount) {
		BitSet incompatRegs = new BitSet(regCount);
		incompatRegs.flip(0, regCount);
		return incompatRegs;
	}
	
	private static boolean hasHoleInRange(List<Register> curRegs) {
		// the only "hole" that is allowed: if regN is wide -> regN+1 must not be there
		Register startReg = curRegs.get(0);
		int nextExpectedRegNum = startReg.getNumber() + 1;
		if (startReg.isWide()) {
			nextExpectedRegNum++;
		}
		// loop starts at 1, since the first reg alone cannot have a hole
		for (int i = 1; i < curRegs.size(); i++) {
			Register curReg = curRegs.get(i);
			int curRegNum = curReg.getNumber();
			if (curRegNum != nextExpectedRegNum) {
				return true;
			}
			nextExpectedRegNum++;
			if (curReg.isWide()) {
				nextExpectedRegNum++;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " ref: " + referencedItem;
	}
	
	public Insn3rc shallowCloneWithRegs(List<Register> newRegs) {
		Insn3rc shallowClone = new Insn3rc(getOpcode(), newRegs, regCount, referencedItem);
		shallowClone.setInsnOffset(getInsnOffset());
		return shallowClone;
	}
}