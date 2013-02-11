package soot.toDex.instructions;

import java.util.BitSet;

import org.jf.dexlib.Item;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction35c;

import soot.toDex.Register;

/**
 * The "35c" instruction format: It needs three 16-bit code units, has five registers
 * and is used for method/type items (hence the "c" for "constant pool").<br>
 * <br>
 * It is used by the "filled-new-array" opcode and the various "invoke-" opcodes.<br>
 * <br>
 * IMPLEMENTATION NOTE: the wide args for "35c" must be explicitly stated - internally,
 * such args are implicitly represented by e.g. "regD = wide, regE = emptyReg" to avoid using null
 * and to distinguish the first "half" of a wide reg from the second one.
 * this is made explicit ("regD.num, regD.num + 1") while building the real insn and while getting
 * the "real" explicit reg numbers.
 */
public class Insn35c extends AbstractInsn implements FiveRegInsn {
	
	private int regCount;
	
	private final Item<?> referencedItem;

	public Insn35c(Opcode opc, int regCount, Register regD, Register regE, Register regF, Register regG, Register regA, Item<?> referencedItem) {
		super(opc);
		this.regCount = regCount;
		regs.add(regD);
		regs.add(regE);
		regs.add(regF);
		regs.add(regG);
		regs.add(regA);
		this.referencedItem = referencedItem;
	}

	public Register getRegD() {
		return regs.get(REG_D_IDX);
	}

	public Register getRegE() {
		return regs.get(REG_E_IDX);
	}

	public Register getRegF() {
		return regs.get(REG_F_IDX);
	}

	public Register getRegG() {
		return regs.get(REG_G_IDX);
	}

	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}
	
	private static boolean isImplicitWide(Register firstReg, Register secondReg) {
		return firstReg.isWide() && secondReg.isEmptyReg();
	}
	
	private static int getPossiblyWideNumber(Register reg, Register previousReg) {
		if (isImplicitWide(previousReg, reg)) {
			// we cannot use reg.getNumber(), since the empty reg's number is always 0
			return previousReg.getNumber() + 1;
		}
		return reg.getNumber();
	}
	
	private int[] getRealRegNumbers() {
		int[] realRegNumbers = new int[5];
		Register regD = getRegD();
		Register regE = getRegE();
		Register regF = getRegF();
		Register regG = getRegG();
		Register regA = getRegA();
		realRegNumbers[REG_D_IDX] = regD.getNumber();
		realRegNumbers[REG_E_IDX] = getPossiblyWideNumber(regE, regD);
		realRegNumbers[REG_F_IDX] = getPossiblyWideNumber(regF, regE);
		realRegNumbers[REG_G_IDX] = getPossiblyWideNumber(regG, regF);
		realRegNumbers[REG_A_IDX] = getPossiblyWideNumber(regA, regG);
		return realRegNumbers;
	}

	@Override
	protected Instruction getRealInsn0() {
		int[] realRegNumbers = getRealRegNumbers();
		byte regDNumber = (byte) realRegNumbers[REG_D_IDX];
		byte regENumber = (byte) realRegNumbers[REG_E_IDX];
		byte regFNumber = (byte) realRegNumbers[REG_F_IDX];
		byte regGNumber = (byte) realRegNumbers[REG_G_IDX];
		byte regANumber = (byte) realRegNumbers[REG_A_IDX];
		return new Instruction35c(opc, regCount, regDNumber, regENumber, regFNumber, regGNumber, regANumber, referencedItem);
	}
	
	@Override
	public BitSet getIncompatibleRegs() {
		BitSet incompatRegs = new BitSet(5);
		int[] realRegNumbers = getRealRegNumbers();
		for (int i = 0; i < realRegNumbers.length; i++) {
			// real regs aren't wide, because those are represented as two non-wide regs
			boolean isCompatible = Register.fitsByte(realRegNumbers[i], false);
			if (!isCompatible) {
				incompatRegs.set(i);
				// if second half of a wide reg is incompatible, so is its first half
				Register possibleSecondHalf = regs.get(i);
				if (possibleSecondHalf.isEmptyReg() && i > 0) {
					Register possibleFirstHalf = regs.get(i - 1);
					if (possibleFirstHalf.isWide()) {
						incompatRegs.set(i - 1);
					}
				}
			}
		}
		return incompatRegs;
	}
	
	@Override
	public String toString() {
		return super.toString() + " (" + regCount + " regs), ref: " + referencedItem;
	}
}