/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.jimple.toolkits.invoke;
import soot.options.*;

import java.util.*;
import soot.*;
import soot.jimple.*;
import soot.util.*;

/** Implementation of Class Hierarchy Analysis. */
public class ClassHierarchyAnalysis
{

    /* We really need a newConsertiveInvokeGraph() method. */
   

    /** Creates a new InvokeGraph based on CHA from the current Scene. */

  public static InvokeGraph newInvokeGraph(boolean buildCallGraph,
          boolean includeLibrary ) {

    HashSet visitedMethods = new HashSet(3000);

    List appAndLibClasses = new ArrayList();
    appAndLibClasses.addAll(Scene.v().getApplicationClasses());
    if( includeLibrary ) 
        appAndLibClasses.addAll(Scene.v().getLibraryClasses());
    else
        G.v().out.println( "Warning: using incomplete invoke graph: "+
                "excluding library classes." );


    FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();

    // This is still here only because other analyses expect a Hierarchy
    // to exist in the Scene. The Hierarchy is not used in this class.
    if( !Scene.v().hasActiveHierarchy() ) {
        Scene.v().setActiveHierarchy( new Hierarchy() );
    }

    InvokeGraph g = new InvokeGraph();

    Iterator classesIt = appAndLibClasses.iterator();
    while (classesIt.hasNext()) {
      SootClass c = (SootClass)classesIt.next();

      Iterator methodsIt = c.methodIterator();
      while (methodsIt.hasNext()) {
	SootMethod m = (SootMethod)methodsIt.next();

	if(!m.isConcrete())
	  continue;

	visitedMethods.add(m);

	Body b = m.retrieveActiveBody();

	Iterator unitsIt = b.getUnits().iterator();
	while (unitsIt.hasNext()) {
	  Stmt s = (Stmt)unitsIt.next();
	  if (s.containsInvokeExpr()) {
	    InvokeExpr ie = (InvokeExpr)s.getInvokeExpr();
	    if (ie instanceof VirtualInvokeExpr ||
		ie instanceof InterfaceInvokeExpr) {
	      Type receiverType = ((InstanceInvokeExpr)ie).getBase().getType();

	      g.addSite(s, m);
                            
	      if(receiverType instanceof RefType) {   
		// since Type might be Null
		Iterator targetsIt = 
		  fh.resolveAbstractDispatch(((RefType)receiverType).getSootClass(), 
					    ie.getMethod()).iterator();
                            
		while (targetsIt.hasNext())
		  g.addTarget(s, (SootMethod)targetsIt.next());
	      }
	    } else if (ie instanceof StaticInvokeExpr) {
	      g.addSite(s, m);
	      g.addTarget(s, ie.getMethod());
	    } else if (ie instanceof SpecialInvokeExpr) {
	      g.addSite(s, m);
	      g.addTarget(s, fh.resolveSpecialDispatch((SpecialInvokeExpr)ie, m));
	    }
	  }
	}
      }
    }

    g.setReachableMethods(visitedMethods);

    if (buildCallGraph) {
      g.mcg = (MethodCallGraph)g.newMethodGraph();
    }

    if (Options.v().verbose()) {
      G.v().out.println("  processed "+appAndLibClasses.size()+" classes");
      G.v().out.println("  processed "+visitedMethods.size()+" methods");
    }

    return g;
  }

