/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Navindra Umanee <navindra@cs.mcgill.ca>
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

import soot.*;
import soot.util.*;
import java.util.*;
import soot.jimple.*;
import soot.options.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;

/**
 * General interface for a dominators analysis.
 *
 * @author Navindra Umanee
 **/
public interface DominatorsFinder
{
    /**
     * Returns the graph to which the analysis pertains.
     **/
    public DirectedGraph getGraph();
    
    /**
     * Returns a list of dominators for the given node in the graph.
     **/
    public List getDominators(Object node);

    /**
     * Returns the immediate dominator of node or null if the node has
     * no immediate dominator.
     **/
    public Object getImmediateDominator(Object node);

    /**
     * True if "node" is dominated by "dominator" in the graph.
     **/
    public boolean isDominatedBy(Object node, Object dominator);

    /**
     * True if "node" is dominated by all nodes in "dominators" in the graph.
     **/
    public boolean isDominatedByAll(Object node, Collection dominators);
}
