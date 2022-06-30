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
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.EquivalentValue;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.DefinitionStmt;
import soot.jimple.Stmt;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.FlowSet;

// EqualLocalsAnalysis written by Richard L. Halpert, 2006-12-04
// Finds all values at the given statement from which all of the listed uses come.
public class CommonPrecedingEqualValueAnalysis extends BackwardFlowAnalysis<Unit, FlowSet<Object>> {
  private static final Logger logger = LoggerFactory.getLogger(CommonPrecedingEqualValueAnalysis.class);

  protected Map<? extends Unit, List<Object>> unitToAliasSet = null;
  protected Stmt s = null;

  public CommonPrecedingEqualValueAnalysis(UnitGraph g) {
    super(g);

    // analysis is done on-demand, not now
  }

  /** Returns a list of EquivalentLocals that must always be equal to l at s */
  public List<Object> getCommonAncestorValuesOf(Map<? extends Unit, List<Object>> unitToAliasSet, Stmt s) {
    this.unitToAliasSet = unitToAliasSet;
    this.s = s;

    doAnalysis();

    FlowSet<Object> fs = (FlowSet<Object>) getFlowAfter(s);
    List<Object> ancestorList = new ArrayList<Object>(fs.size());
    for (Object o : fs) {
      ancestorList.add(o);
    }
    return ancestorList;
  }

  @Override
  protected void merge(FlowSet<Object> in1, FlowSet<Object> in2, FlowSet<Object> out) {
    in1.intersection(in2, out);
    // in1.union(in2, out);
  }

  @Override
  protected void flowThrough(FlowSet<Object> in, Unit unit, FlowSet<Object> out) {
    in.copy(out);

    // get list of definitions at this unit
    List<EquivalentValue> newDefs = new ArrayList<EquivalentValue>();
    for (ValueBox vb : unit.getDefBoxes()) {
      newDefs.add(new EquivalentValue(vb.getValue()));
    }

    // If the local of interest was defined in this statement, then we must
    // generate a new list of aliases to it starting here
    List<Object> aliases = unitToAliasSet.get(unit);
    if (aliases != null) {
      out.clear();
      for (Object next : aliases) {
        out.add(next);
      }
    } else if (unit instanceof DefinitionStmt) {
      for (EquivalentValue ev : newDefs) {
        out.remove(ev);
        // to be smarter, we could also add the right side to the list of aliases...
      }
    }

    // logger.debug(stmt + " HAS ALIASES in=" + in + " out=" + out);
  }

  @Override
  protected void copy(FlowSet<Object> source, FlowSet<Object> dest) {
    source.copy(dest);
  }

  @Override
  protected FlowSet<Object> entryInitialFlow() {
    return new ArraySparseSet<Object>(); // should be a full set, not an empty one
  }

  @Override
  protected FlowSet<Object> newInitialFlow() {
    return new ArraySparseSet<Object>(); // should be a full set, not an empty one
  }
}