  /* based on the class hierarchy, compute all reachable methods from 
   * method entry points.
   * 
   * NOTE: I am assuming no dynamic class loading. Otherwise, we need
   * flow analysis, or most conservative approach to handle it.
   * 
   * @param buildCallGraph, if true, build the call graph
   */
  public static InvokeGraph newPreciseInvokeGraph(boolean buildCallGraph) {

    FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
    //
    // This is still here only because other analyses expect a Hierarchy
    // to exist in the Scene. The Hierarchy is not used in this class.
    if( !Scene.v().hasActiveHierarchy() ) {
        Scene.v().setActiveHierarchy( new Hierarchy() );
    }

    InvokeGraph g = new InvokeGraph();

    LinkedList worklist = new LinkedList();
    HashSet visitedMethods = new HashSet(3000);
    HashSet visitedClasses = new HashSet(1000);

    getSystemStartingMethods(worklist, visitedMethods);
    
    while (!worklist.isEmpty()) {
      SootMethod m = (SootMethod)worklist.removeFirst();

      SootClass c = m.getDeclaringClass();
      
      if (!visitedClasses.contains(c)) {
	getDefaultEntryPoints(c, worklist, visitedMethods);
	visitedClasses.add(c);
      }

      if (!m.isConcrete()) continue;

      //      G.v().out.println("  processing "+m.getSignature()+" ...");
      
      Body b = m.retrieveActiveBody();

      Iterator unitsIt = b.getUnits().iterator();
      while (unitsIt.hasNext()) {
	Stmt s = (Stmt)unitsIt.next();

	if (s.containsFieldRef()) {
	  FieldRef fref = (FieldRef) s.getFieldRef();
	  
	  if (fref instanceof StaticFieldRef) {
	    SootField field = fref.getField();
	    
	    SootClass klass = field.getDeclaringClass();
	
	    // do not mark class as visited
	    getClinitMethod(klass, worklist, visitedMethods);
	  }

	} else if (s.containsInvokeExpr()) {
	  InvokeExpr ie = (InvokeExpr)s.getInvokeExpr();
	  if (ie instanceof VirtualInvokeExpr ||
	      ie instanceof InterfaceInvokeExpr) {
	    Type receiverType = ((InstanceInvokeExpr)ie).getBase().getType();
	 
	    g.addSite(s, m);
                            
	    if(receiverType instanceof RefType) {   
	      // since Type might be Null
	      Iterator targetsIt = 
		fh.resolveAbstractDispatch(((RefType)receiverType).getSootClass(), 
					  ie.getMethod()).iterator();
	        
	      while (targetsIt.hasNext()) {
		SootMethod target = (SootMethod)targetsIt.next();
		g.addTarget(s, target);
		
		if (!visitedMethods.contains(target)) {
		  worklist.addLast(target);
		  visitedMethods.add(target);
		}
	      }
	    }
	  } else if (ie instanceof StaticInvokeExpr) {
	    g.addSite(s, m);
	    SootMethod target = ie.getMethod();
	    g.addTarget(s, target);

	    if (!visitedMethods.contains(target)) {
	      worklist.addLast(target);
	      visitedMethods.add(target);
	    }
	    
	  } else if (ie instanceof SpecialInvokeExpr) {
	    g.addSite(s, m);
	    SootMethod target = fh.resolveSpecialDispatch((SpecialInvokeExpr)ie, m); 
	    g.addTarget(s, target);
	    
	    if (!visitedMethods.contains(target)) {
	      worklist.addLast(target);
	      visitedMethods.add(target);
	    }
	    
	  }
	}
      } // end of while(unitIt)
    } // end of while(worklist)

    g.setReachableMethods(visitedMethods);

    if (buildCallGraph) {
      g.mcg = (MethodCallGraph)g.newMethodGraph();
    }

//    if (Options.v().verbose()) 
    {
      G.v().out.println("  processed "+visitedClasses.size()+" classes");
      G.v().out.println("  processed "+visitedMethods.size()+" methods");
    }
    return g;	
  }

  private static SootMethod forceGetMethod( String sig ) {
      if( !Scene.v().containsMethod( sig ) ) return null;
      return Scene.v().getMethod(sig);
  }
  /* for classes touched by getStatic, we need to run <clinit> method
   */
  private static void getClinitMethod(SootClass c, 
				      LinkedList worklist,
				      HashSet visited) {
    String fullsig = "<" + c.getName()+": void <clinit>()>";
    SootMethod m = forceGetMethod(fullsig);
    if ((m!=null) &&(!visited.contains(m))) {
      worklist.addLast(m);
      visited.add(m);
    }
  }

  private final static String[] smsig = {"void start()",
				   "void run()",
				   "void finalize()",
				   "void <clinit>()",
				   "void exit()"};

  private static void getDefaultEntryPoints(SootClass c, 
					    LinkedList worklist,
					    HashSet visited) {
    for (int i=0, n=smsig.length; i<n; i++) {
      String fullsig = "<"+c.getName()+": "+smsig[i]+">";
      SootMethod m = forceGetMethod(fullsig);

      if ((m != null) && (!visited.contains(m))) {
	worklist.addLast(m);
	visited.add(m);
      }  
    }
  }

  private static void getSystemStartingMethods(LinkedList worklist,
					       HashSet visited) {
    // Default entry points.
    try {
      SootMethod m = 
	Scene.v().getMainClass().getMethod("void main(java.lang.String[])");
      worklist.addLast(m);
      visited.add(m);

      m = Scene.v().getMethod("<java.lang.System: void initializeSystemClass()>");
      worklist.addLast(m);
      visited.add(m);

      m = Scene.v().getMethod("<java.lang.ThreadGroup: void <init>()>");
      worklist.addLast(m);
      visited.add(m);

      m = Scene.v().getMethod("<java.lang.ThreadGroup: void uncaughtException(java.lang.Thread,java.lang.Throwable)>");
      worklist.addLast(m);
      visited.add(m);

      m = Scene.v().getMethod("<java.lang.System: void loadLibrary(java.lang.String)>");
      worklist.addLast(m);
      visited.add(m);

    } catch (RuntimeException e) {
    }
  }

}
