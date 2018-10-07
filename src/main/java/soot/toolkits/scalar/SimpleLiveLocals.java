package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Local;
import soot.Timers;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

/**
 * Analysis that provides an implementation of the LiveLocals interface.
 */
public class SimpleLiveLocals implements LiveLocals {
  private static final Logger logger = LoggerFactory.getLogger(SimpleLiveLocals.class);
  final FlowAnalysis<Unit, FlowSet<Local>> analysis;

  /**
   * Computes the analysis given a UnitGraph computed from a method body. It is recommended that a ExceptionalUnitGraph (or
   * similar) be provided for correct results in the case of exceptional control flow.
   *
   * @param graph
   *          a graph on which to compute the analysis.
   *
   * @see ExceptionalUnitGraph
   */
  public SimpleLiveLocals(UnitGraph graph) {
    if (Options.v().time()) {
      Timers.v().liveTimer.start();
    }

    if (Options.v().verbose()) {
      logger.debug("[" + graph.getBody().getMethod().getName() + "]     Constructing SimpleLiveLocals...");
    }

    analysis = new Analysis(graph);

    if (Options.v().time()) {
      Timers.v().liveAnalysisTimer.start();
    }

    analysis.doAnalysis();

    if (Options.v().time()) {
      Timers.v().liveAnalysisTimer.end();
    }

    if (Options.v().time()) {
      Timers.v().liveTimer.end();
    }
  }

  public List<Local> getLiveLocalsAfter(Unit s) {
    // ArraySparseSet returns a unbacked list of elements!
    return analysis.getFlowAfter(s).toList();
  }

  public List<Local> getLiveLocalsBefore(Unit s) {
    // ArraySparseSet returns a unbacked list of elements!
    return analysis.getFlowBefore(s).toList();
  }

  static class Analysis extends BackwardFlowAnalysis<Unit, FlowSet<Local>> {
    Analysis(UnitGraph g) {
      super(g);
    }

    @Override
    protected FlowSet<Local> newInitialFlow() {
      return new ArraySparseSet<Local>();
    }

    @Override
    protected void flowThrough(FlowSet<Local> in, Unit unit, FlowSet<Local> out) {
      in.copy(out);

      // Perform kill
      for (ValueBox box : unit.getDefBoxes()) {
        Value v = box.getValue();
        if (v instanceof Local) {
          Local l = (Local) v;
          out.remove(l);
        }
      }

      // Perform generation
      for (ValueBox box : unit.getUseBoxes()) {
        Value v = box.getValue();
        if (v instanceof Local) {
          Local l = (Local) v;
          out.add(l);
        }
      }
    }

    @Override
    protected void mergeInto(Unit succNode, FlowSet<Local> inout, FlowSet<Local> in) {
      inout.union(in);
    }

    @Override
    protected void merge(FlowSet<Local> in1, FlowSet<Local> in2, FlowSet<Local> out) {
      in1.union(in2, out);
    }

    @Override
    protected void copy(FlowSet<Local> source, FlowSet<Local> dest) {
      source.copy(dest);
    }
  }
}
