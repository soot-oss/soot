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
import soot.jimple.toolkits.pointer.*;
import java.util.*;
import soot.util.*;

/** Implements an available expressions analysis on local variables. 
 * The current implementation is slow but correct.
 * A better implementation would use an implicit universe and
 * the kill rule would be computed on-the-fly for each statement. */
public class FastAvailableExpressionsAnalysis extends ForwardFlowAnalysis
{
    SideEffectTester st;

    Map unitToGenerateSet;
    Map unitToPreserveSet;
    Map rhsToContainingStmt;

    FlowSet emptySet;

    public FastAvailableExpressionsAnalysis(DirectedGraph dg, SootMethod m,
            SideEffectTester st)
    {
        super(dg);
        this.st = st;

        ExceptionalUnitGraph g = (ExceptionalUnitGraph)dg;
        LocalDefs ld = new SmartLocalDefs(g, new SimpleLiveLocals(g));

        // maps an rhs to its containing stmt.  object equality in rhs.
        rhsToContainingStmt = new HashMap();

        emptySet = new ToppedSet(new ArraySparseSet());

        // Create generate sets
        {
            unitToGenerateSet = new HashMap(g.size() * 2 + 1, 0.7f);

            Iterator unitIt = g.iterator();

            while(unitIt.hasNext())
            {
                Unit s = (Unit) unitIt.next();

                FlowSet genSet = (FlowSet)emptySet.clone();
                // In Jimple, expressions only occur as the RHS of an AssignStmt.
                if (s instanceof AssignStmt)
                {
                    AssignStmt as = (AssignStmt)s;
                    if (as.getRightOp() instanceof Expr ||
                        as.getRightOp() instanceof FieldRef)
                    {
                        Value gen = as.getRightOp();
                        rhsToContainingStmt.put(gen, s);

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

                unitToGenerateSet.put(s, genSet);
            }
        }

        doAnalysis();
    }

    protected Object newInitialFlow()
    {
        Object newSet = emptySet.clone();
        ((ToppedSet)newSet).setTop(true);
        return newSet;
    }

    protected Object entryInitialFlow()
    {
        return emptySet.clone();
    }

    protected void flowThrough(Object inValue, Object unit, Object outValue)
    {
        FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

        in.copy(out);
        if (((ToppedSet)in).isTop())
            return;

        // Perform generation
            out.union((FlowSet) unitToGenerateSet.get(unit), out);

        // Perform kill.
	    Unit u = (Unit)unit;
	    List toRemove = new ArrayList();

            if (((ToppedSet)out).isTop())
            {
                throw new RuntimeException("trying to kill on topped set!");
            }
	    List l = new LinkedList();
            l.addAll(((FlowSet)out).toList());
            Iterator it = l.iterator();

            // iterate over things (avail) in out set.
            while (it.hasNext())
            {
                Value avail = (Value) it.next();
                if (avail instanceof FieldRef)
                {
                    if (st.unitCanWriteTo(u, avail)) {
                        out.remove(avail, out);
                    }
                }
                else
                {
                    Iterator usesIt = avail.getUseBoxes().iterator();

                    // iterate over uses in each avail.
                    while (usesIt.hasNext())
                    {
                        Value use = ((ValueBox)usesIt.next()).getValue();
                        
                        if (st.unitCanWriteTo(u, use)) {
                            out.remove(avail, out);
                        }
                    }
                }
            }
    }

    protected void merge(Object in1, Object in2, Object out)
    {
        FlowSet inSet1 = (FlowSet) in1,
            inSet2 = (FlowSet) in2;

        FlowSet outSet = (FlowSet) out;

        inSet1.intersection(inSet2, outSet);
    }
    
    protected void copy(Object source, Object dest)
    {
        FlowSet sourceSet = (FlowSet) source,
            destSet = (FlowSet) dest;
            
        sourceSet.copy(destSet);
    }
}

