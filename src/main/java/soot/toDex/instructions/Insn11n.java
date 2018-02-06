package soot.toDex.instructions;

import java.util.BitSet;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction11n;

import soot.toDex.LabelAssigner;
import soot.toDex.Register;

/**
 * The "11n" instruction format: It needs one 16-bit code unit, has one register
 * and is used for a 4-bit literal (hence the "n" for "nibble").<br>
 * <br>
 * It is used by the opcode "const/4".
 */
public class Insn11n extends AbstractInsn implements OneRegInsn {
	
	private byte litB;

	public Insn11n(Opcode opc, Register regA, byte litB) {
		super(opc);
		regs.add(regA);
		this.litB = litB;
	}

	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}
	
	public byte getLitB() {
		return litB;
	}

	@Override
	protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
		return new BuilderInstruction11n(opc, (byte) getRegA().getNumber(), getLitB());
	}
	
	@Override
	public BitSet getIncompatibleRegs() {
		BitSet incompatRegs = new BitSet(1);
		if (!getRegA().fitsByte()) {
			incompatRegs.set(REG_A_IDX);
		}
		return incompatRegs;
	}
	
	@Override
	public String toString() {
		return super.toString() + " lit: " + getLitB();
	}
}