/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Felix Kwok
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

import java.util.*;
import soot.*;
import soot.util.*;
import soot.toolkits.graph.*;
import soot.jimple.toolkits.invoke.*;
import soot.jimple.*;

/** A directed graph whose nodes are methods and whose edges are call edges. It also knows
 *  about default entry points, and more entry points can be added using the <code>addEntryPoint</code>
 *  method.
 */

public class MethodCallGraph extends HashMutableDirectedGraph {
      
    private Set methodContained;
    private InvokeGraph ig;
    private HashSet reachableMethods;
    private LinkedList allStartNodes;
    private HashSet entryPoints;     // Additional entry points specified by the caller.

    /** Sets the underlying invoke graph to <code>ig</code>. Does not trigger
     *  recomputation or reachable methods.  
     */
    public void setInvokeGraph(InvokeGraph ig) {
	this.ig = ig;
    }

    /** Constructs a MethodCallGraph based on the invoke graph <code>ig</code>, with
     *  default entry points.
     */
    public MethodCallGraph(InvokeGraph ig) {
	methodContained = new HashSet();
	this.ig = ig;
	reachableMethods = new HashSet();
	entryPoints = (ig.mcg == null) ? null : ig.mcg.entryPoints;
	Date start = new Date();
	initialize();
	Date finish = new Date();
	long runtime = finish.getTime() - start.getTime();
	System.out.println("Computing reachable methods took "+(runtime/60000)+" min. "+
                            ((runtime%60000)/1000)+" sec.");         
    }

    /** Constructs a MethodCallGraph based on the invoke graph <code>ig</code>, with
     *  entry points contained in <code>methods</code>.
     */
    public MethodCallGraph(InvokeGraph ig, Collection methods) {
	methodContained = new HashSet();
	this.ig = ig; 
	reachableMethods = new HashSet();
	entryPoints = new HashSet(methods);
	Date start = new Date();
	initialize();
	Date finish = new Date();
	long runtime = finish.getTime() - start.getTime();
	System.out.println("Computing reachable methods took "+(runtime/60000)+" min. "+
			   ((runtime%60000)/1000)+" sec.");
    }

    /** Recomputes the call graph, based on the entry points specified by the current set
     *  of entry points.
     */
    public void refresh() {
	clearAll();
	methodContained = new HashSet();
	reachableMethods = new HashSet();
	Date start = new Date();
	initialize();
	Date finish = new Date();
	long runtime = finish.getTime() - start.getTime();
	System.out.println("Computing reachable methods took "+(runtime/60000)+" min. "+
			   ((runtime%60000)/1000)+" sec.");
    }

    /** Adds an entry point to the current set of entry points. */
    public void addEntryPoint(SootMethod m) {
	if (entryPoints==null)
	    entryPoints = new HashSet(0);
	entryPoints.add(m);
    }
    /** Clear the current set of entry points. */
    public void clearEntryPoints() {
	entryPoints = null;
    }

    /** Returns the current set of entry points. */
    public List getEntryPoints() {
	return new LinkedList(entryPoints);
    }

