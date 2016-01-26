/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 *
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

package soot.dexpler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.Local;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.EqExpr;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.FieldRef;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LengthExpr;
import soot.jimple.NeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThrowStmt;
import soot.jimple.internal.AbstractInstanceInvokeExpr;
import soot.jimple.internal.AbstractInvokeExpr;

/**
 * BodyTransformer to find and change definition of locals used within an if
 * which contains a condition involving two locals ( and not only one local as
 * in DexNullTransformer).
 *
 * It this case, if any of the two locals leads to an object being def or used,
 * all the appropriate defs of the two locals are updated to reflect the use of
 * objects (i.e: 0s are replaced by nulls).
 */
public class DexIfTransformer extends AbstractNullTransformer {
	// Note: we need an instance variable for inner class access, treat this as
	// a local variable (including initialization before use)

	private boolean usedAsObject;
	private boolean doBreak = false;

	public static DexIfTransformer v() {
		return new DexIfTransformer();
	}

	Local l = null;

	protected void internalTransform(final Body body, String phaseName, Map<String,String> options) {
		final DexDefUseAnalysis localDefs = new DexDefUseAnalysis(body);

		Set<IfStmt> ifSet = getNullIfCandidates(body);
		for (IfStmt ifs : ifSet) {
			ConditionExpr ifCondition = (ConditionExpr) ifs.getCondition();
			Local[] twoIfLocals = new Local[] { (Local) ifCondition.getOp1(),
					(Local) ifCondition.getOp2() };
			usedAsObject = false;
			for (Local loc : twoIfLocals) {
				Debug.printDbg("\n[null if with two local candidate] ", loc);
				Set<Unit> defs = localDefs.collectDefinitionsWithAliases(loc);
				
				// process normally
				doBreak = false;
				for (Unit u : defs) {

					// put correct local in l
					if (u instanceof DefinitionStmt) {
						l = (Local) ((DefinitionStmt) u).getLeftOp();
					} else {
						throw new RuntimeException(
								"ERROR: def can not be something else than Assign or Identity statement! (def: " + u
										+ " class: " + u.getClass() + "");
					}

					Debug.printDbg("    target local: ", l, " (Unit: ", u, " )");

					// check defs
					u.apply(new AbstractStmtSwitch() { // Alex: should also end
														// as soon as detected
														// as not used as an
														// object
						public void caseAssignStmt(AssignStmt stmt) {
							Value r = stmt.getRightOp();
							if (r instanceof FieldRef) {
								usedAsObject = isObject(((FieldRef) r).getFieldRef().type());
								if (usedAsObject)
									doBreak = true;
								return;
							} else if (r instanceof ArrayRef) {
								ArrayRef ar = (ArrayRef) r;
								if (ar.getType() instanceof UnknownType) {
									usedAsObject = stmt.hasTag("ObjectOpTag"); // isObject
																				// (findArrayType
																				// (g,
																				// localDefs,
																				// localUses,
																				// stmt));
								} else {
									usedAsObject = isObject(ar.getType());
								}
								if (usedAsObject)
									doBreak = true;
								return;
							} else if (r instanceof StringConstant || r instanceof NewExpr || r instanceof NewArrayExpr) {
								usedAsObject = true;
								if (usedAsObject)
									doBreak = true;
								return;
							} else if (r instanceof CastExpr) {
								usedAsObject = isObject(((CastExpr) r).getCastType());
								if (usedAsObject)
									doBreak = true;
								return;
							} else if (r instanceof InvokeExpr) {
								usedAsObject = isObject(((InvokeExpr) r).getType());
								if (usedAsObject)
									doBreak = true;
								return;
							} else if (r instanceof LengthExpr) {
								usedAsObject = false;
								if (usedAsObject)
									doBreak = true;
								return;
								// introduces alias
							} else if (r instanceof Local) {
							}

						}

						public void caseIdentityStmt(IdentityStmt stmt) {
							if (stmt.getLeftOp() == l) {
								usedAsObject = isObject(stmt.getRightOp().getType());
								if (usedAsObject)
									doBreak = true;
								return;
							}
						}
					});
					if (doBreak)
						break;

					// check uses
					for (Unit use : localDefs.getUsesOf(l)) {
						Debug.printDbg("    use: ", use);
						use.apply(new AbstractStmtSwitch() {
							private boolean examineInvokeExpr(InvokeExpr e) {
								List<Value> args = e.getArgs();
								List<Type> argTypes = e.getMethodRef().parameterTypes();
								assert args.size() == argTypes.size();
								for (int i = 0; i < args.size(); i++) {
									if (args.get(i) == l && isObject(argTypes.get(i))) {
										return true;
									}
								}
								// check for base
								SootMethodRef sm = e.getMethodRef();
								if (!sm.isStatic()) {
									if (e instanceof AbstractInvokeExpr) {
										AbstractInstanceInvokeExpr aiiexpr = (AbstractInstanceInvokeExpr) e;
										Value b = aiiexpr.getBase();
										if (b == l) {
											return true;
										}
									}
								}
								return false;
							}

							public void caseInvokeStmt(InvokeStmt stmt) {
								InvokeExpr e = stmt.getInvokeExpr();
								usedAsObject = examineInvokeExpr(e);
								Debug.printDbg("use as object = ", usedAsObject);
								if (usedAsObject)
									doBreak = true;
								return;
							}

							public void caseAssignStmt(AssignStmt stmt) {
								Value left = stmt.getLeftOp();
								Value r = stmt.getRightOp();

								if (left instanceof ArrayRef) {
									if (((ArrayRef) left).getIndex() == l) {
										// doBreak = true;
										return;
									}
								}

								// IMPOSSIBLE! WOULD BE DEF!
								// // gets value assigned
								// if (stmt.getLeftOp() == l) {
								// if (r instanceof FieldRef)
								// usedAsObject = isObject(((FieldRef)
								// r).getFieldRef().type());
								// else if (r instanceof ArrayRef)
								// usedAsObject = isObject(((ArrayRef)
								// r).getType());
								// else if (r instanceof StringConstant || r
								// instanceof NewExpr || r instanceof
								// NewArrayExpr)
								// usedAsObject = true;
								// else if (r instanceof CastExpr)
								// usedAsObject = isObject
								// (((CastExpr)r).getCastType());
								// else if (r instanceof InvokeExpr)
								// usedAsObject = isObject(((InvokeExpr)
								// r).getType());
								// // introduces alias
								// else if (r instanceof Local) {}
								//
								// }
								// used to assign
								if (stmt.getRightOp() == l) {
									Value l = stmt.getLeftOp();
									if (l instanceof StaticFieldRef
											&& isObject(((StaticFieldRef) l).getFieldRef().type())) {
										usedAsObject = true;
										if (usedAsObject)
											doBreak = true;
										return;
									} else if (l instanceof InstanceFieldRef
											&& isObject(((InstanceFieldRef) l).getFieldRef().type())) {
										usedAsObject = true;
										if (usedAsObject)
											doBreak = true;
										return;
									} else if (l instanceof ArrayRef) {
										Type aType = ((ArrayRef) l).getType();
										if (aType instanceof UnknownType) {
											usedAsObject = stmt.hasTag("ObjectOpTag"); // isObject(
																						// findArrayType(g,
																						// localDefs,
																						// localUses,
																						// stmt));
										} else {
											usedAsObject = isObject(aType);
										}
										if (usedAsObject)
											doBreak = true;
										return;
									}
								}

								// is used as value (does not exclude
								// assignment)
								if (r instanceof FieldRef) {
									usedAsObject = true; // isObject(((FieldRef)
															// r).getFieldRef().type());
									if (usedAsObject)
										doBreak = true;
									return;
								} else if (r instanceof ArrayRef) {
									ArrayRef ar = (ArrayRef) r;
									if (ar.getBase() == l) {
										usedAsObject = true;
									} else { // used as index
										usedAsObject = false;
									}
									if (usedAsObject)
										doBreak = true;
									return;
								} else if (r instanceof StringConstant || r instanceof NewExpr) {
									throw new RuntimeException("NOT POSSIBLE StringConstant or NewExpr at "  + stmt);
								} else if (r instanceof NewArrayExpr) {
									usedAsObject = false;
									if (usedAsObject)
										doBreak = true;
									return;
								} else if (r instanceof CastExpr) {
									usedAsObject = isObject(((CastExpr) r).getCastType());
									if (usedAsObject)
										doBreak = true;
									return;
								} else if (r instanceof InvokeExpr) {
									usedAsObject = examineInvokeExpr((InvokeExpr) stmt.getRightOp());
									Debug.printDbg("use as object 2 = ", usedAsObject);
									if (usedAsObject)
										doBreak = true;
									return;
								} else if (r instanceof LengthExpr) {
									usedAsObject = true;
									if (usedAsObject)
										doBreak = true;
									return;
								} else if (r instanceof BinopExpr) {
									usedAsObject = false;
									if (usedAsObject)
										doBreak = true;
									return;
								}
							}

							public void caseIdentityStmt(IdentityStmt stmt) {
								if (stmt.getLeftOp() == l)
									throw new RuntimeException("IMPOSSIBLE 0");
							}

							public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
								usedAsObject = stmt.getOp() == l;
								if (usedAsObject)
									doBreak = true;
								return;
							}

							public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
								usedAsObject = stmt.getOp() == l;
								if (usedAsObject)
									doBreak = true;
								return;
							}

							public void caseReturnStmt(ReturnStmt stmt) {
								usedAsObject = stmt.getOp() == l && isObject(body.getMethod().getReturnType());
								Debug.printDbg(" [return stmt] ", stmt, " usedAsObject: ", usedAsObject,
										", return type: ", body.getMethod().getReturnType());
								Debug.printDbg(" class: ", body.getMethod().getReturnType().getClass());
								if (usedAsObject)
									doBreak = true;
								return;
							}

							public void caseThrowStmt(ThrowStmt stmt) {
								usedAsObject = stmt.getOp() == l;
								if (usedAsObject)
									doBreak = true;
								return;
							}
						});

						if (doBreak)
							break;

					} // for uses
					if (doBreak)
						break;
				} // for defs

				if (doBreak) // as soon as one def or use refers to an object
								// all defs from the two locals in the if must
								// be updated
					break;

			} // for two locals in if

