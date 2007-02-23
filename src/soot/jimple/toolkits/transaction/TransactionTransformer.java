package soot.jimple.toolkits.transaction;

import java.util.*;

import soot.*;
import soot.util.Chain;
import soot.jimple.*;
import soot.jimple.toolkits.pointer.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.spark.pag.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.toolkits.mhp.*;
import soot.toolkits.mhp.pegcallgraph.*;
import soot.toolkits.mhp.findobject.*;
import soot.toolkits.mhp.stmt.*;
import soot.tagkit.LineNumberTag;
import soot.jimple.toolkits.annotation.nullcheck.*;

public class TransactionTransformer extends SceneTransformer
{
    public TransactionTransformer(Singletons.Global g){}
    public static TransactionTransformer v() 
	{ 
		return G.v().soot_jimple_toolkits_transaction_TransactionTransformer();
	}
	
	boolean optionOneGlobalLock = false;
	boolean optionPrintGraph = false;
	boolean optionPrintTable = false;
	boolean optionPrintDebug = false;
	
	UnsynchronizedMhpAnalysis mhp;

    protected void internalTransform(String phaseName, Map options)
	{
		// Get phase options
		optionOneGlobalLock = PhaseOptions.getBoolean( options, "one-global-lock" );
		optionPrintGraph = PhaseOptions.getBoolean( options, "print-graph" );
		optionPrintTable = PhaseOptions.getBoolean( options, "print-table" );
		optionPrintDebug = PhaseOptions.getBoolean( options, "print-debug" );
		


    	// *** Find and Name Transactions ***
    	// The transaction finder finds the start, end, and preparatory statements
    	// for each transaction. It also calculates the non-transitive read/write 
    	// sets for each transaction.
    	// For all methods, run the intraprocedural analysis (TransactionAnalysis)
    	G.v().out.println("*** Find and Name Transactions ***");
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
    				TransactionAnalysis ta = new TransactionAnalysis(eug, b, optionPrintDebug);
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

    	

    	// *** Find Transitive Read/Write Sets ***
    	// Finds the transitive read/write set for each transaction using a given
    	// nesting model.
    	// Note: currently, open-nesting is run by default. This is the implied
    	// meaning of synchronized regions, so is definitely OK if using that keyword
    	G.v().out.println("*** Find Transitive Read/Write Sets ***");
    	TransactionAwareSideEffectAnalysis tasea = 
    		new TransactionAwareSideEffectAnalysis(
    				Scene.v().getPointsToAnalysis(), 
    				Scene.v().getCallGraph(), AllTransactions);
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
    			
    			RWSet stmtRead = tasea.transactionalReadSet(tn.method, stmt, tn, sld);
    			if(stmtRead != null)
	    			tn.read.union(stmtRead);
    			
    			RWSet stmtWrite = tasea.transactionalWriteSet(tn.method, stmt, tn, sld);
				if(stmtWrite != null)
					tn.write.union(stmtWrite);
			}
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
		
		
		
		// *** Build May Happen In Parallel Info ***
    	G.v().out.println("*** Build May Happen In Parallel Info ***");
    	if(optionOneGlobalLock)
			mhp = null;
		else
			mhp = new UnsynchronizedMhpAnalysis();
		


