/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
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

package soot.dex;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import soot.ArrayType;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.RefType;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
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
import soot.jimple.NeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NullConstant;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.StringConstant;
import soot.jimple.internal.AbstractInstanceInvokeExpr;
import soot.jimple.internal.AbstractInvokeExpr;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.LocalUses;
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
public class DexNullTransformer extends BodyTransformer {
	// Note: we need an instance variable for inner class access, treat this as
	// a local variable (including initialization before use)
	private boolean usedAsObject;
    public static DexNullTransformer v() {
        return new DexNullTransformer();
    }

   Local l = null;
    
	@SuppressWarnings("unchecked")
	protected void internalTransform(final Body body, String phaseName, @SuppressWarnings("rawtypes") Map options) {
        ExceptionalUnitGraph g = new ExceptionalUnitGraph(body);
        SmartLocalDefs localDefs = new SmartLocalDefs(g, new SimpleLiveLocals(g));
        SimpleLocalUses localUses = new SimpleLocalUses(g, localDefs);

        for (Local loc: getNullCandidates(body)) {
            System.out.println("\n[null candidate] "+ loc);
            usedAsObject = false;
            List<Unit> defs = collectDefinitionsWithAliases(loc, localDefs, localUses, body);
            // check if no use
            int check = 0;
            for (Unit u  : defs) {
              for (UnitValueBoxPair pair : (List<UnitValueBoxPair>) localUses.getUsesOf(u)) {
                check++;
                System.out.println("[use in u]: "+ pair.getUnit());
              }
            }
//            if (check == 0) {
//              System.out.println("[removing local] "+ l);
//              for (Unit u: defs) {
//                body.getUnits().remove(u);
//              }
//              body.getLocals().remove(l);
//            }
            // process normally
            boolean doBreak = false;
            for (Unit u  : defs) {
              
              // put correct local in l
              if (u instanceof AssignStmt) {
                l = (Local)((AssignStmt)u).getLeftOp();
              } else if (u instanceof IdentityStmt) {
                l = (Local)((IdentityStmt)u).getLeftOp();
              }
              
              // check defs
              u.apply(new AbstractStmtSwitch() {
                public void caseAssignStmt (AssignStmt stmt) {
                  Value r = stmt.getRightOp();
                      if (r instanceof FieldRef)
                          usedAsObject = isObject(((FieldRef) r).getFieldRef().type());
                      else if (r instanceof ArrayRef)
                          usedAsObject = true;//((ArrayRef) r).getType() instanceof RefType;
                      else if (r instanceof StringConstant || r instanceof NewExpr || r instanceof NewArrayExpr)
                          usedAsObject = true;
                      else if (r instanceof CastExpr)
                          usedAsObject = isObject (((CastExpr)r).getCastType());
                      else if (r instanceof InvokeExpr)
                          usedAsObject = isObject(((InvokeExpr) r).getType());
                      // introduces alias
                      else if (r instanceof Local) {}

                }
                public void caseIdentityStmt(IdentityStmt stmt) {
                  if (stmt.getLeftOp() == l)
                      usedAsObject = isObject(stmt.getRightOp().getType());
              }
              });
              if (usedAsObject) {
                doBreak = true;
                break;
              }
              
              // check uses
                for (UnitValueBoxPair pair : (List<UnitValueBoxPair>) localUses.getUsesOf(u)) {
                    Unit use = pair.getUnit();
                    use.apply( new AbstractStmtSwitch() {
                            private boolean examineInvokeExpr(InvokeExpr e) {
                                List<Value> args = e.getArgs();
                                List<Type> argTypes = e.getMethod().getParameterTypes();
                                assert args.size() == argTypes.size();
                                for (int i = 0; i < args.size(); i++) {
                                	if (args.get(i) == l && argTypes.get(i) instanceof RefType) {
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
                            }
                            public void caseAssignStmt(AssignStmt stmt) {
                                // gets value assigned
                                Value r = stmt.getRightOp();
                                if (stmt.getLeftOp() == l) {
                                    if (r instanceof FieldRef)
                                        usedAsObject = isObject(((FieldRef) r).getFieldRef().type());
                                    else if (r instanceof ArrayRef)
                                        usedAsObject = true;//((ArrayRef) r).getType() instanceof RefType;
                                    else if (r instanceof StringConstant || r instanceof NewExpr || r instanceof NewArrayExpr)
                                        usedAsObject = true;
                                    else if (r instanceof CastExpr)
                                        usedAsObject = isObject (((CastExpr)r).getCastType());
                                    else if (r instanceof InvokeExpr)
                                        usedAsObject = isObject(((InvokeExpr) r).getType());
                                    // introduces alias
                                    else if (r instanceof Local) {}

                                }
                                // used to assign
                                if (stmt.getRightOp() == l) {
                                    Value l = stmt.getLeftOp();
                                    if (l instanceof StaticFieldRef && isObject(((StaticFieldRef) l).getFieldRef().type()))
                                        usedAsObject = true;
                                    else if (l instanceof InstanceFieldRef && isObject(((InstanceFieldRef) l).getFieldRef().type()))
                                        usedAsObject = true;
                                    //TODO: else if (l instanceof Local)
                                      
                                }

                                // is used as value (does not exlude assignment)
                                if (r instanceof InvokeExpr)
                                    usedAsObject = usedAsObject || examineInvokeExpr((InvokeExpr) stmt.getRightOp());
                            }

                            public void caseIdentityStmt(IdentityStmt stmt) {
                                if (stmt.getLeftOp() == l)
                                    usedAsObject = isObject(stmt.getRightOp().getType());
                            }
                            public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
                                usedAsObject = stmt.getOp() == l;
                            }
                            public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
                                usedAsObject = stmt.getOp() == l;
                            }
                            public void caseReturnStmt(ReturnStmt stmt) {
                                usedAsObject = stmt.getOp() == l && isObject(body.getMethod().getReturnType());
                                System.out.println (" [return stmt] "+ stmt +" usedAsObject: "+ usedAsObject +", return type: "+ body.getMethod().getReturnType());
                                System.out.println (" class: "+ body.getMethod().getReturnType().getClass());
                            }
                        });
                    
                    
                    if (usedAsObject) {
                        doBreak = true;
                        break;
                    }

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
    }

  private boolean isObject(Type t) {
    if (t instanceof RefType || t instanceof ArrayType)
      return true;
    return false;
  }
	
    /**
     * Collect all the locals which are assigned a IntConstant(0) or are used
     * within a zero comparison.
     *
     * @param body the body to analyze
     */
    private Set<Local> getNullCandidates(Body body) {
        Set<Local> candidates = new HashSet<Local>();
        Iterator<Unit> i = body.getUnits().iterator();
        while (i.hasNext()) {
            Unit u = i.next();
            if (u instanceof AssignStmt) {
                AssignStmt a = (AssignStmt) u;
                if (! (a.getLeftOp() instanceof Local))
                    continue;
                Local l = (Local) a.getLeftOp();
                Value r = a.getRightOp();
                if ((r instanceof IntConstant && ((IntConstant) r).value == 0)) {
                    candidates.add(l);
                    System.out.println("[add null candidate: "+ u);
                }
            }
            else if (u instanceof IfStmt) {
                ConditionExpr expr = (ConditionExpr) ((IfStmt) u).getCondition();
                if (isZeroComparison(expr) && expr.getOp1() instanceof Local) {
                    candidates.add((Local) expr.getOp1());
                    System.out.println("[add null candidate if: "+ u);
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
                System.out.println("[null] replacing with null in "+ u);
                System.out.println(" new u: "+ u);
            }
        } else if (u instanceof AssignStmt) {
        	AssignStmt s = (AssignStmt) u;
            Value v = s.getRightOp();
            if ((v instanceof IntConstant) && ((IntConstant) v).value == 0) {
                s.setRightOp(NullConstant.v());
                System.out.println("[null] replacing with null in "+ u);
                System.out.println(" new u: "+ u);
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


    /**
     * Collect definitions of l in body including the definitions of aliases of l.
     *
     * In this context an alias is a local that propagates its value to l.
     *
     * @param l the local whose definitions are to collect
     * @param localDefs the LocalDefs object
     * @param body the body that contains the local
     */
    private List<Unit> collectDefinitionsWithAliases(Local l, LocalDefs localDefs, LocalUses localUses, Body body) {
        Set<Local> seenLocals = new HashSet<Local>();
        Stack<Local> newLocals = new Stack<Local>();
        List<Unit> defs = new LinkedList<Unit>();
        newLocals.push(l);

        while (!newLocals.empty()) {
            Local local = newLocals.pop();
            System.out.println("[null local] "+ local);
            if (seenLocals.contains(local))
                continue;
            for (Unit u : collectDefinitions(local, localDefs, body)) {
                if (u instanceof AssignStmt) {
                    Value r = ((AssignStmt) u).getRightOp();
                    if (r instanceof Local && ! seenLocals.contains((Local) r))
                        newLocals.push((Local) r);
                }
                defs.add(u);
                //
                for (UnitValueBoxPair pair : (List<UnitValueBoxPair>) localUses.getUsesOf(u)) {
                  Unit unit = pair.getUnit();
                  if (unit instanceof AssignStmt) {
                    Value right = ((AssignStmt) unit).getRightOp();
                    Value left = ((AssignStmt) unit).getLeftOp();
                    if (right == local  && left instanceof Local && ! seenLocals.contains((Local) left))
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
     * @param l the local whose definitions are to collect
     * @param localDefs the LocalDefs object
     * @param body the body that contains the local
     */
    private List<Unit> collectDefinitions(Local l, LocalDefs localDefs, Body body) {
        List <Unit> defs = new LinkedList<Unit>();
        for (Unit u : body.getUnits()) {
            List<Unit> defsOf = localDefs.getDefsOfAt(l, u);
            if (defsOf != null)
                defs.addAll(defsOf);
        }
        for (Unit u: defs) {
          System.out.println("[add def] "+ u);
        }
        return defs;
    }
}

