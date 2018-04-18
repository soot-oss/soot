/* Soot - a J*va Optimization Framework
 * Copyright (C) 2001 Felix Kwok
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
 * A memory efficient version of HashMutableDirectedGraph, in the sense
 * that throw-away objects passed as arguments will not be kept in the
 * process of adding edges.
 */

public class MemoryEfficientGraph<N> extends HashMutableDirectedGraph<N>
{

    HashMap<N, N> self = new HashMap<N, N>();

    public void addNode(N o) {
        super.addNode(o);
        self.put(o,o);
    }

    public void removeNode(N o) {
        super.removeNode(o);
        self.remove(o);
    }

    public void addEdge(N from, N to) {
        if (containsNode(from) && containsNode(to))
            super.addEdge(self.get(from), self.get(to));
        else if (!containsNode(from))
            throw new RuntimeException(from.toString() + " not in graph!");
        else
            throw new RuntimeException(to.toString() + " not in graph!");
    }

    public void removeEdge(N from, N to) {
        if (containsNode(from) && containsNode(to))
            super.removeEdge(self.get(from), self.get(to));
        else if (!containsNode(from))
            throw new RuntimeException(from.toString() + " not in graph!");
        else
            throw new RuntimeException(to.toString() + " not in graph!");
    }

}

    
