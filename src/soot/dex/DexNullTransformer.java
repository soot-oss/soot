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

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.RefType;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
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
import soot.jimple.NewExpr;
import soot.jimple.NullConstant;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.StringConstant;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.LocalDefs;
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

	@SuppressWarnings("unchecked")
	protected void internalTransform(final Body body, String phaseName, @SuppressWarnings("rawtypes") Map options) {
        ExceptionalUnitGraph g = new ExceptionalUnitGraph(body);
        SmartLocalDefs localDefs = new SmartLocalDefs(g, new SimpleLiveLocals(g));
        SimpleLocalUses localUses = new SimpleLocalUses(g, localDefs);

        for (final Local l: getNullCandidates(body)) {
            usedAsObject = false;
            List<Unit> defs = collectDefinitionsWithAliases(l, localDefs, body);
            for (Unit u  : defs) {
                for (UnitValueBoxPair pair : (List<UnitValueBoxPair>) localUses.getUsesOf(u)) {
                    Unit use = pair.getUnit();
                    use.apply( new AbstractStmtSwitch() {
                            private boolean examineInvokeExpr(InvokeExpr e) {
                                List<Value> args = e.getArgs();
                                List<Type> argTypes = e.getMethod().getParameterTypes();
                                assert args.size() == argTypes.size();
                                for (int i = 0; i < args.size(); i++)
                                	if (args.get(i) == l && argTypes.get(i) instanceof RefType)
                                     return true;
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
                                        usedAsObject = ((FieldRef) r).getFieldRef().type() instanceof RefType;
                                    else if (r instanceof ArrayRef)
                                        usedAsObject = ((ArrayRef) r).getType() instanceof RefType;
                                    else if (r instanceof StringConstant || r instanceof NewExpr)
                                        usedAsObject = true;
                                    else if (r instanceof InvokeExpr)
                                        usedAsObject = ((InvokeExpr) r).getType() instanceof RefType;
                                    // introduces alias
                                    else if (r instanceof Local) {}

                                }
                                // used to assign
                                if (stmt.getRightOp() == r) {
                                    Value l = stmt.getLeftOp();
                                    if (l instanceof StaticFieldRef && ((StaticFieldRef) l).getFieldRef().type() instanceof RefType)
                                        usedAsObject = true;
                                    else if (l instanceof InstanceFieldRef && ((InstanceFieldRef) l).getFieldRef().type() instanceof RefType)
                                        usedAsObject = true;
                                }

                                // is used as value (does not exlude assignment)
                                if (r instanceof InvokeExpr)
                                    usedAsObject = usedAsObject || examineInvokeExpr((InvokeExpr) stmt.getRightOp());
                            }
                            public void caseIdentityStmt(IdentityStmt stmt) {
                                if (stmt.getLeftOp() == l)
                                    usedAsObject = (stmt.getRightOp().getType() instanceof RefType);
                            }
                            public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
                                usedAsObject = stmt.getOp() == l;
                            }
                            public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
                                usedAsObject = stmt.getOp() == l;
                            }
                            public void caseReturnStmt(ReturnStmt stmt) {
                                usedAsObject = stmt.getOp() == l && (body.getMethod().getReturnType() instanceof RefType);
                            }
                        });

                }
            }
            // change values
            if (usedAsObject) {
                for (Unit u : defs) {
                    replaceWithNull(u);
                    for (UnitValueBoxPair pair : (List<UnitValueBoxPair>) localUses.getUsesOf(u)) {
                        Unit use = pair.getUnit();
                        replaceWithNull(use);
                    }
                }
            }
        }
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
                if ((r instanceof IntConstant && ((IntConstant) r).value == 0))
                    candidates.add(l);
            }
            else if (u instanceof IfStmt) {
                ConditionExpr expr = (ConditionExpr) ((IfStmt) u).getCondition();
                if (isZeroComparison(expr) && expr.getOp1() instanceof Local)
                    candidates.add((Local) expr.getOp1());

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
            if (isZeroComparison(expr))
                expr.setOp2(NullConstant.v());
        } else if (u instanceof AssignStmt) {
        	AssignStmt s = (AssignStmt) u;
            Value v = s.getRightOp();
            if ((v instanceof IntConstant) && ((IntConstant) v).value == 0)
                s.setRightOp(NullConstant.v());
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
    private List<Unit> collectDefinitionsWithAliases(Local l, LocalDefs localDefs, Body body) {
        Set<Local> seenLocals = new HashSet<Local>();
        Stack<Local> newLocals = new Stack<Local>();
        List<Unit> defs = new LinkedList<Unit>();
        newLocals.push(l);

        while (!newLocals.empty()) {
            Local local = newLocals.pop();
            if (seenLocals.contains(local))
                continue;
            for (Unit u : collectDefinitions(local, localDefs, body)) {
                if (u instanceof AssignStmt) {
                    Value r = ((AssignStmt) u).getRightOp();
                    if (r instanceof Local && ! seenLocals.contains((Local) r))
                        newLocals.push((Local) r);
                }
                defs.add(u);
            }
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
        return defs;
    }
}

