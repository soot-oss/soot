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

import soot.EquivalentValue;
import soot.Local;
import soot.Unit;
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
public class EqualLocalsAnalysis extends ForwardFlowAnalysis<Unit, FlowSet<Object>> {

  protected Local l = null;
  protected Stmt s = null;

  public EqualLocalsAnalysis(UnitGraph g) {
    super(g);

    // analysis is done on-demand, not now
  }

  /** Returns a list of EquivalentValue wrapped Locals and Refs that must always be equal to l at s */
  public List<Object> getCopiesOfAt(Local l, Stmt s) {
    this.l = l;
    this.s = s;

    doAnalysis();

    FlowSet<Object> fs = (FlowSet<Object>) getFlowBefore(s);
    ArrayList<Object> aliasList = new ArrayList<Object>(fs.size());
    for (Object o : fs) {
      aliasList.add(o);
    }

    if (!aliasList.contains(new EquivalentValue(l))) {
      aliasList.clear();
      aliasList.trimToSize();
    }
    return aliasList;
  }

  @Override
  protected void flowThrough(FlowSet<Object> in, Unit unit, FlowSet<Object> out) {
    in.copy(out);

    // get list of definitions at this unit
    List<EquivalentValue> newDefs = new ArrayList<EquivalentValue>();
    for (ValueBox next : unit.getDefBoxes()) {
      newDefs.add(new EquivalentValue(next.getValue()));
    }

    // If the local of interest was defined in this statement, then we must
    // generate a new list of aliases to it starting here
    if (newDefs.contains(new EquivalentValue(l))) {
      List<Stmt> existingDefStmts = new ArrayList<Stmt>();
      for (Object o : out) {
        if (o instanceof Stmt) {
          existingDefStmts.add((Stmt) o);
        }
      }
      out.clear();

      for (EquivalentValue next : newDefs) {
        out.add(next);
      }
      if (unit instanceof DefinitionStmt) {
        DefinitionStmt du = (DefinitionStmt) unit;
        if (!du.containsInvokeExpr() && !(unit instanceof IdentityStmt)) {
          out.add(new EquivalentValue(du.getRightOp()));
        }
      }

      for (Stmt def : existingDefStmts) {
        List<Value> sNewDefs = new ArrayList<Value>();
        for (ValueBox next : def.getDefBoxes()) {
          sNewDefs.add(next.getValue());
        }
        if (def instanceof DefinitionStmt) {
          if (out.contains(new EquivalentValue(((DefinitionStmt) def).getRightOp()))) {
            for (Value v : sNewDefs) {
              out.add(new EquivalentValue(v));
            }
          } else {
            for (Value v : sNewDefs) {
              out.remove(new EquivalentValue(v));
            }
          }
        }
      }
    } else {
      if (unit instanceof DefinitionStmt) {
        if (out.contains(new EquivalentValue(l))) {
          if (out.contains(new EquivalentValue(((DefinitionStmt) unit).getRightOp()))) {
            for (EquivalentValue ev : newDefs) {
              out.add(ev);
            }
          } else {
            for (EquivalentValue ev : newDefs) {
              out.remove(ev);
            }
          }
        } else {
          // before finding a def for l, just keep track of all definition statements
          // note that if l is redefined, then we'll miss existing values that then
          // become equal to l. It is suboptimal but correct to miss these values.
          out.add(unit);
        }
      }
    }
  }

  @Override
  protected void merge(FlowSet<Object> in1, FlowSet<Object> in2, FlowSet<Object> out) {
    in1.intersection(in2, out);
  }

  @Override
  protected void copy(FlowSet<Object> source, FlowSet<Object> dest) {
    source.copy(dest);
  }

  @Override
  protected FlowSet<Object> entryInitialFlow() {
    return new ArraySparseSet<Object>();
  }

  @Override
  protected FlowSet<Object> newInitialFlow() {
    return new ArraySparseSet<Object>();
  }
}
