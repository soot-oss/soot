package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Ondrej Lhotak
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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Local;
import soot.Timers;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraph.ExceptionDest;
import soot.toolkits.graph.UnitGraph;
import soot.util.Cons;
import soot.util.LocalBitSetPacker;

/**
 * Analysis that provides an implementation of the LocalDefs interface.
 *
 * This Analysis calculates only the definitions of the locals used by a unit. If you need all definitions of local you
 * should use {@see SimpleLocalDefs}.
 *
 * Be warned: This implementation requires a lot of memory and CPU time, normally {@see SimpleLocalDefs} is much faster.
 */
public class SmartLocalDefs implements LocalDefs {
  private static final Logger logger = LoggerFactory.getLogger(SmartLocalDefs.class);
  private final Map<Cons<Unit, Local>, List<Unit>> answer;

  private final Map<Local, Set<Unit>> localToDefs; // for each local, set of units where it's defined
  private final UnitGraph graph;
  private final LocalDefsAnalysis analysis;
  private final Map<Unit, BitSet> liveLocalsAfter;

  public void printAnswer() {
    System.out.println(answer.toString());
  }

  /**
   * Intersects 2 sets and returns the result as a list
   *
   * @param a
   * @param b
   * @return
   */
  private static <T> List<T> asList(Set<T> a, Set<T> b) {
    if (a == null || b == null || a.isEmpty() || b.isEmpty()) {
      return Collections.<T>emptyList();
    }

    if (a.size() < b.size()) {
      List<T> c = new ArrayList<T>(a);
      c.retainAll(b);
      return c;
    } else {
      List<T> c = new ArrayList<T>(b);
      c.retainAll(a);
      return c;
    }
  }

  public SmartLocalDefs(UnitGraph g, LiveLocals live) {
    this.graph = g;

    if (Options.v().time()) {
      Timers.v().defsTimer.start();
    }

    if (Options.v().verbose()) {
      logger.debug("[" + g.getBody().getMethod().getName() + "]     Constructing SmartLocalDefs...");
    }

    final LocalBitSetPacker localPacker = new LocalBitSetPacker(g.getBody());
    localPacker.pack();

    localToDefs = new HashMap<Local, Set<Unit>>();
    liveLocalsAfter = new HashMap<Unit, BitSet>();
    for (Unit u : graph) {
      // translate locals to bits
      BitSet set = new BitSet(localPacker.getLocalCount());
      for (Local l : live.getLiveLocalsAfter(u)) {
        set.set(l.getNumber());
      }
      liveLocalsAfter.put(u, set);

      Local l = localDef(u);
      if (l == null) {
        continue;
      }

      addDefOf(l, u);
    }

    if (Options.v().verbose()) {
      logger.debug("[" + g.getBody().getMethod().getName() + "]        done localToDefs map...");
    }

    if (Options.v().verbose()) {
      logger.debug("[" + g.getBody().getMethod().getName() + "]        done unitToMask map...");
    }

    analysis = new LocalDefsAnalysis(graph);

    answer = new HashMap<Cons<Unit, Local>, List<Unit>>();
    for (Unit u : graph) {
      Set<Unit> s1 = analysis.getFlowBefore(u);
      if (s1 == null || s1.isEmpty()) {
        continue;
      }

      for (ValueBox vb : u.getUseBoxes()) {
        Value v = vb.getValue();
        if (v instanceof Local) {
          Local l = (Local) v;

          Set<Unit> s2 = defsOf(l);
          if (s2 == null || s2.isEmpty()) {
            continue;
          }

          List<Unit> lst = asList(s1, s2);
          if (lst.isEmpty()) {
            continue;
          }

          Cons<Unit, Local> key = new Cons<Unit, Local>(u, l);
          if (!answer.containsKey(key)) {
            answer.put(key, lst);
          }
        }
      }
    }

    localPacker.unpack();

    if (Options.v().time()) {
      Timers.v().defsTimer.end();
    }

    if (Options.v().verbose()) {
      logger.debug("[" + g.getBody().getMethod().getName() + "]     SmartLocalDefs finished.");
    }
  }

  private Local localDef(Unit u) {
    List<ValueBox> defBoxes = u.getDefBoxes();
    int size = defBoxes.size();
    if (size == 0) {
      return null;
    }
    if (size != 1) {
      throw new RuntimeException();
    }
    ValueBox vb = defBoxes.get(0);
    Value v = vb.getValue();
    if (!(v instanceof Local)) {
      return null;
    }
    return (Local) v;
  }

  private Set<Unit> defsOf(Local l) {
    Set<Unit> s = localToDefs.get(l);
    if (s == null) {
      return Collections.emptySet();
    }
    return s;
  }

  private void addDefOf(Local l, Unit u) {
    Set<Unit> s = localToDefs.get(l);
    if (s == null) {
      localToDefs.put(l, s = new HashSet<Unit>());
    }
    s.add(u);
  }

  class LocalDefsAnalysis extends ForwardFlowAnalysisExtended<Unit, Set<Unit>> {
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
    protected void flowThrough(Set<Unit> in, Unit u, Unit succ, Set<Unit> out) {
      final ExceptionalUnitGraph exGraph = graph instanceof ExceptionalUnitGraph ? (ExceptionalUnitGraph) graph : null;
      out.clear();

      BitSet liveLocals = liveLocalsAfter.get(u);
      Local l = localDef(u);

      if (l == null) { // add all units contained in mask
        for (Unit inU : in) {
          if (liveLocals.get(localDef(inU).getNumber())) {
            out.add(inU);
          }
        }
      } else { // check unit whether contained in allDefUnits before add
        // into out set.
        Set<Unit> allDefUnits = defsOf(l);

        boolean isExceptionalTarget = false;
        if (exGraph != null) {
          for (ExceptionDest ed : exGraph.getExceptionDests(u)) {
            if (ed.getTrap() != null && ed.getTrap().getHandlerUnit() == succ) {
              isExceptionalTarget = true;
            }
          }
        }

        for (Unit inU : in) {
          if (liveLocals.get(localDef(inU).getNumber())) {
            // If we have a = foo and foo can throw an exception, we
            // must keep the old definition of a.
            if (isExceptionalTarget || !allDefUnits.contains(inU)) {
              out.add(inU);
            }
          }
        }

        assert isExceptionalTarget || !out.removeAll(allDefUnits);

        if (liveLocals.get(l.getNumber())) {
          if (!isExceptionalTarget) {
            out.add(u);
          }
        }
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
      return new HashSet<Unit>();
    }
  }

  @Override
  public List<Unit> getDefsOfAt(Local l, Unit s) {
    List<Unit> lst = answer.get(new Cons<Unit, Local>(s, l));
    if (lst == null) {
      return Collections.emptyList();
    }
    return lst;
  }

  @Override
  public List<Unit> getDefsOf(Local l) {
    List<Unit> result = new ArrayList<Unit>();
    for (Cons<Unit, Local> cons : answer.keySet()) {
      if (cons.cdr() == l) {
        result.addAll(answer.get(cons));
      }
    }
    return result;
  }

  /**
   * Returns the associated unit graph.
   */
  public UnitGraph getGraph() {
    return graph;
  }

}
