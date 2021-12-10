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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.UnitBox;
import soot.options.Options;
import soot.util.Chain;

/**
 * <p>
 * Represents a CFG where the nodes are {@link Unit} instances and edges represent unexceptional and (possibly) exceptional
 * control flow between <tt>Unit</tt>s.
 * </p>
 *
 * <p>
 * This is an abstract class, providing the facilities used to build CFGs for specific purposes.
 * </p>
 */
public abstract class UnitGraph implements DirectedBodyGraph<Unit> {
  private static final Logger logger = LoggerFactory.getLogger(UnitGraph.class);

  protected final Body body;
  protected final Chain<Unit> unitChain;
  protected final SootMethod method;

  protected List<Unit> heads;
  protected List<Unit> tails;

  protected Map<Unit, List<Unit>> unitToSuccs;
  protected Map<Unit, List<Unit>> unitToPreds;

  /**
   * Performs the work that is required to construct any sort of <tt>UnitGraph</tt>.
   *
   * @param body
   *          The body of the method for which to construct a control flow graph.
   */
  protected UnitGraph(Body body) {
    this.body = body;
    this.unitChain = body.getUnits();
    this.method = body.getMethod();
    if (Options.v().verbose()) {
      logger.debug("[" + method.getName() + "]     Constructing " + this.getClass().getName() + "...");
    }
  }

  /**
   * Utility method for <tt>UnitGraph</tt> constructors. It computes the edges corresponding to unexceptional control flow.
   *
   * @param unitToSuccs
   *          A {@link Map} from {@link Unit}s to {@link List}s of {@link Unit}s. This is an ``out parameter''; callers must
   *          pass an empty {@link Map}. <tt>buildUnexceptionalEdges</tt> will add a mapping for every <tt>Unit</tt> in the
   *          body to a list of its unexceptional successors.
   *
   * @param unitToPreds
   *          A {@link Map} from {@link Unit}s to {@link List}s of {@link Unit}s. This is an ``out parameter''; callers must
   *          pass an empty {@link Map}. <tt>buildUnexceptionalEdges</tt> will add a mapping for every <tt>Unit</tt> in the
   *          body to a list of its unexceptional predecessors.
   */
  protected void buildUnexceptionalEdges(Map<Unit, List<Unit>> unitToSuccs, Map<Unit, List<Unit>> unitToPreds) {
    Iterator<Unit> unitIt = unitChain.iterator();
    Unit nextUnit = unitIt.hasNext() ? unitIt.next() : null;
    while (nextUnit != null) {
      Unit currentUnit = nextUnit;
      nextUnit = unitIt.hasNext() ? unitIt.next() : null;

      ArrayList<Unit> successors = new ArrayList<Unit>();

      if (currentUnit.fallsThrough()) {
        // Add the next unit as the successor
        if (nextUnit != null) {
          successors.add(nextUnit);

          List<Unit> preds = unitToPreds.get(nextUnit);
          if (preds == null) {
            preds = new ArrayList<Unit>();
            unitToPreds.put(nextUnit, preds);
          }
          preds.add(currentUnit);
        }
      }

      if (currentUnit.branches()) {
        for (UnitBox targetBox : currentUnit.getUnitBoxes()) {
          Unit target = targetBox.getUnit();
          // Arbitrary bytecode can branch to the same
          // target it falls through to, so we screen for duplicates:
          if (!successors.contains(target)) {
            successors.add(target);

            List<Unit> preds = unitToPreds.get(target);
            if (preds == null) {
              preds = new ArrayList<Unit>();
              unitToPreds.put(target, preds);
            }
            preds.add(currentUnit);
          }
        }
      }

      // Store away successors
      if (!successors.isEmpty()) {
        successors.trimToSize();
        unitToSuccs.put(currentUnit, successors);
      }
    }
  }

  /**
   * <p>
   * Utility method used in the construction of {@link UnitGraph}s, to be called only after the unitToPreds and unitToSuccs
   * maps have been built.
   * </p>
   *
   * <p>
   * <code>UnitGraph</code> provides an implementation of <code>buildHeadsAndTails()</code> which defines the graph's set of
   * heads to include the first {@link Unit} in the graph's body, together with any other <tt>Unit</tt> which has no
   * predecessors. It defines the graph's set of tails to include all <tt>Unit</tt>s with no successors. Subclasses of
   * <code>UnitGraph</code> may override this method to change the criteria for classifying a node as a head or tail.
   * </p>
   */
  protected void buildHeadsAndTails() {
    tails = new ArrayList<Unit>();
    heads = new ArrayList<Unit>();

    Unit entryPoint = null;
    if (!unitChain.isEmpty()) {
      entryPoint = unitChain.getFirst();
    }
    boolean hasEntryPoint = false;

    for (Unit s : unitChain) {
      List<Unit> succs = unitToSuccs.get(s);
      if (succs == null || succs.isEmpty()) {
        tails.add(s);
      }
      List<Unit> preds = unitToPreds.get(s);
      if (preds == null || preds.isEmpty()) {
        if (s == entryPoint) {
          hasEntryPoint = true;
        }
        heads.add(s);
      }
    }

    // Add the first Unit, even if it is the target of a branch.
    if (entryPoint != null) {
      if (!hasEntryPoint) {
        heads.add(entryPoint);
      }
    }
  }

