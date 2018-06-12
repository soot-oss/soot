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

import soot.Context;
import soot.Kind;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;

/**
 * Represents a single context-sensitive edge in a call graph.
 *
 * @author Ondrej Lhotak
 */
public interface ContextSensitiveEdge {
  /**
   * The context at the source of the call.
   */
  public Context srcCtxt();

  /**
   * The method in which the call occurs; may be null for calls not occurring in a specific method (eg. implicit calls by the
   * VM)
   */
  public SootMethod src();

  /**
   * The unit at which the call occurs; may be null for calls not occurring at a specific statement (eg. calls in native
   * code)
   */
  public Unit srcUnit();

  public Stmt srcStmt();

  /**
   * The context at the target of the call.
   */
  public Context tgtCtxt();

  /** The target method of the call edge. */
  public SootMethod tgt();

  /**
   * The kind of edge. Note: kind should not be tested by other classes; instead, accessors such as isExplicit() should be
   * added.
   **/
  public Kind kind();
}
