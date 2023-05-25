package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.options.Options;
import soot.toolkits.graph.DominatorsFinder;
import soot.toolkits.graph.MHGDominatorsFinder;
import soot.toolkits.graph.UnitGraph;

/**
 * Find all locals guaranteed to be defined at (just before) a given program point.
 *
 * @author Navindra Umanee
 **/
public class GuaranteedDefs {
  private static final Logger logger = LoggerFactory.getLogger(GuaranteedDefs.class);

  protected final Map<Unit, List<Value>> unitToGuaranteedDefs;

  public GuaranteedDefs(UnitGraph graph) {
    if (Options.v().verbose()) {
      logger.debug("[" + graph.getBody().getMethod().getName() + "]     Constructing GuaranteedDefs...");
    }
    this.unitToGuaranteedDefs = new HashMap<Unit, List<Value>>(graph.size() * 2 + 1, 0.7f);

    GuaranteedDefsAnalysis analysis = new GuaranteedDefsAnalysis(graph);
    for (Unit s : graph) {
      FlowSet<Value> set = analysis.getFlowBefore(s);
      this.unitToGuaranteedDefs.put(s, Collections.unmodifiableList(set.toList()));
    }
  }

  /**
   * Returns a list of locals guaranteed to be defined at (just before) program point <tt>s</tt>.
   **/
  public List<Value> getGuaranteedDefs(Unit s) {
    return unitToGuaranteedDefs.get(s);
  }
}

/**
 * Flow analysis to determine all locals guaranteed to be defined at a given program point.
 **/
class GuaranteedDefsAnalysis extends ForwardFlowAnalysis<Unit, FlowSet<Value>> {
  private static final FlowSet<Value> EMPTY_SET = new ArraySparseSet<Value>();
  private final Map<Unit, FlowSet<Value>> unitToGenerateSet;

  GuaranteedDefsAnalysis(UnitGraph graph) {
    super(graph);
    this.unitToGenerateSet = new HashMap<Unit, FlowSet<Value>>(graph.size() * 2 + 1, 0.7f);

    DominatorsFinder<Unit> df = new MHGDominatorsFinder<Unit>(graph);

    // pre-compute generate sets
    for (Unit s : graph) {
      FlowSet<Value> genSet = EMPTY_SET.clone();
      for (Unit dom : df.getDominators(s)) {
        for (ValueBox box : dom.getDefBoxes()) {
          Value val = box.getValue();
          if (val instanceof Local) {
            genSet.add(val, genSet);
          }
        }
      }
      this.unitToGenerateSet.put(s, genSet);
    }

    doAnalysis();
  }

  /**
   * All INs are initialized to the empty set.
   **/
  @Override
  protected FlowSet<Value> newInitialFlow() {
    return EMPTY_SET.clone();
  }

  /**
   * IN(Start) is the empty set
   **/
  @Override
  protected FlowSet<Value> entryInitialFlow() {
    return EMPTY_SET.clone();
  }

  /**
   * OUT is the same as IN plus the genSet.
   **/
  @Override
  protected void flowThrough(FlowSet<Value> in, Unit unit, FlowSet<Value> out) {
    // perform generation (kill set is empty)
    in.union(unitToGenerateSet.get(unit), out);
  }

  /**
   * All paths == Intersection.
   **/
  @Override
  protected void merge(FlowSet<Value> in1, FlowSet<Value> in2, FlowSet<Value> out) {
    in1.intersection(in2, out);
  }

  @Override
  protected void copy(FlowSet<Value> source, FlowSet<Value> dest) {
    source.copy(dest);
  }
}
