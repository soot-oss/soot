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
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.ArrayType;
import soot.Body;
import soot.Local;
import soot.RefLikeType;
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
import soot.jimple.EnterMonitorStmt;
import soot.jimple.EqExpr;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.FieldRef;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LengthExpr;
import soot.jimple.NeExpr;
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
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.SmartLocalDefs;
import soot.toolkits.scalar.UnitValueBoxPair;

/**
 * BodyTransformer to find and change IntConstant(0) to NullConstant where
 * locals are used as objects.
 *
 * @author Michael Markert
 */
public class DexNullTransformer extends DexTransformer {
	// Note: we need an instance variable for inner class access, treat this as
	// a local variable (including initialization before use)

	private boolean usedAsObject;
	private boolean doBreak = false;

    public static DexNullTransformer v() {
        return new DexNullTransformer();
    }

   Local l = null;

	@SuppressWarnings("unchecked")
	protected void internalTransform(final Body body, String phaseName, @SuppressWarnings("rawtypes") Map options) {
        final ExceptionalUnitGraph g = new ExceptionalUnitGraph(body);
        final SmartLocalDefs localDefs = new SmartLocalDefs(g, new SimpleLiveLocals(g));
        final SimpleLocalUses localUses = new SimpleLocalUses(g, localDefs);

        for (Local loc: getNullCandidates(body)) {
            Debug.printDbg("\n[null candidate] ", loc);
            usedAsObject = false;
            List<Unit> defs = collectDefinitionsWithAliases(loc, localDefs, localUses, body);
            // check if no use
            for (Unit u  : defs) {
              for (UnitValueBoxPair pair : (List<UnitValueBoxPair>) localUses.getUsesOf(u)) {
                Debug.printDbg("[use in u]: ", pair.getUnit());
              }
            }
            // process normally
            doBreak = false;
            for (Unit u  : defs) {

              // put correct local in l
              if (u instanceof AssignStmt) {
                l = (Local)((AssignStmt)u).getLeftOp();
              } else if (u instanceof IdentityStmt) {
                l = (Local)((IdentityStmt)u).getLeftOp();
              } else if (u instanceof IfStmt) {
                throw new RuntimeException ("ERROR: def can not be something else than Assign or Identity statement! (def: "+ u +" class: "+ u.getClass() +"");
//                IfStmt ifstmt = (IfStmt)u;
//                Value v = ifstmt.getCondition();
//                if (!(v instanceof CmpExpr)) {
//                  Debug.printDbg("ERROR: the if statement must contain a cmp expr ", u);
//                  System.exit(-1);
//                }
//                CmpExpr cmp = (CmpExpr)v;
//                Value op1 = cmp.getOp1();
//                Value op2 = cmp.getOp2();
//                if (op1 instanceof Local && !(op2 instanceof Local)) {
//                  l = (Local)op1;
//                } else if (op2 instanceof Local && !(op1 instanceof Local)) {
//                  l = (Local)op2;
//                } else {
//                  Debug.printDbg("ERROR: the if statement must contain only one local (and the other should be 0) ", u);
//                  System.exit(-1);
//                }
              }

              Debug.printDbg("    target local: ", l ," (Unit: ",u ," )");

              // check defs
              u.apply(new AbstractStmtSwitch() { // Alex: should also end as soon as detected as not used as an object
                public void caseAssignStmt (AssignStmt stmt) {
                  Value r = stmt.getRightOp();
                      if (r instanceof FieldRef) {
                          usedAsObject = isObject(((FieldRef) r).getFieldRef().type());
                          doBreak = true;
                          return;
                      } else if (r instanceof ArrayRef) {
                          ArrayRef ar = (ArrayRef)r;
                          if (ar.getType() instanceof UnknownType) {
                            usedAsObject = stmt.hasTag("ObjectOpTag"); //isObject (findArrayType (g, localDefs, localUses, stmt));
                          } else {
                            usedAsObject = isObject(ar.getType());
                          }
                          doBreak = true;
                          return;
                      } else if (r instanceof StringConstant || r instanceof NewExpr || r instanceof NewArrayExpr) {
                          usedAsObject = true;
                          doBreak = true;
                          return;
                      } else if (r instanceof CastExpr) {
                          usedAsObject = isObject (((CastExpr)r).getCastType());
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
                      } else if (r instanceof Local) {}

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
                for (UnitValueBoxPair pair : (List<UnitValueBoxPair>) localUses.getUsesOf(u)) {
                    Unit use = pair.getUnit();
                    Debug.printDbg("    use: ", use);
                    use.apply( new AbstractStmtSwitch() {
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
                                    AbstractInstanceInvokeExpr aiiexpr = (AbstractInstanceInvokeExpr)e;
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
                                  if (((ArrayRef)left).getIndex() == l) {
                                    doBreak = true;
                                    return;
                                  }
                                }


// IMPOSSIBLE! WOULD BE DEF!
//                            // gets value assigned
//                                if (stmt.getLeftOp() == l) {
//                                    if (r instanceof FieldRef)
//                                        usedAsObject = isObject(((FieldRef) r).getFieldRef().type());
//                                    else if (r instanceof ArrayRef)
//                                        usedAsObject = isObject(((ArrayRef) r).getType());
//                                    else if (r instanceof StringConstant || r instanceof NewExpr || r instanceof NewArrayExpr)
//                                        usedAsObject = true;
//                                    else if (r instanceof CastExpr)
//                                        usedAsObject = isObject (((CastExpr)r).getCastType());
//                                    else if (r instanceof InvokeExpr)
//                                        usedAsObject = isObject(((InvokeExpr) r).getType());
//                                    // introduces alias
//                                    else if (r instanceof Local) {}
//
//                                }
                                // used to assign
                                if (stmt.getRightOp() == l) {
                                    Value l = stmt.getLeftOp();
                                    if (l instanceof StaticFieldRef && isObject(((StaticFieldRef) l).getFieldRef().type())) {
                                        usedAsObject = true;
                                        doBreak = true;
                                        return;
                                    } else if (l instanceof InstanceFieldRef && isObject(((InstanceFieldRef) l).getFieldRef().type())) {
                                        usedAsObject = true;
                                        doBreak = true;
                                        return;
                                    } else if (l instanceof ArrayRef) {
                                      Type aType = ((ArrayRef) l).getType();
                                      if (aType instanceof UnknownType) {
                                        usedAsObject = stmt.hasTag("ObjectOpTag"); //isObject( findArrayType(g, localDefs, localUses, stmt));
                                      } else {
                                        usedAsObject = isObject(aType);
                                      }
                                        doBreak = true;
                                        return;
                                    }
                                }

                                // is used as value (does not exclude assignment)
                              if (r instanceof FieldRef) {
                                usedAsObject = true; //isObject(((FieldRef) r).getFieldRef().type());
                                doBreak = true;
                                return;
                              } else if (r instanceof ArrayRef) {
                                ArrayRef ar = (ArrayRef)r;
                                if (ar.getBase() == l) {
                                  usedAsObject = true;
                                } else { // used as index
                                  usedAsObject = false;
                                }
                                doBreak = true;
                                return;
                              } else if (r instanceof StringConstant || r instanceof NewExpr) {
                                Debug.printDbg("NOT POSSIBLE StringConstant or NewExpr! ", stmt);
                                System.exit(-1);
                                usedAsObject = true;
                                doBreak = true;
                                return;
                              } else if (r instanceof NewArrayExpr) {
                                usedAsObject = false;
                                doBreak = true;
                                return;
                              } else if (r instanceof CastExpr) {
                                usedAsObject = isObject (((CastExpr)r).getCastType());
                                doBreak = true;
                                return;
                              } else if (r instanceof InvokeExpr) {
                                usedAsObject = examineInvokeExpr((InvokeExpr) stmt.getRightOp());
                                Debug.printDbg("use as object 2 = ", usedAsObject);
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
                                if (stmt.getLeftOp() == l) {
                                  Debug.printDbg("IMPOSSIBLE 0");
                                  System.exit(-1);
                                    usedAsObject = isObject(stmt.getRightOp().getType());
                                }
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
                                usedAsObject = stmt.getOp() == l && isObject(body.getMethod().getReturnType());
                                Debug.printDbg (" [return stmt] ", stmt ," usedAsObject: ", usedAsObject ,", return type: ", body.getMethod().getReturnType());
                                Debug.printDbg (" class: ", body.getMethod().getReturnType().getClass());
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
                  for (UnitValueBoxPair pair : (List<UnitValueBoxPair>) localUses.getUsesOf(u)) {
                      Unit use = pair.getUnit();
                      replaceWithNull(use);
                  }
              }
            } // end if
        }

        // Check for inlined zero values
        for (Unit u : body.getUnits()) {
	        u.apply(new AbstractStmtSwitch() {
	        	@Override
	            public void caseAssignStmt (AssignStmt stmt) {
	            	if (isObject(stmt.getLeftOp().getType()) && stmt.getRightOp() instanceof IntConstant) {
	            		IntConstant iconst = (IntConstant) stmt.getRightOp();
	            		assert iconst.value == 0;
	            		stmt.setRightOp(NullConstant.v());
	            		return;
	            	}
	            	if (stmt.getRightOp() instanceof CastExpr) {
	            		CastExpr ce = (CastExpr) stmt.getRightOp();
	            		if (isObject(ce.getCastType()) && ce.getOp() instanceof IntConstant) {
	            			IntConstant iconst = (IntConstant) ce.getOp();
	            			assert iconst.value == 0;
	            			stmt.setRightOp(NullConstant.v());
	            		}
	            	}
	            	if (stmt.getLeftOp() instanceof ArrayRef && stmt.getRightOp() instanceof IntConstant) {
	            		if (isObjectArray(((ArrayRef) stmt.getLeftOp()).getBase(), body)) {
	            			IntConstant iconst = (IntConstant) stmt.getRightOp();
	            			assert iconst.value == 0;
	            			stmt.setRightOp(NullConstant.v());
	            		}
	            	}
	            }
	        	@Override
	            public void caseReturnStmt (ReturnStmt stmt) {
	        		if (stmt.getOp() instanceof IntConstant && isObject(body.getMethod().getReturnType())) {
	        			IntConstant iconst = (IntConstant) stmt.getOp();
	        			assert iconst.value == 0;
	        			stmt.setOp(NullConstant.v());
	        		}
	        	}
	        });
	        if (u instanceof Stmt) {
	        	Stmt stmt = (Stmt) u;
	        	if (stmt.containsInvokeExpr()) {
	        		InvokeExpr invExpr = stmt.getInvokeExpr();
	        		for (int i = 0; i < invExpr.getArgCount(); i++)
	        			if (isObject(invExpr.getMethodRef().parameterType(i)))
	        				if (invExpr.getArg(i) instanceof IntConstant) {
	        					IntConstant iconst = (IntConstant) invExpr.getArg(i);
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
					if (assign.getRightOp() instanceof FieldRef) {
						FieldRef fr = (FieldRef) assign.getRightOp();
						if (fr.getType() instanceof ArrayType)
							if (isObject(((ArrayType) fr.getType()).getArrayElementType()))
								return true;
					}
				}
			}
		}
		return false;
	}

  private boolean isObject(Type t) {
    return t instanceof RefLikeType;
  }

    /**
     * Collect all the locals which are assigned a IntConstant(0) or are used
     * within a zero comparison.
     *
     * @param body the body to analyze
     */
    private Set<Local> getNullCandidates(Body body) {
        Set<Local> candidates = new HashSet<Local>();
        for (Unit u : body.getUnits()) {
            if (u instanceof AssignStmt) {
                AssignStmt a = (AssignStmt) u;
                if (! (a.getLeftOp() instanceof Local))
                    continue;
                Local l = (Local) a.getLeftOp();
                Value r = a.getRightOp();
                if ((r instanceof IntConstant && ((IntConstant) r).value == 0)) {
                    candidates.add(l);
                    Debug.printDbg("[add null candidate: ", u);
                }
            }
            else if (u instanceof IfStmt) {
                ConditionExpr expr = (ConditionExpr) ((IfStmt) u).getCondition();
                if (isZeroComparison(expr) && expr.getOp1() instanceof Local) {
                    candidates.add((Local) expr.getOp1());
                    Debug.printDbg("[add null candidate if: ", u);
                }

            }
        }

        return candidates;
    }

    /**
     * Replace 0 with null in the given unit.
     *
     * @param u the unit where 0 will be replaced with null.
     */
    private void replaceWithNull(Unit u) {

        if (u instanceof IfStmt) {
            ConditionExpr expr = (ConditionExpr) ((IfStmt) u).getCondition();
            if (isZeroComparison(expr)) {
                expr.setOp2(NullConstant.v());
                Debug.printDbg("[null] replacing with null in ", u);
                Debug.printDbg(" new u: ", u);
            }
        } else if (u instanceof AssignStmt) {
        	AssignStmt s = (AssignStmt) u;
            Value v = s.getRightOp();
            if ((v instanceof IntConstant) && ((IntConstant) v).value == 0) {
                s.setRightOp(NullConstant.v());
                Debug.printDbg("[null] replacing with null in ", u);
                Debug.printDbg(" new u: ", u);
            }
        }

    }

    /**
     * Examine expr if it is a comparison with 0.
     *
     * @param expr the ConditionExpr to examine
     */
    private boolean isZeroComparison(ConditionExpr expr) {
        return (expr.getOp2() instanceof IntConstant)
            && ((IntConstant) expr.getOp2()).value == 0
            && ((expr instanceof EqExpr) || (expr instanceof NeExpr));
    }


}

