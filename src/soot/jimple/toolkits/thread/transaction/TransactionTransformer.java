package soot.jimple.toolkits.thread.transaction;

import java.util.*;

import soot.*;
import soot.util.Chain;
import soot.jimple.*;
import soot.jimple.toolkits.pointer.*;
import soot.jimple.toolkits.thread.ThreadLocalObjectsAnalysis;
import soot.jimple.toolkits.thread.mhp.UnsynchronizedMhpAnalysis;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.infoflow.ClassInfoFlowAnalysis;
import soot.jimple.toolkits.infoflow.SmartMethodInfoFlowAnalysis;
import soot.jimple.toolkits.infoflow.SmartMethodLocalObjectsAnalysis;
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
    	Iterator<Transaction> tnIt = AllTransactions.iterator();
    	while(tnIt.hasNext())
    	{
    		Transaction tn = tnIt.next();
//			Body b = tn.method.retrieveActiveBody();
//			UnitGraph g = new ExceptionalUnitGraph(b);
			UnitGraph g = methodToExcUnitGraph.get(tn.method);
			LocalDefs sld = new SmartLocalDefs(g, new SimpleLiveLocals(g));
    		Iterator<Object> invokeIt = tn.invokes.iterator();
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
		MutableDirectedGraph lockOrder;
		TransitiveTargets tt = new TransitiveTargets(Scene.v().getCallGraph(), new Filter(new TransactionVisibleEdgesPred(null)));
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
			for(int group = 0; group < nextGroup - 1; group++)
			{
				TransactionGroup tnGroup = groups.get(group + 1);
				tnGroup.isDynamicLock = false; // actually, unknown, so not necessary to set it
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
				TransactionGroup tnGroup = groups.get(group + 1);
				tnGroup.isDynamicLock = false; // actually, unknown, so not necessary to set it
				tnGroup.useDynamicLock = false;
				tnGroup.lockObject = null;
			}
			
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
		if(!optionLeaveOriginalLocks)
		{
			// Data structures for determining lock numbers
			List<PointsToSetInternal> lockPTSets = new ArrayList<PointsToSetInternal>();
			Map<Value, Integer> lockToLockNum = new HashMap<Value, Integer>();

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
					// Get a list of contributing uses
					Map<Stmt, List<Value>> unitToUses = new HashMap<Stmt, List<Value>>();
					Iterator entryIt = tn.unitToRWSet.entrySet().iterator();
					while(entryIt.hasNext())
					{
						Map.Entry e = (Map.Entry) entryIt.next();
						RWSet rw = (RWSet) e.getValue();
						if(rw.hasNonEmptyIntersection(rws[group]))
						{
							// this is a contributing unit
							Unit u = (Unit) e.getKey();
							Stmt s = (Stmt) u;
														
							// figure out which "lock number" it is
							// this really ought to be done on a per-use basis, not per-unit
							
							// Get list of contributing uses from this unit
							List allUses = tn.unitToUses.get(s);
							List<Value> contributingUses = new ArrayList<Value>();								
							for(Iterator usesIt = allUses.iterator(); usesIt.hasNext(); )
							{
								Value vEqVal = (Value) usesIt.next();
								Value v = ((vEqVal instanceof EquivalentValue) ? ((EquivalentValue) vEqVal).getValue() : vEqVal);
								
								if(s.containsFieldRef())
								{
									FieldRef fr = s.getFieldRef();
									if(fr instanceof InstanceFieldRef)
									{
										if(((InstanceFieldRef) fr).getBase() == v)
											v = fr;
									}
								}
								RWSet valRW = tasea.valueRWSet(v, tn.method, s);
//								G.v().out.println("v: " + v);
//								G.v().out.println("RW: " + valRW + "groupRW: " + rws[group]);
								if(	valRW != null && valRW.hasNonEmptyIntersection(rws[group]) )
								{
//									G.v().out.print("CONTRIBUTES!\n\n");
									contributingUses.add(vEqVal);
									
								}
//								else
//									G.v().out.println("DOESN'T contribute!\n\n");
							}
							unitToUses.put(s, contributingUses);
						}
					}
					
					// Get list of objects (FieldRef or Local) to be locked (lockset analysis)
//					if(optionUseLocksets) G.v().out.println("lockset for " + tn.name + " w/ " + unitToUses + " is:");
					LocksetAnalysis la = new LocksetAnalysis(new BriefUnitGraph(tn.method.retrieveActiveBody()));
					tn.lockset = la.getLocksetOf(unitToUses, tn.beginning);
					
					// Determine if list is suitable for the selected locking scheme
					// TODO check for nullness
					if(optionUseLocksets)
					{
//						G.v().out.println("  " + (tn.lockset == null ? "FAILURE" : tn.lockset.toString()));
						if(tn.lockset == null)
							tn.group.useLocksets = false;
						else
						{
							// Figure out the lock number for each lock
							for( EquivalentValue lockEqVal : tn.lockset )
							{
								Value lock = lockEqVal.getValue();
								
								// Get reaching objects for this lock
								PointsToSetInternal lockPT;
								if(lock instanceof Local)
									lockPT = (PointsToSetInternal) pta.reachingObjects((Local) lock);
								else if(lock instanceof StaticFieldRef)
									lockPT = (PointsToSetInternal) pta.reachingObjects(((FieldRef) lock).getField());
								else if(lock instanceof InstanceFieldRef)
									lockPT = (PointsToSetInternal) pta.reachingObjects((Local) ((InstanceFieldRef) lock).getBase(), ((FieldRef) lock).getField());
								else
									lockPT = null;
									
								// Assign an existing lock number if possible
								boolean foundLock = false;
								for(int i = 0; i < lockPTSets.size(); i++)
								{
									PointsToSetInternal otherLockPT = lockPTSets.get(i);
									if(lockPT.hasNonEmptyIntersection(otherLockPT))
									{
										G.v().out.println("Lock: " + lock + " num: " + i);
										lockToLockNum.put(lock, new Integer(i));
										otherLockPT.addAll(lockPT, null);
										foundLock = true;
										break;
									}
								}
								
								// Assign a brand new lock number otherwise
								if(!foundLock)
								{
									G.v().out.println("Lock: " + lock + " num: " + lockPTSets.size());
									lockToLockNum.put(lock, new Integer(lockPTSets.size()));
									PointsToSetInternal otherLockPT = new HashPointsToSet(lockPT.getType(), (PAG) pta);
									lockPTSets.add(otherLockPT);
									otherLockPT.addAll(lockPT, null);
								}
							}
						}
					}
					else
					{
						if(tn.lockset == null || tn.lockset.size() != 1)
						{
							tn.lockObject = null;
							tn.group.useDynamicLock = false;
							tn.group.lockObject = null;
						}
						else
						{
							tn.lockObject = (Value) tn.lockset.get(0);
							if(tn.group.lockObject == null || tn.lockObject instanceof Ref)
								tn.group.lockObject = tn.lockObject; // just for display
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

	public void printGraph(Collection<Transaction> AllTransactions, List<TransactionGroup> groups)
	{
		G.v().out.println("[transaction-graph] strict graph transactions {\n[transaction-graph] start=1;");

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

					Iterator<TransactionDataDependency> tnedgeit = tn.edges.iterator();
					while(tnedgeit.hasNext())
					{
						TransactionDataDependency edge = tnedgeit.next();
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
			Iterator<Transaction> tnIt = AllTransactions.iterator();
			while(tnIt.hasNext())
			{
				Transaction tn = tnIt.next();
				if(tn.setNumber == -1)
				{
					if(Scene.v().getReachableMethods().contains(tn.method))
						G.v().out.println("[transaction-graph] " + tn.name + " [name=\"" + tn.method.toString() + "\"];");
					else
						G.v().out.println("[transaction-graph] " + tn.name + " [name=\"" + tn.method.toString() + "\" color=cadetblue1];");
				
					Iterator<TransactionDataDependency> tnedgeit = tn.edges.iterator();
					while(tnedgeit.hasNext())
					{
						TransactionDataDependency edge = tnedgeit.next();
						Transaction tnedge = edge.other;
						if(tnedge.setNumber != tn.setNumber || tnedge.setNumber == -1)
							G.v().out.println("[transaction-graph] " + tn.name + " -- " + tnedge.name + " [color=" + (edge.size > 0 ? "black" : "cadetblue1") + " style=" + (tn.setNumber > 0 && tn.group.useDynamicLock ? "dashed" : "solid") + " exactsize=" + edge.size + "];");
					}
				}
			}
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
			G.v().out.println("[transaction-table] Transaction " + tn.name);
			G.v().out.println("[transaction-table] Where: " + tn.method.getDeclaringClass().toString() + ":" + tn.method.toString() + ":  ");
			G.v().out.println("[transaction-table] Orig : " + tn.origLock);
			G.v().out.println("[transaction-table] Prep : " + tn.prepStmt);
			G.v().out.println("[transaction-table] Begin: " + tn.entermonitor);
			G.v().out.print("[transaction-table] End  : early:" + tn.earlyEnds.toString() + " exc:" + tn.exceptionalEnd + " through:" + tn.end + " \n");
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
    							rws[group].toString().replaceAll("\\[", "     : [").replaceAll("\n", "\n[transaction-groups] ") + 
								(rws[group].size() == 0 ? "\n[transaction-groups] " : ""));
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