    	// *** Calculate Locking Groups ***
    	// Search for data dependencies between transactions, and split them into disjoint sets
    	G.v().out.println("*** Calculate Locking Groups ***");
    	int nextGroup = 1;
    	if(optionOneGlobalLock) // use one group for all transactions
    	{
	    	Iterator tnIt1 = AllTransactions.iterator();
	    	while(tnIt1.hasNext())
	    	{
	    		Transaction tn1 = (Transaction) tnIt1.next();
	    		tn1.setNumber = 1;
    		}
    		nextGroup++;
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
		    			if(tn1.write.hasNonEmptyIntersection(tn2.write) ||
		    					tn1.write.hasNonEmptyIntersection(tn2.read) ||
		    					tn1.read.hasNonEmptyIntersection(tn2.write))
		    			{
		    				// Determine the size of the intersection for GraphViz output
		    				CodeBlockRWSet rw = tn1.write.intersection(tn2.write);
		    				rw.union(tn1.write.intersection(tn2.read));
		    				rw.union(tn1.read.intersection(tn2.write));
		    				int size = rw.size();
		    				
		    				// Record this 
		    				tn1.edges.add(new DataDependency(tn2, size, rw));
	//	    				tn2.edges.add(new DataDependency(tn1, size, rw)); // will be added in opposite direction later
		    				
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
		    						if(tn1 != tn2) // if they are equal, then they are already in the same group!
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
	//	    			if(mayHappenInParallel(tn1, tn1)) // we compare to ourselves already
	//	    			{
	//	    				tn1.setNumber = nextGroup;
	//	    				nextGroup++;
	//	    			}
	//	    			else
	//	    			{
		    				tn1.setNumber = -1; // delete transactional region
	//	    			}
		    		}	    			
	    		}
	    	}
    	}


		// *** Calculate Locking Objects ***
    	// Get a list of all dependencies for each group
    	G.v().out.println("*** Calculate Locking Objects ***");
    	RWSet rws[] = new CodeBlockRWSet[nextGroup - 1];
    	for(int group = 0; group < nextGroup - 1; group++)
    		rws[group] = new CodeBlockRWSet();
		if(!optionOneGlobalLock)
		{
	    	Iterator tnIt8 = AllTransactions.iterator();
	    	while(tnIt8.hasNext())
	    	{
	    		Transaction tn = (Transaction) tnIt8.next();
	    		Iterator EdgeIt = tn.edges.iterator();
	    		while(EdgeIt.hasNext())
	    		{
	    			DataDependency dd = (DataDependency) EdgeIt.next();
		    		rws[tn.setNumber - 1].union(dd.rw);
	    		}
	    	}
	    }

		// Inspect each group's RW dependencies to determine if there's a possibility
		// of a shared lock object (if all dependencies are fields/localobjs of the same object)
		boolean mayBeFieldsOfSameObject[] = new boolean[nextGroup - 1];
		boolean mustBeFieldsOfSameObjectForAllTns[] = new boolean[nextGroup - 1];
