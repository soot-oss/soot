package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrice Pominville, Raja Vallee-Rai
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
import soot.G;
import soot.Scene;
import soot.Singletons.Global;
import soot.options.Options;
import soot.toolkits.exceptions.ThrowAnalysis;

public class ExceptionalUnitGraphFactory {
  public ExceptionalUnitGraphFactory(Global g) {
  }

  public static ExceptionalUnitGraphFactory v() {
    return G.v().soot_toolkits_graph_ExceptionalUnitGraphFactory();
  }

  public static ExceptionalUnitGraph createExceptionalUnitGraph(Body body) {
    return v().newExceptionalUnitGraph(body, Scene.v().getDefaultThrowAnalysis(), Options.v().omit_excepting_unit_edges());
  }

  public static ExceptionalUnitGraph createExceptionalUnitGraph(Body body, ThrowAnalysis throwAnalysis) {
    return v().newExceptionalUnitGraph(body, throwAnalysis, Options.v().omit_excepting_unit_edges());
  }

  public static ExceptionalUnitGraph createExceptionalUnitGraph(Body body, ThrowAnalysis throwAnalysis,
      boolean omitExceptingUnitEdges) {
    return v().newExceptionalUnitGraph(body, throwAnalysis, omitExceptingUnitEdges);
  }

  protected ExceptionalUnitGraph newExceptionalUnitGraph(Body body, ThrowAnalysis throwAnalysis,
      boolean omitExceptingUnitEdges) {
    return new ExceptionalUnitGraph(body, throwAnalysis, omitExceptingUnitEdges);
  }

}
