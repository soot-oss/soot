/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Feng Qian
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

package soot.jimple.toolkits.annotation.arraycheck;

import soot.toolkits.graph.*;
import java.util.*;

/**
   add skipNode method to direct all predecessor edges to successors.

   override 'addEdge' to add node if the node was not in the graph

*/
class ExtendedHashMutableDirectedGraph extends HashMutableDirectedGraph
{  
    public ExtendedHashMutableDirectedGraph() {}
    
    /**
       If nodes are not in the graph, add them into graph first.
     */
    public void addEdge (Object from, Object to)
    {
	if (!super.containsNode(from))
	    super.addNode(from);

	if (!super.containsNode(to))
	    super.addNode(to);

	super.addEdge(from, to);
    }


    /**
       Add mutual edge to the graph. It should be optimized in the future.
    */
    public void addMutualEdge (Object from, Object to)
    {
	if (!super.containsNode(from))
	    super.addNode(from);

	if (!super.containsNode(to))
	    super.addNode(to);

	super.addEdge(from, to);
	super.addEdge(to, from);
    }

    /** 
       Bypass the in edge to out edge. Not delete the node 
     */
    public void skipNode(Object node)
    {
	if (!super.containsNode(node))
	    return;

	Object[] preds = getPredsOf(node).toArray();
	Object[] succs = getSuccsOf(node).toArray();

	
	for (int i=0; i<preds.length; i++)
	{
	    for (int j=0; j<succs.length; j++)
	    {
		if (preds[i] != succs[j])
		    super.addEdge(preds[i], succs[j]);
	    }
	}

	for (int i=0; i<preds.length; i++)
	{
	    super.removeEdge(preds[i], node);
	}

	for (int j=0; j<succs.length; j++)
	{
	    super.removeEdge(node, succs[j]);
	}

	super.removeNode(node);
    }

    public void mergeWith(ExtendedHashMutableDirectedGraph other)
    {
	List nodes = other.getNodes();

	Iterator nodesIt = nodes.iterator();

	while (nodesIt.hasNext())
	{
	    Object node = nodesIt.next();

	    List succs = other.getSuccsOf(node);

	    Iterator succsIt = succs.iterator();

	    while (succsIt.hasNext())
	    {
		Object succ = succsIt.next();
		
		this.addEdge(node, succ);
	    }
	}
    }

    public String toString()
    {
	String rtn = "Graph:\n";

        List nodes = super.getNodes();

	Iterator nodesIt = nodes.iterator();

	while (nodesIt.hasNext())
        {
	    Object node = nodesIt.next();

	    List succs = super.getSuccsOf(node);

	    Iterator succsIt = succs.iterator();

	    while (succsIt.hasNext())
	    {
		Object succ = succsIt.next();

		rtn = rtn + node + "\t --- \t" + succ +"\n";
	    }
	}	 

	return rtn;
    }
}








