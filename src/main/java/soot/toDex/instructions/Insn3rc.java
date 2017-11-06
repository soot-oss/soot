package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction3rc;
import org.jf.dexlib2.iface.reference.Reference;
import org.jf.dexlib2.writer.builder.BuilderReference;

import soot.toDex.LabelAssigner;
import soot.toDex.Register;
import soot.toDex.SootToDexUtils;

/**
 * The "3rc" instruction format: It needs three 16-bit code units, has a whole range
 * of registers (hence the "r" for "ranged") and is used for method/type items
 * (hence the "c" for "constant pool").<br>
 * <br>
 * It is used by the "filled-new-array/range" opcode and the various ranged "invoke-" opcodes.
 */
public class Insn3rc extends AbstractInsn {
	
	private short regCount;
	
	private Reference referencedItem;

	public Insn3rc(Opcode opc, List<Register> regs, short regCount, Reference referencedItem) {
		super(opc);
		this.regs = regs;
		this.regCount = regCount;
		this.referencedItem = referencedItem;
	}
	
	@Override
	protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
		Register startReg = regs.get(0);
		return new BuilderInstruction3rc(opc, startReg.getNumber(), regCount, referencedItem);
	}
	
	@Override
	public BitSet getIncompatibleRegs() {
		// if there is one problem -> all regs are incompatible (this could be optimized in reg allocation, probably)
		int regCount = SootToDexUtils.getRealRegCount(regs);
		if (hasHoleInRange()) {
			return getAllIncompatible(regCount);
		}
		for (Register r : regs) {
			if (!r.fitsUnconstrained()) {
				return getAllIncompatible(regCount);
			}
			if (r.isWide()) {
				boolean secondWideHalfFits = Register.fitsUnconstrained(r.getNumber() + 1, false);
				if (!secondWideHalfFits) {
					return getAllIncompatible(regCount);
				}
			}
		}
		return new BitSet(regCount);
	}
	
	private static BitSet getAllIncompatible(int regCount) {
		BitSet incompatRegs = new BitSet(regCount);
		incompatRegs.flip(0, regCount);
		return incompatRegs;
	}
	
	private boolean hasHoleInRange() {
		// the only "hole" that is allowed: if regN is wide -> regN+1 must not be there
		Register startReg = regs.get(0);
		int nextExpectedRegNum = startReg.getNumber() + 1;
		if (startReg.isWide()) {
			nextExpectedRegNum++;
		}
		// loop starts at 1, since the first reg alone cannot have a hole
		for (int i = 1; i < regs.size(); i++) {
			Register r = regs.get(i);
			int regNum = r.getNumber();
			if (regNum != nextExpectedRegNum) {
				return true;
			}
			nextExpectedRegNum++;
			if (r.isWide()) {
				nextExpectedRegNum++;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " ref: " + referencedItem;
	}
}