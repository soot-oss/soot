// 
// (c) 2012 University of Luxembourg - Interdisciplinary Centre for 
// Security Reliability and Trust (SnT) - All rights reserved
//
// Author: Alexandre Bartel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 2.1 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>. 
//

package soot.dexpler.typing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.ArrayType;
import soot.Body;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.PrimType;
import soot.RefType;
import soot.ShortType;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.Value;
import soot.ValueBox;
import soot.dexpler.Debug;
import soot.dexpler.IDalvikTyper;
import soot.dexpler.tags.DoubleOpTag;
import soot.dexpler.tags.FloatOpTag;
import soot.dexpler.tags.IntOpTag;
import soot.dexpler.tags.LongOpTag;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.BreakpointStmt;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.DivExpr;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.NewArrayExpr;
import soot.jimple.NopStmt;
import soot.jimple.NullConstant;
import soot.jimple.RemExpr;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StmtSwitch;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;
import soot.jimple.UnopExpr;
import soot.jimple.UshrExpr;
import soot.tagkit.Tag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;

public class DalvikTyper implements IDalvikTyper {

    private static DalvikTyper dt = null;
    
    private Set<Constraint> constraints = new HashSet<Constraint>();
    private Map<ValueBox, Type> typed = new HashMap<ValueBox, Type>();
    private Map<Local, Type> localTyped = new HashMap<Local, Type>();
    private Set<Local> localTemp = new HashSet<Local>();
    private List<LocalObj> localObjList = new ArrayList<LocalObj>();
	private Map<Local, List<LocalObj>> local2Obj = new HashMap<Local, List<LocalObj>>();
    
    private DalvikTyper() {}
    
    public static DalvikTyper v() {
        if (dt == null)
            dt = new DalvikTyper();
        return dt;
    }
    
    public void clear() {
        constraints.clear();
        typed.clear();
        localTyped.clear();
        localTemp.clear();
        localObjList.clear();
		local2Obj.clear();
    }
    
	public void setType(ValueBox vb, Type t, boolean isUse) {
	    if (IDalvikTyper.DEBUG)
			Debug.printDbg(IDalvikTyper.DEBUG, "   [setType] ", vb, " -> ", t);

		if (t instanceof UnknownType) {

			throw new RuntimeException("error: expected concreted type. Got " + t);
		}

        if (vb.getValue() instanceof Local) {
			LocalObj lb = new LocalObj(vb, t, isUse);
			localObjList.add(lb);
			Local k = (Local) vb.getValue();
			if (!local2Obj.containsKey(k)) {
				local2Obj.put(k, new ArrayList<LocalObj>());
			}
			local2Obj.get(k).add(lb);
        } else {
			Debug.printDbg(IDalvikTyper.DEBUG, "not instance of local: vb: ", vb, " value: ", vb.getValue(), " class: ", vb.getValue().getClass());
        }
	}

	public void addConstraint(ValueBox l, ValueBox r) {
	    if (IDalvikTyper.DEBUG)
			Debug.printDbg(IDalvikTyper.DEBUG, "   [addConstraint] ", l, " < ", r);
		constraints.add(new Constraint(l, r));		
	}
	

