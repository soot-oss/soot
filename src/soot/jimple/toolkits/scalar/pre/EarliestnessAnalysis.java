package soot.jimple.toolkits.scalar.pre;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;

/** An expression is <i>earliest</i> at the entrance to basic block <code>b</code> 
 * if no block from <code>entry</code> to <code>b</code> both includes a computation of
 * the expression and produces the same value as evaluating the expression at the
 * entrance to block <code>b</code>. */
class EarliestnessAnalysis extends ForwardFlowAnalysis
{
    BoundedFlowSet emptySet;
    FlowUniverse exprUniv;

    HashMap blockToGenerateSet;
    HashMap blockToPreserveSet;       

    public EarliestnessAnalysis(BlockGraph g, AnticipatableExprs a, FlowUniverse exprUniv)
    {
        super(g);
        blockToGenerateSet = new HashMap(g.size() * 2 + 1, 0.7f);
        blockToPreserveSet = new HashMap(g.size() * 2 + 1, 0.7f);

        emptySet = new ArrayPackedSet(exprUniv);
        this.exprUniv = exprUniv;

        Iterator blockIt = g.iterator();

        while (blockIt.hasNext())
        {
            Block b = (Block)blockIt.next();

            BoundedFlowSet nonTrans = LocallyTransparentExprs.getTransLocExprsOf(b, exprUniv);
            nonTrans.complement(nonTrans);
            blockToGenerateSet.put(b, nonTrans);

            BoundedFlowSet ant = a.getAnticipatableExprsBefore(b);
            blockToPreserveSet.put(b, ant);
        }

        doAnalysis();
    }

    protected Object newInitialFlow()
    {
        BoundedFlowSet allExprs = (BoundedFlowSet)emptySet.clone();
        allExprs.complement(allExprs);

        return allExprs;
    }

    protected void copy(Object source, Object dest)
    {
        FlowSet sourceSet = (FlowSet) source,
            destSet = (FlowSet) dest;
        
        sourceSet.copy(destSet);
    }

    protected void merge(Object in1, Object in2, Object out)
    {
        FlowSet inSet1 = (FlowSet) in1,
            inSet2 = (FlowSet) in2;
        
        FlowSet outSet = (FlowSet) out;
        
        inSet1.union(inSet2, outSet);
    }

    protected void flowThrough(Object inValue, Directed b, Object outValue)
    {
        BoundedFlowSet in = (BoundedFlowSet) inValue, out = (BoundedFlowSet) outValue;

        // Remove non-preserved sets from `in' and dump into `out'.
        in.intersection((FlowSet) blockToPreserveSet.get(b), out);

        // Add generated sets to `out'.
        out.union((FlowSet) blockToGenerateSet.get(b), out);
    }
}
