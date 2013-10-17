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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * BodyTransformer to find and change definition of locals used
 * within an if which contains a condition involving two locals (
 * and not only one local as in DexNullTransformer).
 *
 * It this case, if any of the two locals leads to an object being
 * def or used, all the appropriate defs of the two locals are updated
 * to reflect the use of objects (i.e: 0s are replaced by nulls).
 */
public class DexIfTransformer extends DexTransformer {
	// Note: we need an instance variable for inner class access, treat this as
	// a local variable (including initialization before use)

	private boolean usedAsObject;
	private boolean doBreak = false;

    public static DexIfTransformer v() {
        return new DexIfTransformer();
    }

   Local l = null;

	@SuppressWarnings("unchecked")
	protected void internalTransform(final Body body, String phaseName, @SuppressWarnings("rawtypes") Map options) {
        final ExceptionalUnitGraph g = new ExceptionalUnitGraph(body);
        final SmartLocalDefs localDefs = new SmartLocalDefs(g, new SimpleLiveLocals(g));
        final SimpleLocalUses localUses = new SimpleLocalUses(g, localDefs);

        Set<IfStmt> ifSet = getNullIfCandidates(body);        
        for (IfStmt ifs: ifSet) {
          List<Local> twoIfLocals = new ArrayList<Local>();
          ConditionExpr ifCondition = (ConditionExpr)ifs.getCondition();
          Local lOp1 = (Local)ifCondition.getOp1();
          Local lOp2 = (Local)ifCondition.getOp2();
          twoIfLocals.add(lOp1);
          twoIfLocals.add(lOp2);
          usedAsObject = false;
          for (Local loc: twoIfLocals) {
            Debug.printDbg("\n[null if with two local candidate] ", loc);
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
              }

              Debug.printDbg("    target local: ", l ," (Unit: ",u ," )");

              // check defs
              u.apply(new AbstractStmtSwitch() { // Alex: should also end as soon as detected as not used as an object
                public void caseAssignStmt (AssignStmt stmt) {
                  Value r = stmt.getRightOp();
                      if (r instanceof FieldRef) {
                          usedAsObject = isObject(((FieldRef) r).getFieldRef().type());
                          if (usedAsObject)
                            doBreak = true;
                          return;
                      } else if (r instanceof ArrayRef) {
                          ArrayRef ar = (ArrayRef)r;
                          if (ar.getType() instanceof UnknownType) {
                            usedAsObject = stmt.hasTag("ObjectOpTag"); //isObject (findArrayType (g, localDefs, localUses, stmt));
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
                          usedAsObject = isObject (((CastExpr)r).getCastType());
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
                      } else if (r instanceof Local) {}

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
                                if (usedAsObject)
                                  doBreak = true;
                                return;
                            }
                            public void caseAssignStmt(AssignStmt stmt) {
                              Value left = stmt.getLeftOp();
                                Value r = stmt.getRightOp();

                                if (left instanceof ArrayRef) {
                                  if (((ArrayRef)left).getIndex() == l) {
                                    //doBreak = true;
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
                                        if (usedAsObject)
                                          doBreak = true;
                                        return;
                                    } else if (l instanceof InstanceFieldRef && isObject(((InstanceFieldRef) l).getFieldRef().type())) {
                                        usedAsObject = true;
                                        if (usedAsObject)
                                          doBreak = true;
                                        return;
                                    } else if (l instanceof ArrayRef) {
                                      Type aType = ((ArrayRef) l).getType();
                                      if (aType instanceof UnknownType) {
                                        usedAsObject = stmt.hasTag("ObjectOpTag"); //isObject( findArrayType(g, localDefs, localUses, stmt));
                                      } else {
                                        usedAsObject = isObject(aType);
                                      }
                                        if (usedAsObject)
                                          doBreak = true;
                                        return;
                                    }
                                }

                                // is used as value (does not exclude assignment)
                              if (r instanceof FieldRef) {
                                usedAsObject = true; //isObject(((FieldRef) r).getFieldRef().type());
                                if (usedAsObject)
                                  doBreak = true;
                                return;
                              } else if (r instanceof ArrayRef) {
                                ArrayRef ar = (ArrayRef)r;
                                if (ar.getBase() == l) {
                                  usedAsObject = true;
                                } else { // used as index
                                  usedAsObject = false;
                                }
                                if (usedAsObject)
                                  doBreak = true;
                                return;
                              } else if (r instanceof StringConstant || r instanceof NewExpr) {
                                Debug.printDbg("NOT POSSIBLE StringConstant or NewExpr! ", stmt);
                                System.exit(-1);
                                usedAsObject = true;
                                if (usedAsObject)
                                  doBreak = true;
                                return;
                              } else if (r instanceof NewArrayExpr) {
                                usedAsObject = false;
                                if (usedAsObject)
                                  doBreak = true;
                                return;
                              } else if (r instanceof CastExpr) {
                                usedAsObject = isObject (((CastExpr)r).getCastType());
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
                                if (stmt.getLeftOp() == l) {
                                  Debug.printDbg("IMPOSSIBLE 0");
                                  System.exit(-1);
                                    usedAsObject = isObject(stmt.getRightOp().getType());
                                }
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
                                Debug.printDbg (" [return stmt] ", stmt ," usedAsObject: ", usedAsObject ,", return type: ", body.getMethod().getReturnType());
                                Debug.printDbg (" class: ", body.getMethod().getReturnType().getClass());
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

            if (doBreak) // as soon as one def or use refers to an object all defs from the two locals in the if must be updated
              break;

         } // for two locals in if

          // change values
          if (usedAsObject) {
            List<Unit> defsOp1 = collectDefinitionsWithAliases(lOp1, localDefs, localUses, body);
            List<Unit> defsOp2 = collectDefinitionsWithAliases(lOp1, localDefs, localUses, body);
            defsOp1.addAll(defsOp2);
            for (Unit u : defsOp1) {
                replaceWithNull(u);
                for (UnitValueBoxPair pair : (List<UnitValueBoxPair>) localUses.getUsesOf(u)) {
                    Unit use = pair.getUnit();
                    replaceWithNull(use);
                }
            }
          } // end if

        } // for if statements
    }


  private boolean isObject(Type t) {
    return t instanceof RefLikeType;
  }

    /**
     * Collect all the if statements comparing two locals with
     * an Eq or Ne expression
     *
     * @param body the body to analyze
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
                    candidates.add((IfStmt)u);
                    Debug.printDbg("[add if candidate: ", u);
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

//        if (u instanceof IfStmt) {
//            ConditionExpr expr = (ConditionExpr) ((IfStmt) u).getCondition();
//            if (expr.getOp1() instanceof IntConstant && ((IntConstant)expr.getOp1()).value == 0 ) {
//                expr.setOp1(NullConstant.v());
//                Debug.printDbg("[null] replacing with null in ", u);
//                Debug.printDbg(" new u: ", u);
//            } else if (expr.getOp2() instanceof IntConstant && ((IntConstant)expr.getOp2()).value == 0 ) {
//              expr.setOp2(NullConstant.v());
//              Debug.printDbg("[null] replacing with null in ", u);
//              Debug.printDbg(" new u: ", u);
//          } else {
//            throw new RuntimeException ("ERROR: if has no IntConstant to replace by NullConstant! ("+ u +")");
//          }
//        } else
      if (u instanceof AssignStmt) {
        	AssignStmt s = (AssignStmt) u;
            Value v = s.getRightOp();
            if ((v instanceof IntConstant) && ((IntConstant) v).value == 0) {
                s.setRightOp(NullConstant.v());
                Debug.printDbg("[null] replacing with null in ", u);
                Debug.printDbg(" new u: ", u);
            }
        }

    }




}