	public void assignType(final Body b) {
	    
		Debug.printDbg("assignTypes: before: \n", b);

		constraints.clear();
		localObjList.clear();

		final Set<Unit> todoUnits = new HashSet<Unit>();


		// put constraints:
		for (Unit u : b.getUnits()) {
			StmtSwitch ss = new StmtSwitch() {

				@Override
				public void caseBreakpointStmt(BreakpointStmt stmt) {
					// nothing

				}

				@Override
				public void caseInvokeStmt(InvokeStmt stmt) {
					// add constraint
					DalvikTyper.v().setInvokeType(stmt.getInvokeExpr());

				}

				@Override
				public void caseAssignStmt(AssignStmt stmt) {
					// add constraint
					Value l = stmt.getLeftOp();
					Value r = stmt.getRightOp();

					// size in new array expression is of tye integer
					if (r instanceof NewArrayExpr) {
						NewArrayExpr nae = (NewArrayExpr) r;
						ValueBox sb = nae.getSizeBox();
						if (sb.getValue() instanceof Local) {
							DalvikTyper.v().setType(sb, IntType.v(), true);
						}
					}

					// array index is of type integer
					if (stmt.containsArrayRef()) {
						ArrayRef ar = stmt.getArrayRef();
						ValueBox sb = ar.getIndexBox();
						if (sb.getValue() instanceof Local) {
							DalvikTyper.v().setType(sb, IntType.v(), true);
						}
					}

					if (l instanceof Local && r instanceof Local) {
						DalvikTyper.v().addConstraint(stmt.getLeftOpBox(), stmt.getRightOpBox());
						return;
					}

					if (stmt.containsInvokeExpr()) {
						DalvikTyper.v().setInvokeType(stmt.getInvokeExpr());
					}

					if (r instanceof Local) { // l NOT local
						Type leftType = stmt.getLeftOp().getType();
						if (l instanceof ArrayRef && leftType instanceof UnknownType) {
							// find type later
							todoUnits.add(stmt);
							return;
						}
						DalvikTyper.v().setType(stmt.getRightOpBox(), leftType, true);
						return;
					}

					if (l instanceof Local) { // r NOT local

						if (r instanceof UntypedConstant)
							return;
						for (Tag t : stmt.getTags()) {

							if (r instanceof CastExpr) {
								// do not check tag, since the tag is for the operand of the cast
								break;
							}

							Debug.printDbg("assign stmt tag: ", stmt, t);
							if (t instanceof IntOpTag) {
								checkExpr(r, IntType.v());
								DalvikTyper.v().setType(stmt.getLeftOpBox(), IntType.v(), false);
								return;
							} else if (t instanceof FloatOpTag) {
								checkExpr(r, FloatType.v());
								DalvikTyper.v().setType(stmt.getLeftOpBox(), FloatType.v(), false);
								return;
							} else if (t instanceof DoubleOpTag) {
								checkExpr(r, DoubleType.v());
								DalvikTyper.v().setType(stmt.getLeftOpBox(), DoubleType.v(), false);
								return;
							} else if (t instanceof LongOpTag) {
								checkExpr(r, LongType.v());
								DalvikTyper.v().setType(stmt.getLeftOpBox(), LongType.v(), false);
								return;
							}
						}
						Type rightType = stmt.getRightOp().getType();
						if (r instanceof ArrayRef && rightType instanceof UnknownType) {
							// find type later
							todoUnits.add(stmt);
							return;
						} else if (r instanceof CastExpr) {
							CastExpr ce = (CastExpr) r;
							Type castType = ce.getCastType();
							if (castType instanceof PrimType) {
								// check incoming primitive type
								for (Tag t : stmt.getTags()) {
									Debug.printDbg("assign primitive type from stmt tag: ", stmt, t);
									if (t instanceof IntOpTag) {
										DalvikTyper.v().setType(ce.getOpBox(), IntType.v(), false);
										return;
									} else if (t instanceof FloatOpTag) {
										DalvikTyper.v().setType(ce.getOpBox(), FloatType.v(), false);
										return;
									} else if (t instanceof DoubleOpTag) {
										DalvikTyper.v().setType(ce.getOpBox(), DoubleType.v(), false);
										return;
									} else if (t instanceof LongOpTag) {
										DalvikTyper.v().setType(ce.getOpBox(), LongType.v(), false);
										return;
									}
								}
							} else {
								// incoming type is object
								DalvikTyper.v().setType(ce.getOpBox(), RefType.v("java.lang.Object"), false);
							}
						}
						DalvikTyper.v().setType(stmt.getLeftOpBox(), rightType, false);
						return;
					}

				}

				@Override
				public void caseIdentityStmt(IdentityStmt stmt) {
					DalvikTyper.v().setType(stmt.getLeftOpBox(), stmt.getRightOp().getType(), false);

				}

				@Override
				public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
					// add constraint
					DalvikTyper.v().setType(stmt.getOpBox(), RefType.v("java.lang.Object"), true);

				}

				@Override
				public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
					// add constraint
					DalvikTyper.v().setType(stmt.getOpBox(), RefType.v("java.lang.Object"), true);
				}

				@Override
				public void caseGotoStmt(GotoStmt stmt) {
					// nothing

				}

				@Override
				public void caseIfStmt(IfStmt stmt) {
					// add constraint
					Value c = stmt.getCondition();
					if (c instanceof BinopExpr) {
						BinopExpr bo = (BinopExpr) c;
						Value op1 = bo.getOp1();
						Value op2 = bo.getOp2();
						if (op1 instanceof Local && op2 instanceof Local) {
							DalvikTyper.v().addConstraint(bo.getOp1Box(), bo.getOp2Box());
						}
					}

				}

				@Override
				public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
					// add constraint
					DalvikTyper.v().setType(stmt.getKeyBox(), IntType.v(), true);

				}

				@Override
				public void caseNopStmt(NopStmt stmt) {
					// nothing

				}

				@Override
				public void caseRetStmt(RetStmt stmt) {
					// nothing

				}

				@Override
				public void caseReturnStmt(ReturnStmt stmt) {
					// add constraint
					DalvikTyper.v().setType(stmt.getOpBox(), b.getMethod().getReturnType(), true);

				}

