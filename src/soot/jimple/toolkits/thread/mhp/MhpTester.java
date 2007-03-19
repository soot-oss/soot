package soot.jimple.toolkits.thread.mhp;

import java.util.*;

import soot.*;

/** MhpTester written by Richard L. Halpert 2007-03-15
 *  An interface for any object that can provide May-Happen-in-Parallel info and
 *  a list of the program's threads (List of AbstractRuntimeThreads)
 */

public interface MhpTester
{
    public boolean mayHappenInParallel(SootMethod m1, SootMethod m2); // method level MHP
	
    public boolean mayHappenInParallel(SootMethod m1, Unit u1, SootMethod m2, Unit u2); // stmt level MHP

	public void printMhpSummary();
	
	public List getThreads();
}
