package soot.jimple.toolkits.scalar.pre;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;

class AnticipEarliestExprs
{
    AnticipatableExprs ant;
    EarliestnessAnalysis earl;

    public AnticipEarliestExprs(BlockGraph g, AnticipatableExprs a, 
                                FlowUniverse uni)
    {
        this.ant = a;
        this.earl = new EarliestnessAnalysis(g, a, uni);
    }

    /* universe is all expressions in the program. */
    public BoundedFlowSet getAnticipEarliestExprsBefore(Block b)
    {
        BoundedFlowSet res = ant.getAnticipatableExprsBefore(b);
        res.intersection((FlowSet)earl.getFlowBefore(b), res);

        return res;
    }
}
