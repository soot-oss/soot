// 
// (c) 2012 University of Luxembourg - Interdisciplinary Centre for 
// Security Reliability and Trust (SnT) - All rights reserved
//
// Author: Alexandre Bartel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>. 
//

package soot.dexpler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import soot.ArrayType;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.FieldRef;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.Stmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.LocalUses;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.SmartLocalDefs;
import soot.toolkits.scalar.UnitValueBoxPair;

public abstract class DexTransformer extends BodyTransformer {

	/**
	 * Collect definitions of l in body including the definitions of aliases of
	 * l.
	 * 
	 * In this context an alias is a local that propagates its value to l.
	 * 
	 * @param l
	 *            the local whose definitions are to collect
	 * @param localDefs
	 *            the LocalDefs object
	 * @param body
	 *            the body that contains the local
	 */
	protected List<Unit> collectDefinitionsWithAliases(Local l,
			LocalDefs localDefs, LocalUses localUses, Body body) {
		Set<Local> seenLocals = new HashSet<Local>();
		Stack<Local> newLocals = new Stack<Local>();
		List<Unit> defs = new LinkedList<Unit>();
		newLocals.push(l);

		while (!newLocals.empty()) {
			Local local = newLocals.pop();
			Debug.printDbg("[null local] ", local);
			if (seenLocals.contains(local))
				continue;
			for (Unit u : collectDefinitions(local, localDefs, body)) {
				if (u instanceof AssignStmt) {
					Value r = ((AssignStmt) u).getRightOp();
					if (r instanceof Local && !seenLocals.contains((Local) r))
						newLocals.push((Local) r);
				}
				defs.add(u);
				//
				List<UnitValueBoxPair> usesOf = (List<UnitValueBoxPair>) localUses
						.getUsesOf(u);
				for (UnitValueBoxPair pair : usesOf) {
					Unit unit = pair.getUnit();
					if (unit instanceof AssignStmt) {
						Value right = ((AssignStmt) unit).getRightOp();
						Value left = ((AssignStmt) unit).getLeftOp();
						if (right == local && left instanceof Local
								&& !seenLocals.contains((Local) left))
							newLocals.push((Local) left);
					}
				}
				//
			}
			seenLocals.add(local);
		}
		return defs;
	}

	/**
	 * Convenience method that collects all definitions of l.
	 * 
	 * @param l
	 *            the local whose definitions are to collect
	 * @param localDefs
	 *            the LocalDefs object
	 * @param body
	 *            the body that contains the local
	 */
	private List<Unit> collectDefinitions(Local l, LocalDefs localDefs,
			Body body) {
		List<Unit> defs = new LinkedList<Unit>();
		for (Unit u : body.getUnits()) {
			List<Unit> defsOf = localDefs.getDefsOfAt(l, u);
			if (defsOf != null)
				defs.addAll(defsOf);
		}
		for (Unit u : defs) {
			Debug.printDbg("[add def] ", u);
		}
		return defs;
	}

