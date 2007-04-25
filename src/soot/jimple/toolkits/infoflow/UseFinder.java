package soot.jimple.toolkits.infoflow;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.toolkits.scalar.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.*;

// UseFinder written by Richard L. Halpert, 2007-03-13
// Compiles a list of all uses of fields of each application class within the
// application classes by looking at every application method.
// Compiles a list of all calls to methods of each application class within the
// application classes by using the call graph.

public class UseFinder
{
	ReachableMethods rm;

	Map classToExtFieldAccesses; // each field access is a Pair <containing method, stmt>
	Map classToIntFieldAccesses;
	
	Map classToExtCalls; // each call is a Pair <containing method, stmt>
	Map classToIntCalls;
	
	public UseFinder()
	{
		classToExtFieldAccesses = new HashMap();
		classToIntFieldAccesses = new HashMap();
		classToExtCalls = new HashMap();
		classToIntCalls = new HashMap();
		
		rm = Scene.v().getReachableMethods();
		
		doAnalysis();
	}
	
	public UseFinder(ReachableMethods rm)
	{
		classToExtFieldAccesses = new HashMap();
		classToIntFieldAccesses = new HashMap();
		classToExtCalls = new HashMap();
		classToIntCalls = new HashMap();
		
		this.rm = rm;
		
		doAnalysis();
	}
	
	public List getExtFieldAccesses(SootClass sc)
	{
		if(classToExtFieldAccesses.containsKey(sc))
			return (List) classToExtFieldAccesses.get(sc);
		throw new RuntimeException("UseFinder does not search non-application classes: " + sc);
	}
	
	public List getIntFieldAccesses(SootClass sc)
	{
		if(classToIntFieldAccesses.containsKey(sc))
			return (List) classToIntFieldAccesses.get(sc);
		throw new RuntimeException("UseFinder does not search non-application classes: " + sc);
	}
	
	public List getExtCalls(SootClass sc)
	{
		if(classToExtCalls.containsKey(sc))
			return (List) classToExtCalls.get(sc);
		throw new RuntimeException("UseFinder does not search non-application classes: " + sc);
	}
	
	public List getIntCalls(SootClass sc)
	{
		if(classToIntCalls.containsKey(sc))
			return (List) classToIntCalls.get(sc);
		throw new RuntimeException("UseFinder does not search non-application classes: " + sc);
	}
	
	// This is an incredibly stupid way to do this... we should just use the call graph for faster/better info!
	public List getExtMethods(SootClass sc)
	{
		if(classToExtCalls.containsKey(sc))
		{
			List extCalls = (List) classToExtCalls.get(sc);
			List extMethods = new ArrayList();
			for(Iterator callIt = extCalls.iterator(); callIt.hasNext(); )
			{
				Pair call = (Pair) callIt.next();
				SootMethod calledMethod = ((Stmt) call.getO2()).getInvokeExpr().getMethod();
				if(!extMethods.contains(calledMethod))
					extMethods.add(calledMethod);
			}
			return extMethods;
		}
		throw new RuntimeException("UseFinder does not search non-application classes: " + sc);
	}
	
	public List getExtFields(SootClass sc)
	{
		if(classToExtFieldAccesses.containsKey(sc))
		{
			List extAccesses = (List) classToExtFieldAccesses.get(sc);
			List extFields = new ArrayList();
			for(Iterator accessIt = extAccesses.iterator(); accessIt.hasNext(); )
			{
				Pair access = (Pair) accessIt.next();
				SootField accessedField = ((Stmt) access.getO2()).getFieldRef().getField();
				if(!extFields.contains(accessedField))
					extFields.add(accessedField);
			}
			return extFields;
		}
		throw new RuntimeException("UseFinder does not search non-application classes: " + sc);
	}
	
	private void doAnalysis()
	{
		Chain appClasses = Scene.v().getApplicationClasses();
		
		// Set up lists of internal and external accesses
		Iterator appClassesIt = appClasses.iterator();
		while(appClassesIt.hasNext())
		{
			SootClass appClass = (SootClass) appClassesIt.next();
			classToIntFieldAccesses.put(appClass, new ArrayList());
			classToExtFieldAccesses.put(appClass, new ArrayList());
			classToIntCalls.put(appClass, new ArrayList());
			classToExtCalls.put(appClass, new ArrayList());
		}

		// Find internal and external accesses
		appClassesIt = appClasses.iterator();
		while(appClassesIt.hasNext())
		{
			SootClass appClass = (SootClass) appClassesIt.next();		
    	    Iterator methodsIt = appClass.getMethods().iterator();
    	    while (methodsIt.hasNext())
    	    {
    	    	SootMethod method = (SootMethod) methodsIt.next();
				if(method.isConcrete() && rm.contains(method))
				{
					Body b = method.retrieveActiveBody();
					Iterator unitsIt = b.getUnits().iterator();
					while(unitsIt.hasNext())
					{
						Stmt s = (Stmt) unitsIt.next();
						if(s.containsFieldRef())
						{
							FieldRef fr = s.getFieldRef();
							if(fr.getFieldRef().resolve().getDeclaringClass() == appClass)
							{
								if(fr instanceof StaticFieldRef)
								{
									// static field ref in same class is considered internal
									((List) classToIntFieldAccesses.get(appClass)).add(new Pair(method, s));
								}
								else if(fr instanceof InstanceFieldRef)
								{
									InstanceFieldRef ifr = (InstanceFieldRef) fr;
									if( !method.isStatic() && ifr.getBase().equivTo(b.getThisLocal()) )
									{
										// this.field ref is considered internal
										((List) classToIntFieldAccesses.get(appClass)).add(new Pair(method, s));
									}
									else
									{
										// o.field ref is considered external
										((List) classToExtFieldAccesses.get(appClass)).add(new Pair(method, s));
									}
								}
							}
							else
							{
								// ref to some other class is considered external
								List otherClassList = ((List) classToExtFieldAccesses.get(fr.getFieldRef().resolve().getDeclaringClass()));
								if(otherClassList == null)
								{
									otherClassList = new ArrayList();
									classToExtFieldAccesses.put(fr.getFieldRef().resolve().getDeclaringClass(), otherClassList);
								}
								otherClassList.add(new Pair(method, s));
							}
						}
						if(s.containsInvokeExpr())
						{
							InvokeExpr ie = s.getInvokeExpr();
							if(ie.getMethodRef().resolve().getDeclaringClass() == appClass) // what about sub/superclasses
							{
								if(ie instanceof StaticInvokeExpr)
								{
									// static field ref in same class is considered internal
									((List) classToIntCalls.get(appClass)).add(new Pair(method, s));
								}
								else if(ie instanceof InstanceInvokeExpr)
								{
									InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
									if( !method.isStatic() && iie.getBase().equivTo(b.getThisLocal()) )
									{
										// this.field ref is considered internal
										((List) classToIntCalls.get(appClass)).add(new Pair(method, s));
									}
									else
									{
										// o.field ref is considered external
										((List) classToExtCalls.get(appClass)).add(new Pair(method, s));
									}
								}
							}
							else
							{
								// ref to some other class is considered external
								List otherClassList = ((List) classToExtCalls.get(ie.getMethodRef().resolve().getDeclaringClass()));
								if(otherClassList == null)
								{
									otherClassList = new ArrayList();
									classToExtCalls.put(ie.getMethodRef().resolve().getDeclaringClass(), otherClassList);
								}
								otherClassList.add(new Pair(method, s));
							}
						}
					}
				}
			}
		}
	}
}
