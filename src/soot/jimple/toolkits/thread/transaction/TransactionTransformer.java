package soot.jimple.toolkits.thread.transaction;

import java.util.*;

import soot.*;
import soot.util.Chain;
import soot.jimple.*;
import soot.jimple.toolkits.pointer.*;
import soot.jimple.toolkits.scalar.CommonPrecedingEqualValueAnalysis;
import soot.jimple.toolkits.scalar.EqualUsesAnalysis;
import soot.jimple.toolkits.thread.ThreadLocalObjectsAnalysis;
import soot.jimple.toolkits.thread.mhp.UnsynchronizedMhpAnalysis;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.infoflow.ClassInfoFlowAnalysis;
import soot.jimple.toolkits.infoflow.InfoFlowAnalysis;
import soot.jimple.toolkits.infoflow.SmartMethodInfoFlowAnalysis;
import soot.jimple.toolkits.infoflow.SmartMethodLocalObjectsAnalysis;
import soot.jimple.spark.pag.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.tagkit.LineNumberTag;
import soot.jimple.toolkits.annotation.nullcheck.*;

public class TransactionTransformer extends SceneTransformer
{
    public TransactionTransformer(Singletons.Global g){}
    public static TransactionTransformer v() 
	{ 
		return G.v().soot_jimple_toolkits_transaction_TransactionTransformer();
	}
	
	// Lock options
	boolean optionOneGlobalLock = false;
	boolean optionStaticLocks = false;
	boolean optionUseLocksets = false;
	boolean optionLeaveOriginalLocks = false;
	boolean optionIncludeEmptyPossibleEdges = false;
	
	// Semantic options
	boolean optionAvoidDeadlock = true;
	boolean optionOpenNesting = true;	
	
	// Analysis options
	boolean optionDoMHP = false;
	boolean optionDoTLO = false;
	
	// Output options
	boolean optionPrintMhpSummary = true; // not a CLI option yet
	boolean optionPrintGraph = false;
	boolean optionPrintTable = false;
	boolean optionPrintDebug = false;
	
	UnsynchronizedMhpAnalysis mhp;

