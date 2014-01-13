package soot.toDex.instructions;

import java.util.BitSet;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction31t;

import soot.toDex.Register;
import soot.toDex.SootToDexUtils;

/**
 * The "31t" instruction format: It needs three 16-bit code units, has one register
 * and is used for jump targets (hence the "t").<br>
 * <br>
 * It is used e.g. by the opcodes "packed-switch" and "sparse-switch".
 */
public class Insn31t extends InsnWithOffset implements OneRegInsn {
	
	public Insn31t(Opcode opc, Register regA) {
		super(opc);
		regs.add(regA);
	}

	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}

	@Override
	protected Instruction getRealInsn0() {
		int offB = getRelativeOffset();
		return new Instruction31t(opc, (short) getRegA().getNumber(), offB);
	}
	
	@Override
	public BitSet getIncompatibleRegs() {
		BitSet incompatRegs = new BitSet(1);
		if (!getRegA().fitsShort()) {
			incompatRegs.set(REG_A_IDX);
		}
		return incompatRegs;
	}
	
	@Override
	public boolean offsetFit() {
		int offB = getRelativeOffset();
		return SootToDexUtils.fitsSigned32(offB);
	}
}