
package soot.jimple.toolkits.thread.mhp;

import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.thread.AbstractRuntimeThread;
import soot.jimple.toolkits.thread.mhp.findobject.AllocNodesFinder;
import soot.jimple.toolkits.thread.mhp.findobject.MultiRunStatementsFinder;
import soot.jimple.toolkits.thread.mhp.pegcallgraph.PegCallGraph;
import soot.jimple.spark.ondemand.DemandCSPointsTo;
import soot.jimple.spark.pag.*;
import soot.options.SparkOptions;
import java.util.*;

/** UnsynchronizedMhpAnalysis written by Richard L. Halpert 2006-12-09
 *  Calculates May-Happen-in-Parallel (MHP) information as if in the absence
 *  of synchronization. Any synchronization statements (synchronized, wait, 
 *  notify, etc.) are ignored. If the program has no synchronization, then this 
 *  actually generates correct MHP. This is useful if you are trying to generate
 *  (replacement) synchronization. It is also useful if an approximation is
 *  acceptable, because it runs much faster than a synch-aware MHP analysis.
 *
 *  This analysis uses may-alias information to determine the types of threads
 *  launched and the call graph to determine which methods they may call.
 *  This analysis uses a run-once/run-one-at-a-time/run-many classification to
 *  determine if a thread may be run in parallel with itself.
 */

public class SynchObliviousMhpAnalysis implements MhpTester, Runnable
{
	List<AbstractRuntimeThread> threadList;
	boolean optionPrintDebug;
	boolean optionThreaded = false; // DOESN'T WORK if set to true... ForwardFlowAnalysis uses a static field in a thread-unsafe way
	
	Thread self;
	
	public SynchObliviousMhpAnalysis()
	{
		threadList = new ArrayList<AbstractRuntimeThread>();
		optionPrintDebug = false;

		self = null;

		buildThreadList();
	}

	protected void buildThreadList() // can only be run once if optionThreaded is true
	{
		if(optionThreaded)
		{
			if(self != null)
				return; // already running... do nothing

			self = new Thread(this);
			self.start();
		}
		else
			run();
	}

