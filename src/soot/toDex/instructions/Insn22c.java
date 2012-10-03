package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Item;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction22c;

import soot.toDex.Register;

public class Insn22c extends AbstractInsn implements TwoRegInsn {
	
	private Item<?> referencedItem;

	public Insn22c(Opcode opc, Register regA, Register regB, Item<?> referencedItem) {
		super(opc);
		regs.add(regA);
		regs.add(regB);
		this.referencedItem = referencedItem;
	}

	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}
	
	public Register getRegB() {
		return regs.get(REG_B_IDX);
	}
	
	public Item<?> getReferencedItem() {
		return referencedItem;
	}

	@Override
	protected Instruction getRealInsn0() {
		return new Instruction22c(opc, (byte) getRegA().getNumber(), (byte) getRegB().getNumber(), referencedItem);
	}
	
	@Override
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		BitSet incompatRegs = new BitSet(2);
		if (!curRegs.get(REG_A_IDX).fitsByte()) {
			incompatRegs.set(REG_A_IDX);
		}
		if (!curRegs.get(REG_B_IDX).fitsByte()) {
			incompatRegs.set(REG_B_IDX);
		}
		return incompatRegs;
	}
	
	@Override
	public String toString() {
		return super.toString() + " ref: " + referencedItem;
	}

	public Insn22c shallowCloneWithRegs(List<Register> newRegs) {
		Register newRegA = newRegs.get(REG_A_IDX);
		Register newRegB = newRegs.get(REG_B_IDX);
		Insn22c shallowClone = new Insn22c(getOpcode(), newRegA, newRegB, referencedItem);
		shallowClone.setInsnOffset(getInsnOffset());
		return shallowClone;
	}
}