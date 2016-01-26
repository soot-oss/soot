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

import soot.Body;
import soot.DoubleType;
import soot.FloatType;
import soot.Local;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.CmpExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.DoubleConstant;
import soot.jimple.FieldRef;
import soot.jimple.FloatConstant;
import soot.jimple.IdentityStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.NewArrayExpr;
import soot.jimple.ReturnStmt;

/**
 * BodyTransformer to find and change initialization type of Jimple variables.
 * Dalvik bytecode does not provide enough information regarding the type of
 * initialized variables. For instance, using the dexdump disassembler on some
 * Dalvik bytecode can produce the following (wrong) output:
 * 
 * 006c : const -wide v6 , #double 0.000000 // #0014404410000000 0071: and-long
 * /2 addr v6 , v4
 * 
 * At 0x6c, the initialized register is not of type double, but of type long
 * because it is used in a long and operation at 0x71. Thus, one need to check
 * how the register is used to deduce its type. By default, and since the
 * dexdump disassembler does not perform such analysis, it supposes the register
 * is of type double.
 * 
 * Dalvik comes with the following instructions to initialize constants: 0x12
 * const/4 vx,lit4 0x13 const/16 vx,lit16 0x14 const vx, lit32 0x15 const/high16
 * v0, lit16 0x16 const-wide/16 vx, lit16 0x17 const-wide/32 vx, lit32 0x18
 * const-wide vx, lit64 0x19 const-wide/high16 vx,lit16 0x1A const-string
 * vx,string id 0x1B const-string-jumbo vx,string 0x1C const-class vx,type id
 *
 * Instructions 0x12, 0x1A, 0x1B, 0x1C can not produce wrong initialized
 * registers. The other instructions are converted to the following Jimple
 * statement: JAssignStmt ( Local, rightValue ). Since at the time of the
 * statement creation the no analysis can be performed, a default type is given
 * to rightValue. This default type is "int" for registers whose size is less or
 * equal to 32bits and "long" to registers whose size is 64bits. The problem is
 * that 32bits registers could be either "int" or "float" and 64bits registers
 * "long" or "double". If the analysis concludes that an "int" has to be changed
 * to a "float", rightValue has to change from IntConstant.v(literal) to
 * Float.intBitsToFloat((int) literal). If the analysis concludes that an "long"
 * has to be changed to a "double, rightValue has to change from
 * LongConstant.v(literal) to
 * DoubleConstant.v(Double.longBitsToDouble(literal)).
 */
public class DexNumTransformer extends DexTransformer {
	// Note: we need an instance variable for inner class access, treat this as
	// a local variable (including initialization before use)

	private boolean usedAsFloatingPoint;
	boolean doBreak = false;

	public static DexNumTransformer v() {
		return new DexNumTransformer();
	}
	
	protected void internalTransform(final Body body, String phaseName, Map<String,String> options) {
		final DexDefUseAnalysis localDefs = new DexDefUseAnalysis(body);
		
        for (Local loc : getNumCandidates(body)) {
            Debug.printDbg("\n[num candidate] ", loc);
			usedAsFloatingPoint = false;
			Set<Unit> defs = localDefs.collectDefinitionsWithAliases(loc);
			
	        // process normally
			doBreak = false;
			for (Unit u : defs) {
				// put correct local in l
				final Local l = u instanceof DefinitionStmt ? (Local) ((DefinitionStmt) u).getLeftOp()
						: null;
				
		        Debug.printDbg("    def  : ", u);
				Debug.printDbg("    local: ", l);

				// check defs
				u.apply(new AbstractStmtSwitch() {
					public void caseAssignStmt(AssignStmt stmt) {
						Value r = stmt.getRightOp();
						if (r instanceof BinopExpr && !(r instanceof CmpExpr)) {
							usedAsFloatingPoint = examineBinopExpr((Unit) stmt);
							doBreak = true;
						} else if (r instanceof FieldRef) {
							usedAsFloatingPoint = isFloatingPointLike(((FieldRef) r)
									.getFieldRef().type());
							doBreak = true;
						} else if (r instanceof NewArrayExpr) {
							NewArrayExpr nae = (NewArrayExpr) r;
							Type t = nae.getType();
							Debug.printDbg("new array expr: ", nae, " type: ",
									t);
							usedAsFloatingPoint = isFloatingPointLike(t);
							doBreak = true;
						} else if (r instanceof ArrayRef) {
							ArrayRef ar = (ArrayRef) r;
							Type arType = ar.getType();
							Debug.printDbg("ar: ", r, " ", arType);
							if (arType instanceof UnknownType) {
								Type t = findArrayType(localDefs,
										stmt, 0, Collections.<Unit> emptySet()); // TODO:
																					// check
																					// where
																					// else
																					// to
																					// update
																					// if(ArrayRef...
								Debug.printDbg(" array type:", t);
								usedAsFloatingPoint = isFloatingPointLike(t);
							} else {
								usedAsFloatingPoint = isFloatingPointLike(ar
										.getType());
							}
							doBreak = true;
						} else if (r instanceof CastExpr) {
							usedAsFloatingPoint = isFloatingPointLike(((CastExpr) r)
									.getCastType());
							doBreak = true;
						} else if (r instanceof InvokeExpr) {
							usedAsFloatingPoint = isFloatingPointLike(((InvokeExpr) r)
									.getType());
							doBreak = true;
						} else if (r instanceof LengthExpr) {
							usedAsFloatingPoint = false;
							doBreak = true;
						}
						// introduces alias
						else if (r instanceof Local) {
						}

					}

					public void caseIdentityStmt(IdentityStmt stmt) {
						Debug.printDbg("h");
						if (stmt.getLeftOp() == l) {
							usedAsFloatingPoint = isFloatingPointLike(stmt
									.getRightOp().getType());
							doBreak = true;
						}
					}
				});

				if (doBreak) {
					break;
				}

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
										&& isFloatingPointLike(argTypes.get(i))) {
									return true;
								}
							}
							return false;
						}

