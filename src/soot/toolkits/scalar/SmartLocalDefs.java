/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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

package soot.toolkits.scalar;
import soot.options.*;

import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.*;
import soot.util.*;
import java.util.*;


/**
 *   Analysis that provides an implementation of the LocalDefs interface.
 */
public class SmartLocalDefs implements LocalDefs
{
    private final LiveLocals live;
    private final Map localToDefs; // for each local, set of units
                                   // where it's defined
    private final UnitGraph graph;
    private final LocalDefsAnalysis analysis;
    private final Map unitToMask;
    public SmartLocalDefs(UnitGraph g, LiveLocals live) {
        this.live = live;
        this.graph = g;

        if(Options.v().time())
            Timers.v().defsTimer.start();
        
        if(Options.v().verbose())
            G.v().out.println("[" + g.getBody().getMethod().getName() +
                               "]     Constructing SmartLocalDefs...");

        localToDefs = new HashMap();
        unitToMask = new HashMap();
        for( Iterator uIt = g.iterator(); uIt.hasNext(); ) {
            final Unit u = (Unit) uIt.next();
            Local l = localDef(u);
            if( l == null ) continue;
            FlowSet s = defsOf(l);
            s.add(u);
        }

        if(Options.v().verbose())
            G.v().out.println("[" + g.getBody().getMethod().getName() +
                               "]        done localToDefs map..." );

        for( Iterator uIt = g.iterator(); uIt.hasNext(); ) {

            final Unit u = (Unit) uIt.next();
            FlowSet mask = new ArraySparseSet();
            for( Iterator liveLocalIt = live.getLiveLocalsAfter(u).iterator(); liveLocalIt.hasNext(); ) {
                final Local liveLocal = (Local) liveLocalIt.next();
                mask.union(defsOf(liveLocal));
            }
            unitToMask.put(u, mask);
        }

        if(Options.v().verbose())
            G.v().out.println("[" + g.getBody().getMethod().getName() +
                               "]        done unitToMask map..." );

        analysis = new LocalDefsAnalysis(graph);

        if(Options.v().time())
            Timers.v().defsTimer.end();

	if(Options.v().verbose())
	    G.v().out.println("[" + g.getBody().getMethod().getName() +
                               "]     SmartLocalDefs finished.");
    }
    private Local localDef(Unit u) {
        List defBoxes = u.getDefBoxes();
        if( defBoxes.size() == 0 ) return null;
        if( defBoxes.size() != 1 ) throw new RuntimeException();
        ValueBox vb = (ValueBox) defBoxes.get(0);
        Value v = vb.getValue();
        if( !(v instanceof Local) ) return null;
        return (Local) v;
    }
    private FlowSet defsOf( Local l ) {
        FlowSet ret = (FlowSet)localToDefs.get(l);
        if( ret == null ) localToDefs.put( l, ret = new ArraySparseSet() );
        return ret;
    }

    class LocalDefsAnalysis extends ForwardFlowAnalysis {
        LocalDefsAnalysis(UnitGraph g) {
            super(g);
            doAnalysis();
        }
        protected void merge(Object inoutO, Object inO) {
            FlowSet inout = (FlowSet) inoutO;
            FlowSet in = (FlowSet) inO;

            inout.union(in);
        }
        protected void merge(Object in1, Object in2, Object out) {
            FlowSet inSet1 = (FlowSet) in1;
            FlowSet inSet2 = (FlowSet) in2;
            FlowSet outSet = (FlowSet) out;

            inSet1.union(inSet2, outSet);
        }
        protected void flowThrough(Object inValue, Object unit, Object outValue) {
            Unit u = (Unit) unit;
            FlowSet in = (FlowSet) inValue;
            FlowSet out = (FlowSet) outValue;
            Local l = localDef(u);
            in.copy(out);
            if( l != null ) {
                out.difference(defsOf(l));
                out.add(u);
            }
            out.intersection((FlowSet)unitToMask.get(u));
        }
    
        protected void copy(Object source, Object dest) {
            FlowSet sourceSet = (FlowSet) source;
            FlowSet destSet   = (FlowSet) dest;
                
            sourceSet.copy(destSet);
        }

        protected Object newInitialFlow() {
            return new ArraySparseSet();
        }

        protected Object entryInitialFlow() {
            return new ArraySparseSet();
        }
    }

    public List getDefsOfAt(Local l, Unit s)
    {
        FlowSet ret = new ArraySparseSet();
        defsOf(l).copy(ret);
        ret.intersection((FlowSet)analysis.getFlowBefore(s));
        return ret.toList();
    }

}

