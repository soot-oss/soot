/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
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

import soot.*;
import soot.util.*;
import java.util.*;



/**
 *  Identifies and provides an interface to query the strongly-connected
 *  components of DirectedGraph instances.
 *  @see DirectedGraph
 */

public class StronglyConnectedComponents
{
    private HashMap nodeToColor = new HashMap();
    private static final int 
        WHITE = 0,
        GRAY = 1,
        BLACK = 2;
    Chain finishingOrder = new ArrayChain();

    private List componentList = new ArrayList();
    private HashMap nodeToComponent = new HashMap();
    MutableDirectedGraph sccGraph = new HashMutableDirectedGraph();

    /**
     *  @param g a graph for which we want to compute the strongly
     *           connected components. 
     *  @see DirectedGraph
     */
    public StronglyConnectedComponents(DirectedGraph g)
    {
        // Color all nodes white
        {
            Iterator nodeIt = g.iterator();
            while(nodeIt.hasNext())
                nodeToColor.put(nodeIt.next(), new Integer(WHITE));
        }
        
        // Visit each node
        {
            Iterator nodeIt = g.iterator();
            
            while(nodeIt.hasNext())
            {
                Object s = nodeIt.next();
               
                if(((Integer) nodeToColor.get(s)).intValue() == WHITE)
                    visitNode(g, s); 
            }
        }

        // Re-color all nodes white
        {
            Iterator nodeIt = g.iterator();
            while(nodeIt.hasNext())
                nodeToColor.put(nodeIt.next(), new Integer(WHITE));
        }

        // Visit each node via transpose edges
        {
            Iterator revNodeIt = finishingOrder.iterator();
            while (revNodeIt.hasNext())
            {
                Object s = revNodeIt.next();

                if(((Integer) nodeToColor.get(s)).intValue() == WHITE)
                {
                    List currentComponent = null;

                    if (nodeToComponent.get(s) == null)
                    {
                        currentComponent = new ArrayList();
                        sccGraph.addNode(currentComponent);
                    }
                    else
                        currentComponent = (List)nodeToComponent.get(s);

                    visitRevNode(g, s, currentComponent); 
                    componentList.add(Collections.unmodifiableList(currentComponent));
                }
            }
        }
        componentList = Collections.unmodifiableList(componentList);
    }

    private void visitNode(DirectedGraph graph, Object startNode)
    {
        LinkedList nodeStack = new LinkedList();
        LinkedList indexStack = new LinkedList();
        
        nodeToColor.put(startNode, new Integer(GRAY));
        
        nodeStack.addLast(startNode);
        indexStack.addLast(new Integer(-1));
        
        while(!nodeStack.isEmpty())
        {
            int toVisitIndex = ((Integer) indexStack.removeLast()).intValue();
            Directed toVisitNode = (Directed)nodeStack.getLast();
            
            toVisitIndex++;
            
            indexStack.addLast(new Integer(toVisitIndex));
            
            if(toVisitIndex >= graph.getSuccsOf(toVisitNode).size())
            {
                // Visit this node now that we ran out of children 
                    finishingOrder.addFirst(toVisitNode);

                    nodeToColor.put(toVisitNode, new Integer(BLACK));                
                
                // Pop this node off
                    nodeStack.removeLast();
                    indexStack.removeLast();
            }
            else
            {
                Object childNode = graph.getSuccsOf(toVisitNode).get(toVisitIndex);
                
                // Visit this child next if not already visited (or on stack)
                    if(((Integer) nodeToColor.get(childNode)).intValue() == WHITE)
                    {
                        nodeToColor.put(childNode, new Integer(GRAY));
                        
                        nodeStack.addLast(childNode);
                        indexStack.addLast(new Integer(-1));
                    }
            }
        }
    }

    private void visitRevNode(DirectedGraph graph, Object startNode, List currentComponent)
    {
        LinkedList nodeStack = new LinkedList();
        LinkedList indexStack = new LinkedList();
        
        nodeToColor.put(startNode, new Integer(GRAY));
        
        nodeStack.addLast(startNode);
        indexStack.addLast(new Integer(-1));
        
        while(!nodeStack.isEmpty())
        {
            int toVisitIndex = ((Integer) indexStack.removeLast()).intValue();
            Directed toVisitNode = (Directed)nodeStack.getLast();
            
            toVisitIndex++;
            
            indexStack.addLast(new Integer(toVisitIndex));
            
            if(toVisitIndex >= graph.getPredsOf(toVisitNode).size())
            {
                // No more nodes.  Add toVisitNode to current component.
                    currentComponent.add(toVisitNode);
                    nodeToComponent.put(toVisitNode, currentComponent);
               
                // Pop this node off
                    nodeStack.removeLast();
                    indexStack.removeLast();
            }
            else
            {
                Object childNode = graph.getPredsOf(toVisitNode).get(toVisitIndex);
                
                // Visit this child next if not already visited (or on stack)
                    if(((Integer) nodeToColor.get(childNode)).intValue() == WHITE)
                    {
                        nodeToColor.put(childNode, new Integer(GRAY));
                        
                        nodeStack.addLast(childNode);
                        indexStack.addLast(new Integer(-1));
                    }
                    else if (((Integer)nodeToColor.get(childNode)).intValue() == GRAY)
                        /* we just stumbled on a back edge, in the same component. Ignore. */;
                    else
                    {
                        /* we may be visiting a node in another component.  if so, add edge to sccGraph. */
                        if (nodeToComponent.get(childNode) != currentComponent)
                            sccGraph.addEdge(currentComponent, nodeToComponent.get(childNode));
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
    public List getComponents()
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
        return (List)nodeToComponent.get(a);
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
