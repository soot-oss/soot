package soot.jimple.toolkits.scalar.pre;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;

class LatestExprs
{
    DelayedExprs del;
    FlowUniverse uni;
    BlockGraph g;

    public LatestExprs(BlockGraph g, DelayedExprs del, FlowUniverse uni)
    {
        this.g = g;
        this.del = del;
        this.uni = uni;
    }

    public BoundedFlowSet getLatestExprsBefore(Block b)
    {
        BoundedFlowSet res = new ArrayPackedSet(uni); res.complement(res);

        Iterator bSuccsIt = g.getSuccsOf(b).iterator();
        while (bSuccsIt.hasNext())
            res.intersection(del.getDelayedExprsBefore((Block)bSuccsIt.next()), res);

        res.union(LocallyAnticipatableExprs.getAntLocExprsOf(b, uni), res);
        res.intersection(del.getDelayedExprsBefore(b), res);

        return res;
    }
}
