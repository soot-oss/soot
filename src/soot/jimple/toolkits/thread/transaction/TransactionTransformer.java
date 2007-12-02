package soot.jimple.toolkits.thread.transaction;

import java.util.*;

import soot.*;
import soot.util.Chain;
import soot.jimple.*;
import soot.jimple.toolkits.pointer.*;
import soot.jimple.toolkits.thread.ThreadLocalObjectsAnalysis;
import soot.jimple.toolkits.thread.mhp.UnsynchronizedMhpAnalysis;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.infoflow.*;
import soot.jimple.spark.pag.*;
import soot.jimple.spark.sets.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;

public class TransactionTransformer extends SceneTransformer
{
    public TransactionTransformer(Singletons.Global g){}
    public static TransactionTransformer v() 
	{ 
		return G.v().soot_jimple_toolkits_thread_transaction_TransactionTransformer();
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
	boolean optionOnFlyTLO = false; // not a CLI option yet // on-fly is more efficient, but harder to measure in time
	
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
			optionUseLocksets = false;
			optionLeaveOriginalLocks = false;
		}
		if(lockingScheme.equals("leave-original"))
		{
			optionOneGlobalLock = false;
			optionStaticLocks = false;
			optionUseLocksets = false;
			optionLeaveOriginalLocks = true;
		}
		
		optionAvoidDeadlock = PhaseOptions.getBoolean( options, "avoid-deadlock" );
		optionOpenNesting = PhaseOptions.getBoolean( options, "open-nesting" );

