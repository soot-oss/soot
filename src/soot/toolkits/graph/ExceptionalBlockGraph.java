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
import java.io.*;

/**
 *  <p>Represents a CFG where the nodes are {@link Block}s and the
 *  edges are derived from control flow.  Control flow associated with
 *  exceptions is taken into account: when a <tt>Unit</tt> may throw
 *  an exception that is caught by a {@link Trap} within the
 *  <tt>Body</tt>, the excepting <tt>Unit</tt> starts a new basic
 *  block.</p>
 */

public class ExceptionalBlockGraph extends BlockGraph 
{
    /**
     *   <p> Constructs a <tt>ExceptionalBlockGraph</tt> for the blocks
     *   found by partitioning the the units of the provided
     *   {@link Body} instance into basic blocks.</p>
     *
     *   <p> Note that this constructor builds a {@link
     *   ExceptionalUnitGraph} internally when splitting <tt>body</tt>'s
     *   {@link Unit}s into {@link Block}s.  Callers who already have
     *   a {@link ExceptionalUnitGraph} to hand can use the constructor
     *   taking a <tt>ExceptionalUnitGraph</tt> as a parameter, as a
     *   minor optimization.
     *
     *   @param body    The underlying body we want to make a graph for.
     */
    public ExceptionalBlockGraph(Body body)
    {
        super(new ExceptionalUnitGraph(body));
    }


    /**
     *   Constructs a graph for the blocks found by partitioning the
     *   the units in a {@link ExceptionalUnitGraph}.  
     *
     *   @param body    The underlying body we want to make a graph for.
     *
     *   @param unitGraph A {@link ExceptionalUnitGraph} built from <tt>body</tt>
     *                  The <tt>ExceptionalBlockGraph</tt> constructor uses
     *                  the passed <tt>graph</tt> to split the body into
     *			blocks. 
     */
    public ExceptionalBlockGraph(ExceptionalUnitGraph unitGraph)
    {
        super(unitGraph);

	if (DEBUG)
	    soot.util.PhaseDumper.v().dumpGraph(this, mBody);
    }
}


