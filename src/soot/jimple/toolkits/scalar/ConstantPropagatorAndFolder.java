/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Phong Co
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
import soot.options.*;
import soot.*;
import soot.toolkits.scalar.*;
import soot.jimple.*;

import java.util.*;

import soot.toolkits.graph.*;

/** Does constant propagation and folding. 
 * Constant folding is the compile-time evaluation of constant
 * expressions (i.e. 2 * 3). */
public class ConstantPropagatorAndFolder extends BodyTransformer
{
    public ConstantPropagatorAndFolder( Singletons.Global g ) {}
    public static ConstantPropagatorAndFolder v() { return G.v().soot_jimple_toolkits_scalar_ConstantPropagatorAndFolder(); }

    protected void internalTransform(Body b, String phaseName, Map<String,String> options)
    {
        int numFolded = 0;
        int numPropagated = 0;

        if (Options.v().verbose())
            G.v().out.println("[" + b.getMethod().getName() +
                               "] Propagating and folding constants...");

        UnitGraph g = new ExceptionalUnitGraph(b);
        LocalDefs localDefs = LocalDefs.Factory.newLocalDefs(g);

        // Perform a constant/local propagation pass.
        Orderer<Unit> orderer = new PseudoTopologicalOrderer<Unit>();
        
        if (b.getMethod().getSignature().equals("<java.util.concurrent.ConcurrentSkipListMap: int compare(java.lang.Object,java.lang.Object)>"))
        	System.out.println("x");
        
        // go through each use box in each statement
        for (Unit u : orderer.newList(g, false)) {

            // propagation pass
            for (ValueBox useBox : u.getUseBoxes()) {
                Value value = useBox.getValue();
                if (value instanceof Local) {
                    Local local = (Local) value;
                    List<Unit> defsOfUse = localDefs.getDefsOfAt(local, u);
                    if (defsOfUse.size() == 1) {
                        DefinitionStmt defStmt = (DefinitionStmt) defsOfUse.get(0);
                        Value rhs = defStmt.getRightOp();
                        if (rhs instanceof NumericConstant
                        		|| rhs instanceof StringConstant
                        		|| rhs instanceof NullConstant) {
                            if (useBox.canContainValue(rhs)) {
                                useBox.setValue(rhs);
                                numPropagated++;
                            }
                        }
                        else if (rhs instanceof CastExpr) {
                        	CastExpr ce = (CastExpr) rhs;
                        	if (ce.getCastType() instanceof RefType
                        			&& ce.getOp() instanceof NullConstant) {
                                defStmt.getRightOpBox().setValue(NullConstant.v());
                                numPropagated++;
                        	}
                        }
                    }
                }
            }
                
            // folding pass
            for (ValueBox useBox : u.getUseBoxes()) {
                Value value = useBox.getValue();
                if (!(value instanceof Constant)) {
                    if (Evaluator.isValueConstantValued(value)) {
                        Value constValue =
                            Evaluator.getConstantValueOf(value);
                        if (useBox.canContainValue(constValue)) {
                            useBox.setValue(constValue);
                            numFolded++;
                        }
                    }
                }
            }
        }

       if (Options.v().verbose())
            G.v().out.println("[" + b.getMethod().getName() +
                "]     Propagated: " + numPropagated + ", Folded:  " + numFolded);

    } // optimizeConstants

}
    




