package soot.jimple.toolkits.ide.icfg;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import heros.SynchronizedBy;
import heros.solver.IDESolver;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import soot.Body;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.jimple.Stmt;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;

public abstract class AbstractJimpleBasedICFG implements BiDiInterproceduralCFG<Unit, SootMethod> {

  protected final boolean enableExceptions;

  @SynchronizedBy("thread-safe data structure")
  private final Map<Unit, Body> unitToOwner = createUnitToOwnerMap();

  @SynchronizedBy("by use of synchronized LoadingCache class")
  protected LoadingCache<Body, DirectedGraph<Unit>> bodyToUnitGraph
      = IDESolver.DEFAULT_CACHE_BUILDER.build(new CacheLoader<Body, DirectedGraph<Unit>>() {
        @Override
        public DirectedGraph<Unit> load(Body body) throws Exception {
          return makeGraph(body);
        }
      });

  @SynchronizedBy("by use of synchronized LoadingCache class")
  protected LoadingCache<SootMethod, List<Value>> methodToParameterRefs
      = IDESolver.DEFAULT_CACHE_BUILDER.build(new CacheLoader<SootMethod, List<Value>>() {
        @Override
        public List<Value> load(SootMethod m) throws Exception {
          return m.getActiveBody().getParameterRefs();
        }
      });

  @SynchronizedBy("by use of synchronized LoadingCache class")
  protected LoadingCache<SootMethod, Set<Unit>> methodToCallsFromWithin
      = IDESolver.DEFAULT_CACHE_BUILDER.build(new CacheLoader<SootMethod, Set<Unit>>() {
        @Override
        public Set<Unit> load(SootMethod m) throws Exception {
          return getCallsFromWithinMethod(m);
        }
      });

  public AbstractJimpleBasedICFG() {
    this(true);
  }

  /**
   * Creates a new map used for the unitToOwner map. Must be thread-safe.
   *
   * @return a new thread-safe map
   */
  protected Map<Unit, Body> createUnitToOwnerMap() {
    return new ConcurrentHashMap<>();
  }

  public AbstractJimpleBasedICFG(boolean enableExceptions) {
    this.enableExceptions = enableExceptions;
  }

  public Body getBodyOf(Unit u) {
    assert unitToOwner.containsKey(u) : "Statement " + u + " not in unit-to-owner mapping";
    Body b = unitToOwner.get(u);
    return b;
  }

  @Override
  public SootMethod getMethodOf(Unit u) {
    Body b = getBodyOf(u);
    return b == null ? null : b.getMethod();
  }

  @Override
  public List<Unit> getSuccsOf(Unit u) {
    Body body = getBodyOf(u);
    if (body == null) {
      return Collections.emptyList();
    }
    DirectedGraph<Unit> unitGraph = getOrCreateUnitGraph(body);
    return unitGraph.getSuccsOf(u);
  }

  @Override
  public DirectedGraph<Unit> getOrCreateUnitGraph(SootMethod m) {
    return getOrCreateUnitGraph(m.getActiveBody());
  }

  public DirectedGraph<Unit> getOrCreateUnitGraph(Body body) {
    return bodyToUnitGraph.getUnchecked(body);
  }