  /**
   * Utility method that produces a new map from the {@link Unit}s of this graph's body to the union of the values stored in
   * the two argument {@link Map}s, used to combine the maps of exceptional and unexceptional predecessors and successors
   * into maps of all predecessors and successors. The values stored in both argument maps must be {@link List}s of
   * {@link Unit}s, which are assumed not to contain any duplicate <tt>Unit</tt>s.
   *
   * @param mapA
   *          The first map to be combined.
   *
   * @param mapB
   *          The second map to be combined.
   */
  protected Map<Unit, List<Unit>> combineMapValues(Map<Unit, List<Unit>> mapA, Map<Unit, List<Unit>> mapB) {
    // The duplicate screen
    Map<Unit, List<Unit>> result = new LinkedHashMap<Unit, List<Unit>>(mapA.size() * 2 + 1, 0.7f);
    for (Unit unit : unitChain) {
      List<Unit> listA = mapA.get(unit);
      if (listA == null) {
        listA = Collections.emptyList();
      }
      List<Unit> listB = mapB.get(unit);
      if (listB == null) {
        listB = Collections.emptyList();
      }

      int resultSize = listA.size() + listB.size();
      if (resultSize == 0) {
        result.put(unit, Collections.<Unit>emptyList());
      } else {
        List<Unit> resultList = new ArrayList<Unit>(resultSize);
        List<Unit> list;
        // As a minor optimization of the duplicate screening,
        // copy the longer list first.
        if (listA.size() >= listB.size()) {
          resultList.addAll(listA);
          list = listB;
        } else {
          resultList.addAll(listB);
          list = listA;
        }
        for (Unit element : list) {
          // It is possible for there to be both an exceptional
          // and an unexceptional edge connecting two Units
          // (though probably not in a class generated by
          // javac), so we need to screen for duplicates. On the
          // other hand, we expect most of these lists to have
          // only one or two elements, so it doesn't seem worth
          // the cost to build a Set to do the screening.
          if (!resultList.contains(element)) {
            resultList.add(element);
          }
        }
        result.put(unit, resultList);
      }
    }
    return result;
  }

  /**
   * Utility method for adding an edge to maps representing the CFG.
   *
   * @param unitToSuccs
   *          The {@link Map} from {@link Unit}s to {@link List}s of their successors.
   *
   * @param unitToPreds
   *          The {@link Map} from {@link Unit}s to {@link List}s of their successors.
   *
   * @param head
   *          The {@link Unit} from which the edge starts.
   *
   * @param tail
   *          The {@link Unit} to which the edge flows.
   */
  protected void addEdge(Map<Unit, List<Unit>> unitToSuccs, Map<Unit, List<Unit>> unitToPreds, Unit head, Unit tail) {
    List<Unit> headsSuccs = unitToSuccs.get(head);
    if (headsSuccs == null) {
      headsSuccs = new ArrayList<Unit>(3); // We expect this list to remain short.
      unitToSuccs.put(head, headsSuccs);
    }
    if (!headsSuccs.contains(tail)) {
      headsSuccs.add(tail);
      List<Unit> tailsPreds = unitToPreds.get(tail);
      if (tailsPreds == null) {
        tailsPreds = new ArrayList<Unit>();
        unitToPreds.put(tail, tailsPreds);
      }
      tailsPreds.add(head);
    }
  }

  /**
   * @return The body from which this UnitGraph was built.
   *
   * @see Body
   */
  @Override
  public Body getBody() {
    return body;
  }

  /**
   * Look for a path in graph, from def to use. This path has to lie inside an extended basic block (and this property
   * implies uniqueness.). The path returned includes from and to.
   *
   * @param from
   *          start point for the path.
   * @param to
   *          end point for the path.
   * @return null if there is no such path.
   */
  public List<Unit> getExtendedBasicBlockPathBetween(Unit from, Unit to) {
    // if this holds, we're doomed to failure!!!
    if (this.getPredsOf(to).size() > 1) {
      return null;
    }

    // pathStack := list of succs lists
    // pathStackIndex := last visited index in pathStack
    LinkedList<Unit> pathStack = new LinkedList<Unit>();
    LinkedList<Integer> pathStackIndex = new LinkedList<Integer>();

    pathStack.add(from);
    pathStackIndex.add(0);

    final int psiMax = this.getSuccsOf(from).size();
    int level = 0;
    while (pathStackIndex.get(0) != psiMax) {
      int p = pathStackIndex.get(level);

      List<Unit> succs = this.getSuccsOf(pathStack.get(level));
      if (p >= succs.size()) {
        // no more succs - backtrack to previous level.

        pathStack.remove(level);
        pathStackIndex.remove(level);

        level--;
        int q = pathStackIndex.get(level);
        pathStackIndex.set(level, q + 1);
        continue;
      }

      Unit betweenUnit = succs.get(p);

      // we win!
      if (betweenUnit == to) {
        pathStack.add(to);
        return pathStack;
      }

      // check preds of betweenUnit to see if we should visit its kids.
      if (this.getPredsOf(betweenUnit).size() > 1) {
        pathStackIndex.set(level, p + 1);
        continue;
      }

      // visit kids of betweenUnit.
      level++;
      pathStackIndex.add(0);
      pathStack.add(betweenUnit);
    }
    return null;
  }

  /* DirectedGraph implementation */
  @Override
  public List<Unit> getHeads() {
    return heads;
  }

  @Override
  public List<Unit> getTails() {
    return tails;
  }

  @Override
  public List<Unit> getPredsOf(Unit u) {
    List<Unit> l = unitToPreds.get(u);
    return l == null ? Collections.emptyList() : l;
  }

  @Override
  public List<Unit> getSuccsOf(Unit u) {
    List<Unit> l = unitToSuccs.get(u);
    return l == null ? Collections.emptyList() : l;
  }

  @Override
  public int size() {
    return unitChain.size();
  }

  @Override
  public Iterator<Unit> iterator() {
    return unitChain.iterator();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (Unit u : unitChain) {
      buf.append("// preds: ").append(getPredsOf(u)).append('\n');
      buf.append(u).append('\n');
      buf.append("// succs ").append(getSuccsOf(u)).append('\n');
    }
    return buf.toString();
  }
}
