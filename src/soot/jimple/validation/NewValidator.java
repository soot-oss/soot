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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

/**
 * A relatively simple validator. It tries to check whether
 * after each new-expression-statement there is a corresponding
 * call to the &lt;init&gt; method before a use or the end of the method.
 * 
 * @author Marc Miltenberger
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
			if (u instanceof Stmt) {
				Stmt s = (Stmt) u;
				if (s instanceof AssignStmt) {
					AssignStmt assign = (AssignStmt) s;

					// First seek for a JNewExpr.
					if (assign.getRightOp() instanceof JNewExpr) {
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
					
						checkForInitializerOnPath(g, assign, assign, locals, new HashSet<Unit>(), exception);

					}

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
	@SuppressWarnings("unchecked")
	private boolean checkForInitializerOnPath(UnitGraph g, Unit start, Unit currentUnit, LinkedHashSet<Local> aliasingLocals, HashSet<Unit> seen, List<ValidationException> exception) {
		final String errorMsg = "There is a path to " + currentUnit + " where <init> does not get called in between.";
		
		final boolean hasSeen = !seen.add(currentUnit);
		
		if (hasSeen)
		{
			//At least this path does not contain a <init>-method,
			//as we reach a statement we have already seen.
			//However, if we have a loop like this
			//x = new X();
			//label2:
			//...
			//if $r0 < 5 goto label2
			//specialinvoke x.<X: void <init>>();
			//everything is fine, although we have seen the statement
			//after label2 already.
			//Thus we return true, as we have not yet seen a usage of it.
			return true;
		}
		
		boolean creatingAlias = false;

		Unit check = currentUnit;
		if (check instanceof Stmt &&
				//The start statement is the new-statement itself.
				currentUnit != start) {
			if (check instanceof AssignStmt) {
				AssignStmt assignCheck = (AssignStmt) check;
				if (aliasingLocals.contains(assignCheck
						.getRightOp())) {
					if (assignCheck.getLeftOp() instanceof Local) {
						//A new alias is created.
						//Since this set of aliasing locals is only valid for this
						//particular path, we clone the set before changing it.
						aliasingLocals = (LinkedHashSet<Local>) aliasingLocals.clone();
						aliasingLocals.add((Local) assignCheck
								.getLeftOp());
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
					return true;
				} else {
					//Since the local on the left hand side gets overwritten
					//even if it was aliasing with our original local, 
					//now it does not any more...
					aliasingLocals.remove(assignCheck.getLeftOp());
				}
			}
			
			if (((Stmt) check).containsInvokeExpr()) {
				InvokeExpr expr = ((Stmt) check)
						.getInvokeExpr();
				if (expr.getMethod().isConstructor()) {
					if (!(expr instanceof JSpecialInvokeExpr)) {
						exception.add(new ValidationException(
										check,
										"<init> method calls may only be used with specialinvoke."));
						//At least we found an initializer, so we return true...
						return true;
					}
					if (!(check instanceof JInvokeStmt)) {
						exception.add(new ValidationException(
										check,
										"<init> methods may only be called with invoke statements."));
						//At least we found an initializer, so we return true...
						return true;
					}

					JSpecialInvokeExpr invoke = (JSpecialInvokeExpr) expr;
					if (aliasingLocals.contains(invoke.getBase()))
					{
						//We are happy now, continue the
						//loop and check other
						//pathes
						return true;
					}
				}
			}
		}
		


		if (!creatingAlias) {
			for (ValueBox box : currentUnit.getUseBoxes()) {
				Value used = box.getValue();
				if (aliasingLocals.contains(used)) {
					//The current unit uses one of the aliasing locals, but
					//there was no initializer in between.
					//However, when creating such an alias, the use is okay.
					exception.add(new ValidationException(
							start,
							errorMsg));
					return false;
				}
			}
		}
	
		List<Unit> successors = g.getSuccsOf(currentUnit);
		for (Unit succ : successors) {
			if (!checkForInitializerOnPath(g, start, succ, aliasingLocals, (HashSet<Unit>) seen.clone(), exception))
			{
				//Already added an exception to the list, thus we can already return.
				//It is sufficient to give one path as an example.
				return false;
			}
		}

		if (successors.isEmpty()) {
			//This means that we are e.g. at the end of the method
			//There was no <init> call on our way...
			exception.add(new ValidationException(
					start,
					errorMsg));
			return false;
		}
		
		return true;
	}

	@Override
	public boolean isBasicValidator() {
		return false;
	}
}
