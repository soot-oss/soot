package soot.jimple.toolkits.scalar.pre;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;

class OptimalExprs
{
    LatestExprs lat;
    IsolatedExprs iso;

    public OptimalExprs(BlockGraph g, LatestExprs lat, IsolatedExprs iso,
                                FlowUniverse uni)
    {
        this.lat = lat; this.iso = iso;
    }

    public BoundedFlowSet getOptimalExprsBefore(Block b)
    {
        BoundedFlowSet res = iso.getIsolatedExprsAfter(b);
        res.complement(res);
        res.union(lat.getLatestExprsBefore(b), res);
        return res;
    }
}
