package soot.jimple.toolkits.infoflow;

import soot.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.*;

// ClassLocalObjectsAnalysis written by Richard L. Halpert, 2007-02-23
// Finds objects that are local to the given scope.
// NOTE THAT THIS ANALYSIS'S RESULTS DO NOT APPLY TO SUBCLASSES OF THE GIVEN CLASS

public class ClassLocalObjectsAnalysis
{
	boolean printdfgs = false;

	LocalObjectsAnalysis loa;
	InfoFlowAnalysis dfa;
	InfoFlowAnalysis primitiveDfa;
	UseFinder uf;
	SootClass sootClass;
	
	Map methodToMethodLocalObjectsAnalysis;
	Map methodToContext;
	
	List allMethods;
	// methods that are called at least once from outside of this class (ie need to be public, protected, or package-private)
	List externalMethods;
	// methods that are only ever called by other methods in this class (ie could be marked private)
	List internalMethods;
	// methods that should be used as starting points when determining if a value in a method called from this class is local or shared
	// for thread-local objects, this would contain just the run method.  For structure-local, it should contain all external methods
	List entryMethods;
	
	List allFields;
	List externalFields;
	List internalFields;
	
	ArrayList localFields;
	ArrayList sharedFields;
	ArrayList localInnerFields;
	ArrayList sharedInnerFields;
	
	public ClassLocalObjectsAnalysis(LocalObjectsAnalysis loa, InfoFlowAnalysis dfa, UseFinder uf, SootClass sootClass)
	{
		this(loa, dfa, null, uf, sootClass, null);
	}
	
	public ClassLocalObjectsAnalysis(LocalObjectsAnalysis loa, InfoFlowAnalysis dfa, InfoFlowAnalysis primitiveDfa, UseFinder uf, SootClass sootClass, List entryMethods)
	{
		printdfgs = dfa.printDebug();
		this.loa = loa;
		this.dfa = dfa;
		this.primitiveDfa = primitiveDfa;
		this.uf = uf;
		this.sootClass = sootClass;
		
		this.methodToMethodLocalObjectsAnalysis = new HashMap();
		this.methodToContext = null;
		 
		this.allMethods = null;
		this.externalMethods = null;
		this.internalMethods = null;
	 	this.entryMethods = entryMethods;
		 
		this.allFields = null;
		this.externalFields = null;
		this.internalFields = null;
		
		this.localFields = null;
		this.sharedFields = null;
		this.localInnerFields = null;
		this.sharedInnerFields = null;
		
		prepare();
		doAnalysis();
	}
	
	private void prepare()
	{
		// Get list of all methods
		allMethods = getAllReachableMethods(sootClass);
		
		// Get list of external methods
		externalMethods = uf.getExtMethods(sootClass);
		SootClass superclass = sootClass;
		if(superclass.hasSuperclass())
			superclass = superclass.getSuperclass();
		while(superclass.hasSuperclass())
		{
			if(superclass.isApplicationClass())
		        externalMethods.addAll(uf.getExtMethods(superclass));
			superclass = superclass.getSuperclass();
		}

		// Get list of internal methods
		internalMethods = new ArrayList();
		for(Iterator methodsIt = allMethods.iterator(); methodsIt.hasNext(); )
		{
			SootMethod method = (SootMethod) methodsIt.next();
			if(!externalMethods.contains(method))
				internalMethods.add(method);
		}
		
		// Get list of all fields
		allFields = getAllFields(sootClass);
		
		// Get list of external fields
		externalFields = uf.getExtFields(sootClass);
		superclass = sootClass;
		if(superclass.hasSuperclass())
			superclass = superclass.getSuperclass();
		while(superclass.hasSuperclass())
		{
			if(superclass.isApplicationClass())
		        externalFields.addAll(uf.getExtFields(superclass));
			superclass = superclass.getSuperclass();
		}

		// Get list of internal fields
		internalFields = new ArrayList();
		for(Iterator fieldsIt = allFields.iterator(); fieldsIt.hasNext(); )
		{
			SootField field = (SootField) fieldsIt.next();
			if(!externalFields.contains(field))
				internalFields.add(field);
		}
		
	}