//		boolean mustBeSameArrayElementForAllTns[] = new boolean[nextGroup - 1];
		Value lockObject[] = new Value[nextGroup - 1];
		if(optionOneGlobalLock)
		{
			for(int group = 0; group < nextGroup - 1; group++)
			{
				mayBeFieldsOfSameObject[group] = false;
				mustBeFieldsOfSameObjectForAllTns[group] = false;
				lockObject[group] = null;
			}
		}
		else
		{
			for(int group = 0; group < nextGroup - 1; group++)
			{
				// For this group, find out if all RW Dependencies are possibly fields of the same object (object points-tos overlap)
				mayBeFieldsOfSameObject[group] = true;
				lockObject[group] = null;
				
				if(rws[group].size() <= 0) // WHAT DOES IT MEAN FOR THIS TO BE 0???
				{
					mayBeFieldsOfSameObject[group] = false;
					continue;
				}

				if(rws[group].getGlobals().size() > 0)
				{
					mayBeFieldsOfSameObject[group] = false;
					continue;
				}
				
				Iterator grwsIt1 = rws[group].getFields().iterator();
				while(grwsIt1.hasNext() && mayBeFieldsOfSameObject[group])
				{
					Object field1 = (Object) grwsIt1.next();
					Iterator grwsIt2 = rws[group].getFields().iterator();
					while(grwsIt2.hasNext() && mayBeFieldsOfSameObject[group])
					{
						Object field2 = (Object) grwsIt2.next();
						if(!rws[group].getBaseForField(field1).hasNonEmptyIntersection(rws[group].getBaseForField(field2)))
						{
							mayBeFieldsOfSameObject[group] = false;
						}
					}
				}

				if(rws[group].size() > 0 && mayBeFieldsOfSameObject[group])
				{
					mustBeFieldsOfSameObjectForAllTns[group] = true; // might be true
	//				mustBeSameArrayElementForAllTns[group] = true;
				}
				else
				{
					mustBeFieldsOfSameObjectForAllTns[group] = false; // can't be true
	//				mustBeSameArrayElementForAllTns[group] = true;
				}
			}
		}
		
		// For each transaction, if the group's R/Ws may be fields of the same object, 
		// then check for the transaction if they must be fields of the same RUNTIME OBJECT
    	Iterator tnIt9 = AllTransactions.iterator();
    	while(tnIt9.hasNext())
    	{
    		Transaction tn = (Transaction) tnIt9.next();
			
			int group = tn.setNumber - 1;
			if(group < 0)
				continue;
					
			if(mustBeFieldsOfSameObjectForAllTns[group]) // if still might be true, then inspect for this Transaction
			{
				// Get a list of units that read/write to any of the dependencies.
				Map unitToLocal = new HashMap();
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
						// This statement contributes to one of the RW dependencies
						Unit u = (Unit) e.getKey();
						Stmt s = (Stmt) u;

						// Get the base object of the field reference at this
						// statement. If there's an invoke expression, we can't be
						// sure it doesn't R/W to another object of the same type.
						value = null;
						index = null;
						if(!s.containsInvokeExpr())
						{
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
										PointsToSet base = Scene.v().getPointsToAnalysis().reachingObjects( lBase );
										rSet.addFieldRef( base, ifr.getField() );
									}
									else if( r instanceof ArrayRef )
									{
										ArrayRef ar = (ArrayRef) r;
										rBase = (Local) ar.getBase();
										rIndex = ar.getIndex();
										PointsToSet base = Scene.v().getPointsToAnalysis().reachingObjects( lBase );
										rSet.addFieldRef( base, PointsToAnalysis.ARRAY_ELEMENTS_NODE );
									}
									if(rSet.hasNonEmptyIntersection(rws[group]))
									{
										value = rBase; // if the lvalue's write set contributes, use it
										index = rIndex;
									}
								}
							}
						}

						G.v().out.println("LOCAL OBJ REF for " + s + 
							" value:" + value + " index:" + index);

						if(value == null)
						{
							mustBeFieldsOfSameObjectForAllTns[group] = false;
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
				
				// Use LocalInfoFlowAnalysis to determine if all contributing units must be accessing fields of the same RUNTIME OBJECT
//				G.v().out.print("Transaction " + tn.name + " in " + tn.method + " ");
//				UnitGraph g = new ExceptionalUnitGraph(tn.method.retrieveActiveBody());
				UnitGraph g = (UnitGraph) methodToExcUnitGraph.get(tn.method);
				LocalInfoFlowAnalysis lif = new LocalInfoFlowAnalysis(g);
				Vector barriers = (Vector) tn.ends.clone();
				barriers.add(tn.begin);
				if( lif.mustPointToSameObj(unitToLocal, barriers) ) // runs the analysis
				{
					Map firstUseToAliasSet = lif.getFirstUseToAliasSet(); // get first uses for this transaction					
					NullnessAnalysis na = new NullnessAnalysis(g);
					NullnessAssumptionAnalysis naa = new NullnessAssumptionAnalysis(g);
					CommonAncestorValueAnalysis cav = new CommonAncestorValueAnalysis(new BriefUnitGraph(tn.method.retrieveActiveBody()));
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
								if(lockObject[group] == null || v instanceof Ref)
									lockObject[group] = v;
								if( tn.lockObject == null || v instanceof Ref )
									tn.lockObject = v;
							}
							else
							{
//								G.v().out.println("Value " + v + " was rejected as a lock object because it could be null in " + tn.name);
							}
						}
						else
						{
//							G.v().out.println("Ancestor " + o + " was rejected as a lock object because it is null in " + tn.name);
						}
					}
					
					if(tn.lockObject == null)
					{
//						G.v().out.println("No value was found for a lock object in " + tn.name + " (no ancestor value found)");
						mustBeFieldsOfSameObjectForAllTns[group] = false;
						continue; // move on to next transaction
					}
					
					if(false) // allRefsAreArrayRefs ) // NOTE: This is disabled because there is a possibility of
													   // null elements in the array.  You cannot lock a null object.
													   // We need a won't-introduce-a-new-null-object-error analysis!
					{
						G.v().out.println("checking for equivalence of these array indices" + unitToArrayIndex.values());
						if( lif.mustPointToSameObj(unitToArrayIndex, barriers) )
						{
							G.v().out.println("array indices are equivalent... finding ancestor value at " + tn.begin);
							firstUseToAliasSet = lif.getFirstUseToAliasSet(); // get first uses for this transaction					
							cav = new CommonAncestorValueAnalysis(new BriefUnitGraph(tn.method.retrieveActiveBody()));
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
				}
				else
				{
//					G.v().out.println("No value was found for a lock object in " + tn.name + " (transaction accesses multiple objects of same type)");
					mustBeFieldsOfSameObjectForAllTns[group] = false;
				}
			}
			else
			{
//				G.v().out.println("No value was found for a lock object in " + tn.name + " (transactions in group access multiple objects of different types)");
			}
		}
		
		
		
		// *** Print Output and Transform Program ***
    	G.v().out.println("*** Print Output and Transform Program ***");

		// Print topological graph in format of graphviz package
		if(optionPrintGraph)
		{
			printGraph(AllTransactions);
		}

		// Print table of transaction information
		if(optionPrintTable)
		{
			printTable(AllTransactions);			
			printGroups(nextGroup, mayBeFieldsOfSameObject, mustBeFieldsOfSameObjectForAllTns, lockObject, rws);
		}

    	// For all methods, run the transformer (Pessimistic Transaction Tranformation)

    	// BEGIN UGLINESS
		TransactionBodyTransformer.addedGlobalLockObj = new boolean[nextGroup];
		TransactionBodyTransformer.addedGlobalLockObj[0] = false;
		boolean useGlobalLock[] = new boolean[nextGroup - 1];
		for(int i = 1; i < nextGroup; i++)
		{
			TransactionBodyTransformer.addedGlobalLockObj[i] = (!optionOneGlobalLock) && mustBeFieldsOfSameObjectForAllTns[i - 1];
			useGlobalLock[i - 1] = !mustBeFieldsOfSameObjectForAllTns[i - 1];
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
    	    	
   	    			TransactionBodyTransformer.v().setDetails(fs, nextGroup, useGlobalLock);
   	    			TransactionBodyTransformer.v().internalTransform(b,phaseName, options); 
				}
    	    }
    	}
	}
    
    public boolean mayHappenInParallel(Transaction tn1, Transaction tn2)
    {
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

	public void printGraph(Collection AllTransactions)
	{
		G.v().out.println("[transaction-graph] strict graph transactions {\n[transaction-graph] start=1;");		
		Iterator tnIt6 = AllTransactions.iterator();
		while(tnIt6.hasNext())
		{
			Transaction tn = (Transaction) tnIt6.next();
			Iterator tnedgeit = tn.edges.iterator();
			G.v().out.println("[transaction-graph] " + tn.name + " [name=\"" + tn.method.toString() + "\"];");
			while(tnedgeit.hasNext())
			{
				DataDependency edge = (DataDependency) tnedgeit.next();
				Transaction tnedge = edge.other;
				G.v().out.println("[transaction-graph] " + tn.name + " -- " + tnedge.name + " [color=" + (edge.size > 5 ? (edge.size > 50 ? "black" : "blue") : "black") + " style=" + (edge.size > 50 ? "dashed" : "solid") + " exactsize=" + edge.size + "];");
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
				G.v().out.print(((DataDependency)tnedgeit.next()).other.name + " ");
			G.v().out.println("\n[transaction-table] Lock : " + (tn.lockObject == null ? "Global" : (tn.lockObject.toString() + (tn.lockObjectArrayIndex == null ? "" : "[" + tn.lockObjectArrayIndex + "]")) ));
			G.v().out.println("[transaction-table] Group: " + tn.setNumber + "\n[transaction-table] ");
		}
	}
	
	public void printGroups(int nextGroup, boolean mayBeFieldsOfSameObject[], boolean mustBeFieldsOfSameObjectForAllTns[], Value lockObject[], RWSet rws[])
	{
			G.v().out.print("[transaction-groups] Group Summaries\n[transaction-groups] ");
			for(int group = 0; group < nextGroup - 1; group++)
    		{
    			G.v().out.print("Groups" + (group + 1) +
								" mayBeFieldsOfSameObject=" + mayBeFieldsOfSameObject[group] +
								" mustBeFieldsOfSameObjectForAllTns=" + mustBeFieldsOfSameObjectForAllTns[group] + 
								" lock object: " + (lockObject[group] == null? "null" : lockObject[group].toString()) + "\n[transaction-groups] " + 
    							rws[group].toString().replaceAll("\\[", "     : [").replaceAll("\n", "\n[transaction-groups] ") + 
								(rws[group].size() == 0 ? "\n[transaction-groups] " : ""));
	    	}
			G.v().out.println("");
	}
}
