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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.ArrayType;
import soot.Body;
import soot.Local;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.FieldRef;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NullConstant;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThrowStmt;
import soot.jimple.internal.AbstractInstanceInvokeExpr;
import soot.jimple.internal.AbstractInvokeExpr;

/**
 * BodyTransformer to find and change IntConstant(0) to NullConstant where
 * locals are used as objects.
 *
 * @author Michael Markert
 */
public class DexNullTransformer extends AbstractNullTransformer {
	// Note: we need an instance variable for inner class access, treat this as
	// a local variable (including initialization before use)

	private boolean usedAsObject;
	private boolean doBreak = false;

	public static DexNullTransformer v() {
		return new DexNullTransformer();
	}

	private Local l = null;

	protected void internalTransform(final Body body, String phaseName, Map<String,String> options) {
		final DexDefUseAnalysis localDefs = new DexDefUseAnalysis(body);
		
		for (Local loc : getNullCandidates(body)) {
			Debug.printDbg("\n[null candidate] ", loc);
			usedAsObject = false;
			Set<Unit> defs = localDefs.collectDefinitionsWithAliases(loc);
			// process normally
			doBreak = false;
			for (Unit u : defs) {
				// put correct local in l
				if (u instanceof DefinitionStmt) {
					l = (Local) ((DefinitionStmt) u).getLeftOp();
				} else if (u instanceof IfStmt) {
					throw new RuntimeException(
							"ERROR: def can not be something else than Assign or Identity statement! (def: "
									+ u + " class: " + u.getClass() + "");
				}

				Debug.printDbg("    target local: ", l, " (Unit: ", u, " )");

				// check defs
				u.apply(new AbstractStmtSwitch() { // Alex: should also end as
													// soon as detected as not
													// used as an object
					public void caseAssignStmt(AssignStmt stmt) {
						Value r = stmt.getRightOp();
						if (r instanceof FieldRef) {
							usedAsObject = isObject(((FieldRef) r)
									.getFieldRef().type());
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
							doBreak = true;
							return;
						} else if (r instanceof StringConstant
								|| r instanceof NewExpr
								|| r instanceof NewArrayExpr) {
							usedAsObject = true;
							doBreak = true;
							return;
						} else if (r instanceof CastExpr) {
							usedAsObject = isObject(((CastExpr) r)
									.getCastType());
							doBreak = true;
							return;
						} else if (r instanceof InvokeExpr) {
							usedAsObject = isObject(((InvokeExpr) r).getType());
							doBreak = true;
							return;
						} else if (r instanceof LengthExpr) {
							usedAsObject = false;
							doBreak = true;
							return;
							// introduces alias
						} else if (r instanceof Local) {
						}

					}

					public void caseIdentityStmt(IdentityStmt stmt) {
						if (stmt.getLeftOp() == l) {
							usedAsObject = isObject(stmt.getRightOp().getType());
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
							List<Type> argTypes = e.getMethodRef()
									.parameterTypes();
							assert args.size() == argTypes.size();
							for (int i = 0; i < args.size(); i++) {
								if (args.get(i) == l
										&& isObject(argTypes.get(i))) {
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
							doBreak = true;
							return;
						}

						public void caseAssignStmt(AssignStmt stmt) {
							Value left = stmt.getLeftOp();
							Value r = stmt.getRightOp();

							if (left instanceof ArrayRef) {
								ArrayRef ar = (ArrayRef) left;
								if (ar.getIndex() == l) {
									doBreak = true;
									return;
								}
								else if (ar.getBase() == l) {
									usedAsObject = true;
									doBreak = true;
									return;
								}
							}
							
							if (left instanceof InstanceFieldRef) {
								InstanceFieldRef ifr = (InstanceFieldRef) left;
								if (ifr.getBase() == l) {
									usedAsObject = true;
									doBreak = true;
									return;
								}
							}
							
							// used to assign
							if (stmt.getRightOp() == l) {
								Value l = stmt.getLeftOp();
								if (l instanceof StaticFieldRef
										&& isObject(((StaticFieldRef) l)
												.getFieldRef().type())) {
									usedAsObject = true;
									doBreak = true;
									return;
								} else if (l instanceof InstanceFieldRef
										&& isObject(((InstanceFieldRef) l)
												.getFieldRef().type())) {
									usedAsObject = true;
									doBreak = true;
									return;
								} else if (l instanceof ArrayRef) {
									Type aType = ((ArrayRef) l).getType();
									if (aType instanceof UnknownType) {
										usedAsObject = stmt
												.hasTag("ObjectOpTag"); // isObject(
																		// findArrayType(g,
																		// localDefs,
																		// localUses,
																		// stmt));
									} else {
										usedAsObject = isObject(aType);
									}
									doBreak = true;
									return;
								}
							}

							// is used as value (does not exclude assignment)
							if (r instanceof FieldRef) {
								usedAsObject = true; // isObject(((FieldRef)
														// r).getFieldRef().type());
								doBreak = true;
								return;
							} else if (r instanceof ArrayRef) {
								ArrayRef ar = (ArrayRef) r;
								if (ar.getBase() == l) {
									usedAsObject = true;
								} else { // used as index
									usedAsObject = false;
								}
								doBreak = true;
								return;
							} else if (r instanceof StringConstant
									|| r instanceof NewExpr) {
								throw new RuntimeException(
										"NOT POSSIBLE StringConstant or NewExpr at " + stmt);
							} else if (r instanceof NewArrayExpr) {
								usedAsObject = false;
								doBreak = true;
								return;
							} else if (r instanceof CastExpr) {
								usedAsObject = isObject(((CastExpr) r)
										.getCastType());
								doBreak = true;
								return;
							} else if (r instanceof InvokeExpr) {
								usedAsObject = examineInvokeExpr((InvokeExpr) stmt
										.getRightOp());
								Debug.printDbg("use as object 2 = ",
										usedAsObject);
								doBreak = true;
								return;
							} else if (r instanceof LengthExpr) {
								usedAsObject = true;
								doBreak = true;
								return;
							} else if (r instanceof BinopExpr) {
								usedAsObject = false;
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
							doBreak = true;
							return;
						}

						public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
							usedAsObject = stmt.getOp() == l;
							doBreak = true;
							return;
						}

						public void caseReturnStmt(ReturnStmt stmt) {
							usedAsObject = stmt.getOp() == l
									&& isObject(body.getMethod()
											.getReturnType());
							Debug.printDbg(" [return stmt] ", stmt,
									" usedAsObject: ", usedAsObject,
									", return type: ", body.getMethod()
											.getReturnType());
							Debug.printDbg(" class: ", body.getMethod()
									.getReturnType().getClass());
							doBreak = true;
							return;
						}

						public void caseThrowStmt(ThrowStmt stmt) {
							usedAsObject = stmt.getOp() == l;
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

			// change values
			if (usedAsObject) {
				for (Unit u : defs) {
					replaceWithNull(u);
					Set<Value> defLocals = new HashSet<Value>();
					for (ValueBox vb : u.getDefBoxes())
						defLocals.add(vb.getValue());
					
					Local l = (Local) ((DefinitionStmt) u).getLeftOp();
					for (Unit uuse : localDefs.getUsesOf(l)) {
						Stmt use = (Stmt) uuse;
						// If we have a[x] = 0 and a is an object, we may not conclude 0 -> null
						if (!use.containsArrayRef()
								|| !defLocals.contains(use.getArrayRef().getBase()))
							replaceWithNull(use);
					}
				}
			} // end if
		}

		// Check for inlined zero values
		for (Unit u : body.getUnits()) {
			u.apply(new AbstractStmtSwitch() {
				@Override
				public void caseAssignStmt(AssignStmt stmt) {
					// Case a = 0 with a being an object
					if (isObject(stmt.getLeftOp().getType())
							&& isConstZero(stmt.getRightOp())) {
						stmt.setRightOp(NullConstant.v());
						return;
					}
					
					// Case a = (Object) 0
					if (stmt.getRightOp() instanceof CastExpr) {
						CastExpr ce = (CastExpr) stmt.getRightOp();
						if (isObject(ce.getCastType()) && isConstZero(ce.getOp())) {
							stmt.setRightOp(NullConstant.v());
						}
					}
					
					// Case a[0] = 0
					if (stmt.getLeftOp() instanceof ArrayRef
							&& isConstZero(stmt.getRightOp())) {
						ArrayRef ar = (ArrayRef) stmt.getLeftOp();
						if (isObjectArray(ar.getBase(), body)
								|| stmt.hasTag("ObjectOpTag")) {
							stmt.setRightOp(NullConstant.v());
						}
					}
				}

				private boolean isConstZero(Value rightOp) {
					if (rightOp instanceof IntConstant && ((IntConstant) rightOp).value == 0)
						return true;
					if (rightOp instanceof LongConstant && ((LongConstant) rightOp).value == 0)
						return true;
					return false;
				}

				@Override
				public void caseReturnStmt(ReturnStmt stmt) {
					if (stmt.getOp() instanceof IntConstant
							&& isObject(body.getMethod().getReturnType())) {
						IntConstant iconst = (IntConstant) stmt.getOp();
						assert iconst.value == 0;
						stmt.setOp(NullConstant.v());
					}
				}
				
				@Override
				public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
					if (stmt.getOp() instanceof IntConstant && ((IntConstant) stmt.getOp()).value == 0)
						stmt.setOp(NullConstant.v());
				}
				
				@Override
				public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
					if (stmt.getOp() instanceof IntConstant && ((IntConstant) stmt.getOp()).value == 0)
						stmt.setOp(NullConstant.v());
				}
				
			});
			if (u instanceof Stmt) {
				Stmt stmt = (Stmt) u;
				if (stmt.containsInvokeExpr()) {
					InvokeExpr invExpr = stmt.getInvokeExpr();
					for (int i = 0; i < invExpr.getArgCount(); i++)
						if (isObject(invExpr.getMethodRef().parameterType(i)))
							if (invExpr.getArg(i) instanceof IntConstant) {
								IntConstant iconst = (IntConstant) invExpr
										.getArg(i);
								assert iconst.value == 0;
								invExpr.setArg(i, NullConstant.v());
							}
				}
			}
		}
	}

	private boolean isObjectArray(Value v, Body body) {
		for (Unit u : body.getUnits()) {
			if (u instanceof AssignStmt) {
				AssignStmt assign = (AssignStmt) u;
				if (assign.getLeftOp() == v) {
					if (assign.getRightOp() instanceof NewArrayExpr) {
						NewArrayExpr nea = (NewArrayExpr) assign.getRightOp();
						if (isObject(nea.getBaseType()))
							return true;
					}
					else if (assign.getRightOp() instanceof FieldRef) {
						FieldRef fr = (FieldRef) assign.getRightOp();
						if (fr.getType() instanceof ArrayType)
							if (isObject(((ArrayType) fr.getType())
									.getArrayElementType()))
								return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Collect all the locals which are assigned a IntConstant(0) or are used
	 * within a zero comparison.
	 *
	 * @param body
	 *            the body to analyze
	 */
	private Set<Local> getNullCandidates(Body body) {
		Set<Local> candidates = null;
		for (Unit u : body.getUnits()) {
			if (u instanceof AssignStmt) {
				AssignStmt a = (AssignStmt) u;
				if (!(a.getLeftOp() instanceof Local))
					continue;
				Local l = (Local) a.getLeftOp();
				Value r = a.getRightOp();
				if ((r instanceof IntConstant && ((IntConstant) r).value == 0)
						|| (r instanceof LongConstant && ((LongConstant) r).value == 0)) {
					if (candidates == null)
						candidates = new HashSet<Local>();
					candidates.add(l);
					Debug.printDbg("[add null candidate: ", u);
				}
			} else if (u instanceof IfStmt) {
				ConditionExpr expr = (ConditionExpr) ((IfStmt) u)
						.getCondition();
				if (isZeroComparison(expr) && expr.getOp1() instanceof Local) {
					if (candidates == null)
						candidates = new HashSet<Local>();
					candidates.add((Local) expr.getOp1());
					Debug.printDbg("[add null candidate if: ", u);
				}
			}
		}

		return candidates == null ? Collections.<Local>emptySet() : candidates;
	}
	
}
