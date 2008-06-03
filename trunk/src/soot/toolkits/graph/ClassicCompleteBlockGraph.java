/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 John Jorgensen
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


package soot.toolkits.graph;

import java.util.*;
import soot.*;

/**
 *  <p>Represents a CFG where the nodes are {@link Block}s and the
 *  edges are derived from control flow.  Control flow associated with
 *  exceptions is taken into account: when a <tt>Unit</tt> may throw
 *  an exception that is caught by a {@link Trap} within the
 *  <tt>Body</tt>, the excepting <tt>Unit</tt> starts a new basic
 *  block.</p>
 *
 *  <p> <tt>ClassicCompleteBlockGraph</tt> approximates the results
 *  that would have been produced by Soot's {@link CompleteBlockGraph}
 *  in releases up to Soot 2.1.0. It is included solely for testing
 *  purposes, and should not be used in actual analyses.  The approximation
 *  works not by duplicating the old {@link CompleteBlockGraph}'s logic,
 *  but by using {@link ClassicCompleteUnitGraph} as the basis for
 *  dividing {@link Unit}s into {@link Block}s.</p>
 *
 */

public class ClassicCompleteBlockGraph extends BlockGraph 
{
    /**
     *   <p> Constructs a <tt>ClassicCompleteBlockGraph</tt> for the blocks
     *   found by partitioning the the units of the provided
     *   {@link Body} instance into basic blocks.</p>
     *
     *   <p> Note that this constructor builds a {@link
     *   ClassicCompleteUnitGraph} internally when splitting <tt>body</tt>'s
     *   {@link Unit}s into {@link Block}s.  Callers who already have
     *   a {@link ClassicCompleteUnitGraph} to hand can use the constructor
     *   taking a <tt>ClassicCompleteUnitGraph</tt> as a parameter, as a
     *   minor optimization.
     *
     *   @param body    The underlying body we want to make a graph for.
     */
    public ClassicCompleteBlockGraph(Body body)
    {
        super(new ClassicCompleteUnitGraph(body));
    }


    /**
     *   Constructs a graph for the blocks found by partitioning the
     *   the units in a {@link ClassicCompleteUnitGraph}.  
     *
     *   @param unitGraph A {@link ClassicCompleteUnitGraph} built from 
     *                  <tt>body</tt>. The <tt>CompleteBlockGraph</tt> constructor uses
     *                  the passed <tt>graph</tt> to split the body into
     *			blocks. 
     */
    public ClassicCompleteBlockGraph(ClassicCompleteUnitGraph unitGraph)
    {
        super(unitGraph);
	// Adjust the heads and tails to match the old CompleteBlockGraph.
	Unit entryPoint = (Unit) (getBody().getUnits().getFirst());
	List newHeads = new ArrayList(1);
	for (Iterator blockIt = getBlocks().iterator(); blockIt.hasNext(); ) {
	    Block b = (Block) blockIt.next();
	    if (b.getHead() == entryPoint) {
		newHeads.add(b);
		break;
	    }
	}
	mHeads = Collections.unmodifiableList(newHeads);
	mTails = Collections.EMPTY_LIST;

	soot.util.PhaseDumper.v().dumpGraph(this, mBody);
    }
}


