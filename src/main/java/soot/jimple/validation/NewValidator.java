/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jimple.validation;

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
 * A relatively simple validator. It tries to check whether
 * after each new-expression-statement there is a corresponding
 * call to the &lt;init&gt; method before a use or the end of the method.
 * 
 * @author Marc Miltenberger
 * @author Steven Arzt
 */
public enum NewValidator implements BodyValidator {
	INSTANCE;

	public static NewValidator v() {
		return INSTANCE;
	}

	@Override
	/**
	 * Checks whether after each new-instruction a constructor call
	 * follows.
	 */
	public void validate(Body body, List<ValidationException> exception) {
		UnitGraph g = new BriefUnitGraph(body);
		for (Unit u : body.getUnits()) {
			if (u instanceof AssignStmt) {
				AssignStmt assign = (AssignStmt) u;
				
				// First seek for a JNewExpr.
				if (assign.getRightOp() instanceof NewExpr) {
					if (!(assign.getLeftOp().getType() instanceof RefType)) {
						exception
								.add(new ValidationException(
										u,
										"A new-expression must be used on reference type locals",
										"Body of method "
												+ body.getMethod()
														.getSignature()
												+ " contains a new-expression, which is assigned to a non-reference local"));
						return;
					}

					// We search for a JSpecialInvokeExpr on the local.
					LinkedHashSet<Local> locals = new LinkedHashSet<Local>();
					locals.add((Local) assign.getLeftOp());
				
					checkForInitializerOnPath(g, (Local) assign.getLeftOp(), assign, exception);
				}
			}
		}
	}

	/**
	 * <p>Checks whether all pathes from start to the end of the method
	 * have a call to the &lt;init&gt; method in between.</p>
	 * <code>
	 * $r0 = new X;<br>
	 * ...<br>
	 * specialinvoke $r0.<X: void <init>()>; //validator checks whether this statement is missing 
	 * </code>
	 * <p>
	 * Regarding <i>aliasingLocals</i>:<br>
	 *  The first local in the set
	 *        is always the local on the LHS of the new-expression-assignment (called: original local;
	 *        in the example <code>$r0</code>).
	 * </p>
	 * 
	 * @param g the unit graph of the method
	 * @param start the unit to start (containing the new-expression)
	 * @param currentUnit the current unit
	 * @param aliasingLocals a set of all aliasing locals
	 * @param seen a set of all seen units on this path
	 * @param exception the list of all collected exceptions
	 * @return true if a call to a &lt;init&gt;-Method has been found on this way.
	 */
	private boolean checkForInitializerOnPath(UnitGraph g, Local initLocal,
			Unit stmt, List<ValidationException> exception) {
		final String errorMsg = "There is a path to " + stmt + " where <init> does not get called in between.";
		
		List<Unit> workList = new ArrayList<Unit>();
		Set<Unit> doneSet = new HashSet<Unit>();
		workList.add(stmt);
		
		Set<Local> aliasingLocals = new HashSet<Local>();
		aliasingLocals.add(initLocal);
		
		while (!workList.isEmpty()) {
			Stmt curStmt = (Stmt) workList.remove(0);
			if (!doneSet.add(curStmt))
				continue;
			
			if (curStmt.containsInvokeExpr()) {
				InvokeExpr expr = curStmt.getInvokeExpr();
				if (expr.getMethod().isConstructor()) {
					if (!(expr instanceof SpecialInvokeExpr)) {
						exception.add(new ValidationException(
								curStmt, "<init> method calls may only be used with specialinvoke."));
						//At least we found an initializer, so we return true...
						return true;
					}
					if (!(curStmt instanceof InvokeStmt)) {
						exception.add(new ValidationException(
								curStmt, "<init> methods may only be called with invoke statements."));
						//At least we found an initializer, so we return true...
						return true;
					}
					
					SpecialInvokeExpr invoke = (SpecialInvokeExpr) expr;
					if (aliasingLocals.contains(invoke.getBase()))
					{
						//We are happy now, continue the
						//loop and check other
						//pathes
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
						//A new alias is created.
						aliasingLocals.add((Local) assignCheck.getLeftOp());
						creatingAlias = true;
					}
				}
				Local originalLocal = aliasingLocals.iterator().next();
				if (originalLocal.equals(assignCheck.getLeftOp())) {
					//In case of dead assignments:
					
					//Handles cases like
					//$r0 = new x;
					//$r0 = null;
					
					//But not cases like
					//$r0 = new x;
					//$r1 = $r0;
					//$r1 = null;
					//Because we check for the original local
					continue;
				}
				else {
					//Since the local on the left hand side gets overwritten
					//even if it was aliasing with our original local, 
					//now it does not any more...
					aliasingLocals.remove(assignCheck.getLeftOp());
				}
			}
			
			if (!creatingAlias) {
				for (ValueBox box : curStmt.getUseBoxes()) {
					Value used = box.getValue();
					if (aliasingLocals.contains(used)) {
						//The current unit uses one of the aliasing locals, but
						//there was no initializer in between.
						//However, when creating such an alias, the use is okay.
						exception.add(new ValidationException(stmt, errorMsg));
						return false;
					}
				}
			}
			
			// Enqueue the successors
			List<Unit> successors = g.getSuccsOf(curStmt);
			if (successors.isEmpty()) {
				//This means that we are e.g. at the end of the method
				//There was no <init> call on our way...
				exception.add(new ValidationException(stmt, errorMsg));
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
