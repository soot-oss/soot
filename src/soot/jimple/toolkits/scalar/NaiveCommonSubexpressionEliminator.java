/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrick Lam
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
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

/** Runs an available expressions analysis on a body, then
 * eliminates common subexpressions.
 *
 * This implementation is especially slow, as it does not
 * run on basic blocks.  A better implementation (which wouldn't
 * catch every single cse, but would get most) would use
 * basic blocks instead. 
 *
 * It is also slow because the flow universe is explicitly created; it
 * need not be.  A better implementation would implicitly compute the
 * kill sets at every node.  */

public class NaiveCommonSubexpressionEliminator extends BodyTransformer
{ 
    private static NaiveCommonSubexpressionEliminator instance = 
        new NaiveCommonSubexpressionEliminator();
    private NaiveCommonSubexpressionEliminator() {}

    public static NaiveCommonSubexpressionEliminator v() { return instance; }

    public String getDeclaredOptions() { return super.getDeclaredOptions(); }

    public String getDefaultOptions() { return "disabled"; }

    /** Common subexpression eliminator. */
    protected void internalTransform(Body b, String phaseName, Map options)
    {
        int counter = 0;

        Options.checkOptions(options, phaseName, getDeclaredOptions());

        if(Main.isVerbose)
            System.out.println("[" + b.getMethod().getName() +
                "]     Eliminating common subexpressions (naively)...");

        AvailableExpressions ae = new AvailableExpressions(b);

        Chain units = b.getUnits();
        Iterator unitsIt = units.snapshotIterator();
        while (unitsIt.hasNext())
        {
            Stmt s = (Stmt) unitsIt.next();

            if (s instanceof AssignStmt)
            {
                Chain availExprs = ae.getAvailableEquivsBefore(s);
                Value v = (Value)((AssignStmt)s).getRightOp();
                EquivalentValue ev = new EquivalentValue(v);

                if (availExprs.contains(ev))
                {
                    // now we need to track down the containing stmt.
                    List availPairs = ae.getAvailablePairsBefore(s);

                    Iterator availIt = availPairs.iterator();
                    while (availIt.hasNext())
                    {
                        UnitValueBoxPair up = (UnitValueBoxPair)availIt.next();
                        if (up.getValueBox().getValue().equivTo(v))
                        {
                            // create a local for temp storage.
                            // (we could check to see that the def must-reach, I guess...)
                            Local l = Jimple.v().newLocal("$cseTmp"+counter, Type.toMachineType(v.getType()));
                            counter++;

                            b.getLocals().add(l);

                            // I hope it's always an AssignStmt -- Jimple should guarantee this.
                            AssignStmt origCalc = (AssignStmt)up.getUnit();
                            Value origLHS = origCalc.getLeftOp();

                            origCalc.setLeftOp(l);
                            
                            Unit copier = Jimple.v().newAssignStmt(origLHS, l);
                            units.insertAfter(copier, origCalc);

                            ((AssignStmt)s).setRightOp(l);
                        }
                    }
                }
            }
        }
    }
}
