package soot.toDex.instructions;

import java.util.BitSet;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction12x;

import soot.toDex.LabelAssigner;
import soot.toDex.Register;

/**
 * The "12x" instruction format: It needs one 16-bit code unit, has two registers
 * and is used for general purposes (hence the "x").<br>
 * <br>
 * It is used e.g. by the opcodes "move-object", "array-length", the unary operations
 * and the "/2addr" binary operations.
 */
public class Insn12x extends AbstractInsn implements TwoRegInsn {

	public Insn12x(Opcode opc, Register regA, Register regB) {
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
		return new BuilderInstruction12x(opc, (byte) getRegA().getNumber(), (byte) getRegB().getNumber());
	}
	
	@Override
	public BitSet getIncompatibleRegs() {
		BitSet incompatRegs = new BitSet(2);
		if (!getRegA().fitsByte()) {
			incompatRegs.set(REG_A_IDX);
		}
		if (!getRegB().fitsByte()) {
			incompatRegs.set(REG_B_IDX);
		}
		return incompatRegs;
	}
}