package soot.jimple.toolkits.pointer;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.Local;
import soot.RefLikeType;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Stmt;
import soot.toolkits.graph.StronglyConnectedComponentsFast;
import soot.toolkits.graph.UnitGraph;

/**
 * A special version of the local must-alias analysis that takes redefinitions within loops into account. For variable that
 * is redefined in a loop, the must-alias information is invalidated and set to {@link LocalMustAliasAnalysis#UNKNOWN}. E.g.
 * assume the following example: <code>
 * while(..) {
 *   c = foo();        //(1)
 *   c.doSomething();  //(2)
 * }
 * </code>
 *
 * While it is certainly true that c at (2) must-alias c at (1) (they have the same value number), it is also true that in a
 * second iteration, c at (2) may not alias the previous c at (2).
 *
 * @author Eric Bodden
 */
public class StrongLocalMustAliasAnalysis extends LocalMustAliasAnalysis {

  protected Set<Integer> invalidInstanceKeys;

  public StrongLocalMustAliasAnalysis(UnitGraph g) {
    super(g);
    invalidInstanceKeys = new HashSet<Integer>();
    /*
     * Find all SCCs, then invalidate all instance keys for variable defined within an SCC.
     */
    StronglyConnectedComponentsFast<Unit> sccAnalysis = new StronglyConnectedComponentsFast<Unit>(g);
    for (List<Unit> scc : sccAnalysis.getTrueComponents()) {
      for (Unit unit : scc) {
        for (ValueBox vb : unit.getDefBoxes()) {
          Value defValue = vb.getValue();
          if (defValue instanceof Local) {
            Local defLocal = (Local) defValue;
            if (defLocal.getType() instanceof RefLikeType) {
              Object instanceKey = getFlowBefore(unit).get(defLocal);
              // if key is not already UNKNOWN
              if (instanceKey instanceof Integer) {
                Integer intKey = (Integer) instanceKey;
                invalidInstanceKeys.add(intKey);
              }
              instanceKey = getFlowAfter(unit).get(defLocal);
              // if key is not already UNKNOWN
              if (instanceKey instanceof Integer) {
                Integer intKey = (Integer) instanceKey;
                invalidInstanceKeys.add(intKey);
              }
            }
          }
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean mustAlias(Local l1, Stmt s1, Local l2, Stmt s2) {
    Object l1n = getFlowBefore(s1).get(l1);
    Object l2n = getFlowBefore(s2).get(l2);

    if (l1n == null || l2n == null || invalidInstanceKeys.contains(l1n) || invalidInstanceKeys.contains(l2n)) {
      return false;
    }

    return l1n == l2n;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String instanceKeyString(Local l, Stmt s) {
    Object ln = getFlowBefore(s).get(l);
    if (invalidInstanceKeys.contains(ln)) {
      return "UNKNOWN";
    }
    return super.instanceKeyString(l, s);
  }

}
