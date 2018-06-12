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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Local;
import soot.Unit;
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
  protected Map<Unit, List> unitToGuaranteedDefs;

  public GuaranteedDefs(UnitGraph graph) {
    if (Options.v().verbose()) {
      logger.debug("[" + graph.getBody().getMethod().getName() + "]     Constructing GuaranteedDefs...");
    }

    GuaranteedDefsAnalysis analysis = new GuaranteedDefsAnalysis(graph);

    // build map
    {
      unitToGuaranteedDefs = new HashMap<Unit, List>(graph.size() * 2 + 1, 0.7f);
      Iterator<Unit> unitIt = graph.iterator();

      while (unitIt.hasNext()) {
        Unit s = (Unit) unitIt.next();
        FlowSet set = (FlowSet) analysis.getFlowBefore(s);
        unitToGuaranteedDefs.put(s, Collections.unmodifiableList(set.toList()));
      }
    }
  }

  /**
   * Returns a list of locals guaranteed to be defined at (just before) program point <tt>s</tt>.
   **/
  public List getGuaranteedDefs(Unit s) {
    return unitToGuaranteedDefs.get(s);
  }
}

/**
 * Flow analysis to determine all locals guaranteed to be defined at a given program point.
 **/
class GuaranteedDefsAnalysis extends ForwardFlowAnalysis {
  FlowSet emptySet = new ArraySparseSet();
  Map<Unit, FlowSet> unitToGenerateSet;

  GuaranteedDefsAnalysis(UnitGraph graph) {
    super(graph);
    DominatorsFinder df = new MHGDominatorsFinder(graph);
    unitToGenerateSet = new HashMap<Unit, FlowSet>(graph.size() * 2 + 1, 0.7f);

    // pre-compute generate sets
    for (Iterator unitIt = graph.iterator(); unitIt.hasNext();) {
      Unit s = (Unit) unitIt.next();
      FlowSet genSet = emptySet.clone();

      for (Iterator domsIt = df.getDominators(s).iterator(); domsIt.hasNext();) {
        Unit dom = (Unit) domsIt.next();
        for (Iterator boxIt = dom.getDefBoxes().iterator(); boxIt.hasNext();) {
          ValueBox box = (ValueBox) boxIt.next();
          if (box.getValue() instanceof Local) {
            genSet.add(box.getValue(), genSet);
          }
        }
      }

      unitToGenerateSet.put(s, genSet);
    }

    doAnalysis();
  }

  /**
   * All INs are initialized to the empty set.
   **/
  protected Object newInitialFlow() {
    return emptySet.clone();
  }

  /**
   * IN(Start) is the empty set
   **/
  protected Object entryInitialFlow() {
    return emptySet.clone();
  }

  /**
   * OUT is the same as IN plus the genSet.
   **/
  protected void flowThrough(Object inValue, Object unit, Object outValue) {
    FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

    // perform generation (kill set is empty)
    in.union(unitToGenerateSet.get(unit), out);
  }

  /**
   * All paths == Intersection.
   **/
  protected void merge(Object in1, Object in2, Object out) {
    FlowSet inSet1 = (FlowSet) in1, inSet2 = (FlowSet) in2, outSet = (FlowSet) out;

    inSet1.intersection(inSet2, outSet);
  }

  protected void copy(Object source, Object dest) {
    FlowSet sourceSet = (FlowSet) source, destSet = (FlowSet) dest;

    sourceSet.copy(destSet);
  }
}
