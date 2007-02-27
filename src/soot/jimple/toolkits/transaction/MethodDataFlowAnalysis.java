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

// MethodDataFlowAnalysis written by Richard L. Halpert, 2007-02-25
// Constructs a data flow table for the given method.  Ignores indirect flow.
// These tables conservatively approximate how data flows from parameters,
// fields, and globals to parameters, fields, globals, and the return value.
// Note that a ref-type parameter (or field or global) might allow access to a
// large data structure, but that entire structure will be represented only by
// the parameter's one node in the data flow graph.

public class MethodDataFlowAnalysis extends ForwardFlowAnalysis
{
	DataFlowAnalysis dfa;
	boolean refOnly;
	
	MutableDirectedGraph dataFlowGraph;
	Ref returnRef;
	
	FlowSet entrySet;
	FlowSet emptySet;
	
	boolean printMessages;
	
	public MethodDataFlowAnalysis(UnitGraph g, DataFlowAnalysis dfa, boolean ignoreNonRefTypeFlow)
	{
		super(g);
		this.dfa = dfa;
		this.refOnly = ignoreNonRefTypeFlow;
		
		this.dataFlowGraph = new MemoryEfficientGraph();
		this.returnRef = new ParameterRef(g.getBody().getMethod().getReturnType(), -1); // it's a dummy parameter ref
		
		this.entrySet = new ArraySparseSet();
		this.emptySet = new ArraySparseSet();
		
		printMessages = true;
		
		if(printMessages)
			G.v().out.println("----- STARTING ANALYSIS FOR " + g.getBody().getMethod() + " -----");
		doAnalysis();
		if(printMessages)
			G.v().out.println("-----   ENDING ANALYSIS FOR " + g.getBody().getMethod() + " -----");
	}
	
	protected MethodDataFlowAnalysis(UnitGraph g, DataFlowAnalysis dfa, boolean ignoreNonRefTypeFlow, boolean dummyDontRunAnalysisYet)
	{
		super(g);
		this.dfa = dfa;
		this.refOnly = ignoreNonRefTypeFlow;
		
		this.dataFlowGraph = new MemoryEfficientGraph();
		this.returnRef = new ParameterRef(g.getBody().getMethod().getReturnType(), -1); // it's a dummy parameter ref
		
		this.entrySet = new ArraySparseSet();
		this.emptySet = new ArraySparseSet();
	}

	public MutableDirectedGraph getDataFlowGraph()
	{
		return dataFlowGraph;
	}

	protected void merge(Object in1, Object in2, Object out)
	{
		FlowSet inSet1 = (FlowSet) in1;
		FlowSet inSet2 = (FlowSet) in2;
		FlowSet outSet = (FlowSet) out;

		inSet1.intersection(inSet2, outSet);
	}
	
