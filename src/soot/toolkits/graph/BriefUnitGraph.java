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
 *  Represents a CFG where the nodes are Unit instances, and 
 *  where no edges are included to account for  control flow
 *  associated with exceptions.
 *
 *  @see Unit
 *  @see UnitGraph
 */
public class BriefUnitGraph extends UnitGraph
{
    /**
     *   Constructs a BriefUnitGraph given a Body instance.
     *   @param body The underlying body we want to make a 
     *               graph for.
     */
    public BriefUnitGraph(Body body)
    {
        super(body);
	int size = unitChain.size();

        if(Options.v().time())
            Timers.v().graphTimer.start();

	unitToSuccs = new HashMap(size * 2 + 1, 0.7f);
	unitToPreds = new HashMap(size * 2 + 1, 0.7f);
	buildUnexceptionalEdges(unitToSuccs, unitToPreds);
	makeMappedListsUnmodifiable(unitToSuccs);
	makeMappedListsUnmodifiable(unitToPreds);

	buildHeadsAndTails();

        if(Options.v().time())
            Timers.v().graphTimer.end();

	soot.util.PhaseDumper.v().dumpGraph(this, body);
    }
}




