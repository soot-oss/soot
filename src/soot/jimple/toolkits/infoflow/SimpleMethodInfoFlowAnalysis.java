package soot.jimple.toolkits.infoflow;

import soot.*;

import java.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.jimple.internal.*;
import soot.jimple.*;

// SimpleMethodInfoFlowAnalysis written by Richard L. Halpert, 2007-02-25
// Constructs a data flow table for the given method.  Ignores indirect flow.
// These tables conservatively approximate how data flows from parameters,
// fields, and globals to parameters, fields, globals, and the return value.
// Note that a ref-type parameter (or field or global) might allow access to a
// large data structure, but that entire structure will be represented only by
// the parameter's one node in the data flow graph.

public class SimpleMethodInfoFlowAnalysis extends ForwardFlowAnalysis
{
	SootMethod sm;
	Value thisLocal;
	InfoFlowAnalysis dfa;
	boolean refOnly;
	
	MutableDirectedGraph infoFlowGraph;
	Ref returnRef;
	
	FlowSet entrySet;
	FlowSet emptySet;
	
	boolean printMessages;
	
	public static int counter = 0;
	
	public SimpleMethodInfoFlowAnalysis(UnitGraph g, InfoFlowAnalysis dfa, boolean ignoreNonRefTypeFlow)
	{
		this(g, dfa, ignoreNonRefTypeFlow, true);
		
		counter++;
		
		// Add all of the nodes necessary to ensure that this is a complete data flow graph
		
		// Add every parameter of this method
		for(int i = 0; i < sm.getParameterCount(); i++)
		{
			EquivalentValue parameterRefEqVal = InfoFlowAnalysis.getNodeForParameterRef(sm, i);
			if(!infoFlowGraph.containsNode(parameterRefEqVal))
				infoFlowGraph.addNode(parameterRefEqVal);
		}
		
		// Add every field of this class
		for(Iterator it = sm.getDeclaringClass().getFields().iterator(); it.hasNext(); )
		{
			SootField sf = (SootField) it.next();
			EquivalentValue fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, sf);
			if(!infoFlowGraph.containsNode(fieldRefEqVal))
				infoFlowGraph.addNode(fieldRefEqVal);
		}
		
