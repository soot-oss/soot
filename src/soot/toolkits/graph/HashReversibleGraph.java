/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Navindra Umanee <navindra@cs.mcgill.ca>
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

import java.util.*;

/**
 * A reversible version of HashMutableDirectedGraph
 *
 * @author Navindra Umanee
 **/
public class HashReversibleGraph extends HashMutableDirectedGraph
    implements ReversibleGraph
{
    protected boolean reversed;

    public HashReversibleGraph(DirectedGraph dg)
    {
        this();

        for(Iterator i = dg.iterator(); i.hasNext();){
            Object s = i.next();
            addNode(s);
        }

        for(Iterator i = dg.iterator(); i.hasNext();){
            Object s = i.next();
            List succs = dg.getSuccsOf(s);
            for(Iterator succsIt = succs.iterator(); succsIt.hasNext();){
                Object t = succsIt.next();
                addEdge(s, t);
            }
        }

        /* use the same heads and tails as the original graph */
        
        heads.clear();
        heads.addAll(dg.getHeads());
        tails.clear();
        tails.addAll(dg.getTails());
    }
            
    public HashReversibleGraph()
    {
        super();
        reversed = false;
    }
    
    public boolean isReversed()
    {
        return reversed;
    }

    public ReversibleGraph reverse()
    {   
        reversed = !reversed;
        return this;
    }

    public void addEdge(Object from, Object to)
    {
        if(reversed)
            super.addEdge(to, from);
        else
            super.addEdge(from, to);
    }

    public void removeEdge(Object from, Object to)
    {
        if(reversed)
            super.removeEdge(to, from);
        else
            super.removeEdge(from, to);
    }

    public boolean containsEdge(Object from, Object to)
    {
        return reversed ? super.containsEdge(to, from) : super.containsEdge(from, to);
    }

    public List getHeads()
    {
        return reversed ? super.getTails() : super.getHeads();
    }

    public List getTails()
    {
        return reversed ? super.getHeads() : super.getTails();
    }   

    public List getPredsOf(Object s)
    {
        return reversed ? super.getSuccsOf(s) : super.getPredsOf(s);
    }
    
    public List getSuccsOf(Object s)
    {
        return reversed ? super.getPredsOf(s) : super.getSuccsOf(s);
    }
}
