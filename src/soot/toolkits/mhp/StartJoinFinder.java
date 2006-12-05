package soot.toolkits.mhp;

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

// StartJoinFinder written by Richard L. Halpert, 2006-12-04
// This can be used as an alternative to PegGraph and PegChain
// if only thread start, join, and type information is needed

// This is implemented as a real flow analysis so that, in the future,
// flow information can be used to match starts with joins

public class StartJoinFinder
{
	Set startStatements;
	Set joinStatements;
	
	Map startToRunMethods;
	Map startToAllocNodes;
	Map startToContainingMethod;
	
	public StartJoinFinder(PAG pag)
	{
		startStatements = new HashSet();
		joinStatements = new HashSet();

		startToRunMethods = new HashMap();
		startToAllocNodes = new HashMap();
		startToContainingMethod = new HashMap();
		
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
    		    	
    	    		// run the intraprocedural analysis
    				StartJoinAnalysis sja = new StartJoinAnalysis(new ExceptionalUnitGraph(b), method, pag);
    				
    				// Add to interprocedural results
    				startStatements.addAll(sja.getStartStatements());
    				joinStatements.addAll(sja.getJoinStatements());
    				startToRunMethods.putAll(sja.getStartToRunMethods());
    				startToAllocNodes.putAll(sja.getStartToAllocNodes());
    				Iterator startIt = sja.getStartStatements().iterator();
    				while(startIt.hasNext())
    				{
    					Stmt start = (Stmt) startIt.next();
    					startToContainingMethod.put(start, method);
    				}
    			}
    		}
    	}
	}
	
	public Set getStartStatements()
	{
		return startStatements;
	}
	
	public Set getJoinStatements()
	{
		return joinStatements;
	}

	public Map getStartToRunMethods()
	{
		return startToRunMethods;
	}

	public Map getStartToAllocNodes()
	{
		return startToAllocNodes;
	}
	
	public Map getStartToContainingMethod()
	{
		return startToContainingMethod;
	}
}