	// Returns a list of reachable methods in class sc and its superclasses
	public static List getAllReachableMethods(SootClass sc)
	{
		ReachableMethods rm = Scene.v().getReachableMethods();
		
		// Get list of reachable methods declared in this class
		List allMethods = new ArrayList();
		Iterator methodsIt = sc.methodIterator();
		while(methodsIt.hasNext())
		{
			SootMethod method = (SootMethod) methodsIt.next();
			if(rm.contains(method))
				allMethods.add(method);
		}
		
		// Add reachable methods declared in superclasses
		SootClass superclass = sc;
		if(superclass.hasSuperclass())
			superclass = superclass.getSuperclass();
		while(superclass.hasSuperclass()) // we don't want to process Object
		{
	        Iterator scMethodsIt = superclass.methodIterator();
	        while(scMethodsIt.hasNext())
	        {
				SootMethod scMethod = (SootMethod) scMethodsIt.next();
				if(rm.contains(scMethod))
					allMethods.add(scMethod);
	        }
			superclass = superclass.getSuperclass();
		}
		return allMethods;
	}
	
	// Returns a list of fields in class sc and its superclasses
	public static List getAllFields(SootClass sc)
	{
		// Get list of reachable methods declared in this class
		// Also get list of fields declared in this class
		List allFields = new ArrayList();
		Iterator fieldsIt = sc.getFields().iterator();
		while(fieldsIt.hasNext())
		{
			SootField field = (SootField) fieldsIt.next();
			allFields.add(field);
		}
		
		// Add reachable methods and fields declared in superclasses
		SootClass superclass = sc;
		if(superclass.hasSuperclass())
			superclass = superclass.getSuperclass();
		while(superclass.hasSuperclass()) // we don't want to process Object
		{
	        Iterator scFieldsIt = superclass.getFields().iterator();
	        while(scFieldsIt.hasNext())
	        {
				SootField scField = (SootField) scFieldsIt.next();
				allFields.add(scField);
	        }
			superclass = superclass.getSuperclass();
		}
		return allFields;
	}
	
