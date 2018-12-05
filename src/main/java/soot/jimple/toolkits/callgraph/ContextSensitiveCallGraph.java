package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Ondrej Lhotak
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

import java.util.Iterator;

import soot.Context;
import soot.SootMethod;
import soot.Unit;

/**
 * Represents a context-sensitive call graph for querying by client analyses.
 * 
 * @author Ondrej Lhotak
 */
public interface ContextSensitiveCallGraph {
  /**
   * Returns all MethodOrMethodContext's (context,method pairs) that are the source of some edge.
   */
  public Iterator edgeSources();

  /**
   * Returns all ContextSensitiveEdge's in the call graph.
   */
  public Iterator allEdges();

  /**
   * Returns all ContextSensitiveEdge's out of unit srcUnit in method src in context srcCtxt.
   */
  public Iterator edgesOutOf(Context srcCtxt, SootMethod src, Unit srcUnit);

  /**
   * Returns all ContextSensitiveEdge's out of method src in context srcCtxt.
   */
  public Iterator edgesOutOf(Context srcCtxt, SootMethod src);

  /**
   * Returns all ContextSensitiveEdge's into method tgt in context tgtCtxt.
   */
  public Iterator edgesInto(Context tgtCtxt, SootMethod tgt);
}
