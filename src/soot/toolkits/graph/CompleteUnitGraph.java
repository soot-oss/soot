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
 * Modified by the Sable Research Group and others 1997-2004.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.toolkits.graph;

import soot.Body;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.exceptions.PedanticThrowAnalysis;

/**
 *  <p>Represents a CFG for a {@link Body} instance where the nodes
 *  are {@link Unit} instances, and where control flow associated with
 *  exceptions is taken into account. In a
 *  <code>CompleteUnitGraph</code>, every {@link Unit} covered by a
 *  {@link Trap} is considered to have the potential to throw an
 *  exception caught by the {@link Trap}, so there are edges to the
 *  {@link Trap}'s handler from every trapped {@link Unit} , as well
 *  as from all the predecessors of the trapped {@link Unit}s.
 *
 *  <p>This implementation of <code>CompleteUnitGraph</code> is included
 *  for backwards compatibility, but the graphs it produces are not
 *  necessarily identical to the graphs produced by the implementation of
 *  <code>CompleteUnitGraph</code> provided by versions of Soot
 *  up to and including release 2.1.0.  The known differences include:
 *
 *  <ul>
 * 
 *  <li>If a {@link Body} includes {@link Unit}s which branch into the
 *  middle of the region protected by a {@link Trap} this 
 *  implementation of <code>CompleteUnitGraph</code> will include
 *  edges from those branching {@link Unit}s to the {@link Trap}'s
 *  handler (since the branches are predecessors of an instruction which may
 *  throw an exception caught by the {@link Trap}.  The 2.1.0
 *  implementation of {@link CompleteUnitGraph} mistakenly omitted
 *  these edges.
 *
 *  </ul></p>
 */
public class CompleteUnitGraph extends ExceptionalUnitGraph 
{
    public CompleteUnitGraph(Body b) {
	super(b, PedanticThrowAnalysis.v(), false);
    }
}
