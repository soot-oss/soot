package soot.jimple.toolkits.pointer;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2007 Patrick Lam
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import soot.Body;
import soot.Local;
import soot.RefType;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.NewExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.AbstractNewExpr;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

/**
 * LocalNotMayAliasAnalysis attempts to determine if two local variables (at two potentially different program points)
 * definitely point to different objects.
 *
 * The underlying abstraction is that of definition expressions. When a local variable gets assigned a new object (unlike
 * LocalMust, only NewExprs), the analysis tracks the source of the value. If two variables have different sources, then they
 * are different.
 *
 * See Sable TR 2007-8 for details.
 *
 * @author Patrick Lam
 */
public class LocalMustNotAliasAnalysis extends ForwardFlowAnalysis<Unit, HashMap<Local, Set<NewExpr>>> {

  @SuppressWarnings("serial")
  protected static final NewExpr UNKNOWN = new AbstractNewExpr() {
    @Override
    public String toString() {
      return "UNKNOWN";
    }

    @Override
    public Object clone() {
      return this;
    }
  };

  protected final Set<Local> locals;

  public LocalMustNotAliasAnalysis(UnitGraph g) {
    this(g, g.getBody());
  }

  public LocalMustNotAliasAnalysis(DirectedGraph<Unit> directedGraph, Body b) {
    super(directedGraph);
    this.locals = new HashSet<Local>(b.getLocals());

    doAnalysis();
  }

  @Override
  protected void merge(HashMap<Local, Set<NewExpr>> in1, HashMap<Local, Set<NewExpr>> in2, HashMap<Local, Set<NewExpr>> o) {
    for (Local l : locals) {
      Set<NewExpr> l1 = in1.get(l), l2 = in2.get(l);
      Set<NewExpr> out = o.get(l);
      out.clear();
      if (l1.contains(UNKNOWN) || l2.contains(UNKNOWN)) {
        out.add(UNKNOWN);
      } else {
        out.addAll(l1);
        out.addAll(l2);
      }
    }
  }

  @Override
  protected void flowThrough(HashMap<Local, Set<NewExpr>> in, Unit unit, HashMap<Local, Set<NewExpr>> out) {
    out.clear();
    out.putAll(in);

    if (unit instanceof DefinitionStmt) {
      DefinitionStmt ds = (DefinitionStmt) unit;
      Value lhs = ds.getLeftOp();
      if (lhs instanceof Local) {
        HashSet<NewExpr> lv = new HashSet<NewExpr>();
        out.put((Local) lhs, lv);
        Value rhs = ds.getRightOp();
        if (rhs instanceof NewExpr) {
          lv.add((NewExpr) rhs);
        } else if (rhs instanceof Local) {
          lv.addAll(in.get((Local) rhs));
        } else {
          lv.add(UNKNOWN);
        }
      }
    }
  }

  @Override
  protected void copy(HashMap<Local, Set<NewExpr>> source, HashMap<Local, Set<NewExpr>> dest) {
    dest.putAll(source);
  }

  @Override
  protected HashMap<Local, Set<NewExpr>> entryInitialFlow() {
    HashMap<Local, Set<NewExpr>> m = new HashMap<Local, Set<NewExpr>>();
    for (Local l : locals) {
      HashSet<NewExpr> s = new HashSet<NewExpr>();
      s.add(UNKNOWN);
      m.put(l, s);
    }
    return m;
  }

  @Override
  protected HashMap<Local, Set<NewExpr>> newInitialFlow() {
    HashMap<Local, Set<NewExpr>> m = new HashMap<Local, Set<NewExpr>>();
    for (Local l : locals) {
      m.put(l, new HashSet<NewExpr>());
    }
    return m;
  }

  /**
   * Returns true if this analysis has any information about local l at statement s (i.e. it is not {@link #UNKNOWN}). In
   * particular, it is safe to pass in locals/statements that are not even part of the right method. In those cases
   * <code>false</code> will be returned. Permits s to be <code>null</code>, in which case <code>false</code> will be
   * returned.
   */
  public boolean hasInfoOn(Local l, Stmt s) {
    HashMap<Local, Set<NewExpr>> flowBefore = getFlowBefore(s);
    if (flowBefore == null) {
      return false;
    } else {
      Set<NewExpr> info = flowBefore.get(l);
      return info != null && !info.contains(UNKNOWN);
    }
  }

  /**
   * @return true if values of l1 (at s1) and l2 (at s2) are known to point to different objects
   */
  public boolean notMayAlias(Local l1, Stmt s1, Local l2, Stmt s2) {
    Set<NewExpr> l1n = getFlowBefore(s1).get(l1);
    Set<NewExpr> l2n = getFlowBefore(s2).get(l2);

    if (l1n.contains(UNKNOWN) || l2n.contains(UNKNOWN)) {
      return false;
    }

    Set<NewExpr> n = new HashSet<NewExpr>(l1n);
    n.retainAll(l2n);
    return n.isEmpty();
  }

  /**
   * If the given local at the given statement was initialized with a single, concrete new-expression then the type of this
   * expression is returned. Otherwise this method returns null.
   */
  public RefType concreteType(Local l, Stmt s) {
    Set<NewExpr> set = getFlowBefore(s).get(l);
    if (set.size() != 1) {
      return null;
    } else {
      NewExpr singleNewExpr = set.iterator().next();
      return (singleNewExpr == UNKNOWN) ? null : (RefType) singleNewExpr.getType();
    }
  }
}
