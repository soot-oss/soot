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
 *   Analysis that computes live locals, local defs, and local uses all at once.
 */
public class CombinedDUAnalysis extends BackwardFlowAnalysis implements CombinedAnalysis, LocalDefs, LocalUses, LiveLocals
{
    // Implementations of our interfaces...
    private Map defsOfAt = new HashMap();
    public List getDefsOfAt(Local l, Unit s) {
        Cons cons = new Cons(l, s);
        List ret = (List) defsOfAt.get(cons);
        if(ret == null) {
            defsOfAt.put(cons, ret = new ArrayList());
        }
        return ret;
    }
    private Map usesOf = new HashMap();
    public List getUsesOf(Unit u) {
        List ret = (List) usesOf.get(u);
        if( ret == null ) {
            Local def = localDefed(u);
            if( def == null ) {
                usesOf.put(u, ret = Collections.EMPTY_LIST);
            } else {
                usesOf.put(u, ret = new ArrayList());
                for( Iterator vbIt = ((FlowSet)getFlowAfter(u)).iterator(); vbIt.hasNext(); ) {
                    final ValueBox vb = (ValueBox) vbIt.next();
                    if( vb.getValue() != def ) continue;
                    ret.add(new UnitValueBoxPair((Unit)useBoxToUnit.get(vb), vb));
                }
            }
        }
        return ret;
    }
    private Map liveLocalsBefore = new HashMap();
    public List getLiveLocalsBefore(Unit u) {
        List ret = (List) liveLocalsBefore.get(u);
        if( ret == null ) {
            HashSet hs = new HashSet();
            for( Iterator vbIt = ((FlowSet)getFlowBefore(u)).iterator(); vbIt.hasNext(); ) {
                final ValueBox vb = (ValueBox) vbIt.next();
                hs.add(vb.getValue());
            }
            liveLocalsBefore.put(u, ret = new ArrayList(hs));
        }
        return ret;
    }
    private Map liveLocalsAfter = new HashMap();
    public List getLiveLocalsAfter(Unit u) {
        List ret = (List) liveLocalsAfter.get(u);
        if( ret == null ) {
            HashSet hs = new HashSet();
            for( Iterator vbIt = ((FlowSet)getFlowAfter(u)).iterator(); vbIt.hasNext(); ) {
                final ValueBox vb = (ValueBox) vbIt.next();
                hs.add(vb.getValue());
            }
            liveLocalsAfter.put(u, ret = new ArrayList(hs));
        }
        return ret;
    }

    // The actual analysis is below.
    
    private final Map useBoxToUnit = new HashMap();
    private final Map unitToLocalDefed = new HashMap();
    private Local localDefed(Unit u) { return (Local) unitToLocalDefed.get(u); }
    private final Map unitToLocalUseBoxes = new HashMap();
    private final MultiMap localToUseBoxes = new HashMultiMap();
    private final UnitGraph graph;

