package soot.asm;

import java.util.ArrayList;

import soot.Local;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;

/**
 * Frame of stack for an instruction.
 */
final class StackFrame {

	private Operand[] out;
	private Local[] inStackLocals;
	private ValueBox[] boxes;
	private ArrayList<Operand[]> in;
	private final JimpleSource src;
	
	/**
	 * Constructs a new stack frame.
	 * @param src source the frame belongs to.
	 */
	StackFrame(JimpleSource src) {
		this.src = src;
	}
	
	/**
	 * @return operands produced by this frame.
	 */
	Operand[] out() {
		return out;
	}
	
	/**
	 * Sets the operands used by this frame.
	 * @param oprs the operands.
	 */
	void in(Operand... oprs) {
		ArrayList<Operand[]> in = this.in;
		if (in == null) {
			in = this.in = new ArrayList<Operand[]>(1);
		} else {
			in.clear();
		}
		in.add(oprs);
		inStackLocals = new Local[oprs.length];
	}
	
	/**
	 * Sets the value boxes corresponding to the operands used
	 * by this frame.
	 * @param boxes the boxes.
	 */
	void boxes(ValueBox... boxes) {
		this.boxes = boxes;
	}
	
	/**
	 * Sets the operands produced by this frame.
	 * @param oprs the operands.
	 */
	void out(Operand... oprs) {
		out = oprs;
	}
	
	/**
	 * Merges the specified operands with the operands used by this frame.
	 * @param oprs the new operands.
	 * @throws IllegalArgumentException if the number of new operands is not equal
	 * to the number of old operands.
	 */
	void mergeIn(Operand... oprs) {
		ArrayList<Operand[]> in = this.in;
		if (in.get(0).length != oprs.length)
			throw new IllegalArgumentException("Invalid in operands length!");
		AssignStmt as;
		int nrIn = in.size();
		boolean diff = false;
		all_opr:
		for (int i = 0; i != oprs.length; i++) {
			Operand newOp = oprs[i];
			for (int j = 0; j != nrIn; j++) {
				if (in.get(j)[i].equivTo(newOp))
					continue all_opr;
			}
			diff = true;
			/* merge, since prevOp != newOp */
			Local stack = inStackLocals[i];
			if (stack != null) {
				if (newOp.stack == null) {
					newOp.stack = stack;
					as = Jimple.v().newAssignStmt(stack, newOp.value);
					src.setUnit(newOp.insn, as);
					newOp.updateBoxes();
				} else {
					as = Jimple.v().newAssignStmt(stack, newOp.stackOrValue());
					src.mergeUnits(newOp.insn, as);
					newOp.addBox(as.getRightOpBox());
				}
			} else {
				for (int j = 0; j != nrIn; j++) {
					stack = in.get(j)[i].stack;
					if (stack != null)
						break;
				}
				if (stack == null) {
					stack = newOp.stack;
					if (stack == null)
						stack = src.newStackLocal();
				}
				/* add assign statement for prevOp */
				ValueBox box = boxes == null ? null : boxes[i];
				for (int j = 0; j != nrIn; j++) {
					Operand prevOp = in.get(j)[i];
					if (prevOp.stack == stack)
						continue;
					prevOp.removeBox(box);
					if (prevOp.stack == null) {
						prevOp.stack = stack;
						as = Jimple.v().newAssignStmt(stack, prevOp.value);
						src.setUnit(prevOp.insn, as);
					} else {
						as = src.getUnit(prevOp.insn);
						ValueBox lvb = as.getLeftOpBox();
						if (lvb.getValue() != prevOp.stack)
							throw new AssertionError("Invalid stack local!");
						lvb.setValue(stack);
						prevOp.stack = stack;
					}
					prevOp.updateBoxes();
				}
				if (newOp.stack != stack) {
					if (newOp.stack == null) {
						newOp.stack = stack;
						as = Jimple.v().newAssignStmt(stack, newOp.value);
						src.setUnit(newOp.insn, as);
					} else {
						as = src.getUnit(newOp.insn);
						ValueBox lvb = as.getLeftOpBox();
						if (lvb.getValue() != newOp.stack)
							throw new AssertionError("Invalid stack local!");
						lvb.setValue(stack);
						newOp.stack = stack;
					}
					newOp.updateBoxes();
				}
				if (box != null)
					box.setValue(stack);
				inStackLocals[i] = stack;
			}
			
			/*
			 * this version uses allocates local if it
			 * finds both operands have stack locals allocated already
			 */
			/*if (stack == null) {
				if (in.size() != 1)
					throw new AssertionError("Local h " + in.size());
				stack = src.newStackLocal();
				inStackLocals[i] = stack;
				ValueBox box = boxes == null ? null : boxes[i];
				/* add assign statement for prevOp *
				for (int j = 0; j != nrIn; j++) {
					Operand prevOp = in.get(j)[i];
					prevOp.removeBox(box);
					if (prevOp.stack == null) {
						prevOp.stack = stack;
						as = Jimple.v().newAssignStmt(stack, prevOp.value);
						src.setUnit(prevOp.insn, as);
						prevOp.updateBoxes();
					} else {
						as = Jimple.v().newAssignStmt(stack, prevOp.stackOrValue());
						src.mergeUnits(prevOp.insn, as);
					}
					prevOp.addBox(as.getRightOpBox());
				}
				if (box != null)
					box.setValue(stack);
			}
			if (newOp.stack == null) {
				newOp.stack = stack;
				as = Jimple.v().newAssignStmt(stack, newOp.value);
				src.setUnit(newOp.insn, as);
				newOp.updateBoxes();
			} else {
				as = Jimple.v().newAssignStmt(stack, newOp.stackOrValue());
				src.mergeUnits(newOp.insn, as);
			}
			newOp.addBox(as.getRightOpBox());*/
		}
		if (diff)
			in.add(oprs);
	}
}