	private void doAnalysis()
	{
		// Combine the DFA results for each of this class's methods, using safe
		// approximations for which parameters, fields, and globals are shared
		// or local.
		
		// Separate fields into shared and local.  Initially fields are known to be
		// shared if they have any external accesses, or if they're static.
		// Methods are iterated over, moving fields to shared if shared data flows to them.
		// This is repeated until no fields move for a complete iteration.
		
		// Populate localFields and sharedFields with fields of this class
		localFields = new ArrayList();
		sharedFields = new ArrayList();
		Iterator fieldsIt = allFields.iterator();
		while(fieldsIt.hasNext())
		{
			SootField field = (SootField) fieldsIt.next();
			if( fieldIsInitiallyLocal(field) )
				localFields.add(field);
			else
				sharedFields.add(field);
		}
		
		// Add inner fields to localFields and sharedFields, if present
		localInnerFields = new ArrayList();
		sharedInnerFields = new ArrayList();
		Iterator methodsIt = allMethods.iterator();
		while(methodsIt.hasNext())
		{
			SootMethod method = (SootMethod) methodsIt.next();
			
			// Get data flow summary
			MutableDirectedGraph dataFlowSummary;
			if(primitiveDfa != null)
			{
				dataFlowSummary = primitiveDfa.getMethodInfoFlowSummary(method);
				
				if(printdfgs && method.getDeclaringClass().isApplicationClass())
				{
					DirectedGraph primitiveGraph = primitiveDfa.getMethodInfoFlowAnalysis(method).getMethodAbbreviatedInfoFlowGraph();
					InfoFlowAnalysis.printGraphToDotFile("dfg/" + method.getDeclaringClass().getShortName() + "_" + method.getName() + "_primitive", 
						primitiveGraph, method.getName() + "_primitive", false);

					DirectedGraph nonPrimitiveGraph = dfa.getMethodInfoFlowAnalysis(method).getMethodAbbreviatedInfoFlowGraph();
					InfoFlowAnalysis.printGraphToDotFile("dfg/" + method.getDeclaringClass().getShortName() + "_" + method.getName(),
						nonPrimitiveGraph, method.getName(), false);
				}
			}
			else
			{
				dataFlowSummary = dfa.getMethodInfoFlowSummary(method);
				
				if(printdfgs && method.getDeclaringClass().isApplicationClass())
				{
					DirectedGraph nonPrimitiveGraph = dfa.getMethodInfoFlowAnalysis(method).getMethodAbbreviatedInfoFlowGraph();
					InfoFlowAnalysis.printGraphToDotFile("dfg/" + method.getDeclaringClass().getShortName() + "_" + method.getName(),
						nonPrimitiveGraph, method.getName(), false);
				}
			}
				
			// Iterate through nodes
			Iterator nodesIt = dataFlowSummary.getNodes().iterator();
			while(nodesIt.hasNext())
			{
				EquivalentValue node = (EquivalentValue) nodesIt.next();
				if(node.getValue() instanceof InstanceFieldRef)
				{
					InstanceFieldRef ifr = (InstanceFieldRef) node.getValue();
					if( !localFields.contains(ifr.getField()) && !sharedFields.contains(ifr.getField()) &&
						!localInnerFields.contains(ifr.getField()) ) // && !sharedInnerFields.contains(ifr.getField()))
					{
						// this field is read or written, but is not in the lists of fields!
						localInnerFields.add(ifr.getField());
					}
				}
			}
		}
		
		// Propagate (aka iterate iterate iterate iterate! hope it's not too slow)
		boolean changed = true;
		while(changed)
		{
			changed = false;
//			G.v().out.println("Starting iteration:");
			methodsIt = allMethods.iterator();
			while(methodsIt.hasNext())
			{
				SootMethod method = (SootMethod) methodsIt.next();
				// we can't learn anything from non-concrete methods, and statics can't write non-static fields
				if(method.isStatic() || !method.isConcrete())
					continue;
				
				ListIterator localFieldsIt = ((List) localFields).listIterator();
				boolean printedMethodHeading = false;
				while(localFieldsIt.hasNext())
				{
					SootField localField = (SootField) localFieldsIt.next();
					List sourcesAndSinks = new ArrayList();

					MutableDirectedGraph dataFlowSummary;
					if(primitiveDfa != null)
						dataFlowSummary = primitiveDfa.getMethodInfoFlowSummary(method);
					else
						dataFlowSummary = dfa.getMethodInfoFlowSummary(method);
					
					EquivalentValue node = dfa.getNodeForFieldRef(method, localField);
					if(dataFlowSummary.containsNode(node))
					{
						sourcesAndSinks.addAll(dataFlowSummary.getSuccsOf(node));
						sourcesAndSinks.addAll(dataFlowSummary.getPredsOf(node));
					}

					Iterator sourcesAndSinksIt = sourcesAndSinks.iterator();
					if(localField.getDeclaringClass().isApplicationClass() &&
					   sourcesAndSinksIt.hasNext())
					{
//						if(!printedMethodHeading)
//						{
//							G.v().out.println("    Method: " + method.toString());
//							printedMethodHeading = true;
//						}
//						G.v().out.println("        Field: " + localField.toString());
					}
					while(sourcesAndSinksIt.hasNext())
					{
						EquivalentValue sourceOrSink = (EquivalentValue) sourcesAndSinksIt.next();
						Ref sourceOrSinkRef = (Ref) sourceOrSink.getValue();
						boolean fieldBecomesShared = false;
						if(sourceOrSinkRef instanceof ParameterRef) // or return ref
						{
							fieldBecomesShared = !parameterIsLocal(method, sourceOrSink, true);
						}
						else if(sourceOrSinkRef instanceof ThisRef) // or return ref
						{
							fieldBecomesShared = !thisIsLocal(method, sourceOrSink);
						}
						else if(sourceOrSinkRef instanceof InstanceFieldRef)
						{
							fieldBecomesShared = sharedFields.contains( ((FieldRef)sourceOrSinkRef).getField() ) || sharedInnerFields.contains( ((FieldRef)sourceOrSinkRef).getField() );
						}
						else if(sourceOrSinkRef instanceof StaticFieldRef)
						{
							fieldBecomesShared = true;
						}
						else
						{
							throw new RuntimeException("Unknown type of Ref in Data Flow Graph:");
						}
						
						if(fieldBecomesShared)
						{
//							if(localField.getDeclaringClass().isApplicationClass())
//								G.v().out.println("            Source/Sink: " + sourceOrSinkRef.toString() + " is SHARED");
							localFieldsIt.remove();
							sharedFields.add(localField);
							changed = true;
							break; // other sources don't matter now... it only takes one to taint the field
						}
						else
						{
//							if(localField.getDeclaringClass().isApplicationClass())
//								G.v().out.println("            Source: " + sourceRef.toString() + " is local");
						}
					}
				}


				ListIterator localInnerFieldsIt = ((List) localInnerFields).listIterator();
//				boolean printedMethodHeading = false;
				while(!changed && localInnerFieldsIt.hasNext())
				{
					SootField localInnerField = (SootField) localInnerFieldsIt.next();
					List sourcesAndSinks = new ArrayList();

					MutableDirectedGraph dataFlowSummary;
					if(primitiveDfa != null)
						dataFlowSummary = primitiveDfa.getMethodInfoFlowSummary(method);
					else
						dataFlowSummary = dfa.getMethodInfoFlowSummary(method);
					
					EquivalentValue node = dfa.getNodeForFieldRef(method, localInnerField);
					if(dataFlowSummary.containsNode(node))
					{
						sourcesAndSinks.addAll(dataFlowSummary.getSuccsOf(node));
						sourcesAndSinks.addAll(dataFlowSummary.getPredsOf(node));
					}

					Iterator sourcesAndSinksIt = sourcesAndSinks.iterator();
					if(localInnerField.getDeclaringClass().isApplicationClass() &&
					   sourcesAndSinksIt.hasNext())
					{
//						if(!printedMethodHeading)
//						{
//							G.v().out.println("    Method: " + method.toString());
//							printedMethodHeading = true;
//						}
//						G.v().out.println("        Field: " + localField.toString());
					}
					while(sourcesAndSinksIt.hasNext())
					{
						EquivalentValue sourceOrSink = (EquivalentValue) sourcesAndSinksIt.next();
						Ref sourceOrSinkRef = (Ref) sourceOrSink.getValue();
						boolean fieldBecomesShared = false;
						if(sourceOrSinkRef instanceof ParameterRef) // or return ref
						{
							fieldBecomesShared = !parameterIsLocal(method, sourceOrSink, true);
						}
						else if(sourceOrSinkRef instanceof ThisRef) // or return ref
						{
							fieldBecomesShared = !thisIsLocal(method, sourceOrSink);
						}
						else if(sourceOrSinkRef instanceof InstanceFieldRef)
						{
							fieldBecomesShared = sharedFields.contains( ((FieldRef)sourceOrSinkRef).getField() ) || sharedInnerFields.contains( ((FieldRef)sourceOrSinkRef).getField() );
						}
						else if(sourceOrSinkRef instanceof StaticFieldRef)
						{
							fieldBecomesShared = true;
						}
						else
						{
							throw new RuntimeException("Unknown type of Ref in Data Flow Graph:");
						}
						
						if(fieldBecomesShared)
						{
//							if(localField.getDeclaringClass().isApplicationClass())
//								G.v().out.println("            Source/Sink: " + sourceOrSinkRef.toString() + " is SHARED");
							localInnerFieldsIt.remove();
							sharedInnerFields.add(localInnerField);
							changed = true;
							break; // other sources don't matter now... it only takes one to taint the field
						}
						else
						{
//							if(localField.getDeclaringClass().isApplicationClass())
//								G.v().out.println("            Source: " + sourceRef.toString() + " is local");
						}
					}
				}
			}
		}
		
		// Print debug output
		if(dfa.printDebug())
		{
			G.v().out.println("        Found local/shared fields for " + sootClass.toString());
			G.v().out.println("          Local fields: ");
			Iterator localsToPrintIt = localFields.iterator();
			while(localsToPrintIt.hasNext())
			{
				SootField localToPrint = (SootField) localsToPrintIt.next();
				if(localToPrint.getDeclaringClass().isApplicationClass())
					G.v().out.println("                  " + localToPrint);
			}
			G.v().out.println("          Shared fields: ");
			Iterator sharedsToPrintIt = sharedFields.iterator();
			while(sharedsToPrintIt.hasNext())
			{
				SootField sharedToPrint = (SootField) sharedsToPrintIt.next();
				if(sharedToPrint.getDeclaringClass().isApplicationClass())
					G.v().out.println("                  " + sharedToPrint);
			}
			G.v().out.println("          Local inner fields: ");
			localsToPrintIt = localInnerFields.iterator();
			while(localsToPrintIt.hasNext())
			{
				SootField localToPrint = (SootField) localsToPrintIt.next();
				if(localToPrint.getDeclaringClass().isApplicationClass())
					G.v().out.println("                  " + localToPrint);
			}
			G.v().out.println("          Shared inner fields: ");
			sharedsToPrintIt = sharedInnerFields.iterator();
			while(sharedsToPrintIt.hasNext())
			{
				SootField sharedToPrint = (SootField) sharedsToPrintIt.next();
				if(sharedToPrint.getDeclaringClass().isApplicationClass())
					G.v().out.println("                  " + sharedToPrint);
			}
		}

		precomputeLocalityContexts();
	}
	