    /* Sets up entry points and computes reachable methods. */
    private void initialize() {

	LinkedList st = new LinkedList();
	LinkedList inits = new LinkedList();
         
	// If entryPoints == null, we use the default entry points, PLUS all "void <init>()" if newInstance is reachable.
	// If entryPoints is non-null, the entry points are the defaults plus "entryPoints" (but not all inits).

	// Gather all methods belonging to application or library classes.

	HashSet appAndLibClasses = new HashSet();
	appAndLibClasses.addAll(Scene.v().getApplicationClasses());
	appAndLibClasses.addAll(Scene.v().getLibraryClasses());
	HashSet methodSet = new HashSet();
	Iterator classesIt = appAndLibClasses.iterator();
	while (classesIt.hasNext()) {
	    SootClass c = (SootClass)classesIt.next();
	    Iterator methodsIt = c.getMethods().iterator();
             while (methodsIt.hasNext()) {
                 SootMethod m = (SootMethod)methodsIt.next();
                 String sig = m.getSubSignature();
                 
                 // Set up default entry points.
                 if (sig.equals("void main(java.lang.String[])"))
                     st.addLast(m);
                 else if (sig.equals("void start()"))
                     st.addLast(m);
                 else if (sig.equals("void run()"))
                     st.addLast(m);
                 else if (sig.equals("void finalize()"))
                     st.addLast(m);
                 else if (sig.equals("void <clinit>()"))
                     st.addLast(m);
                 else if (sig.equals("void exit()"))
                     st.addLast(m);
                 else if (sig.equals("java.lang.Class loadClass(java.lang.String)"))
                     st.addLast(m);
                 else if (sig.equals("void <init>()") && entryPoints==null)
                     inits.addLast(m);
             }

         }
         // More default entry points.
         try {
             st.addLast(Scene.v().getMainClass().getMethod("void main(java.lang.String[])"));
             st.addLast(Scene.v().getMethod("<java.lang.System: void initializeSystemClass()>"));
             st.addLast(Scene.v().getMethod("<java.lang.ThreadGroup: void <init>()>"));
             st.addLast(Scene.v().getMethod
                        ("<java.lang.ThreadGroup: void uncaughtException(java.lang.Thread,java.lang.Throwable)>"));
             st.addLast(Scene.v().getMethod("<java.lang.System: void loadLibrary(java.lang.String)>"));
         }
         catch (RuntimeException e) { }

         allStartNodes = new LinkedList(st);
         if (entryPoints!=null)
             allStartNodes.addAll(entryPoints);
         for (Iterator it = allStartNodes.iterator(); it.hasNext(); )
             addEdges((SootMethod)it.next());

         if (entryPoints == null) {
             if (isReachable("<java.lang.Class: java.lang.Object newInstance()>"))
                 for (Iterator it = inits.iterator(); it.hasNext(); )
                     addEdges((SootMethod)it.next());
         }
    }

    /** Returns a list of methods reachable from a method listed in methods. */
    public List getMethodsReachableFrom(Collection methods) {
	return new LinkedList(getMethodsReachableFrom0(methods));
    }          

    private HashSet getMethodsReachableFrom0(Collection methods) {	
	LinkedList st = new LinkedList(methods);
	HashSet greyNodes = new HashSet();
	HashSet retVals = new HashSet();

	while (!st.isEmpty()) {
	    Object o = st.getLast();
	    if (!containsNode(o)) {
		st.removeLast();
		continue;
	    }
	    if (!greyNodes.contains(o)) {
		greyNodes.add(o);
		for (Iterator succsIt = ((List)getSuccsOf(o)).iterator(); succsIt.hasNext(); ) {
		    Object child = succsIt.next();
		    if (!greyNodes.contains(child))
			st.addLast(child);
		}
	    }
	    else {
		retVals.add(o);
		st.removeLast();
	    }
	}
	return retVals;
    }

    /** Returns true if the method specified in <code>signature</code> is reachable. */
    public boolean isReachable(String signature) {
	return containsNode(Scene.v().getMethod(signature));
    }

    /** Returns true if the method <code>m</code> is reachable. *.
	public boolean isReachable(SootMethod m) {
	return containsNode(m);
	}

    /** Returns a list of reachable methods. */
    public List getReachableMethods() {
	return getNodes();
    }
      
    // A non-recursive implementation of addEdges (for speed)
    // John pointed out to replace getNodes.contains() by containsNode.
    private void addEdges(SootMethod meth) {
	if (methodContained.contains(meth))
	    return;
	methodContained.add(meth);
	
	if (!containsNode(meth))
	    addNode(meth);

	LinkedList st = new LinkedList();
	st.addLast(meth);
	while (!st.isEmpty()) {
	    SootMethod m = (SootMethod)st.removeLast();
	    for (Iterator targetsIt = ig.getTargetsOf(m).iterator(); targetsIt.hasNext(); ) {
		SootMethod target = (SootMethod)targetsIt.next();
		if (!containsNode(target))
		    addNode(target);
		addEdge(m, target);
		if (methodContained.contains(target))
		    continue;
		methodContained.add(target);
		st.addLast(target);
	    }
	}
    }
}





