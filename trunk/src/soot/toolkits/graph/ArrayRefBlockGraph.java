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

import java.util.*;
import soot.*;
import soot.jimple.Stmt;
import soot.baf.Inst;


/**
 *  A CFG where the nodes are {@link Block} instances, and where 
 *  {@link Unit}s which include array references start new blocks.
 *  Exceptional control flow is ignored, so
 *  the graph will be a forest where each exception handler 
 *  constitutes a disjoint subgraph.
 */
public class ArrayRefBlockGraph extends BlockGraph 
{
    /**
     *   <p>Constructs an {@link ArrayRefBlockGraph} from the given
     *   {@link Body}.</p>
     *
     *   <p> Note that this constructor builds a {@link
     *   BriefUnitGraph} internally when splitting <tt>body</tt>'s
     *   {@link Unit}s into {@link Block}s.  Callers who need both a
     *   {@link BriefUnitGraph} and an {@link ArrayRefBlockGraph}
     *   should use the constructor taking the <tt>BriefUnitGraph</tt> as a
     *   parameter, as a minor optimization.</p>
     *
     *  @param the Body instance from which the graph is built.
     */
    public  ArrayRefBlockGraph(Body body)
    {
        this(new BriefUnitGraph(body));
    }


    /**
     *   Constructs an <tt>ArrayRefBlockGraph</tt> corresponding to the
     *   <tt>Unit</tt>-level control flow represented by the 
     *   passed {@link BriefUnitGraph}. 
     *
     *   @param unitGraph  The <tt>BriefUnitGraph</tt> for which
     *                 to build an <tt>ArrayRefBlockGraph</tt>.
     */
    public  ArrayRefBlockGraph(BriefUnitGraph unitGraph)
    {
        super(unitGraph);

	soot.util.PhaseDumper.v().dumpGraph(this, mBody);
    }

    
    /**
     * <p>Utility method for computing the basic block leaders for a
     * {@link Body}, given its {@link UnitGraph} (i.e., the
     * instructions which begin new basic blocks).</p>
     *
     * <p> This implementation chooses as block leaders all
     * the <tt>Unit</tt>s that {@link BlockGraph.computerLeaders()},
     * and adds:
     *
     * <ul>
     *
     * <li>All <tt>Unit</tt>s which contain an array reference, as 
     * defined by {@link Stmt.containsArrayRef()} and 
     * {@link Inst.containsArrayRef()}.
     *
     * <li>The first <tt>Unit</tt> not covered by each {@link Trap} (i.e.,
     * the <tt>Unit</tt> returned by {@link Trap.getLastUnit()}.</li>
     *
     * </ul></p>
     *
     * @param unitGraph is the <tt>Unit</tt>-level CFG which is to be split
     * into basic blocks.
     *
     * @return the {@link Set} of {@link Unit}s in <tt>unitGraph</tt> which
     * are block leaders.
     */
    protected Set<Unit> computeLeaders(UnitGraph unitGraph) {
	Body body = unitGraph.getBody();
	if (body != mBody) {
	    throw new RuntimeException("ArrayRefBlockGraph.computeLeaders() called with a UnitGraph that doesn't match its mBody.");
	}
        Set<Unit> leaders = super.computeLeaders(unitGraph);

	for (Iterator it = body.getUnits().iterator(); it.hasNext(); ) {
	    Unit unit = (Unit) it.next();
	    if (((unit instanceof Stmt) && ((Stmt) unit).containsArrayRef()) ||
		((unit instanceof Inst) && ((Inst) unit).containsArrayRef())) {
		leaders.add(unit);
	    }
	}
	return leaders;
    }
}


