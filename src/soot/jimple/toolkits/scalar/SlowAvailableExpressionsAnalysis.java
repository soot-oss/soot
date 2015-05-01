/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrick Lam
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.jimple.toolkits.scalar;
import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.jimple.*;

import java.util.*;
import soot.util.*;

// future work: fieldrefs.

/** Implements an available expressions analysis on local variables. 
 * The current implementation is slow but correct.
 * A better implementation would use an implicit universe and
 * the kill rule would be computed on-the-fly for each statement. */
public class SlowAvailableExpressionsAnalysis extends ForwardFlowAnalysis<Unit, FlowSet<Value>>
{
    Map<Unit, BoundedFlowSet<Value>> unitToGenerateSet;
    Map<Unit, BoundedFlowSet<Value>> unitToPreserveSet;
    Map<Value, Stmt> rhsToContainingStmt;
    private final HashMap<Value, EquivalentValue> valueToEquivValue;

    FlowSet<Value> emptySet;
    
    public SlowAvailableExpressionsAnalysis(DirectedGraph<Unit> dg)
    {
        super(dg);

        UnitGraph g = (UnitGraph)dg;

        /* we need a universe of all of the expressions. */
        ArrayList<Value> exprs = new ArrayList<Value>();

        // Consider "a + b".  containingExprs maps a and b (object equality) both to "a + b" (equivalence).
        HashMap<EquivalentValue, Chain<EquivalentValue>> containingExprs =
        		new HashMap<EquivalentValue, Chain<EquivalentValue>>();

        // maps a Value to its EquivalentValue.
        valueToEquivValue = new HashMap<Value, EquivalentValue>();

        // maps an rhs to its containing stmt.  object equality in rhs.
        rhsToContainingStmt = new HashMap<Value, Stmt>();

        HashMap<EquivalentValue, Chain<Value>> equivValToSiblingList = new HashMap<EquivalentValue, Chain<Value>>();

        // Create the set of all expressions, and a map from values to their containing expressions.
        for (Unit u : g.getBody().getUnits()) {
            Stmt s = (Stmt) u;

            if (s instanceof AssignStmt)
            {
                Value v = ((AssignStmt)s).getRightOp();
                rhsToContainingStmt.put(v, s);
                EquivalentValue ev = valueToEquivValue.get(v);
                if (ev == null)
                {
                    ev = new EquivalentValue(v);
                    valueToEquivValue.put(v, ev);
                }

                Chain<Value> sibList = null;
                if (equivValToSiblingList.get(ev) == null)
                    { sibList = new HashChain<Value>(); equivValToSiblingList.put(ev, sibList); }
                else
                    sibList = equivValToSiblingList.get(ev);
                
                if (!sibList.contains(v)) sibList.add(v);

                if (!(v instanceof Expr))
                    continue;

                if (!exprs.contains(v))
                {
                    exprs.add(v);

                    // Add map values for contained objects.
                    for (ValueBox vb : v.getUseBoxes()) {
                        Value o = vb.getValue();
                        EquivalentValue eo = valueToEquivValue.get(o);
                        if (eo == null)
                        {
                            eo = new EquivalentValue(o);
                            valueToEquivValue.put(o, eo);
                        }

                        if (equivValToSiblingList.get(eo) == null)
                            { sibList = new HashChain<Value>(); equivValToSiblingList.put(eo, sibList); }
                        else
                            sibList = equivValToSiblingList.get(eo);
                        if (!sibList.contains(o)) sibList.add(o);

                        Chain<EquivalentValue> l = null;
                        if (containingExprs.containsKey(eo))
                            l = containingExprs.get(eo);
                        else
                        {
                            l = new HashChain<EquivalentValue>();
                            containingExprs.put(eo, l);
                        }

                        if (!l.contains(ev))
                            l.add(ev);
                    }
                }
            }
        }

        FlowUniverse<Value> exprUniv = new ArrayFlowUniverse<Value>(exprs.toArray(new Value[exprs.size()]));
        emptySet = new ArrayPackedSet<Value>(exprUniv);

        // Create preserve sets.
        {
            unitToPreserveSet = new HashMap<Unit, BoundedFlowSet<Value>>(g.size() * 2 + 1, 0.7f);

            for (Unit s : g) {
                BoundedFlowSet<Value> killSet = new ArrayPackedSet<Value>(exprUniv);

                // We need to do more!  In particular handle invokeExprs, etc.

                // For each def (say of x), kill the set of exprs containing x.
                for (ValueBox box : s.getDefBoxes()) {
                    Value v = box.getValue();
                    EquivalentValue ev = valueToEquivValue.get(v);

                    Chain<EquivalentValue> c = containingExprs.get(ev);
                    if (c != null)
                    {
                    	for (EquivalentValue container : c)
                        {
                            // Add all siblings of it.next().
                            for (Value sibVal : equivValToSiblingList.get(container))
                                killSet.add(sibVal, killSet);
                        }
                    }
                }

                // Store complement
                    killSet.complement(killSet);
                    unitToPreserveSet.put(s, killSet);
            }
        }

        // Create generate sets
        {
            unitToGenerateSet = new HashMap<Unit, BoundedFlowSet<Value>>(g.size() * 2 + 1, 0.7f);

            for (Unit s : g) {
                BoundedFlowSet<Value> genSet = new ArrayPackedSet<Value>(exprUniv);
                // In Jimple, expressions only occur as the RHS of an AssignStmt.
                if (s instanceof AssignStmt)

                {
                    AssignStmt as = (AssignStmt)s;
                    if (as.getRightOp() instanceof Expr)
                    {
                        // canonical rep of as.getRightOp();
                        Value gen = as.getRightOp();

                        boolean cantAdd = false;
                        if (gen instanceof NewExpr || 
                               gen instanceof NewArrayExpr || 
                               gen instanceof NewMultiArrayExpr)
                            cantAdd = true;
                        if (gen instanceof InvokeExpr)
                            cantAdd = true;

                        // Whee, double negative!
                        if (!cantAdd)
                            genSet.add(gen, genSet);
                    }
                }

                // remove the kill set
                genSet.intersection(unitToPreserveSet.get(s), genSet);

                unitToGenerateSet.put(s, genSet);
            }
        }

        doAnalysis();
    }

    protected FlowSet<Value> newInitialFlow()
    {
        BoundedFlowSet<Value> out = (BoundedFlowSet<Value>)emptySet.clone();
        out.complement(out);
        return out;
    }

    protected FlowSet<Value> entryInitialFlow()
    {
        return emptySet.clone();
    }

    protected void flowThrough(FlowSet<Value> in, Unit unit, FlowSet<Value> out)
    {
        // Perform kill
            in.intersection(unitToPreserveSet.get(unit), out);

        // Perform generation
            out.union(unitToGenerateSet.get(unit), out);
    }

    protected void merge(FlowSet<Value> inSet1, FlowSet<Value> inSet2,
    		FlowSet<Value> outSet)
    {
        inSet1.intersection(inSet2, outSet);
    }
    
    protected void copy(FlowSet<Value> sourceSet, FlowSet<Value> destSet)
    {
        sourceSet.copy(destSet);
    }
}

