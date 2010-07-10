package soot.jimple.toolkits.thread.mhp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Hierarchy;
import soot.Local;
import soot.MethodOrMethodContext;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Filter;
import soot.jimple.toolkits.callgraph.TransitiveTargets;
import soot.jimple.toolkits.pointer.LocalMustAliasAnalysis;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.MHGPostDominatorsFinder;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

// StartJoinFinder written by Richard L. Halpert, 2006-12-04
// This can be used as an alternative to PegGraph and PegChain
// if only thread start, join, and type information is needed

// This is implemented as a real flow analysis so that, in the future,
// flow information can be used to match starts with joins

public class StartJoinAnalysis extends ForwardFlowAnalysis
{
	Set<Stmt> startStatements;
	Set<Stmt> joinStatements;
	
	Hierarchy hierarchy;
	
	Map<Stmt, List<SootMethod>> startToRunMethods;
	Map<Stmt, List<AllocNode>> startToAllocNodes;
	Map<Stmt, Stmt> startToJoin;
	
	public StartJoinAnalysis(UnitGraph g, SootMethod sm, CallGraph callGraph, PAG pag)
	{
		super(g);
		
		startStatements = new HashSet<Stmt>();
		joinStatements = new HashSet<Stmt>();
		
		hierarchy = Scene.v().getActiveHierarchy();

		startToRunMethods = new HashMap<Stmt, List<SootMethod>>();
		startToAllocNodes = new HashMap<Stmt, List<AllocNode>>();
		startToJoin = new HashMap<Stmt, Stmt>();
		
		// Get lists of start and join statements		
		doFlowInsensitiveSingleIterationAnalysis();
		
		if(!startStatements.isEmpty())
		{
			// Get supporting info and analyses
			MHGPostDominatorsFinder pd = new MHGPostDominatorsFinder(new BriefUnitGraph(sm.getActiveBody()));
			//EqualUsesAnalysis lif = new EqualUsesAnalysis(g);
			LocalMustAliasAnalysis lma = new LocalMustAliasAnalysis(g);
			TransitiveTargets runMethodTargets = new TransitiveTargets( callGraph, new Filter(new RunMethodsPred()) );
			
			// Build a map from start stmt to possible run methods, 
			// and a map from start stmt to possible allocation nodes,
			// and a map from start stmt to guaranteed join stmt
			Iterator<Stmt> startIt = startStatements.iterator();
			while (startIt.hasNext())
			{
				Stmt start = startIt.next();
				
				List<SootMethod> runMethodsList = new ArrayList<SootMethod>(); // will be a list of possible run methods called by this start stmt
				List<AllocNode> allocNodesList = new ArrayList<AllocNode>(); // will be a list of possible allocation nodes for the thread object that's getting started
				
				// Get possible thread objects (may alias)
				Value startObject = ((InstanceInvokeExpr) (start).getInvokeExpr()).getBase();
				PointsToSetInternal pts = (PointsToSetInternal) pag.reachingObjects((Local) startObject);
				List<AllocNode> mayAlias = getMayAliasList(pts);
				if( mayAlias.size() < 1 )
					continue; // If the may alias is empty, this must be dead code
					
				// For each possible thread object, get run method
				Iterator<MethodOrMethodContext> mayRunIt = runMethodTargets.iterator( start ); // fails for some call graphs
				while( mayRunIt.hasNext() )
				{
					SootMethod runMethod = (SootMethod) mayRunIt.next();
					if( runMethod.getSubSignature().equals("void run()") )
					{
						runMethodsList.add(runMethod);
					}
				}
				
				// If haven't found any run methods, then use the type of the startObject,
				// and add run from it and all subclasses
				if(runMethodsList.isEmpty() && ((RefType) startObject.getType()).getSootClass().isApplicationClass())
				{
					List<SootClass> threadClasses = hierarchy.getSubclassesOfIncluding( ((RefType) startObject.getType()).getSootClass() );
					Iterator<SootClass> threadClassesIt = threadClasses.iterator();
					while(threadClassesIt.hasNext())
					{
						SootClass currentClass = threadClassesIt.next();
						if( currentClass.declaresMethod("void run()") )							
						{
							runMethodsList.add(currentClass.getMethod("void run()"));
						}
					}
				}

				// For each possible thread object, get alloc node
				Iterator<AllocNode> mayAliasIt = mayAlias.iterator();
				while( mayAliasIt.hasNext() )
				{
					AllocNode allocNode = mayAliasIt.next();
					allocNodesList.add(allocNode);
					if(runMethodsList.isEmpty())
					{
						throw new RuntimeException("Can't find run method for: " + startObject);	
/*
						if( allocNode.getType() instanceof RefType )
						{
							List threadClasses = hierarchy.getSubclassesOf(((RefType) allocNode.getType()).getSootClass());
							Iterator threadClassesIt = threadClasses.iterator();
							while(threadClassesIt.hasNext())
							{
								SootClass currentClass = (SootClass) threadClassesIt.next();
								if( currentClass.declaresMethod("void run()") )							
								{
									runMethodsList.add(currentClass.getMethod("void run()"));
								}
							}
						}
*/					
					}			
				}
				
				// Add this start stmt to both maps
				startToRunMethods.put(start, runMethodsList);
				startToAllocNodes.put(start, allocNodesList);
				
				// does this start stmt match any join stmt???
				Iterator<Stmt> joinIt = joinStatements.iterator();
				while (joinIt.hasNext())
				{
					Stmt join = joinIt.next();
					Value joinObject = ((InstanceInvokeExpr) (join).getInvokeExpr()).getBase();
					
					// If startObject and joinObject MUST be the same, and if join post-dominates start
					List barriers = new ArrayList();
					barriers.addAll(g.getSuccsOf(join)); // definitions of the start variable are tracked until they pass a join
//					if( lif.areEqualUses( start, (Local) startObject, join, (Local) joinObject, barriers) )
					if(lma.mustAlias((Local) startObject, start, (Local) joinObject, join))
					{
						if((pd.getDominators(start)).contains(join)) // does join post-dominate start?
						{
//							G.v().out.println("START-JOIN PAIR: " + start + ", " + join);
							startToJoin.put(start, join); // then this join always joins this start's thread
						}
					}
				}
			}
		}
	}
	
