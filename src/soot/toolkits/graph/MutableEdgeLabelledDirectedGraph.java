/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Richard L. Halpert
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



/**
 *   Defines a DirectedGraph which is modifiable and associates
 *   a label object with every edge. Provides an interface to
 *   add/delete nodes and edges.
 */

public interface MutableEdgeLabelledDirectedGraph extends DirectedGraph
{
    /**
     *  Adds an edge to the graph between 2 nodes.
     *  If the edge is already present no change is made.
     *  @param from   out node for the edge.
     *  @param to     in node for the edge.
     *  @param label  label for the edge.
     */
    public void addEdge(Object from, Object to, Object label);


    /**
     *  Returns a list of labels for which an edge exists between from and to
     *  @param from   out node for the edges to remove.
     *  @param to     in node for the edges to remove.
     */
    public List<Object> getLabelsForEdges(Object from, Object to);


    /**
     *  Returns a MutableDirectedGraph consisting of
     *  all edges with the given label and their nodes.
     *  Nodes without edges are not included in the new graph.
     *  @param label  label for the edge to remove.
     */
	public MutableDirectedGraph getEdgesForLabel(Object label);


    /**
     *  Removes an edge between 2 nodes in the graph.
     *  If the edge is not present no change is made.
     *  @param from   out node for the edges to remove.
     *  @param to     in node for the edges to remove.
     *  @param label  label for the edge to remove.
     */
    public void removeEdge(Object from, Object to, Object label);
    
    
    /**
     *  Removes all edges between 2 nodes in the graph.
     *  If no edges are present, no change is made.
     *  @param from  out node for the edges to remove.
     *  @param to    in node for the edges to remove.
     */
    public void removeAllEdges(Object from, Object to);
    
    
    /**
     *  Removes all edges with the given label in the graph.
     *  If no edges are present, no change is made.
     *  @param label  label for the edge to remove.
     */
    public void removeAllEdges(Object label);


    /** @return true if the graph contains an edge between 
     *  the 2 nodes with the given label, otherwise return false.
     */ 
    public boolean containsEdge(Object from, Object to, Object label);
    
    
    /** @return true if the graph contains any edges between 
     *  the 2 nodes, otherwise return false.
     *  @param from  out node for the edges.
     *  @param to    in node for the edges.
     */ 
    public boolean containsAnyEdge(Object from, Object to);
    
    
    /** @return true if the graph contains any edges
     *  with the given label, otherwise return false.
     *  @param label  label for the edges.
     */ 
    public boolean containsAnyEdge(Object label);


    /** @return a list of the nodes that compose the graph. No ordering is implied.*/
    public List<Object> getNodes();


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

 





