/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 John Jorgensen
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.toolkits.exceptions;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Singletons;
import soot.Trap;
import soot.Unit;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraph.ExceptionDest;
import soot.util.Chain;

/**
 * A {@link BodyTransformer} that shrinks the protected area covered
 * by each {@link Trap} in the {@link Body} so that it begins at the first of
 * the {@link Body}'s {@link Unit}s which might throw an exception caught by
 * the {@link Trap} and ends just after the last {@link Unit} which might
 * throw an exception caught by the {@link Trap}. In the case where none
 * of the {@link Unit}s protected by a {@link Trap} can throw the exception
 * it catches, the {@link Trap}'s protected area is left completely empty, 
 * which will likely cause the {@link UnreachableCodeEliminator} to remove the 
 * {@link Trap} completely.
 *
 * The {@link TrapTightener} is used to reduce the risk of
 * unverifiable code which can result from the use of {@link
 * ExceptionalUnitGraph}s from which unrealizable exceptional
 * control flow edges have been removed.
 */

public final class TrapTightener extends BodyTransformer {

    public TrapTightener( Singletons.Global g ) {}
    public static TrapTightener v() { return soot.G.v().soot_toolkits_exceptions_TrapTightener(); }

    protected void internalTransform(Body body, String phaseName, Map options) {
        if(Options.v().verbose())
            G.v().out.println("[" + body.getMethod().getName() + "] Tightening trap boundaries...");

	Chain<Trap> trapChain = body.getTraps();
	Chain<Unit> unitChain = body.getUnits();
	if (trapChain.size() > 0) {
	    ExceptionalUnitGraph graph = new ExceptionalUnitGraph(body);

	    for (Iterator<Trap> trapIt = trapChain.iterator(); trapIt.hasNext(); ) {
		Trap trap = (Trap) trapIt.next();
		Unit firstTrappedUnit = trap.getBeginUnit();
		Unit firstTrappedThrower = null;
		Unit firstUntrappedUnit = trap.getEndUnit();
		Unit lastTrappedUnit = 
		    (Unit) unitChain.getPredOf(firstUntrappedUnit);
		Unit lastTrappedThrower = null;
		for (Unit u = firstTrappedUnit; 
		     u != null && u != firstUntrappedUnit; 
		     u = (Unit) unitChain.getSuccOf(u)) {
			if (mightThrowTo(graph, u, trap)) {
			    firstTrappedThrower = u;
			    break;
			}
		}
		if (firstTrappedThrower != null) {
		    for (Unit u = lastTrappedUnit;
			 u != null; u = (Unit) unitChain.getPredOf(u)) {
			if (mightThrowTo(graph, u, trap)) {
			    lastTrappedThrower = u;
			    break;
			}
		    }
		}
		// If no statement inside the trap can throw an exception, we remove the
		// complete trap.
		if (firstTrappedThrower == null)
			trapIt.remove();
		else {
			if (firstTrappedThrower != null &&
					firstTrappedUnit != firstTrappedThrower) {
			    trap.setBeginUnit(firstTrappedThrower);
			}
			if (lastTrappedThrower == null) {
			    lastTrappedThrower = firstTrappedUnit;
			}
			if (lastTrappedUnit != lastTrappedThrower) {
			    trap.setEndUnit((Unit) unitChain.getSuccOf(lastTrappedThrower));
			}
		}
	    }
	}
    }

    /**
     * A utility routine which determines if a particular {@link Unit} 
     * might throw an exception to a particular {@link Trap}, according to 
     * the information supplied by a particular control flow graph.
     *
     * @param g The control flow graph providing information about exceptions.
     * @param u The unit being inquired about.
     * @param t The trap being inquired about.
     * @return <tt>true</tt> if <tt>u</tt> might throw an exception caught
     * by <tt>t</tt>, according to <tt>g</tt.
     */
    protected boolean mightThrowTo(ExceptionalUnitGraph g, Unit u, Trap t) {
	for (ExceptionDest dest : g.getExceptionDests(u)) {
	    if (dest.getTrap() == t) {
		return true;
	    }
	}
	return false;
    }
}