	private List<AllocNode> getMayAliasList(PointsToSetInternal pts)
	{
		List<AllocNode> list = new ArrayList<AllocNode>();
		final HashSet<AllocNode> ret = new HashSet<AllocNode>();
		pts.forall( new P2SetVisitor() {
			public void visit( Node n ) {
				
				ret.add( (AllocNode)n );
			}
		} );
		Iterator<AllocNode> it = ret.iterator();
		while (it.hasNext()){
			list.add( it.next() );
		}
		return list;
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
	
	public void doFlowInsensitiveSingleIterationAnalysis()
	{
		FlowSet fs = (FlowSet) newInitialFlow();
		Iterator stmtIt = graph.iterator();
		while(stmtIt.hasNext())
		{
			Stmt s = (Stmt) stmtIt.next();
			flowThrough(fs, s, fs);
		}
	}
	
	protected void merge(Object in1, Object in2, Object out)
	{
		FlowSet inSet1 = (FlowSet) in1;
		FlowSet inSet2 = (FlowSet) in2;
		FlowSet outSet = (FlowSet) out;
		
		inSet1.intersection(inSet2, outSet);
	}
	
	protected void flowThrough(Object inValue, Object unit,
			Object outValue)
	{
		Stmt stmt = (Stmt) unit;
		
/*
		in.copy(out);

		// get list of definitions at this unit
		List newDefs = new ArrayList();
		if(stmt instanceof DefinitionStmt)
		{
			Value leftOp = ((DefinitionStmt)stmt).getLeftOp();
			if(leftOp instanceof Local)
				newDefs.add((Local) leftOp);
		}
		
		// kill any start stmt whose base has been redefined
		Iterator outIt = out.iterator();
		while(outIt.hasNext())
		{
			Stmt outStmt = (Stmt) outIt.next();
			if(newDefs.contains((Local) ((InstanceInvokeExpr) (outStmt).getInvokeExpr()).getBase()))
				out.remove(outStmt);
		}
*/				
		// Search for start/join invoke expressions
		if(stmt.containsInvokeExpr())
		{
			// If this is a start stmt, add it to startStatements
			InvokeExpr ie = stmt.getInvokeExpr();
			if(ie instanceof InstanceInvokeExpr)
			{
				InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
				SootMethod invokeMethod = ie.getMethod();
				if(invokeMethod.getName().equals("start"))
				{
					RefType baseType = (RefType) iie.getBase().getType();
					if(!baseType.getSootClass().isInterface()) // the start method we're looking for is NOT an interface method
					{
						List<SootClass> superClasses = hierarchy.getSuperclassesOfIncluding(baseType.getSootClass());
						Iterator<SootClass> it = superClasses.iterator();
						while (it.hasNext())
						{
							if( it.next().getName().equals("java.lang.Thread") )
							{
								// This is a Thread.start()
								if(!startStatements.contains(stmt))
									startStatements.add(stmt);
									
								// Flow this Thread.start() down
		//						out.add(stmt);
							}
						}
					}
				}

				// If this is a join stmt, add it to joinStatements
				if(invokeMethod.getName().equals("join")) // the join method we're looking for is NOT an interface method
				{
					RefType baseType = (RefType) iie.getBase().getType();
					if(!baseType.getSootClass().isInterface())
					{
						List<SootClass> superClasses = hierarchy.getSuperclassesOfIncluding(baseType.getSootClass());
						Iterator<SootClass> it = superClasses.iterator();
						while (it.hasNext())
						{
							if( it.next().getName().equals("java.lang.Thread") )
							{
								// This is a Thread.join()
								if(!joinStatements.contains(stmt))
									joinStatements.add(stmt);
							}
						}
					}
				}
			}
		}
	}
	
	protected void copy(Object source, Object dest)
	{
		
		FlowSet sourceSet = (FlowSet) source;
		FlowSet destSet   = (FlowSet) dest;
		
		sourceSet.copy(destSet);
		
	}
	
	protected Object entryInitialFlow()
	{
		return new ArraySparseSet();
	}
	
	protected Object newInitialFlow()
	{
		return new ArraySparseSet();
	}	
}

