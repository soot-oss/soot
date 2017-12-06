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
 *   HashMap based implementation of a MutableBlockGraph.
 */

public class HashMutableDirectedGraph<N> implements MutableDirectedGraph<N> {		

    protected Map<N,Set<N>> nodeToPreds;
    protected Map<N,Set<N>> nodeToSuccs;

    protected Set<N> heads;
    protected Set<N> tails;
    
    private static <T> List<T> getCopy(Collection<? extends T> c) {
        return Collections.unmodifiableList(new ArrayList<T>(c));    	
    }
    
    public HashMutableDirectedGraph()
    {
        nodeToPreds = new HashMap<N,Set<N>>();
        nodeToSuccs = new HashMap<N,Set<N>>();
        heads = new HashSet<N>();
        tails = new HashSet<N>();
    }

    /** Removes all nodes and edges. */
    public void clearAll() {
        nodeToPreds.clear();
        nodeToSuccs.clear();
        heads.clear();
        tails.clear();
    }

    public Object clone() {
        HashMutableDirectedGraph<N> g = new HashMutableDirectedGraph<N>();
        g.nodeToPreds.putAll(nodeToPreds);
        g.nodeToSuccs.putAll(nodeToSuccs);
        g.heads.addAll(heads);
        g.tails.addAll(tails);
        return g;
    }

    /* Returns an unbacked list of heads for this graph. */
    @Override
    public List<N> getHeads()
    {
        return getCopy(heads);
    }

    /* Returns an unbacked list of tails for this graph. */
    @Override
    public List<N> getTails()
    {
        return getCopy(tails);
    }
    
    @Override
    public List<N> getPredsOf(N s)
    {
        Set<N> preds = nodeToPreds.get(s);
        if (preds != null)
            return getCopy(preds);
       
        throw new RuntimeException(s+"not in graph!");
    }
    
    /**
     * Same as {@link #getPredsOf(Object)} but returns a set.
     * This is faster than calling {@link #getPredsOf(Object)}.
     * Also, certain operations like {@link Collection#contains(Object)}
     * execute faster on the set than on the list.
     * The returned set is unmodifiable. 
     */
    public Set<N> getPredsOfAsSet(N s)
    {
        Set<N> preds = nodeToPreds.get(s);
        if (preds != null)
            return Collections.unmodifiableSet(preds);
        
        throw new RuntimeException(s+"not in graph!");
    }

    @Override
    public List<N> getSuccsOf(N s)
    {
        Set<N> succs = nodeToSuccs.get(s);
        if (succs != null)
        	return getCopy(succs);
        
        throw new RuntimeException(s+"not in graph!");
    }
    
    /**
     * Same as {@link #getSuccsOf(Object)} but returns a set.
     * This is faster than calling {@link #getSuccsOf(Object)}.
     * Also, certain operations like {@link Collection#contains(Object)}
     * execute faster on the set than on the list.
     * The returned set is unmodifiable. 
     */
    public Set<N> getSuccsOfAsSet(N s)
    {
        Set<N> succs = nodeToSuccs.get(s);
        if (succs != null)
            return Collections.unmodifiableSet(succs);
        
        throw new RuntimeException(s+"not in graph!");
    }

    @Override
    public int size()
    {
        return nodeToPreds.keySet().size();
    }

    @Override
    public Iterator<N> iterator()
    {
        return nodeToPreds.keySet().iterator();
    }
    
    @Override
    public void addEdge(N from, N to)
    {
        if (from == null || to == null)
						throw new RuntimeException("edge from or to null");

        if (containsEdge(from, to))
            return;

        Set<N> succsList = nodeToSuccs.get(from);
        if (succsList == null)
            throw new RuntimeException(from + " not in graph!");

        Set<N> predsList = nodeToPreds.get(to);
        if (predsList == null)
            throw new RuntimeException(to + " not in graph!");

        heads.remove(to);
        tails.remove(from);

        succsList.add(to);
        predsList.add(from);
    }

    @Override
    public void removeEdge(N from, N to)
    {
        if (!containsEdge(from, to))
            return;

        Set<N> succs = nodeToSuccs.get(from);
        if (succs == null)
            throw new RuntimeException(from + " not in graph!");

        Set<N> preds = nodeToPreds.get(to);
        if (preds == null)
            throw new RuntimeException(to + " not in graph!");

        succs.remove(to);
        preds.remove(from);

        if (succs.isEmpty())
            tails.add(from);

        if (preds.isEmpty())
            heads.add(to);
    }

    @Override
    public boolean containsEdge(N from, N to)
    {
    		Set<N> succs = nodeToSuccs.get(from);
				if (succs == null)
						return false;
        return succs.contains(to);
    }

    @Override
    public boolean containsNode(Object node)
    {
        return nodeToPreds.keySet().contains(node);
    }
    
    @Override
    public List<N> getNodes()
    {
        return getCopy(nodeToPreds.keySet());
    }
    
    @Override
    public void addNode(N node)
    {
		if (containsNode(node))
			throw new RuntimeException("Node already in graph");
				
		nodeToSuccs.put(node, new LinkedHashSet<N>());
		nodeToPreds.put(node, new LinkedHashSet<N>());
		heads.add(node); 
		tails.add(node);
    }
    
    @Override
    public void removeNode(N node)
    {
    	for (N n : new ArrayList<N>(nodeToSuccs.get(node))) {
    		removeEdge(node, n);
    	}
    	nodeToSuccs.remove(node);
    	
    	for (N n : new ArrayList<N>(nodeToPreds.get(node))) {
    		removeEdge(n, node);
    	}
    	nodeToPreds.remove(node);
    	
        heads.remove(node); 
        tails.remove(node);
    }

    public void printGraph() {
    	for (N node : this ) {
		    G.v().out.println("Node = "+node);
		    G.v().out.println("Preds:");
		    for (N p : getPredsOf(node)) {
				G.v().out.print("     ");
				G.v().out.println(p);
		    }
		    G.v().out.println("Succs:");
		    for (N s : getSuccsOf(node)) {
				G.v().out.print("     ");
				G.v().out.println(s);
		    }
		}
    }

}

 
