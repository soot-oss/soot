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
// NOTE THAT THIS ANALYSIS'S RESULTS DO NOT APPLY TO SUBCLASSES OF THE SCOPE CLASS

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
		
		// Determine which methods are reachable in this program
		ReachableMethods rm = Scene.v().getReachableMethods();

		// Get list of reachable methods declared in this class
		// Also get list of fields declared in this class
		List scopeMethods = new ArrayList();
		List scopeFields = new ArrayList();
		Iterator scopeMethodsIt = scopeClass.methodIterator();
		while(scopeMethodsIt.hasNext())
		{
			SootMethod scopeMethod = (SootMethod) scopeMethodsIt.next();
			if(rm.contains(scopeMethod))
				scopeMethods.add(scopeMethod);
		}
		Iterator scopeFieldsIt = scopeClass.getFields().iterator();
		while(scopeFieldsIt.hasNext())
		{
			SootField scopeField = (SootField) scopeFieldsIt.next();
			scopeFields.add(scopeField);
		}
		
		// Add reachable methods and fields declared in superclasses
		SootClass superclass = scopeClass;
		if(superclass.hasSuperclass())
			superclass = scopeClass.getSuperclass();
		while(superclass.hasSuperclass()) // we don't want to process Object
		{
	        Iterator scMethodsIt = superclass.methodIterator();
	        while(scMethodsIt.hasNext())
	        {
				SootMethod scMethod = (SootMethod) scMethodsIt.next();
				if(rm.contains(scMethod))
					scopeMethods.add(scMethod);
	        }
	        Iterator scFieldsIt = superclass.getFields().iterator();
	        while(scFieldsIt.hasNext())
	        {
				SootField scField = (SootField) scFieldsIt.next();
				
				scopeFields.add(scField);
	        }
			superclass = superclass.getSuperclass();
		}
		
		// Separate fields into shared and local.  Initially, all fields are local.
		// Methods are iterated over, moving fields to shared if shared data flows to them.
		// This is repeated until no fields move for a complete iteration.
		
		// Populate localFields and sharedFields with SootFields
		ArrayList localFields = new ArrayList();
		List sharedFields = new ArrayList();
		Iterator fieldsIt = scopeFields.iterator();
		while(fieldsIt.hasNext())
		{
			SootField field = (SootField) fieldsIt.next();
			if( fieldIsInitiallyLocal(field.makeRef()) )
				localFields.add(field);
			else
				sharedFields.add(field);
		}
		
		// Propagate (aka iterate iterate iterate iterate! hope it's not too slow)
		boolean changed = true;
		while(changed)
		{
			changed = false;
			G.v().out.println("Starting iteration:");
			Iterator methodsIt = scopeMethods.iterator();
			while(methodsIt.hasNext())
			{
				SootMethod method = (SootMethod) methodsIt.next();
				// we can't learn anything from non-concrete methods, and statics can't write non-static fields
				if(method.isStatic() || !method.isConcrete())
					continue;
				Iterator localFieldsIt = ((List) localFields.clone()).iterator(); // unbacked iterator so we can remove from the original
				boolean printedMethodHeading = false;
//				if(method.getDeclaringClass().isApplicationClass() && localFieldsIt.hasNext())
//				{
//					G.v().out.println("    Method: " + method.toString());
//					printedMethodHeading = true;
//				}
				while(localFieldsIt.hasNext())
				{
					SootField localField = (SootField) localFieldsIt.next();
					List sources = dfa.getSourcesOf(method, dfa.getEquivalentValueFieldRef(method, localField));
					Iterator sourcesIt = sources.iterator();
					if(localField.getDeclaringClass().isApplicationClass() &&
					   sourcesIt.hasNext())
					{
						if(!printedMethodHeading)
						{
							G.v().out.println("    Method: " + method.toString());
							printedMethodHeading = true;
						}
						G.v().out.println("        Field: " + localField.toString());
					}
					while(sourcesIt.hasNext())
					{
						EquivalentValue source = (EquivalentValue) sourcesIt.next();
						Ref sourceRef = (Ref) source.getValue();
						boolean fieldBecomesShared = false;
						if(sourceRef instanceof ParameterRef)
						{
							fieldBecomesShared = !parameterIsLocal(method, source);
						}
						else if(sourceRef instanceof InstanceFieldRef)
						{
							fieldBecomesShared = sharedFields.contains(source);
						}
						else if(sourceRef instanceof StaticFieldRef)
						{
							fieldBecomesShared = true;
						}
						else
						{
							throw new RuntimeException("Unknown type of Ref in Data Flow Graph:");
						}
						
						if(fieldBecomesShared)
						{
							if(localField.getDeclaringClass().isApplicationClass())
								G.v().out.println("            Source: " + sourceRef.toString() + " is SHARED");
							localFields.remove(localField);
							sharedFields.add(localField);
							changed = true;
//							break; // other sources don't matter now... it only takes one to taint the field
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
		
		// DEBUG: If we survived that craziness, then print out the resulting list!
		G.v().out.println("Found local/shared fields for " + scopeClass.toString());
		G.v().out.println("    Local fields: ");
		Iterator localsToPrintIt = localFields.iterator();
		while(localsToPrintIt.hasNext())
		{
			SootField localToPrint = (SootField) localsToPrintIt.next();
			if(localToPrint.getDeclaringClass().isApplicationClass())
				G.v().out.println("                  " + localToPrint);
		}
		G.v().out.println("    Shared fields: ");
		Iterator sharedsToPrintIt = sharedFields.iterator();
		while(sharedsToPrintIt.hasNext())
		{
			SootField sharedToPrint = (SootField) sharedsToPrintIt.next();
			if(sharedToPrint.getDeclaringClass().isApplicationClass())
				G.v().out.println("                  " + sharedToPrint);
		}
				
		// Analyze each method: determine which Locals are local and which are shared
		Iterator it = scopeClass.getMethods().iterator();
		while(it.hasNext())
		{
			SootMethod method = (SootMethod) it.next();
			
			// For each method, analyze the body
			
		}
	}
	
	// Should check field access rights, and possibly perform an analysis
	// to determine if a field that is accessible is ever directly accessed
	private boolean fieldIsInitiallyLocal(SootFieldRef fieldRef)
	{
		if(fieldRef.isStatic())
			return false;
		return true;
		
		// public fields require a whole-program search for accesses outside of this class
		//  - beware reentrant behavior, as a LocalObjectsScopeAnalysis will be required to analyze these accesses if found
		// protected and package-private fields require a this-package search for accesses outside of this class
		//  - beware reentrant behavior, as a LocalObjectsScopeAnalysis will be required to analyze these accesses if found
		// private fields are assumed local
		// NOTE THAT THIS ANALYSIS'S RESULTS DO NOT APPLY TO SUBCLASSES OF THE SCOPE CLASS
	}
	
	// Should perform a Local-Inputs analysis on the callsites to this method
	private boolean parameterIsLocal(SootMethod method, EquivalentValue parameterRef)
	{
		return false;
	}
}

