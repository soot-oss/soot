package soot.jimple.spark.pag;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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

import java.util.HashMap;
import java.util.Map;

import soot.SootMethod;
import soot.Type;

/**
 * Represents a simple variable node (Green) in the pointer assignment graph that is specific to a particular method
 * invocation.
 *
 * @author Ondrej Lhotak
 */
public class LocalVarNode extends VarNode {
  public ContextVarNode context(Object context) {
    return cvns == null ? null : cvns.get(context);
  }

  public SootMethod getMethod() {
    return method;
  }

  public String toString() {
    return "LocalVarNode " + getNumber() + " " + variable + " " + method;
  }
  /* End of public methods. */

  LocalVarNode(PAG pag, Object variable, Type t, SootMethod m) {
    super(pag, variable, t);
    this.method = m;
    // if( m == null ) throw new RuntimeException( "method shouldn't be null" );
  }

  /** Registers a cvn as having this node as its base. */
  void addContext(ContextVarNode cvn, Object context) {
    if (cvns == null) {
      cvns = new HashMap<Object, ContextVarNode>();
    }
    cvns.put(context, cvn);
  }

  /* End of package methods. */

  protected Map<Object, ContextVarNode> cvns;
  protected SootMethod method;
}