	protected boolean isNonRefType(Type type)
	{
		return !(type instanceof RefType);
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
	
	private ArrayList getDirectSources(Value v, FlowSet fs)
	{
		ArrayList ret = new ArrayList(); // of "interesting sources"
		EquivalentValue vEqVal = new EquivalentValue(v);
		Iterator fsIt = fs.iterator();
		while(fsIt.hasNext())
		{
			Pair pair = (Pair) fsIt.next();
			if( pair.getO1().equals(vEqVal) )
				ret.add( ((EquivalentValue)pair.getO2()).getValue() );
		}
		return ret;
	}
	
	private List getSources(Value v, FlowSet fs) // returns all sources, including sources of sources (recursively) (but does not include v, since it might just be a sink)
	{		
		ArrayList sources = getDirectSources(v, fs); // of "interesting sources"
		for(int i = 0; i < sources.size(); i++)
		{
			Value currentSource = (Value) sources.get(i);
			List newSources = getDirectSources(currentSource, fs);
			Iterator newSourcesIt = newSources.iterator();
			while(newSourcesIt.hasNext())
			{
				Value newSource = (Value) newSourcesIt.next();
				if(!sources.contains(newSource))
					sources.add(newSource);
			}
		}
		return sources;
	}

	// For when data flows to a local
	protected void handleFlowsToValue(Value sink, Value initialSource, FlowSet fs)
	{
		List sources = getSources(initialSource, fs); // list of Refs... returns all other sources
		if(isTrackableSource(initialSource))
			sources.add(initialSource);
		Iterator sourcesIt = sources.iterator();
		while(sourcesIt.hasNext())
		{
			Value source = (Value) sourcesIt.next();
			EquivalentValue sinkEqVal = new EquivalentValue(sink);
			EquivalentValue sourceEqVal = new EquivalentValue(source);
			if(sinkEqVal.equals(sourceEqVal))
				continue;
			Pair pair = new Pair(sinkEqVal, sourceEqVal);
			if(!fs.contains(pair))
			{
				fs.add(pair);
				if(isInterestingSource(source) && isInterestingSink(sink))
				{
					if(!dataFlowGraph.containsNode(sinkEqVal))
						dataFlowGraph.addNode(sinkEqVal);
					if(!dataFlowGraph.containsNode(sourceEqVal))
						dataFlowGraph.addNode(sourceEqVal);
					dataFlowGraph.addEdge(sourceEqVal, sinkEqVal);
					if(printMessages)
						G.v().out.println("Found " + source + " flows to " + sink);
				}
			}
		}
	}
	
	// for when data flows to the data structure pointed to by a local
	protected void handleFlowsToDataStructure(Value base, Value initialSource, FlowSet fs)
	{
		List sinks = getSources(base, fs);
		sinks.add(base);
		List sources = getSources(initialSource, fs);
		if(isTrackableSource(initialSource))
			sources.add(initialSource);
		Iterator sourcesIt = sources.iterator();
		while(sourcesIt.hasNext())
		{
			Value source = (Value) sourcesIt.next();
			EquivalentValue sourceEqVal = new EquivalentValue(source);
			Iterator sinksIt = sinks.iterator();
			while(sinksIt.hasNext())
			{
				Value sink = (Value) sinksIt.next();
				EquivalentValue sinkEqVal = new EquivalentValue(sink);
				if(sinkEqVal.equals(sourceEqVal))
					continue;
				Pair pair = new Pair(sinkEqVal, sourceEqVal);
				if(!fs.contains(pair))
				{
					fs.add(pair);
					if(isInterestingSource(source) && isInterestingSink(sink))
					{
						if(!dataFlowGraph.containsNode(sinkEqVal))
							dataFlowGraph.addNode(sinkEqVal);
						if(!dataFlowGraph.containsNode(sourceEqVal))
							dataFlowGraph.addNode(sourceEqVal);
						dataFlowGraph.addEdge(sourceEqVal, sinkEqVal);
						if(printMessages)
							G.v().out.println("Found " + source + " flows to " + sink);
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

	protected List handleInvokeExpr(InvokeExpr ie, FlowSet fs)
	{
		// get the data flow graph
		MutableDirectedGraph dataFlowGraph = dfa.getDataFlowGraphOf(ie); // must return a graph whose nodes are Refs!!!
		
		List returnValueSources = new ArrayList();
		
		Iterator nodeIt = dataFlowGraph.getNodes().iterator();
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
		
		out.clear();
		in.copy(out);
		
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
					handleFlowsToValue(is.getLeftOp(), ir, out);
				}
			}
			else if(ir instanceof ThisRef)
			{
				if( !ignoreThisDataType(ir.getType()) )
				{
					// <Local, ThisRef and sources>
					handleFlowsToValue(is.getLeftOp(), ir, out);
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
					handleFlowsToValue(returnRef, rv, out);
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
				if(!((UnitGraph) graph).getBody().getMethod().isStatic() && 
					ifr.getBase().equivTo(((UnitGraph) graph).getBody().getThisLocal())) // data flows into the field ref
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
				if( (!((UnitGraph) graph).getBody().getMethod().isStatic()) && 
					ifr.getBase().equivTo(((UnitGraph) graph).getBody().getThisLocal())) // data flows from the field ref
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
				sources.addAll(handleInvokeExpr(ie, out));
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
						handleFlowsToDataStructure(sink, source, out);
					}
				}
				else
				{
					Iterator sourcesIt = sources.iterator();
					while(sourcesIt.hasNext())
					{
						Value source = (Value) sourcesIt.next();
						handleFlowsToValue(sink, source, out);
					}
				}
			}
		}
		else if(stmt.containsInvokeExpr()) // flows data between receiver object, parameters, globals, and return value
		{
			handleInvokeExpr(stmt.getInvokeExpr(), out);
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
		return entrySet.clone();
	}
	
	protected Object newInitialFlow()
	{
		return emptySet.clone();
	}
	
	public void addToEntryInitialFlow(Value source, Value sink)
	{
		EquivalentValue sinkEqVal = new EquivalentValue(sink);
		EquivalentValue sourceEqVal = new EquivalentValue(source);
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
		EquivalentValue sinkEqVal = new EquivalentValue(sink);
		EquivalentValue sourceEqVal = new EquivalentValue(source);
		if(sinkEqVal.equals(sourceEqVal))
			return;
		Pair pair = new Pair(sinkEqVal, sourceEqVal);
		if(!emptySet.contains(pair))
		{
			emptySet.add(pair);
		}
	}
}

