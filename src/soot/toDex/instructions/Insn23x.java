package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction23x;

import soot.toDex.Register;

/**
 * The "23x" instruction format: It needs two 16-bit code units, has three registers
 * and is used for general purposes (hence the "x").<br>
 * <br>
 * It is used e.g. by the opcodes "cmp-long", "aput" and "add-int".
 */
public class Insn23x extends AbstractInsn implements ThreeRegInsn {

	public Insn23x(Opcode opc, Register regA, Register regB, Register regC) {
		super(opc);
		regs.add(regA);
		regs.add(regB);
		regs.add(regC);
	}
	
	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}
	
	public Register getRegB() {
		return regs.get(REG_B_IDX);
	}
	
	public Register getRegC() {
		return regs.get(REG_C_IDX);
	}

	@Override
	protected Instruction getRealInsn0() {
		return new Instruction23x(opc, (short) getRegA().getNumber(), (short) getRegB().getNumber(), (short) getRegC().getNumber());
	}
	
	@Override
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		BitSet incompatRegs = new BitSet(3);
		if (!curRegs.get(REG_A_IDX).fitsShort()) {
			incompatRegs.set(REG_A_IDX);
		}
		if (!curRegs.get(REG_B_IDX).fitsShort()) {
			incompatRegs.set(REG_B_IDX);
		}
		if (!curRegs.get(REG_C_IDX).fitsShort()) {
			incompatRegs.set(REG_C_IDX);
		}
		return incompatRegs;
	}

	public Insn23x shallowCloneWithRegs(List<Register> newRegs) {
		Register newRegA = newRegs.get(REG_A_IDX);
		Register newRegB = newRegs.get(REG_B_IDX);
		Register newRegC = newRegs.get(REG_C_IDX);
		Insn23x shallowClone = new Insn23x(getOpcode(), newRegA, newRegB, newRegC);
		shallowClone.setInsnOffset(getInsnOffset());
		return shallowClone;
	}
}