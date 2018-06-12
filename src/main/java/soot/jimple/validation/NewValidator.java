package soot.jimple.validation;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import soot.Body;
import soot.Local;
import soot.RefType;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.NewExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

/**
 * A relatively simple validator. It tries to check whether after each new-expression-statement there is a corresponding call
 * to the &lt;init&gt; method before a use or the end of the method.
 *
 * @author Marc Miltenberger
 * @author Steven Arzt
 */
public enum NewValidator implements BodyValidator {
  INSTANCE;

  private static final String errorMsg
      = "There is a path from '%s' to the usage '%s' where <init> does not get called in between.";

  public static boolean MUST_CALL_CONSTRUCTOR_BEFORE_RETURN = false;

  public static NewValidator v() {
    return INSTANCE;
  }

  /**
   * Checks whether after each new-instruction a constructor call follows.
   */
  @Override
  public void validate(Body body, List<ValidationException> exceptions) {
    UnitGraph g = new BriefUnitGraph(body);
    for (Unit u : body.getUnits()) {
      if (u instanceof AssignStmt) {
        AssignStmt assign = (AssignStmt) u;

        // First seek for a JNewExpr.
        if (assign.getRightOp() instanceof NewExpr) {
          if (!(assign.getLeftOp().getType() instanceof RefType)) {
            exceptions.add(new ValidationException(u, "A new-expression must be used on reference type locals",
                String.format("Body of method %s contains a new-expression, which is assigned to a non-reference local",
                    body.getMethod().getSignature())));
            return;
          }

          // We search for a JSpecialInvokeExpr on the local.
          LinkedHashSet<Local> locals = new LinkedHashSet<Local>();
          locals.add((Local) assign.getLeftOp());

          checkForInitializerOnPath(g, assign, exceptions);
        }
      }
    }
  }

  /**
   * <p>
   * Checks whether all pathes from start to the end of the method have a call to the &lt;init&gt; method in between.
   * </p>
   * <code>
   * $r0 = new X;<br>
   * ...<br>
   * specialinvoke $r0.<X: void <init>()>; //validator checks whether this statement is missing
   * </code>
   * <p>
   * Regarding <i>aliasingLocals</i>:<br>
   * The first local in the set is always the local on the LHS of the new-expression-assignment (called: original local; in
   * the example <code>$r0</code>).
   * </p>
   *
   * @param g
   *          the unit graph of the method
   * @param exception
   *          the list of all collected exceptions
   * @return true if a call to a &lt;init&gt;-Method has been found on this way.
   */
  private boolean checkForInitializerOnPath(UnitGraph g, AssignStmt newStmt, List<ValidationException> exception) {
    List<Unit> workList = new ArrayList<Unit>();
    Set<Unit> doneSet = new HashSet<Unit>();
    workList.add(newStmt);

    Set<Local> aliasingLocals = new HashSet<Local>();
    aliasingLocals.add((Local) newStmt.getLeftOp());

    while (!workList.isEmpty()) {
      Stmt curStmt = (Stmt) workList.remove(0);
      if (!doneSet.add(curStmt)) {
        continue;
      }
      if (!newStmt.equals(curStmt)) {
        if (curStmt.containsInvokeExpr()) {
          InvokeExpr expr = curStmt.getInvokeExpr();
          if (expr.getMethod().isConstructor()) {
            if (!(expr instanceof SpecialInvokeExpr)) {
              exception.add(new ValidationException(curStmt, "<init> method calls may only be used with specialinvoke."));
              // At least we found an initializer, so we return true...
              return true;
            }
            if (!(curStmt instanceof InvokeStmt)) {
              exception.add(new ValidationException(curStmt, "<init> methods may only be called with invoke statements."));
              // At least we found an initializer, so we return true...
              return true;
            }

            SpecialInvokeExpr invoke = (SpecialInvokeExpr) expr;
            if (aliasingLocals.contains(invoke.getBase())) {
              // We are happy now, continue the loop and check other paths
              continue;
            }
          }
        }

        // We are still in the loop, so this was not the constructor call we
        // were looking for
        boolean creatingAlias = false;
        if (curStmt instanceof AssignStmt) {
          AssignStmt assignCheck = (AssignStmt) curStmt;
          if (aliasingLocals.contains(assignCheck.getRightOp())) {
            if (assignCheck.getLeftOp() instanceof Local) {
              // A new alias is created.
              aliasingLocals.add((Local) assignCheck.getLeftOp());
              creatingAlias = true;
            }
          }
          Local originalLocal = aliasingLocals.iterator().next();
          if (originalLocal.equals(assignCheck.getLeftOp())) {
            // In case of dead assignments:

            // Handles cases like
            // $r0 = new x;
            // $r0 = null;

            // But not cases like
            // $r0 = new x;
            // $r1 = $r0;
            // $r1 = null;
            // Because we check for the original local
            continue;
          } else {
            // Since the local on the left hand side gets overwritten
            // even if it was aliasing with our original local,
            // now it does not any more...
            aliasingLocals.remove(assignCheck.getLeftOp());
          }
        }

        if (!creatingAlias) {
          for (ValueBox box : curStmt.getUseBoxes()) {
            Value used = box.getValue();
            if (aliasingLocals.contains(used)) {
              // The current unit uses one of the aliasing locals, but
              // there was no initializer in between.
              // However, when creating such an alias, the use is okay.
              exception.add(new ValidationException(newStmt, String.format(errorMsg, newStmt, curStmt)));
              return false;
            }
          }
        }
      }
      // Enqueue the successors
      List<Unit> successors = g.getSuccsOf(curStmt);
      if (successors.isEmpty() && MUST_CALL_CONSTRUCTOR_BEFORE_RETURN) {
        // This means that we are e.g. at the end of the method
        // There was no <init> call on our way...
        exception.add(new ValidationException(newStmt, String.format(errorMsg, newStmt, curStmt)));
        return false;
      }
      workList.addAll(successors);
    }
    return true;
  }

  @Override
  public boolean isBasicValidator() {
    return false;
  }
}