	private void precomputeLocalityContexts()
	{
		// Initialize worklist
		ArrayList worklist = new ArrayList();
		worklist.addAll(entryMethods);
		
		// Initialize set of contexts
		methodToContext = new HashMap(); // TODO: add the ability to share a map with another CLOA to save memory (be careful of context-sensitive call graph)
		for(Iterator worklistIt = worklist.iterator(); worklistIt.hasNext(); )
		{
			SootMethod method = (SootMethod) worklistIt.next();
			methodToContext.put(method, getContextFor(method));
		}
		
		// Propagate
		Date start = new Date();
		if(dfa.printDebug())
			G.v().out.println("CLOA: Starting Propagation at " + start);
		while(worklist.size() > 0)
		{
			ArrayList newWorklist = new ArrayList();
			for(Iterator worklistIt = worklist.iterator(); worklistIt.hasNext(); )
			{
				SootMethod containingMethod = (SootMethod) worklistIt.next();
				CallLocalityContext containingContext = (CallLocalityContext) methodToContext.get(containingMethod);

				if(dfa.printDebug())
					G.v().out.println("      " + containingMethod.getName() + " " + containingContext.toShortString());
				
				// Calculate the context for each invoke stmt in the containingMethod
				Map invokeToContext = new HashMap();
				for(Iterator edgesIt = Scene.v().getCallGraph().edgesOutOf(containingMethod); edgesIt.hasNext(); )
				{
					Edge e = (Edge) edgesIt.next();
					if( !e.src().getDeclaringClass().isApplicationClass() || e.srcStmt() == null )
						continue;
					CallLocalityContext invokeContext;
					if( !invokeToContext.containsKey(e.srcStmt()) )
					{
						invokeContext = getContextFor(e, containingMethod, containingContext);
						invokeToContext.put(e.srcStmt(), invokeContext);
					}
					else
					{
						invokeContext = (CallLocalityContext) invokeToContext.get(e.srcStmt());
					}
					
					if( !methodToContext.containsKey(e.tgt()) )
					{
						methodToContext.put(e.tgt(), invokeContext);
						newWorklist.add(e.tgt());
					}
					else
					{
//						G.v().out.println("        Merging Contexts for " + e.tgt());
						boolean causedChange = ((CallLocalityContext) methodToContext.get(e.tgt())).merge(invokeContext); // The contexts being merged could be from different DFAs.  If so, primitive version might be bigger.
						if( causedChange )
							newWorklist.add(e.tgt());
					}
				}
			}
			worklist = newWorklist;
		}
    	long longTime = ((new Date()).getTime() - start.getTime()) / 100;
    	float time = ((float) longTime) / 10.0f;
		if(dfa.printDebug())
			G.v().out.println("CLOA: Ending Propagation after " + time + "s");
	}
	
