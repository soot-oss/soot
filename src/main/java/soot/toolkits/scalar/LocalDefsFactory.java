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

import soot.Body;
import soot.Singletons;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs.FlowAnalysisMode;

/**
 * The default implementation of local defs.
 */
public class LocalDefsFactory {

  /**
   * Singleton constructor.
   *
   * @param global
   *          the singletons container. Must not be {@code null}
   * @throws NullPointerException
   *           when {@code global} argument is {@code null}
   */
  public LocalDefsFactory(Singletons.Global global) {
    if (global == null) {
      throw new NullPointerException("Cannot instantiate LocalDefsFactory with null Singletons.Global");
    }

  }

  /**
   * Creates a new LocalDefs analysis based on a {@code ExceptionalUnitGraph}
   *
   * @see soot.toolkits.graph.ExceptionalUnitGraphFactory#createExceptionalUnitGraph(Body)
   * @see soot.validation.UsesValidator
   * @param body
   * @return a new LocalDefs instance
   */
  public LocalDefs newLocalDefs(Body body) {
    return newLocalDefs(body, false);
  }

  /**
   * Creates a new LocalDefs analysis based on a {@code ExceptionalUnitGraph} If you don't trust the input you should set
   * {@code expectUndefined} to {@code true}.
   *
   * @see soot.toolkits.graph.ExceptionalUnitGraphFactory#createExceptionalUnitGraph(Body)
   * @param body
   * @param expectUndefined
   *          if you expect uses of locals that are undefined
   * @return a new LocalDefs instance
   */
  public LocalDefs newLocalDefs(Body body, boolean expectUndefined) {
    return newLocalDefs(ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body), expectUndefined);
  }

  /**
   * Creates a new LocalDefs analysis based on a given {@code UnitGraph}
   *
   * @see soot.toolkits.graph.UnitGraph#UnitGraph(Body)
   * @param graph
   *          the graph to work with
   * @return a new LocalDefs instance
   */
  public LocalDefs newLocalDefs(UnitGraph graph) {
    return newLocalDefs(graph, false);
  }

  /**
   * Creates a new LocalDefs analysis based on a given {@code UnitGraph}. If you don't trust the input you should set
   * {@code expectUndefined} to {@code true}.
   *
   * @see soot.toolkits.graph.UnitGraph#UnitGraph(Body)
   * @see soot.validation.UsesValidator
   * @param graph
   *          the graph to work with
   * @param expectUndefined
   *          if you expect uses of locals that are undefined
   * @return a new LocalDefs instance
   */
  public LocalDefs newLocalDefs(UnitGraph graph, boolean expectUndefined) {
    // return new SmartLocalDefs(graph, LiveLocals.Factory.newLiveLocals(graph));
    return new SimpleLocalDefs(graph, expectUndefined ? FlowAnalysisMode.OmitSSA : FlowAnalysisMode.Automatic);
  }

  /**
   * Creates a new LocalDefs analysis based on a given {@code UnitGraph}. This analysis will be flow-insensitive, i.e., for a
   * given local, it will always give all statements that ever write to that local regardless of potential redefinitions in
   * between.
   *
   * @see soot.toolkits.graph.UnitGraph#UnitGraph(Body)
   * @see soot.validation.UsesValidator
   * @param graph
   *          the graph to work with
   * @return a new LocalDefs instance
   */
  public LocalDefs newLocalDefsFlowInsensitive(UnitGraph graph) {
    // return new SmartLocalDefs(graph, LiveLocals.Factory.newLiveLocals(graph));
    return new SimpleLocalDefs(graph, FlowAnalysisMode.FlowInsensitive);
  }
}