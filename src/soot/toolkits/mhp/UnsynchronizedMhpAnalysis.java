
package soot.toolkits.mhp;

import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.toolkits.mhp.pegcallgraph.*;
import soot.toolkits.mhp.findobject.*;
import soot.jimple.internal.*;
import soot.toolkits.mhp.stmt.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.invoke.*;
import soot.jimple.spark.*;
import soot.jimple.spark.pag.*;
import soot.options.SparkOptions;
import soot.util.*;
import java.util.*;
import java.io.*;

/** UnsynchronizedMhpAnalysis written by Richard L. Halpert 2006-12-09
 *  Calculates May-Happen-in-Parallel (MHP) information as if in the absence
 *  of synchronization. Any synchronization statements (synchronized, wait, 
 *  notify, etc.) are ignored. If the program has no synchronization, then this 
 *  actually generates correct MHP. This is useful if you are trying to generate
 *  (replacement) synchronization.
 *
 *  This analysis uses may-alias information to determine the types of threads
 *  launched and the call graph to determine which methods they may call.
 *  This analysis uses a run-once/run-one-at-a-time/run-many classification to
 *  determine if a thread may be run in parallel with itself.
 */

public class UnsynchronizedMhpAnalysis
{
	List MHPLists;
	boolean optionPrintDebug;
	
	public UnsynchronizedMhpAnalysis()
	{
		MHPLists = new ArrayList();
		optionPrintDebug = false;
		buildMHPLists();
	}