	public CallLocalityContext getMergedContext(SootMethod method)
	{
		if(methodToContext.containsKey(method))
			return (CallLocalityContext) methodToContext.get(method);
		
		return null;
	}

	private CallLocalityContext getContextFor(Edge e, SootMethod containingMethod, CallLocalityContext containingContext)
	{
		// get new called method and calling context
		InvokeExpr ie;
		if(e.srcStmt().containsInvokeExpr())
			ie = e.srcStmt().getInvokeExpr();
		else
			ie = null;
			
		SootMethod callingMethod = e.tgt();
		CallLocalityContext callingContext = new CallLocalityContext(dfa.getMethodInfoFlowSummary(callingMethod).getNodes()); // just keeps a map from NODE to SHARED/LOCAL
		
		// We will use the containing context that we have to determine if base/args are local
		if(callingMethod.isConcrete())
		{
			Body b = containingMethod.retrieveActiveBody();
		
			// check base
			if(ie != null && ie instanceof InstanceInvokeExpr)
			{
				InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
				if( !containingMethod.isStatic() && iie.getBase().equivTo(b.getThisLocal()) )
				{
					// calling another method on same object... basically copy the previous context
					Iterator localRefsIt = containingContext.getLocalRefs().iterator();
					while(localRefsIt.hasNext())
					{
						EquivalentValue rEqVal = (EquivalentValue) localRefsIt.next();
						Ref r = (Ref) rEqVal.getValue();
						if(r instanceof InstanceFieldRef)
						{
							EquivalentValue newRefEqVal = dfa.getNodeForFieldRef(callingMethod, ((FieldRef) r).getFieldRef().resolve());
							if(callingContext.containsField(newRefEqVal)) // if not, then we're probably calling a parent class's method, so some fields are missing
								callingContext.setFieldLocal(newRefEqVal); // must make a new eqval for the method getting called
						}
						else if(r instanceof ThisRef)
							callingContext.setThisLocal();
					}
				}
				else if( SmartMethodLocalObjectsAnalysis.isObjectLocal(dfa, containingMethod, containingContext, iie.getBase()) )
				{
					// calling a method on a local object
					callingContext.setAllFieldsLocal();
					callingContext.setThisLocal();
				}
				else
				{
					// calling a method on a shared object
					callingContext.setAllFieldsShared();
					callingContext.setThisShared();
				}
			}
			else
			{
				callingContext.setAllFieldsShared();
				callingContext.setThisShared();
			}
			
			// check args
			if(ie == null)
				callingContext.setAllParamsShared();
			else
			{
				for(int param = 0; param < ie.getArgCount(); param++)
				{
					if( SmartMethodLocalObjectsAnalysis.isObjectLocal(dfa, containingMethod, containingContext, ie.getArg(param)) )
						callingContext.setParamLocal(param);
					else
						callingContext.setParamShared(param);
				}
			}
		}
		else
		{
			// The only conservative solution for a bodyless method is to assume everything is shared
			callingContext.setAllFieldsShared();
			callingContext.setThisShared();				
			callingContext.setAllParamsShared();
		}
		return callingContext;
	}

