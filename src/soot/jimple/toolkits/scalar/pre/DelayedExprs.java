package soot.jimple.toolkits.scalar.pre;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;

class DelayedExprs
{
    AnticipEarliestExprs anea;
    DelayednessAnalysis del;

    public DelayedExprs(BlockGraph g, AnticipEarliestExprs anea, 
                                FlowUniverse uni)
    {
        this.anea = anea;
        this.del = new DelayednessAnalysis(g, anea, uni);
    }

    public BoundedFlowSet getDelayedExprsBefore(Block b)
    {
        BoundedFlowSet res = (BoundedFlowSet)(((BoundedFlowSet)del.getFlowBefore(b)).clone());
        res.union(anea.getAnticipEarliestExprsBefore(b), res);
        return res;
    }

    public BoundedFlowSet getDelayedExprsAfter(Block b)
    {
        return (BoundedFlowSet)del.getFlowAfter(b);
    }
}
