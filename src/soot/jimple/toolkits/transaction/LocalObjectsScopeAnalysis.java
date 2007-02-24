package soot.jimple.toolkits.transaction;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.toolkits.mhp.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.jimple.toolkits.callgraph.*;
import soot.tagkit.*;
import soot.jimple.internal.*;
import soot.jimple.*;
import soot.jimple.spark.sets.*;
import soot.jimple.spark.pag.*;
import soot.toolkits.scalar.*;

// LocalObjectsScopeAnalysis written by Richard L. Halpert, 2007-02-23
// Finds objects that are local to the given scope.
// Begins by finding objects created in the given scope.  Then, creates lists
// of requirements for each of these objects to truly be local.

public class LocalObjectsScopeAnalysis
{
	SootClass scopeClass;
	DataFlowAnalysis dfa;
	
	public LocalObjectsScopeAnalysis(SootClass scopeClass, DataFlowAnalysis dfa)
	{
		 this.scopeClass = scopeClass;
		 this.dfa = dfa;
		 
		 doAnalysis();
	}
	
	private void doAnalysis()
	{
		// Combine the DFA results for each of this class's methods, using safe
		// approximations for which parameters, fields, and globals are shared
		// or local.  These approximations could be calculated by an analysis.
		// Methods that aren't called in this program can be excluded.
		
		
		// Analyze each method: determine which Locals are local and which are shared
		Iterator it = scopeClass.getMethods().iterator();
		while(it.hasNext())
		{
			SootMethod method = (SootMethod) it.next();
			
			// For each method, analyze the body
			
		}
	}
}

