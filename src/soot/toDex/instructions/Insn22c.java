package soot.toDex.instructions;

import java.util.BitSet;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.builder.instruction.BuilderInstruction22c;
import org.jf.dexlib2.writer.builder.BuilderReference;

import soot.toDex.LabelAssigner;
import soot.toDex.Register;

/**
 * The "22c" instruction format: It needs two 16-bit code units, has two registers
 * and is used for class/type items (hence the "c" for "constant pool").<br>
 * <br>
 * It is used e.g. by the opcodes "instance-of", "new-array" and "iget".
 */
public class Insn22c extends AbstractInsn implements TwoRegInsn {
	
	private BuilderReference referencedItem;

	public Insn22c(Opcode opc, Register regA, Register regB, BuilderReference referencedItem) {
		super(opc);
		regs.add(regA);
		regs.add(regB);
		this.referencedItem = referencedItem;
	}

	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}
	
	public Register getRegB() {
		return regs.get(REG_B_IDX);
	}

	@Override
	protected BuilderInstruction getRealInsn0(LabelAssigner assigner) {
		return new BuilderInstruction22c(opc, getRegA().getNumber(),
				getRegB().getNumber(), referencedItem);
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
		return super.toString() + " ref: " + referencedItem;
	}
}