/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Navindra Umanee <navindra@cs.mcgill.ca>
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

import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.jimple.*;
import soot.options.*;
import java.util.*;
import soot.util.*;

/**
 * This adapter provides a DirectedGraph interface to DominatorTree.
 *
 * <p>
 *
 * This might be useful if e.g. you want to apply a DirectedGraph
 * analysis such as the PseudoTopologicalOrderer to a DominatorTree.
 *
 * @author Navindra Umanee
 **/
public class DominatorTreeAdapter implements DirectedGraph
{
    DominatorTree dt;
    
    public DominatorTreeAdapter(DominatorTree dt)
    {
        this.dt = dt;
    }

    public List getHeads()
    {
        return Collections.singletonList(dt.getHead());
    }

    public List getTails()
    {
        return dt.getTails();
    }

    public List getPredsOf(Object node)
    {
        return Collections.singletonList(dt.getParentOf((DominatorNode)node));
    }

    public List getSuccsOf(Object node)
    {
        return dt.getChildrenOf((DominatorNode)node);
    }

    public Iterator iterator()
    {
        return dt.iterator();
    }

    public int size()
    {
        return dt.size();
    }
}
