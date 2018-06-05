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

import java.util.Iterator;
import java.util.Set;

import soot.Body;
import soot.Trap;
import soot.Unit;
import soot.baf.Inst;
import soot.jimple.Stmt;

/**
 * A CFG where the nodes are {@link Block} instances, and where {@link Unit}s which include array references start new
 * blocks. Exceptional control flow is ignored, so the graph will be a forest where each exception handler constitutes a
 * disjoint subgraph.
 */
public class ArrayRefBlockGraph extends BlockGraph {
  /**
   * <p>
   * Constructs an {@link ArrayRefBlockGraph} from the given {@link Body}.
   * </p>
   *
   * <p>
   * Note that this constructor builds a {@link BriefUnitGraph} internally when splitting <tt>body</tt>'s {@link Unit}s into
   * {@link Block}s. Callers who need both a {@link BriefUnitGraph} and an {@link ArrayRefBlockGraph} should use the
   * constructor taking the <tt>BriefUnitGraph</tt> as a parameter, as a minor optimization.
   * </p>
   *
   * @param the
   *          Body instance from which the graph is built.
   */
  public ArrayRefBlockGraph(Body body) {
    this(new BriefUnitGraph(body));
  }

  /**
   * Constructs an <tt>ArrayRefBlockGraph</tt> corresponding to the <tt>Unit</tt>-level control flow represented by the
   * passed {@link BriefUnitGraph}.
   *
   * @param unitGraph
   *          The <tt>BriefUnitGraph</tt> for which to build an <tt>ArrayRefBlockGraph</tt>.
   */
  public ArrayRefBlockGraph(BriefUnitGraph unitGraph) {
    super(unitGraph);

    soot.util.PhaseDumper.v().dumpGraph(this, mBody);
  }

  /**
   * <p>
   * Utility method for computing the basic block leaders for a {@link Body}, given its {@link UnitGraph} (i.e., the
   * instructions which begin new basic blocks).
   * </p>
   *
   * <p>
   * This implementation chooses as block leaders all the <tt>Unit</tt>s that {@link BlockGraph.computerLeaders()}, and adds:
   *
   * <ul>
   *
   * <li>All <tt>Unit</tt>s which contain an array reference, as defined by {@link Stmt.containsArrayRef()} and
   * {@link Inst.containsArrayRef()}.
   *
   * <li>The first <tt>Unit</tt> not covered by each {@link Trap} (i.e., the <tt>Unit</tt> returned by
   * {@link Trap.getLastUnit()}.</li>
   *
   * </ul>
   * </p>
   *
   * @param unitGraph
   *          is the <tt>Unit</tt>-level CFG which is to be split into basic blocks.
   *
   * @return the {@link Set} of {@link Unit}s in <tt>unitGraph</tt> which are block leaders.
   */
  protected Set<Unit> computeLeaders(UnitGraph unitGraph) {
    Body body = unitGraph.getBody();
    if (body != mBody) {
      throw new RuntimeException(
          "ArrayRefBlockGraph.computeLeaders() called with a UnitGraph that doesn't match its mBody.");
    }
    Set<Unit> leaders = super.computeLeaders(unitGraph);

    for (Iterator<Unit> it = body.getUnits().iterator(); it.hasNext();) {
      Unit unit = it.next();
      if (((unit instanceof Stmt) && ((Stmt) unit).containsArrayRef())
          || ((unit instanceof Inst) && ((Inst) unit).containsArrayRef())) {
        leaders.add(unit);
      }
    }
    return leaders;
  }
}
