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

import java.util.*;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.*;

/** Maps invokeExpr's to their declaring and target methods. 
 * ClassHierarchyAnalysis is the default source of InvokeGraphs, although
 * VTA and RTA can create or trim these graphs. */
public class InvokeGraph
{   
    HashMap invokeExprToDeclaringMethod = new HashMap();
    HashMap invokeExprToTargetMethods = new HashMap();
    HashMap methodToInvokeExprs = new HashMap();
  
    public SootMethod getDeclaringMethod(InvokeExpr ie) 
    {
        return (SootMethod)invokeExprToDeclaringMethod.get(ie);
    }

    /** Returns the invoke expressions of container added via addInvokeExpr */
    public List getSitesOf(SootMethod container) 
    {
        List l = (List)methodToInvokeExprs.get(container);
        if (l != null)
            return l;
        else
            return new ArrayList();
    }

    /** Returns the list of targets of SootMethod */
    public List getTargetsOf(SootMethod m) 
    {
	if ((!m.isConcrete()) || (!m.hasActiveBody()))
	{
	    // No body to look at so no targets
	    return new ArrayList();
	}

	Iterator unitsIt = m.getActiveBody().getUnits().iterator();

	// Use a set as temporary structure to ensure unique targets
	Set targets = new HashSet();

	// The targets of a method is the union of the targets
	// for all invoke expressions in the method
	while (unitsIt.hasNext())
        {
	    Stmt s = (Stmt)unitsIt.next();
	    if (s.containsInvokeExpr())
	    {
		InvokeExpr ie = (InvokeExpr)s.getInvokeExpr();

		try {
		    targets.addAll(getTargetsOf(ie));
		}
		catch (java.lang.RuntimeException e) {}
	    }
	}

	// Transfer the results to a list to match return type 
	ArrayList retList = new ArrayList();
	retList.addAll(targets);
	return retList;
    }

    public List getTransitiveTargetsOf(SootMethod m) 
    {
	Set workList  = new HashSet();
	Set processed = new HashSet();
	Set targets   = new HashSet();

	// Start with just this method
	workList.add(m);
	
	while(!workList.isEmpty())
	{
	    SootMethod cur = (SootMethod) workList.iterator().next();

	    // Pop a method from the worklist, add it to the processed set
	    workList.remove(cur);
	    processed.add(cur);

	    // Find the targets of this method that are not 
	    // already in the processed set
	    List curTargets = getTargetsOf(cur);
	    curTargets.removeAll(processed);

	    // Add these targets to the result set
	    // and to the worklist
	    targets.addAll(curTargets);
	    workList.addAll(curTargets);
	}
	
	// Transfer the results to a list to match return type 
	ArrayList toReturn = new ArrayList();
	toReturn.addAll(targets);
	return toReturn;
    }

    public List getTargetsOf(InvokeExpr ie) 
    {
        List toReturn = (List) invokeExprToTargetMethods.get(ie);

        if(toReturn == null)
            throw new RuntimeException("Site is not part of invoke graph!");
            
        return toReturn;
    }

    public void removeTarget(InvokeExpr ie, SootMethod target) 
    {
        List l = (List)invokeExprToTargetMethods.get(ie);
        l.remove(target);
    }

    /** Add an InvokeGraph target to an InvokeExpr ie. 
      * Note that ie must previously have been addInvokeExpr'd. */
    public void addTarget(InvokeExpr ie, SootMethod target) 
    {
        List l = (List)invokeExprToTargetMethods.get(ie);
        l.add(target);
    }

    public void addSite(InvokeExpr ie, SootMethod container) 
    {
        invokeExprToDeclaringMethod.put(ie, container);
        invokeExprToTargetMethods.put(ie, new ArrayList());

        List l = (List)methodToInvokeExprs.get(container);
        if (l == null) 
            l = new ArrayList();
        l.add(ie);

        methodToInvokeExprs.put(container, l);
    }

    public void removeSite(InvokeExpr ie) 
    {
        SootMethod d = (SootMethod)invokeExprToDeclaringMethod.remove(ie);
        invokeExprToTargetMethods.remove(ie);
        List l = (List)methodToInvokeExprs.get(d);
        if (l.size() == 1)
            methodToInvokeExprs.remove(d);
        else
            l.remove(ie);            
    }

    /** This method is to be called after the imitator has been addInvokeExpr'd. */
    public void copyTargets(InvokeExpr roleModel, InvokeExpr imitator)
    {
        Iterator it = getTargetsOf(roleModel).iterator();
        while (it.hasNext())
            addTarget(imitator, (SootMethod)it.next());
    }

    public MutableDirectedGraph newMethodGraph()
    {
        HashMutableDirectedGraph g = new HashMutableDirectedGraph();

        List appAndLibClasses = new ArrayList();
        appAndLibClasses.addAll(Scene.v().getApplicationClasses());
        appAndLibClasses.addAll(Scene.v().getLibraryClasses());

        // Add all of the methods as nodes of g.
        // Note that if we had a list of entryPoints, we'd
        // have a mini tree shaker.
        {
            Iterator classesIt = appAndLibClasses.iterator();

            while (classesIt.hasNext())
            {
                SootClass c = (SootClass)classesIt.next();
                Iterator methodsIt = c.getMethods().iterator();
                while (methodsIt.hasNext())
                {
                    SootMethod m = (SootMethod)methodsIt.next();
                    g.addNode(m);
                }
            }
        }

        // Add edges to g
        {
            Iterator methodsIt = g.getNodes().iterator();
            while (methodsIt.hasNext())
            {
                SootMethod m = (SootMethod)methodsIt.next();
                
                if(!m.isConcrete())
                    continue;

                Iterator sitesIt = getSitesOf(m).iterator();
                while (sitesIt.hasNext())
                {
                    InvokeExpr ie = (InvokeExpr)sitesIt.next();
                    Iterator targetsIt = getTargetsOf(ie).iterator();
                    while (targetsIt.hasNext())
                    {
                        Object target = targetsIt.next();
                        if (g.containsNode(target))
                            g.addEdge(m, target);
                    }
                }
            }
        }
        return g;
    }
}

