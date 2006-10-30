/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

package soot.jbco.util;

import java.util.*;

import soot.Body;
import soot.Trap;
import soot.Unit;
import soot.toolkits.graph.TrapUnitGraph;

/**
 * @author Michael Batchelder 
 * 
 * Created on 15-Jun-2006 
 */
public class SimpleExceptionalGraph extends TrapUnitGraph {

  /**
   * @param body
   */
  public SimpleExceptionalGraph(Body body) {
    super(body);
	int size = unitChain.size();

	unitToSuccs = new HashMap(size * 2 + 1, 0.7f);
	unitToPreds = new HashMap(size * 2 + 1, 0.7f);
	buildUnexceptionalEdges(unitToSuccs, unitToPreds);
	buildSimpleExceptionalEdges(unitToSuccs, unitToPreds);
	
	makeMappedListsUnmodifiable(unitToSuccs);
	makeMappedListsUnmodifiable(unitToPreds);
	buildHeadsAndTails();
  }

  protected void buildSimpleExceptionalEdges(Map unitToSuccs, Map unitToPreds) {
	for (Iterator trapIt = body.getTraps().iterator(); 
	     	trapIt.hasNext(); ) {
	    Trap trap = (Trap) trapIt.next();

	    Unit handler = trap.getHandlerUnit();
	    for (Iterator predIt = ((List)unitToPreds.get(trap.getBeginUnit())).iterator();
	    	 	predIt.hasNext();) {
	      Unit pred = (Unit)predIt.next();
	      addEdge(unitToSuccs, unitToPreds, pred, handler);
	    }
	}
  }
}