			// change values
			if (usedAsObject) {
				Set<Unit> defsOp1 = localDefs.collectDefinitionsWithAliases(twoIfLocals[0]);
				Set<Unit> defsOp2 = localDefs.collectDefinitionsWithAliases(twoIfLocals[1]);
				defsOp1.addAll(defsOp2);
				for (Unit u : defsOp1) {
					Stmt s = (Stmt) u;
					// If we have a[x] = 0 and a is an object, we may not conclude 0 -> null
					if (!s.containsArrayRef() || (!defsOp1.contains(s.getArrayRef().getBase())
							&& !defsOp2.contains(s.getArrayRef().getBase())))
						replaceWithNull(u);
					
					Local l = (Local) ((DefinitionStmt) u).getLeftOp();
					for (Unit uuse : localDefs.getUsesOf(l)) {
						Stmt use = (Stmt) uuse;
						// If we have a[x] = 0 and a is an object, we may not conclude 0 -> null
						if (!use.containsArrayRef() || (twoIfLocals[0] != use.getArrayRef().getBase())
								&& twoIfLocals[1] != use.getArrayRef().getBase())
							replaceWithNull(use);
					}
				}
			} // end if

		} // for if statements
	}

	/**
	 * Collect all the if statements comparing two locals with an Eq or Ne
	 * expression
	 *
	 * @param body
	 *            the body to analyze
	 */
	private Set<IfStmt> getNullIfCandidates(Body body) {
		Set<IfStmt> candidates = new HashSet<IfStmt>();
		Iterator<Unit> i = body.getUnits().iterator();
		while (i.hasNext()) {
			Unit u = i.next();
			if (u instanceof IfStmt) {
				ConditionExpr expr = (ConditionExpr) ((IfStmt) u).getCondition();
				boolean isTargetIf = false;
				if (((expr instanceof EqExpr) || (expr instanceof NeExpr))) {
					if (expr.getOp1() instanceof Local && expr.getOp2() instanceof Local) {
						isTargetIf = true;
					}
				}
				if (isTargetIf) {
					candidates.add((IfStmt) u);
					Debug.printDbg("[add if candidate: ", u);
				}

			}
		}

		return candidates;
	}

}
