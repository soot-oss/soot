package soot.toDex.instructions;

import java.util.BitSet;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction22s;

import soot.toDex.Register;

/**
 * The "22s" instruction format: It needs two 16-bit code units, has two registers
 * and is used for a 16-bit literal (hence the "s" for "short").<br>
 * <br>
 * It is used by the "/lit16" opcodes for binary operations.
 */
public class Insn22s extends AbstractInsn implements TwoRegInsn {
	
	private short litC;

	public Insn22s(Opcode opc, Register regA, Register regB, short litC) {
		super(opc);
		regs.add(regA);
		regs.add(regB);
		this.litC = litC;
	}
	
	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}
	
	public Register getRegB() {
		return regs.get(REG_B_IDX);
	}
	
	public short getLitC() {
		return litC;
	}

	@Override
	protected Instruction getRealInsn0() {
		return new Instruction22s(opc, (byte) getRegA().getNumber(), (byte) getRegB().getNumber(), getLitC());
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
	
	@Override
	public String toString() {
		return super.toString() + " lit: " + getLitC();
	}
}