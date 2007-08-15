/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville, Raja Vallee-Rai, Patrick Lam
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

import soot.*;
import soot.util.*;




/**
 *   HashMap based implementation of a MutableEdgeLabelledDirectedGraph.
 */
class DGEdge
{
	Object from;
	Object to;
	
	public DGEdge(Object from, Object to)
	{
		this.from = from;
		this.to = to;
	}
	
	public Object from()
	{
		return from;
	}
	
	public Object to()
	{
		return to;
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof DGEdge)
		{
			DGEdge other = (DGEdge) o;
			return from.equals(other.from) && to.equals(other.to);
		}
		return false;
	}
	
	public int hashCode()
	{
		return from.hashCode() + to.hashCode();
	}
}

public class HashMutableEdgeLabelledDirectedGraph implements MutableEdgeLabelledDirectedGraph
{
    protected HashMap<Object,ArrayList> nodeToPreds = new HashMap();
    protected HashMap<Object,ArrayList> nodeToSuccs = new HashMap();
    
    protected HashMap<DGEdge,ArrayList<Object>> edgeToLabels = new HashMap();
    protected HashMap<Object,ArrayList<DGEdge>> labelToEdges = new HashMap();

    protected Chain heads = new HashChain();
    protected Chain tails = new HashChain();

    public HashMutableEdgeLabelledDirectedGraph()
    {
    }

    /** Removes all nodes and edges. */
    public void clearAll() {
        nodeToPreds = new HashMap();
        nodeToSuccs = new HashMap();
        edgeToLabels = new HashMap();
        labelToEdges = new HashMap();
        heads = new HashChain();
        tails = new HashChain();
    }

    public Object clone() {
        HashMutableEdgeLabelledDirectedGraph g = new HashMutableEdgeLabelledDirectedGraph();
        g.nodeToPreds = (HashMap)nodeToPreds.clone();
        g.nodeToSuccs = (HashMap)nodeToSuccs.clone();
        g.edgeToLabels = (HashMap)edgeToLabels.clone();
        g.labelToEdges = (HashMap)labelToEdges.clone();
        g.heads = HashChain.listToHashChain(HashChain.toList(heads));
        g.tails = HashChain.listToHashChain(HashChain.toList(tails));
        return g;
    }

    /* Returns an unbacked list of heads for this graph. */
    public List getHeads()
    {
        ArrayList l = new ArrayList(); l.addAll(heads);
        return Collections.unmodifiableList(l);
    }

    /* Returns an unbacked list of tails for this graph. */
    public List getTails()
    {
        ArrayList l = new ArrayList(); l.addAll(tails);
        return Collections.unmodifiableList(l);
    }

    public List getPredsOf(Object s)
    {
        List l = nodeToPreds.get(s);
        if (l != null)
            return Collections.unmodifiableList(l);
        else
            throw new RuntimeException(s+"not in graph!");
    }

    public List getSuccsOf(Object s)
    {
        List l = nodeToSuccs.get(s);
        if (l != null)
            return Collections.unmodifiableList(l);
        else
            throw new RuntimeException(s+"not in graph!");
    }

    public int size()
    {
        return nodeToPreds.keySet().size();
    }

    public Iterator iterator()
    {
        return nodeToPreds.keySet().iterator();
    }

    public void addEdge(Object from, Object to, Object label)
    {
        if (from == null || to == null)
						throw new RuntimeException("edge from or to null");
						
		if (label == null)
						throw new RuntimeException("edge with null label");

        if (containsEdge(from, to, label))
            return;

        List<Object> succsList = nodeToSuccs.get(from);
        if (succsList == null)
            throw new RuntimeException(from + " not in graph!");

        List<Object> predsList = nodeToPreds.get(to);
        if (predsList == null)
            throw new RuntimeException(to + " not in graph!");

        if (heads.contains(to))
            heads.remove(to);

        if (tails.contains(from))
            tails.remove(from);
		
		if(!succsList.contains(to))
	        succsList.add(to);
	    if(!predsList.contains(from))
	        predsList.add(from);
	    
	    DGEdge edge = new DGEdge(from, to);
		if(!edgeToLabels.containsKey(edge))
			edgeToLabels.put(edge, new ArrayList());
	    List<Object> labels = edgeToLabels.get(edge);
	    
	    if(!labelToEdges.containsKey(label))
	    	labelToEdges.put(label, new ArrayList());
	    List<DGEdge> edges = labelToEdges.get(label);
		
//		if(!labels.contains(label))
			labels.add(label);
//		if(!edges.contains(edge))
			edges.add(edge);
    }

	public List<Object> getLabelsForEdges(Object from, Object to)
	{
		DGEdge edge = new DGEdge(from, to);
		return edgeToLabels.get(edge);
	}
	
	public MutableDirectedGraph getEdgesForLabel(Object label)
	{
		List<DGEdge> edges = labelToEdges.get(label);
		MutableDirectedGraph ret = new HashMutableDirectedGraph();
		if(edges == null)
			return ret;
		for(DGEdge edge : edges)
		{
			if(!ret.containsNode(edge.from()))
				ret.addNode(edge.from());
			if(!ret.containsNode(edge.to()))
				ret.addNode(edge.to());
			ret.addEdge(edge.from(), edge.to());
		}
		return ret;
	}