    public static CombinedAnalysis v(final UnitGraph graph) {
        if(true) {
            return new CombinedDUAnalysis(graph);
        }

        return new CombinedAnalysis() {
            CombinedDUAnalysis combined = new CombinedDUAnalysis(graph);
            SimpleLocalDefs defs = new SimpleLocalDefs(graph);
            SimpleLocalUses uses = new SimpleLocalUses(graph, defs);
            SimpleLiveLocals live = new SimpleLiveLocals(graph);
            public List getDefsOfAt(Local l, Unit s) {
                HashSet hs1 = new HashSet(combined.getDefsOfAt(l, s));
                HashSet hs2 = new HashSet(defs.getDefsOfAt(l, s));
                if( !hs1.equals(hs2) ) throw new RuntimeException(
                        "Defs of "+l+" in "+s+"\ncombined: "+hs1+"\nsimple: "+hs2);
                return combined.getDefsOfAt(l, s);
            }
            public List getUsesOf(Unit u) {
                HashSet hs1 = new HashSet(combined.getUsesOf(u));
                HashSet hs2 = new HashSet(uses.getUsesOf(u));
                if( !hs1.equals(hs2) ) throw new RuntimeException(
                        "Uses of "+u+"\ncombined: "+hs1+"\nsimple: "+hs2);
                return combined.getUsesOf(u);
            }
            public List getLiveLocalsBefore(Unit u) {
                HashSet hs1 = new HashSet(combined.getLiveLocalsBefore(u));
                HashSet hs2 = new HashSet(live.getLiveLocalsBefore(u));
                if( !hs1.equals(hs2) ) throw new RuntimeException(
                        "llb of "+u+"\ncombined: "+hs1+"\nsimple: "+hs2);
                return combined.getLiveLocalsBefore(u);
            }
            public List getLiveLocalsAfter(Unit u) {
                HashSet hs1 = new HashSet(combined.getLiveLocalsAfter(u));
                HashSet hs2 = new HashSet(live.getLiveLocalsAfter(u));
                if( !hs1.equals(hs2) ) throw new RuntimeException(
                        "lla of "+u+"\ncombined: "+hs1+"\nsimple: "+hs2);
                return combined.getLiveLocalsAfter(u);
            }
        }
        ;
    }
    private CombinedDUAnalysis(UnitGraph graph) {
        super(graph);
        this.graph = graph;

        if(Options.v().verbose())
            G.v().out.println("[" + graph.getBody().getMethod().getName() +
                               "]     Constructing CombinedDUAnalysis...");
 
        for( Iterator uIt = graph.iterator(); uIt.hasNext(); ) {
 
            final Unit u = (Unit) uIt.next();
            List defs = localsInBoxes(u.getDefBoxes());
            if( defs.size() == 1 ) unitToLocalDefed.put(u, defs.get(0));
            else if(defs.size() != 0) throw new RuntimeException("Locals defed in "+u+": "+defs.size());
            ArraySparseSet localUseBoxes = new ArraySparseSet();
            for( Iterator vbIt = u.getUseBoxes().iterator(); vbIt.hasNext(); ) {
                final ValueBox vb = (ValueBox) vbIt.next();
                Value v = vb.getValue();
                if(!(v instanceof Local)) continue;
                localUseBoxes.add(vb);
                if( useBoxToUnit.containsKey(vb) ) throw new RuntimeException("Aliased ValueBox "+vb+" in Unit "+u);
                useBoxToUnit.put(vb, u);
                localToUseBoxes.put(v, vb);
            }
            unitToLocalUseBoxes.put(u, localUseBoxes);
        }

        doAnalysis();

        for( Iterator defUnitIt = graph.iterator(); defUnitIt.hasNext(); ) {

            final Unit defUnit = (Unit) defUnitIt.next();
            /*
            getLiveLocalsAfter(defUnit);
            getLiveLocalsBefore(defUnit);
            getUsesOf(defUnit);
            */
            Local localDefed = localDefed(defUnit);
            if( localDefed == null ) continue;
            for( Iterator vbIt = ((FlowSet)getFlowAfter(defUnit)).iterator(); vbIt.hasNext(); ) {
                final ValueBox vb = (ValueBox) vbIt.next();
                if( vb.getValue() != localDefed ) continue;
                Unit useUnit = (Unit) useBoxToUnit.get(vb);
                getDefsOfAt(localDefed, useUnit).add(defUnit);
            }
        }
        if(Options.v().verbose())
            G.v().out.println("[" + graph.getBody().getMethod().getName() +
                               "]     Finished CombinedDUAnalysis...");
 
    }
    private List localsInBoxes(List/*ValueBox*/ boxes) {
        List ret = new ArrayList();
        for( Iterator vbIt = boxes.iterator(); vbIt.hasNext(); ) {
            final ValueBox vb = (ValueBox) vbIt.next();
            Value v = vb.getValue();
            if( !(v instanceof Local) ) continue;
            ret.add(v);
        }
        return ret;
    }


// STEP 1: What are we computing?
// SETS OF USE BOXES CONTAINING LOCALS => Use HashSet.
//
// STEP 2: Precisely define what we are computing.
// A use box B is live at program point P if there exists a path from P to the
// unit using B on which the local in B is not defined.
//
// STEP 3: Decide whether it is a backwards or forwards analysis.
// BACKWARDS
//
// STEP 4: Is the merge operator union or intersection?
// UNION
    protected void merge(Object inoutO, Object inO) {
        FlowSet inout = (FlowSet) inoutO;
        FlowSet in = (FlowSet) inO;

        inout.union(in);
    }
    protected void merge(Object in1O, Object in2O, Object outO) {
        FlowSet in1 = (FlowSet) in1O;
        FlowSet in2 = (FlowSet) in2O;
        FlowSet out = (FlowSet) outO;

        in1.union(in2, out);
    }
// STEP 5: Define flow equations.
// in(s) = ( out(s) minus boxes(def(s)) ) union useboxes(s)
    protected void flowThrough(Object outValue, Object unit, Object inValue) {
        Unit u = (Unit) unit;
        FlowSet in = (FlowSet) inValue;
        FlowSet out = (FlowSet) outValue;
        Local def = localDefed(u);
        out.copy(in);
        if(def != null) {
            Collection boxesDefed = localToUseBoxes.get(def);
            for( Iterator vbIt = in.toList().iterator(); vbIt.hasNext(); ) {
                final ValueBox vb = (ValueBox) vbIt.next();
                if(boxesDefed.contains(vb)) in.remove(vb);
            }
        }
        in.union((FlowSet) unitToLocalUseBoxes.get(u));
    }

// STEP 6: Determine value for start/end node, and
// initial approximation.
//
// end node:              empty set
// initial approximation: empty set
    protected Object entryInitialFlow()
    {
        return new ArraySparseSet();
    }
        
    protected Object newInitialFlow()
    {
        return new ArraySparseSet();
    }

    protected void copy(Object source, Object dest) {
        FlowSet sourceSet = (FlowSet) source;
        FlowSet destSet   = (FlowSet) dest;
            
        sourceSet.copy(destSet);
    }

}

