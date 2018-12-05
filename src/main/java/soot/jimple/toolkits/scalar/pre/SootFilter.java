package soot.jimple.toolkits.scalar.pre;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Florian Loitsch
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

import soot.EquivalentValue;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.ConcreteRef;
import soot.jimple.Constant;
import soot.jimple.DivExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.RemExpr;
import soot.jimple.UnopExpr;

/**
 * Allows easy filtering/wrapping of Soot objects. Operations that are done very often are grouped here.
 */
public class SootFilter {

  /**
   * wraps a value into a EquivalentValue. returns <code>null</code> if <code>val</code> is null.
   *
   * @param val
   *          the Value to wrap.
   * @return the EquivalentValue containing val.
   */
  public static EquivalentValue equiVal(Value val) {
    if (val == null) {
      return null;
    }
    return new EquivalentValue(val);
  }

  /**
   * filters out the RHS of an assignmentStmt.
   *
   * @param unit
   *          a Unit from which to extract the RHS.
   * @return the RHS-Value of <code>unit</code> or <code>null</code> if <code>unit</code> wasn't an assignment-stmt.
   */
  public static Value rhs(Unit unit) {
    if (unit instanceof AssignStmt) {
      return ((AssignStmt) unit).getRightOp();
    } else {
      return null;
    }
  }

  /**
   * only lets binary expression through.
   *
   * @param val
   *          the Value to test for.
   * @return <code>val</code> if it is a binary expression. otherwise <code>null</code>.
   */
  public static Value binop(Value val) {
    if (val == null) {
      return null;
    }
    if (val instanceof BinopExpr) {
      return val;
    }
    return null;
  }

  /**
   * only lets binary RHS through.
   *
   * @param unit
   *          the Unit to test for.
   * @return the rhs of the current unit, if <code>unit</code> is an AssigStmt and its RHS is a binary expression. otherwise
   *         <code>null</code>.
   */
  public static Value binopRhs(Unit unit) {
    return binop(rhs(unit));
  }

  /**
   * only lets concrete references through. A concrete reference is either an array-ref or a field-ref.<br>
   * returns <code>null</code> if <code>val</code> already was null.
   *
   * @param val
   *          the Value to test for.
   * @return the <code>val</code> if it was a concrete reference. otherwise <code>null</code>.
   */
  public static Value concreteRef(Value val) {
    if (val == null) {
      return null;
    }
    if (val instanceof ConcreteRef) {
      return val;
    }
    return null;
  }

  /**
   * filters out Exception-throwing Values. This method is perhaps conservative.<br>
   * returns <code>null</code> if <code>val</code> is null.
   *
   * @param val
   *          the Value to test for.
   * @return <code>val</code> if val doesn't throw any exception, or <code>null</code> otherwise.
   */
  public static Value noExceptionThrowing(Value val) {
    if (val == null) {
      return null;
    }
    if (!throwsException(val)) {
      return val;
    } else {
      return null;
    }
  }

  /**
   * filters out RHS that don't throw any exception.
   *
   * @param unit
   *          the Unit to test.
   * @return the rhs, if <code>unit</code> is an assignment-stmt and can't throw any exception.
   */
  public static Value noExceptionThrowingRhs(Unit unit) {
    return noExceptionThrowing(rhs(unit));
  }

  /**
   * filters out RHS that aren't invokes.
   *
   * @param unit
   *          the Unit to look at.
   * @return the RHS of <code>unit</code> if it is an assignment-stmt, and its RHS is not an invoke.
   */
  public static Value noInvokeRhs(Unit unit) {
    return noInvoke(rhs(unit));
  }

  /**
   * filters out Invokes.<br>
   * returns <code>null</code> if <code>val</code> is null.
   *
   * @param val
   *          the Value to inspect
   * @return <code>val</code>, if val is not an invoke, <code>null</code> otherwise.
   */
  public static Value noInvoke(Value val) {
    if (val == null || isInvoke(val)) {
      return null;
    } else {
      return val;
    }
  }

  /**
   * returns true, if <code>val</code> is an invoke.
   *
   * @param val
   *          the Value to inspect.
   * @return true if <code>val</code> is an invoke.
   */
  public static boolean isInvoke(Value val) {
    val = getEquivalentValueRoot(val);
    if (val instanceof InvokeExpr) {
      return true;
    }
    return false;
  }

  /**
   * filters out Locals.<br>
   * returns <code>null</code> if <code>val</code> is null.
   *
   * @param val
   *          the Value to look at.
   * @return <code>val</code>, if it is a Local, <code>null</code> otherwise.
   */
  public static Value local(Value val) {
    if (val != null && isLocal(val)) {
      return val;
    } else {
      return null;
    }
  }

  /**
   * only lets non-Locals through.<br>
   * returns <code>null</code> if <code>val</code> is null.
   *
   * @param val
   *          the Value to look at.
   * @return <code>val</code>, if it is not a Local, <code>null</code> otherwise.
   */
  public static Value noLocal(Value val) {
    if (val != null && !isLocal(val)) {
      return val;
    } else {
      return null;
    }
  }

  /**
   * returns true, if <code>val</code> is a Local.
   */
  public static boolean isLocal(Value val) {
    return (getEquivalentValueRoot(val) instanceof Local);
  }

  /**
   * returns the Value of an EquivalentValue. If there are several EquivalentValues stacked one into another, gets the
   * deepest Value.<br>
   * returns <code>null</code> if <code>val</code> is null.
   *
   * @param val
   *          the Value to inspect.
   * @return val, if val is not an EquivalentValue, or the deepest Value otherwise.
   */
  public static Value getEquivalentValueRoot(Value val) {
    if (val == null) {
      return null;
    }
    /*
     * extract the Value, if val is an EquivalentValue. One of the reasons, why testing for "instanceof" is sometimes not a
     * good idea.
     */
    while (val instanceof EquivalentValue) {
      val = ((EquivalentValue) val).getValue();
    }
    return val;
  }

  /**
   * a (probably) conservative way of telling, if a Value throws an exception or not.
   */
  public static boolean throwsException(Value val) {
    val = getEquivalentValueRoot(val);

    /* i really hope i did not forget any... */
    if (val instanceof BinopExpr || val instanceof UnopExpr || val instanceof Local || val instanceof Constant) {
      if (val instanceof DivExpr || val instanceof RemExpr || val instanceof LengthExpr) {
        return true;
      }
      return false;
    }
    return true;
  }
}
