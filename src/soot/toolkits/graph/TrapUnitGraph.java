/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville, Raja Vallee-Rai
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


package soot.toolkits.graph;

import soot.*;

import java.util.*;

import soot.options.Options;


/**
 *  <p>
 *  Represents a CFG for a {@link Body} instance where the nodes are
 *  {@link Unit} instances, and where, in additional to unexceptional
 *  control flow edges, edges are added from every trapped {@link
 *  Unit} to the {@link Trap}'s handler <code>Unit</code>, regardless
 *  of whether the trapped <code>Unit</code>s may actually throw the
 *  exception caught by the <code>Trap</code>.</p>
 *
 *  <p>
 *  There are three distinctions between the exceptional edges added 
 *  in <code>TrapUnitGraph</code> and the exceptional edges added in
 *  {@link ExceptionalUnitGraph}:
 *  <ol>
 *  <li>
 *  In <code>ExceptionalUnitGraph</code>, the edges to
 *  <code>Trap</code>s are associated with <code>Unit</code>s which
 *  may actually throw an exception which the <code>Trap</code>
 *  catches (according to the {@link
 *  soot.toolkits.exceptions.ThrowAnalysis ThrowAnalysis} used in the
 *  construction of the graph). In <code>TrapUnitGraph</code>, there
 *  are edges from every trapped <code>Unit</code> to the
 *  <code>Trap</code>, regardless of whether it can throw an exception
 *  caught by the <code>Trap</code>.
 *  </li>
 *  <li>
 *  In <code>ExceptionalUnitGraph</code>, when a <code>Unit</code> may
 *  throw an exception that is caught by a <code>Trap</code> there
 *  are edges from every predecessor of the excepting
 *  <code>Unit</code> to the <code>Trap</code>'s handler. In
 *  <code>TrapUnitGraph</code>, edges are not added from the
 *  predecessors of excepting <code>Unit</code>s.</li>
 *  <li>
 *  In <code>ExceptionalUnitGraph</code>, when a <code>Unit</code> may
 *  throw an exception that is caught by a <code>Trap</code>, there
 *  may be no edge from the excepting <code>Unit</code> itself to the
 *  <code>Trap</code> (depending on the possibility of side effects
 *  and the setting of the <code>omitExceptingUnitEdges</code>
 *  parameter). In <code>TrapUnitGraph</code>, there is always an edge
 *  from the excepting <code>Unit</code> to the
 *  <code>Trap</code>.</li>
 *  </ol>
 */
public class TrapUnitGraph extends UnitGraph
{
    /**
     *  Constructs the graph from a given Body instance.
     *  @param the Body instance from which the graph is built.
     */
    public TrapUnitGraph(Body body)
    {
        super(body);
	int size = unitChain.size();

        if(Options.v().time())
            Timers.v().graphTimer.start();

	unitToSuccs = new HashMap<Unit, List<Unit>>(size * 2 + 1, 0.7f);
	unitToPreds = new HashMap<Unit, List<Unit>>(size * 2 + 1, 0.7f);
	buildUnexceptionalEdges(unitToSuccs, unitToPreds);
	buildExceptionalEdges(unitToSuccs, unitToPreds);

	buildHeadsAndTails();

        if(Options.v().time())
            Timers.v().graphTimer.end();

	soot.util.PhaseDumper.v().dumpGraph(this, body);
    }


    /**
     * Method to compute the edges corresponding to exceptional
     * control flow. 
     *
     * @param unitToSuccs A <code>Map</code> from {@link Unit}s to {@link
     *                    List}s of <code>Unit</code>s. This is an &ldquo;out
     *                    parameter&rdquo;; <code>buildExceptionalEdges</code>
     *                    will add a mapping for every <code>Unit</code>
     *                    within the scope of one or more {@link
     *                    Trap}s to a <code>List</code> of the handler
     *                    units of those <code>Trap</code>s.
     *
     * @param unitToPreds A <code>Map</code> from <code>Unit</code>s to 
     *                    <code>List</code>s of <code>Unit</code>s. This is an
     *                    &ldquo;out parameter&rdquo;;
     *                    <code>buildExceptionalEdges</code> will add a
     *                    mapping for every <code>Trap</code> handler to
     *                    all the <code>Unit</code>s within the scope of
     *                    that <code>Trap</code>.
     */
    protected void buildExceptionalEdges(Map<Unit, List<Unit>> unitToSuccs, Map<Unit, List<Unit>> unitToPreds) {
    	for (Trap trap : body.getTraps()) {
		    Unit first = trap.getBeginUnit();
		    Unit last =  unitChain.getPredOf(trap.getEndUnit());
		    Unit catcher = trap.getHandlerUnit();	
		    
		    for (Iterator<Unit> unitIt = unitChain.iterator(first, last); unitIt.hasNext(); ) {
		    	Unit trapped = unitIt.next();
		    	addEdge(unitToSuccs, unitToPreds, trapped, catcher);
		    }
    	}
    }
}
