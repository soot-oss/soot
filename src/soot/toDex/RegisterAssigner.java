package soot.toDex;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.jf.dexlib2.Opcode;

import soot.jimple.Stmt;
import soot.toDex.instructions.AddressInsn;
import soot.toDex.instructions.Insn;
import soot.toDex.instructions.Insn11n;
import soot.toDex.instructions.Insn21s;
import soot.toDex.instructions.Insn23x;
import soot.toDex.instructions.TwoRegInsn;

/**
 * Assigns final register numbers in instructions so that they fit into their
 * format and obey the calling convention (that is, the last registers are for
 * the parameters).<br>
 * <br>
 * Note that the final instruction list can contain additional "move"
 * instructions.<br>
 * <br>
 * IMPLEMENTATION NOTE: The algorithm is heavily inspired by
 * com.android.dx.dex.code.OutputFinisher.
 */
class RegisterAssigner {

	private class InstructionIterator implements Iterator<Insn> {

		private final ListIterator<Insn> insnsIterator;
		private final Map<Insn, Stmt> insnStmtMap;
		private final Map<Insn, LocalRegisterAssignmentInformation> insnRegisterMap;

		public InstructionIterator(List<Insn> insns, Map<Insn, Stmt> insnStmtMap,
				Map<Insn, LocalRegisterAssignmentInformation> insnRegisterMap) {
			this.insnStmtMap = insnStmtMap;
			this.insnsIterator = insns.listIterator();
			this.insnRegisterMap = insnRegisterMap;
		}

		@Override
		public boolean hasNext() {
			return insnsIterator.hasNext();
		}

		@Override
		public Insn next() {
			return insnsIterator.next();
		}

		public Insn previous() {
			return insnsIterator.previous();
		}

		@Override
		public void remove() {
			this.insnsIterator.remove();
		}

		public void add(Insn element, Insn forOriginal, Register newRegister) {
			LocalRegisterAssignmentInformation originalRegisterLocal = this.insnRegisterMap.get(forOriginal);
			if (originalRegisterLocal != null) {
				if (newRegister != null)
					this.insnRegisterMap.put(element, LocalRegisterAssignmentInformation.v(newRegister,
							this.insnRegisterMap.get(forOriginal).getLocal()));
				else
					this.insnRegisterMap.put(element, originalRegisterLocal);
			}

			if (this.insnStmtMap.containsKey(forOriginal))
				this.insnStmtMap.put(element, insnStmtMap.get(forOriginal));
			this.insnsIterator.add(element);
		}

		public void set(Insn element, Insn forOriginal) {
			LocalRegisterAssignmentInformation originalRegisterLocal = this.insnRegisterMap.get(forOriginal);
			if (originalRegisterLocal != null) {
				this.insnRegisterMap.put(element, originalRegisterLocal);
				this.insnRegisterMap.remove(forOriginal);
			}

			if (this.insnStmtMap.containsKey(forOriginal)) {
				this.insnStmtMap.put(element, insnStmtMap.get(forOriginal));
				this.insnStmtMap.remove(forOriginal);
			}
			this.insnsIterator.set(element);
		}

	}

	private RegisterAllocator regAlloc;

	public RegisterAssigner(RegisterAllocator regAlloc) {
		this.regAlloc = regAlloc;
	}

	public List<Insn> finishRegs(List<Insn> insns, Map<Insn, Stmt> insnsStmtMap,
			Map<Insn, LocalRegisterAssignmentInformation> instructionRegisterMap,
			List<LocalRegisterAssignmentInformation> parameterInstructionsList) {
		renumParamRegsToHigh(insns, parameterInstructionsList);
		reserveRegisters(insns, insnsStmtMap, parameterInstructionsList);
		InstructionIterator insnIter = new InstructionIterator(insns, insnsStmtMap, instructionRegisterMap);
		while (insnIter.hasNext()) {
			Insn oldInsn = insnIter.next();
			if (oldInsn.hasIncompatibleRegs()) {
				Insn fittingInsn = findFittingInsn(oldInsn);
				if (fittingInsn != null) {
					insnIter.set(fittingInsn, oldInsn);
				} else {
					fixIncompatRegs(oldInsn, insnIter);
				}
			}
		}
		return insns;
	}

