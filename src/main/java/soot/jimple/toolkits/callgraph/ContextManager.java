package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import soot.Context;
import soot.Kind;
import soot.MethodOrMethodContext;
import soot.SootMethod;
import soot.Unit;

/**
 * Interface for context managers, which decide how edges should be added to a context-sensitive call graph.
 * 
 * @author Ondrej Lhotak
 */
public interface ContextManager {
  public void addStaticEdge(MethodOrMethodContext src, Unit srcUnit, SootMethod target, Kind kind);

  public void addVirtualEdge(MethodOrMethodContext src, Unit srcUnit, SootMethod target, Kind kind, Context typeContext);

  public CallGraph callGraph();
}
