package soot.jimple.toolkits.transaction;

import java.util.*;
import soot.*;
import soot.util.Chain;
import soot.jimple.Stmt;
import soot.jimple.toolkits.pointer.RWSet;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.ExceptionalUnitGraph;

public class TransactionTransformer extends SceneTransformer
{
    public TransactionTransformer(Singletons.Global g){}
    public static TransactionTransformer v() 
	{ 
		return G.v().soot_jimple_toolkits_transaction_TransactionTransformer();
	}

    protected void internalTransform(String phaseName, Map options)
	{
		G.v().out.println("TransactionTransformer");
//    	CallGraph cg = Scene.v().getCallGraph();
    	Map methodToFlowSet = new HashMap();

    	// For all methods, run the intraprocedural analysis (transaction finder)
    	Iterator runAnalysisClassesIt = Scene.v().getApplicationClasses().iterator();
    	while (runAnalysisClassesIt.hasNext()) 
    	{
    	    SootClass appClass = (SootClass) runAnalysisClassesIt.next();
    	    Iterator methodsIt = appClass.getMethods().iterator();
    	    while (methodsIt.hasNext())
    	    {
    	    	SootMethod method = (SootMethod) methodsIt.next();
//    	    	G.v().out.println(method.toString());
				if(method.isConcrete())
				{
	    	    	Body b = method.retrieveActiveBody();
    		    	
    	    		// run the interprocedural analysis
    				TransactionAnalysis ptft = new TransactionAnalysis(new ExceptionalUnitGraph(b), b);
    				Chain units = b.getUnits();
    				Unit firstUnit = (Unit) units.iterator().next();
    				FlowSet fs = (FlowSet) ptft.getFlowBefore(firstUnit);
    			
    				// add the results to the list of results
    				methodToFlowSet.put(method, fs);
				}
    	    }
    	}
    	
    	// Create a composite list of all transactions
    	List AllTransactions = new Vector();
    	Collection AllFlowSets = methodToFlowSet.values();
    	Iterator fsIt = AllFlowSets.iterator();
    	while(fsIt.hasNext())
    	{
    		FlowSet fs = (FlowSet) fsIt.next();
    		AllTransactions.addAll(fs.toList());
    	}
    	
    	// Give each method a unique, deterministic identifier (based on an alphabetical sort by method name)
    	List methodNamesTemp = new ArrayList();
    	Iterator appClassesIt = Scene.v().getApplicationClasses().iterator();
    	while (appClassesIt.hasNext()) 
    	{
    	    SootClass appClass = (SootClass) appClassesIt.next();
    	    Iterator methodsIt = appClass.getMethods().iterator();
    	    while (methodsIt.hasNext())
    	    {
    	    	SootMethod method = (SootMethod) methodsIt.next();
    	    	methodNamesTemp.add(appClass.getName() + "." + method.getName());
    	    }
		}
		String methodNames[] = new String[1];
		methodNames = (String []) methodNamesTemp.toArray(methodNames);
		Arrays.sort(methodNames);

		// Identify each transaction with its method's identifier
    	Iterator tnIt0 = AllTransactions.iterator();
    	while(tnIt0.hasNext())
    	{
    		Transaction tn1 = (Transaction) tnIt0.next();
    		tn1.name = "m" + Arrays.binarySearch(methodNames, tn1.method.getDeclaringClass().getName() + "." + tn1.method.getName());
    	}
    	
    	// Complete the read/write set of each transaction using the list
    	// to create a TransactionAwareSideEffectAnalysis
    	// This breaks atomicity: inner transaction's effects are visible
    	// as soon as the inner transaction completes.
//    	TransactionAwareSideEffectAnalysis tasea = 
//    		new TransactionAwareSideEffectAnalysis(Scene.v().getPointsToAnalysis(), 
//    				Scene.v().getCallGraph(), AllTransactions);
//    	Iterator tnIt = AllTransactions.iterator();
//    	while(tnIt.hasNext())
//    	{
//    		Transaction tn = (Transaction) tnIt.next();
//    		Iterator invokeIt = tn.invokes.iterator();
//    		while(invokeIt.hasNext())
//    		{
//    			Stmt stmt = (Stmt) invokeIt.next();
//            	RWSet stmtRead = tasea.readSet( tn.method, stmt );
//            	RWSet stmtWrite = tasea.writeSet( tn.method, stmt );
//        		tn.read.union(stmtRead);
//        		tn.write.union(stmtWrite);
//    		}
//    	}
    	
    	// *** Calculate locking scheme ***
    	
    	// (1) add external data races as one-line transactions
    	// note that finding them isn't that hard (though it is time consuming)
    	// however, actually adding entermonitor & exitmonitor statements is rather complex... must deal with exception handling!
    	// For all methods, run the intraprocedural analysis (transaction finder)
/*    	Map methodToStrayRWSet = new HashMap();
    	Iterator runRWFinderClassesIt = Scene.v().getApplicationClasses().iterator();
    	while (runRWFinderClassesIt.hasNext()) 
    	{
    	    SootClass appClass = (SootClass) runRWFinderClassesIt.next();
    	    Iterator methodsIt = appClass.getMethods().iterator();
    	    while (methodsIt.hasNext())
    	    {
    	    	SootMethod method = (SootMethod) methodsIt.next();
//    	    	G.v().out.println(method.toString());
    	    	Body b = method.retrieveActiveBody();
    	    	
    	    	// run the interprocedural analysis
    			PTFindStrayRW ptfrw = new PTFindStrayRW(new ExceptionalUnitGraph(b), b, AllTransactions);
    			Chain units = b.getUnits();
    			Unit firstUnit = (Unit) units.iterator().next();
    			FlowSet fs = (FlowSet) ptfrw.getFlowBefore(firstUnit);
    			
    			// add the results to the list of results
    			methodToStrayRWSet.put(method, fs);
    	    }
    	}
*/    	
    	
    	// (2) Search for data dependencies between transactions, and split them into disjoint sets
    	int nextGroup = 1;
    	Iterator tnIt1 = AllTransactions.iterator();
    	while(tnIt1.hasNext())
    	{
    		Transaction tn1 = (Transaction) tnIt1.next();
    		
    		// if this transaction has somehow already been marked for deletion
    		if(tn1.setNumber == -1)
    			continue;
    		
    		// if this transaction is empty
    		if(tn1.read.size() == 0 && tn1.write.size() == 0)
    		{
    			// this transaction has no effect except on locals... we don't need it!
    			tn1.setNumber = -1; // AKA delete the transactional region (but don't really so long as we are using
    								// the synchronized keyword in our language... because java guarantees memory
    								// barriers at certain points in synchronized blocks)
    		}
    		else
    		{
	        	Iterator tnIt2 = AllTransactions.iterator();
	    		while(tnIt2.hasNext())
	    		{
	    			Transaction tn2 = (Transaction) tnIt2.next();
	    			
	    			// check if it's us!
	    			if(tn1 == tn2)
	    				continue;
	    			
	    			// check if this transactional region is going to be deleted
	    			if(tn2.setNumber == -1)
	    				continue;

	    			// check if they're already marked as having an interference
//	    			if(tn1.setNumber > 0 && tn1.setNumber == tn2.setNumber)
//	    				continue;
	    			
	    			// check if these two transactions can't ever be in parallel
	    			if(!mightBeInParallel(tn1, tn2))
	    				continue;

	    			// check for RW or WW data dependencies.
	    			if(tn1.write.hasNonEmptyIntersection(tn2.write) ||
	    					tn1.write.hasNonEmptyIntersection(tn2.read) ||
	    					tn1.read.hasNonEmptyIntersection(tn2.write))
	    			{
	    				// Determine the size of the intersection for GraphViz output
	    				int size = 0;
	    				if(tn1.write.hasNonEmptyIntersection(tn2.write))
	    					size += tn1.write.intersection(tn2.write).size();
	    				if(tn1.write.hasNonEmptyIntersection(tn2.read))
	    					size += tn1.write.intersection(tn2.read).size();
	    				if(tn1.read.hasNonEmptyIntersection(tn2.write))
	    					size += tn1.read.intersection(tn2.write).size();
	    				
	    				// Record this 
	    				tn1.edges.add(new DataDependency(tn2, size));
	    				tn2.edges.add(new DataDependency(tn1, size));
	    				
	    				// if tn1 already is in a group
	    				if(tn1.setNumber > 0)
	    				{
	    					// if tn2 is NOT already in a group
	    					if(tn2.setNumber == 0)
	    					{
	    						tn2.setNumber = tn1.setNumber;
	    					}
	    					// if tn2 is already in a group
	    					else if(tn2.setNumber > 0)
	    					{
		    					int setToDelete = tn2.setNumber;
		    					int setToExpand = tn1.setNumber;
			    	        	Iterator tnIt3 = AllTransactions.iterator();
			    	    		while(tnIt3.hasNext())
			    	    		{
			    	    			Transaction tn3 = (Transaction) tnIt3.next();
			    	    			if(tn3.setNumber == setToDelete)
			    	    			{
			    	    				tn3.setNumber = setToExpand;
			    	    			}
			    	    		}	    						
	    					}
	    				}
	    				// if tn1 is NOT already in a group
	    				else if(tn1.setNumber == 0)
	    				{
	    					// if tn2 is NOT already in a group
	    					if(tn2.setNumber == 0)
		    				{
		    					tn1.setNumber = tn2.setNumber = nextGroup;
		    					nextGroup++;
		    				}
	    					// if tn2 is already in a group
	    					else if(tn2.setNumber > 0)
	    					{
	    						tn1.setNumber = tn2.setNumber;
	    					}
	    				}
	    			}
	    		}
	    		// If, after comparing to all other transactions, we have no group:
	    		if(tn1.setNumber == 0)
	    		{
	    			if(true) //mightBeInParallel(tn1, tn1))
	    			{
	    				tn1.setNumber = nextGroup;
	    				nextGroup++;
	    			}
	    			else
	    			{
//	    				tn1.setNumber = -1; // delete transactional region
	    			}
	    		}	    			
    		}
    	}
    	    	
    	// For all methods, run the transformer (Pessimistic Transaction Tranformation)
		// Print output for graphviz package
		G.v().out.println("[transaction] strict graph transactions {\nstart=1;");
    	// BEGIN AWFUL HACK
		TransactionBodyTransformer.addedGlobalLockObj = new boolean[nextGroup];
		for(int i = 0; i < nextGroup; i++)
			TransactionBodyTransformer.addedGlobalLockObj[i] = false;
		// END AWFUL HACK
    	Iterator doTransformClassesIt = Scene.v().getApplicationClasses().iterator();
    	while (doTransformClassesIt.hasNext()) 
    	{
    	    SootClass appClass = (SootClass) doTransformClassesIt.next();
    	    Iterator methodsIt = appClass.getMethods().iterator();
    	    while (methodsIt.hasNext())
    	    {
    	    	SootMethod method = (SootMethod) methodsIt.next();
				if(method.isConcrete())
				{
	    	    	Body b = method.getActiveBody();
    	    	
    		    	FlowSet fs = (FlowSet) methodToFlowSet.get(method);
    	    	
   	    			TransactionBodyTransformer.v().setDetails(fs, nextGroup);
   	    			TransactionBodyTransformer.v().internalTransform(b,phaseName, options); 
				}
    	    }
    	}
		// Print output for graphviz package
		G.v().out.println("[transaction] }");
	}
    