	private void renumParamRegsToHigh(List<Insn> insns,
			List<LocalRegisterAssignmentInformation> parameterInstructionsList) {
		int regCount = regAlloc.getRegCount();
		int paramRegCount = regAlloc.getParamRegCount();
		if (paramRegCount == 0 || paramRegCount == regCount) {
			return; // no params or no locals -> nothing to do
		}
		for (Insn insn : insns) {
			for (Register r : insn.getRegs()) {
				renumParamRegToHigh(r, regCount, paramRegCount);
			}
		}
		for (LocalRegisterAssignmentInformation parameter : parameterInstructionsList) {
			renumParamRegToHigh(parameter.getRegister(), regCount, paramRegCount);
		}
	}

	private void renumParamRegToHigh(Register r, int regCount, int paramRegCount) {
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

	/**
	 * Reserves low registers in case we later find an instruction that has
	 * short operands. We can then move the real operands into the reserved low
	 * ones and use those instead.
	 * 
	 * @param insns
	 * @param insnsStmtMap
	 * @param parameterInstructionsList
	 */
	private void reserveRegisters(List<Insn> insns, Map<Insn, Stmt> insnsStmtMap,
			List<LocalRegisterAssignmentInformation> parameterInstructionsList) {
		// reserve registers as long as new ones are needed
		int reservedRegs = 0;
		while (true) {
			int regsNeeded = getRegsNeeded(reservedRegs, insns, insnsStmtMap);
			int regsToReserve = regsNeeded - reservedRegs;
			if (regsToReserve <= 0) {
				break;
			}
			regAlloc.increaseRegCount(regsToReserve);
			// "reservation": shift the old regs to higher numbers
			for (Insn insn : insns) {
				shiftRegs(insn, regsToReserve);
			}
			for (LocalRegisterAssignmentInformation info : parameterInstructionsList) {
				Register r = info.getRegister();
				r.setNumber(r.getNumber() + regsToReserve);
			}
			reservedRegs += regsToReserve;
		}
	}

	/**
	 * Gets the maximum number of registers needed by a single instruction in
	 * the given list of instructions.
	 * 
	 * @param regsAlreadyReserved
	 * @param insns
	 * @param insnsStmtMap
	 * @return
	 */
	private int getRegsNeeded(int regsAlreadyReserved, List<Insn> insns, Map<Insn, Stmt> insnsStmtMap) {
		int regsNeeded = regsAlreadyReserved; // we only need regs that weren't
												// reserved yet
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
				insnsStmtMap.put(fittingInsn, insnsStmtMap.get(insn));
				insnsStmtMap.remove(insn);
				continue;
			}
			// no fitting instruction -> save if we need more registers
			int newRegsNeeded = insn.getMinimumRegsNeeded();
			if (newRegsNeeded > regsNeeded) {
				regsNeeded = newRegsNeeded;
			}
		}
		return regsNeeded;
	}

	private void shiftRegs(Insn insn, int shiftAmount) {
		for (Register r : insn.getRegs()) {
			r.setNumber(r.getNumber() + shiftAmount);
		}
	}

	private void fixIncompatRegs(Insn insn, InstructionIterator allInsns) {
		List<Register> regs = insn.getRegs();
		BitSet incompatRegs = insn.getIncompatibleRegs();
		Register resultReg = regs.get(0);
		// do we have an incompatible result reg?
		boolean hasResultReg = insn.getOpcode().setsRegister() || insn.getOpcode().setsWideRegister();
		boolean isResultRegIncompat = incompatRegs.get(0);

		// is there an incompat result reg which is not also used as a source
		// (like in /2addr)?
		if (hasResultReg && isResultRegIncompat && !insn.getOpcode().name.endsWith("/2addr")
				&& !insn.getOpcode().name.equals("check-cast")) {
			// yes, so pretend result reg is compatible, since it will get a
			// special move
			incompatRegs.clear(0);
		}

		// handle normal incompatible regs, if any: add moves
		if (incompatRegs.cardinality() > 0) {
			addMovesForIncompatRegs(insn, allInsns, regs, incompatRegs);
		}

		// handle incompatible result reg. This is for three-operand
		// instructions
		// in which the result register is out of scope. For /2addr
		// instructions,
		// we need to coherently move source and result, so this is already done
		// in addMovesForIncompatRegs.
		if (hasResultReg && isResultRegIncompat) {
			Register resultRegClone = resultReg.clone();
			addMoveForIncompatResultReg(allInsns, resultRegClone, resultReg, insn);
		}
	}

	private void addMoveForIncompatResultReg(InstructionIterator insns, Register destReg, Register origResultReg,
			Insn curInsn) {
		if (destReg.getNumber() == 0) {
			// destination reg is already where we want it: avoid "move r0, r0"
			return;
		}
		origResultReg.setNumber(0); // fix reg in original insn
		Register sourceReg = new Register(destReg.getType(), 0);
		Insn extraMove = StmtVisitor.buildMoveInsn(destReg, sourceReg);
		insns.add(extraMove, curInsn, destReg);
	}

	/**
	 * Adds move instructions to put values into lower registers before using
	 * them in an instruction. This assumes that enough registers have been
	 * reserved at 0...n.
	 * 
	 * @param curInsn
	 * @param insns
	 * @param regs
	 * @param incompatRegs
	 */
	private void addMovesForIncompatRegs(Insn curInsn, InstructionIterator insns, List<Register> regs,
			BitSet incompatRegs) {
		Register newRegister = null;
		final Register resultReg = regs.get(0);
		final boolean hasResultReg = curInsn.getOpcode().setsRegister() || curInsn.getOpcode().setsWideRegister();
		Insn moveResultInsn = null;

		insns.previous(); // extra MOVEs are added _before_ the current insn
		int nextNewDestination = 0;
		for (int regIdx = 0; regIdx < regs.size(); regIdx++) {
			if (incompatRegs.get(regIdx)) {
				// reg is incompatible -> add extra MOVE
				Register incompatReg = regs.get(regIdx);
				if (incompatReg.isEmptyReg()) {
					/*
					 * second half of a wide reg: do not add a move, since the
					 * empty reg is only considered incompatible to reserve the
					 * subsequent reg number
					 */
					continue;
				}
				Register source = incompatReg.clone();
				Register destination = new Register(source.getType(), nextNewDestination);
				nextNewDestination += SootToDexUtils.getDexWords(source.getType());
				if (source.getNumber() != destination.getNumber()) {
					Insn extraMove = StmtVisitor.buildMoveInsn(destination, source);
					insns.add(extraMove, curInsn, null); // advances the cursor,
															// so no next()
															// needed
					// finally patch the original, incompatible reg
					incompatReg.setNumber(destination.getNumber());

					// If this is the result register, we need to save the
					// result as well
					if (hasResultReg && regIdx == resultReg.getNumber()) {
						moveResultInsn = StmtVisitor.buildMoveInsn(source, destination);
						newRegister = destination;
					}
				}
			}
		}
		insns.next(); // get past current insn again

		if (moveResultInsn != null)
			insns.add(moveResultInsn, curInsn, newRegister); // advances the
																// cursor, so no
																// next() needed
	}

	private Insn findFittingInsn(Insn insn) {
		if (!insn.hasIncompatibleRegs()) {
			return null; // no incompatible regs -> no fitting needed
		}
		// we expect the dex specification to rarely change, so we hard-code the
		// mapping "unfitting -> fitting"
		Opcode opc = insn.getOpcode();
		if (insn instanceof Insn11n && opc.equals(Opcode.CONST_4)) {
			// const-4 (11n, byteReg) -> const-16 (21s, shortReg)
			Insn11n unfittingInsn = (Insn11n) insn;
			if (unfittingInsn.getRegA().fitsShort()) {
				return new Insn21s(Opcode.CONST_16, unfittingInsn.getRegA(), unfittingInsn.getLitB());
			}
		} else if (insn instanceof TwoRegInsn && opc.name.endsWith("_2ADDR")) {
			// */2addr (12x, byteReg,byteReg) -> * (23x,
			// shortReg,shortReg,shortReg)
			Register regA = ((TwoRegInsn) insn).getRegA();
			Register regB = ((TwoRegInsn) insn).getRegB();
			if (regA.fitsShort() && regB.fitsShort()) {
				// use new opcode without the "/2addr"
				int oldOpcLength = opc.name.length();
				String newOpcName = opc.name.substring(0, oldOpcLength - 6);
				Opcode newOpc = Opcode.valueOf(newOpcName);
				Register regAClone = regA.clone();
				return new Insn23x(newOpc, regA, regAClone, regB);
			}
		} else if (insn instanceof TwoRegInsn && SootToDexUtils.isNormalMove(opc)) {
			/*
			 * move+ (12x, byteReg,byteReg) -> move+/from16 (22x,
			 * shortReg,unconstReg) -> move+/16 (32x, unconstReg,unconstReg)
			 * where "+" is "", "-object" or "-wide"
			 */
			Register regA = ((TwoRegInsn) insn).getRegA();
			Register regB = ((TwoRegInsn) insn).getRegB();
			if (regA.getNumber() != regB.getNumber()) {
				return StmtVisitor.buildMoveInsn(regA, regB);
			}
		}
		// no fitting insn found
		return null;
	}
}