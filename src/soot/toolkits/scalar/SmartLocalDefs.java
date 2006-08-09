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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.G;
import soot.Local;
import soot.Timers;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.options.Options;
import soot.toolkits.graph.UnitGraph;
import soot.util.Cons;


/**
 *   Analysis that provides an implementation of the LocalDefs interface.
 */
public class SmartLocalDefs implements LocalDefs
{
    private final Map answer;

    private final Map localToDefs; // for each local, set of units
                                   // where it's defined
    private final UnitGraph graph;
    private final LocalDefsAnalysis analysis;
    private final Map unitToMask;
    public SmartLocalDefs(UnitGraph g, LiveLocals live) {
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
            HashSet s = defsOf(l);
            s.add(u);
        }

        if(Options.v().verbose())
            G.v().out.println("[" + g.getBody().getMethod().getName() +
                               "]        done localToDefs map..." );

        for( Iterator uIt = g.iterator(); uIt.hasNext(); ) {
            final Unit u = (Unit) uIt.next();
            unitToMask.put(u, new HashSet(live.getLiveLocalsAfter(u)));
        }

        if(Options.v().verbose())
            G.v().out.println("[" + g.getBody().getMethod().getName() +
                               "]        done unitToMask map..." );

        analysis = new LocalDefsAnalysis(graph);

        answer = new HashMap();
        for( Iterator uIt = graph.iterator(); uIt.hasNext(); ) {
            final Unit u = (Unit) uIt.next();
            for( Iterator vbIt = u.getUseBoxes().iterator(); vbIt.hasNext(); ) {
                final ValueBox vb = (ValueBox) vbIt.next();
                Value v = vb.getValue();
                if( !(v instanceof Local) ) continue;
                HashSet analysisResult = (HashSet) analysis.getFlowBefore(u);
                ArrayList al = new ArrayList();
                for( Iterator unitIt = defsOf((Local)v).iterator(); unitIt.hasNext(); ) {
                    final Unit unit = (Unit) unitIt.next();
                    if(analysisResult.contains(unit)) al.add(unit);
                }
                answer.put(new Cons(u, v), al);
            }
        }
        if(Options.v().time())
            Timers.v().defsTimer.end();

	if(Options.v().verbose())
	    G.v().out.println("[" + g.getBody().getMethod().getName() +
                               "]     SmartLocalDefs finished.");
    }
    private Local localDef(Unit u) {
        List defBoxes = u.getDefBoxes();
		int size = defBoxes.size();
        if( size == 0 ) return null;
        if( size != 1 ) throw new RuntimeException();
        ValueBox vb = (ValueBox) defBoxes.get(0);
        Value v = vb.getValue();
        if( !(v instanceof Local) ) return null;
        return (Local) v;
    }
    private HashSet defsOf( Local l ) {
        HashSet ret = (HashSet)localToDefs.get(l);
        if( ret == null ) localToDefs.put( l, ret = new HashSet() );
        return ret;
    }

    class LocalDefsAnalysis extends ForwardFlowAnalysis {
        LocalDefsAnalysis(UnitGraph g) {
            super(g);
            doAnalysis();
        }
        protected void merge(Object inoutO, Object inO) {
            HashSet inout = (HashSet) inoutO;
            HashSet in = (HashSet) inO;

            inout.addAll(in);
        }
        protected void merge(Object in1, Object in2, Object out) {
            HashSet inSet1 = (HashSet) in1;
            HashSet inSet2 = (HashSet) in2;
            HashSet outSet = (HashSet) out;

            outSet.clear();
            outSet.addAll(inSet1);
            outSet.addAll(inSet2);
        }
		
        protected void flowThrough(Object inValue, Object unit, Object outValue) {
            Unit u = (Unit) unit;
            HashSet in = (HashSet) inValue;
            HashSet out = (HashSet) outValue;
            out.clear();
            Set mask = (Set) unitToMask.get(u);
            Local l = localDef(u);
			HashSet allDefUnits = null;
			if (l == null)
			{//add all units contained in mask
	            for( Iterator inUIt = in.iterator(); inUIt.hasNext(); ) {
	                final Unit inU = (Unit) inUIt.next();
	                if( mask.contains(localDef(inU)) )
					{
						out.add(inU);
					}
	            }
			}
			else
			{//check unit whether contained in allDefUnits before add into out set.
				allDefUnits = defsOf(l);
				
	            for( Iterator inUIt = in.iterator(); inUIt.hasNext(); ) {
	                final Unit inU = (Unit) inUIt.next();
    	            if( mask.contains(localDef(inU)) )
					{//only add unit not contained in allDefUnits
						if ( allDefUnits.contains(inU)){
							out.remove(inU);
						} else {
							out.add(inU);
						}
					}
    	        }
   	            out.removeAll(allDefUnits);
   	            if(mask.contains(l)) out.add(u);
			}
        }

    
        protected void copy(Object source, Object dest) {
            HashSet sourceSet = (HashSet) source;
            HashSet destSet   = (HashSet) dest;
              
			//retain all the elements contained by sourceSet
			if (destSet.size() > 0)
				destSet.retainAll(sourceSet);
			
			//add the elements not contained by destSet
			if (sourceSet.size() > 0)
			{
				for( Iterator its = sourceSet.iterator(); its.hasNext(); ) {
					Object o = its.next();
					if (!destSet.contains(o))
					{//need add this element.
						destSet.add(o);
					}
				}
			}

        }

        protected Object newInitialFlow() {
            return new HashSet();
        }

        protected Object entryInitialFlow() {
            return new HashSet();
        }
    }

    public List getDefsOfAt(Local l, Unit s)
    {
        return (List) answer.get(new Cons(s, l));
    }

}

