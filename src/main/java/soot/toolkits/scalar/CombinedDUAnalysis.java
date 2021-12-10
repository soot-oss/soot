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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.options.Options;
import soot.toolkits.graph.UnitGraph;
import soot.util.Cons;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

/**
 * Analysis that computes live locals, local defs, and local uses all at once.
 *
 * SA, 09.09.2014: Inefficient as hell (memory). Use the distinct analyses or fix this class before using it.
 */
public class CombinedDUAnalysis extends BackwardFlowAnalysis<Unit, FlowSet<ValueBox>> implements CombinedAnalysis {
  private static final Logger logger = LoggerFactory.getLogger(CombinedDUAnalysis.class);

  // Implementations of our interfaces...
  private final Map<Cons<Local, Unit>, List<Unit>> defsOfAt = new HashMap<Cons<Local, Unit>, List<Unit>>();
  private final Map<Unit, List<UnitValueBoxPair>> usesOf = new HashMap<Unit, List<UnitValueBoxPair>>();
  private final Map<Unit, List<Local>> liveLocalsBefore = new HashMap<Unit, List<Local>>();
  private final Map<Unit, List<Local>> liveLocalsAfter = new HashMap<Unit, List<Local>>();

  @Override
  public List<Unit> getDefsOfAt(Local l, Unit s) {
    Cons<Local, Unit> cons = new Cons<Local, Unit>(l, s);
    List<Unit> ret = defsOfAt.get(cons);
    if (ret == null) {
      defsOfAt.put(cons, ret = new ArrayList<Unit>());
    }
    return ret;
  }

  @Override
  public List<Unit> getDefsOf(Local l) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public List<UnitValueBoxPair> getUsesOf(Unit u) {
    List<UnitValueBoxPair> ret = usesOf.get(u);
    if (ret == null) {
      Local def = unitToLocalDefed.get(u);
      if (def == null) {
        usesOf.put(u, ret = Collections.emptyList());
      } else {
        usesOf.put(u, ret = new ArrayList<UnitValueBoxPair>());
        for (ValueBox vb : getFlowAfter(u)) {
          if (vb.getValue() == def) {
            ret.add(new UnitValueBoxPair(useBoxToUnit.get(vb), vb));
          }
        }
      }
    }
    return ret;
  }

  @Override
  public List<Local> getLiveLocalsBefore(Unit u) {
    List<Local> ret = liveLocalsBefore.get(u);
    if (ret == null) {
      HashSet<Local> hs = new HashSet<Local>();
      for (ValueBox vb : getFlowBefore(u)) {
        hs.add((Local) vb.getValue());
      }
      liveLocalsBefore.put(u, ret = new ArrayList<Local>(hs));
    }
    return ret;
  }

  @Override
  public List<Local> getLiveLocalsAfter(Unit u) {
    List<Local> ret = liveLocalsAfter.get(u);
    if (ret == null) {
      HashSet<Local> hs = new HashSet<Local>();
      for (ValueBox vb : getFlowAfter(u)) {
        hs.add((Local) vb.getValue());
      }
      liveLocalsAfter.put(u, ret = new ArrayList<Local>(hs));
    }
    return ret;
  }

  // The actual analysis is below.

  private final Map<ValueBox, Unit> useBoxToUnit = new HashMap<ValueBox, Unit>();
  private final Map<Unit, Local> unitToLocalDefed = new HashMap<Unit, Local>();
  private final Map<Unit, ArraySparseSet<ValueBox>> unitToLocalUseBoxes = new HashMap<Unit, ArraySparseSet<ValueBox>>();
  private final MultiMap<Value, ValueBox> localToUseBoxes = new HashMultiMap<Value, ValueBox>();

  public CombinedDUAnalysis(UnitGraph graph) {
    super(graph);
    if (Options.v().verbose()) {
      logger.debug("[" + graph.getBody().getMethod().getName() + "]     Constructing CombinedDUAnalysis...");
    }

    for (Unit u : graph) {
      List<Value> defs = localsInBoxes(u.getDefBoxes());
      switch (defs.size()) {
        case 0:
          break;
        case 1:
          unitToLocalDefed.put(u, (Local) defs.get(0));
          break;
        default:
          throw new RuntimeException("Locals defed in " + u + ": " + defs.size());
      }
      ArraySparseSet<ValueBox> localUseBoxes = new ArraySparseSet<ValueBox>();
      for (ValueBox vb : u.getUseBoxes()) {
        Value v = vb.getValue();
        if (v instanceof Local) {
          localUseBoxes.add(vb);
          if (useBoxToUnit.containsKey(vb)) {
            throw new RuntimeException("Aliased ValueBox " + vb + " in Unit " + u);
          }
          useBoxToUnit.put(vb, u);
          localToUseBoxes.put(v, vb);
        }
      }
      unitToLocalUseBoxes.put(u, localUseBoxes);
    }

    doAnalysis();

    for (Unit defUnit : graph) {
      Local localDefed = unitToLocalDefed.get(defUnit);
      if (localDefed == null) {
        continue;
      }
      for (ValueBox vb : getFlowAfter(defUnit)) {
        if (vb.getValue() != localDefed) {
          continue;
        }
        Unit useUnit = useBoxToUnit.get(vb);
        getDefsOfAt(localDefed, useUnit).add(defUnit);
      }
    }
    if (Options.v().verbose()) {
      logger.debug("[" + graph.getBody().getMethod().getName() + "]     Finished CombinedDUAnalysis...");
    }
  }

  private List<Value> localsInBoxes(List<ValueBox> boxes) {
    List<Value> ret = new ArrayList<Value>();
    for (ValueBox vb : boxes) {
      Value v = vb.getValue();
      if (!(v instanceof Local)) {
        continue;
      }
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
  protected void merge(FlowSet<ValueBox> inout, FlowSet<ValueBox> in) {
    inout.union(in);
  }

  @Override
  protected void merge(FlowSet<ValueBox> in1, FlowSet<ValueBox> in2, FlowSet<ValueBox> out) {
    in1.union(in2, out);
  }

  // STEP 5: Define flow equations.
  // in(s) = ( out(s) minus boxes(def(s)) ) union useboxes(s)
  @Override
  protected void flowThrough(FlowSet<ValueBox> out, Unit u, FlowSet<ValueBox> in) {
    Local def = unitToLocalDefed.get(u);
    out.copy(in);
    if (def != null) {
      Collection<ValueBox> boxesDefed = localToUseBoxes.get(def);
      for (ValueBox vb : in) {
        if (boxesDefed.contains(vb)) {
          in.remove(vb);
        }
      }
    }
    in.union(unitToLocalUseBoxes.get(u));
  }

  // STEP 6: Determine value for start/end node, and
  // initial approximation.
  //
  // end node: empty set
  // initial approximation: empty set
  @Override
  protected FlowSet<ValueBox> entryInitialFlow() {
    return new ArraySparseSet<ValueBox>();
  }

  @Override
  protected FlowSet<ValueBox> newInitialFlow() {
    return new ArraySparseSet<ValueBox>();
  }

  @Override
  protected void copy(FlowSet<ValueBox> source, FlowSet<ValueBox> dest) {
    source.copy(dest);
  }
}
