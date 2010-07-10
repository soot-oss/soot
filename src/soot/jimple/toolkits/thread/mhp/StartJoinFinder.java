package soot.jimple.toolkits.thread.mhp;

import soot.*;
import java.util.*;

import soot.toolkits.graph.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.*;
import soot.jimple.spark.pag.*;

// StartJoinFinder written by Richard L. Halpert, 2006-12-04
// This can be used as an alternative to PegGraph and PegChain
// if only thread start, join, and type information is needed

public class StartJoinFinder
{
	Set<Stmt> startStatements;
	Set<Stmt> joinStatements;
	
	Map<Stmt, List<SootMethod>> startToRunMethods;
	Map<Stmt, List<AllocNode>> startToAllocNodes;
	Map<Stmt, Stmt> startToJoin;
	Map<Stmt, SootMethod> startToContainingMethod;
	
	public StartJoinFinder(CallGraph callGraph, PAG pag)
	{
		startStatements = new HashSet<Stmt>();
		joinStatements = new HashSet<Stmt>();

		startToRunMethods = new HashMap<Stmt, List<SootMethod>>();
		startToAllocNodes = new HashMap<Stmt, List<AllocNode>>();
		startToJoin = new HashMap<Stmt, Stmt>();
		startToContainingMethod = new HashMap<Stmt, SootMethod>();
		
    	Iterator runAnalysisClassesIt = Scene.v().getApplicationClasses().iterator();
    	while (runAnalysisClassesIt.hasNext()) 
    	{
    	    SootClass appClass = (SootClass) runAnalysisClassesIt.next();
    	    Iterator methodsIt = appClass.getMethods().iterator();
    	    while (methodsIt.hasNext())
    	    {
    	    	SootMethod method = (SootMethod) methodsIt.next();
    	    	
    	    	// If this method may have a start or run method as a target, then do a start/join analysis
    	    	boolean mayHaveStartStmt = false;
    			Iterator edgesIt = callGraph.edgesOutOf( method );
    			while(edgesIt.hasNext())
    			{
    				SootMethod target = ((Edge) edgesIt.next()).tgt();
    				if(target.getName().equals("start") || target.getName().equals("run"))
    					mayHaveStartStmt = true;
    			}
    	    	
				if(mayHaveStartStmt && method.isConcrete())
				{
	    	    	Body b = method.retrieveActiveBody();
    		    	
    	    		// run the intraprocedural analysis
    				StartJoinAnalysis sja = new StartJoinAnalysis(new ExceptionalUnitGraph(b), method, callGraph, pag);
    				
    				// Add to interprocedural results
    				startStatements.addAll(sja.getStartStatements());
    				joinStatements.addAll(sja.getJoinStatements());
    				startToRunMethods.putAll(sja.getStartToRunMethods());
    				startToAllocNodes.putAll(sja.getStartToAllocNodes());
    				startToJoin.putAll(sja.getStartToJoin());
    				Iterator<Stmt> startIt = sja.getStartStatements().iterator();
    				while(startIt.hasNext())
    				{
    					Stmt start = startIt.next();
    					startToContainingMethod.put(start, method);
    				}
    			}
    		}
    	}
	}
	
	public Set<Stmt> getStartStatements()
	{
		return startStatements;
	}
	
	public Set<Stmt> getJoinStatements()
	{
		return joinStatements;
	}

	public Map<Stmt, List<SootMethod>> getStartToRunMethods()
	{
		return startToRunMethods;
	}

	public Map<Stmt, List<AllocNode>> getStartToAllocNodes()
	{
		return startToAllocNodes;
	}
	
	public Map<Stmt, Stmt> getStartToJoin()
	{
		return startToJoin;
	}
	
	public Map<Stmt, SootMethod> getStartToContainingMethod()
	{
		return startToContainingMethod;
	}
}

