/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville, Raja Vallee-Rai, Patrick Lam
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


import java.util.*;
import soot.*;
import soot.util.*;

public class HashMutableDirectedGraph implements MutableDirectedGraph
{
    class NodePair
    {
        Object car, cdr;

        NodePair(Object car, Object cdr) { this.car = car; this.cdr = cdr; }
        public int hashCode() { return car.hashCode() * 101 + cdr.hashCode() + 17; }
        public boolean equals(Object o)
        {
            if (!(o instanceof NodePair))
                return false;

            NodePair n = (NodePair)o;
            if (n.car.equals(car) && n.cdr.equals(cdr))
                return true;
            return false;
        }
    }

    private HashMap nodeToPreds = new HashMap();
    private HashMap nodeToSuccs = new HashMap();
    private HashSet edgePairs = new HashSet();
    private Chain nodes = new HashChain();

    private Chain heads = new HashChain();
    private Chain tails = new HashChain();

    public HashMutableDirectedGraph()
    {
    }

    /** Returns an unbacked list of heads for this graph. */
    public List getHeads()
    {
        ArrayList l = new ArrayList(); l.addAll(heads);
        return Collections.unmodifiableList(l);
    }

    /** Returns an unbacked list of tails for this graph. */
    public List getTails()
    {
        ArrayList l = new ArrayList(); l.addAll(tails);
        return Collections.unmodifiableList(l);
    }

    public List getPredsOf(Directed s)
    {
        List l = (List) nodeToPreds.get(s);
        if (l != null)
            return Collections.unmodifiableList(l);
        else
            throw new RuntimeException(s+"not in graph!");
    }

    public List getSuccsOf(Directed s)
    {
        List l = (List) nodeToSuccs.get(s);
        if (l != null)
            return Collections.unmodifiableList(l);
        else
            throw new RuntimeException(s+"not in graph!");
    }

    public int size()
    {
        return nodes.size();
    }

    public Iterator iterator()
    {
        return nodes.iterator();
    }

    public void addEdge(Object from, Object to)
    {
        if (containsEdge(from, to))
            return;

        List succsList = (List)nodeToSuccs.get(from);
        if (succsList == null)
            throw new RuntimeException(from + " not in graph!");

        List predsList = (List)nodeToPreds.get(to);
        if (predsList == null)
            throw new RuntimeException(to + " not in graph!");

        if (heads.contains(to))
            heads.remove(to);

        if (tails.contains(from))
            tails.remove(from);

        succsList.add(to);
        predsList.add(from);

        edgePairs.add(new NodePair(from, to));
    }

    public void removeEdge(Object from, Object to)
    {
        if (!containsEdge(from, to))
            return;

        List succsList = (List)nodeToSuccs.get(from);
        if (succsList == null)
            throw new RuntimeException(from + " not in graph!");

        List predsList = (List)nodeToPreds.get(to);
        if (predsList == null)
            throw new RuntimeException(to + " not in graph!");

        succsList.remove(to);
        predsList.remove(from);

        if (succsList.isEmpty())
            tails.add(from);

        if (predsList.isEmpty())
            heads.add(to);

        edgePairs.remove(new NodePair(from, to));
    }

    public boolean containsEdge(Object from, Object to)
    {
        return edgePairs.contains(new NodePair(from, to));
    }

    public boolean containsNode(Object node)
    {
        return nodes.contains(node);
    }

    public List getNodes()
    {
        ArrayList l = new ArrayList(); l.addAll(nodes);
        return Collections.unmodifiableList(l);        
    }

    public void addNode(Object node)
    {
        nodes.add(node);
        nodeToSuccs.put(node, new ArrayList());
        nodeToPreds.put(node, new ArrayList());
        heads.add(node); tails.add(node);
    }

    public void removeNode(Object node)
    {
        nodes.remove(node);
        nodeToSuccs.remove(node);
        nodeToPreds.remove(node);
        if (heads.contains(node))
            heads.remove(node); 

        if (tails.contains(node))
            tails.remove(node);
    }
}

 
