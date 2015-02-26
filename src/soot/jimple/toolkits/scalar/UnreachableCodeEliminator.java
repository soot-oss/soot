/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Phong Co
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

/*
 * Modified by the Sable Research Group and others 1997-2003.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */



package soot.jimple.toolkits.scalar;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.PhaseOptions;
import soot.Scene;
import soot.Singletons;
import soot.Trap;
import soot.Unit;
import soot.options.Options;
import soot.toolkits.exceptions.PedanticThrowAnalysis;
import soot.toolkits.exceptions.ThrowAnalysis;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.Chain;


public class UnreachableCodeEliminator extends BodyTransformer
{
	protected ThrowAnalysis throwAnalysis = null;

	public UnreachableCodeEliminator( Singletons.Global g ) {}
	public static UnreachableCodeEliminator v() { return G.v().soot_jimple_toolkits_scalar_UnreachableCodeEliminator(); }

	public UnreachableCodeEliminator( ThrowAnalysis ta ) {
		this.throwAnalysis = ta;
	}

	protected void internalTransform(Body body, String phaseName, Map<String,String> options) 
	{		
		if (Options.v().verbose()) {
			G.v().out.println("[" + body.getMethod().getName() + "] Eliminating unreachable code...");
		}
		
		// Force a conservative ExceptionalUnitGraph() which
		// necessarily includes an edge from every trapped Unit to
		// its handler, so that we retain Traps in the case where
		// trapped units remain, but the default ThrowAnalysis
		// says that none of them can throw the caught exception.
		if (this.throwAnalysis == null)
			this.throwAnalysis = PhaseOptions.getBoolean(options, "remove-unreachable-traps", true)
				? Scene.v().getDefaultThrowAnalysis() : PedanticThrowAnalysis.v();
		ExceptionalUnitGraph graph =  new ExceptionalUnitGraph(body, throwAnalysis, false);

		Chain<Unit> units = body.getUnits();
		int numPruned = units.size();
		
		Set<Unit> reachable = units.isEmpty()
			? Collections.<Unit>emptySet()
			: reachable(units.getFirst(), graph)
			;
		
		// Now eliminate empty traps. (and unreachable handlers)
		//
		// For the most part, this is an atavism, an an artifact of
		// pre-ExceptionalUnitGraph code, when the only way for a trap to 
		// become unreachable was if all its trapped units were removed, and
		// the stmtIt loop did not remove Traps as it removed handler units.
		// We've left this separate test for empty traps here, even though 
		// most such traps would already have been eliminated by the preceding
		// loop, because in arbitrary bytecode you could have
		// handler unit that was still reachable by normal control flow, even
		// though it no longer trapped any units (though such code is unlikely
		// to occur in practice, and certainly no in code generated from Java
		// source.		
		for ( Iterator<Trap> it = body.getTraps().iterator(); it.hasNext(); ) {
			Trap trap = it.next();
			if ( (trap.getBeginUnit() == trap.getEndUnit()) || !reachable.contains(trap.getHandlerUnit()) ) {
				it.remove();
			}
		}
		
		// We must make sure that the end units of all traps which are still
		// alive are kept in the code
		for (Trap t : body.getTraps())
			if (t.getEndUnit() == body.getUnits().getLast())
				reachable.add(t.getEndUnit());
			
		units.retainAll(reachable);   
	  	
		numPruned -= units.size();
		
		if (Options.v().verbose()) {
			G.v().out.println("[" + body.getMethod().getName() + "]	 Removed " + numPruned + " statements...");
		}
	}
	
	// Used to be: "mark first statement and all its successors, recursively"
	// Bad idea! Some methods are extremely long. It broke because the recursion reached the
	// 3799th level.
	private <T> Set<T> reachable(T first, DirectedGraph<T> g) {
		if ( first == null || g == null ) {
			return Collections.<T>emptySet();
		}
		Set<T> visited = new HashSet<T>(g.size());
		Deque<T> q = new ArrayDeque<T>();
		q.addFirst(first);
		do {
			T t = q.removeFirst();
			if ( visited.add(t) ) {				
				q.addAll(g.getSuccsOf(t));
			}
		}
		while (!q.isEmpty());
		
		return visited;
	}
}
