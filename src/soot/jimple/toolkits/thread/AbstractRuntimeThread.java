
package soot.jimple.toolkits.thread;

import soot.*;
import soot.jimple.*;
import java.util.*;

/** AbstractRuntimeThread written by Richard L. Halpert 2007-03-04
 *  Acts as a container for the thread information collected by 
 *  UnsynchronizedMhpAnalysis.  A set of threads started from the same location 
 *  will be represented by one AbstractRuntimeThread, with runsMany set to true.
 */

public class AbstractRuntimeThread
{
	// Where thread is started/joined
	Stmt startStmt;
	SootMethod startStmtMethod;
	Stmt joinStmt;
	
	// What methods are in the thread
	List<Object> methods;
	List<Object> runMethods; // meant to be a subset of methods

	// What kind of parallelism
	boolean runsMany;
	boolean runsOnce;
	boolean runsOneAtATime;
	
	// How we determined the parallelism
	boolean startStmtHasMultipleReachingObjects;
	boolean startStmtMayBeRunMultipleTimes;
//	boolean hasJoinStmt; // just check if joinStmt is null or not
	boolean startMethodIsReentrant;
	boolean startMethodMayHappenInParallel;
	
	// Just for kicks
	boolean isMainThread;


	public AbstractRuntimeThread()
	{
		startStmt = null;
		startStmtMethod = null;
		methods = new ArrayList<Object>();
		runMethods = new ArrayList<Object>();

		// What kind of parallelism - this is set unsafely, so analysis MUST set it correctly
		runsMany = false;
		runsOnce = false;
		runsOneAtATime = false;
		
		// How we determined the parallelism - this is set unsafely, so analysis MUST set it correctly
		startStmtHasMultipleReachingObjects = false;
		startStmtMayBeRunMultipleTimes = false;
		startMethodIsReentrant = false;
		startMethodMayHappenInParallel = false;
		
		// Just for kicks
		isMainThread = false;
	}
	
	public void setStartStmt(Stmt startStmt)
	{
		this.startStmt = startStmt;
	}
	
	public void setJoinStmt(Stmt joinStmt)
	{
		this.joinStmt = joinStmt;
	}
	
	public void setStartStmtMethod(SootMethod startStmtMethod)
	{
		this.startStmtMethod = startStmtMethod;
	}
	
	public SootMethod getStartStmtMethod()
	{
		return startStmtMethod;
	}

	public boolean containsMethod(Object method)
	{
		return methods.contains(method);
	}
	
	public void addMethod(Object method)
	{
		methods.add(method);
	}
	
	public void addRunMethod(Object method)
	{
		runMethods.add(method);
	}
	
	public List<Object> getRunMethods()
	{
		return runMethods;
	}
	
	public int methodCount()
	{
		return methods.size();
	}
	
	public Object getMethod(int methodNum)
	{
		return methods.get(methodNum);
	}
	
	public void setStartStmtHasMultipleReachingObjects()
	{
		startStmtHasMultipleReachingObjects = true;
	}
	
	public void setStartStmtMayBeRunMultipleTimes()
	{
		startStmtMayBeRunMultipleTimes = true;
	}
	
	public void setStartMethodIsReentrant()
	{
		startMethodIsReentrant = true;
	}
	
	// Does this ever happen?  Should run a test to see if this situation would
	// already be caught by StartStmtMayBeRunMultipleTimes
	public void setStartMethodMayHappenInParallel()
	{
		startMethodMayHappenInParallel = true;
	}
		
	public void setRunsMany()
	{
		runsMany = true;
		runsOnce = false;
		runsOneAtATime = false;
	}
	
	public void setRunsOnce()
	{
		runsMany = false;
		runsOnce = true;
		runsOneAtATime = false;
	}
	
	public void setRunsOneAtATime()
	{
		runsMany = false;
		runsOnce = false;
		runsOneAtATime = true;
	}
	
	public void setIsMainThread()
	{
		isMainThread = true;
	}


	public String toString()
	{
		String ret = (isMainThread ? "Main Thread" : "User Thread") + " (" + (runsMany ? "Multi,  " : (runsOnce ? "Single, " : (runsOneAtATime ? "At-Once,": "ERROR")));
		if(startStmtHasMultipleReachingObjects)
		{
			ret = ret + "MRO,"; // Multiple Reaching Objects
			if(startMethodIsReentrant)
				ret = ret + "SMR"; // Start Method is Reentrant
			else if(startMethodMayHappenInParallel)
				ret = ret + "MSP"; // May be Started in Parallel
			else if(startStmtMayBeRunMultipleTimes)
				ret = ret + "RMT"; // Run Multiple Times
			else
				ret = ret + "ROT"; // Run One Time
		}
		else
		{
			if(isMainThread)
				ret = ret + "---,---"; // no start stmt...
			else
				ret = ret + "SRO,---"; // Single Reaching Object
		}
		ret = ret + "): ";
		
		if(!isMainThread)
			ret = ret + "Started in " + startStmtMethod + " by " + startStmt + "\n";
		else
			ret = ret + "\n";
			
		if(joinStmt != null)
			ret = ret + "                               " + "Joined  in " + startStmtMethod + " by " + joinStmt + "\n";
		
		ret = ret + methods.toString();
		
		return ret;
	}
}