	public CallLocalityContext getContextFor(SootMethod sm) { return getContextFor(sm, false); }
	private CallLocalityContext getContextFor(SootMethod sm, boolean includePrimitiveDataFlowIfAvailable)
	{
		CallLocalityContext context;
		if(includePrimitiveDataFlowIfAvailable)
			context = new CallLocalityContext(primitiveDfa.getMethodInfoFlowSummary(sm).getNodes());
		else
			context = new CallLocalityContext(dfa.getMethodInfoFlowSummary(sm).getNodes());
		
		// Set context for every parameter that is shared
		for(int i = 0; i < sm.getParameterCount(); i++) // no need to worry about return value... 
		{
			EquivalentValue paramEqVal = dfa.getNodeForParameterRef(sm, i);
			if(parameterIsLocal(sm, paramEqVal, includePrimitiveDataFlowIfAvailable))
			{
				context.setParamLocal(i);
			}
			else
			{
				context.setParamShared(i);
			}
		}
		
		// Set context for every field that is local
		for(Iterator it = getLocalFields().iterator(); it.hasNext();)
		{
			SootField sf = (SootField) it.next();
			EquivalentValue fieldRefEqVal = dfa.getNodeForFieldRef(sm, sf);
			context.setFieldLocal(fieldRefEqVal);
		}
		
		// Set context for every field that is shared
		for(Iterator it = getSharedFields().iterator(); it.hasNext();)
		{
			SootField sf = (SootField) it.next();
			EquivalentValue fieldRefEqVal = dfa.getNodeForFieldRef(sm, sf);
			context.setFieldShared(fieldRefEqVal);
		}
		return context;
	}
	
