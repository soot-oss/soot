package soot.toDex.instructions;

import java.util.BitSet;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction23x;

import soot.toDex.LabelAssigner;
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
	protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
		return new BuilderInstruction23x(opc, (short) getRegA().getNumber(),
				(short) getRegB().getNumber(), (short) getRegC().getNumber());
	}
	
	@Override
	public BitSet getIncompatibleRegs() {
		BitSet incompatRegs = new BitSet(3);
		if (!getRegA().fitsShort()) {
			incompatRegs.set(REG_A_IDX);
		}
		if (!getRegB().fitsShort()) {
			incompatRegs.set(REG_B_IDX);
		}
		if (!getRegC().fitsShort()) {
			incompatRegs.set(REG_C_IDX);
		}
		return incompatRegs;
	}
}