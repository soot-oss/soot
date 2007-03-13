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

// UseFinder written by Richard L. Halpert, 2007-03-13
// Compiles a list of all uses of fields of each application class within the
// application classes by looking at every application method.
// Compiles a list of all calls to methods of each application class within the
// application classes by using the call graph.  This is for convenience...

public class UseFinder
{
	Map classToExtFieldAccesses;
	Map classToIntFieldAccesses;
	
	public UseFinder()
	{
		classToExtFieldAccesses = new HashMap();
		classToIntFieldAccesses = new HashMap();
	}
	
	public void doAnalysis()
	{
		Chain appClasses = Scene.v().getApplicationClasses();
		
		// Set up lists of internal and external accesses
		Iterator appClassesIt = appClasses.iterator();
		while(appClassesIt.hasNext())
		{
			SootClass appClass = (SootClass) appClassesIt.next();
			List intAccesses = new ArrayList();
			List extAccesses = new ArrayList();
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
				if(method.isConcrete())
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
								((List) classToExtFieldAccesses.get(appClass)).add(new Pair(method, s));
							}
						}
					}
				}
			}
		}
	}
}