    protected void internalTransform(String phaseName, Map options)
	{
		// Get phase options

		String lockingScheme = PhaseOptions.getString( options, "locking-scheme" );
		if(lockingScheme.equals("fine-grained"))
		{
			optionOneGlobalLock = false;
			optionStaticLocks = false;
			optionUseLocksets = true;
			optionLeaveOriginalLocks = false;
		}
//		if(lockingScheme.equals("fine-static"))
//		{
//			optionOneGlobalLock = false;
//			optionStaticLocks = true;
//			optionUseLocksets = true;
//			optionLeaveOriginalLocks = false;
//		}
		if(lockingScheme.equals("medium-grained")) // rename to coarse-grained
		{
			optionOneGlobalLock = false;
			optionStaticLocks = false;
			optionUseLocksets = false;
			optionLeaveOriginalLocks = false;
		}
		if(lockingScheme.equals("coarse-grained")) // rename to coarse-static
		{
			optionOneGlobalLock = false;
			optionStaticLocks = true;
			optionUseLocksets = false;
			optionLeaveOriginalLocks = false;
		}
		if(lockingScheme.equals("single-static"))
		{
			optionOneGlobalLock = true;
			optionStaticLocks = true;
			optionLeaveOriginalLocks = false;
		}
		if(lockingScheme.equals("leave-original"))
		{
			optionOneGlobalLock = false;
			optionStaticLocks = false;
			optionLeaveOriginalLocks = true;
			optionIncludeEmptyPossibleEdges = false;
		}
		
		optionAvoidDeadlock = PhaseOptions.getBoolean( options, "avoid-deadlock" );
		optionOpenNesting = PhaseOptions.getBoolean( options, "open-nesting" );

		optionDoMHP = PhaseOptions.getBoolean( options, "do-mhp" );
		optionDoTLO = PhaseOptions.getBoolean( options, "do-tlo" );

		optionPrintGraph = PhaseOptions.getBoolean( options, "print-graph" );
		optionPrintTable = PhaseOptions.getBoolean( options, "print-table" );
		optionPrintDebug = PhaseOptions.getBoolean( options, "print-debug" );
		
		
		
		// *** Build May Happen In Parallel Info ***
		if(optionDoMHP && Scene.v().getPointsToAnalysis() instanceof PAG)
		{
	    	G.v().out.println("[wjtp.tn] *** Build May-Happen-in-Parallel Info *** " + (new Date()));
			mhp = new UnsynchronizedMhpAnalysis();
			if(optionPrintMhpSummary)
			{
				mhp.printMhpSummary();
			}
		}
		else
		{
			mhp = null;
		}
		


		// *** Find Thread-Local Objects ***
		ThreadLocalObjectsAnalysis tlo = null;
    	if(optionDoTLO)
    	{
	    	G.v().out.println("[wjtp.tn] *** Find Thread-Local Objects *** " + (new Date()));
	    	if(mhp != null)
	    		tlo = new ThreadLocalObjectsAnalysis(mhp); // can tell only that a field/local is local to the object it's being accessed in
			else
	    		tlo = new ThreadLocalObjectsAnalysis(new UnsynchronizedMhpAnalysis()); // can tell only that a field/local is local to the object it's being accessed in
    	}



    	// *** Find and Name Transactions ***
    	// The transaction finder finds the start, end, and preparatory statements
    	// for each transaction. It also calculates the non-transitive read/write 
    	// sets for each transaction.
    	// For all methods, run the intraprocedural analysis (TransactionAnalysis)
		Date start = new Date();
    	G.v().out.println("[wjtp.tn] *** Find and Name Transactions *** " + start);
    	Map methodToFlowSet = new HashMap();
    	Map methodToExcUnitGraph = new HashMap();
    	Iterator runAnalysisClassesIt = Scene.v().getApplicationClasses().iterator();
    	while (runAnalysisClassesIt.hasNext()) 
    	{
    	    SootClass appClass = (SootClass) runAnalysisClassesIt.next();
    	    Iterator methodsIt = appClass.getMethods().iterator();
    	    while (methodsIt.hasNext())
    	    {
    	    	SootMethod method = (SootMethod) methodsIt.next();
				if(method.isConcrete())
				{
	    	    	Body b = method.retrieveActiveBody();
	    	    	ExceptionalUnitGraph eug = new ExceptionalUnitGraph(b);
    		    	methodToExcUnitGraph.put(method, eug);
    		    	
    	    		// run the intraprocedural analysis
    				TransactionAnalysis ta = new TransactionAnalysis(eug, b, optionPrintDebug, tlo);
    				Chain units = b.getUnits();
    				Unit lastUnit = (Unit) units.getLast();
    				FlowSet fs = (FlowSet) ta.getFlowBefore(lastUnit);
    			
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
    		List fList = fs.toList();
    		for(int i = 0; i < fList.size(); i++)
    			AllTransactions.add(((TransactionFlowPair) fList.get(i)).tn);
    	}

		// Assign Names To Transactions
		assignNamesToTransactions(AllTransactions);

    	G.v().out.println("[wjtp.tn] Total Simple Method Data Flow Analyses: " + ClassInfoFlowAnalysis.methodCount);
    	G.v().out.println("[wjtp.tn] Total Smart Method Data Flow Analyses: " + SmartMethodInfoFlowAnalysis.counter);
    	G.v().out.println("[wjtp.tn] Total Method Local Objects Analyses: " + SmartMethodLocalObjectsAnalysis.counter);

    	

    	// *** Find Transitive Read/Write Sets ***
    	// Finds the transitive read/write set for each transaction using a given
    	// nesting model.
    	G.v().out.println("[wjtp.tn] *** Find Transitive Read/Write Sets *** " + (new Date()));
    	PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
    	TransactionAwareSideEffectAnalysis tasea = null;
    	if(optionOpenNesting) // TODO: NOT COMPLETE.  Must have open/closed switch in TransactionAnalysis as well.
    	{
    		tasea = new TransactionAwareSideEffectAnalysis(
    					pta, 
    					Scene.v().getCallGraph(), AllTransactions, tlo);
    	}
    	else
    	{
    		tasea = new TransactionAwareSideEffectAnalysis(
    					pta, 
    					Scene.v().getCallGraph(), null, tlo);
    	}
    	Iterator tnIt = AllTransactions.iterator();
    	while(tnIt.hasNext())
    	{
    		Transaction tn = (Transaction) tnIt.next();
//			Body b = tn.method.retrieveActiveBody();
//			UnitGraph g = new ExceptionalUnitGraph(b);
			UnitGraph g = (UnitGraph) methodToExcUnitGraph.get(tn.method);
			LocalDefs sld = new SmartLocalDefs(g, new SimpleLiveLocals(g));
    		Iterator invokeIt = tn.invokes.iterator();
    		while(invokeIt.hasNext())
    		{
    			Stmt stmt = (Stmt) invokeIt.next();
    			
    			HashSet uses = new HashSet();
    			RWSet stmtRead = tasea.readSet(tn.method, stmt, tn, sld, uses);
    			if(stmtRead != null)
	    			tn.read.union(stmtRead);
    			
    			RWSet stmtWrite = tasea.writeSet(tn.method, stmt, tn, sld, uses);
				if(stmtWrite != null)
					tn.write.union(stmtWrite);
					
				// memory hog???
				CodeBlockRWSet bothRW = new CodeBlockRWSet();
				bothRW.union(stmtRead);
				bothRW.union(stmtWrite);
       			tn.unitToRWSet.put(stmt, bothRW);

				List usesList;
				if(tn.unitToUses.containsKey(stmt))
					usesList = (List) tn.unitToUses.get(stmt);
				else
				{
					usesList = new ArrayList();
					tn.unitToUses.put(stmt, usesList);
				}

				for(Iterator usesIt = uses.iterator(); usesIt.hasNext(); )
				{
					Object use = usesIt.next();
					if(!usesList.contains(use))
						usesList.add(use);
				}
			}
    	}
    	long longTime = ((new Date()).getTime() - start.getTime()) / 100;
    	float time = ((float) longTime) / 10.0f;
		G.v().out.println("[wjtp.tn] Total Time for TLO Steps: " + time + "s");
    	G.v().out.println("[wjtp.tn] Total Simple Method Data Flow Analyses: " + ClassInfoFlowAnalysis.methodCount);
    	G.v().out.println("[wjtp.tn] Total Smart Method Data Flow Analyses: " + SmartMethodInfoFlowAnalysis.counter);
    	G.v().out.println("[wjtp.tn] Total Method Local Objects Analyses: " + SmartMethodLocalObjectsAnalysis.counter);



    	// *** Find Stray Reads/Writes *** (DISABLED)
    	// add external data races as one-line transactions
    	// note that finding them isn't that hard (though it is time consuming)
    	// For all methods, run the intraprocedural analysis (transaction finder)
    	// Note that these will only be transformed if they are either added to
    	// methodToFlowSet or if a loop and new body transformer are used for methodToStrayRWSet
/*    	Map methodToStrayRWSet = new HashMap();
    	Iterator runRWFinderClassesIt = Scene.v().getApplicationClasses().iterator();
    	while (runRWFinderClassesIt.hasNext()) 
    	{
    	    SootClass appClass = (SootClass) runRWFinderClassesIt.next();
    	    Iterator methodsIt = appClass.getMethods().iterator();
    	    while (methodsIt.hasNext())
    	    {
    	    	SootMethod method = (SootMethod) methodsIt.next();
    	    	Body b = method.retrieveActiveBody();
				UnitGraph g = (UnitGraph) methodToExcUnitGraph.get(method);
    	    	
    	    	// run the interprocedural analysis
//    			PTFindStrayRW ptfrw = new PTFindStrayRW(new ExceptionalUnitGraph(b), b, AllTransactions);
    			PTFindStrayRW ptfrw = new PTFindStrayRW(g, b, AllTransactions);
    			Chain units = b.getUnits();
    			Unit firstUnit = (Unit) units.iterator().next();
    			FlowSet fs = (FlowSet) ptfrw.getFlowBefore(firstUnit);
    			
    			// add the results to the list of results
    			methodToStrayRWSet.put(method, fs);
    	    }
    	}
//*/    	
		


    	// *** Calculate Locking Groups ***
    	// Search for data dependencies between transactions, and split them into disjoint sets
    	G.v().out.println("[wjtp.tn] *** Calculate Locking Groups *** " + (new Date()));
    	int nextGroup = 1;
    	List groups = new ArrayList();
    	groups.add(new TransactionGroup(0)); // dummy group
    	if(optionOneGlobalLock) // use one group for all transactions
    	{
    		TransactionGroup onlyGroup = new TransactionGroup(nextGroup);
	    	Iterator tnIt1 = AllTransactions.iterator();
	    	while(tnIt1.hasNext())
	    	{
	    		Transaction tn1 = (Transaction) tnIt1.next();
	    		onlyGroup.add(tn1);
    		}
    		nextGroup++;
    		groups.add(onlyGroup);
    	}
    	else // calculate separate groups for transactions
    	{
	    	Iterator tnIt1 = AllTransactions.iterator();
	    	while(tnIt1.hasNext())
	    	{
	    		Transaction tn1 = (Transaction) tnIt1.next();
	    		
	    		// if this transaction has somehow already been marked for deletion
	    		if(tn1.setNumber == -1)
	    			continue;
	    		
	    		// if this transaction is empty
	    		if(tn1.read.size() == 0 && tn1.write.size() == 0 && !optionLeaveOriginalLocks)
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
		    				    			
		    			// check if this transactional region is going to be deleted
		    			if(tn2.setNumber == -1)
		    				continue;

		    			// check if they're already marked as having an interference
		    			// NOTE: this results in a sound grouping, but a badly 
		    			//       incomplete dependency graph. If the dependency 
		    			//       graph is to be analyzed, we cannot do this
	//	    			if(tn1.setNumber > 0 && tn1.setNumber == tn2.setNumber)
	//	    				continue;
		    			
		    			// check if these two transactions can't ever be in parallel
		    			if(!mayHappenInParallel(tn1, tn2))
		    				continue;

		    			// check for RW or WW data dependencies.
		    			// or, for optionLeaveOriginalLocks, check type compatibility
		    			SootClass classOne = null;
		    			SootClass classTwo = null;
		    			boolean typeCompatible = false;
		    			boolean emptyEdge = false;
		    			if(tn1.origLock != null && tn2.origLock != null)
		    			{
							// Check if edge is empty
	    					if(tn1.origLock == null || tn2.origLock == null)
	    						emptyEdge = true;
	    					else if(!(tn1.origLock instanceof Local) || !(tn2.origLock instanceof Local))
	    						emptyEdge = !tn1.origLock.equals(tn2.origLock);
	    					else
		    					emptyEdge = !pta.reachingObjects((Local) tn1.origLock).hasNonEmptyIntersection(pta.reachingObjects((Local) tn2.origLock));

							// Check if types are compatible
			    			RefLikeType typeOne = (RefLikeType) tn1.origLock.getType();
			    			RefLikeType typeTwo = (RefLikeType) tn2.origLock.getType();
			    			classOne = (typeOne instanceof RefType) ? ((RefType) typeOne).getSootClass() : null;
			    			classTwo = (typeTwo instanceof RefType) ? ((RefType) typeTwo).getSootClass() : null;
			    			if(classOne != null && classTwo != null)
			    			{
				    			Hierarchy h = Scene.v().getActiveHierarchy();
				    			if(classOne.isInterface())
				    			{
				    				if(classTwo.isInterface())
				    				{
				    					typeCompatible = 
				    						h.getSubinterfacesOfIncluding(classOne).contains(classTwo) ||
				    						h.getSubinterfacesOfIncluding(classTwo).contains(classOne);
				    				}
				    				else
				    				{
				    					typeCompatible = 
				    						h.getImplementersOf(classOne).contains(classTwo);
				    				}
				    			}
				    			else
				    			{
				    				if(classTwo.isInterface())
				    				{
				    					typeCompatible =
				    						h.getImplementersOf(classTwo).contains(classOne);
				    				}
				    				else
				    				{
						    			typeCompatible = 
						    				(classOne != null && Scene.v().getActiveHierarchy().getSubclassesOfIncluding(classOne).contains(classTwo) ||
					    				 	 classTwo != null && Scene.v().getActiveHierarchy().getSubclassesOfIncluding(classTwo).contains(classOne));
					    			}
					    		}
					    	}
			    		}
		    			if((!optionLeaveOriginalLocks && 
		    				   (tn1.write.hasNonEmptyIntersection(tn2.write) ||
		    					tn1.write.hasNonEmptyIntersection(tn2.read) ||
		    					tn1.read.hasNonEmptyIntersection(tn2.write))	) || 
		    			   ( optionLeaveOriginalLocks && typeCompatible && (optionIncludeEmptyPossibleEdges || !emptyEdge) ))
		    			{
		    				// Determine the size of the intersection for GraphViz output
		    				CodeBlockRWSet rw = null;
		    				int size;
		    				if(optionLeaveOriginalLocks)
		    				{
		    					rw = new CodeBlockRWSet();
		    					size = emptyEdge ? 0 : 1;
		    				}
		    				else
		    				{
			    				rw = tn1.write.intersection(tn2.write);
			    				rw.union(tn1.write.intersection(tn2.read));
			    				rw.union(tn1.read.intersection(tn2.write));
			    				size = rw.size();
			    			}			    			
		    				
		    				// Record this 
		    				tn1.edges.add(new TransactionDataDependency(tn2, size, rw));
	                        // Don't add opposite... all n^2 pairs will be visited separately
		    				
		    				if(size > 0)
		    				{
			    				// if tn1 already is in a group
			    				if(tn1.setNumber > 0)
			    				{
			    					// if tn2 is NOT already in a group
			    					if(tn2.setNumber == 0)
			    					{
			    						tn1.group.add(tn2);
			    					}
			    					// if tn2 is already in a group
			    					else if(tn2.setNumber > 0)
			    					{
			    						if(tn1.setNumber != tn2.setNumber) // if they are equal, then they are already in the same group!
			    						{
			    							tn1.group.mergeGroups(tn2.group);
						    	    	}
			    					}
			    				}
			    				// if tn1 is NOT already in a group
			    				else if(tn1.setNumber == 0)
			    				{
			    					// if tn2 is NOT already in a group
			    					if(tn2.setNumber == 0)
		 		    				{
		 		    					TransactionGroup newGroup = new TransactionGroup(nextGroup);
										newGroup.add(tn1);
										newGroup.add(tn2);
										groups.add(newGroup);
				    					nextGroup++;
				    				}
			    					// if tn2 is already in a group
			    					else if(tn2.setNumber > 0)
			    					{
			    						tn2.group.add(tn1);
			    					}
			    				}
			    			}
		    			}
		    		}
		    		// If, after comparing to all other transactions, we have no group:
		    		if(tn1.setNumber == 0)
		    		{
	    				tn1.setNumber = -1; // delete transactional region
		    		}	    			
	    		}
	    	}
    	}
    	
    	
    	
		// *** Detect the Possibility of Deadlock ***
		MutableDirectedGraph lockOrder;
		TransitiveTargets tt = new TransitiveTargets(Scene.v().getCallGraph(), new Filter(new TransactionVisibleEdgesPred(null)));
		boolean foundDeadlock;
		do
		{
			G.v().out.println("[wjtp.tn] *** Detect the Possibility of Deadlock *** " + (new Date()));
			foundDeadlock = false;
	    	lockOrder = new HashMutableDirectedGraph(); // start each iteration with a fresh graph
			
			// Assemble the partial ordering of locks
	    	Iterator deadlockIt1 = AllTransactions.iterator();
	    	while(deadlockIt1.hasNext() && !foundDeadlock)
	    	{
	    		Transaction tn1 = (Transaction) deadlockIt1.next();
	    		
	    		// skip if unlocked
	    		if( tn1.setNumber <= 0 )
	    			continue;
	    			
	    		// add a node for this set
	    		if( !lockOrder.containsNode(tn1.group) )
	    		{
	    			lockOrder.addNode(tn1.group);
	    		}
	    			
	    		// Get list of tn1's target methods
	    		if(tn1.transitiveTargets == null)
	    		{
		    		tn1.transitiveTargets = new HashSet();
		    		Iterator tn1InvokeIt = tn1.invokes.iterator();
		    		while(tn1InvokeIt.hasNext())
		    		{
		    			Unit tn1Invoke = (Unit) tn1InvokeIt.next();
		    			Iterator targetIt = tt.iterator(tn1Invoke);
		    			while(targetIt.hasNext())
		    				tn1.transitiveTargets.add(targetIt.next());
		    		}
		    	}
	    		
	    		// compare to each other tn
	    		Iterator deadlockIt2 = AllTransactions.iterator();
	    		while(deadlockIt2.hasNext() && !foundDeadlock)
	    		{
	    			Transaction tn2 = (Transaction) deadlockIt2.next();
	    			
	    			// skip if unlocked or in same set as tn1
	    			if( tn2.setNumber <= 0 || tn2.setNumber == tn1.setNumber )
	    				continue;
	    				
	    			// add a node for this set
		    		if( !lockOrder.containsNode(tn2.group) )
		    		{
		    			lockOrder.addNode(tn2.group);
		    		}	
		    			
		    		if( tn1.transitiveTargets.contains(tn2.method) && !foundDeadlock )
		    		{
		    			// This implies the partial ordering tn1lock before tn2lock
		    			if(optionPrintDebug)
		    			{
			    			G.v().out.println("group" + (tn1.setNumber) + " before group" + (tn2.setNumber) + ": " +
			    				"outer: " + tn1.name + " inner: " + tn2.name);
			    		}
		    			
		    			// Check if tn2lock before tn1lock is in our lock order
		    			List afterTn2 = new ArrayList();
		    			afterTn2.addAll( lockOrder.getSuccsOf(tn2.group) );
		    			for( int i = 0; i < afterTn2.size(); i++ )
		    				afterTn2.addAll( lockOrder.getSuccsOf(afterTn2.get(i)) );
		    				
		    			if( afterTn2.contains(tn1.group) )
		    			{
		    				if(!optionAvoidDeadlock)
		    				{
			    				G.v().out.println("[wjtp.tn] DEADLOCK HAS BEEN DETECTED: not correcting");
								foundDeadlock = true;
			    			}
			    			else
			    			{
			    				G.v().out.println("[wjtp.tn] DEADLOCK HAS BEEN DETECTED: merging group" +
			    					(tn1.setNumber) + " and group" + (tn2.setNumber) +
			    					" and restarting deadlock detection");
    					
								if(optionPrintDebug)
								{
									G.v().out.println("tn1.setNumber was " + tn1.setNumber + " and tn2.setNumber was " + tn2.setNumber);
									G.v().out.println("tn1.group.size was " + tn1.group.transactions.size() +
										" and tn2.group.size was " + tn2.group.transactions.size());
									G.v().out.println("tn1.group.num was  " + tn1.group.num() + " and tn2.group.num was  " + tn2.group.num());
								}
								tn1.group.mergeGroups(tn2.group);
								if(optionPrintDebug)
								{
									G.v().out.println("tn1.setNumber is  " + tn1.setNumber + " and tn2.setNumber is  " + tn2.setNumber);
									G.v().out.println("tn1.group.size is  " + tn1.group.transactions.size() +
										" and tn2.group.size is  " + tn2.group.transactions.size());
								}
								
								foundDeadlock = true;
			    			}
		    			}
		    			
		    			lockOrder.addEdge(tn1.group, tn2.group);
		    		}
	    		}
	    	}
		} while(foundDeadlock && optionAvoidDeadlock);


		// *** Calculate Locking Objects ***
    	// Get a list of all dependencies for each group
    	G.v().out.println("[wjtp.tn] *** Calculate Locking Objects *** " + (new Date()));
    	RWSet rws[] = new CodeBlockRWSet[nextGroup - 1];
    	for(int group = 0; group < nextGroup - 1; group++)
    		rws[group] = new CodeBlockRWSet();
		if(!optionStaticLocks)
		{
	    	Iterator tnIt8 = AllTransactions.iterator();
	    	while(tnIt8.hasNext())
	    	{
	    		Transaction tn = (Transaction) tnIt8.next();
	    		if(tn.setNumber <= 0)
	    			continue;
	    		Iterator EdgeIt = tn.edges.iterator();
	    		while(EdgeIt.hasNext())
	    		{
	    			TransactionDataDependency tdd = (TransactionDataDependency) EdgeIt.next();
		    		rws[tn.setNumber - 1].union(tdd.rw);
	    		}
	    	}
	    }

		// Inspect each group's RW dependencies to determine if there's a possibility
		// of a shared lock object (if all dependencies are fields/localobjs of the same object)
//		boolean mustBeSameArrayElementForAllTns[] = new boolean[nextGroup - 1];
		if(optionStaticLocks)
		{
			for(int group = 0; group < nextGroup - 1; group++)
			{
				TransactionGroup tnGroup = (TransactionGroup) groups.get(group + 1);
				tnGroup.accessesOnlyOneType = false; // actually, unknown, so not necessary to set it
				tnGroup.useDynamicLock = false; 
				tnGroup.lockObject = null;
			}
		}
		else if(optionLeaveOriginalLocks)
		{
			// if for any lock there is any def to anything other than a static field, then it's a local lock.
			// initialize all groups to static
			for(int group = 0; group < nextGroup - 1; group++)
			{
				TransactionGroup tnGroup = (TransactionGroup) groups.get(group + 1);
				tnGroup.accessesOnlyOneType = false; // actually, unknown, so not necessary to set it
				tnGroup.useDynamicLock = false;
				tnGroup.lockObject = null;
			}
			
			// for each transaction, check every def of the lock
	    	Iterator tnAIt = AllTransactions.iterator();
	    	while(tnAIt.hasNext())
	    	{
	    		Transaction tn = (Transaction) tnAIt.next();
	    		if(tn.setNumber <= 0)
	    			continue;
	    		int group = tn.setNumber - 1;
				ExceptionalUnitGraph egraph = new ExceptionalUnitGraph(tn.method.retrieveActiveBody());
	    		SmartLocalDefs sld = new SmartLocalDefs(egraph, new SimpleLiveLocals(egraph));
	    		if(tn.origLock == null || !(tn.origLock instanceof Local)) // || tn.begin == null)
	    			continue;
	    		List rDefs = sld.getDefsOfAt( (Local) tn.origLock , tn.begin );
	    		if(rDefs == null)
	    			continue;
            	Iterator rDefsIt = rDefs.iterator();
            	while (rDefsIt.hasNext())
            	{
					Stmt next = (Stmt) rDefsIt.next();
					if(next instanceof DefinitionStmt)
					{
						Value rightOp = ((DefinitionStmt) next).getRightOp();
						if(rightOp instanceof FieldRef)
						{
							if(((FieldRef) rightOp).getField().isStatic())
							{
								// lock may be static
								tn.group.lockObject = rightOp;
							}
							else
							{
								// this lock must be dynamic
								tn.group.accessesOnlyOneType = true;
								tn.group.useDynamicLock = true;
								tn.group.lockObject = tn.origLock;
							}
						}
						else
						{
							// this lock is probably dynamic (but it's hard to tell for sure)
							tn.group.accessesOnlyOneType = true;
							tn.group.useDynamicLock = true;
							tn.group.lockObject = tn.origLock;
						}
					}
					else
					{
						// this lock is probably dynamic (but it's hard to tell for sure)
						tn.group.accessesOnlyOneType = true;
						tn.group.useDynamicLock = true;
						tn.group.lockObject = tn.origLock;
					}
				}
	    	}
		}
		else if(optionUseLocksets)
		{
			for(int group = 0; group < nextGroup - 1; group++)
			{
				TransactionGroup tnGroup = (TransactionGroup) groups.get(group + 1);

				tnGroup.useLocksets = true; // initially, guess that this is true

				// empty groups don't get locks
				if(rws[group].size() <= 0) // There are no transactions in this group
				{
					tnGroup.useLocksets = false;
					continue;
				}
			}
		}
		else // Find local locks when possible
		{
			for(int group = 0; group < nextGroup - 1; group++)
			{
				TransactionGroup tnGroup = (TransactionGroup) groups.get(group + 1);

				// For this group, find out if all RW Dependencies are possibly fields of the same object (object points-tos overlap)
				tnGroup.accessesOnlyOneType = true; // initially, guess that this is true
				tnGroup.lockObject = null;

				// empty groups don't get locks
				if(rws[group].size() <= 0) // There are no transactions in this group
				{
					tnGroup.accessesOnlyOneType = false;
					continue;
				}
				
				// groups with globals get a static lock
				if(rws[group].getGlobals().size() > 0) // There is some global field in this group
				{
					tnGroup.accessesOnlyOneType = false;
					continue;
				}
				
				// groups with contributing reads/writes from more than one type of object get a static lock, or a lockset
				Iterator grwsIt1 = rws[group].getFields().iterator();
				while(grwsIt1.hasNext() && tnGroup.accessesOnlyOneType)
				{
					Object field1 = (Object) grwsIt1.next();
					Iterator grwsIt2 = rws[group].getFields().iterator();
					while(grwsIt2.hasNext() && tnGroup.accessesOnlyOneType)
					{
						Object field2 = (Object) grwsIt2.next();
						if(!rws[group].getBaseForField(field1).hasNonEmptyIntersection(rws[group].getBaseForField(field2)))
						{
							// These two accessed fields are from two disjoint sets of objects
							tnGroup.accessesOnlyOneType = false;
						}
					}
				}

				tnGroup.useDynamicLock = tnGroup.accessesOnlyOneType; // attempt to use a dynamic lock if we access only one type
			}
		}
		
		// For each transaction, if the group's R/Ws may be fields of the same object, 
		// then check for the transaction if they must be fields of the same RUNTIME OBJECT
		if(!optionLeaveOriginalLocks)
		{
	    	Iterator tnIt9 = AllTransactions.iterator();
	    	while(tnIt9.hasNext())
	    	{
	    		Transaction tn = (Transaction) tnIt9.next();
				
				int group = tn.setNumber - 1;
				if(group < 0)
					continue;
						
				if(tn.group.useDynamicLock || tn.group.useLocksets) // if attempting to use a dynamic lock or locksets
				{
					// Get a list of contributing reads/writes: 
					// units that read/write to any of the dependences.
					Map unitToLocal = new HashMap();
					Map unitToUses = new HashMap();
					Map unitToArrayIndex = new HashMap(); // if all relevant R/Ws are via array references, we need to track the indexes as well.
					boolean allRefsAreArrayRefs = true;
					Value value = null;
					Value index = null; // array index if there is one
					Iterator entryIt = tn.unitToRWSet.entrySet().iterator();
					while(entryIt.hasNext())
					{
						Map.Entry e = (Map.Entry) entryIt.next();
						RWSet rw = (RWSet) e.getValue();
						if(rw.hasNonEmptyIntersection(rws[group]))
						{
							// This statement contributes to one or more RW dependencies
							Unit u = (Unit) e.getKey();
							Stmt s = (Stmt) u;
							
							if(optionUseLocksets)
							{
								unitToUses.put(s, tn.unitToUses.get(s));
							}
							
							// Get the base object of the field reference at this
							// statement. If there's an invoke expression, we can't be
							// sure it doesn't R/W to another object of the same type.
							value = null;
							index = null;
							if(!s.containsInvokeExpr() && (s.containsFieldRef() || s.containsArrayRef()))
							{
								if(s.containsFieldRef())
								{
									FieldRef fr = s.getFieldRef();
									if(fr instanceof InstanceFieldRef)
									{
										value = ((InstanceFieldRef) fr).getBase();
									}
									else
									{
										value = s.getFieldRef(); // actually, should use the class itself, not the field
									}
								}
								else
								{
									value = s.getArrayRef().getBase();
								}
								/*
								if( s instanceof AssignStmt ) {
									AssignStmt a = (AssignStmt) s;
									Value r = a.getRightOp();
									Value l = a.getLeftOp();
									
									// which one should we use???
									RWSet lSet = new StmtRWSet();
									Local lBase = null;
									Value lIndex = null;
									if( l instanceof InstanceFieldRef ) {
										InstanceFieldRef ifr = (InstanceFieldRef) l;
										lBase = (Local) ifr.getBase();
										PointsToSet base = Scene.v().getPointsToAnalysis().reachingObjects( lBase );
										lSet.addFieldRef( base, ifr.getField() );
									} else if( l instanceof ArrayRef ) {
										ArrayRef ar = (ArrayRef) l;
										lBase = (Local) ar.getBase();
										lIndex = ar.getIndex();
										PointsToSet base = Scene.v().getPointsToAnalysis().reachingObjects( lBase );
										lSet.addFieldRef( base, PointsToAnalysis.ARRAY_ELEMENTS_NODE );
									}
									if(lSet.hasNonEmptyIntersection(rws[group]))
									{
										value = lBase; // if the lvalue's write set contributes, use it
										index = lIndex;
									}
									else // otherwise it must be the rvalue!
									{
										RWSet rSet = new StmtRWSet();
										Local rBase = null;
										Value rIndex = null;
										if( r instanceof InstanceFieldRef ) 
										{
											InstanceFieldRef ifr = (InstanceFieldRef) r;
											rBase = (Local) ifr.getBase();
											PointsToSet base = Scene.v().getPointsToAnalysis().reachingObjects( rBase );
											rSet.addFieldRef( base, ifr.getField() );
										}
										else if( r instanceof ArrayRef )
										{
											ArrayRef ar = (ArrayRef) r;
											rBase = (Local) ar.getBase();
											rIndex = ar.getIndex();
											PointsToSet base = Scene.v().getPointsToAnalysis().reachingObjects( rBase );
											rSet.addFieldRef( base, PointsToAnalysis.ARRAY_ELEMENTS_NODE );
										}
										if(rSet.hasNonEmptyIntersection(rws[group]))
										{
											value = rBase; // if the lvalue's write set contributes, use it
											index = rIndex;
										}
									}
								}
								*/
							}

	//						G.v().out.println("LOCAL OBJ REF for " + s + 
	//							" value:" + value + " index:" + index);

							if(value == null)
							{
								if(!optionUseLocksets)
								{
									tn.group.useDynamicLock = false; // failed to use a dynamic lock
//									mustBeFieldsOfSameObjectForAllTns[group] = false;
									tn.group.lockObject = null;
//									lockObject[group] = null;
								}
								break; // move on to next transaction
							}
							unitToLocal.put(u, value);
							
							if(index == null)
							{
								allRefsAreArrayRefs = false;
							}
							
							if(allRefsAreArrayRefs)
								unitToArrayIndex.put(u, index);
							
						}
					}
					
					// Use EqualUsesAnalysis to determine if all contributing units must be accessing fields of the same RUNTIME OBJECT
	//				G.v().out.print("Transaction " + tn.name + " in " + tn.method + " ");
	//				UnitGraph g = new ExceptionalUnitGraph(tn.method.retrieveActiveBody());
					UnitGraph g = (UnitGraph) methodToExcUnitGraph.get(tn.method);

					if(optionUseLocksets) // multiple locks per region
					{						
						G.v().out.println("lockset for " + tn.name + " w/ " + unitToUses + " is:");
								
						LocksetAnalysis la = new LocksetAnalysis(new BriefUnitGraph(tn.method.retrieveActiveBody()));
						tn.lockset = la.getLocksetOf(unitToUses, tn.begin);
						
						if(tn.lockset == null)
							tn.group.useLocksets = false;
							
						G.v().out.println("  " + (tn.lockset == null ? "FAILURE" : tn.lockset.toString()));
						
//						G.v().out.println("Group " + group + " has lockset " + la.getLockset());
					}
					else // one lock per region
					{
						EqualUsesAnalysis lif = new EqualUsesAnalysis(g);
						Vector barriers = (Vector) tn.ends.clone();
						barriers.add(tn.begin);
						if( lif.areEqualUses(unitToLocal, barriers) ) // runs the analysis
						{
							Map firstUseToAliasSet = lif.getFirstUseToAliasSet(); // get first uses for this transaction					
							NullnessAnalysis na = new NullnessAnalysis(g);
							NullnessAssumptionAnalysis naa = new NullnessAssumptionAnalysis(g);
							CommonPrecedingEqualValueAnalysis cav = new CommonPrecedingEqualValueAnalysis(new BriefUnitGraph(tn.method.retrieveActiveBody()));
							List ancestors = cav.getCommonAncestorValuesOf(firstUseToAliasSet, tn.begin);
							Iterator ancestorsIt = ancestors.iterator();
							while(ancestorsIt.hasNext())
							{
								Object o = ancestorsIt.next();
								Value v = null;
								if(o instanceof EquivalentValue)
								{
									v = ((EquivalentValue) o).getValue();
								}
								else if (o instanceof Value) // This isn't supposed to happen, but it does.  Damn.
								{
									v = (Value) o;
								}
								else
								{
		//							G.v().out.println("THIS SHOULD BE AN EQVALUE BUT ISN'T: " + o + " is of type " + o.getClass());
								}
								
								if(v != null)
								{
									if(v instanceof Ref || 
										(v instanceof Local && 
											(na.isAlwaysNonNullBefore(tn.begin, (Local) v) ||
											 naa.isAssumedNonNullBefore(tn.begin, (Local) v))) )
									{
										if(tn.group.lockObject == null || v instanceof Ref)
											tn.group.lockObject = v;
										if( tn.lockObject == null || v instanceof Ref )
											tn.lockObject = v;
									}
									else
									{
//										G.v().out.println("Value " + v + " was rejected as a lock object because it could be null in " + tn.name);
									}
								}
								else
								{
//									G.v().out.println("Ancestor " + o + " was rejected as a lock object because it is null in " + tn.name);
								}
							}
							
							if(tn.lockObject == null)
							{
//								G.v().out.println("No value was found for a lock object in " + tn.name + " (no ancestor value found)");
								tn.group.useDynamicLock = false; // failed to use a dynamic lock
								tn.group.lockObject = null;
								continue; // move on to next transaction
							}
							
/*
							if( allRefsAreArrayRefs ) // NOTE: This is disabled because there is a possibility of
															   // null elements in the array.  You cannot lock a null object.
															   // We need a won't-introduce-a-new-null-object-error analysis!
							{
								G.v().out.println("checking for equivalence of these array indices" + unitToArrayIndex.values());
								if( lif.areEqualUses(unitToArrayIndex, barriers) )
								{
									G.v().out.println("array indices are equivalent... finding ancestor value at " + tn.begin);
									firstUseToAliasSet = lif.getFirstUseToAliasSet(); // get first uses for this transaction					
									cav = new CommonPrecedingEqualValueAnalysis(new BriefUnitGraph(tn.method.retrieveActiveBody()));
									ancestors = cav.getCommonAncestorValuesOf(firstUseToAliasSet, tn.begin);
									ancestorsIt = ancestors.iterator();
									while(ancestorsIt.hasNext())
									{
										Object o = ancestorsIt.next();
										Value v = null;
										if(o instanceof EquivalentValue)
										{
											v = ((EquivalentValue) o).getValue();
										}
										else if (o instanceof Value) // This isn't supposed to happen, but it does.  Damn.
										{
											v = (Value) o;
										}
										else
										{
											G.v().out.println("THIS SHOULD BE AN EQVALUE BUT ISN'T: " + o + " is of type " + o.getClass());
										}
										if(v != null)
										{
											if( tn.lockObjectArrayIndex == null || v instanceof Local )
												tn.lockObjectArrayIndex = v;
										}
									}
									G.v().out.println("array index ancestor is: " + tn.lockObjectArrayIndex);
								}
							}
*/
						}
						else
						{
//							G.v().out.println("No value was found for a lock object in " + tn.name + " (transaction accesses multiple objects of same type)");
							tn.group.useDynamicLock = false; // failed to use a dynamic lock
							tn.group.lockObject = null;
						}
					}
				}
				else
				{
//					G.v().out.println("No value was found for a lock object in " + tn.name + " (transactions in group access multiple objects of different types)");
				}
			}
		}
		
		
		
		// *** Print Output and Transform Program ***
    	G.v().out.println("[wjtp.tn] *** Print Output and Transform Program *** " + (new Date()));

		// Print topological graph in graphviz format
		if(optionPrintGraph)
		{
			printGraph(AllTransactions, groups);
		}

		// Print table of transaction information
		if(optionPrintTable)
		{
			printTable(AllTransactions);			
			printGroups(AllTransactions, nextGroup, groups, rws);
		}

    	// For all methods, run the transformer (Pessimistic Transaction Tranformation)
		if(!optionLeaveOriginalLocks)
		{
	    	// BEGIN UGLINESS
			TransactionBodyTransformer.addedGlobalLockObj = new boolean[nextGroup];
			TransactionBodyTransformer.addedGlobalLockObj[0] = false;
			boolean useGlobalLock[] = new boolean[nextGroup - 1];
			for(int i = 1; i < nextGroup; i++)
			{
				TransactionGroup tnGroup = (TransactionGroup) groups.get(i);
				TransactionBodyTransformer.addedGlobalLockObj[i] = (!optionOneGlobalLock) && (tnGroup.useDynamicLock || tnGroup.useLocksets);
				useGlobalLock[i - 1] = !tnGroup.useDynamicLock && !tnGroup.useLocksets;
			}
			// END UGLINESS
			
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
	    	    	
	    	    		if(fs != null) // any new method that is added after the transaction analysis will have a null
	    	    		{
//		   	    			TransactionBodyTransformer.v().setDetails(fs, nextGroup, useGlobalLock);
		   	    			TransactionBodyTransformer.v().internalTransform(b, fs, groups); 
		   	    		}
					}
	    	    }
	    	}
	    }
	}
    
    public boolean mayHappenInParallel(Transaction tn1, Transaction tn2)
    {
    	if(mhp == null)
    	{
    		if(optionLeaveOriginalLocks)
    			return true;
    		ReachableMethods rm = Scene.v().getReachableMethods();
    		if(!rm.contains(tn1.method) || !rm.contains(tn2.method))
    			return false;
    		return true;
    	}
    	return mhp.mayHappenInParallel(tn1.method, tn2.method);
    }
    
    public void assignNamesToTransactions(List AllTransactions)
    {
       	// Give each method a unique, deterministic identifier
       	// Sort transactions into bins... one for each method name
       	
       	// Get list of method names
    	List methodNamesTemp = new ArrayList();
    	Iterator tnIt5 = AllTransactions.iterator();
    	while (tnIt5.hasNext()) 
    	{
    	    Transaction tn1 = (Transaction) tnIt5.next();
    	    String mname = tn1.method.getSignature(); //tn1.method.getSignature() + "." + tn1.method.getName();
    	    if(!methodNamesTemp.contains(mname))
    	    	methodNamesTemp.add(mname);
		}
		String methodNames[] = new String[1];
		methodNames = (String []) methodNamesTemp.toArray(methodNames);
		Arrays.sort(methodNames);

		// Initialize method-named bins
		// this matrix is <# method names> wide and <max txns possible in one method> + 1 tall
		int identMatrix[][] = new int[methodNames.length][Transaction.nextIDNum - methodNames.length + 2];
		for(int i = 0; i < methodNames.length; i++)
		{
			identMatrix[i][0] = 0;
			for(int j = 1; j < Transaction.nextIDNum - methodNames.length + 1; j++)
			{
				identMatrix[i][j] = 50000;
			}
		}
		
		// Put transactions into bins
    	Iterator tnIt0 = AllTransactions.iterator();
    	while(tnIt0.hasNext())
    	{
    		Transaction tn1 = (Transaction) tnIt0.next();
			int methodNum = Arrays.binarySearch(methodNames, tn1.method.getSignature());
			identMatrix[methodNum][0]++;
			identMatrix[methodNum][identMatrix[methodNum][0]] = tn1.IDNum;
    	}
    	
    	// Sort bins by transaction IDNum
    	// IDNums vary, but always increase in code-order within a method
    	for(int j = 0; j < methodNames.length; j++)
    	{
    		identMatrix[j][0] = 0; // set the counter to 0 so it sorts out (into slot 0).
    		Arrays.sort(identMatrix[j]); // sort this subarray
		}
		
		// Generate a name based on the bin number and location within the bin
    	Iterator tnIt4 = AllTransactions.iterator();
    	while(tnIt4.hasNext())
    	{
    		Transaction tn1 = (Transaction) tnIt4.next();
			int methodNum = Arrays.binarySearch(methodNames, tn1.method.getSignature());
			int tnNum = Arrays.binarySearch(identMatrix[methodNum], tn1.IDNum) - 1;
    		tn1.name = "m" + (methodNum < 10? "00" : (methodNum < 100? "0" : "")) + methodNum + "n" + (tnNum < 10? "0" : "") + tnNum;
    	}
	}	

	public void printGraph(Collection AllTransactions, List groups)
	{
		G.v().out.println("[transaction-graph] strict graph transactions {\n[transaction-graph] start=1;");

		for(int group = 0; group < groups.size(); group++)
		{
			boolean printedHeading = false;
			Iterator tnIt = AllTransactions.iterator();
			while(tnIt.hasNext())
			{
				Transaction tn = (Transaction) tnIt.next();
				if(tn.setNumber == group + 1)
				{
					if(!printedHeading)
					{
//						if(localLock[group] && lockObject[group] != null)
						if(tn.group.useDynamicLock && tn.group.lockObject != null)
						{
							String typeString = "";
//							if(lockObject[group].getType() instanceof RefType)
//								typeString = ((RefType) lockObject[group].getType()).getSootClass().getShortName();
//							else
//								typeString = lockObject[group].getType().toString();
							if(tn.group.lockObject.getType() instanceof RefType)
								typeString = ((RefType) tn.group.lockObject.getType()).getSootClass().getShortName();
							else
								typeString = tn.group.lockObject.getType().toString();
							G.v().out.println("[transaction-graph] subgraph cluster_" + (group + 1) + " {\n[transaction-graph] color=blue;\n[transaction-graph] label=\"Lock: a \\n" + typeString + " object\";");
						}
						else
						{
							String objString = "";
//							if(lockObject[group] == null)
							if(tn.group.lockObject == null)
							{
								objString = "lockObj" + (group + 1);
							}
//							else if(lockObject[group] instanceof FieldRef)
							else if(tn.group.lockObject instanceof FieldRef)
							{
//								SootField field = ((FieldRef) lockObject[group]).getField();
								SootField field = ((FieldRef) tn.group.lockObject).getField();
								objString = field.getDeclaringClass().getShortName() + "." + field.getName();
							}
							else
								objString = tn.group.lockObject.toString();
//								objString = lockObject[group].toString();
							G.v().out.println("[transaction-graph] subgraph cluster_" + (group + 1) + " {\n[transaction-graph] color=blue;\n[transaction-graph] label=\"Lock: \\n" + objString + "\";");
						}
						printedHeading = true;
					}
					if(Scene.v().getReachableMethods().contains(tn.method))
						G.v().out.println("[transaction-graph] " + tn.name + " [name=\"" + tn.method.toString() + "\"];");
					else
						G.v().out.println("[transaction-graph] " + tn.name + " [name=\"" + tn.method.toString() + "\" color=cadetblue1];");

					Iterator tnedgeit = tn.edges.iterator();
					while(tnedgeit.hasNext())
					{
						TransactionDataDependency edge = (TransactionDataDependency) tnedgeit.next();
						Transaction tnedge = edge.other;
						if(tnedge.setNumber == group + 1)
							G.v().out.println("[transaction-graph] " + tn.name + " -- " + tnedge.name + " [color=" + (edge.size > 0 ? "black" : "cadetblue1") + " style=" + (tn.setNumber > 0 && tn.group.useDynamicLock ? "dashed" : "solid") + " exactsize=" + edge.size + "];");
					}
				}
				
			}
			if(printedHeading)
				G.v().out.println("[transaction-graph] }");
		}

		{
			boolean printedHeading = false;
			Iterator tnIt = AllTransactions.iterator();
			while(tnIt.hasNext())
			{
				Transaction tn = (Transaction) tnIt.next();
				if(tn.setNumber == -1)
				{
					if(Scene.v().getReachableMethods().contains(tn.method))
						G.v().out.println("[transaction-graph] " + tn.name + " [name=\"" + tn.method.toString() + "\"];");
					else
						G.v().out.println("[transaction-graph] " + tn.name + " [name=\"" + tn.method.toString() + "\" color=cadetblue1];");
				
					Iterator tnedgeit = tn.edges.iterator();
					while(tnedgeit.hasNext())
					{
						TransactionDataDependency edge = (TransactionDataDependency) tnedgeit.next();
						Transaction tnedge = edge.other;
						if(tnedge.setNumber != tn.setNumber || tnedge.setNumber == -1)
							G.v().out.println("[transaction-graph] " + tn.name + " -- " + tnedge.name + " [color=" + (edge.size > 0 ? "black" : "cadetblue1") + " style=" + (tn.setNumber > 0 && tn.group.useDynamicLock ? "dashed" : "solid") + " exactsize=" + edge.size + "];");
					}
				}
			}
		}

		G.v().out.println("[transaction-graph] }");
	}	

	public void printTable(Collection AllTransactions)
	{
		G.v().out.println("[transaction-table] ");
		Iterator tnIt7 = AllTransactions.iterator();
		while(tnIt7.hasNext())
		{
			Transaction tn = (Transaction) tnIt7.next();
			G.v().out.println("[transaction-table] Transaction " + tn.name);
			G.v().out.println("[transaction-table] Where: " + tn.method.getDeclaringClass().toString() + ":" + tn.method.toString() + ":  ");
			G.v().out.println("[transaction-table] Orig : " + tn.origLock);
			G.v().out.println("[transaction-table] Prep : " + (tn.prepStmt == null ? "none" : tn.prepStmt.toString()));
			G.v().out.println("[transaction-table] Begin: " + tn.begin.toString());
			G.v().out.print("[transaction-table] End  : " + tn.ends.toString() + " \n");
			G.v().out.println("[transaction-table] Size : " + tn.units.size());
			if(tn.read.size() < 100)
				G.v().out.print("[transaction-table] Read : " + tn.read.size() + "\n[transaction-table] " + 
					tn.read.toString().replaceAll("\\[", "     : [").replaceAll("\n", "\n[transaction-table] ") + 
					(tn.read.size() == 0 ? "\n[transaction-table] " : ""));
			else
				G.v().out.print("[transaction-table] Read : " + tn.read.size() + "  \n[transaction-table] ");
			if(tn.write.size() < 100)
				G.v().out.print("Write: " + tn.write.size() + "\n[transaction-table] " + 
					tn.write.toString().replaceAll("\\[", "     : [").replaceAll("\n", "\n[transaction-table] ") + 
					(tn.write.size() == 0 ? "\n[transaction-table] " : "")); // label provided by previous print statement
			else
				G.v().out.print("Write: " + tn.write.size() + "\n[transaction-table] "); // label provided by previous print statement
			G.v().out.print("Edges: (" + tn.edges.size() + ") "); // label provided by previous print statement
			Iterator tnedgeit = tn.edges.iterator();
			while(tnedgeit.hasNext())
				G.v().out.print(((TransactionDataDependency)tnedgeit.next()).other.name + " ");
			if(tn.lockset != null)
				G.v().out.println("\n[transaction-table] Locks: " + tn.lockset);
			else
				G.v().out.println("\n[transaction-table] Lock : " + (tn.setNumber == -1 ? "-" : (tn.lockObject == null ? "Global" : (tn.lockObject.toString() + (tn.lockObjectArrayIndex == null ? "" : "[" + tn.lockObjectArrayIndex + "]")) )));
			G.v().out.println("[transaction-table] Group: " + tn.setNumber + "\n[transaction-table] ");
		}
	}
	
	public void printGroups(Collection AllTransactions, int nextGroup, List groups, RWSet rws[])
	{
			G.v().out.print("[transaction-groups] Group Summaries\n[transaction-groups] ");
			for(int group = 0; group < nextGroup - 1; group++)
    		{
    			TransactionGroup tnGroup = (TransactionGroup) groups.get(group + 1);
    			G.v().out.print("Group " + (group + 1) + " ");
				G.v().out.print("Locking: " + (tnGroup.accessesOnlyOneType && tnGroup.useDynamicLock ? "Dynamic" : "Static") + " on " + (optionUseLocksets ? "locksets" : (tnGroup.lockObject == null ? "null" : tnGroup.lockObject.toString())) );
				G.v().out.print("\n[transaction-groups]      : ");
				Iterator tnIt = AllTransactions.iterator();
				while(tnIt.hasNext())
				{
					Transaction tn = (Transaction) tnIt.next();
					if(tn.setNumber == group + 1)
						G.v().out.print(tn.name + " ");
				}
				G.v().out.print("\n[transaction-groups] " + 
    							rws[group].toString().replaceAll("\\[", "     : [").replaceAll("\n", "\n[transaction-groups] ") + 
								(rws[group].size() == 0 ? "\n[transaction-groups] " : ""));
	    	}
			G.v().out.print("Erasing \n[transaction-groups]      : ");
			Iterator tnIt = AllTransactions.iterator();
			while(tnIt.hasNext())
			{
				Transaction tn = (Transaction) tnIt.next();
				if(tn.setNumber == -1)
					G.v().out.print(tn.name + " ");
			}
			G.v().out.println("\n[transaction-groups] ");
	}
}