    public static boolean mightBeInParallel(Transaction tn1, Transaction tn2)
    {
    	String Thread2Fcns[] = {"rotary.Driver.run", "rotary.Driver.Think", "rotary.Car.getLocation", "rotary.RoadSegment.getCars", 								"rotary.RoadSegment.getPhysicalLocation", "rotary.CollisionDetector.addCollision", 									"rotary.Car.destroy", "rotary.RoadSegment.removeCar", "rotary.StateActionHistory.add",
    							"rotary.ReinforcementLearner.registerHistory", "rotary.StateActionHistory.size", "rotary.StateActionHistory.get", 
    							"rotary.DriverValueFunction.get", "rotary.DriverValueFunction.set"
    							};
    							
    	String Thread3Fcns[] = {"rotary.Car.run", "rotary.Car.Update", "rotary.Driver.getAcceleration", "rotary.RoadSegment.removeCar", 
    							"rotary.RoadSegment.addCar", "rotary.Rotary.removeCar"
    							};

//		G.v().out.println(tn1.method.getDeclaringClass().getName() + "." + tn1.method.getName());

		return true;
/*
    	if(contains(Thread2Fcns, tn1.method.getDeclaringClass().getName() + "." + tn1.method.getName()))
    	{
    		if(contains(Thread3Fcns, tn2.method.getDeclaringClass().getName() + "." + tn2.method.getName()))
    		{
    			return true;
    		}
    		else if(contains(Thread2Fcns, tn2.method.getDeclaringClass().getName() + "." + tn2.method.getName()))
    		{
    			return false; // return true for naive MHP (assumes multiples of all threads)
    		}
    	}
    	else if(contains(Thread3Fcns, tn1.method.getDeclaringClass().getName() + "." + tn1.method.getName()))
    	{
    		if(contains(Thread2Fcns, tn2.method.getDeclaringClass().getName() + "." + tn2.method.getName()))
    		{
    			return true;
    		}
    		else if(contains(Thread3Fcns, tn2.method.getDeclaringClass().getName() + "." + tn2.method.getName()))
    		{
    			return false; // return true for naive MHP (assumes multiples of all threads)
    		}
    	}
    	return false;
*/
    }
    
    public static boolean contains(String strings[], String string)
    {
    	boolean retval = false;
    	for(int i = 0; i < strings.length; i++)
    	{
    		if(strings[i].equals(string))
    			retval = true;
    	}
    	return retval;
    }
}