  protected DirectedGraph<Unit> makeGraph(Body body) {
    return enableExceptions ? ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body) : new BriefUnitGraph(body);
  }

  protected Set<Unit> getCallsFromWithinMethod(SootMethod m) {
    Set<Unit> res = null;
    for (Unit u : m.getActiveBody().getUnits()) {
      if (isCallStmt(u)) {
        if (res == null) {
          res = new LinkedHashSet<Unit>();
        }
        res.add(u);
      }
    }
    return res == null ? Collections.<Unit>emptySet() : res;
  }

  @Override
  public boolean isExitStmt(Unit u) {
    Body body = getBodyOf(u);
    DirectedGraph<Unit> unitGraph = getOrCreateUnitGraph(body);
    return unitGraph.getTails().contains(u);
  }

  @Override
  public boolean isStartPoint(Unit u) {
    Body body = getBodyOf(u);
    DirectedGraph<Unit> unitGraph = getOrCreateUnitGraph(body);
    return unitGraph.getHeads().contains(u);
  }

  @Override
  public boolean isFallThroughSuccessor(Unit u, Unit succ) {
    assert getSuccsOf(u).contains(succ);
    if (!u.fallsThrough()) {
      return false;
    }
    Body body = getBodyOf(u);
    return body.getUnits().getSuccOf(u) == succ;
  }

  @Override
  public boolean isBranchTarget(Unit u, Unit succ) {
    assert getSuccsOf(u).contains(succ);
    if (!u.branches()) {
      return false;
    }
    for (UnitBox ub : u.getUnitBoxes()) {
      if (ub.getUnit() == succ) {
        return true;
      }
    }
    return false;
  }

  @Override
  public List<Value> getParameterRefs(SootMethod m) {
    return methodToParameterRefs.getUnchecked(m);
  }

  @Override
  public Collection<Unit> getStartPointsOf(SootMethod m) {
    if (m.hasActiveBody()) {
      Body body = m.getActiveBody();
      DirectedGraph<Unit> unitGraph = getOrCreateUnitGraph(body);
      return unitGraph.getHeads();
    }
    return Collections.emptySet();
  }

  public boolean setOwnerStatement(Unit u, Body b) {
    return unitToOwner.put(u, b) == null;
  }

  @Override
  public boolean isCallStmt(Unit u) {
    return ((Stmt) u).containsInvokeExpr();
  }

  @Override
  public Set<Unit> allNonCallStartNodes() {
    Set<Unit> res = new LinkedHashSet<Unit>(unitToOwner.keySet());
    for (Iterator<Unit> iter = res.iterator(); iter.hasNext();) {
      Unit u = iter.next();
      if (isStartPoint(u) || isCallStmt(u)) {
        iter.remove();
      }
    }
    return res;
  }

  @Override
  public Set<Unit> allNonCallEndNodes() {
    Set<Unit> res = new LinkedHashSet<Unit>(unitToOwner.keySet());
    for (Iterator<Unit> iter = res.iterator(); iter.hasNext();) {
      Unit u = iter.next();
      if (isExitStmt(u) || isCallStmt(u)) {
        iter.remove();
      }
    }
    return res;
  }

  @Override
  public Collection<Unit> getReturnSitesOfCallAt(Unit u) {
    return getSuccsOf(u);
  }

  @Override
  public Set<Unit> getCallsFromWithin(SootMethod m) {
    return methodToCallsFromWithin.getUnchecked(m);
  }

  public void initializeUnitToOwner(SootMethod m) {
    if (m.hasActiveBody()) {
      Body b = m.getActiveBody();
      initializeUnitToOwner(b);
    }
  }

  public void initializeUnitToOwner(Body b) {
    PatchingChain<Unit> units = b.getUnits();
    for (Unit unit : units) {
      unitToOwner.put(unit, b);
    }
  }

  @Override
  public List<Unit> getPredsOf(Unit u) {
    assert u != null;
    Body body = getBodyOf(u);
    if (body == null) {
      return Collections.emptyList();
    }
    DirectedGraph<Unit> unitGraph = getOrCreateUnitGraph(body);
    return unitGraph.getPredsOf(u);
  }

  @Override
  public Collection<Unit> getEndPointsOf(SootMethod m) {
    if (m.hasActiveBody()) {
      Body body = m.getActiveBody();
      DirectedGraph<Unit> unitGraph = getOrCreateUnitGraph(body);
      return unitGraph.getTails();
    }
    return Collections.emptySet();
  }

  @Override
  public List<Unit> getPredsOfCallAt(Unit u) {
    return getPredsOf(u);
  }

  @Override
  public boolean isReturnSite(Unit n) {
    for (Unit pred : getPredsOf(n)) {
      if (isCallStmt(pred)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isReachable(Unit u) {
    return unitToOwner.containsKey(u);
  }

}
