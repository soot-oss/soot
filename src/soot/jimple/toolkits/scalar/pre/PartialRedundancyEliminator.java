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
    }

    protected void internalTransform(Body b, String phaseName, Map options)
    {
        BlockGraph g = new BriefBlockGraph(b);

        /* we need a universe of all of the expressions. */
        Iterator unitsIt = b.getUnits().iterator();
        ArrayList exprs = new ArrayList();
        HashSet exprSet = new HashSet();

        EquivalentValue l = null;

        while (unitsIt.hasNext())
        {
            Stmt s = (Stmt)unitsIt.next();

            if (s instanceof AssignStmt)
            {
                Value v = ((AssignStmt)s).getRightOp();
                EquivalentValue ev = new EquivalentValue(v);

                if (l != null)
                    System.out.println("got value "+v+" last was "+l+" equality "+ev.equals(l));
                if (v instanceof Expr && !exprSet.contains(ev))
                {
                    l = ev;
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
        IsolatedExprs isoExprs = new IsolatedExprs(g, latExprs, exprUniv);
        OptimalExprs optExprs = new OptimalExprs(g, latExprs, isoExprs, exprUniv);
        RedundantExprs rednExprs = new RedundantExprs(g, latExprs, isoExprs, exprUniv);

        {
            Iterator it = g.iterator();
            while (it.hasNext())
            {
                Block bl = (Block)it.next();
                System.out.println("antExprs: "+antExprs.getAnticipatableExprsBefore(bl));
                System.out.println("anteaExprs: "+aneaExprs.getAnticipEarliestExprsBefore(bl));
                System.out.println("optBefore: "+optExprs.getOptimalExprsBefore(bl));
                System.out.println("rednBefore: "+rednExprs.getRedundantExprsOf(bl));
                System.out.println(bl);
            }
        }

    }
}
