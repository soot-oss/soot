package soot.toDex;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;

import org.jf.dexlib.Code.Opcode;

import soot.toDex.instructions.AddressInsn;
import soot.toDex.instructions.Insn;
import soot.toDex.instructions.Insn11n;
import soot.toDex.instructions.Insn21s;
import soot.toDex.instructions.Insn23x;
import soot.toDex.instructions.TwoRegInsn;

// inspired by com.android.dx.dex.code.OutputFinisher
public class RegisterAssigner {
	
	private RegisterAllocator regAlloc;
	
	private List<Insn> insns;
	
	public RegisterAssigner(RegisterAllocator regAlloc, List<Insn> insns) {
		this.regAlloc = regAlloc;
		this.insns = insns;
	}
	
	public void renumParamRegsToHigh() {
		int regCount = regAlloc.getRegCount();
		int paramRegCount = regAlloc.getParamRegCount();
		if (paramRegCount == 0 || paramRegCount == regCount) {
			return; // no params or no locals -> nothing to do
		}
		for (Insn insn : insns) {
			for (Register r : insn.getRegs()) {
				int oldNum = r.getNumber();
				if (oldNum >= paramRegCount) {
					// not a parameter -> "move" down
					int newNormalRegNum = oldNum - paramRegCount;
					r.setNumber(newNormalRegNum);
				} else {
					// parameter -> "move" up
					int newParamRegNum = oldNum + regCount - paramRegCount;
					r.setNumber(newParamRegNum);
				}
			}
		}
	}

	public List<Insn> finishRegs() {
		reserveRegisters();
		ListIterator<Insn> insnIter = insns.listIterator();
		while (insnIter.hasNext()) {
			Insn oldInsn = insnIter.next();
			if (oldInsn instanceof AddressInsn) {
				continue; // no regs/fitting needed
			}
			// are there incompatible regs?
			BitSet curIncompatRegs = oldInsn.getIncompatibleRegs();
			if (curIncompatRegs.cardinality() > 0) {
				// first try to find a fitting insn
				Insn fittingInsn = findFittingInsn(oldInsn);
				if (fittingInsn != null) {
					insnIter.set(fittingInsn);
				} else {
					List<Register> curRegs = oldInsn.getRegs();
					// do we have an incompatible result reg?
					boolean hasResultReg = oldInsn.getOpcode().setsRegister();
					boolean isResultRegIncompat = curIncompatRegs.get(0);
					Register resultReg = curRegs.get(0).clone();
					// if we have and the result reg is not also used as a source (like in /2addr), pretend it is compatible
					if (hasResultReg && isResultRegIncompat && !oldInsn.getOpcode().name.endsWith("/2addr")) {
						curIncompatRegs.clear(0);
					}
					// handle normal incompatible regs, if any
					if (curIncompatRegs.cardinality() > 0) {
						addMovesForIncompatRegs(insnIter, curRegs, curIncompatRegs);
					}
					// handle incompatible result reg
					if (hasResultReg && isResultRegIncompat) {
						addMoveForIncompatResultReg(insnIter, resultReg, curRegs.get(0));
					}
				}
			}
		}
		return insns;
	}

	private void reserveRegisters() {
		// reserve registers as long as new ones are needed
		int reservedRegs = 0;
		while (true) {
			int regsNeeded = getRegsNeeded(reservedRegs);
			int regsToReserve = regsNeeded - reservedRegs;
			if (regsToReserve <= 0) {
				break;
			}
			regAlloc.increaseRegCount(regsToReserve);
			// "reservation": shift the old regs to higher numbers
			for (Insn insn : insns) {
				shiftRegs(insn, regsToReserve);
			}
			reservedRegs += regsToReserve;
		}
	}

	private static void shiftRegs(Insn insn, int shiftAmount) {
		for (Register r : insn.getRegs()) {
			r.setNumber(r.getNumber() + shiftAmount);
		}
	}
	
	private static void addMoveForIncompatResultReg(ListIterator<Insn> insns, Register destReg, Register origResultReg) {
		if (destReg.getNumber() == 0) {
			// destination reg is already where we want it: avoid "move r0, r0"
			return;
		}
		origResultReg.setNumber(0);
		Register sourceReg = new Register(destReg.getType(), 0);
		Insn extraMove = StmtVisitor.buildMoveInsn(destReg, sourceReg);
		insns.add(extraMove);
	}

	private static void addMovesForIncompatRegs(ListIterator<Insn> insns, List<Register> curRegs, BitSet incompatRegs) {
		insns.previous(); // extra MOVEs are added _before_ the current insn
		int nextNewDestination = 0;
		for (int regIdx = 0; regIdx < curRegs.size(); regIdx++) {
			if (incompatRegs.get(regIdx)) {
				// reg is incompatible -> add extra MOVE
				Register incompatReg = curRegs.get(regIdx);
				if (incompatReg.isEmptyReg()) {
					/*
					 * second half of a wide reg: do not add a move,
					 * since the empty reg is only considered incompatible to reserve the subsequent reg number
					 */
					continue;
				}
				Register source = incompatReg.clone();
				Register destination = new Register(source.getType(), nextNewDestination);
				nextNewDestination++;
				if (destination.isWide()) {
					nextNewDestination++;
				}
				if (source.getNumber() != destination.getNumber()) {
					Insn extraMove = StmtVisitor.buildMoveInsn(destination, source);
					insns.add(extraMove); // advances the cursor, so no next() needed
					// finally patch the original, incompatible reg
					incompatReg.setNumber(destination.getNumber());
				}
			}
		}
		insns.next(); // get past current insn again
	}
	
