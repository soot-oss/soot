package soot.jimple.toolkits.scalar.pre;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;

class IsolatedExprs
{
    IsolatednessAnalysis iso;

    public IsolatedExprs(BlockGraph g, LatestExprs lat, 
                                FlowUniverse uni)
    {
        this.iso = new IsolatednessAnalysis(g, lat, uni);
    }

    public BoundedFlowSet getIsolatedExprsBefore(Block b)
    {
        return (BoundedFlowSet)iso.getFlowBefore(b);
    }

    public BoundedFlowSet getIsolatedExprsAfter(Block b)
    {
        return (BoundedFlowSet)iso.getFlowAfter(b);
    }
}
