package soot.jimple.spark.geom.geomPA;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2011 Richard Xiao
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

import soot.jimple.spark.pag.Node;

/**
 * An abstract class for hiding different encoding methods, e.g. Geom, HeapIns, PtIns.
 * 
 * @author xiao
 *
 */
public abstract class IEncodingBroker {
  /**
   * Generate a node of proper kind.
   * 
   * @param v
   * @return
   */
  public abstract IVarAbstraction generateNode(Node v);

  /**
   * Build the initial encoding of the pointer assignments and points-to facts.
   */
  public abstract void initFlowGraph(GeomPointsTo ptAnalyzer);

  /**
   * Return the signature of the implemented sub-class, may be useful in somewhere.
   * 
   * @return
   */
  public abstract String getSignature();
}