				@Override
				public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
					// nothing

				}

				@Override
				public void caseTableSwitchStmt(TableSwitchStmt stmt) {
					// add constraint
					DalvikTyper.v().setType(stmt.getKeyBox(), IntType.v(), true);

				}

				@Override
				public void caseThrowStmt(ThrowStmt stmt) {
					// add constraint
					DalvikTyper.v().setType(stmt.getOpBox(), RefType.v("java.lang.Object"), true);

				}

				@Override
				public void defaultCase(Object obj) {
					throw new RuntimeException("error: unknown statement: " + obj);

				}

			};
			u.apply(ss);
		}

		// print todo list:
		// <com.admob.android.ads.q: void a(android.os.Bundle,java.lang.String,java.lang.Object)>
		if (!todoUnits.isEmpty()) {

			// propagate array types
			UnitGraph ug = new ExceptionalUnitGraph(b);
			SimpleLocalDefs sld = new SimpleLocalDefs(ug);
			SimpleLocalUses slu = new SimpleLocalUses(b, sld);

			for (Unit u : b.getUnits()) {
				if (u instanceof DefinitionStmt) {
					Debug.printDbg("U: ", u);
					DefinitionStmt ass = (DefinitionStmt) u;
					Value r = ass.getRightOp();

					if (r instanceof UntypedConstant)
						continue;

					Type rType = r.getType();
					if (rType instanceof ArrayType && ass.getLeftOp() instanceof Local) {
						Debug.printDbg("propagate-array: checking ", u);
						// propagate array type through aliases
						Set<Unit> done = new HashSet<Unit>();
						Set<DefinitionStmt> toDo = new HashSet<DefinitionStmt>();
						toDo.add(ass);
						while (!toDo.isEmpty()) {
							DefinitionStmt currentUnit = toDo.iterator().next();
							if (done.contains(currentUnit)) {
								toDo.remove(currentUnit);
								continue;
							}
							done.add(currentUnit);

							for (UnitValueBoxPair uvbp : slu.getUsesOf(currentUnit)) {
								Unit use = uvbp.unit;
								Value l2 = null;
								Value r2 = null;
								if (use instanceof AssignStmt) {
									AssignStmt ass2 = (AssignStmt) use;
									l2 = ass2.getLeftOp();
									r2 = ass2.getRightOp();
									if (!(l2 instanceof Local) || !(r2 instanceof Local || r2 instanceof ArrayRef)) {
										Debug.printDbg("propagate-array: skipping ", use);
										continue;
									}


									Type newType = null;
									if (r2 instanceof Local) {
										List<LocalObj> lobjs = local2Obj.get(r2);
										newType = lobjs.get(0).t;

									} else if (r2 instanceof ArrayRef) {

										ArrayRef ar = (ArrayRef) r2;
										// skip if use is in index
										if (ar.getIndex() == currentUnit.getLeftOp()) {
											Debug.printDbg("skipping since local is used as index...");
											continue;
										}

										Local arBase = (Local) ar.getBase();
										List<LocalObj> lobjs = local2Obj.get(arBase);
										Type baseT = lobjs.get(0).t;
										if (baseT.toString().equals(("java.lang.Object"))) {
											// look for an array type, because an TTT[] is also an Object...
											ArrayType aTypeOtherThanObject = null;
											for (LocalObj lo : local2Obj.get(arBase)) {
												if (lo.t instanceof ArrayType) {
													aTypeOtherThanObject = (ArrayType) lo.t;
												}
											}
											if (aTypeOtherThanObject == null) {
												throw new RuntimeException("error: did not found array type for base " + arBase + " " + local2Obj.get(arBase) + " \n " + b);
											}
											baseT = aTypeOtherThanObject;
										}

										ArrayType at = (ArrayType) baseT;
										newType = at.getElementType();
									} else {
										throw new RuntimeException("error: expected Local or ArrayRef. Got " + r2);
									}

									toDo.add((DefinitionStmt) use);
									DalvikTyper.v().setType(ass2.getLeftOpBox(), newType, true);
								}
							}
						}

					}
				}
			}

			for (Unit u : todoUnits) {
				Debug.printDbg("todo unit: ", u);
			}

			while (!todoUnits.isEmpty()) {
				Unit u = todoUnits.iterator().next();
				if (!(u instanceof AssignStmt)) {
					throw new RuntimeException("error: expecting assign stmt. Got " + u);
				}
				AssignStmt ass = (AssignStmt) u;
				Value l = ass.getLeftOp();
				Value r = ass.getRightOp();
				ArrayRef ar = null;
				Local loc = null;
				if (l instanceof ArrayRef) {
					ar = (ArrayRef) l;
					loc = (Local) r;
				} else if (r instanceof ArrayRef) {
					ar = (ArrayRef) r;
					loc = (Local) l;
				} else {
					throw new RuntimeException("error: expecting an array ref. Got " + u);
				}

				Local baselocal = (Local) ar.getBase();
				if (!local2Obj.containsKey(baselocal)) {
					Debug.printDbg("oups no baselocal! for ", u);
					Debug.printDbg("b: ", b.getMethod(), " \n", b);
					throw new RuntimeException("oups");
				}

				Type baseT = local2Obj.get(baselocal).get(0).t;
				if (baseT.toString().equals(("java.lang.Object"))) {
					// look for an array type, because an TTT[] is also an Object...
					ArrayType aTypeOtherThanObject = null;
					for (LocalObj lo : local2Obj.get(baselocal)) {
						if (lo.t instanceof ArrayType) {
							aTypeOtherThanObject = (ArrayType) lo.t;
						}
					}
					if (aTypeOtherThanObject == null) {
						throw new RuntimeException("did not found array type for base " + baselocal + " " + local2Obj.get(baselocal) + " \n " + b);
					}
					baseT = aTypeOtherThanObject;
				}
				ArrayType basetype = (ArrayType) baseT;

				Debug.printDbg("v: ", ar, " base:", ar.getBase(), " base type: ", basetype, " type: ", ar.getType());

				Type t = basetype.getElementType();
				if (t instanceof UnknownType) {
					todoUnits.add(u);
					continue;
				} else {
					DalvikTyper.v().setType(ar == l ? ass.getRightOpBox() : ass.getLeftOpBox(), t, true);
					todoUnits.remove(u);
				}

			}

			//throw new RuntimeException("ouppppp");
		}
	    
	    Debug.printDbg(IDalvikTyper.DEBUG, "list of constraints:");
	    List<ValueBox> vbList = b.getUseAndDefBoxes();
	    
	    // clear constraints after local splitting and dead code eliminator
	    List<Constraint> toRemove = new ArrayList<Constraint>();
		for (Constraint c: constraints) {    
		    if (!vbList.contains(c.l)) {
				Debug.printDbg(IDalvikTyper.DEBUG, "warning: ", c.l, " not in locals! removing...");
		        toRemove.add(c);
		        continue;
		    }
		    if (!vbList.contains(c.r)) {
				Debug.printDbg(IDalvikTyper.DEBUG, "warning: ", c.r, " not in locals! removing...");
                toRemove.add(c);
                continue;
		    }
		}
		for (Constraint c: toRemove)
		    constraints.remove(c);
		
		// keep only valid locals
		for (LocalObj lo: localObjList) {
		    if (!vbList.contains(lo.vb)) {
				Debug.printDbg(IDalvikTyper.DEBUG, "  -- removing vb: ", lo.vb, " with type ", lo.t);
		        continue;
		    }
		    Local l = lo.getLocal();
		    Type t = lo.t;
            if (localTemp.contains(l) && lo.isUse) {
				Debug.printDbg(IDalvikTyper.DEBUG, "  /!\\ def already added for local ", l, "! for vb: ", lo.vb);
            } else {
				Debug.printDbg(IDalvikTyper.DEBUG, "  * add type ", t, " to local ", l, " for vb: ", lo.vb);
                localTemp.add(l);
                typed.put(lo.vb, t);
            }
		}
	    for (ValueBox vb: typed.keySet()) {
	        if (vb.getValue() instanceof Local) {
	            Local l = (Local)vb.getValue();
	            localTyped.put(l, typed.get(vb));
	        }
	    }
		
		for (Constraint c: constraints)
			Debug.printDbg(IDalvikTyper.DEBUG, "  -> constraint: ", c);
		for (ValueBox vb: typed.keySet()) {
			Debug.printDbg(IDalvikTyper.DEBUG, "    typed: ", vb, " -> ", typed.get(vb));
		}
		for (Local l: localTyped.keySet()){
			Debug.printDbg(IDalvikTyper.DEBUG, "    localTyped: ", l, " -> ", localTyped.get(l));
		}
		
		while (!constraints.isEmpty()) {
		    boolean update = false;
		    for (Constraint c: constraints) {
				Debug.printDbg(IDalvikTyper.DEBUG, "current constraint: ", c);
		        Value l = c.l.getValue();
		        Value r = c.r.getValue();
		        if (l instanceof Local && r instanceof Constant) {
		            Constant cst = (Constant)r;
		            if (!localTyped.containsKey(l))
		                continue;
		            Type lt = localTyped.get((Local)l);
					Debug.printDbg(IDalvikTyper.DEBUG, "would like to set type ", lt, " to constant: ", c);
		            Value newValue = null;
		            if (lt instanceof IntType
		                    || lt instanceof BooleanType
		                    || lt instanceof ShortType
		                    || lt instanceof CharType
		                    || lt instanceof ByteType) {
		                UntypedIntOrFloatConstant uf = (UntypedIntOrFloatConstant)cst;
		                newValue = uf.toIntConstant();
		            } else if (lt instanceof FloatType) {
		                UntypedIntOrFloatConstant uf = (UntypedIntOrFloatConstant)cst;
		                newValue = uf.toFloatConstant();
		            } else if (lt instanceof DoubleType) {
		                UntypedLongOrDoubleConstant ud = (UntypedLongOrDoubleConstant)cst;
		                newValue = ud.toDoubleConstant();
		            } else if (lt instanceof LongType) {
		                UntypedLongOrDoubleConstant ud = (UntypedLongOrDoubleConstant)cst;
		                newValue = ud.toLongConstant();
		            } else {
		                if (cst instanceof UntypedIntOrFloatConstant && ((UntypedIntOrFloatConstant)cst).value == 0) {
		                    newValue = NullConstant.v();
							Debug.printDbg("new null constant for constraint ", c, " with l type: ", localTyped.get(l));
		                } else {
							throw new RuntimeException("unknow type for constance: " + lt);
		                }
		            }
		            c.r.setValue(newValue);

					Debug.printDbg(IDalvikTyper.DEBUG, "remove constraint: ", c);
		            constraints.remove(c);
		            update = true;
		            break;
		        } else if (l instanceof Local && r instanceof Local) {
		            Local leftLocal = (Local)l;
		            Local rightLocal = (Local)r;
		            if (localTyped.containsKey(leftLocal)) {
		                Type leftLocalType = localTyped.get(leftLocal);
		                if (!localTyped.containsKey(rightLocal)) {
							Debug.printDbg(IDalvikTyper.DEBUG, "set type ", leftLocalType, " to local ", rightLocal);
		                    rightLocal.setType(leftLocalType);
		                    setLocalTyped(rightLocal, leftLocalType);
		                }
						Debug.printDbg(IDalvikTyper.DEBUG, "remove constraint: ", c);
		                constraints.remove(c);
		                update = true;
		                break;
		            } else if (localTyped.containsKey(rightLocal)) {
		                Type rightLocalType = localTyped.get(rightLocal);
		                if (!localTyped.containsKey(leftLocal)) {
							Debug.printDbg(IDalvikTyper.DEBUG, "set type ", rightLocalType, " to local ", leftLocal);
		                    leftLocal.setType(rightLocalType);
		                    setLocalTyped(leftLocal, rightLocalType);
		                }
						Debug.printDbg(IDalvikTyper.DEBUG, "remove constraint: ", c);
		                constraints.remove(c);
	                    update = true;
	                    break;
		            }
		        } else if (l instanceof ArrayRef && r instanceof Local) {
		            Local rightLocal = (Local)r;
		            ArrayRef ar = (ArrayRef)l;
		            Local base = (Local)ar.getBase();
					Debug.printDbg(IDalvikTyper.DEBUG, "base: ", base);
					Debug.printDbg(IDalvikTyper.DEBUG, "index: ", ar.getIndex());
		            if (localTyped.containsKey(base)) {
		                Type t = localTyped.get(base);

						Debug.printDbg(IDalvikTyper.DEBUG, "type of local1: ", t, " ", t.getClass());
		                Type elementType = null;
		                if (t instanceof ArrayType) {
		                    ArrayType at = (ArrayType)t;
		                    elementType = at.getArrayElementType();
		                } else {
		                    continue;
		                }
		                 
		                if (!localTyped.containsKey(rightLocal)) {
							Debug.printDbg(IDalvikTyper.DEBUG, "set type ", elementType, " to local ", r);
		                    rightLocal.setType(elementType);
		                    setLocalTyped(rightLocal, elementType);
		                }
						Debug.printDbg(IDalvikTyper.DEBUG, "remove constraint: ", c);
	                    constraints.remove(c);
	                    update = true;
	                    break;
		            }
		        } else if (l instanceof Local && r instanceof ArrayRef) {
		            Local leftLocal = (Local)l;
                    ArrayRef ar = (ArrayRef)r;
                    Local base = (Local)ar.getBase();
                    if (localTyped.containsKey(base)) {
                        Type t = localTyped.get(base);

						Debug.printDbg(IDalvikTyper.DEBUG, "type of local2: ", t, " ", t.getClass());
                        Type elementType = null;
                        if (t instanceof ArrayType) {
                            ArrayType at = (ArrayType)t;
                            elementType = at.getArrayElementType();
                        } else {
                            continue;
                        }
                        
                        if (!localTyped.containsKey(leftLocal)) {
							Debug.printDbg(IDalvikTyper.DEBUG, "set type ", elementType, " to local ", l);
                            leftLocal.setType(elementType);
                            setLocalTyped(leftLocal, elementType);
                        }
						Debug.printDbg(IDalvikTyper.DEBUG, "remove constraint: ", c);
                        constraints.remove(c);
                        update = true;
                        break;
                    }
                }  else {
					throw new RuntimeException("error: do not handling this kind of constraint: " + c);
		        }
		    }
		    if (!update)
		        break;
		}
		
		for (Unit u : b.getUnits()) {
			if (!(u instanceof AssignStmt))
				continue;
			AssignStmt ass = (AssignStmt) u;
			if (!(ass.getLeftOp() instanceof Local))
				continue;
			if (!(ass.getRightOp() instanceof UntypedConstant))
				continue;
			UntypedConstant uc = (UntypedConstant) ass.getRightOp();
			ass.setRightOp(uc.defineType(localTyped.get(ass.getLeftOp())));

		}

		// At this point some constants may be untyped.
		// (for instance if it is only use in a if condition).
		// We assume type in integer.
		//
		for (Constraint c: constraints) {
			Debug.printDbg(IDalvikTyper.DEBUG, "current constraint: ", c);
            Value l = c.l.getValue();
            Value r = c.r.getValue();
            if (l instanceof Local && r instanceof Constant) {
                if (r instanceof UntypedIntOrFloatConstant) {
                    UntypedIntOrFloatConstant cst = (UntypedIntOrFloatConstant)r;
                    Value newValue = null;
                    if (cst.value != 0) {
						Debug.printDbg(IDalvikTyper.DEBUG, "[untyped constaints] set type int to non zero constant: ", c, " = ", cst.value);
                        newValue = cst.toIntConstant();
                    } else { // check if used in cast, just in case...
                        for (Unit u: b.getUnits()) {
                            for (ValueBox vb1: u.getUseBoxes()) {
                                Value v1 = vb1.getValue();
                                if (v1 == l) {
									Debug.printDbg("local used in ", u);
                                    if (u instanceof AssignStmt) {
                                        AssignStmt a = (AssignStmt)u;
                                        Value right = a.getRightOp();
                                        if (right instanceof CastExpr) {
                                            newValue = NullConstant.v();
                                        } else {
                                            newValue = cst.toIntConstant();
                                        }
                                    } else if (u instanceof IfStmt) {
                                        newValue = cst.toIntConstant();//TODO check this better
                                    }
                                }
                            }
                        }
                    }
					if (newValue == null) {
						throw new RuntimeException("error: no type found for local: " + l);
					}
                    c.r.setValue(newValue);
                } else if (r instanceof UntypedLongOrDoubleConstant) {
					Debug.printDbg(IDalvikTyper.DEBUG, "[untyped constaints] set type long to constant: ", c);
                    Value newValue = ((UntypedLongOrDoubleConstant)r).toLongConstant();
                    c.r.setValue(newValue);
                }
            }
		}
		
		// fix untypedconstants which have flown to an array index
		for (Unit u: b.getUnits()) {
			StmtSwitch sw = new StmtSwitch() {

				@Override
				public void caseBreakpointStmt(BreakpointStmt stmt) {
					// TODO Auto-generated method stub

				}

				@Override
				public void caseInvokeStmt(InvokeStmt stmt) {
					changeUntypedConstantsInInvoke(stmt.getInvokeExpr());

				}

				@Override
				public void caseAssignStmt(AssignStmt stmt) {


					if (stmt.getRightOp() instanceof NewArrayExpr) {
						NewArrayExpr nae = (NewArrayExpr) stmt.getRightOp();
						if (nae.getSize() instanceof UntypedConstant) {
							UntypedIntOrFloatConstant uc = (UntypedIntOrFloatConstant) nae.getSize();
							nae.setSize(uc.defineType(IntType.v()));
						}
					} else if (stmt.getRightOp() instanceof UntypedConstant) {
						UntypedConstant uc = (UntypedConstant) stmt.getRightOp();
						Value l = stmt.getLeftOp();
						Type lType = null;
						if (l instanceof ArrayRef) {
							ArrayRef ar = (ArrayRef) l;
							Local baseLocal = (Local) ar.getBase();
							ArrayType arrayType = (ArrayType) localTyped.get(baseLocal);
							lType = arrayType.getElementType();
						} else {
							lType = l.getType();
						}
						stmt.setRightOp(uc.defineType(lType));
					} else if (stmt.getRightOp() instanceof InvokeExpr) {
						changeUntypedConstantsInInvoke((InvokeExpr) stmt.getRightOp());
					}

					if (!stmt.containsArrayRef()) {
						return;
					}
					ArrayRef ar = stmt.getArrayRef();
					if ((ar.getIndex() instanceof UntypedConstant)) {
						UntypedIntOrFloatConstant uc = (UntypedIntOrFloatConstant) ar.getIndex();
						ar.setIndex(uc.toIntConstant());
					}

					if (stmt.getLeftOp() instanceof ArrayRef && stmt.getRightOp() instanceof UntypedConstant) {
						UntypedConstant uc = (UntypedConstant) stmt.getRightOp();
						Local baseLocal = (Local) stmt.getArrayRef().getBase();
						ArrayType lType = (ArrayType) localTyped.get(baseLocal);
						Type elemType = lType.getElementType();
						stmt.setRightOp(uc.defineType(elemType));
					}

				}

				@Override
				public void caseIdentityStmt(IdentityStmt stmt) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void caseGotoStmt(GotoStmt stmt) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void caseIfStmt(IfStmt stmt) {
					Value c = stmt.getCondition();
					if (c instanceof BinopExpr) {
						BinopExpr be = (BinopExpr) c;
						Value op1 = be.getOp1();
						Value op2 = be.getOp2();
						if (op1 instanceof UntypedConstant || op2 instanceof UntypedConstant) {
							Debug.printDbg("if to handle: ", stmt);

							if (op1 instanceof Local) {
								Type t = localTyped.get(op1);
								Debug.printDbg("if op1 type: ", t);
								UntypedConstant uc = (UntypedConstant) op2;
								be.setOp2(uc.defineType(t));
							} else if (op2 instanceof Local) {
								Type t = localTyped.get(op2);
								Debug.printDbg("if op2 type: ", t);
								UntypedConstant uc = (UntypedConstant) op1;
								be.setOp1(uc.defineType(t));
							} else if (op1 instanceof UntypedConstant && op2 instanceof UntypedConstant) {
								if (op1 instanceof UntypedIntOrFloatConstant && op2 instanceof UntypedIntOrFloatConstant) {
									UntypedIntOrFloatConstant uc1 = (UntypedIntOrFloatConstant) op1;
									UntypedIntOrFloatConstant uc2 = (UntypedIntOrFloatConstant) op2;
									be.setOp1(uc1.toIntConstant()); // to int or float, it does not matter
									be.setOp2(uc2.toIntConstant());
								} else if (op1 instanceof UntypedLongOrDoubleConstant && op2 instanceof UntypedLongOrDoubleConstant) {
									UntypedLongOrDoubleConstant uc1 = (UntypedLongOrDoubleConstant) op1;
									UntypedLongOrDoubleConstant uc2 = (UntypedLongOrDoubleConstant) op2;
									be.setOp1(uc1.toLongConstant()); // to long or double, it does not matter
									be.setOp2(uc2.toLongConstant());
								} else {
									throw new RuntimeException("error: expected same type of untyped constants. Got " + stmt);
								}
							} else if (op1 instanceof UntypedConstant || op2 instanceof UntypedConstant) {
								if (op1 instanceof UntypedConstant) {
									UntypedConstant uc = (UntypedConstant) op1;
									be.setOp1(uc.defineType(op2.getType()));
								} else if (op2 instanceof UntypedConstant) {
									UntypedConstant uc = (UntypedConstant) op2;
									be.setOp2(uc.defineType(op1.getType()));
								}
							} else {
								throw new RuntimeException("error: expected local/untyped untyped/local or untyped/untyped. Got " + stmt);
							}
						}
					} else if (c instanceof UnopExpr) {

					} else {
						throw new RuntimeException("error: expected binop or unop. Got " + stmt);
					}
					
				}

				@Override
				public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void caseNopStmt(NopStmt stmt) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void caseRetStmt(RetStmt stmt) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void caseReturnStmt(ReturnStmt stmt) {
					if (stmt.getOp() instanceof UntypedConstant) {
						UntypedConstant uc = (UntypedConstant) stmt.getOp();
						Type type = b.getMethod().getReturnType();
						stmt.setOp(uc.defineType(type));
					}
					
				}

				@Override
				public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void caseTableSwitchStmt(TableSwitchStmt stmt) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void caseThrowStmt(ThrowStmt stmt) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void defaultCase(Object obj) {
					// TODO Auto-generated method stub
					
				}
				



			};
			u.apply(sw);

		}
		
		// fix untyped constants remaining 

		Debug.printDbg("assignTypes: after: \n", b);

	}

	
	private void changeUntypedConstantsInInvoke(InvokeExpr invokeExpr) {
		for (int i = 0; i < invokeExpr.getArgCount(); i++) {
			Value v = invokeExpr.getArg(i);
			if (!(v instanceof UntypedConstant))
				continue;
			Type t = invokeExpr.getMethodRef().parameterType(i);
			UntypedConstant uc = (UntypedConstant) v;
			invokeExpr.setArg(i, uc.defineType(t));
		}

	}
	
	
	
	protected void checkExpr(Value v, Type t) {

		for (ValueBox vb : v.getUseBoxes()) {
			Value value = vb.getValue();
			if (value instanceof Local) {
				// special case where the second operand is always of type integer
				if ((v instanceof ShrExpr || v instanceof ShlExpr || v instanceof UshrExpr) && ((BinopExpr) v).getOp2() == value) {
					Debug.printDbg("setting type of operand two of shift expression to integer", value);
					DalvikTyper.v().setType(vb, IntType.v(), true);
					continue;
				}
				DalvikTyper.v().setType(vb, t, true);
			} else if (value instanceof UntypedConstant) {

				UntypedConstant uc = (UntypedConstant) value;

				// special case where the second operand is always of type integer
				if ((v instanceof ShrExpr || v instanceof ShlExpr || v instanceof UshrExpr) && ((BinopExpr) v).getOp2() == value) {
					UntypedIntOrFloatConstant ui = (UntypedIntOrFloatConstant) uc;
					vb.setValue(ui.toIntConstant());
					continue;
				}
				vb.setValue(uc.defineType(t));
			}

		}

	}

	protected void setInvokeType(InvokeExpr invokeExpr) {
		for (int i = 0; i < invokeExpr.getArgCount(); i++) {
			Value v = invokeExpr.getArg(i);
			if (!(v instanceof Local))
				continue;
			Type t = invokeExpr.getMethodRef().parameterType(i);
			DalvikTyper.v().setType(invokeExpr.getArgBox(i), t, true);
		}
		if (invokeExpr instanceof StaticInvokeExpr) {
			// nothing to do
		} else if (invokeExpr instanceof InstanceInvokeExpr) {
			InstanceInvokeExpr iie = (InstanceInvokeExpr) invokeExpr;
			DalvikTyper.v().setType(iie.getBaseBox(), RefType.v("java.lang.Object"), true);
		} else if (invokeExpr instanceof DynamicInvokeExpr) {
			DynamicInvokeExpr die = (DynamicInvokeExpr) invokeExpr;
			// ?
		} else {
			throw new RuntimeException("error: unhandled invoke expression: " + invokeExpr + " " + invokeExpr.getClass());
		}

	}

	private void setLocalTyped(Local l, Type t) {
        localTyped.put(l, t);
    }


	class LocalObj {
	    ValueBox vb;
	    Type t;
	    //private Local l;
	    boolean isUse;
	    public LocalObj(ValueBox vb, Type t, boolean isUse) {
	        this.vb = vb;
	        //this.l = (Local)vb.getValue();
	        this.t = t;
	        this.isUse = isUse;
	    }
	    
	    public Local getLocal(){
	        return (Local)vb.getValue();
	    }
	    
	}



    class Constraint {
	    ValueBox l;
	    ValueBox r;
	    public Constraint(ValueBox l, ValueBox r) {
	        this.l = l;
	        this.r = r;
	    }
	    
	    public String toString() {
	        return l +" < "+ r;
	    }
	}

	// this is needed because UnuesedStatementTransformer checks types in the div expressions
	public void typeUntypedConstrantInDiv(final Body b) {
		for (Unit u : b.getUnits()) {
			StmtSwitch sw = new StmtSwitch() {

				@Override
				public void caseBreakpointStmt(BreakpointStmt stmt) {
					// TODO Auto-generated method stub

				}

				@Override
				public void caseInvokeStmt(InvokeStmt stmt) {
					changeUntypedConstantsInInvoke(stmt.getInvokeExpr());
				}

				@Override
				public void caseAssignStmt(AssignStmt stmt) {

					if (stmt.getRightOp() instanceof NewArrayExpr) {
						NewArrayExpr nae = (NewArrayExpr) stmt.getRightOp();
						if (nae.getSize() instanceof UntypedConstant) {
							UntypedIntOrFloatConstant uc = (UntypedIntOrFloatConstant) nae.getSize();
							nae.setSize(uc.defineType(IntType.v()));
						}
					} else if (stmt.getRightOp() instanceof InvokeExpr) {
						changeUntypedConstantsInInvoke((InvokeExpr) stmt.getRightOp());
					} else if (stmt.getRightOp() instanceof CastExpr) {
						CastExpr ce = (CastExpr) stmt.getRightOp();
						if (ce.getOp() instanceof UntypedConstant) {
							UntypedConstant uc = (UntypedConstant) ce.getOp();
							// check incoming primitive type
							for (Tag t : stmt.getTags()) {
								Debug.printDbg("assign primitive type from stmt tag: ", stmt, t);
								if (t instanceof IntOpTag) {
									ce.setOp(uc.defineType(IntType.v()));
									return;
								} else if (t instanceof FloatOpTag) {
									ce.setOp(uc.defineType(FloatType.v()));
									return;
								} else if (t instanceof DoubleOpTag) {
									ce.setOp(uc.defineType(DoubleType.v()));
									return;
								} else if (t instanceof LongOpTag) {
									ce.setOp(uc.defineType(LongType.v()));
									return;
								}
							}

							// 0 -> null
							ce.setOp(uc.defineType(RefType.v("java.lang.Object")));
						}
					}

					if (stmt.containsArrayRef()) {
						ArrayRef ar = stmt.getArrayRef();
						if ((ar.getIndex() instanceof UntypedConstant)) {
							UntypedIntOrFloatConstant uc = (UntypedIntOrFloatConstant) ar.getIndex();
							ar.setIndex(uc.toIntConstant());
						}
					}

					Value r = stmt.getRightOp();
					if (r instanceof DivExpr || r instanceof RemExpr) {
						//DivExpr de = (DivExpr) r;
						for (Tag t : stmt.getTags()) {
							Debug.printDbg("div stmt tag: ", stmt, t);
							if (t instanceof IntOpTag) {
								checkExpr(r, IntType.v());
								return;
							} else if (t instanceof FloatOpTag) {
								checkExpr(r, FloatType.v());
								return;
							} else if (t instanceof DoubleOpTag) {
								checkExpr(r, DoubleType.v());
								return;
							} else if (t instanceof LongOpTag) {
								checkExpr(r, LongType.v());
								return;
							}
						}

					}
				}

				@Override
				public void caseIdentityStmt(IdentityStmt stmt) {
					// TODO Auto-generated method stub

				}

				@Override
				public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
					// TODO Auto-generated method stub

				}

				@Override
				public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
					// TODO Auto-generated method stub

				}

				@Override
				public void caseGotoStmt(GotoStmt stmt) {
					// TODO Auto-generated method stub

				}

				@Override
				public void caseIfStmt(IfStmt stmt) {
					// TODO Auto-generated method stub

				}

				@Override
				public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
					// TODO Auto-generated method stub

				}

				@Override
				public void caseNopStmt(NopStmt stmt) {
					// TODO Auto-generated method stub

				}

				@Override
				public void caseRetStmt(RetStmt stmt) {
					// TODO Auto-generated method stub

				}

				@Override
				public void caseReturnStmt(ReturnStmt stmt) {
					if (stmt.getOp() instanceof UntypedConstant) {
						UntypedConstant uc = (UntypedConstant) stmt.getOp();
						Type type = b.getMethod().getReturnType();
						stmt.setOp(uc.defineType(type));
					}
				}

				@Override
				public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
					// TODO Auto-generated method stub

				}

				@Override
				public void caseTableSwitchStmt(TableSwitchStmt stmt) {
					// TODO Auto-generated method stub

				}

				@Override
				public void caseThrowStmt(ThrowStmt stmt) {
					if (stmt.getOp() instanceof UntypedConstant) {
						UntypedConstant uc = (UntypedConstant) stmt.getOp();
						stmt.setOp(uc.defineType(RefType.v("java.lang.Object")));
					}
				}

				@Override
				public void defaultCase(Object obj) {
					// TODO Auto-generated method stub

				}

			};
			u.apply(sw);
		}

	}

}
