package soot.jimple.toolkits.scalar.pre;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;

class RedundantExprs
{
    LatestExprs lat;
    IsolatedExprs iso;
    FlowUniverse uni;

    public RedundantExprs(BlockGraph g, LatestExprs lat, IsolatedExprs iso,
                                FlowUniverse uni)
    {
        this.lat = lat; this.iso = iso; this.uni = uni;
    }

    public BoundedFlowSet getRedundantExprsOf(Block b)
    {
        BoundedFlowSet res = iso.getIsolatedExprsAfter(b);
        res.union(lat.getLatestExprsBefore(b), res);
        res.complement(res);
        res.intersection(LocallyAnticipatableExprs.getAntLocExprsOf(b, uni), res);
        return res;
    }
}
