/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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






package soot.toolkits.scalar;

import soot.*;
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;

public abstract class FlowAnalysis
{
    protected Map unitToAfterFlow,
        unitToBeforeFlow;

    UnitGraph graph;

    public FlowAnalysis(UnitGraph graph)
    {
        unitToAfterFlow = new HashMap(graph.size() * 2 + 1, 0.7f);
        unitToBeforeFlow = new HashMap(graph.size() * 2 + 1, 0.7f);

        this.graph = graph;
    }

    protected abstract Object newInitialFlow();

    protected abstract boolean isForward();

    protected abstract void flowThrough(Object in, Unit s, Object out);
    protected abstract void merge(Object in1, Object in2, Object out);
    protected abstract void copy(Object source, Object dest);
    protected abstract void doAnalysis();

    public Object getFlowAfterUnit(Unit s)
    {
        return unitToAfterFlow.get(s);
    }

    public Object getFlowBeforeUnit(Unit s)
    {
        return unitToBeforeFlow.get(s);
    }
}
