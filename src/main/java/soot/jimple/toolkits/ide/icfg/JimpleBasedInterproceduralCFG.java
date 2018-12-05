package soot.jimple.toolkits.ide.icfg;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2013 Eric Bodden and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import heros.DontSynchronize;
import heros.InterproceduralCFG;
import heros.SynchronizedBy;
import heros.ThreadSafe;
import heros.solver.IDESolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * Default implementation for the {@link InterproceduralCFG} interface. Includes all statements reachable from
 * {@link Scene#getEntryPoints()} through explicit call statements or through calls to {@link Thread#start()}.
 *
 * This class is designed to be thread safe, and subclasses of this class must be designed in a thread-safe way, too.
 */
@ThreadSafe
public class JimpleBasedInterproceduralCFG extends AbstractJimpleBasedICFG {

  protected static final Logger logger = LoggerFactory.getLogger(IDESolver.class);

  protected boolean includeReflectiveCalls = false;
  protected boolean includePhantomCallees = false;

  // retains only callers that are explicit call sites or Thread.start()
  public class EdgeFilter extends Filter {
    protected EdgeFilter() {
      super(new EdgePredicate() {
        @Override
        public boolean want(Edge e) {
          return e.kind().isExplicit() || e.kind().isThread() || e.kind().isExecutor() || e.kind().isAsyncTask()
              || e.kind().isClinit() || e.kind().isPrivileged() || (includeReflectiveCalls && e.kind().isReflection());
        }
      });
    }
  }

  @DontSynchronize("readonly")
  protected final CallGraph cg;

  protected CacheLoader<Unit, Collection<SootMethod>> loaderUnitToCallees = new CacheLoader<Unit, Collection<SootMethod>>() {
    @Override
    public Collection<SootMethod> load(Unit u) throws Exception {
      ArrayList<SootMethod> res = null;
      // only retain callers that are explicit call sites or
      // Thread.start()
      Iterator<Edge> edgeIter = new EdgeFilter().wrap(cg.edgesOutOf(u));
      while (edgeIter.hasNext()) {
        Edge edge = edgeIter.next();
        SootMethod m = edge.getTgt().method();
        if (includePhantomCallees || m.hasActiveBody()) {
          if (res == null) {
            res = new ArrayList<SootMethod>();
          }
          res.add(m);
        } else if (IDESolver.DEBUG) {
          logger.error(String.format("Method %s is referenced but has no body!", m.getSignature(), new Exception()));
        }
      }

      if (res != null) {
        res.trimToSize();
        return res;
      } else {
        return Collections.emptySet();
      }
    }
  };

  @SynchronizedBy("by use of synchronized LoadingCache class")
  protected final LoadingCache<Unit, Collection<SootMethod>> unitToCallees
      = IDESolver.DEFAULT_CACHE_BUILDER.build(loaderUnitToCallees);

  protected CacheLoader<SootMethod, Collection<Unit>> loaderMethodToCallers
      = new CacheLoader<SootMethod, Collection<Unit>>() {
        @Override
        public Collection<Unit> load(SootMethod m) throws Exception {
          ArrayList<Unit> res = new ArrayList<Unit>();
          // only retain callers that are explicit call sites or
          // Thread.start()
          Iterator<Edge> edgeIter = new EdgeFilter().wrap(cg.edgesInto(m));
          while (edgeIter.hasNext()) {
            Edge edge = edgeIter.next();
            res.add(edge.srcUnit());
          }
          res.trimToSize();
          return res;
        }
      };
  @SynchronizedBy("by use of synchronized LoadingCache class")
  protected final LoadingCache<SootMethod, Collection<Unit>> methodToCallers
      = IDESolver.DEFAULT_CACHE_BUILDER.build(loaderMethodToCallers);

  public JimpleBasedInterproceduralCFG() {
    this(true);
  }

  public JimpleBasedInterproceduralCFG(boolean enableExceptions) {
    this(enableExceptions, false);
  }

  public JimpleBasedInterproceduralCFG(boolean enableExceptions, boolean includeReflectiveCalls) {
    super(enableExceptions);
    this.includeReflectiveCalls = includeReflectiveCalls;

    cg = Scene.v().getCallGraph();
    initializeUnitToOwner();
  }

  protected void initializeUnitToOwner() {
    for (Iterator<MethodOrMethodContext> iter = Scene.v().getReachableMethods().listener(); iter.hasNext();) {
      SootMethod m = iter.next().method();
      initializeUnitToOwner(m);
    }
  }

  public void initializeUnitToOwner(SootMethod m) {
    if (m.hasActiveBody()) {
      Body b = m.getActiveBody();
      PatchingChain<Unit> units = b.getUnits();
      for (Unit unit : units) {
        unitToOwner.put(unit, b);
      }
    }
  }

  @Override
  public Collection<SootMethod> getCalleesOfCallAt(Unit u) {
    return unitToCallees.getUnchecked(u);
  }

  @Override
  public Collection<Unit> getCallersOf(SootMethod m) {
    return methodToCallers.getUnchecked(m);
  }

  /**
   * Sets whether methods that operate on the callgraph shall also return phantom methods as potential callees
   *
   * @param includePhantomCallees
   *          True if phantom methods shall be returned as potential callees, otherwise false
   */
  public void setIncludePhantomCallees(boolean includePhantomCallees) {
    this.includePhantomCallees = includePhantomCallees;
  }

}