	protected Type findArrayType(ExceptionalUnitGraph g,
			SmartLocalDefs localDefs, SimpleLocalUses localUses,
			Stmt arrayStmt, int depth, Set<Unit> alreadyVisitedDefs) {
		if (depth > 20)
			System.out.println("x");

		ArrayRef aRef = null;
		if (arrayStmt.containsArrayRef()) {
			aRef = arrayStmt.getArrayRef();
		}
		Local aBase = null;

		if (null == aRef) {
			if (arrayStmt instanceof AssignStmt) {
				AssignStmt stmt = (AssignStmt) arrayStmt;
				aBase = (Local) stmt.getRightOp();
			} else {
				System.out.println("ERROR: not an assign statement: "
						+ arrayStmt);
				System.exit(-1);
			}
		} else {
			aBase = (Local) aRef.getBase();
		}

		List<Unit> defsOfaBaseList = localDefs.getDefsOfAt(aBase, arrayStmt);
		if (defsOfaBaseList == null || defsOfaBaseList.size() == 0) {
			System.out
					.println("ERROR: no def statement found for array base local "
							+ arrayStmt);
			System.exit(-1);
		}

		// We should find an answer only by processing the first item of the
		// list
		Type aType = null;
		for (Unit baseDef : defsOfaBaseList) {
			Debug.printDbg("dextransformer: ", baseDef);
			if (alreadyVisitedDefs.contains(baseDef))
				continue;
			Set<Unit> newVisitedDefs = new HashSet<Unit>(alreadyVisitedDefs);
			newVisitedDefs.add(baseDef);

			// baseDef is either an assignment statement or an identity
			// statement
			if (baseDef instanceof AssignStmt) {
				AssignStmt stmt = (AssignStmt) baseDef;
				Value r = stmt.getRightOp();
				if (r instanceof FieldRef) {
					Type t = ((FieldRef) r).getFieldRef().type();
					if (t instanceof ArrayType) {
						ArrayType at = (ArrayType) t;
						t = at.getArrayElementType();
					}
					Debug.printDbg("atype fieldref: ", t);
					if (depth == 0) {
						aType = t;
						break;
					} else {
						return t;
					}
				} else if (r instanceof ArrayRef) {
					ArrayRef ar = (ArrayRef) r;
					if (ar.getType().equals(".unknown")
							|| ar.getType().toString().equals("unknown")) { // ||
																			// ar.getType())
																			// {
						System.out.println("second round from stmt: " + stmt);
						Type t = findArrayType(g, localDefs, localUses, stmt,
								++depth, newVisitedDefs); // TODO: which type should be
											// returned?
						if (t instanceof ArrayType) {
							ArrayType at = (ArrayType) t;
							t = at.getArrayElementType();
						}
						if (depth == 0) {
							aType = t;
							break;
						} else {
							return t;
						}
					} else {
						Debug.printDbg("atype arrayref: ", ar.getType()
								.toString());
						ArrayType at = (ArrayType) stmt.getRightOp().getType();
						Type t = at.getArrayElementType();
						if (depth == 0) {
							aType = t;
							break;
						} else {
							return t;
						}
					}
				} else if (r instanceof NewArrayExpr) {
					NewArrayExpr expr = (NewArrayExpr) r;
					Type t = expr.getBaseType();
					Debug.printDbg("atype newarrayexpr: ", t);
					if (depth == 0) {
						aType = t;
						break;
					} else {
						return t;
					}
				} else if (r instanceof CastExpr) {
					Type t = (((CastExpr) r).getCastType());
					Debug.printDbg("atype cast: ", t);
					if (depth == 0) {
						aType = t;
						break;
					} else {
						return t;
					}
				} else if (r instanceof InvokeExpr) {
					Type t = ((InvokeExpr) r).getMethodRef().returnType();
					Debug.printDbg("atype invoke: ", t);
					if (depth == 0) {
						aType = t;
						break;
					} else {
						return t;
					}
				// introduces alias. We look whether there is any type
				// information associated with the alias.
				} else if (r instanceof Local) {
					Debug.printDbg("atype alias: ", stmt);
					Type t = findArrayType(g, localDefs, localUses, stmt,
							++depth, newVisitedDefs);
					if (depth == 0) {
						aType = t;
						//break;
					} else {
						// return t;
						aType = t;
					}
				} else if (r instanceof Constant) {
				} else {
					throw new RuntimeException(
							"ERROR: def statement not possible! " + stmt);
				}

			} else if (baseDef instanceof IdentityStmt) {
				IdentityStmt stmt = (IdentityStmt) baseDef;
				ArrayType at = (ArrayType) stmt.getRightOp().getType();
				Type t = at.getArrayElementType();
				if (depth == 0) {
					aType = t;
					break;
				} else {
					return t;
				}
			} else {
				throw new RuntimeException(
						"ERROR: base local def must be AssignStmt or IdentityStmt! "
								+ baseDef);
			}

			if (aType != null)
			    break;

		} // loop

		if (depth == 0 && aType == null)
			throw new RuntimeException(
					"ERROR: could not find type of array from statement '"
							+ arrayStmt + "'");
		else
			return aType;
	}

}
