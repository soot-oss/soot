/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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





package soot.toolkits.scalar;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.Scene;
import soot.Singletons;
import soot.Timers;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.options.Options;
import soot.toolkits.exceptions.ThrowAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.NumberedSet;

/**
 *    A BodyTransformer that attemps to indentify and separate uses of a local
 *    variable that are independent of each other. Conceptually the inverse transform
 *    with respect to the LocalPacker transform. 
 *    
 *
 *    For example the code:
 *
 *    for(int i; i < k; i++);
 *    for(int i; i < k; i++);
 *
 *    would be transformed into:
 *    for(int i; i < k; i++);
 *    for(int j; j < k; j++);
 *
 *
 *
 *    @see BodyTransformer
 *    @see LocalPacker
 *    @see Body 
 */
public class LocalSplitter extends BodyTransformer
{
	
	protected ThrowAnalysis throwAnalysis;
	protected boolean omitExceptingUnitEdges;

	public LocalSplitter( Singletons.Global g ) {
	}
	
	public LocalSplitter( ThrowAnalysis ta ) {
		this(ta, false);
	}

	public LocalSplitter( ThrowAnalysis ta, boolean omitExceptingUnitEdges ) {
		this.throwAnalysis = ta;
		this.omitExceptingUnitEdges = omitExceptingUnitEdges;
	}
	
	public static LocalSplitter v() { return G.v().soot_toolkits_scalar_LocalSplitter(); }
    
	@Override
    protected void internalTransform(Body body, String phaseName, Map<String, String> options)
    {		
        if(Options.v().verbose())
            G.v().out.println("[" + body.getMethod().getName() + "] Splitting locals...");
        
		if (Options.v().time()) 
			Timers.v().splitTimer.start();
		

        if(Options.v().time())
                Timers.v().splitPhase1Timer.start();

        if (throwAnalysis == null)
        	throwAnalysis = Scene.v().getDefaultThrowAnalysis();
        
        if (omitExceptingUnitEdges == false)
        	omitExceptingUnitEdges = Options.v().omit_excepting_unit_edges();
                
        // Go through the definitions, building the webs
    	ExceptionalUnitGraph graph = new ExceptionalUnitGraph(body, throwAnalysis, omitExceptingUnitEdges);
 	
    	// run in panic mode on first split (maybe change this depending on the input source)
		final LocalDefs defs = LocalDefs.Factory.newLocalDefs(graph, true);
		final LocalUses uses = LocalUses.Factory.newLocalUses(graph, defs);
		
        if(Options.v().time())
            Timers.v().splitPhase1Timer.end();
        if(Options.v().time())
            Timers.v().splitPhase2Timer.start();
        
		Set<Unit> visited = new HashSet<Unit>();
        
		// Collect the set of locals that we need to split
		NumberedSet<Local> localsToSplit = new NumberedSet<Local>(Scene.v().getLocalNumberer());
		{
			NumberedSet<Local> localsVisited = new NumberedSet<Local>(Scene.v().getLocalNumberer());
	        for (Unit s : body.getUnits()) {
	            if (s.getDefBoxes().isEmpty())
	                continue;
	            if (!(s.getDefBoxes().get(0).getValue() instanceof Local))
	            	continue;
	            
	            // If we see a local the second time, we know that we must split it
	            Local l = (Local) s.getDefBoxes().get(0).getValue();
	            if (!localsVisited.add(l))
	            	localsToSplit.add(l);
	        }
		}
		
		int w = 0;
        for (Unit s : body.getUnits()) {
            if (s.getDefBoxes().isEmpty())
                continue;
            
            if (s.getDefBoxes().size() > 1)
                throw new RuntimeException("stmt with more than 1 defbox!");
            
            if (!(s.getDefBoxes().get(0).getValue() instanceof Local))
            	continue;
            
            // we don't want to visit a node twice
            if (visited.remove(s))
            	continue;
                        
            // always reassign locals to avoid "use before definition" bugs!
            // unfortunately this creates a lot of new locals, so it's important
            // to remove them afterwards
            Local oldLocal = (Local) s.getDefBoxes().get(0).getValue();  
            if (!localsToSplit.contains(oldLocal))
            	continue;
            Local newLocal = (Local) oldLocal.clone();
            
            newLocal.setName(newLocal.getName()+'#'+ ++w); // renaming should not be done here
    		body.getLocals().add(newLocal);
            
    		Deque<Unit> queue = new ArrayDeque<Unit>();
    		queue.addFirst(s);
    		do {
    			final Unit head = queue.removeFirst();
    			if (visited.add(head)) {
        			for (UnitValueBoxPair use : uses.getUsesOf(head)) {        				
        				ValueBox vb = use.valueBox;
        				Value v = vb.getValue();
        				
        				if (v == newLocal)
        					continue;
        				
        				// should always be true - but who knows ...
        				if (v instanceof Local) {
        					Local l = (Local) v;        		
        					queue.addAll(defs.getDefsOfAt(l, use.unit));
	        				vb.setValue(newLocal);
        				}    				
        			}
        			
    				for (ValueBox vb : head.getDefBoxes()) {
    					Value v = vb.getValue();
    					if (v instanceof Local) {        						
    						vb.setValue(newLocal);
    					}
    				}
    			}
    		}
    		while (!queue.isEmpty());
    		
    		// keep the set small
    		visited.remove(s);
        }

        if(Options.v().time())
            Timers.v().splitPhase2Timer.end();
        
		if (Options.v().time()) 
			Timers.v().splitTimer.end();
    }
}