	private int getRegsNeeded(int regsAlreadyReserved) {
		int regsNeeded = regsAlreadyReserved; // we only need regs that weren't reserved yet
		for (int i = 0; i < insns.size(); i++) {
			Insn insn = insns.get(i);
			if (insn instanceof AddressInsn) {
				continue; // needs no regs/fitting
			}
			// first try to find a better opcode
			Insn fittingInsn = findFittingInsn(insn);
			if (fittingInsn != null) {
				// use the fitting instruction and continue with next one
				insns.set(i, fittingInsn);
				continue;
			}
			// no fitting instruction -> save if we need more registers
			int newRegsNeeded = calcRegsNeeded(insn);
			if (newRegsNeeded > regsNeeded) {
				regsNeeded = newRegsNeeded;
			}
		}
		return regsNeeded;
	}

	private static int calcRegsNeeded(Insn insn) {
		/*
		 * get fitting insn with regs starting at 0, that is an "ideal" one without register constraints.
		 * we use such an ideal insn because registers in insns that have no fitting alternative will only be
		 * replaced after register reservation, while adding MOVEs from high registers to those low ones starting at 0.
		 */
		List<Register> lowRegs = getLowVersion(insn.getRegs());
		Insn lowInsn = insn.shallowCloneWithRegs(lowRegs); // we only need a shallow clone for finding a fitting insn, the clone won't be written to
		Insn idealInsn = findFittingInsn(lowInsn);
		if (idealInsn == null) {
			// no better insn fits -> use our low insn anyhow
			idealInsn = lowInsn;
		}
		/*
		 * get the regs count needed for the fitting ideal insn, given the current regs.
		 * this corresponds with the number of additional MOVEs needed later on.
		 */
		BitSet stillIncompatRegs = idealInsn.getIncompatibleRegs(insn.getRegs());
		return insn.getMinimumRegsNeeded(stillIncompatRegs);
	}
	
	private static List<Register> getLowVersion(List<Register> regs) {
		List<Register> lowVersion = new ArrayList<Register>();
		int nextRegNum = 0;
		for (Register r : regs) {
			Register lowReg = r.isEmptyReg() ? r : new Register(r.getType(), nextRegNum);
			lowVersion.add(lowReg);
			nextRegNum++;
			if (r.isWide()) {
				nextRegNum++;
			}
		}
		return lowVersion;
	}

	private static Insn findFittingInsn(Insn insn) {
		if (!insn.hasIncompatibleRegs()) {
			return null; // no incompatible regs -> no fitting needed
		}
		// we expect the dex specification to rarely change, so we hard-code the mapping "unfitting -> fitting"
		Opcode opc = insn.getOpcode();
		if (insn instanceof Insn11n && opc.equals(Opcode.CONST_4)) {
			// const-4 (11n, byteReg) -> const-16 (21s, shortReg)
			Insn11n unfittingInsn = (Insn11n) insn;
			if (unfittingInsn.getRegA().fitsShort()) {
				return new Insn21s(Opcode.CONST_16, unfittingInsn.getRegA(), unfittingInsn.getLitB());
			}
		} else if (insn instanceof TwoRegInsn && opc.name.endsWith("/2addr")) {
			// */2addr (12x, byteReg,byteReg) -> * (23x, shortReg,shortReg,shortReg)
			Register regA = ((TwoRegInsn)insn).getRegA();
			Register regB = ((TwoRegInsn)insn).getRegB();
			if (regA.fitsShort() && regB.fitsShort()) {
				// use new opcode without the "/2addr"
				int oldOpcLength = opc.name.length();
				String newOpcName = opc.name.substring(0, oldOpcLength - 6);
				Opcode newOpc = Opcode.getOpcodeByName(newOpcName);
				Register regAClone = regA.clone();
				return new Insn23x(newOpc, regA, regAClone, regB);
			}
		} else if (insn instanceof TwoRegInsn && SootToDexUtils.isNormalMove(opc)) {
			/*
			 * move+ (12x, byteReg,byteReg) -> move+/from16 (22x, shortReg,unconstReg) -> move+/16 (32x, unconstReg,unconstReg)
			 * where "+" is "", "-object" or "-wide"
			 */
			Register regA = ((TwoRegInsn)insn).getRegA();
			Register regB = ((TwoRegInsn)insn).getRegB();
			if (regA.getNumber() != regB.getNumber()) {
				return StmtVisitor.buildMoveInsn(regA, regB);
			}
		}
		// no fitting insn found
		return null;
	}
}