package soot.jimple.toolkits.annotation.nullcheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2006 Richard L. Halpert
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import soot.Immediate;
import soot.Local;
import soot.RefLikeType;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.MonitorStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JCastExpr;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;

/**
 * An intraprocedural nullness assumption analysis that computes for each location and each value in a method if the value
 * (before or after that location) is treated as definitely null, definitely non-null or neither. This information could be
 * useful in deciding whether or not to insert code that accesses a potentially null object. If the original program assumes
 * a value is non-null, then adding a use of that value will not introduce any NEW nullness errors into the program. This
 * code may be buggy, or just plain wrong. It has not been checked.
 *
 * @author Richard L. Halpert Adapted from Eric Bodden's NullnessAnalysis
 */
public class NullnessAssumptionAnalysis extends BackwardFlowAnalysis<Unit, NullnessAssumptionAnalysis.AnalysisInfo> {

  protected final static Object BOTTOM = new Object() {
    @Override
    public String toString() {
      return "bottom";
    }
  };

  protected final static Object NULL = new Object() {
    @Override
    public String toString() {
      return "null";
    }
  };

  protected final static Object NON_NULL = new Object() {
    @Override
    public String toString() {
      return "non-null";
    }
  };

  // TOP IS MEANINGLESS FOR THIS ANALYSIS: YOU CAN'T ASSUME A VALUE IS NULL AND NON_NULL. BOTTOM IS USED FOR THAT CASE
  protected final static Object TOP = new Object() {
    @Override
    public String toString() {
      return "top";
    }
  };

