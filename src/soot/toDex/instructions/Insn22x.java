package soot.toDex.instructions;

import java.util.BitSet;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction22x;

import soot.toDex.Register;

/**
 * The "22x" instruction format: It needs two 16-bit code units, has two registers
 * and is used for general purposes (hence the "x").<br>
 * <br>
 * It is used by the opcodes "move/from16", "move-wide/from16" and "move-object/from16".
 */
public class Insn22x extends AbstractInsn implements TwoRegInsn {

	public Insn22x(Opcode opc, Register regA, Register regB) {
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
	
	protected Instruction getRealInsn0() {
		return new Instruction22x(opc, (short) getRegA().getNumber(), getRegB().getNumber());
	}
	
	@Override
	public BitSet getIncompatibleRegs() {
		BitSet incompatRegs = new BitSet(2);
		if (!getRegA().fitsShort()) {
			incompatRegs.set(REG_A_IDX);
		}
		if (!getRegB().fitsUnconstrained()) {
			incompatRegs.set(REG_B_IDX);
		}
		return incompatRegs;
	}
}