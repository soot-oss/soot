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
import soot.util.*;
import java.util.*;
import soot.options.Options;


/**
 *  <p>
 *  Represents a CFG for a Body instance where the nodes are {@link
 *  Unit} instances, and where, in additional to unexceptional control
 *  flow edges, edges are added from every trapped {@link Unit} to the
 *  {@link Trap}'s handler <tt>Unit</tt>, regardless of whether the
 *  trapped <tt>Unit</tt>s may actually throw the exception caught by
 *  the <tt>Trap</tt>.</p>
 *
 *  <p>
 *  There are three distinctions between the exceptional edges added 
 *  in <tt>TrapUnitGraph</tt> and the exceptional edges added in
 *  {@link CompleteUnitGraph}:
 *  <ol>
 *  <li>
 *  In <tt>CompleteUnitGraph</tt>, the edges to <tt>Trap</tt>s are 
 *  associated with <tt>Unit</tt>s which may actually throw an 
 *  exception which the <tt>Trap</tt> catches. In <tt>TrapUnitGraph</tt>,
 *  there are edges from every trapped <tt>Unit</tt> to the <tt>Trap</tt>,
 *  regardless of whether it can throw an exception caught by the <tt>Trap</tt>
 *  </li>
 *  <li>
 *  In <tt>CompleteUnitGraph</tt>, when a <tt>Unit</tt> may throw
 *  an exception that is caught by a <tt>Trap</tt>, there are edges from	
 *  every predecessor of the excepting <tt>Unit</tt> to the <tt>Trap</tt>'s
 *  handler. In <tt>TrapUnitGraph</tt>, edges are not added from the 
 *  predecessors of excepting <tt>Unit</tt>s</li>.
 *  <li>
 *  In <tt>CompleteUnitGraph</tt>, when a <tt>Unit</tt> may throw an
 *  exception that is caught by a <tt>Trap</tt>, there is no edge from
 *  the excepting <tt>Unit</tt> itself to the <tt>Trap</tt> if
 *  the excepting <tt>Unit</tt> has no side effects. In
 *  <tt>TrapUnitGraph</tt>, there is always an edge from the excepting
 *  <tt>Unit</tt> to the <tt>Trap</tt>.</li>
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

	unitToSuccs = new HashMap(size * 2 + 1, 0.7f);
	unitToPreds = new HashMap(size * 2 + 1, 0.7f);
	buildUnexceptionalEdges(unitToSuccs, unitToPreds);
	buildExceptionalEdges(unitToSuccs, unitToPreds);
	makeMappedListsUnmodifiable(unitToSuccs);
	makeMappedListsUnmodifiable(unitToPreds);

	buildHeadsAndTails();

        if(Options.v().time())
            Timers.v().graphTimer.end();

	if (DEBUG)
	    soot.util.PhaseDumper.v().dumpGraph(this, body);
    }


    /**
     * Method to compute the edges corresponding to exceptional
     * control flow. 
     *
     * @param unitToSuccs A {@link Map} from {@link Unit}s to {@link
     *                    List}s of {@link Unit}s. This is * an ``out
     *                    parameter''; <tt>buildExceptionalEdges</tt>
     *                    will add a mapping for every <tt>Unit</tt>
     *                    within the scope of one or more {@link
     *                    Trap}s to a <tt>List</tt> of the handler
     *                    units of those <tt>Trap</tt>s.
     *
     * @param unitToPreds A {@link Map} from {@link Unit}s to 
     *                    {@link List}s of {@link Unit}s. This is an
     *                    ``out parameter'';
     *                    <tt>buildExceptionalEdges</tt> will add a
     *                    mapping for every {@link Trap} handler to
     *                    all the <tt>Unit</tt>s within the scope of
     *                    that <tt>Trap</tt>.
     */
    protected void buildExceptionalEdges(Map unitToSuccs, Map unitToPreds) {
	for (Iterator trapIt = body.getTraps().iterator(); 
	     trapIt.hasNext(); ) {
	    Trap trap = (Trap) trapIt.next();
	    Unit first = trap.getBeginUnit();
	    Unit last = (Unit) unitChain.getPredOf(trap.getEndUnit());
	    Unit catcher = trap.getHandlerUnit();
	    for (Iterator unitIt = unitChain.iterator(first, last);
		 unitIt.hasNext(); ) {
		Unit trapped = (Unit) unitIt.next();
		addEdge(unitToSuccs, unitToPreds, trapped, catcher);
	    }
	}
    }
}
