package soot.toolkits.exceptions;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Singletons;
import soot.Trap;
import soot.Unit;

/**
 * Some compilers generate duplicate traps:
 * 
 *    Exception table:
 *      from    to  target type
 *          9    30    37   Class java/lang/Throwable
 *          9    30    44   any
 *         37    46    44   any
 *
 * The semantics is as follows:
 * 
 * 		try {
 * 			// block
 * 		}
 * 		catch {
 * 			// handler 1
 * 		}
 * 		finally {
 * 			// handler 2
 * 		}
 * 
 * In this case, the first trap covers the block and jumps to handler 1. The
 * second trap also covers the block and jumps to handler 2. The third trap
 * covers handler 1 and jumps to handler 2. If we treat "any" as java.lang.
 * Throwable, the second handler is clearly unnecessary. Worse, it violates
 * Soot's invariant that there may only be one handler per combination of
 * covered code region and jump target.
 * 
 * This transformer detects and removes such unnecessary traps.
 * 
 * @author Steven Arzt
 *
 */
public class DuplicateCatchAllTrapRemover extends BodyTransformer {

	public DuplicateCatchAllTrapRemover(Singletons.Global g) {
	}

	public static DuplicateCatchAllTrapRemover v() {
		return soot.G.v().soot_toolkits_exceptions_DuplicateCatchAllTrapRemover();
	}

	@Override
	protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
		// Find two traps that use java.lang.Throwable as their type and that
		// span the same code region
		for (Iterator<Trap> t1It = b.getTraps().snapshotIterator(); t1It.hasNext(); ) {
			Trap t1 = t1It.next();
			if (t1.getException().getName().equals("java.lang.Throwable")) {
				for (Iterator<Trap> t2It = b.getTraps().snapshotIterator(); t2It.hasNext(); ) {
					Trap t2 = t2It.next();
					if (t1 != t2
							&& t1.getBeginUnit() == t2.getBeginUnit()
							&& t1.getEndUnit() == t2.getEndUnit()
							&& t2.getException().getName().equals("java.lang.Throwable")) {
						// Both traps (t1, t2) span the same code and catch java.lang.Throwable.
						// Check if one trap jumps to a target that then jumps to the target of
						// the other trap.
						for (Trap t3 : b.getTraps()) {
							if (t3 != t1 && t3 != t2
									&& t3.getException().getName().equals("java.lang.Throwable")) {
								if (trapCoversUnit(b, t3, t1.getHandlerUnit())
										&& t3.getHandlerUnit() == t2.getHandlerUnit()) {
									// c -> t1 -> t3 -> t2 && c -> t2
									b.getTraps().remove(t2);
									break;
								}
								else if (trapCoversUnit(b, t3, t2.getHandlerUnit())
										&& t3.getHandlerUnit() == t1.getHandlerUnit()) {
									// c -> t2 -> t3 -> t1 && c -> t1
									b.getTraps().remove(t1);
									break;
								}
							}
								
						}
					}
				}
			}
		}
	}

	/**
	 * Checks whether the given trap covers the given unit, i.e., there is an
	 * exceptional control flow from the given unit to the given trap
	 * @param b The body containing the unit and the trap
	 * @param trap The trap
	 * @param unit The unit
	 * @return True if there can be an exceptional control flow from the given
	 * unit to the given trap
	 */
	private boolean trapCoversUnit(Body b, Trap trap, Unit unit) {
		for (Iterator<Unit> unitIt = b.getUnits().iterator(trap.getBeginUnit(),
				trap.getEndUnit()); unitIt.hasNext(); ) {
			Unit u = unitIt.next();
			if (u == unit)
				return true;
		}
		return false;
	}

}