	public boolean isObjectLocal(Value localOrRef, SootMethod sm) { return isObjectLocal(localOrRef, sm, false); }	
	private boolean isObjectLocal(Value localOrRef, SootMethod sm, boolean includePrimitiveDataFlowIfAvailable)
	{
		if(localOrRef instanceof StaticFieldRef)
		{
			return false;
		}
		
		if(dfa.printDebug())
			G.v().out.println("      CLOA testing if " + localOrRef + " is local in " + sm);

		SmartMethodLocalObjectsAnalysis smloa = getMethodLocalObjectsAnalysis(sm, includePrimitiveDataFlowIfAvailable);
		if(localOrRef instanceof InstanceFieldRef)
		{
			InstanceFieldRef ifr = (InstanceFieldRef) localOrRef;
			if( ifr.getBase().equivTo(smloa.getThisLocal()) )
				return isFieldLocal(ifr.getFieldRef().resolve());
			else
			{
				// if referred object is local, then find out if field is local in that object
				if(isObjectLocal(ifr.getBase(), sm, includePrimitiveDataFlowIfAvailable))
				{
					boolean retval = loa.isFieldLocalToParent(ifr.getFieldRef().resolve());
					if(dfa.printDebug())
						G.v().out.println("      " + (retval ? "local" : "shared"));
					return retval;
				}
				else
				{
					if(dfa.printDebug())
						G.v().out.println("      shared");
					return false;
				}
			}
		}
		// TODO Prepare a CallLocalityContext!
		CallLocalityContext context = getContextFor(sm);
		
		boolean retval = smloa.isObjectLocal(localOrRef, context);
		if(dfa.printDebug())
			G.v().out.println("      " + (retval ? "local" : "shared"));
		return retval;
	}
	
	public SmartMethodLocalObjectsAnalysis getMethodLocalObjectsAnalysis(SootMethod sm) { return getMethodLocalObjectsAnalysis(sm, false); }
	private SmartMethodLocalObjectsAnalysis getMethodLocalObjectsAnalysis(SootMethod sm, boolean includePrimitiveDataFlowIfAvailable)
	{
		if(includePrimitiveDataFlowIfAvailable && primitiveDfa != null)
		{
			Body b = sm.retrieveActiveBody();
			UnitGraph g = new ExceptionalUnitGraph(b);
			return new SmartMethodLocalObjectsAnalysis(g, primitiveDfa);
		}
		else if(!methodToMethodLocalObjectsAnalysis.containsKey(sm))
		{
			// Analyze this method
			Body b = sm.retrieveActiveBody();
			UnitGraph g = new ExceptionalUnitGraph(b);
			SmartMethodLocalObjectsAnalysis smloa = new SmartMethodLocalObjectsAnalysis(g, dfa);
			methodToMethodLocalObjectsAnalysis.put(sm, smloa);
		}
		return (SmartMethodLocalObjectsAnalysis) methodToMethodLocalObjectsAnalysis.get(sm);
	}
	
	// Should check field access rights, and possibly perform an analysis
	// to determine if a field that is accessible is ever directly accessed
	private boolean fieldIsInitiallyLocal(SootFieldRef fieldRef)
	{
		return fieldIsInitiallyLocal(fieldRef.resolve());
	}
	
	private boolean fieldIsInitiallyLocal(SootField field)
	{
		if(field.isStatic())
		{
			// Static fields are always shared
			return false;
		}
		else if(field.isPrivate())
		{
			// Private fields may be local
			return true;
		}
		else
		{
			return !externalFields.contains(field);
		}
	}
	