  /**
   * Creates a new analysis for the given graph/
   *
   * @param graph
   *          any unit graph
   */
  public NullnessAssumptionAnalysis(UnitGraph graph) {
    super(graph);

    doAnalysis();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void flowThrough(AnalysisInfo in, Unit unit, AnalysisInfo outValue) {
    AnalysisInfo out = new AnalysisInfo(in);

    Stmt s = (Stmt) unit;

    // in case of an if statement, we neet to compute the branch-flow;
    // e.g. for a statement "if(x!=null) goto s" we have x==null for the fallOut and
    // x!=null for the branchOut
    // or for an instanceof expression
    // if(s instanceof JIfStmt) {
    // JIfStmt ifStmt = (JIfStmt) s;
    // handleIfStmt(ifStmt, in, out, outBranch);
    // }
    // in case of a monitor statement, we know that the programmer assumes we have a non-null value
    if (s instanceof MonitorStmt) {
      out.put(((MonitorStmt) s).getOp(), NON_NULL);
    }

    // if we have an array ref, set the info for this ref to TOP,
    // cause we need to be conservative here
    if (s.containsArrayRef()) {
      handleArrayRef(s.getArrayRef(), out);
    }
    // same for field refs, but also set the receiver object to non-null, if there is one
    if (s.containsFieldRef()) {
      handleFieldRef(s.getFieldRef(), out);
    }
    // same for invoke expr., also set the receiver object to non-null, if there is one
    if (s.containsInvokeExpr()) {
      handleInvokeExpr(s.getInvokeExpr(), out);
    }

    // allow sublasses to define certain values as always-non-null
    for (Map.Entry<Value, Object> entry : out.entrySet()) {
      if (isAlwaysNonNull(entry.getKey())) {
        entry.setValue(NON_NULL);
      }
    }

    // if we have a definition (assignment) statement to a ref-like type, handle it,
    if (s instanceof DefinitionStmt) {
      // need to copy the current out set because we need to assign under this assumption;
      // so this copy becomes the in-set to handleRefTypeAssignment
      DefinitionStmt defStmt = (DefinitionStmt) s;
      if (defStmt.getLeftOp().getType() instanceof RefLikeType) {
        handleRefTypeAssignment(defStmt, new AnalysisInfo(out), out);
      }
    }

    // save memory by only retaining information about locals
    for (Iterator<Value> outIter = out.keySet().iterator(); outIter.hasNext();) {
      Value v = outIter.next();
      if (!(v instanceof Local)) {
        outIter.remove();
      }
    }

    // now copy the computed info to out
    copy(out, outValue);
  }

  /**
   * This can be overridden by sublasses to mark a certain value as constantly non-null.
   *
   * @param v
   *          any value
   * @return true if it is known that this value (e.g. a method return value) is never null
   */
  protected boolean isAlwaysNonNull(Value v) {
    return false;
  }

  private void handleArrayRef(ArrayRef arrayRef, AnalysisInfo out) {
    // here we know that the array must point to an object, but the array value might be anything
    out.put(arrayRef.getBase(), NON_NULL);
  }

  private void handleFieldRef(FieldRef fieldRef, AnalysisInfo out) {
    if (fieldRef instanceof InstanceFieldRef) {
      InstanceFieldRef instanceFieldRef = (InstanceFieldRef) fieldRef;
      // here we know that the receiver must point to an object
      out.put(instanceFieldRef.getBase(), NON_NULL);
    }
  }

  private void handleInvokeExpr(InvokeExpr invokeExpr, AnalysisInfo out) {
    if (invokeExpr instanceof InstanceInvokeExpr) {
      InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
      // here we know that the receiver must point to an object
      out.put(instanceInvokeExpr.getBase(), NON_NULL);
    }
  }

  private void handleRefTypeAssignment(DefinitionStmt assignStmt, AnalysisInfo rhsInfo, AnalysisInfo out) {
    Value right = assignStmt.getRightOp();
    // unbox casted value
    if (right instanceof JCastExpr) {
      right = ((JCastExpr) right).getOp();
    }

    // An assignment invalidates any assumptions of null/non-null for lhs
    // We COULD be more accurate by assigning those assumptions to the rhs prior to this statement
    rhsInfo.put(right, BOTTOM);

    // assign from rhs to lhs
    out.put(assignStmt.getLeftOp(), rhsInfo.get(right));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void copy(AnalysisInfo source, AnalysisInfo dest) {
    dest.clear();
    dest.putAll(source);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected AnalysisInfo entryInitialFlow() {
    return new AnalysisInfo();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void merge(AnalysisInfo in1, AnalysisInfo in2, AnalysisInfo out) {
    HashSet<Value> values = new HashSet<Value>();
    values.addAll(in1.keySet());
    values.addAll(in2.keySet());

    out.clear();

    for (Value v : values) {
      HashSet<Object> leftAndRight = new HashSet<Object>();
      leftAndRight.add(in1.get(v));
      leftAndRight.add(in2.get(v));

      Object result;
      // This needs to be corrected for assumption *** TODO
      // TOP stays TOP
      if (leftAndRight.contains(BOTTOM)) {
        // if on either side we know nothing... then together we know nothing for sure
        result = BOTTOM;
      } else if (leftAndRight.contains(NON_NULL)) {
        if (leftAndRight.contains(NULL)) {
          // NULL and NON_NULL merges to BOTTOM
          result = BOTTOM;
        } else {
          // NON_NULL and NON_NULL stays NON_NULL
          result = NON_NULL;
        }
      } else if (leftAndRight.contains(NULL)) {
        // NULL and NULL stays NULL
        result = NULL;
      } else {
        // only BOTTOM remains
        result = BOTTOM;
      }

      out.put(v, result);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected AnalysisInfo newInitialFlow() {
    return new AnalysisInfo();
  }

  /**
   * Returns <code>true</code> if the analysis could determine that i is always treated as null after and including the
   * statement s.
   *
   * @param s
   *          a statement of the respective body
   * @param i
   *          a local or constant of that body
   * @return true if i is always null right before this statement
   */
  public boolean isAssumedNullBefore(Unit s, Immediate i) {
    return getFlowBefore(s).get(i) == NULL;
  }

  /**
   * Returns <code>true</code> if the analysis could determine that i is always treated as non-null after and including the
   * statement s.
   *
   * @param s
   *          a statement of the respective body
   * @param i
   *          a local of that body
   * @return true if i is always non-null right before this statement
   */
  public boolean isAssumedNonNullBefore(Unit s, Immediate i) {
    return getFlowBefore(s).get(i) == NON_NULL;
  }

  /**
   * The analysis info is a simple mapping of type {@link Value} to any of the constants BOTTOM, NON_NULL, NULL or TOP. This
   * class returns BOTTOM by default.
   *
   * @author Eric Bodden
   */
  protected static class AnalysisInfo extends HashMap<Value, Object> {

    public AnalysisInfo() {
      super();
    }

    public AnalysisInfo(Map<Value, Object> m) {
      super(m);
    }

    @Override
    public Object get(Object key) {
      Object object = super.get(key);
      if (object == null) {
        return BOTTOM;
      }
      return object;
    }
  }
}