						public void caseInvokeStmt(InvokeStmt stmt) {
							InvokeExpr e = stmt.getInvokeExpr();
							usedAsFloatingPoint = examineInvokeExpr(e);
						}

						public void caseAssignStmt(AssignStmt stmt) {
							// only case where 'l' could be on the left side is
							// arrayRef with 'l' as the index
							Value left = stmt.getLeftOp();
							if (left instanceof ArrayRef) {
								ArrayRef ar = (ArrayRef) left;
								if (ar.getIndex() == l) {
									doBreak = true;
									return;
								}
							}

							// from this point, we only check the right hand
							// side of the assignment
							Value r = stmt.getRightOp();
							if (r instanceof ArrayRef) {
								if (((ArrayRef) r).getIndex() == l) {
									doBreak = true;
									return;
								}
							} else if (r instanceof InvokeExpr) {
								usedAsFloatingPoint = examineInvokeExpr((InvokeExpr) r);
								doBreak = true;
								return;
							} else if (r instanceof BinopExpr) {
								usedAsFloatingPoint = examineBinopExpr((Unit) stmt);
								doBreak = true;
								return;
							} else if (r instanceof CastExpr) {
								usedAsFloatingPoint = stmt.hasTag("FloatOpTag")
										|| stmt.hasTag("DoubleOpTag");
								doBreak = true;
								return;
							} else if (r instanceof Local && r == l) {
								if (left instanceof FieldRef) {
									FieldRef fr = (FieldRef) left;
									if (isFloatingPointLike(fr.getType())) {
										usedAsFloatingPoint = true;
									}
									doBreak = true;
									return;
								} else if (left instanceof ArrayRef) {
									ArrayRef ar = (ArrayRef) left;
									Type arType = ar.getType();
									Debug.printDbg("ar: ", r, " ", arType);
									if (arType instanceof UnknownType) {
										arType = findArrayType(localDefs, stmt, 0, Collections.<Unit> emptySet());
									}
									Debug.printDbg(" array type:", arType);
									usedAsFloatingPoint = isFloatingPointLike(arType);
									doBreak = true;
									return;
								}
							}

						}

						public void caseReturnStmt(ReturnStmt stmt) {
							usedAsFloatingPoint = stmt.getOp() == l
									&& isFloatingPointLike(body.getMethod()
											.getReturnType());
							Debug.printDbg(" [return stmt] ", stmt,
									" usedAsObject: ", usedAsFloatingPoint,
									", return type: ", body.getMethod()
											.getReturnType());
							Debug.printDbg(" class: ", body.getMethod()
									.getReturnType().getClass());
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
			if (usedAsFloatingPoint) {
				for (Unit u : defs) {
					replaceWithFloatingPoint(u);
				}
			} // end if

		}
	}

	protected boolean examineBinopExpr(Unit u) {
		if (u.hasTag("FloatOpTag") || u.hasTag("DoubleOpTag")) {
			return true;
		}
		return false;
	}

	private boolean isFloatingPointLike(Type t) {
		return (t instanceof FloatType || t instanceof DoubleType);
	}

	/**
	 * Collect all the locals which are assigned a IntConstant(0) or are used
	 * within a zero comparison.
	 *
	 * @param body
	 *            the body to analyze
	 */
	private Set<Local> getNumCandidates(Body body) {
		Set<Local> candidates = new HashSet<Local>();
		for (Unit u : body.getUnits()) {
			if (u instanceof AssignStmt) {
				AssignStmt a = (AssignStmt) u;
				if (!(a.getLeftOp() instanceof Local))
					continue;
				Local l = (Local) a.getLeftOp();
				Value r = a.getRightOp();
				if ((r instanceof IntConstant || r instanceof LongConstant)) {
					candidates.add(l);
					Debug.printDbg("[add num candidate: ", u);
				}
			}
		}

		return candidates;
	}

	/**
	 * Replace 0 with null in the given unit.
	 *
	 * @param u
	 *            the unit where 0 will be replaced with null.
	 */
	private void replaceWithFloatingPoint(Unit u) {
		if (u instanceof AssignStmt) {
			AssignStmt s = (AssignStmt) u;
			Value v = s.getRightOp();
			if ((v instanceof IntConstant)) {
				int vVal = ((IntConstant) v).value;
				s.setRightOp(FloatConstant.v(Float.intBitsToFloat((int) vVal)));
				Debug.printDbg("[floatingpoint] replacing with float in ", u);
			} else if (v instanceof LongConstant) {
				long vVal = ((LongConstant) v).value;
				s.setRightOp(DoubleConstant.v(Double
						.longBitsToDouble((long) vVal)));
				Debug.printDbg("[floatingpoint] replacing with double in ", u);
			}
		}

	}

}