	public void buildMHPLists()
	{
		SootMethod mainMethod = Scene.v().getMainClass().getMethodByName("main");

		PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		if (!(pta instanceof PAG))
		{
		   throw new RuntimeException("You must use Spark for points-to analysis when computing MHP information!");
		}
		PAG pag = (PAG) pta;
		SparkOptions so = pag.getOpts();
		if(so.rta())
		   throw new RuntimeException("MHP cannot be calculated using RTA due to incomplete call graph");

		CallGraph callGraph = Scene.v().getCallGraph();

		// Get a call graph trimmed to contain only the relevant methods (non-lib, non-native)
//		G.v().out.println("    MHP: PegCallGraph");
		PegCallGraph pecg = new PegCallGraph(callGraph);
	    
	    // Find allocation nodes that are run more than once
	    // Also find methods that are run more than once
//		G.v().out.println("    MHP: AllocNodesFinder");
		AllocNodesFinder anf = new AllocNodesFinder(pecg, callGraph, (PAG) pta);
		Set multiRunAllocNodes = anf.getMultiRunAllocNodes();
		Set multiCalledMethods = anf.getMultiCalledMethods();

		// Find Thread.start() and Thread.join() statements (in live code)
//		G.v().out.println("    MHP: StartJoinFinder");
		StartJoinFinder sjf = new StartJoinFinder(callGraph, (PAG) pta); // does analysis
		Map startToAllocNodes = sjf.getStartToAllocNodes();
		Map startToRunMethods = sjf.getStartToRunMethods();
		Map startToContainingMethod = sjf.getStartToContainingMethod();
		Map startToJoin = sjf.getStartToJoin();
		
		// Build MHP Lists
//		G.v().out.println("    MHP: Building MHP Lists");
		Map containingMethodToThreadMethods = new HashMap();
		Iterator threadIt = startToRunMethods.entrySet().iterator();
		int threadNum = 0;
		while(threadIt.hasNext())
		{
			// Get list of possible Runnable.run methods (actually, a list of peg chains)
			// and a list of allocation sites for this thread start statement
			// and the thread start statement itself
			Map.Entry e = (Map.Entry) threadIt.next();
			Stmt startStmt = (Stmt) e.getKey();
			List runMethods = (List) e.getValue();
			List threadAllocNodes = (List) startToAllocNodes.get(e.getKey());

			// Get a list of all possible unique Runnable.run methods for this thread start statement
			List threadMethods = new ArrayList();
			Iterator runMethodsIt = runMethods.iterator();
			while(runMethodsIt.hasNext())
			{
				SootMethod method = (SootMethod) runMethodsIt.next();
				if(!threadMethods.contains(method))
					threadMethods.add(method);
			}
			
			// Get a list containing all methods in the call graph(s) rooted at the possible run methods for this thread start statement
			// AKA a list of all methods that might be called by the thread started here
			int methodNum = 0;
			while(methodNum < threadMethods.size()) // iterate over all methods in threadMethods, even as new methods are being added to it
			{
				Iterator succMethodsIt = pecg.getSuccsOf(threadMethods.get(methodNum)).iterator();
				while(succMethodsIt.hasNext())
				{
					SootMethod method = (SootMethod) succMethodsIt.next();
					// if all edges into this method are of Kind THREAD, ignore it 
					// (because it's a run method that won't be called as part of THIS thread)
					boolean ignoremethod = true;
					Iterator edgeInIt = callGraph.edgesInto(method);
					while(edgeInIt.hasNext())
					{
						Edge edge = (Edge) edgeInIt.next();
						if( edge.kind() != Kind.THREAD && threadMethods.contains(edge.src())) // called directly by any of the thread methods?
							ignoremethod = false;
					}
					if(!ignoremethod && !threadMethods.contains(method))
						threadMethods.add(method);
				}
				methodNum++;
			}
			
			// Add this list of methods to MHPLists
			MHPLists.add(threadMethods);
			if(optionPrintDebug)
				System.out.println("THREAD" + threadNum + ": " + threadMethods.toString());
			
			// Find out if the "thread" in "thread.start()" could be more than one object
			boolean mayStartMultipleThreadObjects = (threadAllocNodes.size() > 1) || so.types_for_sites();
			if(!mayStartMultipleThreadObjects) // if there's only one alloc node
			{
				if(multiRunAllocNodes.contains(threadAllocNodes.iterator().next())) // but it gets run more than once
				{
					mayStartMultipleThreadObjects = true; // then "thread" in "thread.start()" could be more than one object
				}
			}
			
			// Find out if the "thread.start()" statement may be run more than once
			SootMethod startStmtMethod = (SootMethod) startToContainingMethod.get(startStmt);
			boolean mayBeRunMultipleTimes = multiCalledMethods.contains(startStmtMethod); // if method is called more than once...
			if(!mayBeRunMultipleTimes)
			{
				UnitGraph graph = new CompleteUnitGraph(startStmtMethod.getActiveBody());
				MultiRunStatementsFinder finder = new MultiRunStatementsFinder(
					graph, startStmtMethod, multiCalledMethods, callGraph);
				FlowSet multiRunStatements = finder.getMultiRunStatements(); // list of all units that may be run more than once in this method
				if(multiRunStatements.contains(startStmt))
					mayBeRunMultipleTimes = true;
			}

			// If a run-many thread.start() statement is (always) associated with a join statement in the same method,
			// then it may be possible to treat it as run-once, if this method is non-reentrant and called only
			// by one thread (sounds strict, but actually this is the most common case)
			if(mayBeRunMultipleTimes && startToJoin.containsKey(startStmt))
			{
				mayBeRunMultipleTimes = false; // well, actually, we don't know yet
				methodNum = 0;
				List containingMethodCalls = new ArrayList();
				containingMethodCalls.add(startStmtMethod);
				while(methodNum < containingMethodCalls.size()) // iterate over all methods in threadMethods, even as new methods are being added to it
				{
					Iterator succMethodsIt = pecg.getSuccsOf(containingMethodCalls.get(methodNum)).iterator();
					while(succMethodsIt.hasNext())
					{
						SootMethod method = (SootMethod) succMethodsIt.next();
						if(method == startStmtMethod)
						{// this method is reentrant
							mayBeRunMultipleTimes = true; // this time it's for sure
							break;
						}
						if(!containingMethodCalls.contains(method))
							containingMethodCalls.add(method);
					}
					methodNum++;
				}
				if(!mayBeRunMultipleTimes)
				{// There's still one thing that might cause this to be run multiple times: if it can be run in parallel with itself
				 // but we can't find that out 'till we're done
					containingMethodToThreadMethods.put(startStmtMethod, threadMethods);
				}
			}

			// If more than one thread might be started at this start statement,
			// and this start statement may be run more than once,
			// then add this list of methods to MHPLists *AGAIN*
			if(optionPrintDebug)
				System.out.println("Start Stmt " + startStmt.toString() + 
					" mayStartMultipleThreadObjects=" + mayStartMultipleThreadObjects + " mayBeRunMultipleTimes=" + mayBeRunMultipleTimes);
			if(mayStartMultipleThreadObjects && mayBeRunMultipleTimes)
			{
				MHPLists.add(((ArrayList) threadMethods).clone());
				if(optionPrintDebug)
					System.out.println("THREAD-AGAIN" + threadNum + ": " + threadMethods.toString());
			}
			threadNum++;
		}

		// do same for main method
		List mainMethods = new ArrayList();
		MHPLists.add(mainMethods);
		mainMethods.add(mainMethod);
		// get all the successors, add to threadMethods
		int methodNum = 0;
		while(methodNum < mainMethods.size())
		{
			Iterator succMethodsIt = pecg.getSuccsOf(mainMethods.get(methodNum)).iterator();
			while(succMethodsIt.hasNext())
			{
				SootMethod method = (SootMethod) succMethodsIt.next();
				// if all edges into this are of Kind THREAD, ignore it
				boolean ignoremethod = true;
				Iterator edgeInIt = callGraph.edgesInto(method);
				while(edgeInIt.hasNext())
				{
					if( ((Edge) edgeInIt.next()).kind() != Kind.THREAD )
						ignoremethod = false;
				}
				if(!ignoremethod && !mainMethods.contains(method))
					mainMethods.add(method);
			}
			methodNum++;
		}
		if(optionPrintDebug)
			G.v().out.println("MAIN   : " + mainMethods.toString());
			
		// Revisit the containing methods of start-join pairs that are non-reentrant but might be called in parallel
		boolean addedNew = true;
		while(addedNew)
		{
			addedNew = false;
			Iterator it = containingMethodToThreadMethods.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry e = (Map.Entry) it.next();
				SootMethod someStartMethod = (SootMethod) e.getKey();
				List someThreadMethods = (List) e.getValue();
				if(mayHappenInParallel(someStartMethod, someStartMethod))
				{
					MHPLists.add(((ArrayList) someThreadMethods).clone());
					containingMethodToThreadMethods.remove(someStartMethod);
					if(optionPrintDebug)
						G.v().out.println("THREAD-REVIVED: " + someThreadMethods);
					addedNew = true;
				}
			}
		}
	}

    public boolean mayHappenInParallel(SootMethod m1, SootMethod m2)
    {
    	if(MHPLists == null)
    	{
    		return true;
		}

		int size = MHPLists.size();
		for(int i = 0; i < size; i++)
		{
			if(((List)MHPLists.get(i)).contains(m1))
			{
				for(int j = 0; j < size; j++)
				{
					if(((List)MHPLists.get(j)).contains(m2) && i != j)
					{
						return true;
					}
				}
			}
		}
		return false;
	}
}

