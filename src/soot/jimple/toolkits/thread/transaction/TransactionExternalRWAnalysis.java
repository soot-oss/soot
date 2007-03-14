package soot.jimple.toolkits.thread.transaction;
// PTFindTransactions - Analysis to locate transactional regions

import java.util.Iterator;
import java.util.Map;
import java.util.List;

import soot.*;
import soot.jimple.Stmt;
import soot.jimple.toolkits.pointer.FullObjectSet;
import soot.jimple.toolkits.pointer.SideEffectAnalysis;
import soot.jimple.toolkits.pointer.RWSet;
import soot.jimple.toolkits.pointer.Union;
import soot.jimple.toolkits.pointer.UnionFactory;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.BackwardFlowAnalysis;

public class TransactionExternalRWAnalysis extends BackwardFlowAnalysis
{
    FlowSet emptySet = new ArraySparseSet();
    Map unitToGenerateSet;
    Body body;
    SideEffectAnalysis sea;
    List tns;

    TransactionExternalRWAnalysis(UnitGraph graph, Body b, List tns)
	{
		super(graph);
		body = b;
		this.tns = tns;
		if( G.v().Union_factory == null ) {
		    G.v().Union_factory = new UnionFactory() {
			public Union newUnion() { return FullObjectSet.v(); }
		    };
		}
    	sea = Scene.v().getSideEffectAnalysis();
		sea.findNTRWSets( body.getMethod() );
        doAnalysis();
	}
    	
    /**
     * All INs are initialized to the empty set.
     **/
    protected Object newInitialFlow()
    {
        return emptySet.clone();
    }

    /**
     * IN(Start) is the empty set
     **/
    protected Object entryInitialFlow()
    {
        return emptySet.clone();
    }

    /**
     * OUT is the same as (IN minus killSet) plus the genSet.
     **/
    protected void flowThrough(Object inValue, Object unit, Object outValue)
    {
        FlowSet
            in = (FlowSet) inValue,
            out = (FlowSet) outValue;

    	RWSet stmtRead = sea.readSet( body.getMethod(), (Stmt) unit );
    	RWSet stmtWrite = sea.writeSet( body.getMethod(), (Stmt) unit );
        
    	Boolean addSelf = Boolean.FALSE;
    	
    	Iterator tnIt = tns.iterator();
    	while(tnIt.hasNext())
    	{
    		Transaction tn = (Transaction) tnIt.next();
    		if(stmtRead.hasNonEmptyIntersection(tn.write) || 
    			stmtWrite.hasNonEmptyIntersection(tn.read) ||
    			stmtWrite.hasNonEmptyIntersection(tn.write))
    			addSelf = Boolean.TRUE;
    	}
    	
        in.copy(out);
    	if(addSelf.booleanValue())
    	{
    		Transaction tn = new Transaction((Stmt) unit, false, body.getMethod(), 0);
    		tn.begin = (Stmt) unit;
    		tn.units.add((Stmt) unit);
    		tn.read.union(stmtRead);
    		tn.write.union(stmtWrite);
    		out.add(tn);
    	}
    }

    /**
     * union, except for transactions in progress.  They get joined
     **/
    protected void merge(Object in1, Object in2, Object out)
    {
        FlowSet
            inSet1 = (FlowSet) ((FlowSet) in1).clone(),
            inSet2 = (FlowSet) ((FlowSet) in2).clone(),
            outSet = (FlowSet) out;
        boolean hasANull1 = false;
        Transaction tn1 = null;
        Iterator inIt1 = inSet1.iterator();
        while(inIt1.hasNext())
        {
            tn1 = (Transaction) inIt1.next();
            if(tn1.begin == null)
            {
            	hasANull1 = true;
            	break;
            }
        }
        
        boolean hasANull2 = false;
        Transaction tn2 = null;
        Iterator inIt2 = inSet2.iterator();
        while(inIt2.hasNext())
        {
            tn2 = (Transaction) inIt2.next();
            if(tn2.begin == null)
            {
            	hasANull2 = true;
            	break;
            }
        }
        if(hasANull1 && hasANull2)
        {
        	inSet1.remove(tn1);
        	Iterator itends = tn1.ends.iterator();
        	while(itends.hasNext())
        	{
        		Stmt stmt = (Stmt) itends.next();
        		if(!tn2.ends.contains(stmt))
        			tn2.ends.add(stmt);
        	}
        	tn2.read.union(tn1.read);
        	tn2.write.union(tn1.write);
        }
        inSet1.union(inSet2, outSet);
    }

    protected void copy(Object source, Object dest)
    {
        FlowSet
            sourceSet = (FlowSet) source,
            destSet = (FlowSet) dest;

        sourceSet.copy(destSet);
    }
}
