package soot.dexpler;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.Stmt;
import soot.options.Options;
import soot.toolkits.scalar.UnusedLocalEliminator;

/**
 * Transformer that simplifies array initializations. It converts
 * 
 * 		a = 0
 * 		b = 42
 * 		c[a] = b
 * 
 * to
 * 
 * 		c[0] 42
 * 
 * This transformer performs copy propagation, dead assignment elimination, and
 * unused local elimination at once for this special case. The idea is to reduce
 * the code as much as possible for this special case before applying the more
 * expensive other transformers.
 * 
 * @author Steven Arzt
 *
 */
public class DexArrayInitReducer extends BodyTransformer {
	
	public static DexArrayInitReducer v() {
		return new DexArrayInitReducer();
	}

	@Override
	protected void internalTransform(Body b, String phaseName,
			Map<String, String> options) {
		// Make sure that we only have linear control flow
		if (!b.getTraps().isEmpty())
			return;
		
		// Look for a chain of two constant assignments followed by an array put
		Unit u1 = null, u2 = null;
		for (Iterator<Unit> uIt = b.getUnits().snapshotIterator(); uIt.hasNext(); ) {
			Unit u = uIt.next();
			
			// If this is not an assignment, it does not matter.
			if (!(u instanceof AssignStmt) || !((Stmt) u).getBoxesPointingToThis().isEmpty()) {
				u1 = null;
				u2 = null;
				continue;
			}
			
			// If this is an assignment to an array, we must already have two
			// preceding constant assignments
			AssignStmt assignStmt = (AssignStmt) u;
			if (assignStmt.getLeftOp() instanceof ArrayRef) {
				if (u1 != null && u2 != null && u2.getBoxesPointingToThis().isEmpty()
						&& assignStmt.getBoxesPointingToThis().isEmpty()) {
					ArrayRef arrayRef = (ArrayRef) assignStmt.getLeftOp();
					
					Value u1val = u1.getDefBoxes().get(0).getValue();
					Value u2val = u2.getDefBoxes().get(0).getValue();
					
					// index
					if (arrayRef.getIndex() == u1val)
						arrayRef.setIndex(((AssignStmt) u1).getRightOp());
					else if (arrayRef.getIndex() == u2val)
						arrayRef.setIndex(((AssignStmt) u2).getRightOp());
					
					// value
					if (assignStmt.getRightOp() == u1val)
						assignStmt.setRightOp(((AssignStmt) u1).getRightOp());
					else if (assignStmt.getRightOp() == u2val)
						assignStmt.setRightOp(((AssignStmt) u2).getRightOp());
					
					// Remove the unnecessary assignments
					Unit checkU = u;
					boolean foundU1 = false, foundU2 = false,
							doneU1 = false, doneU2 = false;
					while (!(doneU1 && doneU2) && !(foundU1 && foundU2) && checkU != null) {
						// Does the current statement use the value?
						for (ValueBox vb : checkU.getUseBoxes()) {
							if (!doneU1 && vb.getValue() == u1val)
								foundU1 = true;
							if (!doneU2 && vb.getValue() == u2val)
								foundU2 = true;
						}
						
						// Does the current statement overwrite the value?
						for (ValueBox vb : checkU.getDefBoxes()) {
							if (vb.getValue() == u1val)
								doneU1 = true;
							else if (vb.getValue() == u2val)
								doneU2 = true;
						}
						
						// If this statement branches, we abort
						if (checkU.branches()) {
							foundU1 = true;
							foundU2 = true;
							break;
						}
						
						// Get the next statement
						checkU = b.getUnits().getSuccOf(checkU);
					}
					if (!foundU1) {
						// only remove constant assignment if the left value is Local
						if (u1val instanceof Local) {
							b.getUnits().remove(u1);
							if (Options.v().verbose()) {
								G.v().out.println("[" + b.getMethod().getName() + "]    remove 1 " + u1);
							}
						}
					}
					if (!foundU2) {
						// only remove constant assignment if the left value is Local
						if (u2val instanceof Local) {
							b.getUnits().remove(u2);
							if (Options.v().verbose()) {
								G.v().out.println("[" + b.getMethod().getName() + "]    remove 2 " + u2);
							}
						}
					}
					
					u1 = null;
					u2 = null;
				}
				else {
					// No proper initialization before
					u1 = null;
					u2 = null;
					continue;
				}
			}
			
			// We have a normal assignment. This could be an array index or
			// value.
			if (!(assignStmt.getRightOp() instanceof Constant)) {
				u1 = null;
				u2 = null;
				continue;
			}
			
			if (u1 == null) {
				u1 = assignStmt;
			}
			else if (u2 == null) {
				u2 = assignStmt;

				// If the last value is overwritten again, we start again at the beginning
				if (u1 != null) {
					Value op1 = ((AssignStmt) u1).getLeftOp();
					if (op1 == ((AssignStmt) u2).getLeftOp()) {
						u1 = u2;
						u2 = null;
					}
				}
			}
			else {
				u1 = u2;
				u2 = assignStmt;
			}
		}
		
		// Remove all locals that are no longer necessary
		UnusedLocalEliminator.v().transform(b);
	}

}
