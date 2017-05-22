package soot.dexpler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.ReturnStmt;
import soot.jimple.toolkits.scalar.LocalCreation;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.LocalUses;

public class DexReturnValuePropagator extends BodyTransformer {
	
	public static DexReturnValuePropagator v() {
		return new DexReturnValuePropagator();
	}

	@Override
	protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
        ExceptionalUnitGraph graph = new ExceptionalUnitGraph(body, DalvikThrowAnalysis.v(), true);
        LocalDefs localDefs = LocalDefs.Factory.newLocalDefs(graph);
        LocalUses localUses = null;
        LocalCreation localCreation = null;
        
		// If a return statement's operand has only one definition and this is
		// a copy statement, we take the original operand
		for (Unit u : body.getUnits())
			if (u instanceof ReturnStmt) {
				ReturnStmt retStmt = (ReturnStmt) u;
				if (retStmt.getOp() instanceof Local) {
					List<Unit> defs = localDefs.getDefsOfAt((Local) retStmt.getOp(), retStmt);
					if (defs.size() == 1 && defs.get(0) instanceof AssignStmt) {
						AssignStmt assign = (AssignStmt) defs.get(0);
						final Value rightOp = assign.getRightOp();
						final Value leftOp = assign.getLeftOp();
						
						// Copy over the left side if it is a local
						if (rightOp instanceof Local) {
							// We must make sure that the definition we propagate to
							// the return statement is not overwritten in between
							// a = 1; b = a; a = 3; return b; may not be translated
							// to return a;
							if (!isRedefined((Local) rightOp, u, assign, graph))
								retStmt.setOp(rightOp);
						}
						else if (rightOp instanceof Constant) {
							retStmt.setOp(rightOp);
						}
						// If this is a field access which has no other uses,
						// we rename the local to help splitting
						else if (rightOp instanceof FieldRef) {
							if (localUses == null)
								localUses = LocalUses.Factory.newLocalUses(body, localDefs);
							if (localUses.getUsesOf(assign).size() == 1) {
								if (localCreation == null)
									localCreation = new LocalCreation(body.getLocals(), "ret");
								Local newLocal = localCreation.newLocal(leftOp.getType());
								assign.setLeftOp(newLocal);
								retStmt.setOp(newLocal);
							}
						}
					}
				}
			}
	}

	/**
	 * Checks whether the given local has been redefined between the original
	 * definition unitDef and the use unitUse.
	 * @param l The local for which to check for redefinitions
	 * @param unitUse The unit that uses the local
	 * @param unitDef The unit that defines the local
	 * @param graph The unit graph to use for the check
	 * @return True if there is at least one path between unitDef and unitUse on
	 * which local l gets redefined, otherwise false 
	 */
    private boolean isRedefined(Local l, Unit unitUse, AssignStmt unitDef,
    		UnitGraph graph) {
    	List<Unit> workList = new ArrayList<Unit>();
    	workList.add(unitUse);
    	
    	Set<Unit> doneSet = new HashSet<Unit>();
    	
		// Check for redefinitions of the local between definition and use
    	while (!workList.isEmpty()) {
    		Unit curStmt = workList.remove(0);
    		if (!doneSet.add(curStmt))
    			continue;
    		
	    	for (Unit u : graph.getPredsOf(curStmt)) {
	    		if (u != unitDef) {
		    		if (u instanceof DefinitionStmt) {
		    			DefinitionStmt defStmt = (DefinitionStmt) u;
		    			if (defStmt.getLeftOp() == l)
		    				return true;
		    		}
		    		workList.add(u);
	    		}
	    	}
    	}
    	return false;
	}

}
