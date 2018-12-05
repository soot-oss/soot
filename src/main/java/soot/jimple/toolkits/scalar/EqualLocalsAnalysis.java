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

import soot.EquivalentValue;
import soot.Local;
import soot.Value;
import soot.ValueBox;
import soot.jimple.DefinitionStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.Stmt;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

// EqualLocalsAnalysis written by Richard L. Halpert, 2006-12-04
// Finds equal/equavalent/aliasing locals to a given local at a given statement, on demand
// The answer provided is occasionally suboptimal (but correct) in the event where
// a _re_definition of the given local causes it to become equal to existing locals.

public class EqualLocalsAnalysis extends ForwardFlowAnalysis {
  Local l;
  Stmt s;

  public EqualLocalsAnalysis(UnitGraph g) {
    super(g);

    l = null;
    s = null;

    // analysis is done on-demand, not now
  }

  /** Returns a list of EquivalentValue wrapped Locals and Refs that must always be equal to l at s */
  public List getCopiesOfAt(Local l, Stmt s) {
    this.l = l;
    this.s = s;

    doAnalysis();

    FlowSet fs = (FlowSet) getFlowBefore(s);
    List aliasList = new ArrayList(fs.size());
    for (Object o : fs) {
      aliasList.add(o);
    }

    if (aliasList.contains(new EquivalentValue(l))) {
      return aliasList;
    }
    return new ArrayList();
  }

  protected void merge(Object in1, Object in2, Object out) {
    FlowSet inSet1 = (FlowSet) in1;
    FlowSet inSet2 = (FlowSet) in2;
    FlowSet outSet = (FlowSet) out;

    inSet1.intersection(inSet2, outSet);
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
    if (newDefs.contains(new EquivalentValue(l))) {
      List<Object> existingDefStmts = new ArrayList<Object>();
      Iterator outIt = out.iterator();
      while (outIt.hasNext()) {
        Object o = outIt.next();
        if (o instanceof Stmt) {
          existingDefStmts.add(o);
        }
      }
      out.clear();
      Iterator<EquivalentValue> newDefsIt = newDefs.iterator();
      while (newDefsIt.hasNext()) {
        out.add(newDefsIt.next());
      }
      if (stmt instanceof DefinitionStmt) {
        if (!stmt.containsInvokeExpr() && !(stmt instanceof IdentityStmt)) {
          out.add(new EquivalentValue(((DefinitionStmt) stmt).getRightOp()));
        }
      }

      Iterator<Object> existingDefIt = existingDefStmts.iterator();
      while (existingDefIt.hasNext()) {
        Stmt s = (Stmt) existingDefIt.next();
        List sNewDefs = new ArrayList();
        Iterator sNewDefBoxesIt = s.getDefBoxes().iterator();
        while (sNewDefBoxesIt.hasNext()) {
          sNewDefs.add(((ValueBox) sNewDefBoxesIt.next()).getValue());
        }

        if (s instanceof DefinitionStmt) {
          if (out.contains(new EquivalentValue(((DefinitionStmt) s).getRightOp()))) {
            Iterator sNewDefsIt = sNewDefs.iterator();
            while (sNewDefsIt.hasNext()) {
              out.add(new EquivalentValue((Value) sNewDefsIt.next()));
            }
          } else {
            Iterator sNewDefsIt = sNewDefs.iterator();
            while (sNewDefsIt.hasNext()) {
              out.remove(new EquivalentValue((Value) sNewDefsIt.next()));
            }
          }
        }
      }
    } else {
      if (stmt instanceof DefinitionStmt) {
        if (out.contains(new EquivalentValue(l))) {
          if (out.contains(new EquivalentValue(((DefinitionStmt) stmt).getRightOp()))) {
            Iterator<EquivalentValue> newDefsIt = newDefs.iterator();
            while (newDefsIt.hasNext()) {
              out.add(newDefsIt.next());
            }
          } else {
            Iterator<EquivalentValue> newDefsIt = newDefs.iterator();
            while (newDefsIt.hasNext()) {
              out.remove(newDefsIt.next());
            }
          }
        } else // before finding a def for l, just keep track of all definition statements
               // note that if l is redefined, then we'll miss existing values that then
               // become equal to l. It is suboptimal but correct to miss these values.
        {
          out.add(stmt);
        }
      }
    }
  }

  protected void copy(Object source, Object dest) {

    FlowSet sourceSet = (FlowSet) source;
    FlowSet destSet = (FlowSet) dest;

    sourceSet.copy(destSet);

  }

  protected Object entryInitialFlow() {
    return new ArraySparseSet();
  }

  protected Object newInitialFlow() {
    return new ArraySparseSet();
  }
}