		// Add every field of this class's superclasses
		SootClass superclass = sm.getDeclaringClass();
		if(superclass.hasSuperclass())
			superclass = sm.getDeclaringClass().getSuperclass();
		while(superclass.hasSuperclass()) // we don't want to process Object
		{
	        Iterator scFieldsIt = superclass.getFields().iterator();
	        while(scFieldsIt.hasNext())
	        {
				SootField scField = (SootField) scFieldsIt.next();
				EquivalentValue fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, scField);
				if(!infoFlowGraph.containsNode(fieldRefEqVal))
					infoFlowGraph.addNode(fieldRefEqVal);
	        }
			superclass = superclass.getSuperclass();
		}
		
		// Add thisref of this class
		EquivalentValue thisRefEqVal = InfoFlowAnalysis.getNodeForThisRef(sm);
		if(!infoFlowGraph.containsNode(thisRefEqVal))
			infoFlowGraph.addNode(thisRefEqVal);
		
		// Add returnref of this method
		EquivalentValue returnRefEqVal = new CachedEquivalentValue(returnRef);
		if(!infoFlowGraph.containsNode(returnRefEqVal))
			infoFlowGraph.addNode(returnRefEqVal);
		
		if(printMessages)
			G.v().out.println("STARTING ANALYSIS FOR " + g.getBody().getMethod() + " -----");
		doFlowInsensitiveAnalysis();
		if(printMessages)
			G.v().out.println("ENDING   ANALYSIS FOR " + g.getBody().getMethod() + " -----");
	}
	
	/** A constructor that doesn't run the analysis */
	protected SimpleMethodInfoFlowAnalysis(UnitGraph g, InfoFlowAnalysis dfa, boolean ignoreNonRefTypeFlow, boolean dummyDontRunAnalysisYet)
	{
		super(g);
		this.sm = g.getBody().getMethod();
		if(sm.isStatic())
			this.thisLocal = null;
		else
			this.thisLocal = g.getBody().getThisLocal();
		this.dfa = dfa;
		this.refOnly = ignoreNonRefTypeFlow;
		
		this.infoFlowGraph = new MemoryEfficientGraph();
		this.returnRef = new ParameterRef(g.getBody().getMethod().getReturnType(), -1); // it's a dummy parameter ref
		
		this.entrySet = new ArraySparseSet();
		this.emptySet = new ArraySparseSet();
		
		printMessages = false;
	}
	
	public void doFlowInsensitiveAnalysis()
	{
		FlowSet fs = (FlowSet) newInitialFlow();
		boolean flowSetChanged = true;
		while(flowSetChanged)
		{
			int sizebefore = fs.size();
			Iterator stmtIt = graph.iterator();
			while(stmtIt.hasNext())
			{
				Stmt s = (Stmt) stmtIt.next();
				flowThrough(fs, s, fs);
			}
			if(fs.size() > sizebefore)
				flowSetChanged = true;
			else
				flowSetChanged = false;
		}
	}

	public MutableDirectedGraph getMethodInfoFlowSummary()
	{
		return infoFlowGraph;
	}

	protected void merge(Object in1, Object in2, Object out)
	{
		FlowSet inSet1 = (FlowSet) in1;
		FlowSet inSet2 = (FlowSet) in2;
		FlowSet outSet = (FlowSet) out;

		inSet1.union(inSet2, outSet);
	}
	
	protected boolean isNonRefType(Type type)
	{
		return !(type instanceof RefLikeType);
	}
	
	protected boolean ignoreThisDataType(Type type)
	{
		return refOnly && isNonRefType(type);
	}
	
	// Interesting sources are summarized (and possibly printed)
	public boolean isInterestingSource(Value source)
	{
		return (source instanceof Ref);
	}
	
	// Trackable sources are added to the flow set
	public boolean isTrackableSource(Value source)
	{
		return isInterestingSource(source) || (source instanceof Ref);
	}

	// Interesting sinks are possibly printed
	public boolean isInterestingSink(Value sink)
	{
		return (sink instanceof Ref);
	}
	
	// Trackable sinks are added to the flow set
	public boolean isTrackableSink(Value sink)
	{
		return isInterestingSink(sink) || (sink instanceof Ref) || (sink instanceof Local);
	}
	
	private ArrayList<Value> getDirectSources(Value v, FlowSet fs)
	{
		ArrayList<Value> ret = new ArrayList<Value>(); // of "interesting sources"
		EquivalentValue vEqVal = new CachedEquivalentValue(v);
		Iterator fsIt = fs.iterator();
		while(fsIt.hasNext())
		{
			Pair pair = (Pair) fsIt.next();
			if( pair.getO1().equals(vEqVal) )
				ret.add( ((EquivalentValue)pair.getO2()).getValue() );
		}
		return ret;
	}
	
	// For when data flows to a local
	protected void handleFlowsToValue(Value sink, Value initialSource, FlowSet fs)
	{
		if(!isTrackableSink(sink))
			return;

		List<Value> sources = getDirectSources(initialSource, fs); // list of Refs... returns all other sources
		if(isTrackableSource(initialSource))
			sources.add(initialSource);
		Iterator<Value> sourcesIt = sources.iterator();
		while(sourcesIt.hasNext())
		{
			Value source = sourcesIt.next();
			EquivalentValue sinkEqVal = new CachedEquivalentValue(sink);
			EquivalentValue sourceEqVal = new CachedEquivalentValue(source);
			if(sinkEqVal.equals(sourceEqVal))
				continue;
			Pair pair = new Pair(sinkEqVal, sourceEqVal);
			if(!fs.contains(pair))
			{
				fs.add(pair);
				if(isInterestingSource(source) && isInterestingSink(sink))
				{
					if(!infoFlowGraph.containsNode(sinkEqVal))
						infoFlowGraph.addNode(sinkEqVal);
					if(!infoFlowGraph.containsNode(sourceEqVal))
						infoFlowGraph.addNode(sourceEqVal);
					infoFlowGraph.addEdge(sourceEqVal, sinkEqVal);
					if(printMessages)
						G.v().out.println("      Found " + source + " flows to " + sink);
				}
			}
		}
	}
	
	// for when data flows to the data structure pointed to by a local
	protected void handleFlowsToDataStructure(Value base, Value initialSource, FlowSet fs)
	{
		List<Value> sinks = getDirectSources(base, fs);
		if(isTrackableSink(base))
			sinks.add(base);
		List<Value> sources = getDirectSources(initialSource, fs);
		if(isTrackableSource(initialSource))
			sources.add(initialSource);
		Iterator<Value> sourcesIt = sources.iterator();
		while(sourcesIt.hasNext())
		{
			Value source = sourcesIt.next();
			EquivalentValue sourceEqVal = new CachedEquivalentValue(source);
			Iterator<Value> sinksIt = sinks.iterator();
			while(sinksIt.hasNext())
			{
				Value sink = sinksIt.next();
				if(!isTrackableSink(sink))
					continue;
				EquivalentValue sinkEqVal = new CachedEquivalentValue(sink);
				if(sinkEqVal.equals(sourceEqVal))
					continue;
				Pair pair = new Pair(sinkEqVal, sourceEqVal);
				if(!fs.contains(pair))
				{
					fs.add(pair);
					if(isInterestingSource(source) && isInterestingSink(sink))
					{
						if(!infoFlowGraph.containsNode(sinkEqVal))
							infoFlowGraph.addNode(sinkEqVal);
						if(!infoFlowGraph.containsNode(sourceEqVal))
							infoFlowGraph.addNode(sourceEqVal);
						infoFlowGraph.addEdge(sourceEqVal, sinkEqVal);
						if(printMessages)
							G.v().out.println("      Found " + source + " flows to " + sink);
					}
				}
			}
		}
	}
	
	// handles the invoke expression AND returns a list of the return value's sources
		// for each node
			// if the node is a parameter
				// source = argument <Immediate>
			// if the node is a static field
				// source = node <StaticFieldRef>
			// if the node is a field
				// source = receiver object <Local>
			// if the node is the return value
				// continue
				
			// for each sink
				// if the sink is a parameter
					// handleFlowsToDataStructure(sink, source, fs)
				// if the sink is a static field
					// handleFlowsToValue(sink, source, fs)
				// if the sink is a field
					// handleFlowsToDataStructure(receiver object, source, fs)
				// if the sink is the return value
					// add node to list of return value sources

	protected List handleInvokeExpr(InvokeExpr ie, Stmt is, FlowSet fs)
	{
		// get the data flow graph
		MutableDirectedGraph dataFlowGraph = dfa.getInvokeInfoFlowSummary(ie, is, sm); // must return a graph whose nodes are Refs!!!
//		if( ie.getMethodRef().resolve().getSubSignature().equals(new String("boolean remove(java.lang.Object)")) )
//		{
//			G.v().out.println("*!*!*!*!*!<boolean remove(java.lang.Object)> has FLOW SENSITIVE infoFlowGraph: ");
//			ClassInfoFlowAnalysis.printDataFlowGraph(infoFlowGraph);
//		}
		
		List returnValueSources = new ArrayList();
		
		Iterator<Object> nodeIt = dataFlowGraph.getNodes().iterator();
		while(nodeIt.hasNext())
		{
			EquivalentValue nodeEqVal = (EquivalentValue) nodeIt.next();
			
			if(!(nodeEqVal.getValue() instanceof Ref))
				throw new RuntimeException("Illegal node type in data flow graph:" + nodeEqVal.getValue() + " should be an object of type Ref.");
				
			Ref node = (Ref) nodeEqVal.getValue();
			
			Value source = null;
			
			if(node instanceof ParameterRef)
			{
				ParameterRef param = (ParameterRef) node;
				if(param.getIndex() == -1)
					continue;
				source = ie.getArg(param.getIndex()); // Immediate
			}
			else if(node instanceof StaticFieldRef)
			{
				source = node; // StaticFieldRef
			}
			else if(ie instanceof InstanceInvokeExpr && node instanceof InstanceFieldRef)
			{
				InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
				source = iie.getBase(); // Local
			}
			
			Iterator sinksIt = dataFlowGraph.getSuccsOf(nodeEqVal).iterator();
			while(sinksIt.hasNext())
			{
				EquivalentValue sinkEqVal = (EquivalentValue) sinksIt.next();
				Ref sink = (Ref) sinkEqVal.getValue();
				if(sink instanceof ParameterRef)
				{
					ParameterRef param = (ParameterRef) sink;
					if(param.getIndex() == -1)
					{
						returnValueSources.add(source);
					}
					else
					{
						handleFlowsToDataStructure(ie.getArg(param.getIndex()), source, fs);
					}
				}
				else if(sink instanceof StaticFieldRef)
				{
					handleFlowsToValue(sink, source, fs);
				}
				else if(ie instanceof InstanceInvokeExpr && sink instanceof InstanceFieldRef)
				{
					InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
					handleFlowsToDataStructure(iie.getBase(), source, fs);
				}
			}
		}
				
		// return the list of return value sources
		return returnValueSources;
	}
	
	protected void flowThrough(Object inValue, Object unit,
			Object outValue)
	{
		FlowSet in  = (FlowSet) inValue;
		FlowSet out = (FlowSet) outValue;
		Stmt stmt = (Stmt) unit;
		
		if(in != out) // this method is reused for flow insensitive analysis, which uses the same FlowSet for in and out
			in.copy(out);
		FlowSet changedFlow = out;
		
		// Calculate the minimum subset of the flow set that we need to consider - OBSELETE optimization
//		FlowSet changedFlow = new ArraySparseSet();
//		FlowSet oldFlow = new ArraySparseSet();
//		out.copy(oldFlow);
//		in.union(out, out);
//		out.difference(oldFlow, changedFlow);
		
/*
		Iterator changedFlowIt = changedFlow.iterator();
		while(changedFlowIt.hasNext())
		{
			Pair pair = (Pair) changedFlowIt.next();
			EquivalentValue defEqVal = (EquivalentValue) pair.getO1();
			Value def = defEqVal.getValue();
			boolean defIsUsed = false;
			Iterator usesIt = stmt.getUseBoxes().iterator();
			while(usesIt.hasNext())
			{
				Value use = ((ValueBox) usesIt.next()).getValue();
				if(use.equivTo(def))
					defIsUsed = true;
			}
			if(!defIsUsed)
				changedFlow.remove(pair);
		}
*/
		
		// Bail out if there's nothing to consider, unless this might be the first run
//		if(changedFlow.isEmpty() && !oldFlow.equals(emptySet))
//			return;
		

		if(stmt instanceof IdentityStmt) // assigns an IdentityRef to a Local
		{
			IdentityStmt is = (IdentityStmt) stmt;
			IdentityRef ir = (IdentityRef) is.getRightOp();
			
			if(ir instanceof JCaughtExceptionRef)
			{
				// TODO: What the heck do we do with this???
			}
			else if(ir instanceof ParameterRef)
			{
				if( !ignoreThisDataType(ir.getType()) )
				{
					// <Local, ParameterRef and sources>
					handleFlowsToValue(is.getLeftOp(), ir, changedFlow);
				}
			}
			else if(ir instanceof ThisRef)
			{
				if( !ignoreThisDataType(ir.getType()) )
				{
					// <Local, ThisRef and sources>
					handleFlowsToValue(is.getLeftOp(), ir, changedFlow);
				}
			}
		}
		else if(stmt instanceof ReturnStmt) // assigns an Immediate to the "returnRef"
		{
			ReturnStmt rs = (ReturnStmt) stmt;
			Value rv = rs.getOp();
			if(rv instanceof Constant)
			{
				// No (interesting) data flow
			}
			else if(rv instanceof Local)
			{
				if( !ignoreThisDataType(rv.getType()) )
				{
					// <ReturnRef, sources of Local>
					handleFlowsToValue(returnRef, rv, changedFlow);
				}
			}
		}
		else if(stmt instanceof AssignStmt) // assigns a Value to a Variable
		{
			AssignStmt as = (AssignStmt) stmt;
			Value lv = as.getLeftOp();
			Value rv = as.getRightOp();
			
			Value sink = null;
			boolean flowsToDataStructure = false;
			
			if(lv instanceof Local) // data flows into the Local
			{
				sink = lv;
			}
			else if(lv instanceof ArrayRef) // data flows into the base's data structure
			{
				ArrayRef ar = (ArrayRef) lv;
				sink = ar.getBase();
				flowsToDataStructure = true;
			}
			else if(lv instanceof StaticFieldRef) // data flows into the field ref
			{
				sink = lv;
			}
			else if(lv instanceof InstanceFieldRef)
			{
				InstanceFieldRef ifr = (InstanceFieldRef) lv;
				if( ifr.getBase() == thisLocal ) // data flows into the field ref
				{
					sink = lv;
				}
				else // data flows into the base's data structure
				{
					sink = ifr.getBase();
					flowsToDataStructure = true;
				}
			}
			
			List sources = new ArrayList();
			boolean interestingFlow = true;
			
			if(rv instanceof Local)
			{
				sources.add(rv);
				interestingFlow = !ignoreThisDataType(rv.getType());
			}
			else if(rv instanceof Constant)
			{
				sources.add(rv);
				interestingFlow = !ignoreThisDataType(rv.getType());
			}
			else if(rv instanceof ArrayRef) // data flows from the base's data structure
			{
				ArrayRef ar = (ArrayRef) rv;
				sources.add(ar.getBase());
				interestingFlow = !ignoreThisDataType(ar.getType());
			}
			else if(rv instanceof StaticFieldRef)
			{
				sources.add(rv);
				interestingFlow = !ignoreThisDataType(rv.getType());
			}
			else if(rv instanceof InstanceFieldRef)
			{
				InstanceFieldRef ifr = (InstanceFieldRef) rv;
				if( ifr.getBase() == thisLocal ) // data flows from the field ref
				{
					sources.add(rv);
					interestingFlow = !ignoreThisDataType(rv.getType());
				}
				else // data flows from the base's data structure
				{
					sources.add(ifr.getBase());
					interestingFlow = !ignoreThisDataType(ifr.getType());
				}
			}
			else if(rv instanceof AnyNewExpr)
			{
				sources.add(rv);
				interestingFlow = !ignoreThisDataType(rv.getType());
			}
			else if(rv instanceof BinopExpr)
			{
				BinopExpr be = (BinopExpr) rv;
				sources.add(be.getOp1());
				sources.add(be.getOp2());
				interestingFlow = !ignoreThisDataType(be.getType());
			}
			else if(rv instanceof CastExpr)
			{
				CastExpr ce = (CastExpr) rv;
				sources.add(ce.getOp());
				interestingFlow = !ignoreThisDataType(ce.getType());
			}
			else if(rv instanceof InstanceOfExpr)
			{
				InstanceOfExpr ioe = (InstanceOfExpr) rv;
				sources.add(ioe.getOp());
				interestingFlow = !ignoreThisDataType(ioe.getType());
			}
			else if(rv instanceof UnopExpr)
			{
				UnopExpr ue = (UnopExpr) rv;
				sources.add(ue.getOp());
				interestingFlow = !ignoreThisDataType(ue.getType());
			}
			else if(rv instanceof InvokeExpr)
			{
				InvokeExpr ie = (InvokeExpr) rv;
				sources.addAll(handleInvokeExpr(ie, as, changedFlow));
				interestingFlow = !ignoreThisDataType(ie.getType());
			}
			
			if(interestingFlow)
			{
				if(flowsToDataStructure)
				{
					Iterator sourcesIt = sources.iterator();
					while(sourcesIt.hasNext())
					{
						Value source = (Value) sourcesIt.next();
						handleFlowsToDataStructure(sink, source, changedFlow);
					}
				}
				else
				{
					Iterator sourcesIt = sources.iterator();
					while(sourcesIt.hasNext())
					{
						Value source = (Value) sourcesIt.next();
						handleFlowsToValue(sink, source, changedFlow);
					}
				}
			}
		}
		else if(stmt.containsInvokeExpr()) // flows data between receiver object, parameters, globals, and return value
		{
			handleInvokeExpr(stmt.getInvokeExpr(), stmt, changedFlow);
		}
		
//		changedFlow.union(out, out); - OBSELETE optimization
	}
	
	protected void copy(Object source, Object dest)
	{
		
		FlowSet sourceSet = (FlowSet) source;
		FlowSet destSet   = (FlowSet) dest;
		
		sourceSet.copy(destSet);
		
	}
	
	protected Object entryInitialFlow()
	{
		return entrySet.clone();
	}
	
	protected Object newInitialFlow()
	{
		return emptySet.clone();
	}
	
	public void addToEntryInitialFlow(Value source, Value sink)
	{
		EquivalentValue sinkEqVal = new CachedEquivalentValue(sink);
		EquivalentValue sourceEqVal = new CachedEquivalentValue(source);
		if(sinkEqVal.equals(sourceEqVal))
			return;
		Pair pair = new Pair(sinkEqVal, sourceEqVal);
		if(!entrySet.contains(pair))
		{
			entrySet.add(pair);
		}
	}
	
	public void addToNewInitialFlow(Value source, Value sink)
	{
		EquivalentValue sinkEqVal = new CachedEquivalentValue(sink);
		EquivalentValue sourceEqVal = new CachedEquivalentValue(source);
		if(sinkEqVal.equals(sourceEqVal))
			return;
		Pair pair = new Pair(sinkEqVal, sourceEqVal);
		if(!emptySet.contains(pair))
		{
			emptySet.add(pair);
		}
	}
	
	public Value getThisLocal()
	{
		return thisLocal;
	}
}