	public void run()
	{
		SootMethod mainMethod = Scene.v().getMainClass().getMethodByName("main");

		PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		if (pta instanceof DemandCSPointsTo) {
			DemandCSPointsTo demandCSPointsTo = (DemandCSPointsTo) pta;
			pta = demandCSPointsTo.getPAG();
		}		
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
		Set<AllocNode> multiRunAllocNodes = anf.getMultiRunAllocNodes();
		Set<SootMethod> multiCalledMethods = anf.getMultiCalledMethods();

		// Find Thread.start() and Thread.join() statements (in live code)
//		G.v().out.println("    MHP: StartJoinFinder");
		StartJoinFinder sjf = new StartJoinFinder(callGraph, (PAG) pta); // does analysis
		Map<Stmt, List<AllocNode>> startToAllocNodes = sjf.getStartToAllocNodes();
		Map<Stmt, List<SootMethod>> startToRunMethods = sjf.getStartToRunMethods();
		Map<Stmt, SootMethod> startToContainingMethod = sjf.getStartToContainingMethod();
		Map<Stmt, Stmt> startToJoin = sjf.getStartToJoin();
		
		// Build MHP Lists
//		G.v().out.println("    MHP: Building MHP Lists");
		List<AbstractRuntimeThread> runAtOnceCandidates = new ArrayList<AbstractRuntimeThread>();
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
			List threadAllocNodes = startToAllocNodes.get(e.getKey());

			// Get a list of all possible unique Runnable.run methods for this thread start statement
			AbstractRuntimeThread thread = new AbstractRuntimeThread(); // provides a list interface to the methods in a thread's sub-call-graph
			thread.setStartStmt(startStmt);
//			List threadMethods = new ArrayList();
			Iterator runMethodsIt = runMethods.iterator();
			while(runMethodsIt.hasNext())
			{
				SootMethod method = (SootMethod) runMethodsIt.next();
				if(!thread.containsMethod(method))
				{
					thread.addMethod(method);
					thread.addRunMethod(method);
				}
			}
			
			// Get a list containing all methods in the call graph(s) rooted at the possible run methods for this thread start statement
			// AKA a list of all methods that might be called by the thread started here
			int methodNum = 0;
			while(methodNum < thread.methodCount()) // iterate over all methods in threadMethods, even as new methods are being added to it
			{
				Iterator succMethodsIt = pecg.getSuccsOf(thread.getMethod(methodNum)).iterator();
				while(succMethodsIt.hasNext())
				{
					SootMethod method = (SootMethod) succMethodsIt.next();
					// if all edges into this method are of Kind THREAD, ignore it 
					// (because it's a run method that won't be called as part of THIS thread) THIS IS NOT OPTIMAL
					boolean ignoremethod = true;
					Iterator edgeInIt = callGraph.edgesInto(method);
					while(edgeInIt.hasNext())
					{
						Edge edge = (Edge) edgeInIt.next();
						if( edge.kind() != Kind.THREAD && edge.kind() != Kind.ASYNCTASK
								&& thread.containsMethod(edge.src())) // called directly by any of the thread methods?
							ignoremethod = false;
					}
					if(!ignoremethod && !thread.containsMethod(method))
						thread.addMethod(method);
				}
				methodNum++;
			}
			
			// Add this list of methods to MHPLists
			threadList.add(thread);
			if(optionPrintDebug)
				System.out.println(thread.toString());
			
			// Find out if the "thread" in "thread.start()" could be more than one object
			boolean mayStartMultipleThreadObjects = (threadAllocNodes.size() > 1) || so.types_for_sites();
			if(!mayStartMultipleThreadObjects) // if there's only one alloc node
			{
				if(multiRunAllocNodes.contains(threadAllocNodes.iterator().next())) // but it gets run more than once
				{
					mayStartMultipleThreadObjects = true; // then "thread" in "thread.start()" could be more than one object
				}
			}
			
			if(mayStartMultipleThreadObjects)
				thread.setStartStmtHasMultipleReachingObjects();
			
			// Find out if the "thread.start()" statement may be run more than once
			SootMethod startStmtMethod = startToContainingMethod.get(startStmt);
			thread.setStartStmtMethod(startStmtMethod);
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
			
			if(mayBeRunMultipleTimes)
			{
				thread.setStartStmtMayBeRunMultipleTimes();
			}

			// If a run-many thread.start() statement is (always) associated with a join statement in the same method,
			// then it may be possible to treat it as run-once, if this method is non-reentrant and called only
			// by one thread (sounds strict, but actually this is the most common case)
			if(mayBeRunMultipleTimes && startToJoin.containsKey(startStmt))
			{
				thread.setJoinStmt(startToJoin.get(startStmt));
				mayBeRunMultipleTimes = false; // well, actually, we don't know yet
				methodNum = 0;
				List<SootMethod> containingMethodCalls = new ArrayList<SootMethod>();
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
							thread.setStartMethodIsReentrant();
							thread.setRunsMany();
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
					runAtOnceCandidates.add(thread);
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
				threadList.add(thread); // add another copy
				thread.setRunsMany();
				if(optionPrintDebug)
					System.out.println(thread.toString());
			}
			else
				thread.setRunsOnce();
			threadNum++;
		}

