package soot.jimple.toolkits.transaction;
// PTFindTransactions - Analysis to locate transactional regions

import java.util.Iterator;
import java.util.Map;

import soot.*;
import soot.jimple.Stmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.toolkits.pointer.FullObjectSet;
import soot.jimple.toolkits.pointer.SideEffectAnalysis;
import soot.jimple.toolkits.pointer.RWSet;
import soot.jimple.toolkits.pointer.Union;
import soot.jimple.toolkits.pointer.UnionFactory;
import soot.jimple.InvokeStmt;
import soot.jimple.RetStmt;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.BackwardFlowAnalysis;

public class TransactionAnalysis extends BackwardFlowAnalysis
{
    FlowSet emptySet = new ArraySparseSet();
    Map unitToGenerateSet;
    Body body;
	SootMethod method;
    Transaction methodTn;
	SideEffectAnalysis sea;

    TransactionAnalysis(UnitGraph graph, Body b)
	{
		super(graph);
		body = b;
		method = body.getMethod();
		if( G.v().Union_factory == null ) {
		    G.v().Union_factory = new UnionFactory() {
			public Union newUnion() { return FullObjectSet.v(); }
		    };
		}
    	sea = Scene.v().getSideEffectAnalysis();
		sea.findNTRWSets( body.getMethod() );
		methodTn = null;
		if(method.isSynchronized())
		{
			// Entire method is transactional
			methodTn = new Transaction((Stmt) null, false, body.getMethod());
		}
        doAnalysis();
		if(method.isSynchronized() && methodTn != null)
		{
		}
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
		FlowSet ret = (FlowSet) emptySet.clone();
		if(method.isSynchronized() && methodTn != null)
			ret.add(methodTn);
        return ret;
    }

    /**
     * OUT is the same as (IN minus killSet) plus the genSet.
     **/
    protected void flowThrough(Object inValue, Object unit, Object outValue)
    {
        FlowSet
            in = (FlowSet) inValue,
            out = (FlowSet) outValue;

		// If the flowset has a transaction in it with wholeMethod=true and
		// ends is empty then the current instruction is the last instruction on
		// some path of execution in this method, so ends should be set to unit
		if(method.isSynchronized() && in.size() == 1)
		{
			Transaction tn = (Transaction) in.iterator().next();
			if(!tn.ends.isEmpty())
				tn.ends.add(unit);
		}

        // If this instruction is a monitorexit, then 
        //     add a (null,emptylist) to the flowset
        // If there is a (null,anylist) in the flowset, then 
        //     add reads & writes to anylist
        // If there is a (null,anylist) in the flowset and this instruction is 
        //     a monitorenter, change (null, anylist) to (unit, anylist)
        boolean addSelf;
        addSelf = (unit instanceof ExitMonitorStmt);
		// if we're a monitorexit, we might have to add to the flowset

        Iterator inIt = in.iterator();
        while(inIt.hasNext())
        {
            Transaction tn = (Transaction) inIt.next();
            if(tn.begin == null)
            {
            	// Register this unit w/ the current transactional region
            	tn.units.add(unit);
            	
            	// Add our reads and writes to the "current" transactional region
//            	if(unit instanceof InvokeStmt)
//            	{ 
            		// defer calculation of read/write from invoke stmt until
            		// we have compiled a list of all transactions
            		// then use TransactionAwareSideEffectAnalysis
//            		tn.invokes.add(unit);
//            	}
//            	else
//            	{
                	RWSet stmtRead = sea.readSet( method, (Stmt) unit );
                	RWSet stmtWrite = sea.writeSet( method, (Stmt) unit );
            		tn.read.union(stmtRead);
            		tn.write.union(stmtWrite);
//            	}
            	
            	if(unit instanceof EnterMonitorStmt)
            	{
            		// Complete the record for this transaction by adding unit, which is the entry point
            		tn.begin = (Stmt) unit;
            	}
            }
            if(addSelf && tn.ends.contains((Stmt) unit))
            {
            	addSelf = false;
            }
        }
       	in.copy(out);
        if(addSelf)
		{
			out.add(new Transaction((Stmt) unit, false, method));
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
