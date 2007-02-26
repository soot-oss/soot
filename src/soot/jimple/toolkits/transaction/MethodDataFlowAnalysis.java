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
	
	Ref returnRef;
	
	public MethodDataFlowAnalysis(UnitGraph g, DataFlowAnalysis dfa, boolean ignoreNonRefTypeFlow)
	{
		super(g);
		this.dfa = dfa;
		this.refOnly = ignoreNonRefTypeFlow;
		
		this.returnRef = new ParameterRef(g.getBody().getMethod().getReturnType(), -1); // it's a dummy parameter ref
		
		doAnalysis();
	}

	protected void merge(Object in1, Object in2, Object out)
	{
		FlowSet inSet1 = (FlowSet) in1;
		FlowSet inSet2 = (FlowSet) in2;
		FlowSet outSet = (FlowSet) out;

		inSet1.intersection(inSet2, outSet);
	}
	
	private boolean isNonRefType(Type type)
	{
		return !(type instanceof RefType);
	}
	
	private boolean ignoreThisDataType(Type type)
	{
		return refOnly && isNonRefType(type);
	}
	
	private ArrayList getDirectSources(Value v, FlowSet fs)
	{
		ArrayList ret = new ArrayList(); // of Refs
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
		ArrayList sources = getDirectSources(v, fs); // of Refs
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
	private void handleFlowsToValue(Value sink, Value initialSource, FlowSet fs)
	{
		List sources = getSources(initialSource, fs); // list of Refs... returns all other sources
		if(initialSource instanceof Ref)
			sources.add(initialSource);
		Iterator sourcesIt = sources.iterator();
		while(sourcesIt.hasNext())
		{
			Value source = (Value) sourcesIt.next();
			Pair pair = new Pair(new EquivalentValue(sink), new EquivalentValue(source));
			if(!fs.contains(pair))
				fs.add(pair);
		}
	}
	
	// for when data flows to the data structure pointed to by a local
	private void handleFlowsToDataStructure(Value base, Value initialSource, FlowSet fs)
	{
		List sinks = getSources(base, fs);
		List sources = getSources(initialSource, fs);
		if(initialSource instanceof Ref)
			sources.add(initialSource);
		Iterator sourcesIt = sources.iterator();
		while(sourcesIt.hasNext())
		{
			Value source = (Value) sourcesIt.next();
			Iterator sinksIt = sinks.iterator();
			while(sinksIt.hasNext())
			{
				Value sink = (Value) sinksIt.next();
				Pair pair = new Pair(new EquivalentValue(sink), new EquivalentValue(source));
				if(!fs.contains(pair))
					fs.add(pair);
			}
		}
	}
	
	// handles the invoke expression AND returns a list of the return value's sources
	private List handleInvokeExpr(InvokeExpr ie, FlowSet fs)
	{
		// get the data flow graph
		
		
		// for each node
			// if the node is a parameter
				// source = argument
			// if the node is a static field
				// source = node
			// if the node is a field
				// source = receiver object
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
		
		// return the list of return value sources
		return null;
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
				if(ifr.getBase() == ((UnitGraph) graph).getBody().getThisLocal()) // data flows into the field ref
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
				interestingFlow = false;
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
				if(ifr.getBase() == ((UnitGraph) graph).getBody().getThisLocal()) // data flows from the field ref
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
				interestingFlow = false;
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
		return new ArraySparseSet();
	}
	
	protected Object newInitialFlow()
	{
		return new ArraySparseSet();
	}	
}

