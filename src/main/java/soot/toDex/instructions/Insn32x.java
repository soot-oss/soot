package soot.toDex.instructions;

import java.util.BitSet;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction32x;

import soot.toDex.LabelAssigner;
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
	protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
		return new BuilderInstruction32x(opc, getRegA().getNumber(),
				getRegB().getNumber());
	}
	
	@Override
	public BitSet getIncompatibleRegs() {
		BitSet incompatRegs = new BitSet(2);
		if (!getRegA().fitsUnconstrained()) {
			incompatRegs.set(REG_A_IDX);
		}
		if (!getRegB().fitsUnconstrained()) {
			incompatRegs.set(REG_B_IDX);
		}
		return incompatRegs;
	}
}