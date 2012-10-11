package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction32x;

import soot.toDex.Register;

/**
 * The "32x" instruction format: It needs three 16-bit code units, has two registers
 * and is used for general purposes (hence the "x").<br>
 * <br>
 * It is used by the opcodes "move/16", "move-wide/16" and "move-object/16".
 */
public class Insn32x extends AbstractInsn implements TwoRegInsn {

	public Insn32x(Opcode opc, Register regA, Register regB) {
		super(opc);
		regs.add(regA);
		regs.add(regB);
	}
	
	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}

	public Register getRegB() {
		return regs.get(REG_B_IDX);
	}
	
	@Override
	protected Instruction getRealInsn0() {
		return new Instruction32x(opc, getRegA().getNumber(), getRegB().getNumber());
	}
	
	@Override
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		BitSet incompatRegs = new BitSet(2);
		if (!curRegs.get(REG_A_IDX).fitsUnconstrained()) {
			incompatRegs.set(REG_A_IDX);
		}
		if (!curRegs.get(REG_B_IDX).fitsUnconstrained()) {
			incompatRegs.set(REG_B_IDX);
		}
		return incompatRegs;
	}

	public Insn32x shallowCloneWithRegs(List<Register> newRegs) {
		Register newRegA = newRegs.get(REG_A_IDX);
		Register newRegB = newRegs.get(REG_B_IDX);
		Insn32x shallowClone = new Insn32x(getOpcode(), newRegA, newRegB);
		shallowClone.setInsnOffset(getInsnOffset());
		return shallowClone;
	}
}