	protected List getSharedFields()
	{
		return (List) sharedFields.clone();
	}
	
	protected List getLocalFields()
	{
		return (List) localFields.clone();
	}
	
	public List getInnerSharedFields()
	{
		return sharedInnerFields;
	}
	
	protected boolean isFieldLocal(SootField field)
	{
		return localFields.contains(field);
	}
	
	protected boolean isFieldLocal(EquivalentValue fieldRef)
	{
		return localFields.contains( ((SootFieldRef) fieldRef.getValue()).resolve() );
	}	
	
	public boolean parameterIsLocal(SootMethod method, EquivalentValue parameterRef) { return parameterIsLocal(method, parameterRef, false); }
	protected boolean parameterIsLocal(SootMethod method, EquivalentValue parameterRef, boolean includePrimitiveDataFlowIfAvailable)
	{
		if(dfa.printDebug() && method.getDeclaringClass().isApplicationClass())
			G.v().out.println("        Checking PARAM " + parameterRef + " for " + method);
			
		// Check if param is primitive or ref type
		ParameterRef param = (ParameterRef) parameterRef.getValue();
		if( !(param.getType() instanceof RefLikeType) && (!dfa.includesPrimitiveInfoFlow() || method.getName().equals("<init>")) ) // TODO fix
		{
			if(dfa.printDebug() && method.getDeclaringClass().isApplicationClass())
				G.v().out.println("          PARAM is local (primitive)");
			return true; // primitive params are always considered local
		}
		
		// Check if method is externally called
		List extClassCalls = uf.getExtCalls(sootClass);
		Iterator extClassCallsIt = extClassCalls.iterator();
		while(extClassCallsIt.hasNext())
		{
			Pair extCall = (Pair) extClassCallsIt.next();
			Stmt s = (Stmt) extCall.getO2();
			if(s.getInvokeExpr().getMethodRef().resolve() == method)
			{
				if(dfa.printDebug() && method.getDeclaringClass().isApplicationClass())
					G.v().out.println("          PARAM is shared (external access)");
				return false; // If so, assume it's params are shared
			}
		}
		
		// For each internal call, check if arg is local or shared
		List intClassCalls = uf.getIntCalls(sootClass);
		Iterator intClassCallsIt = intClassCalls.iterator(); // returns all internal accesses
		while(intClassCallsIt.hasNext())
		{
			Pair intCall = (Pair) intClassCallsIt.next();
			SootMethod containingMethod = (SootMethod) intCall.getO1();
			Stmt s = (Stmt) intCall.getO2();
			InvokeExpr ie = s.getInvokeExpr();
			if(ie.getMethodRef().resolve() == method)
			{
				if(((ParameterRef) parameterRef.getValue()).getIndex() >= 0)
				{
					if(!isObjectLocal( ie.getArg( ((ParameterRef) parameterRef.getValue()).getIndex() ), containingMethod, includePrimitiveDataFlowIfAvailable)) // WORST CASE SCENARIO HERE IS INFINITE RECURSION!
					{
						if(dfa.printDebug() && method.getDeclaringClass().isApplicationClass())
							G.v().out.println("          PARAM is shared (internal propagation)");
						return false; // if arg is shared for any internal call, then param is shared
					}
				}
				else
				{
					if(s instanceof DefinitionStmt)
					{
						Value obj = ((DefinitionStmt) s).getLeftOp();
						if(!isObjectLocal( obj, containingMethod, includePrimitiveDataFlowIfAvailable)) // WORST CASE SCENARIO HERE IS INFINITE RECURSION!
						{
							if(dfa.printDebug() && method.getDeclaringClass().isApplicationClass())
								G.v().out.println("          PARAM is shared (internal propagation)");
							return false; // if arg is shared for any internal call, then param is shared
						}
					}
				}
			}
		}
		if(dfa.printDebug() && method.getDeclaringClass().isApplicationClass())
			G.v().out.println("          PARAM is local SO FAR (internal propagation)");
		return true; // if argument is always local, then parameter is local
	}
	
	// TODO: SOUND/UNSOUND???
	protected boolean thisIsLocal(SootMethod method, EquivalentValue thisRef)
	{
		return true;
	}
}

