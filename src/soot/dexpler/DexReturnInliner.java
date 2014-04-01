/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.dexpler;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.Local;
import soot.Trap;
import soot.Unit;
import soot.UnitBox;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.GotoStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.toolkits.scalar.SmartLocalDefs;

/**
 * BodyTransformer to inline jumps to return statements. Take the following code:
 * 		a = b
 * 		goto label1
 * 
 * 		label1:
 * 			return a
 * We inline this to produce
 * 		a = b
 * 		return b
 *
 * @author Steven Arzt
 */
public class DexReturnInliner extends DexTransformer {

    public static DexReturnInliner v() {
        return new DexReturnInliner();
    }

    @Override
	protected void internalTransform(final Body body, String phaseName, @SuppressWarnings("rawtypes") Map options) {
		Iterator<Unit> it = body.getUnits().snapshotIterator();
		while (it.hasNext()) {
			Unit u = it.next();
			if (u instanceof GotoStmt) {
				GotoStmt gtStmt = (GotoStmt) u;
				if (gtStmt.getTarget() instanceof ReturnStmt) {
					Stmt stmt = (Stmt) gtStmt.getTarget().clone();
					for (Trap t : body.getTraps())
						for (UnitBox ubox : t.getUnitBoxes())
							if (ubox.getUnit() == u)
								ubox.setUnit(stmt);
					while (!u.getBoxesPointingToThis().isEmpty())
						u.getBoxesPointingToThis().get(0).setUnit(stmt);
					body.getUnits().swapWith(u, stmt);
				}
			}
		}
		
        ExceptionalUnitGraph graph = new ExceptionalUnitGraph(body);
        LocalDefs localDefs = new SmartLocalDefs(graph, new SimpleLiveLocals(graph));
        
		// If a return statement's operand has only one definition and this is
		// a copy statement, we take the original operand
		for (Unit u : body.getUnits())
			if (u instanceof ReturnStmt) {
				ReturnStmt retStmt = (ReturnStmt) u;
				if (retStmt.getOp() instanceof Local) {
					List<Unit> defs = localDefs.getDefsOfAt((Local) retStmt.getOp(), retStmt);
					if (defs.size() == 1 && defs.get(0) instanceof AssignStmt) {
						AssignStmt assign = (AssignStmt) defs.get(0);
						if (assign.getRightOp() instanceof Local
								|| assign.getRightOp() instanceof Constant)
							retStmt.setOp(assign.getRightOp());
					}
				}
			}
    }

}

