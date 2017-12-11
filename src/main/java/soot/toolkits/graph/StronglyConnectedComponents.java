/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Raja Vallee-Rai
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
import soot.options.*;

import soot.*;
import soot.util.*;
import java.util.*;



/**
 *  Identifies and provides an interface to query the strongly-connected
 *  components of DirectedGraph instances.
 *  @see DirectedGraph
 *  @deprecated implementation is inefficient; use {@link StronglyConnectedComponentsFast} instead
 */

@Deprecated
public class StronglyConnectedComponents
{
    private HashMap<Object, Object> nodeToColor;
    private static final Object Visited=new Object();
    private static final Object Black=new Object();
    private final LinkedList<Object> finishingOrder;
    private List<List> componentList = new ArrayList<List>();
    private final HashMap<Object, List<Object>> nodeToComponent = new HashMap<Object, List<Object>>();
    MutableDirectedGraph sccGraph = new HashMutableDirectedGraph();
    private final int[] indexStack;
    private final Object[] nodeStack;
    private int last;
    
    /**
     *  @param g a graph for which we want to compute the strongly
     *           connected components. 
     *  @see DirectedGraph
     */
    public StronglyConnectedComponents(DirectedGraph g)
    {
	nodeToColor = new HashMap<Object, Object>((3*g.size())/2,0.7f);
	indexStack = new int[g.size()];
	nodeStack = new Object[g.size()];        
	finishingOrder = new LinkedList<Object>();
	
        // Visit each node
        {
            Iterator nodeIt = g.iterator();
            
            while(nodeIt.hasNext())
            {
                Object s = nodeIt.next();
               
                if(nodeToColor.get(s) == null)
                    visitNode(g, s); 
            }
        }

	
        // Re-color all nodes white
        nodeToColor = new HashMap<Object, Object>((3*g.size()),0.7f);

        // Visit each node via transpose edges
        {
            Iterator<Object> revNodeIt = finishingOrder.iterator();
            while (revNodeIt.hasNext())
            {
                Object s = revNodeIt.next();

                if(nodeToColor.get(s) == null)
                {
                    List<Object> currentComponent = null;

		    currentComponent = new StationaryArrayList();
		    nodeToComponent.put(s, currentComponent);
		    sccGraph.addNode(currentComponent);
		    componentList.add(currentComponent);

                    visitRevNode(g, s, currentComponent); 
                }
            }
        }
        componentList = Collections.unmodifiableList(componentList);

        if (Options.v().verbose()) 
        {
            G.v().out.println("Done computing scc components");
            G.v().out.println("number of nodes in underlying graph: "+g.size());
            G.v().out.println("number of components: "+sccGraph.size());
        }
    }

    private void visitNode(DirectedGraph graph, Object startNode)
    {
        last=0;
        nodeToColor.put(startNode, Visited);
        
        nodeStack[last]=startNode;
        indexStack[last++]= -1;

	while(last>0)
        {
	    int toVisitIndex = ++indexStack[last-1];
            Object toVisitNode = nodeStack[last-1];
           
	    if(toVisitIndex >= graph.getSuccsOf(toVisitNode).size())
            {
                // Visit this node now that we ran out of children 
                    finishingOrder.addFirst(toVisitNode);

                // Pop this node off
		    last--;
            }
            else
            {
                Object childNode = graph.getSuccsOf(toVisitNode).get(toVisitIndex);
                
                // Visit this child next if not already visited (or on stack)
                    if(nodeToColor.get(childNode) == null)
                    {
                        nodeToColor.put(childNode, Visited);
                        
			nodeStack[last]=childNode;
			indexStack[last++]=-1;
                    }
            }
        }
    }

    private void visitRevNode(DirectedGraph graph, Object startNode, List<Object> currentComponent)
    {
	last=0;
        
        nodeToColor.put(startNode, Visited);
        
	nodeStack[last]=startNode;
        indexStack[last++]= -1;

	while(last>0)
        {
	    int toVisitIndex = ++indexStack[last-1];
            Object toVisitNode = nodeStack[last-1];
            
            if(toVisitIndex >= graph.getPredsOf(toVisitNode).size())
            {
                // No more nodes.  Add toVisitNode to current component.
		currentComponent.add(toVisitNode);
		nodeToComponent.put(toVisitNode, currentComponent);
		nodeToColor.put(toVisitNode, Black);
                // Pop this node off
		last--;
            }
            else
            {
                Object childNode = graph.getPredsOf(toVisitNode).get(toVisitIndex);
                
                // Visit this child next if not already visited (or on stack)
		if(nodeToColor.get(childNode) == null)
                {
		    nodeToColor.put(childNode, Visited);
		    
		    nodeStack[last]=childNode;
		    indexStack[last++]=-1;
		}

		else if (nodeToColor.get(childNode) == Black)
		{
		    /* we may be visiting a node in another component.  if so, add edge to sccGraph. */
		    if (nodeToComponent.get(childNode) != currentComponent)
			sccGraph.addEdge(nodeToComponent.get(childNode), currentComponent);
		}
            }
        }
    }


    /** 
     *  Checks if 2 nodes are in the same strongly-connnected component.
     *  @param a some graph node.
     *  @param b some graph node
     *  @return true if both nodes are in the same strongly-connnected component.
     *          false otherwise.
     */
    public boolean equivalent(Object a, Object b)
    {
        return nodeToComponent.get(a) == nodeToComponent.get(b);
    }


    /**
     *   @return a list of the strongly-connnected components that make
     *           up the computed strongly-connnect component graph.
     */
    public List<List> getComponents()
    {
        return componentList;
    }

    /**
     *  @param a a node of the original graph.
     *  @return the strongly-connnected component node
     *          to which the parameter node belongs.
     */
    public List getComponentOf(Object a)
    {
        return nodeToComponent.get(a);
    }

    /** 
     *  @return the computed strongly-connnected component graph. 
     *  @see DirectedGraph
     */
    public DirectedGraph getSuperGraph()
    {
        /* we should make this unmodifiable at some point. */
        return sccGraph;
    }
}
