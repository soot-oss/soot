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

package soot.jimple.toolkits.scalar.pre;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;

public class PartialRedundancyEliminator extends BodyTransformer
{
    private static PartialRedundancyEliminator instance = new PartialRedundancyEliminator();
    private PartialRedundancyEliminator() {}

    public static PartialRedundancyEliminator v() { return instance; }

    class EquivalentValue
    {
        Value e;
        public EquivalentValue(Value e) { this.e = e; }
        public boolean equals(Object o) { return e.equivTo(((EquivalentValue)o).e); }
        public int hashCode() { return e.equivHashCode(); }
        public String toString() { return e.toString(); }
    }

    protected void internalTransform(Body b, String phaseName, Map options)
    {
        BlockGraph g = new BriefBlockGraph(b);

        /* we need a universe of all of the expressions. */
        Iterator unitsIt = b.getUnits().iterator();
        ArrayList exprs = new ArrayList();
        HashSet exprSet = new HashSet();

        while (unitsIt.hasNext())
        {
            Stmt s = (Stmt)unitsIt.next();

            if (s instanceof AssignStmt)
            {
                Value v = ((AssignStmt)s).getRightOp();
                EquivalentValue ev = new EquivalentValue(v);

                if (v instanceof Expr && !exprSet.contains(ev))
                {
                    exprs.add(v);
                    exprSet.add(ev);
                }
            }
        }

        System.out.println("universe: "+exprs);
        FlowUniverse exprUniv = new FlowUniverse(exprs.toArray());

        AnticipatableExprs antExprs = new AnticipatableExprs(g, exprUniv);
        AnticipEarliestExprs aneaExprs = new AnticipEarliestExprs(g, antExprs, exprUniv);
        DelayedExprs delExprs = new DelayedExprs(g, aneaExprs, exprUniv);
        LatestExprs latExprs = new LatestExprs(g, delExprs, exprUniv);
//          IsolatedExprs isoExprs = new IsolatedExprs(g, latExprs, exprUniv);
//          OptimalExprs optExprs = new OptimalExprs(g, latExprs, isoExprs, exprUniv);
//          RedundantExprs rednExprs = new RedundantExprs(g, latExprs, isoExprs, exprUniv);

        {
            Iterator it = g.iterator();
            while (it.hasNext())
            {
                Block bl = (Block)it.next();
                System.out.println("---\n"+bl);
                System.out.println("transExprs: "+LocallyTransparentExprs.getTransLocExprsOf(bl, exprUniv));
                System.out.println("antLocExprs: "+ LocallyAnticipatableExprs.getAntLocExprsOf(bl, exprUniv));
                System.out.println("antExprs: "+antExprs.getAnticipatableExprsBefore(bl));
                System.out.println("anteaExprs: "+aneaExprs.getAnticipEarliestExprsBefore(bl));
                System.out.println("delExprs: "+delExprs.getDelayedExprsBefore(bl));
                System.out.println("latExprs: "+latExprs.getLatestExprsBefore(bl));
//                  System.out.println("optBefore: "+optExprs.getOptimalExprsBefore(bl));
//                  System.out.println("rednBefore: "+rednExprs.getRedundantExprsOf(bl));
                System.out.println();
            }
        }

    }
}
