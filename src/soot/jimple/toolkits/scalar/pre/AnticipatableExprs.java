package soot.jimple.toolkits.scalar.pre;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;

class AnticipatableExprs
{
    GlobalAnticipatabilityAnalysis a;
     
    public AnticipatableExprs(BlockGraph g, FlowUniverse uni)
    {
        a = new GlobalAnticipatabilityAnalysis(g, uni);
    }

    /* universe is all expressions in the program. */
    public BoundedFlowSet getAnticipatableExprsBefore(Block b)
    {
        return (BoundedFlowSet)a.getFlowBefore(b);
    }
}
