package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction11x;

import soot.toDex.Register;

/**
 * The "11x" instruction format: It needs one 16-bit code unit, has one register
 * and is used for general purposes (hence the "x").<br>
 * <br>
 * It is used e.g. by the opcodes "monitor-enter", "monitor-exit", "move-result" and "return".
 */
public class Insn11x extends AbstractInsn implements OneRegInsn {

	public Insn11x(Opcode opc, Register regA) {
		super(opc);
		regs.add(regA);
	}
	
	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}

	@Override
	protected Instruction getRealInsn0() {
		return new Instruction11x(opc, (short) getRegA().getNumber());
	}
	
	@Override
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		BitSet incompatRegs = new BitSet(1);
		if (!curRegs.get(REG_A_IDX).fitsShort()) {
			incompatRegs.set(REG_A_IDX);
		}
		return incompatRegs;
	}

	public Insn11x shallowCloneWithRegs(List<Register> newRegs) {
		Register newRegA = newRegs.get(REG_A_IDX);
		Insn11x shallowClone = new Insn11x(getOpcode(), newRegA);
		shallowClone.setInsnOffset(getInsnOffset());
		return shallowClone;
	}
}