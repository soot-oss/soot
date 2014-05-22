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

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.toolkits.graph.UnitGraph;

/**
 * An analysis to check whether or not local variables have been initialised.
 * 
 * @author Ganesh Sittampalam
 * @author Eric Bodden
 */
public class InitAnalysis extends ForwardFlowAnalysis<Unit, FlowSet> {
    FlowSet allLocals;

    public InitAnalysis(UnitGraph g) {
        super(g);
        allLocals = new ArraySparseSet();        
        for (Local loc : g.getBody().getLocals()) {
            allLocals.add(loc);
        }

        doAnalysis();
    }

    @Override
    protected FlowSet entryInitialFlow() {
        return new ArraySparseSet();
    }
    
    @Override
    protected FlowSet newInitialFlow() {
        FlowSet ret = new ArraySparseSet();
        allLocals.copy(ret);
        return ret;
    }

    @Override
    protected void flowThrough(FlowSet in, Unit unit, FlowSet out) {
        in.copy(out);

        for (ValueBox defBox : unit.getDefBoxes()) {
            Value lhs = defBox.getValue();
            if (lhs instanceof Local) {
                out.add(lhs);
            }
        }
    }

    @Override
    protected void merge(FlowSet in1, FlowSet in2, FlowSet out) {
        in1.intersection(in2, out);
    }
    
    @Override
    protected void copy(FlowSet source, FlowSet dest) {
        source.copy(dest);
    }

}
