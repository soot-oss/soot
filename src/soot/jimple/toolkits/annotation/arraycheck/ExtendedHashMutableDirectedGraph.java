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