		// do same for main method
		AbstractRuntimeThread mainThread = new AbstractRuntimeThread();
//		List mainMethods = new ArrayList();
		threadList.add(mainThread);
		mainThread.setRunsOnce();
		mainThread.addMethod(mainMethod);
		mainThread.addRunMethod(mainMethod);
		mainThread.setIsMainThread();
		// get all the successors, add to threadMethods
		int methodNum = 0;
		while(methodNum < mainThread.methodCount())
		{
			Iterator succMethodsIt = pecg.getSuccsOf(mainThread.getMethod(methodNum)).iterator();
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
				if(!ignoremethod && !mainThread.containsMethod(method))
					mainThread.addMethod(method);
			}
			methodNum++;
		}
		if(optionPrintDebug)
			G.v().out.println(mainThread.toString());
			
		// Revisit the containing methods of start-join pairs that are non-reentrant but might be called in parallel
		boolean addedNew = true;
		while(addedNew)
		{
			addedNew = false;
			ListIterator<AbstractRuntimeThread> it = runAtOnceCandidates.listIterator();
			while(it.hasNext())
			{
				AbstractRuntimeThread someThread = it.next();
				SootMethod someStartMethod = someThread.getStartStmtMethod();
				if(mayHappenInParallelInternal(someStartMethod, someStartMethod))
				{
					threadList.add(someThread); // add a second copy of it
					someThread.setStartMethodMayHappenInParallel();
					someThread.setRunsMany();
					it.remove();
					if(optionPrintDebug)
						G.v().out.println(someThread.toString());
					addedNew = true;
				}
			}
		}
		
		// mark the remaining threads here as run-one-at-a-time
		Iterator<AbstractRuntimeThread> it = runAtOnceCandidates.iterator();
		while(it.hasNext())
		{
			AbstractRuntimeThread someThread = it.next();
			someThread.setRunsOneAtATime();
		}
	}

    public boolean mayHappenInParallel(SootMethod m1, Unit u1, SootMethod m2, Unit u2)
    {
   		if(optionThreaded)
		{
			if(self == null)
				return true; // not started...

			// Wait until finished
			G.v().out.println("[mhp] waiting for analysis thread to finish");
			try
			{
				self.join();
			}
			catch(InterruptedException ie)
			{
				return true;
			}
		}

		return mayHappenInParallelInternal(m1, m2);
	}
		
    public boolean mayHappenInParallel(SootMethod m1, SootMethod m2)
    { 
   		if(optionThreaded)
		{
			if(self == null)
				return true; // not started...

			// Wait until finished
			G.v().out.println("[mhp] waiting for thread to finish");
			try
			{
				self.join();
			}
			catch(InterruptedException ie)
			{
				return true;
			}
		}

		return mayHappenInParallelInternal(m1, m2);
	}

    private boolean mayHappenInParallelInternal(SootMethod m1, SootMethod m2)
    {
    	if(threadList == null) // not run
    	{
    		return true;
		}

		int size = threadList.size();
		for(int i = 0; i < size; i++)
		{
			if(threadList.get(i).containsMethod(m1))
			{
				for(int j = 0; j < size; j++)
				{
					if(threadList.get(j).containsMethod(m2) && i != j)
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void printMhpSummary()
	{
		if(optionThreaded)
		{
			if(self == null)
				return; // not run... do nothing

			// Wait until finished
			G.v().out.println("[mhp] waiting for thread to finish");
			try
			{
				self.join();
			}
			catch(InterruptedException ie)
			{
				return;
			}
		}

		List<AbstractRuntimeThread> threads = new ArrayList<AbstractRuntimeThread>();
		int size = threadList.size();
		G.v().out.println("[mhp]");
		for(int i = 0; i < size; i++)
		{
			if( !threads.contains(threadList.get(i)) )
			{
				G.v().out.println("[mhp] " + 
					threadList.get(i).toString().replaceAll(
						"\n", "\n[mhp] ").replaceAll(
						">,",">\n[mhp]  "));
				G.v().out.println("[mhp]");
			}
			threads.add(threadList.get(i));
		}
	}
	
	public List<SootClass> getThreadClassList()
	{
		if(optionThreaded)
		{
			if(self == null)
				return null; // not run... do nothing

			// Wait until finished
			G.v().out.println("[mhp] waiting for thread to finish");
			try
			{
				self.join();
			}
			catch(InterruptedException ie)
			{
				return null;
			}
		}
		
		if(threadList == null)
			return null;
		
		List<SootClass> threadClasses = new ArrayList<SootClass>();
		int size = threadList.size();
		for(int i = 0; i < size; i++)
		{
			AbstractRuntimeThread thread = threadList.get(i);
			Iterator<Object> threadRunMethodIt = thread.getRunMethods().iterator();
			while(threadRunMethodIt.hasNext())
			{
				SootClass threadClass = ((SootMethod) threadRunMethodIt.next()).getDeclaringClass(); // what about subclasses???
				if( !threadClasses.contains(threadClass) && threadClass.isApplicationClass() ) // only include application threads
					threadClasses.add(threadClass);
			}
		}
		return threadClasses;
	}
	
	public List<AbstractRuntimeThread> getThreads()
	{
		if(optionThreaded)
		{
			if(self == null)
				return null; // not run... do nothing

			// Wait until finished
			G.v().out.println("[mhp] waiting for thread to finish");
			try
			{
				self.join();
			}
			catch(InterruptedException ie)
			{
				return null;
			}
		}
		
		if(threadList == null)
			return null;

		List<AbstractRuntimeThread> threads = new ArrayList<AbstractRuntimeThread>();
		int size = threadList.size();
		for(int i = 0; i < size; i++)
		{
			if( !threads.contains(threadList.get(i)) )
			{
				threads.add(threadList.get(i));
			}
		}
		return threads;
	}
}

