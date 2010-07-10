package soot.jimple.toolkits.infoflow;

import soot.*;

import java.util.*;
import soot.toolkits.graph.*;
import soot.jimple.internal.*;
import soot.jimple.*;

// SimpleMethodInfoFlowAnalysis written by Richard L. Halpert, 2007-02-25
// Constructs a data flow table for the given method.  Ignores indirect flow.
// These tables conservatively approximate how data flows from parameters,
// fields, and globals to parameters, fields, globals, and the return value.
// Note that a ref-type parameter (or field or global) might allow access to a
// large data structure, but that entire structure will be represented only by
// the parameter's one node in the data flow graph.

public class SmartMethodInfoFlowAnalysis
{
	UnitGraph graph;
	SootMethod sm;
	Value thisLocal;
	InfoFlowAnalysis dfa;
	boolean refOnly; // determines if primitive type data flow is included
	boolean includeInnerFields; // determines if flow to a field of an object (other than this) is treated like flow to that object
	
	HashMutableDirectedGraph abbreviatedInfoFlowGraph;
	HashMutableDirectedGraph infoFlowSummary;
	Ref returnRef;
	
	boolean printMessages;
	
	public static int counter = 0;
	
	public SmartMethodInfoFlowAnalysis(UnitGraph g, InfoFlowAnalysis dfa)
	{
		graph = g;
		this.sm = g.getBody().getMethod();
		if(sm.isStatic())
			this.thisLocal = null;
		else
			this.thisLocal = g.getBody().getThisLocal();
		this.dfa = dfa;
		this.refOnly = !dfa.includesPrimitiveInfoFlow();
		this.includeInnerFields = dfa.includesInnerFields();
		
		this.abbreviatedInfoFlowGraph = new MemoryEfficientGraph();
		this.infoFlowSummary = new MemoryEfficientGraph();
		
		this.returnRef = new ParameterRef(g.getBody().getMethod().getReturnType(), -1); // it's a dummy parameter ref
		
//		this.entrySet = new ArraySparseSet();
//		this.emptySet = new ArraySparseSet();
		
		printMessages = false; //dfa.printDebug();
		
		counter++;
		
		// Add all of the nodes necessary to ensure that this is a complete data flow graph
		
		// Add every parameter of this method
		for(int i = 0; i < sm.getParameterCount(); i++)
		{
			EquivalentValue parameterRefEqVal = InfoFlowAnalysis.getNodeForParameterRef(sm, i);
			if(!infoFlowSummary.containsNode(parameterRefEqVal))
				infoFlowSummary.addNode(parameterRefEqVal);
		}
		
		// Add every relevant field of this class (static methods don't get non-static fields)
		for(Iterator it = sm.getDeclaringClass().getFields().iterator(); it.hasNext(); )
		{
			SootField sf = (SootField) it.next();
			if(sf.isStatic() || !sm.isStatic())
			{
				EquivalentValue fieldRefEqVal;
				if(!sm.isStatic())
					fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, sf, sm.retrieveActiveBody().getThisLocal());
				else
					fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, sf);
					
				if(!infoFlowSummary.containsNode(fieldRefEqVal))
					infoFlowSummary.addNode(fieldRefEqVal);
			}
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
				if(scField.isStatic() || !sm.isStatic())
				{
					EquivalentValue fieldRefEqVal;
					if(!sm.isStatic())
						fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, scField, sm.retrieveActiveBody().getThisLocal());
					else
						fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, scField);
					if(!infoFlowSummary.containsNode(fieldRefEqVal))
						infoFlowSummary.addNode(fieldRefEqVal);
				}
	        }
			superclass = superclass.getSuperclass();
		}
		
		// Add thisref of this class
		if(!sm.isStatic())
		{
			EquivalentValue thisRefEqVal = InfoFlowAnalysis.getNodeForThisRef(sm);
			if(!infoFlowSummary.containsNode(thisRefEqVal))
				infoFlowSummary.addNode(thisRefEqVal);
		}
		
		// Add returnref of this method
		EquivalentValue returnRefEqVal = new CachedEquivalentValue(returnRef);
		if(returnRef.getType() != VoidType.v() && !infoFlowSummary.containsNode(returnRefEqVal))
			infoFlowSummary.addNode(returnRefEqVal);
		
		// Do the analysis
		Date start = new Date();
		int counterSoFar = counter;
		if(printMessages)
			G.v().out.println("STARTING SMART ANALYSIS FOR " + g.getBody().getMethod() + " -----");
		
		// S=#Statements, R=#Refs, L=#Locals, where generally (S ~= L), (L >> R)
		// Generates a data flow graph of refs and locals where "flows to data structure" is represented in a single node
		generateAbbreviatedInfoFlowGraph(); // O(S)
		// Generates a data flow graph of refs where "flows to data structure" has been resolved
		generateInfoFlowSummary(); // O( R*(L+R) )
		
		if(printMessages)
		{
	    	long longTime = ((new Date()).getTime() - start.getTime());
	    	float time = (longTime) / 1000.0f;
			G.v().out.println("ENDING   SMART ANALYSIS FOR " + g.getBody().getMethod() + " ----- " + 
								(counter - counterSoFar + 1) + " analyses took: " + time + "s");
			G.v().out.println("  AbbreviatedDataFlowGraph:");
			InfoFlowAnalysis.printInfoFlowSummary(abbreviatedInfoFlowGraph);
			G.v().out.println("  DataFlowSummary:");
			InfoFlowAnalysis.printInfoFlowSummary(infoFlowSummary);
		}
	}
	
	public void generateAbbreviatedInfoFlowGraph()
	{
		Iterator stmtIt = graph.iterator();
		while(stmtIt.hasNext())
		{
			Stmt s = (Stmt) stmtIt.next();
			addFlowToCdfg(s);
		}
	}
	
	public void generateInfoFlowSummary()
	{
		Iterator nodeIt = infoFlowSummary.iterator();
		while(nodeIt.hasNext())
		{
			EquivalentValue node = (EquivalentValue) nodeIt.next();
			List<EquivalentValue> sources = sourcesOf(node);
			Iterator<EquivalentValue> sourcesIt = sources.iterator();
			while(sourcesIt.hasNext())
			{
				EquivalentValue source = sourcesIt.next();
				if(source.getValue() instanceof Ref)
				{
					infoFlowSummary.addEdge(source, node);
				}
			}
		}
	}
	
	public List<EquivalentValue> sourcesOf(EquivalentValue node) { return sourcesOf(node, new HashSet<EquivalentValue>(), new HashSet<EquivalentValue>()); }
	private List<EquivalentValue> sourcesOf(EquivalentValue node, Set<EquivalentValue> visitedSources, Set<EquivalentValue> visitedSinks)
	{
		visitedSources.add(node);
		
		List<EquivalentValue> ret = new LinkedList<EquivalentValue>();
		if(!abbreviatedInfoFlowGraph.containsNode(node))
			return ret;

		// get direct sources
		Set preds = abbreviatedInfoFlowGraph.getPredsOfAsSet(node);
		Iterator predsIt = preds.iterator();
		while(predsIt.hasNext())
		{
			EquivalentValue pred = (EquivalentValue) predsIt.next();
			if(!visitedSources.contains(pred))
			{
				ret.add(pred);
				ret.addAll(sourcesOf(pred, visitedSources, visitedSinks));
			}
		}
		
		// get sources of (sources of sinks, of which we are one)
		List<EquivalentValue> sinks = sinksOf(node, visitedSources, visitedSinks);
		Iterator<EquivalentValue> sinksIt = sinks.iterator();
		while(sinksIt.hasNext())
		{
			EquivalentValue sink = sinksIt.next();
			if(!visitedSources.contains(sink))
			{
				EquivalentValue flowsToSourcesOf = new CachedEquivalentValue(new AbstractDataSource(sink.getValue()));
				
				if( abbreviatedInfoFlowGraph.getPredsOfAsSet(sink).contains(flowsToSourcesOf) )
				{
					ret.addAll(sourcesOf(flowsToSourcesOf, visitedSources, visitedSinks));
				}
			}
		}
		return ret;
	}
	
	public List<EquivalentValue> sinksOf(EquivalentValue node) { return sinksOf(node, new HashSet<EquivalentValue>(), new HashSet<EquivalentValue>()); }
	private List<EquivalentValue> sinksOf(EquivalentValue node, Set<EquivalentValue> visitedSources, Set<EquivalentValue> visitedSinks)
	{
		List<EquivalentValue> ret = new LinkedList<EquivalentValue>();

//		if(visitedSinks.contains(node))
//			return ret;

		visitedSinks.add(node);
		
		if(!abbreviatedInfoFlowGraph.containsNode(node))
			return ret;

		// get direct sinks
		Set succs = abbreviatedInfoFlowGraph.getSuccsOfAsSet(node);
		Iterator succsIt = succs.iterator();
		while(succsIt.hasNext())
		{
			EquivalentValue succ = (EquivalentValue) succsIt.next();
			if(!visitedSinks.contains(succ))
			{
				ret.add(succ);
				ret.addAll(sinksOf(succ, visitedSources, visitedSinks));
			}
		}
		
		// get sources of (sources of sinks, of which we are one)
		succsIt = succs.iterator();
		while(succsIt.hasNext())
		{
			EquivalentValue succ = (EquivalentValue) succsIt.next();
			if(succ.getValue() instanceof AbstractDataSource)
			{
				// It will have ONE successor, who will be the value whose sources it represents
				Set vHolder = abbreviatedInfoFlowGraph.getSuccsOfAsSet(succ);
				EquivalentValue v = (EquivalentValue) vHolder.iterator().next(); // get the one and only
				if(!visitedSinks.contains(v))
				{
//					Set<EquivalentValue> 
					ret.addAll(sourcesOf(v, visitedSinks, visitedSinks)); // these nodes are really to be marked as sinks, not sources
				}
			}
		}
		return ret;
	}
	
	public HashMutableDirectedGraph getMethodInfoFlowSummary()
	{
		return infoFlowSummary;
	}
	
	public HashMutableDirectedGraph getMethodAbbreviatedInfoFlowGraph()
	{
		return abbreviatedInfoFlowGraph;
	}

	protected boolean isNonRefType(Type type)
	{
		return !(type instanceof RefLikeType);
	}
	
	protected boolean ignoreThisDataType(Type type)
	{
		return refOnly && isNonRefType(type);
	}

	// For when data flows to a local
	protected void handleFlowsToValue(Value sink, Value source)
	{
		EquivalentValue sinkEqVal;
		EquivalentValue sourceEqVal;
		
		if(sink instanceof InstanceFieldRef)
		{
			InstanceFieldRef ifr = (InstanceFieldRef) sink;
			sinkEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, ifr.getField(), (Local) ifr.getBase()); // deals with inner fields
		}
		else
			sinkEqVal = new CachedEquivalentValue(sink);
			
		if(source instanceof InstanceFieldRef)
		{
			InstanceFieldRef ifr = (InstanceFieldRef) source;
			sourceEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, ifr.getField(), (Local) ifr.getBase()); // deals with inner fields
		}
		else
			sourceEqVal = new CachedEquivalentValue(source);
		
		if( source instanceof Ref && !infoFlowSummary.containsNode(sourceEqVal))
			infoFlowSummary.addNode(sourceEqVal);
		if( sink instanceof Ref && !infoFlowSummary.containsNode(sinkEqVal))
			infoFlowSummary.addNode(sinkEqVal);
		
		if(!abbreviatedInfoFlowGraph.containsNode(sinkEqVal))
			abbreviatedInfoFlowGraph.addNode(sinkEqVal);
		if(!abbreviatedInfoFlowGraph.containsNode(sourceEqVal))
			abbreviatedInfoFlowGraph.addNode(sourceEqVal);
		
		abbreviatedInfoFlowGraph.addEdge(sourceEqVal, sinkEqVal);
	}
	
	// for when data flows to the data structure pointed to by a local
	protected void handleFlowsToDataStructure(Value base, Value source)
	{
		EquivalentValue sourcesOfBaseEqVal = new CachedEquivalentValue(new AbstractDataSource(base));
		EquivalentValue baseEqVal = new CachedEquivalentValue(base);

		EquivalentValue sourceEqVal;
		if(source instanceof InstanceFieldRef)
		{
			InstanceFieldRef ifr = (InstanceFieldRef) source;
			sourceEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, ifr.getField(), (Local) ifr.getBase()); // deals with inner fields
		}
		else
			sourceEqVal = new CachedEquivalentValue(source);
		
		if( source instanceof Ref && !infoFlowSummary.containsNode(sourceEqVal))
			infoFlowSummary.addNode(sourceEqVal);
		
		if(!abbreviatedInfoFlowGraph.containsNode(baseEqVal))
			abbreviatedInfoFlowGraph.addNode(baseEqVal);
		if(!abbreviatedInfoFlowGraph.containsNode(sourceEqVal))
			abbreviatedInfoFlowGraph.addNode(sourceEqVal);
		if(!abbreviatedInfoFlowGraph.containsNode(sourcesOfBaseEqVal))
			abbreviatedInfoFlowGraph.addNode(sourcesOfBaseEqVal);

		abbreviatedInfoFlowGraph.addEdge(sourceEqVal, sourcesOfBaseEqVal);
		abbreviatedInfoFlowGraph.addEdge(sourcesOfBaseEqVal, baseEqVal); // for convenience
	}
	
	// For inner fields... we have base flow to field as a service specifically
	// for the sake of LocalObjects... yes, this is a hack!
	protected void handleInnerField(Value innerFieldRef)
	{
/*
		InstanceFieldRef ifr = (InstanceFieldRef) innerFieldRef;
		
		EquivalentValue baseEqVal = new CachedEquivalentValue(ifr.getBase());
		EquivalentValue fieldRefEqVal = dfa.getEquivalentValueFieldRef(sm, ifr.getField()); // deals with inner fields
		
		if(!abbreviatedInfoFlowGraph.containsNode(baseEqVal))
			abbreviatedInfoFlowGraph.addNode(baseEqVal);
		if(!abbreviatedInfoFlowGraph.containsNode(fieldRefEqVal))
			abbreviatedInfoFlowGraph.addNode(fieldRefEqVal);
			
		abbreviatedInfoFlowGraph.addEdge(baseEqVal, fieldRefEqVal);
*/
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

	protected List handleInvokeExpr(InvokeExpr ie, Stmt is)
	{
		// get the data flow graph
		HashMutableDirectedGraph dataFlowSummary = dfa.getInvokeInfoFlowSummary(ie, is, sm); // must return a graph whose nodes are Refs!!!
		if(false) // DEBUG!!!
		{
			SootMethod method = ie.getMethodRef().resolve();
			if(method.getDeclaringClass().isApplicationClass())
			{
				G.v().out.println("Attempting to print graph (will succeed only if ./dfg/ is a valid path)");
				MutableDirectedGraph abbreviatedDataFlowGraph = dfa.getInvokeAbbreviatedInfoFlowGraph(ie, sm);
				InfoFlowAnalysis.printGraphToDotFile("dfg/" + method.getDeclaringClass().getShortName() + "_" + method.getName() + (refOnly ? "" : "_primitive"), 
					abbreviatedDataFlowGraph, method.getName() + (refOnly ? "" : "_primitive"), false);
			}
		}
//		if( ie.getMethodRef().resolve().getSubSignature().equals(new String("boolean remove(java.lang.Object)")) )
//		{
//			G.v().out.println("*!*!*!*!*!<boolean remove(java.lang.Object)> has FLOW SENSITIVE infoFlowSummary: ");
//			ClassInfoFlowAnalysis.printDataFlowGraph(infoFlowSummary);
//		}
		
		List returnValueSources = new ArrayList();
		
		Iterator<Object> nodeIt = dataFlowSummary.getNodes().iterator();
		while(nodeIt.hasNext())
		{
			EquivalentValue nodeEqVal = (EquivalentValue) nodeIt.next();
			
			if(!(nodeEqVal.getValue() instanceof Ref))
				throw new RuntimeException("Illegal node type in data flow summary:" + nodeEqVal.getValue() + " should be an object of type Ref.");
				
			Ref node = (Ref) nodeEqVal.getValue();
			
			List sources = new ArrayList();
//			Value source = null;
			
			if(node instanceof ParameterRef)
			{
				ParameterRef param = (ParameterRef) node;
				if(param.getIndex() == -1)
					continue;
				sources.add(ie.getArg(param.getIndex()));
//				source = ; // Immediate
			}
			else if(node instanceof StaticFieldRef)
			{
				sources.add(node);
//				source = node; // StaticFieldRef
			}
			else if(node instanceof InstanceFieldRef && ie instanceof InstanceInvokeExpr)
			{
				InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
				if(iie.getBase() == thisLocal)
				{
					sources.add(node);
//					source = node;
				}
				else if(includeInnerFields)
				{
					if( false ) // isNonRefType(node.getType()) ) // TODO: double check this policy
					{
						// primitives flow from the parent object
						InstanceFieldRef ifr = (InstanceFieldRef) node;
						if(ifr.getBase() instanceof FakeJimpleLocal)// && ((FakeJimpleLocal) ifr.getBase()).getRealLocal() != null)
							; // sources.add(((FakeJimpleLocal) ifr.getBase()).getRealLocal());
						else
							sources.add(ifr.getBase());
					}
					else
					{
						// objects flow from both
						InstanceFieldRef ifr = (InstanceFieldRef) node;
						if(ifr.getBase() instanceof FakeJimpleLocal)// && ((FakeJimpleLocal) ifr.getBase()).getRealLocal() != null)
							; // sources.add(((FakeJimpleLocal) ifr.getBase()).getRealLocal());
						else
							sources.add(ifr.getBase());
						sources.add(node);
					}
//					source = node;
//					handleInnerField(source);
				}
				else
				{
					sources.add(iie.getBase());
//					source = iie.getBase(); // Local
				}
			}
			else if(node instanceof InstanceFieldRef && includeInnerFields)
			{
				if( false ) // isNonRefType(node.getType()) ) // TODO: double check this policy
				{
					// primitives flow from the parent object
					InstanceFieldRef ifr = (InstanceFieldRef) node;
					if(ifr.getBase() instanceof FakeJimpleLocal)// && ((FakeJimpleLocal) ifr.getBase()).getRealLocal() != null)
						; // sources.add(((FakeJimpleLocal) ifr.getBase()).getRealLocal());
					else
						sources.add(ifr.getBase());
				}
				else
				{
					// objects flow from both
					InstanceFieldRef ifr = (InstanceFieldRef) node;
					if(ifr.getBase() instanceof FakeJimpleLocal)// && ((FakeJimpleLocal) ifr.getBase()).getRealLocal() != null)
						; // sources.add(((FakeJimpleLocal) ifr.getBase()).getRealLocal());
					else
						sources.add(ifr.getBase());
					sources.add(node);
				}
//				source = node;
//				handleInnerField(source);
			}
			else if(node instanceof ThisRef && ie instanceof InstanceInvokeExpr)
			{
				InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
				sources.add(iie.getBase());
//				source = iie.getBase(); // Local
			}
			else
			{
				throw new RuntimeException("Unknown Node Type in Data Flow Graph: node " + node + " in InvokeExpr " + ie);
			}
			
			Iterator sinksIt = dataFlowSummary.getSuccsOfAsSet(nodeEqVal).iterator();
			while(sinksIt.hasNext())
			{
				EquivalentValue sinkEqVal = (EquivalentValue) sinksIt.next();
				Ref sink = (Ref) sinkEqVal.getValue();
				if(sink instanceof ParameterRef)
				{
					ParameterRef param = (ParameterRef) sink;
					if(param.getIndex() == -1)
					{
						returnValueSources.addAll(sources);
					}
					else
					{
						for(Iterator sourcesIt = sources.iterator(); sourcesIt.hasNext(); )
						{
							Value source = (Value) sourcesIt.next();
							handleFlowsToDataStructure(ie.getArg(param.getIndex()), source);
						}
					}
				}
				else if(sink instanceof StaticFieldRef)
				{
					for(Iterator sourcesIt = sources.iterator(); sourcesIt.hasNext(); )
					{
						Value source = (Value) sourcesIt.next();
						handleFlowsToValue(sink, source);
					}
				}
				else if(sink instanceof InstanceFieldRef && ie instanceof InstanceInvokeExpr)
				{
					InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
					if(iie.getBase() == thisLocal)
					{
						for(Iterator sourcesIt = sources.iterator(); sourcesIt.hasNext(); )
						{
							Value source = (Value) sourcesIt.next();
							handleFlowsToValue(sink, source);
						}
					}
					else if(includeInnerFields)
					{
						for(Iterator sourcesIt = sources.iterator(); sourcesIt.hasNext(); )
						{
							Value source = (Value) sourcesIt.next();
							
							if( false ) // isNonRefType(sink.getType()) ) // TODO: double check this policy
							{
								// primitives flow to the parent object
								InstanceFieldRef ifr = (InstanceFieldRef) sink;
								if(ifr.getBase() instanceof FakeJimpleLocal)// && ((FakeJimpleLocal) ifr.getBase()).getRealLocal() != null)
									; // handleFlowsToDataStructure(((FakeJimpleLocal) ifr.getBase()).getRealLocal(), source);
								else
									handleFlowsToDataStructure(ifr.getBase(), source);
							}
							else
							{
								// objects flow to the field
								handleFlowsToValue(sink, source);
							}


							handleInnerField(sink);
						}
					}
					else
					{
						for(Iterator sourcesIt = sources.iterator(); sourcesIt.hasNext(); )
						{
							Value source = (Value) sourcesIt.next();
							handleFlowsToDataStructure(iie.getBase(), source);
						}
					}
				}
				else if(sink instanceof InstanceFieldRef && includeInnerFields)
				{
					for(Iterator sourcesIt = sources.iterator(); sourcesIt.hasNext(); )
					{
						Value source = (Value) sourcesIt.next();
						if( false ) // isNonRefType(sink.getType()) ) // TODO: double check this policy
						{
							// primitives flow to the parent object
							InstanceFieldRef ifr = (InstanceFieldRef) sink;
							if(ifr.getBase() instanceof FakeJimpleLocal)// && ((FakeJimpleLocal) ifr.getBase()).getRealLocal() != null)
								; // handleFlowsToDataStructure(((FakeJimpleLocal) ifr.getBase()).getRealLocal(), source);
							else
								handleFlowsToDataStructure(ifr.getBase(), source);
						}
						else
						{
							handleFlowsToValue(sink, source);
						}

						handleInnerField(sink);
					}
				}
			}
		}
				
		// return the list of return value sources
		return returnValueSources;
	}
	
	protected void addFlowToCdfg(Stmt stmt)
	{
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
					handleFlowsToValue(is.getLeftOp(), ir);
				}
			}
			else if(ir instanceof ThisRef)
			{
				if( !ignoreThisDataType(ir.getType()) )
				{
					// <Local, ThisRef and sources>
					handleFlowsToValue(is.getLeftOp(), ir);
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
					handleFlowsToValue(returnRef, rv);
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
				else if( includeInnerFields )
				{
					if( false ) //isNonRefType(lv.getType()) ) // TODO: double check this policy
					{
						// primitives flow to the parent object
						sink = ifr.getBase();
						flowsToDataStructure = true;
					}
					else
					{
						// objects flow to the field
						sink = lv;
						handleInnerField(sink);
					}
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
				else if( includeInnerFields )
				{
					if( false ) // isNonRefType(rv.getType()) ) // TODO: double check this policy
					{
						// primitives flow from the parent object
						sources.add(ifr.getBase());
					}
					else
					{
						// objects flow from both
						sources.add(ifr.getBase());
						sources.add(rv);
						handleInnerField(rv);
					}
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
			else if(rv instanceof BinopExpr) // does this include compares and others??? yes
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
				sources.addAll(handleInvokeExpr(ie, as));
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
						handleFlowsToDataStructure(sink, source);
					}
				}
				else
				{
					Iterator sourcesIt = sources.iterator();
					while(sourcesIt.hasNext())
					{
						Value source = (Value) sourcesIt.next();
//						if(flowsToBoth && sink instanceof InstanceFieldRef)
//							handleFlowsToDataStructure(((InstanceFieldRef)sink).getBase(), source);
						handleFlowsToValue(sink, source);
					}
				}
			}
		}
		else if(stmt.containsInvokeExpr()) // flows data between receiver object, parameters, globals, and return value
		{
			handleInvokeExpr(stmt.getInvokeExpr(), stmt);
		}
	}
	
	public Value getThisLocal()
	{
		return thisLocal;
	}
}

