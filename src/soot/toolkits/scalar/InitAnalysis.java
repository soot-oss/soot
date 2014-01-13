/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ganesh Sittampalam
 * Copyright (C) 2007 Eric Bodden
 * 
 * This compiler is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This compiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this compiler, in the file LESSER-GPL;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package soot.toolkits.scalar;

import java.util.Iterator;
import java.util.List;

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;

/**
 * An analysis to check whether or not local variables have been initialised.
 * 
 * @author Ganesh Sittampalam
 * @author Eric Bodden
 */
public class InitAnalysis extends ForwardFlowAnalysis {
    FlowSet allLocals;

    public InitAnalysis(UnitGraph g) {
        super(g);
        Chain locs = g.getBody().getLocals();
        allLocals = new ArraySparseSet();
        Iterator it = locs.iterator();
        while (it.hasNext()) {
            Local loc = (Local) it.next();
            allLocals.add(loc);
        }

        doAnalysis();
    }

    protected Object entryInitialFlow() {
        return new ArraySparseSet();
    }

    protected Object newInitialFlow() {
        FlowSet ret = new ArraySparseSet();
        allLocals.copy(ret);
        return ret;
    }

    protected void flowThrough(Object in, Object unit, Object out) {
        FlowSet inSet = (FlowSet) in;
        FlowSet outSet = (FlowSet) out;
        Unit s = (Unit) unit;

        inSet.copy(outSet);

        for (ValueBox defBox : s.getDefBoxes()) {
            Value lhs = defBox.getValue();
            if (lhs instanceof Local) {
                outSet.add(lhs);
            }
        }
    }

    protected void merge(Object in1, Object in2, Object out) {
        FlowSet outSet = (FlowSet) out;
        FlowSet inSet1 = (FlowSet) in1;
        FlowSet inSet2 = (FlowSet) in2;
        inSet1.intersection(inSet2, outSet);
    }

    protected void copy(Object source, Object dest) {
        FlowSet sourceSet = (FlowSet) source;
        FlowSet destSet = (FlowSet) dest;
        sourceSet.copy(destSet);
    }

}
