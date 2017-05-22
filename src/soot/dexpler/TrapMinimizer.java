package soot.dexpler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.Singletons;
import soot.Trap;
import soot.Unit;
import soot.jimple.Jimple;
import soot.options.Options;
import soot.toolkits.exceptions.TrapTransformer;
import soot.toolkits.graph.ExceptionalGraph.ExceptionDest;
import soot.toolkits.graph.ExceptionalUnitGraph;

/**
 * Transformer that splits traps for Dalvik whenever 
 * a statements within the trap cannot reach the trap's handler.
 * 
 * Before: 
 *  trap from label1 to label2 with handler
 * 
 * label1:
 * stmt1 ----> handler
 * stmt2 
 * stmt3 ----> handler
 * label2:
 * 
 * After: 
 *  trap from label1 to label2 with handler
 *  trap from label3 to label4 with handler
 *  
 * label1:
 * stmt1 ----> handler
 * label2:
 * stmt2
 * label3:
 * stmt3 ----> handler
 * label4:
 *
 * @author Alexandre Bartel
 */
public class TrapMinimizer extends TrapTransformer {

    public TrapMinimizer( Singletons.Global g ) {}

	public static TrapMinimizer v() {
		return soot.G.v().soot_dexpler_TrapMinimizer();
	}

	@Override
	protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
		// If we have less then two traps, there's nothing to do here
		if (b.getTraps().size() == 0)
			return;
		
		ExceptionalUnitGraph eug = new ExceptionalUnitGraph(b, DalvikThrowAnalysis.v(),
				Options.v().omit_excepting_unit_edges());
		Set<Unit> unitsWithMonitor = getUnitsWithMonitor(eug);

		Map<Trap, List<Trap>> replaceTrapBy = new HashMap<Trap, List<Trap>>(b.getTraps().size());
		boolean updateTrap = false;
		for (Trap tr : b.getTraps()) {
			List<Trap> newTraps = new ArrayList<Trap>(); // will contain the new traps
			Unit firstTrapStmt = tr.getBeginUnit(); // points to the first unit in the trap
			boolean goesToHandler = false; // true if there is an edge from the unit to the handler of the current trap
			updateTrap = false;
			for (Unit u = tr.getBeginUnit(); u != tr.getEndUnit(); u = b.getUnits().getSuccOf(u)) {
				if (goesToHandler) {
					goesToHandler = false;
				} else {
					// if the previous unit has no exceptional edge to the handler,
					// update firstTrapStmt to point to the current unit
					firstTrapStmt = u;
				}
				
				// If this is the catch-all block and the current unit has an,
				// active monitor, we need to keep the block
				if (tr.getException().getName().equals("java.lang.Throwable")
						&& unitsWithMonitor.contains(u))
					goesToHandler = true;
				
				// check if the current unit has an edge to the current trap's handler
				if (!goesToHandler)
					if (DalvikThrowAnalysis.v().mightThrow(u).catchableAs(tr.getException().getType())) {
						// We need to be careful here. The ExceptionalUnitGraph will
						// always give us an edge from the predecessor of the excepting
						// unit to the handler. This predecessor, however, does not need
						// to be inside the new minimized catch block.
						for (ExceptionDest<Unit> ed : eug.getExceptionDests(u)) {
							if (ed.getTrap() == tr) {
								goesToHandler = true;
								break;
							}
						}
					}
				
				if (!goesToHandler) {
					// if the current unit does not have an edge to the current trap's handler,
					// add a new trap starting at firstTrapStmt ending at the unit before the
					// current unit 'u'.
					updateTrap = true;
					if (firstTrapStmt == u) // do not add an empty trap, but set updateTrap to true
						continue;
					Trap t = Jimple.v().newTrap(tr.getException(), firstTrapStmt, u, tr.getHandlerUnit());
					newTraps.add(t);
				} else {
					// if the current unit has an edge to the current trap's handler,
					// add a trap if the current trap has been updated before and if the
					// next unit is outside the current trap.
					if (b.getUnits().getSuccOf(u) == tr.getEndUnit() && updateTrap) {
						Trap t = Jimple.v().newTrap(tr.getException(), firstTrapStmt, tr.getEndUnit(), tr.getHandlerUnit());
						newTraps.add(t);
					}
				}
			}
			// if updateTrap is true, the current trap has to be replaced by the set of newly created traps 
			// (this set can be empty if the trap covers only instructions that cannot throw any exceptions)
			if (updateTrap) {
				replaceTrapBy.put(tr, newTraps);
			}
		}

		// replace traps where necessary
		for (Trap k : replaceTrapBy.keySet()) {
			b.getTraps().insertAfter(replaceTrapBy.get(k), k); // we must keep the order
			b.getTraps().remove(k);
		}

	}

}
