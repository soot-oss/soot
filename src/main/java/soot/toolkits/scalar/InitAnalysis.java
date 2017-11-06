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
public class InitAnalysis extends ForwardFlowAnalysis<Unit, FlowSet<Local>> {
    FlowSet<Local> allLocals;

    public InitAnalysis(UnitGraph g) {
        super(g);
        allLocals = new ArraySparseSet<Local>();        
        for (Local loc : g.getBody().getLocals()) {
            allLocals.add(loc);
        }

        doAnalysis();
    }

    @Override
    protected FlowSet<Local> entryInitialFlow() {
        return new ArraySparseSet<Local>();
    }
    
    @Override
    protected FlowSet<Local> newInitialFlow() {
        FlowSet<Local> ret = new ArraySparseSet<Local>();
        allLocals.copy(ret);
        return ret;
    }

    @Override
    protected void flowThrough(FlowSet<Local> in, Unit unit, FlowSet<Local> out) {
        in.copy(out);

        for (ValueBox defBox : unit.getDefBoxes()) {
            Value lhs = defBox.getValue();
            if (lhs instanceof Local) {
                out.add((Local) lhs);
            }
        }
    }

    @Override
    protected void merge(FlowSet<Local> in1, FlowSet<Local> in2, FlowSet<Local> out) {
        in1.intersection(in2, out);
    }
    
    @Override
    protected void copy(FlowSet<Local> source, FlowSet<Local> dest) {
        source.copy(dest);
    }

}