		optionDoMHP = PhaseOptions.getBoolean( options, "do-mhp" );
		optionDoTLO = PhaseOptions.getBoolean( options, "do-tlo" );
//		optionOnFlyTLO = PhaseOptions.getBoolean( options, "on-fly-tlo" ); // not a real option yet

//		optionPrintMhpSummary = PhaseOptions.getBoolean( options, "print-mhp" ); // not a real option yet
		optionPrintGraph = PhaseOptions.getBoolean( options, "print-graph" );
		optionPrintTable = PhaseOptions.getBoolean( options, "print-table" );
		optionPrintDebug = PhaseOptions.getBoolean( options, "print-debug" );
		
//		optionIncludeEmptyPossibleEdges = PhaseOptions.getBoolean( options, "include-empty-edges" ); // not a real option yet
		
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
	    		tlo = new ThreadLocalObjectsAnalysis(mhp,false); // can tell only that a field/local is local to the object it's being accessed in
			else
	    		tlo = new ThreadLocalObjectsAnalysis(new UnsynchronizedMhpAnalysis(),false); // can tell only that a field/local is local to the object it's being accessed in
	    	if(!optionOnFlyTLO)
	    	{
		    	tlo.precompute();
	    		G.v().out.println("[wjtp.tn] TLO totals (#analyzed/#encountered): " + SmartMethodInfoFlowAnalysis.counter + "/" + ClassInfoFlowAnalysis.methodCount);
		    }
    	}



    	// *** Find and Name Transactions ***
    	// The transaction finder finds the start, end, and preparatory statements
    	// for each transaction. It also calculates the non-transitive read/write 
    	// sets for each transaction.
    	// For all methods, run the intraprocedural analysis (TransactionAnalysis)
		Date start = new Date();
    	G.v().out.println("[wjtp.tn] *** Find and Name Transactions *** " + start);
    	Map<SootMethod, FlowSet> methodToFlowSet = new HashMap<SootMethod, FlowSet>();
    	Map<SootMethod, ExceptionalUnitGraph> methodToExcUnitGraph = new HashMap<SootMethod, ExceptionalUnitGraph>();
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
    	List<Transaction> AllTransactions = new Vector<Transaction>();
    	Collection<FlowSet> AllFlowSets = methodToFlowSet.values();
    	Iterator<FlowSet> fsIt = AllFlowSets.iterator();
    	while(fsIt.hasNext())
    	{
    		FlowSet fs = fsIt.next();
    		List fList = fs.toList();
    		for(int i = 0; i < fList.size(); i++)
    			AllTransactions.add(((TransactionFlowPair) fList.get(i)).tn);
    	}

		// Assign Names To Transactions
		assignNamesToTransactions(AllTransactions);

    	if(optionOnFlyTLO)
    	{
    		G.v().out.println("[wjtp.tn] TLO totals (#analyzed/#encountered): " + SmartMethodInfoFlowAnalysis.counter + "/" + ClassInfoFlowAnalysis.methodCount);
    	}

    	

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
    	Iterator<Transaction> tnIt = AllTransactions.iterator();
    	while(tnIt.hasNext())
    	{
    		Transaction tn = tnIt.next();
    		Iterator<Object> invokeIt = tn.invokes.iterator();
    		while(invokeIt.hasNext())
    		{
    			Stmt stmt = (Stmt) invokeIt.next();
    			
    			HashSet uses = new HashSet();
    			RWSet stmtRead = tasea.readSet(tn.method, stmt, tn, uses);
    			if(stmtRead != null)
	    			tn.read.union(stmtRead);
    			
    			RWSet stmtWrite = tasea.writeSet(tn.method, stmt, tn, uses);
				if(stmtWrite != null)
					tn.write.union(stmtWrite);
					
				// memory hog???
/*				CodeBlockRWSet bothRW = new CodeBlockRWSet();
				bothRW.union(stmtRead);
				bothRW.union(stmtWrite);
       			tn.unitToRWSet.put(stmt, bothRW);

				List<Object> usesList;
				if(tn.unitToUses.containsKey(stmt))
					usesList = tn.unitToUses.get(stmt);
				else
				{
					usesList = new ArrayList<Object>();
					tn.unitToUses.put(stmt, usesList);
				}

				for(Iterator usesIt = uses.iterator(); usesIt.hasNext(); )
				{
					Object use = usesIt.next();
					if(!usesList.contains(use))
						usesList.add(use);
				}
*/
			}
    	}
    	long longTime = ((new Date()).getTime() - start.getTime()) / 100;
    	float time = ((float) longTime) / 10.0f;
    	if(optionOnFlyTLO)
    	{
    		G.v().out.println("[wjtp.tn] TLO totals (#analyzed/#encountered): " + SmartMethodInfoFlowAnalysis.counter + "/" + ClassInfoFlowAnalysis.methodCount);
			G.v().out.println("[wjtp.tn] Time for stages utilizing on-fly TLO: " + time + "s");
    	}



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
    	List<TransactionGroup> groups = new ArrayList<TransactionGroup>();
    	groups.add(new TransactionGroup(0)); // dummy group
    	if(optionOneGlobalLock) // use one group for all transactions
    	{
    		TransactionGroup onlyGroup = new TransactionGroup(nextGroup);
	    	Iterator<Transaction> tnIt1 = AllTransactions.iterator();
	    	while(tnIt1.hasNext())
	    	{
	    		Transaction tn1 = tnIt1.next();
	    		onlyGroup.add(tn1);
    		}
    		nextGroup++;
    		groups.add(onlyGroup);
    	}
    	else // calculate separate groups for transactions
    	{
	    	Iterator<Transaction> tnIt1 = AllTransactions.iterator();
	    	while(tnIt1.hasNext())
	    	{
	    		Transaction tn1 = tnIt1.next();
	    		
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
		        	Iterator<Transaction> tnIt2 = AllTransactions.iterator();
		    		while(tnIt2.hasNext())
		    		{
		    			Transaction tn2 = tnIt2.next();
		    				    			
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
		TransitiveTargets tt = new TransitiveTargets(Scene.v().getCallGraph(), new Filter(new TransactionVisibleEdgesPred(null)));
		if(!optionUseLocksets) // deadlock detection for all single-lock-per-region allocations
		{
			MutableDirectedGraph lockOrder;
			boolean foundDeadlock;
			do
			{
				G.v().out.println("[wjtp.tn] *** Detect the Possibility of Deadlock *** " + (new Date()));
				foundDeadlock = false;
		    	lockOrder = new HashMutableDirectedGraph(); // start each iteration with a fresh graph
				
				// Assemble the partial ordering of locks
		    	Iterator<Transaction> deadlockIt1 = AllTransactions.iterator();
		    	while(deadlockIt1.hasNext() && !foundDeadlock)
		    	{
		    		Transaction tn1 = deadlockIt1.next();
		    		
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
			    		tn1.transitiveTargets = new HashSet<MethodOrMethodContext>();
			    		Iterator<Object> tn1InvokeIt = tn1.invokes.iterator();
			    		while(tn1InvokeIt.hasNext())
			    		{
			    			Unit tn1Invoke = (Unit) tn1InvokeIt.next();
			    			Iterator<MethodOrMethodContext> targetIt = tt.iterator(tn1Invoke);
			    			while(targetIt.hasNext())
			    				tn1.transitiveTargets.add(targetIt.next());
			    		}
			    	}
		    		
		    		// compare to each other tn
		    		Iterator<Transaction> deadlockIt2 = AllTransactions.iterator();
		    		while(deadlockIt2.hasNext() && !foundDeadlock)
		    		{
		    			Transaction tn2 = deadlockIt2.next();
		    			
		    			// skip if unlocked or in same set as tn1
		    			if( tn2.setNumber <= 0 || tn2.setNumber == tn1.setNumber ) // this is wrong... dynamic locks in same group can be diff locks
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
			    			{
			    				List succs = lockOrder.getSuccsOf(afterTn2.get(i));
			    				for( Object o : succs )
			    				{
			    					if(!afterTn2.contains(o))
					    				afterTn2.add(o);
				    			}
			    			}
			    				
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
		}


		// *** Calculate Locking Objects ***
    	// Get a list of all dependencies for each group
    	G.v().out.println("[wjtp.tn] *** Calculate Locking Objects *** " + (new Date()));
    	RWSet rws[] = new CodeBlockRWSet[nextGroup - 1];
    	for(int group = 0; group < nextGroup - 1; group++)
    		rws[group] = new CodeBlockRWSet();
		if(!optionStaticLocks)
		{
			// THIS SHOULD BE REMOVED AND REPLACED WITH A PER-TRANSACTION CONTRIBUTING RWSET
	    	Iterator<Transaction> tnIt8 = AllTransactions.iterator();
	    	while(tnIt8.hasNext())
	    	{
	    		Transaction tn = tnIt8.next();
	    		if(tn.setNumber <= 0)
	    			continue;
	    		Iterator<TransactionDataDependency> EdgeIt = tn.edges.iterator();
	    		while(EdgeIt.hasNext())
	    		{
	    			TransactionDataDependency tdd = EdgeIt.next();
		    		rws[tn.setNumber - 1].union(tdd.rw);
	    		}
	    	}
	    }

		// Inspect each group's RW dependencies to determine if there's a possibility
		// of a shared lock object (if all dependencies are fields/localobjs of the same object)
		if(optionStaticLocks)
		{
			// Allocate one new static lock for each group.
			for(int group = 0; group < nextGroup - 1; group++)
			{
				TransactionGroup tnGroup = groups.get(group + 1);
				tnGroup.isDynamicLock = false;
				tnGroup.useDynamicLock = false; 
				tnGroup.lockObject = null;
			}
		}
		else if(optionLeaveOriginalLocks)
		{
			// This mode is treated similarly to dynamic allocation: one lock per group, could be static or dynamic.
			// However, instead of finding the lock, we already have it, and need to figure out if it's static or not.
			for(int group = 0; group < nextGroup - 1; group++)
			{
				TransactionGroup tnGroup = groups.get(group + 1);
				tnGroup.isDynamicLock = false; // assume it's a static lock... then try to prove otherwise
				tnGroup.useDynamicLock = false;
				tnGroup.lockObject = null;
			}
			
			// if for any lock there is any def to anything other than a static field, then it's a local lock.			
			// for each transaction, check every def of the lock
	    	Iterator<Transaction> tnAIt = AllTransactions.iterator();
	    	while(tnAIt.hasNext())
	    	{
	    		Transaction tn = tnAIt.next();
	    		if(tn.setNumber <= 0)
	    			continue;
	    		ExceptionalUnitGraph egraph = new ExceptionalUnitGraph(tn.method.retrieveActiveBody());
	    		SmartLocalDefs sld = new SmartLocalDefs(egraph, new SimpleLiveLocals(egraph));
	    		if(tn.origLock == null || !(tn.origLock instanceof Local)) // || tn.begin == null)
	    			continue;
	    		List<Unit> rDefs = sld.getDefsOfAt( (Local) tn.origLock , tn.entermonitor );
	    		if(rDefs == null)
	    			continue;
            	Iterator<Unit> rDefsIt = rDefs.iterator();
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
								tn.group.isDynamicLock = true;
								tn.group.useDynamicLock = true;
								tn.group.lockObject = tn.origLock;
							}
						}
						else
						{
							// this lock is probably dynamic (but it's hard to tell for sure)
							tn.group.isDynamicLock = true;
							tn.group.useDynamicLock = true;
							tn.group.lockObject = tn.origLock;
						}
					}
					else
					{
						// this lock is probably dynamic (but it's hard to tell for sure)
						tn.group.isDynamicLock = true;
						tn.group.useDynamicLock = true;
						tn.group.lockObject = tn.origLock;
					}
				}
	    	}
		}
		else // for locksets and dynamic locks
		{
			for(int group = 0; group < nextGroup - 1; group++)
			{
				TransactionGroup tnGroup = groups.get(group + 1);

				if(optionUseLocksets)
				{
					tnGroup.useLocksets = true; // initially, guess that this is true
				}
				else
				{
					tnGroup.isDynamicLock = (rws[group].getGlobals().size() == 0);
					tnGroup.useDynamicLock = true;
					tnGroup.lockObject = null;
				}

				// empty groups don't get locks
				if(rws[group].size() <= 0) // There are no transactions in this group
				{
					if(optionUseLocksets)
					{
						tnGroup.useLocksets = false;
					}
					else
					{
						tnGroup.isDynamicLock = false;
						tnGroup.useDynamicLock = false;
					}
					continue;
				}
			}
		}

		// Find runtime lock objects (if using dynamic locks or locksets)
		Map<Value, Integer> lockToLockNum = null;
		List<PointsToSetInternal> lockPTSets = null;
		if(!optionOneGlobalLock && !optionStaticLocks && !optionLeaveOriginalLocks)
		{
			// Data structures for determining lock numbers
			lockPTSets = new ArrayList<PointsToSetInternal>();
			lockToLockNum = new HashMap<Value, Integer>();

			// For each transaction, if the group's R/Ws may be fields of the same object, 
			// then check for the transaction if they must be fields of the same RUNTIME OBJECT
	    	Iterator<Transaction> tnIt9 = AllTransactions.iterator();
	    	while(tnIt9.hasNext())
	    	{
	    		Transaction tn = tnIt9.next();
				
				int group = tn.setNumber - 1;
				if(group < 0)
					continue;
						
				if(tn.group.useDynamicLock || tn.group.useLocksets) // if attempting to use a dynamic lock or locksets
				{
					
					// Get list of objects (FieldRef or Local) to be locked (lockset analysis)
					G.v().out.println("[wjtp.tn] * " + tn.name + " *");
					LocksetAnalysis la = new LocksetAnalysis(new BriefUnitGraph(tn.method.retrieveActiveBody()));
					tn.lockset = la.getLocksetOf(tasea, rws[group], tn);
					
					// Determine if list is suitable for the selected locking scheme
					// TODO check for nullness
					if(optionUseLocksets)
					{
						// Post-process the locksets
						if(tn.lockset == null || tn.lockset.size() <= 0)
						{
							// If the lockset is invalid, revert the entire group to static locks:
							tn.group.useLocksets = false;
							
							// Create a lockset containing a single placeholder static lock for each tn in the group
							Value newStaticLock = new NewStaticLock(tn.method.getDeclaringClass());
							EquivalentValue newStaticLockEqVal = new EquivalentValue(newStaticLock);
							for(Transaction groupTn : tn.group)
							{
								groupTn.lockset = new ArrayList<EquivalentValue>();
								groupTn.lockset.add(newStaticLockEqVal);
							}

							// Assign a lock number to the placeholder
							Integer lockNum = new Integer(-lockPTSets.size()); // negative indicates a static lock
							G.v().out.println("[wjtp.tn] Lock: num " + lockNum + " type " + newStaticLock.getType() + " obj " + newStaticLock);
							lockToLockNum.put(newStaticLockEqVal, lockNum);
							lockToLockNum.put(newStaticLock, lockNum);
							PointsToSetInternal dummyLockPT = new HashPointsToSet(newStaticLock.getType(), (PAG) pta); // KILLS CHA-BASED ANALYSIS (pointer exception)
							lockPTSets.add(dummyLockPT);
						}
						else
						{
							// If the lockset is valid
							// Assign a lock number for each lock in the lockset
							for( EquivalentValue lockEqVal : tn.lockset )
							{
								Value lock = lockEqVal.getValue();
								
								// Get reaching objects for this lock
								PointsToSetInternal lockPT;
								if(lock instanceof Local)
									lockPT = (PointsToSetInternal) pta.reachingObjects((Local) lock);
								else if(lock instanceof StaticFieldRef) // needs special treatment: could be primitive
									lockPT = null;
								else if(lock instanceof InstanceFieldRef)
								{
									Local base = (Local) ((InstanceFieldRef) lock).getBase();
									if(base instanceof FakeJimpleLocal)
										lockPT = (PointsToSetInternal) pta.reachingObjects(((FakeJimpleLocal)base).getRealLocal(), ((FieldRef) lock).getField());
									else
										lockPT = (PointsToSetInternal) pta.reachingObjects(base, ((FieldRef) lock).getField());
								}
								else if(lock instanceof NewStaticLock) // placeholder for anything that needs a static lock
									lockPT = null;
								else
									lockPT = null;
									
								if( lockPT != null )
								{
									// Assign an existing lock number if possible
									boolean foundLock = false;
									for(int i = 0; i < lockPTSets.size(); i++)
									{
										PointsToSetInternal otherLockPT = lockPTSets.get(i);
										if(lockPT.hasNonEmptyIntersection(otherLockPT)) // will never happen for empty, negative numbered sets
										{
											G.v().out.println("[wjtp.tn] Lock: num " + i + " type " + lock.getType() + " obj " + lock);
											lockToLockNum.put(lock, new Integer(i));
											otherLockPT.addAll(lockPT, null);
											foundLock = true;
											break;
										}
									}
									
									// Assign a brand new lock number otherwise
									if(!foundLock)
									{
										G.v().out.println("[wjtp.tn] Lock: num " + lockPTSets.size() + " type " + lock.getType() + " obj " + lock);
										lockToLockNum.put(lock, new Integer(lockPTSets.size()));
										PointsToSetInternal otherLockPT = new HashPointsToSet(lockPT.getType(), (PAG) pta);
										lockPTSets.add(otherLockPT);
										otherLockPT.addAll(lockPT, null);
									}
								}
								else // static field locks and pathological cases...
								{
									// Assign an existing lock number if possible
									if( lockToLockNum.get(lockEqVal) != null )
									{
										Integer lockNum = lockToLockNum.get(lockEqVal);
										G.v().out.println("[wjtp.tn] Lock: num " + lockNum + " type " + lock.getType() + " obj " + lock);
										lockToLockNum.put(lock, lockNum);
									}
									else
									{
										Integer lockNum = new Integer(-lockPTSets.size()); // negative indicates a static lock
										G.v().out.println("[wjtp.tn] Lock: num " + lockNum + " type " + lock.getType() + " obj " + lock);
										lockToLockNum.put(lockEqVal, lockNum);
										lockToLockNum.put(lock, lockNum);
										PointsToSetInternal dummyLockPT = new HashPointsToSet(lock.getType(), (PAG) pta);
										lockPTSets.add(dummyLockPT);
									}
								}
							}

						}
					}
					else
					{
						if(tn.lockset == null || tn.lockset.size() != 1)
						{// Found too few or too many locks
							// So use a static lock instead
							tn.lockObject = null;
							tn.group.useDynamicLock = false;
							tn.group.lockObject = null;
						}
						else
						{// Found exactly one lock
							// Use it!
							tn.lockObject = (Value) tn.lockset.get(0);
							
							// If it's the best lock we've found in the group yet, use it for display
							if(tn.group.lockObject == null || tn.lockObject instanceof Ref)
								tn.group.lockObject = tn.lockObject;
						}
					}
				}
			}
			if(optionUseLocksets)
			{
				// If any lock has only a singleton reaching object, treat it like a static lock
				for(int i = 0; i < lockPTSets.size(); i++)
				{
					PointsToSetInternal pts = lockPTSets.get(i);
					if(pts.size() == 1 && false) // isSingleton(pts)) // It's NOT easy to find a singleton: single alloc node must be run just once
					{
						for(Object e : lockToLockNum.entrySet())
						{
							Map.Entry entry = (Map.Entry) e;
							Integer value = (Integer) entry.getValue();
							if(value == i)
							{
								entry.setValue(new Integer(-i));
							}
						}
					}
				}
			}
		}
		
		// print out locksets
		if(optionUseLocksets)
		{
			for( Transaction tn : AllTransactions )
			{
				if( tn.group != null )
				{
					G.v().out.println("[wjtp.tn] " + tn.name + " lockset: " + locksetToLockNumString(tn.lockset, lockToLockNum) + (tn.group.useLocksets ? "" : " (placeholder)"));
				}
			}
		}
		
		
		// *** Detect the Possibility of Deadlock for Locksets ***
		MutableEdgeLabelledDirectedGraph permanentOrder = new HashMutableEdgeLabelledDirectedGraph();
		MutableEdgeLabelledDirectedGraph lockOrder = null;
		if(optionUseLocksets) // deadlock detection and lock ordering for lockset allocations
		{
			boolean foundDeadlock;
			do
			{
				G.v().out.println("[wjtp.tn] *** Detect " + (optionAvoidDeadlock ? "and Correct " : "") + "the Possibility of Deadlock for Locksets *** " + (new Date()));
				foundDeadlock = false;
		    	lockOrder = (HashMutableEdgeLabelledDirectedGraph) ((HashMutableEdgeLabelledDirectedGraph) permanentOrder).clone(); // start each iteration with a fresh copy of the permanent orders
				
				// Assemble the partial ordering of locks
		    	Iterator<Transaction> deadlockIt1 = AllTransactions.iterator();
		    	while(deadlockIt1.hasNext() && !foundDeadlock)
		    	{
		    		Transaction tn1 = deadlockIt1.next();
		    		
		    		// skip if unlocked
		    		if( tn1.group == null )
		    			continue;
		    			
		    		// add a node for each lock in this lockset
					for( EquivalentValue lockEqVal : tn1.lockset )
					{
						Value lock = lockEqVal.getValue();
		    		
		    			if( !lockOrder.containsNode(lockToLockNum.get(lock)) )
		    				lockOrder.addNode(lockToLockNum.get(lock));
		    		}
		    			
		    		// Get list of tn1's target methods
		    		if(tn1.transitiveTargets == null)
		    		{
			    		tn1.transitiveTargets = new HashSet<MethodOrMethodContext>();
			    		Iterator<Object> tn1InvokeIt = tn1.invokes.iterator();
			    		while(tn1InvokeIt.hasNext())
			    		{
			    			Unit tn1Invoke = (Unit) tn1InvokeIt.next();
			    			Iterator<MethodOrMethodContext> targetIt = tt.iterator(tn1Invoke);
			    			while(targetIt.hasNext())
			    				tn1.transitiveTargets.add(targetIt.next());
			    		}
			    	}
		    		
		    		// compare to each other tn
		    		Iterator<Transaction> deadlockIt2 = AllTransactions.iterator();
		    		while(deadlockIt2.hasNext() && !foundDeadlock)
		    		{
		    			Transaction tn2 = deadlockIt2.next();
		    			
		    			// skip if unlocked
		    			if( tn2.group == null )
		    				continue;
		    			
		    			// add a node for each lock in this lockset
		    			for( EquivalentValue lockEqVal : tn2.lockset )
						{
							Value lock = lockEqVal.getValue();
				    		
				    		if( !lockOrder.containsNode(lockToLockNum.get(lock)) )
				    			lockOrder.addNode(lockToLockNum.get(lock));
				    	}
				    				    			
			    		if( tn1.transitiveTargets.contains(tn2.method) && !foundDeadlock )
			    		{
			    			// This implies the partial ordering (locks in tn1) before (locks in tn2)
			    			if(true) //optionPrintDebug)
			    			{
				    			G.v().out.println("locks in " + (tn1.name) + " before locks in " + (tn2.name) + ": " +
				    				"outer: " + tn1.name + " inner: " + tn2.name);
				    		}
			    			
			    			// Check if tn2locks before tn1locks is in our lock order
							for( EquivalentValue lock2EqVal : tn2.lockset )
							{
								Value lock2 = lock2EqVal.getValue();
								Integer lock2Num = lockToLockNum.get(lock2);

				    			List afterTn2 = new ArrayList();
								afterTn2.addAll( lockOrder.getSuccsOf(lock2Num) ); // filter here!
								ListIterator lit = afterTn2.listIterator();
								while(lit.hasNext())
								{
									Integer to = (Integer) lit.next(); // node the edges go to
									List labels = lockOrder.getLabelsForEdges(lock2Num, to);
									boolean keep = false;
									if(labels != null) // this shouldn't really happen... is something wrong with the edge-labelled graph?
									{
										for(Object l : labels)
										{
											Transaction labelTn = (Transaction) l;
											
											// Check if labelTn and tn1 share a static lock
											boolean tnsShareAStaticLock = false;
											for( EquivalentValue tn1LockEqVal : tn1.lockset )
											{
												Integer tn1LockNum = lockToLockNum.get(tn1LockEqVal.getValue());
												if(tn1LockNum < 0)
												{
													// this is a static lock... see if some lock in labelTn has the same #
													for( EquivalentValue labelTnLockEqVal : labelTn.lockset )
													{
														if(lockToLockNum.get(labelTnLockEqVal.getValue()) == tn1LockNum)
														{
															tnsShareAStaticLock = true;
														}
													}
												}
											}
											
											if(!tnsShareAStaticLock) // !hasStaticLockInCommon(tn1, labelTn))
											{
												keep = true;
												break;
											}
										}
									}
									if(!keep)
										lit.remove();
								}

/*				    			for( int i = 0; i < afterTn2.size(); i++ )
				    			{
				    				List succs = lockOrder.getSuccsOf(afterTn2.get(i)); // but not here
				    				for( Object o : succs )
				    				{
				    					if(!afterTn2.contains(o))
						    				afterTn2.add(o);
					    			}
				    			}
*/
			    				
								for( EquivalentValue lock1EqVal : tn1.lockset )
								{
									Value lock1 = lock1EqVal.getValue();
									Integer lock1Num = lockToLockNum.get(lock1);
									
						    		if( ( lock1Num != lock2Num || 
						    			  lock1Num > 0 ) &&
							    		  afterTn2.contains(lock1Num) )
					    			{
					    				if(!optionAvoidDeadlock)
					    				{
						    				G.v().out.println("[wjtp.tn] DEADLOCK HAS BEEN DETECTED: not correcting");
											foundDeadlock = true;
						    			}
						    			else
						    			{
						    				G.v().out.println("[wjtp.tn] DEADLOCK HAS BEEN DETECTED while inspecting " + lock1Num + " ("+lock1+") and " + lock2Num + " ("+lock2+") ");
			    					
											// Create a deadlock avoidance edge
											DeadlockAvoidanceEdge dae = new DeadlockAvoidanceEdge(tn1.method.getDeclaringClass());
											EquivalentValue daeEqVal = new EquivalentValue(dae);
											
											// Register it as a static lock
											Integer daeNum = new Integer(-lockPTSets.size()); // negative indicates a static lock
											permanentOrder.addNode(daeNum);
											lockToLockNum.put(dae, daeNum);
											PointsToSetInternal dummyLockPT = new HashPointsToSet(lock1.getType(), (PAG) pta);
											lockPTSets.add(dummyLockPT);

											// Add it to the locksets of tn1 and whoever says l2 before l1
											for(EquivalentValue lockEqVal : tn1.lockset)
											{
												Integer lockNum = lockToLockNum.get(lockEqVal.getValue());
												if(!permanentOrder.containsNode(lockNum))
													permanentOrder.addNode(lockNum);
												permanentOrder.addEdge(daeNum, lockNum, tn1);
											}
											tn1.lockset.add(daeEqVal);

											List forwardLabels = lockOrder.getLabelsForEdges(lock1Num, lock2Num);
											if(forwardLabels != null)
											{
												for(Object t : forwardLabels)
												{
													Transaction tn = (Transaction) t;
													if(!tn.lockset.contains(daeEqVal))
													{
														for(EquivalentValue lockEqVal : tn.lockset)
														{
															Integer lockNum = lockToLockNum.get(lockEqVal.getValue());
															if(!permanentOrder.containsNode(lockNum))
																permanentOrder.addNode(lockNum);
															permanentOrder.addEdge(daeNum, lockNum, tn);
														}
														tn.lockset.add(daeEqVal);
													}
												}
											}
											
											List backwardLabels = lockOrder.getLabelsForEdges(lock2Num, lock1Num);
											if(backwardLabels != null)
											{
												for(Object t : backwardLabels)
												{
													Transaction tn = (Transaction) t;
													if(!tn.lockset.contains(daeEqVal))
													{
														for(EquivalentValue lockEqVal : tn.lockset)
														{
															Integer lockNum = lockToLockNum.get(lockEqVal.getValue());
															if(!permanentOrder.containsNode(lockNum))
																permanentOrder.addNode(lockNum);
															permanentOrder.addEdge(daeNum, lockNum, tn);
														}
														tn.lockset.add(daeEqVal);
							    						G.v().out.println("[wjtp.tn]   Adding deadlock avoidance edge between " +
							    							(tn1.name) + " and " + (tn.name));
							    					}
												}
												G.v().out.println("[wjtp.tn]   Restarting deadlock detection");
											}
											
											foundDeadlock = true;
											break;
						    			}
					    			}
					    			
					    			if(lock1Num != lock2Num)
										lockOrder.addEdge(lock1Num, lock2Num, tn1);
					    		}
					    		if(foundDeadlock)
					    			break;
					    	}
				    	}
		    		}
		    	}
			} while(foundDeadlock && optionAvoidDeadlock);
			((HashMutableEdgeLabelledDirectedGraph) lockOrder).printGraph();
		
			G.v().out.println("[wjtp.tn] *** Reorder Locksets to Avoid Deadlock *** " + (new Date()));
			for(Transaction tn : AllTransactions)
			{
				// Get the portion of the lock order that is visible to tn
				HashMutableDirectedGraph visibleOrder = new HashMutableDirectedGraph();
				if(tn.group != null)
				{
					for(Transaction otherTn : AllTransactions)
					{
						// Check if otherTn and tn share a static lock
						boolean tnsShareAStaticLock = false;
						for( EquivalentValue tnLockEqVal : tn.lockset )
						{
							Integer tnLockNum = lockToLockNum.get(tnLockEqVal.getValue());
							if(tnLockNum < 0)
							{
								// this is a static lock... see if some lock in labelTn has the same #
								if(otherTn.group != null)
								{
									for( EquivalentValue otherTnLockEqVal : otherTn.lockset )
									{
										if(lockToLockNum.get(otherTnLockEqVal.getValue()) == tnLockNum)
										{
											tnsShareAStaticLock = true;
										}
									}
								}
								else
									tnsShareAStaticLock = true; // not really... but we want to skip this one
							}
						}
						
						if(!tnsShareAStaticLock || tn == otherTn) // if tns don't share any static lock, or if tns are the same one
						{
							// add these orderings to tn's visible order
							MutableDirectedGraph orderings = lockOrder.getEdgesForLabel(otherTn);
							for(Object node1 : orderings.getNodes())
							{
								if(!visibleOrder.containsNode(node1))
									visibleOrder.addNode(node1);
								for(Object node2 : orderings.getSuccsOf(node1))
								{
									if(!visibleOrder.containsNode(node2))
										visibleOrder.addNode(node2);
									visibleOrder.addEdge(node1, node2);
								}
							}
						}
					}

					G.v().out.println("VISIBLE ORDER FOR " + tn.name);
					visibleOrder.printGraph();
				
					// Order locks in tn's lockset according to the visible order (insertion sort)
					List<EquivalentValue> newLockset = new ArrayList();
					for(EquivalentValue lockEqVal : tn.lockset)
					{
						Value lockToInsert = lockEqVal.getValue();
						Integer lockNumToInsert = lockToLockNum.get(lockToInsert);
						int i = 0;
						while( i < newLockset.size() )
						{
							EquivalentValue existingLockEqVal = newLockset.get(i);
							Value existingLock = existingLockEqVal.getValue();
							Integer existingLockNum = lockToLockNum.get(existingLock);
							if( visibleOrder.containsEdge(lockNumToInsert, existingLockNum) ||
								lockNumToInsert < existingLockNum )
	//							!visibleOrder.containsEdge(existingLockNum, lockNumToInsert) ) // if(! existing before toinsert )
								break;
							i++;
						}
						newLockset.add(i, lockEqVal);
					}
					G.v().out.println("reordered from " + locksetToLockNumString(tn.lockset, lockToLockNum) +
									" to " + locksetToLockNumString(newLockset, lockToLockNum));

					tn.lockset = newLockset;
				}
			}
		}
		
		// *** Print Output and Transform Program ***
    	G.v().out.println("[wjtp.tn] *** Print Output and Transform Program *** " + (new Date()));

		// Print topological graph in graphviz format
		if(optionPrintGraph)
		{
			printGraph(AllTransactions, groups, lockToLockNum);
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
				TransactionGroup tnGroup = groups.get(i);
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
	    	    	
	    		    	FlowSet fs = methodToFlowSet.get(method);
	    	    	
	    	    		if(fs == null) // newly added methods have no flowset
	    	    			continue;

	   	    			TransactionBodyTransformer.v().internalTransform(b, fs, groups); 
					}
	    	    }
	    	}
	    }
	}
	
	public String locksetToLockNumString(List<EquivalentValue> lockset, Map<Value, Integer> lockToLockNum)
	{
		if( lockset == null ) return "null";
		String ret = "[";
		boolean first = true;
		for( EquivalentValue lockEqVal : lockset )
		{
			if(!first)
				ret = ret + " ";
			first = false;
			ret = ret + lockToLockNum.get(lockEqVal.getValue());
		}
		return ret + "]";
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
    
    public void assignNamesToTransactions(List<Transaction> AllTransactions)
    {
       	// Give each method a unique, deterministic identifier
       	// Sort transactions into bins... one for each method name
       	
       	// Get list of method names
    	List<String> methodNamesTemp = new ArrayList<String>();
    	Iterator<Transaction> tnIt5 = AllTransactions.iterator();
    	while (tnIt5.hasNext()) 
    	{
    	    Transaction tn1 = tnIt5.next();
    	    String mname = tn1.method.getSignature(); //tn1.method.getSignature() + "." + tn1.method.getName();
    	    if(!methodNamesTemp.contains(mname))
    	    	methodNamesTemp.add(mname);
		}
		String methodNames[] = new String[1];
		methodNames = methodNamesTemp.toArray(methodNames);
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
    	Iterator<Transaction> tnIt0 = AllTransactions.iterator();
    	while(tnIt0.hasNext())
    	{
    		Transaction tn1 = tnIt0.next();
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
    	Iterator<Transaction> tnIt4 = AllTransactions.iterator();
    	while(tnIt4.hasNext())
    	{
    		Transaction tn1 = tnIt4.next();
			int methodNum = Arrays.binarySearch(methodNames, tn1.method.getSignature());
			int tnNum = Arrays.binarySearch(identMatrix[methodNum], tn1.IDNum) - 1;
    		tn1.name = "m" + (methodNum < 10? "00" : (methodNum < 100? "0" : "")) + methodNum + "n" + (tnNum < 10? "0" : "") + tnNum;
    	}
	}	

	public void printGraph(Collection<Transaction> AllTransactions, List<TransactionGroup> groups, Map<Value, Integer> lockToLockNum)
	{
		final String[] colors = {"black", "blue", "blueviolet", "chartreuse", "crimson", "darkgoldenrod1", "darkseagreen", "darkslategray", "deeppink",
			"deepskyblue1", "firebrick1", "forestgreen", "gold", "gray80", "navy", "pink", "red", "sienna", "turquoise1", "yellow"};
		Map<Integer, String> lockColors = new HashMap<Integer, String>();
		int colorNum = 0;
		HashSet<Transaction> visited = new HashSet<Transaction>();
		
		G.v().out.println("[transaction-graph]" + (optionUseLocksets ? "" : " strict") + " graph transactions {"); // "\n[transaction-graph] start=1;");

		for(int group = 0; group < groups.size(); group++)
		{
			boolean printedHeading = false;
			Iterator<Transaction> tnIt = AllTransactions.iterator();
			while(tnIt.hasNext())
			{
				Transaction tn = tnIt.next();
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
						else if(tn.group.useLocksets)
						{
							G.v().out.println("[transaction-graph] subgraph cluster_" + (group + 1) + " {\n[transaction-graph] color=blue;\n[transaction-graph] label=\"Locksets\";");
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
						G.v().out.println("[transaction-graph] " + tn.name + " [name=\"" + tn.method.toString() + "\" style=\"setlinewidth(3)\"];");
					else
						G.v().out.println("[transaction-graph] " + tn.name + " [name=\"" + tn.method.toString() + "\" color=cadetblue1 style=\"setlinewidth(1)\"];");

					if(tn.group.useLocksets) // print locks instead of dependence edges
					{
						for(EquivalentValue lockEqVal : tn.lockset)
						{
							Integer lockNum = lockToLockNum.get(lockEqVal.getValue());
							for(Transaction tn2 : tn.group)
							{
								if(!visited.contains(tn2) && mayHappenInParallel(tn, tn2))
								{
									for(EquivalentValue lock2EqVal : tn2.lockset)
									{
										Integer lock2Num = lockToLockNum.get(lock2EqVal.getValue());
										if(lockNum.intValue() == lock2Num.intValue())
										{
											// Get the color for this lock
											if(!lockColors.containsKey(lockNum))
											{
												lockColors.put(lockNum, colors[colorNum % colors.length]);
												colorNum++;
											}
											String color = lockColors.get(lockNum);

											// Draw an edge for this lock
											G.v().out.println("[transaction-graph] " + tn.name + " -- " + tn2.name + " [color=" + color + " style=" + (lockNum >= 0 ? "dashed" : "solid") + " exactsize=1 style=\"setlinewidth(3)\"];");
										}
									}
								}
							}
							visited.add(tn);
						}
					}
					else
					{
						Iterator<TransactionDataDependency> tnedgeit = tn.edges.iterator();
						while(tnedgeit.hasNext())
						{
							TransactionDataDependency edge = tnedgeit.next();
							Transaction tnedge = edge.other;
							if(tnedge.setNumber == group + 1)
								G.v().out.println("[transaction-graph] " + tn.name + " -- " + tnedge.name + " [color=" + (edge.size > 0 ? "black" : "cadetblue1") + " style=" + (tn.setNumber > 0 && tn.group.useDynamicLock ? "dashed" : "solid") + " exactsize=" + edge.size + " style=\"setlinewidth(3)\"];");
						}
					}
				}
				
			}
			if(printedHeading)
				G.v().out.println("[transaction-graph] }");
		}
		
		// Print nodes with no group
		{
			boolean printedHeading = false;
			Iterator<Transaction> tnIt = AllTransactions.iterator();
			while(tnIt.hasNext())
			{
				Transaction tn = tnIt.next();
				if(tn.setNumber == -1)
				{
					if(!printedHeading)
					{
						// putting these nodes in a "source" ranked subgraph makes them appear above all the clusters
						G.v().out.println("[transaction-graph] subgraph lone {\n[transaction-graph] rank=source;");
						printedHeading = true;
					}
					if(Scene.v().getReachableMethods().contains(tn.method))
						G.v().out.println("[transaction-graph] " + tn.name + " [name=\"" + tn.method.toString() + "\" style=\"setlinewidth(3)\"];");
					else
						G.v().out.println("[transaction-graph] " + tn.name + " [name=\"" + tn.method.toString() + "\" color=cadetblue1 style=\"setlinewidth(1)\"];");

					Iterator<TransactionDataDependency> tnedgeit = tn.edges.iterator();
					while(tnedgeit.hasNext())
					{
						TransactionDataDependency edge = tnedgeit.next();
						Transaction tnedge = edge.other;
						if(tnedge.setNumber != tn.setNumber || tnedge.setNumber == -1)
							G.v().out.println("[transaction-graph] " + tn.name + " -- " + tnedge.name + " [color=" + (edge.size > 0 ? "black" : "cadetblue1") + " style=" + (tn.setNumber > 0 && tn.group.useDynamicLock ? "dashed" : "solid") + " exactsize=" + edge.size + " style=\"setlinewidth(1)\"];");
					}
				}
			}
			if(printedHeading)
				G.v().out.println("[transaction-graph] }");
		}

		G.v().out.println("[transaction-graph] }");
	}	

	public void printTable(Collection<Transaction> AllTransactions)
	{
		G.v().out.println("[transaction-table] ");
		Iterator<Transaction> tnIt7 = AllTransactions.iterator();
		while(tnIt7.hasNext())
		{
			Transaction tn = tnIt7.next();
			
			// Figure out if it's reachable, and if it MHP itself
			boolean reachable = false;
			boolean mhpself = false;
			{
	    		ReachableMethods rm = Scene.v().getReachableMethods();
	    		reachable = rm.contains(tn.method);
		    	if(mhp != null)
		    		mhpself = mhp.mayHappenInParallel(tn.method, tn.method);
		    }
			G.v().out.println("[transaction-table] Transaction " + tn.name + (reachable ? " reachable" : " dead") + (mhpself ? " [called from >= 2 threads]" : " [called from <= 1 thread]"));
			G.v().out.println("[transaction-table] Where: " + tn.method.getDeclaringClass().toString() + ":" + tn.method.toString() + ":  ");
			G.v().out.println("[transaction-table] Orig : " + tn.origLock);
			G.v().out.println("[transaction-table] Prep : " + tn.prepStmt);
			G.v().out.println("[transaction-table] Begin: " + tn.entermonitor);
			G.v().out.print("[transaction-table] End  : early:" + tn.earlyEnds.toString() + " exc:" + tn.exceptionalEnd + " through:" + tn.end + " \n");
			G.v().out.println("[transaction-table] Size : " + tn.units.size());
			if(tn.read.size() < 100)
				G.v().out.print("[transaction-table] Read : " + tn.read.size() + "\n[transaction-table] " + 
					tn.read.toString().replaceAll("\\[", "     : [").replaceAll("\n", "\n[transaction-table] "));
			else
				G.v().out.print("[transaction-table] Read : " + tn.read.size() + "  \n[transaction-table] ");
			if(tn.write.size() < 100)
				G.v().out.print("Write: " + tn.write.size() + "\n[transaction-table] " + 
					tn.write.toString().replaceAll("\\[", "     : [").replaceAll("\n", "\n[transaction-table] ")); // label provided by previous print statement
			else
				G.v().out.print("Write: " + tn.write.size() + "\n[transaction-table] "); // label provided by previous print statement
			G.v().out.print("Edges: (" + tn.edges.size() + ") "); // label provided by previous print statement
			Iterator<TransactionDataDependency> tnedgeit = tn.edges.iterator();
			while(tnedgeit.hasNext())
				G.v().out.print(tnedgeit.next().other.name + " ");
			if(tn.group != null && tn.group.useLocksets)
			{
				G.v().out.println("\n[transaction-table] Locks: " + tn.lockset);
				
			}
			else
				G.v().out.println("\n[transaction-table] Lock : " + (tn.setNumber == -1 ? "-" : (tn.lockObject == null ? "Global" : (tn.lockObject.toString() + (tn.lockObjectArrayIndex == null ? "" : "[" + tn.lockObjectArrayIndex + "]")) )));
			G.v().out.println("[transaction-table] Group: " + tn.setNumber + "\n[transaction-table] ");
		}
	}
	
	public void printGroups(Collection<Transaction> AllTransactions, int nextGroup, List<TransactionGroup> groups, RWSet rws[])
	{
			G.v().out.print("[transaction-groups] Group Summaries\n[transaction-groups] ");
			for(int group = 0; group < nextGroup - 1; group++)
    		{
    			TransactionGroup tnGroup = groups.get(group + 1);
    			if(tnGroup.size() > 0)
    			{
	    			G.v().out.print("Group " + (group + 1) + " ");
					G.v().out.print("Locking: " + (tnGroup.useLocksets ? "using " : (tnGroup.isDynamicLock && tnGroup.useDynamicLock ? "Dynamic on " : "Static on ")) + (tnGroup.useLocksets ? "locksets" : (tnGroup.lockObject == null ? "null" : tnGroup.lockObject.toString())) );
					G.v().out.print("\n[transaction-groups]      : ");
					Iterator<Transaction> tnIt = AllTransactions.iterator();
					while(tnIt.hasNext())
					{
						Transaction tn = tnIt.next();
						if(tn.setNumber == group + 1)
							G.v().out.print(tn.name + " ");
					}
					G.v().out.print("\n[transaction-groups] " + 
	    							rws[group].toString().replaceAll("\\[", "     : [").replaceAll("\n", "\n[transaction-groups] "));
	    		}
	    	}
			G.v().out.print("Erasing \n[transaction-groups]      : ");
			Iterator<Transaction> tnIt = AllTransactions.iterator();
			while(tnIt.hasNext())
			{
				Transaction tn = tnIt.next();
				if(tn.setNumber == -1)
					G.v().out.print(tn.name + " ");
			}
			G.v().out.println("\n[transaction-groups] ");
	}
}
