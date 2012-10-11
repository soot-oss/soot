package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction12x;

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
	protected Instruction getRealInsn0() {
		return new Instruction12x(opc, (byte) getRegA().getNumber(), (byte) getRegB().getNumber());
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

	public Insn12x shallowCloneWithRegs(List<Register> newRegs) {
		Register newRegA = newRegs.get(REG_A_IDX);
		Register newRegB = newRegs.get(REG_B_IDX);
		Insn12x shallowClone = new Insn12x(getOpcode(), newRegA, newRegB);
		shallowClone.setInsnOffset(getInsnOffset());
		return shallowClone;
	}
}