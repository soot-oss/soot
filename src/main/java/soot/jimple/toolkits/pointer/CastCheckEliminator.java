package soot.jimple.toolkits.pointer;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 - 2004 Ondrej Lhotak
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

import soot.Body;
import soot.Local;
import soot.RefType;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.EqExpr;
import soot.jimple.IfStmt;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.NeExpr;
import soot.jimple.NewExpr;
import soot.jimple.NullConstant;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;

/** A flow analysis that detects redundant cast checks. */
public class CastCheckEliminator extends ForwardBranchedFlowAnalysis<LocalTypeSet> {

  protected LocalTypeSet emptySet;

  public CastCheckEliminator(BriefUnitGraph cfg) {
    super(cfg);
    makeInitialSet();
    doAnalysis();
    tagCasts();
  }

  /** Put the results of the analysis into tags in cast statements. */
  protected void tagCasts() {
    for (Unit u : ((UnitGraph) graph).getBody().getUnits()) {
      if (u instanceof AssignStmt) {
        Value rhs = ((AssignStmt) u).getRightOp();
        if (rhs instanceof CastExpr) {
          CastExpr cast = (CastExpr) rhs;
          Type t = cast.getCastType();
          if (t instanceof RefType) {
            Value op = cast.getOp();
            if (op instanceof Local) {
              LocalTypeSet set = getFlowBefore(u);
              u.addTag(new CastCheckTag(set.get(set.indexOf((Local) op, (RefType) t))));
            } else {
              assert (op instanceof NullConstant);
              u.addTag(new CastCheckTag(true));
            }
          }
        }
      }
    }
  }

  /**
   * Find all the locals of reference type and all the types used in casts to initialize the mapping from locals and types to
   * bits in the bit vector in LocalTypeSet.
   */
  protected void makeInitialSet() {
    final Body body = ((UnitGraph) graph).getBody();
    // Find all locals of reference type
    List<Local> refLocals = new ArrayList<Local>();
    for (Local l : body.getLocals()) {
      if (l.getType() instanceof RefType) {
        refLocals.add(l);
      }
    }

    // Find types of all casts
    List<Type> types = new ArrayList<Type>();
    for (Unit u : body.getUnits()) {
      if (u instanceof AssignStmt) {
        Value rhs = ((AssignStmt) u).getRightOp();
        if (rhs instanceof CastExpr) {
          Type t = ((CastExpr) rhs).getCastType();
          if (t instanceof RefType && !types.contains(t)) {
            types.add(t);
          }
        }
      }
    }

    this.emptySet = new LocalTypeSet(refLocals, types);
  }

  /** Returns a new, aggressive (local,type) set. */
  @Override
  protected LocalTypeSet newInitialFlow() {
    LocalTypeSet ret = (LocalTypeSet) emptySet.clone();
    ret.setAllBits();
    return ret;
  }

  /** This is the flow function as described in the assignment write-up. */
  @Override
  protected void flowThrough(LocalTypeSet in, Unit unit, List<LocalTypeSet> outFallVals, List<LocalTypeSet> outBranchVals) {
    final LocalTypeSet out = (LocalTypeSet) in.clone();
    LocalTypeSet outBranch = out; // aliased to out unless unit is IfStmt

    // First kill all locals defined in this statement
    for (ValueBox b : unit.getDefBoxes()) {
      Value v = b.getValue();
      if (v instanceof Local && v.getType() instanceof RefType) {
        out.killLocal((Local) v);
      }
    }

    if (unit instanceof AssignStmt) {
      // An AssignStmt may be a new, a simple copy, or a cast
      AssignStmt astmt = (AssignStmt) unit;
      Value rhs = astmt.getRightOp();
      Value lhs = astmt.getLeftOp();
      if (lhs instanceof Local && rhs.getType() instanceof RefType) {
        Local l = (Local) lhs;
        if (rhs instanceof NewExpr) {
          out.localMustBeSubtypeOf(l, (RefType) rhs.getType());
        } else if (rhs instanceof CastExpr) {
          CastExpr cast = (CastExpr) rhs;
          Type castType = cast.getCastType();
          if (castType instanceof RefType && cast.getOp() instanceof Local) {
            RefType refType = (RefType) castType;
            Local opLocal = (Local) cast.getOp();
            out.localCopy(l, opLocal);
            out.localMustBeSubtypeOf(l, refType);
            out.localMustBeSubtypeOf(opLocal, refType);
          }
        } else if (rhs instanceof Local) {
          out.localCopy(l, (Local) rhs);
        }
      }

    } else if (unit instanceof IfStmt) {
      // Handle if statements
      IfStmt ifstmt = (IfStmt) unit;

      // This do ... while(false) is here so I can break out of it rather
      // than having to have seven nested if statements. Silly people who
      // took goto's out of the language... <grumble> <grumble>
      do {
        final List<Unit> unitPreds = graph.getPredsOf(unit);
        if (unitPreds.size() != 1) {
          break;
        }
        final Unit predecessor = unitPreds.get(0);
        if (!(predecessor instanceof AssignStmt)) {
          break;
        }
        final AssignStmt pred = (AssignStmt) predecessor;
        final Value predRHS = pred.getRightOp();
        if (!(predRHS instanceof InstanceOfExpr)) {
          break;
        }
        InstanceOfExpr iofexpr = (InstanceOfExpr) predRHS;
        final Type iofCheckType = iofexpr.getCheckType();
        if (!(iofCheckType instanceof RefType)) {
          break;
        }
        final Value iofOp = iofexpr.getOp();
        if (!(iofOp instanceof Local)) {
          break;
        }
        final ConditionExpr c = (ConditionExpr) ifstmt.getCondition();
        if (!c.getOp1().equals(pred.getLeftOp())) {
          break;
        }
        final Value conditionOp2 = c.getOp2();
        if (!(conditionOp2 instanceof IntConstant) || (((IntConstant) conditionOp2).value != 0)) {
          break;
        }
        if (c instanceof NeExpr) {
          // The IfStmt is like this:
          // if x instanceof t goto somewhere_else
          // So x is of type t on the taken branch
          outBranch = (LocalTypeSet) out.clone();
          outBranch.localMustBeSubtypeOf((Local) iofOp, (RefType) iofCheckType);
        } else if (c instanceof EqExpr) {
          // The IfStmt is like this:
          // if !(x instanceof t) goto somewhere_else
          // So x is of type t on the fallthrough branch
          outBranch = (LocalTypeSet) out.clone();
          out.localMustBeSubtypeOf((Local) iofOp, (RefType) iofCheckType);
        }
      } while (false);
    }

    // Now copy the computed (local,type) set to all successors
    for (LocalTypeSet ts : outFallVals) {
      copy(out, ts);
    }
    for (LocalTypeSet ts : outBranchVals) {
      copy(outBranch, ts);
    }
  }

  @Override
  protected void copy(LocalTypeSet s, LocalTypeSet d) {
    d.and(s);
    d.or(s);
  }

  // The merge operator is set intersection.
  @Override
  protected void merge(LocalTypeSet in1, LocalTypeSet in2, LocalTypeSet o) {
    o.setAllBits();
    o.and(in1);
    o.and(in2);
  }

  /** Returns a new, aggressive (local,type) set. */
  @Override
  protected LocalTypeSet entryInitialFlow() {
    return (LocalTypeSet) emptySet.clone();
  }
}
