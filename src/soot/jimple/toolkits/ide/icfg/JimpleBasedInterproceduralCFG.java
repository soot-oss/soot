/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2013 Eric Bodden and others
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
package soot.jimple.toolkits.ide.icfg;

import heros.DontSynchronize;
import heros.InterproceduralCFG;
import heros.SynchronizedBy;
import heros.ThreadSafe;
import heros.solver.IDESolver;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import soot.Body;
import soot.MethodOrMethodContext;
import soot.PatchingChain;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.EdgePredicate;
import soot.jimple.toolkits.callgraph.Filter;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;


/**
 * Default implementation for the {@link InterproceduralCFG} interface.
 * Includes all statements reachable from {@link Scene#getEntryPoints()} through
 * explicit call statements or through calls to {@link Thread#start()}.
 * 
 * This class is designed to be thread safe, and subclasses of this class must be designed
 * in a thread-safe way, too.
 */
@ThreadSafe
public class JimpleBasedInterproceduralCFG extends AbstractJimpleBasedICFG {
	
	//retains only callers that are explicit call sites or Thread.start()
	public static class EdgeFilter extends Filter {		
		protected EdgeFilter() {
			super(new EdgePredicate() {
				@Override
				public boolean want(Edge e) {				
					return e.kind().isExplicit() || e.kind().isThread() || e.kind().isAsyncTask()
							|| e.kind().isClinit();
				}
			});
		}
	}
	
	@DontSynchronize("readonly")
	protected final CallGraph cg;
	
	@SynchronizedBy("by use of synchronized LoadingCache class")
	protected final LoadingCache<Unit,Set<SootMethod>> unitToCallees =
			IDESolver.DEFAULT_CACHE_BUILDER.build( new CacheLoader<Unit,Set<SootMethod>>() {
				@Override
				public Set<SootMethod> load(Unit u) throws Exception {
					Set<SootMethod> res = new LinkedHashSet<SootMethod>();
					//only retain callers that are explicit call sites or Thread.start()
					Iterator<Edge> edgeIter = new EdgeFilter().wrap(cg.edgesOutOf(u));					
					while(edgeIter.hasNext()) {
						Edge edge = edgeIter.next();
						SootMethod m = edge.getTgt().method();
						if(m.hasActiveBody())
							res.add(m);
						else if(IDESolver.DEBUG) 
							System.err.println("Method "+m.getSignature()+" is referenced but has no body!");
					}
					return res; 
				}
			});

	@SynchronizedBy("by use of synchronized LoadingCache class")
	protected final LoadingCache<SootMethod,Set<Unit>> methodToCallers =
			IDESolver.DEFAULT_CACHE_BUILDER.build( new CacheLoader<SootMethod,Set<Unit>>() {
				@Override
				public Set<Unit> load(SootMethod m) throws Exception {
					Set<Unit> res = new LinkedHashSet<Unit>();					
					//only retain callers that are explicit call sites or Thread.start()
					Iterator<Edge> edgeIter = new EdgeFilter().wrap(cg.edgesInto(m));					
					while(edgeIter.hasNext()) {
						Edge edge = edgeIter.next();
						res.add(edge.srcUnit());			
					}
					return res;
				}
			});

	public JimpleBasedInterproceduralCFG() {
		cg = Scene.v().getCallGraph();		
		initializeUnitToOwner();
	}

	protected void initializeUnitToOwner() {
		for(Iterator<MethodOrMethodContext> iter = Scene.v().getReachableMethods().listener(); iter.hasNext(); ) {
			SootMethod m = iter.next().method();
			if(m.hasActiveBody()) {
				Body b = m.getActiveBody();
				PatchingChain<Unit> units = b.getUnits();
				for (Unit unit : units) {
					unitToOwner.put(unit, b);
				}
			}
		}
	}

	@Override
	public Set<SootMethod> getCalleesOfCallAt(Unit u) {
		return unitToCallees.getUnchecked(u);
	}

	@Override
	public Set<Unit> getCallersOf(SootMethod m) {
		return methodToCallers.getUnchecked(m);
	}
	
}
