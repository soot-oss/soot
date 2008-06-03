package soot.jimple.toolkits.thread.synchronization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.Hierarchy;
import soot.Local;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.PointsToAnalysis;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.jimple.toolkits.pointer.CodeBlockRWSet;
import soot.jimple.toolkits.thread.mhp.MhpTester;

public class CriticalSectionInterferenceGraph {

	int nextGroup;
	List<CriticalSectionGroup> groups;
	
	List<CriticalSection> criticalSections;
	MhpTester mhp;
	PointsToAnalysis pta;
	boolean optionOneGlobalLock = false;
	boolean optionLeaveOriginalLocks = false;
	boolean optionIncludeEmptyPossibleEdges = false;
	
	public CriticalSectionInterferenceGraph(List<CriticalSection> criticalSections, MhpTester mhp, boolean optionOneGlobalLock, boolean optionLeaveOriginalLocks, boolean optionIncludeEmptyPossibleEdges)
	{
		this.criticalSections = criticalSections;
		this.mhp = mhp;
		this.pta = Scene.v().getPointsToAnalysis();
		this.optionOneGlobalLock = optionOneGlobalLock;
		this.optionLeaveOriginalLocks = optionLeaveOriginalLocks;
		this.optionIncludeEmptyPossibleEdges = optionIncludeEmptyPossibleEdges;
		
		calculateGroups();
	}
	
	public int groupCount()
	{
		return nextGroup;
	}
	
	public List<CriticalSectionGroup> groups()
	{
		return groups;
	}
	
	public void calculateGroups()
	{
		nextGroup = 1;
		groups = new ArrayList<CriticalSectionGroup>();
		groups.add(new CriticalSectionGroup(0)); // dummy group
		
		if(optionOneGlobalLock) // use one group for all transactions
		{
			CriticalSectionGroup onlyGroup = new CriticalSectionGroup(nextGroup);
	    	Iterator<CriticalSection> tnIt1 = criticalSections.iterator();
	    	while(tnIt1.hasNext())
	    	{
	    		CriticalSection tn1 = tnIt1.next();
	    		onlyGroup.add(tn1);
			}
			nextGroup++;
			groups.add(onlyGroup);
		}
		else // calculate separate groups for transactions
		{
	    	Iterator<CriticalSection> tnIt1 = criticalSections.iterator();
	    	while(tnIt1.hasNext())
	    	{
	    		CriticalSection tn1 = tnIt1.next();
	    		
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
		        	Iterator<CriticalSection> tnIt2 = criticalSections.iterator();
		    		while(tnIt2.hasNext())
		    		{
		    			CriticalSection tn2 = tnIt2.next();
		    				    			
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
		    				tn1.edges.add(new CriticalSectionDataDependency(tn2, size, rw));
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
		 		    					CriticalSectionGroup newGroup = new CriticalSectionGroup(nextGroup);
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
	}
	
    public boolean mayHappenInParallel(CriticalSection tn1, CriticalSection tn2)
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
}
