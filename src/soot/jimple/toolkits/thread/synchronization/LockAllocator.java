package soot.jimple.toolkits.thread.synchronization;

import java.util.*;

import soot.*;
import soot.util.Chain;
import soot.jimple.*;
import soot.jimple.toolkits.pointer.*;
import soot.jimple.toolkits.thread.ThreadLocalObjectsAnalysis;
import soot.jimple.toolkits.thread.mhp.MhpTester;
import soot.jimple.toolkits.thread.mhp.SynchObliviousMhpAnalysis;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.infoflow.*;
import soot.jimple.spark.pag.*;
import soot.jimple.spark.sets.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;

public class LockAllocator extends SceneTransformer
{
    public LockAllocator(Singletons.Global g){}
    public static LockAllocator v() 
	{ 
		return G.v().soot_jimple_toolkits_thread_synchronization_LockAllocator();
	}
    
    List<CriticalSection> criticalSections = null;
    CriticalSectionInterferenceGraph interferenceGraph = null;
    DirectedGraph deadlockGraph = null;
	
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
	
    protected void internalTransform(String phaseName, Map<String,String> options)
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
		MhpTester mhp = null;
		if(optionDoMHP && Scene.v().getPointsToAnalysis() instanceof PAG)
		{
	    	G.v().out.println("[wjtp.tn] *** Build May-Happen-in-Parallel Info *** " + (new Date()));
			mhp = new SynchObliviousMhpAnalysis();
			if(optionPrintMhpSummary)
			{
				mhp.printMhpSummary();
			}
		}
		


