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
import java.util.Collections;
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
    private final Map<Cons, List<Unit>> answer;

    private final Map<Local, Set<Unit>> localToDefs; // for each local, set of units
                                   // where it's defined
    private final UnitGraph graph;
    private final LocalDefsAnalysis analysis;
    private final Map<Unit, Set<?>> unitToMask;
    public SmartLocalDefs(UnitGraph g, LiveLocals live) {
        this.graph = g;

        if(Options.v().time())
            Timers.v().defsTimer.start();
        
        if(Options.v().verbose())
            G.v().out.println("[" + g.getBody().getMethod().getName() +
                               "]     Constructing SmartLocalDefs...");

        localToDefs = new HashMap<Local, Set<Unit>>();
        unitToMask = new HashMap<Unit, Set<?>>();
        for( Unit u : graph ) {  
            unitToMask.put(u, new HashSet(live.getLiveLocalsAfter(u)));
            
            Local l = localDef(u);
            if( l == null ) continue;
            Set<Unit> s = defsOf(l);
            s.add(u);
        }

        if(Options.v().verbose())
            G.v().out.println("[" + g.getBody().getMethod().getName() +
                               "]        done localToDefs map..." );

        if(Options.v().verbose())
            G.v().out.println("[" + g.getBody().getMethod().getName() +
                               "]        done unitToMask map..." );

        analysis = new LocalDefsAnalysis(graph);

        answer = new HashMap<Cons, List<Unit>>();
        for( Unit u : graph ) {            
            for( ValueBox vb : u.getUseBoxes() ) {
                Value v = vb.getValue();
                if( !(v instanceof Local) ) continue;
                Set<Unit> analysisResult = analysis.getFlowBefore(u);
                ArrayList<Unit> al = new ArrayList<Unit>();
                for (Unit unit : defsOf((Local)v)) {
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
        List<ValueBox> defBoxes = u.getDefBoxes();
		int size = defBoxes.size();
        if( size == 0 ) return null;
        if( size != 1 ) throw new RuntimeException();
        ValueBox vb = (ValueBox) defBoxes.get(0);
        Value v = vb.getValue();
        if( !(v instanceof Local) ) return null;
        return (Local) v;
    }
    
    private Set<Unit> defsOf( Local l ) {
    	Set<Unit> ret = localToDefs.get(l);
        if( ret == null ) localToDefs.put( l, ret = new HashSet<Unit>() );
        return ret;
    }

    class LocalDefsAnalysis extends ForwardFlowAnalysis<Unit,Set<Unit>> {
        LocalDefsAnalysis(UnitGraph g) {
            super(g);
            doAnalysis();
        }        		

		@Override
		protected void mergeInto(Unit succNode, Set<Unit> inout, Set<Unit> in) {
			inout.addAll(in);
		}
        
		@Override
		protected void merge(Set<Unit> in1, Set<Unit> in2, Set<Unit> out) {
			// mergeInto should be called
			throw new RuntimeException("should never be called");
		}
        
        @Override
        protected void flowThrough(Set<Unit> in, Unit u, Set<Unit> out) {
            out.clear();
            
            Set<?> mask = unitToMask.get(u);
            Local l = localDef(u);
            Set<Unit> allDefUnits = null;
			if (l == null)
			{//add all units contained in mask
	            for( Unit inU : in )
	                if( mask.contains(localDef(inU)) )
					{
						out.add(inU);
					}
			}
			else
			{//check unit whether contained in allDefUnits before add into out set.
				allDefUnits = defsOf(l);
				
	            for( Unit inU : in )
    	            if( mask.contains(localDef(inU)) )
					{//only add unit not contained in allDefUnits
						if ( allDefUnits.contains(inU)){
							out.remove(inU);
						} else {
							out.add(inU);
						}
					}
   	            out.removeAll(allDefUnits);
   	            
   	            if(mask.contains(l)) 
   	            	out.add(u);
			}
        }

        @Override
        protected void copy(Set<Unit> sourceSet, Set<Unit> destSet) {    
        	destSet.clear();
        	destSet.addAll(sourceSet);
        }
        
        @Override
        protected Set<Unit> newInitialFlow() {
            return new HashSet<Unit>();
        }
        
        @Override
        protected Set<Unit> entryInitialFlow() {
            return Collections.emptySet();
        }
    }

    public List<Unit> getDefsOfAt(Local l, Unit s)
    {
        return answer.get(new Cons(s, l));
    }

}

