package olhotak.liveness;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;

class LiveVariablesAnalysis extends BackwardFlowAnalysis
{
    protected void copy(Object src, Object dest)
    {
        FlowSet srcSet  = (FlowSet) src;
        FlowSet destSet = (FlowSet) dest;
            
        srcSet.copy(destSet);
    }

    protected void merge(Object src1, Object src2, Object dest)
    {
        FlowSet srcSet1 = (FlowSet) src1;
        FlowSet srcSet2 = (FlowSet) src2;
        FlowSet destSet = (FlowSet) dest;

        srcSet1.union(srcSet2, destSet);
    }

    protected void flowThrough(Object srcValue, Object unit,
            Object destValue)
    {
        FlowSet dest = (FlowSet) destValue;
        FlowSet src  = (FlowSet) srcValue;
        Unit    s    = (Unit)    unit;
        src.copy (dest);

        // Take out kill set
        Iterator boxIt = s.getDefBoxes().iterator();
        while (boxIt.hasNext()) {
            ValueBox box = (ValueBox) boxIt.next();
            Value value = box.getValue();
            if (value instanceof Local)
                dest.remove(value);
        }

        // Add gen set
        boxIt = s.getUseBoxes().iterator();
        while (boxIt.hasNext()) {
            ValueBox box = (ValueBox) boxIt.next();
            Value value = box.getValue();
            if (value instanceof Local)
                dest.add(value);
        }
    }

    protected Object entryInitialFlow()
    {
        return new ArraySparseSet();
    }
        
    protected Object newInitialFlow()
    {
        return new ArraySparseSet();
    }

    LiveVariablesAnalysis(DirectedGraph g)
    {
        super(g);

        doAnalysis();
    }
}