		// *** Find Thread-Local Objects ***
		ThreadLocalObjectsAnalysis tlo = null;
    	if(optionDoTLO)
    	{
	    	G.v().out.println("[wjtp.tn] *** Find Thread-Local Objects *** " + (new Date()));
	    	if(mhp != null)
	    		tlo = new ThreadLocalObjectsAnalysis(mhp);
			else
	    		tlo = new ThreadLocalObjectsAnalysis(new SynchObliviousMhpAnalysis());
	    	if(!optionOnFlyTLO)
	    	{
		    	tlo.precompute();
	    		G.v().out.println("[wjtp.tn] TLO totals (#analyzed/#encountered): " + SmartMethodInfoFlowAnalysis.counter + "/" + ClassInfoFlowAnalysis.methodCount);
		    }
	    	else
	    		G.v().out.println("[wjtp.tn] TLO so far (#analyzed/#encountered): " + SmartMethodInfoFlowAnalysis.counter + "/" + ClassInfoFlowAnalysis.methodCount);
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
    	Iterator<SootClass> runAnalysisClassesIt = Scene.v().getApplicationClasses().iterator();
    	while (runAnalysisClassesIt.hasNext()) 
    	{
    	    SootClass appClass = runAnalysisClassesIt.next();
    	    Iterator<SootMethod> methodsIt = appClass.getMethods().iterator();
    	    while (methodsIt.hasNext())
    	    {
    	    	SootMethod method = methodsIt.next();
				if(method.isConcrete())
				{
	    	    	Body b = method.retrieveActiveBody();
	    	    	ExceptionalUnitGraph eug = new ExceptionalUnitGraph(b);
    		    	methodToExcUnitGraph.put(method, eug);
    		    	
    	    		// run the intraprocedural analysis
    				SynchronizedRegionFinder ta = new SynchronizedRegionFinder(eug, b, optionPrintDebug, optionOpenNesting, tlo);
    				Chain<Unit> units = b.getUnits();
    				Unit lastUnit = units.getLast();
    				FlowSet fs = (FlowSet) ta.getFlowBefore(lastUnit);
    			
    				// add the results to the list of results
    				methodToFlowSet.put(method, fs);
				}
    	    }
    	}    	
    	
    	// Create a composite list of all transactions
    	criticalSections = new Vector<CriticalSection>();
    	for(FlowSet fs : methodToFlowSet.values())
    	{
    		List fList = fs.toList();
    		for(int i = 0; i < fList.size(); i++)
    			criticalSections.add(((SynchronizedRegionFlowPair) fList.get(i)).tn);
    	}

		// Assign Names To Transactions
		assignNamesToTransactions(criticalSections);

    	if(optionOnFlyTLO)
    	{
    		G.v().out.println("[wjtp.tn] TLO so far (#analyzed/#encountered): " + SmartMethodInfoFlowAnalysis.counter + "/" + ClassInfoFlowAnalysis.methodCount);
    	}

    	

    	// *** Find Transitive Read/Write Sets ***
    	// Finds the transitive read/write set for each transaction using a given
    	// nesting model.
    	G.v().out.println("[wjtp.tn] *** Find Transitive Read/Write Sets *** " + (new Date()));
    	PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
    	CriticalSectionAwareSideEffectAnalysis tasea = null;
		tasea = new CriticalSectionAwareSideEffectAnalysis(
					pta, 
					Scene.v().getCallGraph(), (optionOpenNesting ? criticalSections : null), tlo);
    	Iterator<CriticalSection> tnIt = criticalSections.iterator();
    	while(tnIt.hasNext())
    	{
    		CriticalSection tn = tnIt.next();
    		for(Unit unit : tn.invokes)
    		{
    			Stmt stmt = (Stmt) unit;
    			HashSet uses = new HashSet();
    			RWSet stmtRead = tasea.readSet(tn.method, stmt, tn, uses);
    			if(stmtRead != null)
	    			tn.read.union(stmtRead);
    			
    			RWSet stmtWrite = tasea.writeSet(tn.method, stmt, tn, uses);
				if(stmtWrite != null)
					tn.write.union(stmtWrite);
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
    	CriticalSectionInterferenceGraph ig = 
    		new CriticalSectionInterferenceGraph(
    				criticalSections, mhp, optionOneGlobalLock,
    				optionLeaveOriginalLocks, optionIncludeEmptyPossibleEdges);
    	interferenceGraph = ig; // save in field for later retrieval
    	
    	
    	
		// *** Detect the Possibility of Deadlock ***
		G.v().out.println("[wjtp.tn] *** Detect the Possibility of Deadlock *** " + (new Date()));
		DeadlockDetector dd = new DeadlockDetector(optionPrintDebug, optionAvoidDeadlock, true, criticalSections);
		if(!optionUseLocksets) // deadlock detection for all single-lock-per-region allocations
			deadlockGraph = dd.detectComponentBasedDeadlock();

		

		// *** Calculate Locking Objects ***
    	// Get a list of all dependencies for each group
    	G.v().out.println("[wjtp.tn] *** Calculate Locking Objects *** " + (new Date()));
		if(!optionStaticLocks)
		{
			// Calculate per-group contributing RWSet
			// (Might be preferable to use per-transaction contributing RWSet)
			for(CriticalSection tn : criticalSections)
	    	{
	    		if(tn.setNumber <= 0)
	    			continue;
	    		for(CriticalSectionDataDependency tdd : tn.edges)
	    			tn.group.rwSet.union(tdd.rw);
	    	}
	    }

		// Inspect each group's RW dependencies to determine if there's a possibility
		// of a shared lock object (if all dependencies are fields/localobjs of the same object)
		Map<Value, Integer> lockToLockNum = null;
		List<PointsToSetInternal> lockPTSets = null;
		if(optionLeaveOriginalLocks)
		{
			analyzeExistingLocks(criticalSections, ig);
		}
		else if(optionStaticLocks)
		{
			setFlagsForStaticAllocations(ig);
		}
		else // for locksets and dynamic locks
		{
			setFlagsForDynamicAllocations(ig);

			// Data structures for determining lock numbers
			lockPTSets = new ArrayList<PointsToSetInternal>();
			lockToLockNum = new HashMap<Value, Integer>();

			findLockableReferences(criticalSections, pta, tasea, lockToLockNum,lockPTSets);

			// print out locksets
			if(optionUseLocksets)
			{
				for( CriticalSection tn : criticalSections )
				{
					if( tn.group != null )
					{
						G.v().out.println("[wjtp.tn] " + tn.name + " lockset: " + locksetToLockNumString(tn.lockset, lockToLockNum) + (tn.group.useLocksets ? "" : " (placeholder)"));
					}
				}
			}
		}

		
		
		// *** Detect the Possibility of Deadlock for Locksets ***
		if(optionUseLocksets) // deadlock detection and lock ordering for lockset allocations
		{
			G.v().out.println("[wjtp.tn] *** Detect " + (optionAvoidDeadlock ? "and Correct " : "") + "the Possibility of Deadlock for Locksets *** " + (new Date()));
			deadlockGraph = dd.detectLocksetDeadlock(lockToLockNum, lockPTSets);
			if(optionPrintDebug)
				((HashMutableEdgeLabelledDirectedGraph) deadlockGraph).printGraph();
		
			G.v().out.println("[wjtp.tn] *** Reorder Locksets to Avoid Deadlock *** " + (new Date()));
			dd.reorderLocksets(lockToLockNum, (HashMutableEdgeLabelledDirectedGraph) deadlockGraph);
		}
		
		// *** Print Output and Transform Program ***
    	G.v().out.println("[wjtp.tn] *** Print Output and Transform Program *** " + (new Date()));

		// Print topological graph in graphviz format
		if(optionPrintGraph)
			printGraph(criticalSections, ig, lockToLockNum);

		// Print table of transaction information
		if(optionPrintTable)
		{
			printTable(criticalSections, mhp);
			printGroups(criticalSections, ig);
		}

    	// For all methods, run the lock transformer
		if(!optionLeaveOriginalLocks)
		{
	    	// Create an array of booleans to keep track of which global locks have been inserted into the program
			boolean[] insertedGlobalLock = new boolean[ig.groupCount()];
			insertedGlobalLock[0] = false;
			for(int i = 1; i < ig.groupCount(); i++)
			{
				CriticalSectionGroup tnGroup = ig.groups().get(i);
				insertedGlobalLock[i] = (!optionOneGlobalLock) && (tnGroup.useDynamicLock || tnGroup.useLocksets);
			}
			
			for(SootClass appClass : Scene.v().getApplicationClasses())
			{
	    	    for(SootMethod method : appClass.getMethods())
	    	    {
					if(method.isConcrete())
					{
	    		    	FlowSet fs = methodToFlowSet.get(method);
	    	    		if(fs != null) // (newly added methods need not be transformed)
	    	    			LockAllocationBodyTransformer.v().internalTransform(method.getActiveBody(), fs, ig.groups(), insertedGlobalLock); 
					}
	    	    }
	    	}
	    }
	}
    
	protected void findLockableReferences(List<CriticalSection> AllTransactions,
			PointsToAnalysis pta, CriticalSectionAwareSideEffectAnalysis tasea,
			Map<Value, Integer> lockToLockNum,
			List<PointsToSetInternal> lockPTSets) {
		// For each transaction, if the group's R/Ws may be fields of the same object, 
		// then check for the transaction if they must be fields of the same RUNTIME OBJECT
		Iterator<CriticalSection> tnIt9 = AllTransactions.iterator();
		while(tnIt9.hasNext())
		{
			CriticalSection tn = tnIt9.next();
			
			int group = tn.setNumber - 1;
			if(group < 0)
				continue;
					
			if(tn.group.useDynamicLock || tn.group.useLocksets) // if attempting to use a dynamic lock or locksets
			{
				
				// Get list of objects (FieldRef or Local) to be locked (lockset analysis)
				G.v().out.println("[wjtp.tn] * " + tn.name + " *");
				LockableReferenceAnalysis la = new LockableReferenceAnalysis(new BriefUnitGraph(tn.method.retrieveActiveBody()));
				tn.lockset = la.getLocksetOf(tasea, tn.group.rwSet, tn);
				
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
						for(CriticalSection groupTn : tn.group)
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
	public void setFlagsForDynamicAllocations(CriticalSectionInterferenceGraph ig) {
		for(int group = 0; group < ig.groupCount() - 1; group++)
		{
			CriticalSectionGroup tnGroup = ig.groups().get(group + 1);

			if(optionUseLocksets)
			{
				tnGroup.useLocksets = true; // initially, guess that this is true
			}
			else
			{
				tnGroup.isDynamicLock = (tnGroup.rwSet.getGlobals().size() == 0);
				tnGroup.useDynamicLock = true;
				tnGroup.lockObject = null;
			}

			// empty groups don't get locks
			if(tnGroup.rwSet.size() <= 0) // There are no edges in this group
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
	public void setFlagsForStaticAllocations(CriticalSectionInterferenceGraph ig) {
		// Allocate one new static lock for each group.
		for(int group = 0; group < ig.groupCount() - 1; group++)
		{
			CriticalSectionGroup tnGroup = ig.groups().get(group + 1);
			tnGroup.isDynamicLock = false;
			tnGroup.useDynamicLock = false; 
			tnGroup.lockObject = null;
		}
	}

    private void analyzeExistingLocks(List<CriticalSection> AllTransactions,
			CriticalSectionInterferenceGraph ig) {
		setFlagsForStaticAllocations(ig);
		
		// if for any lock there is any def to anything other than a static field, then it's a local lock.			
		// for each transaction, check every def of the lock
		Iterator<CriticalSection> tnAIt = AllTransactions.iterator();
		while(tnAIt.hasNext())
		{
			CriticalSection tn = tnAIt.next();
			if(tn.setNumber <= 0)
				continue;
			
			LocalDefs ld = LocalDefs.Factory.newLocalDefs(tn.method.retrieveActiveBody());
			
			if(tn.origLock == null || !(tn.origLock instanceof Local)) // || tn.begin == null)
				continue;
			List<Unit> rDefs = ld.getDefsOfAt( (Local) tn.origLock , tn.entermonitor );
			if(rDefs == null)
				continue;
			for (Unit u : rDefs)
			{
				Stmt next = (Stmt) u;
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
    
	public static String locksetToLockNumString(List<EquivalentValue> lockset, Map<Value, Integer> lockToLockNum)
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
    
    public void assignNamesToTransactions(List<CriticalSection> AllTransactions)
    {
       	// Give each method a unique, deterministic identifier
       	// Sort transactions into bins... one for each method name
       	
       	// Get list of method names
    	List<String> methodNamesTemp = new ArrayList<String>();
    	Iterator<CriticalSection> tnIt5 = AllTransactions.iterator();
    	while (tnIt5.hasNext()) 
    	{
    	    CriticalSection tn1 = tnIt5.next();
    	    String mname = tn1.method.getSignature(); //tn1.method.getSignature() + "." + tn1.method.getName();
    	    if(!methodNamesTemp.contains(mname))
    	    	methodNamesTemp.add(mname);
		}
		String methodNames[] = new String[1];
		methodNames = methodNamesTemp.toArray(methodNames);
		Arrays.sort(methodNames);

		// Initialize method-named bins
		// this matrix is <# method names> wide and <max txns possible in one method> + 1 tall
		int identMatrix[][] = new int[methodNames.length][CriticalSection.nextIDNum - methodNames.length + 2];
		for(int i = 0; i < methodNames.length; i++)
		{
			identMatrix[i][0] = 0;
			for(int j = 1; j < CriticalSection.nextIDNum - methodNames.length + 1; j++)
			{
				identMatrix[i][j] = 50000;
			}
		}
		
		// Put transactions into bins
    	Iterator<CriticalSection> tnIt0 = AllTransactions.iterator();
    	while(tnIt0.hasNext())
    	{
    		CriticalSection tn1 = tnIt0.next();
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
    	Iterator<CriticalSection> tnIt4 = AllTransactions.iterator();
    	while(tnIt4.hasNext())
    	{
    		CriticalSection tn1 = tnIt4.next();
			int methodNum = Arrays.binarySearch(methodNames, tn1.method.getSignature());
			int tnNum = Arrays.binarySearch(identMatrix[methodNum], tn1.IDNum) - 1;
    		tn1.name = "m" + (methodNum < 10? "00" : (methodNum < 100? "0" : "")) + methodNum + "n" + (tnNum < 10? "0" : "") + tnNum;
    	}
	}	

	public void printGraph(Collection<CriticalSection> AllTransactions, CriticalSectionInterferenceGraph ig, Map<Value, Integer> lockToLockNum)
	{
		final String[] colors = {"black", "blue", "blueviolet", "chartreuse", "crimson", "darkgoldenrod1", "darkseagreen", "darkslategray", "deeppink",
			"deepskyblue1", "firebrick1", "forestgreen", "gold", "gray80", "navy", "pink", "red", "sienna", "turquoise1", "yellow"};
		Map<Integer, String> lockColors = new HashMap<Integer, String>();
		int colorNum = 0;
		HashSet<CriticalSection> visited = new HashSet<CriticalSection>();
		
		G.v().out.println("[transaction-graph]" + (optionUseLocksets ? "" : " strict") + " graph transactions {"); // "\n[transaction-graph] start=1;");

		for(int group = 0; group < ig.groups().size(); group++)
		{
			boolean printedHeading = false;
			Iterator<CriticalSection> tnIt = AllTransactions.iterator();
			while(tnIt.hasNext())
			{
				CriticalSection tn = tnIt.next();
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
							for(CriticalSection tn2 : tn.group)
							{
								if(!visited.contains(tn2) && ig.mayHappenInParallel(tn, tn2))
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
						Iterator<CriticalSectionDataDependency> tnedgeit = tn.edges.iterator();
						while(tnedgeit.hasNext())
						{
							CriticalSectionDataDependency edge = tnedgeit.next();
							CriticalSection tnedge = edge.other;
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
			Iterator<CriticalSection> tnIt = AllTransactions.iterator();
			while(tnIt.hasNext())
			{
				CriticalSection tn = tnIt.next();
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

					Iterator<CriticalSectionDataDependency> tnedgeit = tn.edges.iterator();
					while(tnedgeit.hasNext())
					{
						CriticalSectionDataDependency edge = tnedgeit.next();
						CriticalSection tnedge = edge.other;
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

	public void printTable(Collection<CriticalSection> AllTransactions, MhpTester mhp)
	{
		G.v().out.println("[transaction-table] ");
		Iterator<CriticalSection> tnIt7 = AllTransactions.iterator();
		while(tnIt7.hasNext())
		{
			CriticalSection tn = tnIt7.next();
			
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
			Iterator<CriticalSectionDataDependency> tnedgeit = tn.edges.iterator();
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
	
	public void printGroups(Collection<CriticalSection> AllTransactions, CriticalSectionInterferenceGraph ig)
	{
			G.v().out.print("[transaction-groups] Group Summaries\n[transaction-groups] ");
			for(int group = 0; group < ig.groupCount() - 1; group++)
    		{
    			CriticalSectionGroup tnGroup = ig.groups().get(group + 1);
    			if(tnGroup.size() > 0)
    			{
	    			G.v().out.print("Group " + (group + 1) + " ");
					G.v().out.print("Locking: " + (tnGroup.useLocksets ? "using " : (tnGroup.isDynamicLock && tnGroup.useDynamicLock ? "Dynamic on " : "Static on ")) + (tnGroup.useLocksets ? "locksets" : (tnGroup.lockObject == null ? "null" : tnGroup.lockObject.toString())) );
					G.v().out.print("\n[transaction-groups]      : ");
					Iterator<CriticalSection> tnIt = AllTransactions.iterator();
					while(tnIt.hasNext())
					{
						CriticalSection tn = tnIt.next();
						if(tn.setNumber == group + 1)
							G.v().out.print(tn.name + " ");
					}
					G.v().out.print("\n[transaction-groups] " + 
	    							tnGroup.rwSet.toString().replaceAll("\\[", "     : [").replaceAll("\n", "\n[transaction-groups] "));
	    		}
	    	}
			G.v().out.print("Erasing \n[transaction-groups]      : ");
			Iterator<CriticalSection> tnIt = AllTransactions.iterator();
			while(tnIt.hasNext())
			{
				CriticalSection tn = tnIt.next();
				if(tn.setNumber == -1)
					G.v().out.print(tn.name + " ");
			}
			G.v().out.println("\n[transaction-groups] ");
	}
	public CriticalSectionInterferenceGraph getInterferenceGraph() {
		return interferenceGraph;
	}
	public DirectedGraph getDeadlockGraph() {
		return deadlockGraph;
	}
	public List<CriticalSection> getCriticalSections() {
		return criticalSections;
	}
}
