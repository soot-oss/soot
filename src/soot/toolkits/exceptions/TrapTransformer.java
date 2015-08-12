package soot.toolkits.exceptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import soot.BodyTransformer;
import soot.Unit;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.toolkits.graph.UnitGraph;

/**
 * Common abstract base class for all body transformers that change the trap
 * list to, e.g., minimize the trap list
 * 
 * @author Steven Arzt
 *
 */
public abstract class TrapTransformer extends BodyTransformer {

	public Set<Unit> getUnitsWithMonitor(UnitGraph ug) {
		Set<Unit> unitsWithMonitor = new HashSet<Unit>();
		
		for (Unit head : ug.getHeads()) {
			List<Unit> workList = new ArrayList<Unit>();
			workList.add(head);
			Set<Unit> doneSet = new HashSet<Unit>();
			
			while (!workList.isEmpty()) {
				Unit curUnit = workList.remove(0);
				if (!doneSet.add(curUnit))
					continue;
				
				workList.addAll(ug.getSuccsOf(curUnit));
				
				// If this is an "entermonitor" construct,
				// we're in a monitor from now on
				if (curUnit instanceof EnterMonitorStmt)
					unitsWithMonitor.add(curUnit);
				// If we leave a monitor, we're out now
				else if (curUnit instanceof ExitMonitorStmt)
					continue;
				else {
					for (Unit pred : ug.getPredsOf(curUnit))
						if (unitsWithMonitor.contains(pred))
							unitsWithMonitor.add(curUnit);
				}
			}
		}
		
		// Get rid of the entermonitor statements themselves
		for (Iterator<Unit> it = unitsWithMonitor.iterator(); it.hasNext(); )
			if (it.next() instanceof EnterMonitorStmt)
				it.remove();
		
		return unitsWithMonitor;
	}

}
