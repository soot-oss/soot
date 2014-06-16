package soot.toDex.instructions;

import java.util.BitSet;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction21c;
import org.jf.dexlib2.writer.builder.BuilderReference;

import soot.toDex.LabelAssigner;
import soot.toDex.Register;

/**
 * The "21c" instruction format: It needs two 16-bit code units, has one register
 * and is used for class/string/type items (hence the "c" for "constant pool").<br>
 * <br>
 * It is used e.g. by the opcodes "check-cast", "new-instance" and "const-string".
 */
public class Insn21c extends AbstractInsn implements OneRegInsn {
	
	private BuilderReference referencedItem;

	public Insn21c(Opcode opc, Register regA, BuilderReference referencedItem) {
		super(opc);
		regs.add(regA);
		this.referencedItem = referencedItem;
	}

	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}

	@Override
	protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
		return new BuilderInstruction21c(opc, (short) getRegA().getNumber(), referencedItem);
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
	public String toString() {
		return super.toString() + " ref: " + referencedItem;
	}
}