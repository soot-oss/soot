package soot.toolkits.exceptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.BodyTransformer;
import soot.Local;
import soot.Unit;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.toolkits.graph.UnitGraph;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

/**
 * Common abstract base class for all body transformers that change the trap
 * list to, e.g., minimize the trap list
 * 
 * @author Steven Arzt
 *
 */
public abstract class TrapTransformer extends BodyTransformer {
	
	public Set<Unit> getUnitsWithMonitor(UnitGraph ug) {
		// Idea: Associate each unit with a set of monitors held at that
		// statement
		MultiMap<Unit, Local> unitMonitors = new HashMultiMap<>();
		
		// Start at the heads of the unit graph
		List<Unit> workList = new ArrayList<>();
		Set<Unit> doneSet = new HashSet<>();
		for (Unit head : ug.getHeads()) {
			workList.add(head);
		}
		
		while (!workList.isEmpty()) {
			Unit curUnit = workList.remove(0);
			
			boolean hasChanged = false;
			Local exitLocal = null;
			if (curUnit instanceof EnterMonitorStmt) {
				// We enter a new monitor
				EnterMonitorStmt ems = (EnterMonitorStmt) curUnit;
				if (ems.getOp() instanceof Local)
					hasChanged = unitMonitors.put(curUnit, (Local) ems.getOp());
			}
			else if (curUnit instanceof ExitMonitorStmt) {
				// We leave a minitor
				ExitMonitorStmt ems = (ExitMonitorStmt) curUnit;
				if (ems.getOp() instanceof Local)
					exitLocal = (Local) ems.getOp();
			}
			
			// Copy over the monitors from the predecessors
			for (Unit pred : ug.getPredsOf(curUnit))
				for (Local l : unitMonitors.get(pred))
					if (l != exitLocal)
						if (unitMonitors.put(curUnit, l))
							hasChanged = true;
			
			// Work on the successors
			if (doneSet.add(curUnit) || hasChanged)
				workList.addAll(ug.getSuccsOf(curUnit));
		}
		
		return unitMonitors.keySet();
	}

}
