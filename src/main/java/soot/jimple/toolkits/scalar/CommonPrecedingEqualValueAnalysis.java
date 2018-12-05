package soot.jimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.EquivalentValue;
import soot.ValueBox;
import soot.jimple.DefinitionStmt;
import soot.jimple.Stmt;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.FlowSet;

// EqualLocalsAnalysis written by Richard L. Halpert, 2006-12-04
// Finds all values at the given statement from which all of the listed uses
// come.

public class CommonPrecedingEqualValueAnalysis extends BackwardFlowAnalysis {
  private static final Logger logger = LoggerFactory.getLogger(CommonPrecedingEqualValueAnalysis.class);
  Map unitToAliasSet;
  Stmt s;

  public CommonPrecedingEqualValueAnalysis(UnitGraph g) {
    super(g);

    unitToAliasSet = null;
    s = null;

    // analysis is done on-demand, not now
  }

  /** Returns a list of EquivalentLocals that must always be equal to l at s */
  public List getCommonAncestorValuesOf(Map unitToAliasSet, Stmt s) {
    this.unitToAliasSet = unitToAliasSet;
    this.s = s;

    doAnalysis();

    FlowSet fs = (FlowSet) getFlowAfter(s);
    List ancestorList = new ArrayList(fs.size());
    for (Object o : fs) {
      ancestorList.add(o);
    }

    return ancestorList;
  }

  protected void merge(Object in1, Object in2, Object out) {
    FlowSet inSet1 = (FlowSet) in1;
    FlowSet inSet2 = (FlowSet) in2;
    FlowSet outSet = (FlowSet) out;

    inSet1.intersection(inSet2, outSet);
    // inSet1.union(inSet2, outSet);
  }

  protected void flowThrough(Object inValue, Object unit, Object outValue) {
    FlowSet in = (FlowSet) inValue;
    FlowSet out = (FlowSet) outValue;
    Stmt stmt = (Stmt) unit;

    in.copy(out);

    // get list of definitions at this unit
    List<EquivalentValue> newDefs = new ArrayList<EquivalentValue>();
    Iterator newDefBoxesIt = stmt.getDefBoxes().iterator();
    while (newDefBoxesIt.hasNext()) {
      newDefs.add(new EquivalentValue(((ValueBox) newDefBoxesIt.next()).getValue()));
    }

    // If the local of interest was defined in this statement, then we must
    // generate a new list of aliases to it starting here
    if (unitToAliasSet.keySet().contains(stmt)) {
      out.clear();
      List aliases = (List) unitToAliasSet.get(stmt);
      Iterator aliasIt = aliases.iterator();
      while (aliasIt.hasNext()) {
        out.add(aliasIt.next());
      }
    } else if (stmt instanceof DefinitionStmt) {
      Iterator<EquivalentValue> newDefsIt = newDefs.iterator();
      while (newDefsIt.hasNext()) {
        out.remove(newDefsIt.next());
        // to be smarter, we could also add the right side to the list of aliases...
      }
    }

    // logger.debug(""+stmt + " HAS ALIASES in" + in + " out" + out);
  }

  protected void copy(Object source, Object dest) {

    FlowSet sourceSet = (FlowSet) source;
    FlowSet destSet = (FlowSet) dest;

    sourceSet.copy(destSet);

  }

  protected Object entryInitialFlow() {
    return new ArraySparseSet(); // should be a full set, not an empty one
  }

  protected Object newInitialFlow() {
    return new ArraySparseSet(); // should be a full set, not an empty one
  }
}