    public void removeEdge(Object from, Object to, Object label)
    {
        if (!containsEdge(from, to, label))
            return;
            
        DGEdge edge = new DGEdge(from, to);
        List labels = edgeToLabels.get(edge);
        if( labels == null )
        	throw new RuntimeException("edge " + edge + " not in graph!");
        
        List edges = labelToEdges.get(label);
        if( edges == null )
        	throw new RuntimeException("label " + label + " not in graph!");

		labels.remove(label);
		edges.remove(edge);
		
		// if this edge has no more labels, then it's gone!
		if(labels.isEmpty())
		{
			edgeToLabels.remove(edge);

	        List succsList = nodeToSuccs.get(from);
	        if (succsList == null)
	            throw new RuntimeException(from + " not in graph!");

	        List predsList = nodeToPreds.get(to);
	        if (predsList == null)
	            throw new RuntimeException(to + " not in graph!");
			
	        succsList.remove(to);
	        predsList.remove(from);

	        if (succsList.isEmpty())
	            tails.add(from);

	        if (predsList.isEmpty())
	            heads.add(to);
	    }
	    
	    // if this label has no more edges, then who cares?
	    if(edges.isEmpty())
	    	labelToEdges.remove(label);
    }

    public void removeAllEdges(Object from, Object to)
	{
        if (!containsAnyEdge(from, to))
            return;
            
        DGEdge edge = new DGEdge(from, to);
        List labels = edgeToLabels.get(edge);
        if( labels == null )
        	throw new RuntimeException("edge " + edge + " not in graph!");
        
        for(Object label : labels)
        {        
	        removeEdge(from, to, label);
	    }
	}

    public void removeAllEdges(Object label)
    {
		if( !containsAnyEdge(label) )
			return;
		
		List<DGEdge> edges = labelToEdges.get(label);
		if( edges == null )
			throw new RuntimeException("label " + label + " not in graph!");
			
		for(DGEdge edge : edges)
		{
			removeEdge(edge.from(), edge.to(), label);
		}
	}

    public boolean containsEdge(Object from, Object to, Object label)
    {
		DGEdge edge = new DGEdge(from, to);
		if(edgeToLabels.get(edge) != null && edgeToLabels.get(edge).contains(label))
			return true;
		return false;
    }

    public boolean containsAnyEdge(Object from, Object to)
    {
		DGEdge edge = new DGEdge(from, to);
		if(edgeToLabels.get(edge) != null && edgeToLabels.get(edge).isEmpty())
			return false;
		return true;
    }

    public boolean containsAnyEdge(Object label)
    {
		if(labelToEdges.get(label) != null && labelToEdges.get(label).isEmpty())
			return false;
		return true;
    }

    public boolean containsNode(Object node)
    {
        return nodeToPreds.keySet().contains(node);
    }

    public List<Object> getNodes()
    {
        return Arrays.asList(nodeToPreds.keySet().toArray());
    }

    public void addNode(Object node)
    {
				if (containsNode(node))
						throw new RuntimeException("Node already in graph");
				
				nodeToSuccs.put(node, new ArrayList());
        nodeToPreds.put(node, new ArrayList());
        heads.add(node); 
				tails.add(node);
    }

    public void removeNode(Object node)
    {
        List succs = (List)nodeToSuccs.get(node).clone();
        for (Iterator succsIt = succs.iterator(); succsIt.hasNext(); )
            removeAllEdges(node, succsIt.next());
        nodeToSuccs.remove(node);

        List preds = (List)nodeToPreds.get(node).clone();
        for (Iterator predsIt = preds.iterator(); predsIt.hasNext(); )
            removeAllEdges(predsIt.next(), node);
        nodeToPreds.remove(node);

        if (heads.contains(node))
            heads.remove(node); 

        if (tails.contains(node))
            tails.remove(node);
    }

    public void printGraph()
    {

		for (Iterator it = iterator(); it.hasNext(); )
		{
		    Object node = it.next();
		    G.v().out.println("Node = "+node);
		    G.v().out.println("Preds:");
		    for (Iterator predsIt = getPredsOf(node).iterator(); predsIt.hasNext(); )
		    {
		    	Object pred = predsIt.next();
		        DGEdge edge = new DGEdge(pred, node);
		        List labels = edgeToLabels.get(edge);
				G.v().out.println("     " + pred + " [" + labels + "]");
		    }
		    G.v().out.println("Succs:");
		    for (Iterator succsIt = getSuccsOf(node).iterator(); succsIt.hasNext(); )
		    {
		    	Object succ = succsIt.next();
		        DGEdge edge = new DGEdge(node, succ);
		        List labels = edgeToLabels.get(edge);
				G.v().out.println("     " + succ + " [" + labels + "]");
		    }
		}
    }

}

 
