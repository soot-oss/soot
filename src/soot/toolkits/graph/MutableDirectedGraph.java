/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville, Raja Vallee-Rai, Patrick Lam
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
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.toolkits.graph;

import java.util.*;
import soot.*;



/**
 *   Defines a DirectedGraph which is modifiable. Provides
 *   an interface to add/delete nodes and edges.
 */

public interface MutableDirectedGraph extends DirectedGraph
{
    /**
     *  Adds an edge to the graph between 2 nodes.
     *  If the edge is already present no change is made.
     *  @param from  out node for the edge.
     *  @param to    in node for the edge.
     */
    public void addEdge(Object from, Object to);



    /**
     *  Removes an edge between 2 nodes in the graph.
     *  If the edge is not present no change is made.
     *  @param from  out node for the edge to remove.
     *  @param to    in node for the edge to remove.
     */
    public void removeEdge(Object from, Object to);


    /** @return true if the graph contains an edge the 2 nodes 
     *           false otherwise.
     */ 
    public boolean containsEdge(Object from, Object to);

    /** @return a list of the nodes that compose the graph. No ordering is implied.*/
    public List getNodes();

    /**
     *  Adds a node to the graph. Initially the added node has no successors or predecessors.
     *  ; as a consequence it is considered both a head and tail for the graph.
     *  @param node a node to add  to the graph.
     *  @see #getHeads
     *  @see #getTails
     */
    public void addNode(Object node);

    /**
     *  Removes a node from the graph. If the node is not
     *  found in the graph, no change is made.
     *  @param node the node to be removed.
     */
    public void removeNode(Object node);

    /**
     *   @param node node that we want to know if the graph constains.
     *   @return  true if the graph contains the node.
     *            false otherwise.
     */
    public boolean containsNode(Object node);
}

 





