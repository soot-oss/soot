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

/** A graph mapping invoke statements to their declaring and target methods. 
 * ClassHierarchyAnalysis is the default source of InvokeGraphs, although
 * VTA and RTA can create or trim these graphs. */
public class InvokeGraph
{   
    HashMap siteToDeclaringMethod = new HashMap();
    HashMap siteToTargetMethods = new HashMap();
    HashMap methodToSites = new HashMap();
  
    public SootMethod getDeclaringMethod(Stmt site) 
    {
        return (SootMethod)siteToDeclaringMethod.get(site);
    }

    /** Returns the sites of container added via addSite */
    public List getSitesOf(SootMethod container) 
    {
        List l = (List)methodToSites.get(container);
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
                targets.addAll(getTargetsOf(s));
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

    public List getTargetsOf(Stmt site) 
    {
        List toReturn = (List) siteToTargetMethods.get(site);

        if(toReturn == null)
            throw new RuntimeException("Site is not part of invoke graph!");
            
        return toReturn;
    }

    public void removeTarget(Stmt site, SootMethod target) 
    {
        List l = (List)siteToTargetMethods.get(site);
        l.remove(target);
    }

    public void removeAllTargets(Stmt site)
    {
        siteToTargetMethods.put(site, new ArrayList());
    }

    /** Add an InvokeGraph target to an Stmt site. 
      * Note that site must previously have been addSite'd. */
    public void addTarget(Stmt site, SootMethod target) 
    {
        List l = (List)siteToTargetMethods.get(site);
        l.add(target);
    }

    public void addSite(Stmt site, SootMethod container) 
    {
        siteToDeclaringMethod.put(site, container);
        siteToTargetMethods.put(site, new ArrayList());

        List l = (List)methodToSites.get(container);
        if (l == null) 
            l = new ArrayList();
        l.add(site);

        methodToSites.put(container, l);
    }

    public void removeSite(Stmt site) 
    {
        SootMethod d = (SootMethod)siteToDeclaringMethod.remove(site);
        siteToTargetMethods.remove(site);
        List l = (List)methodToSites.get(d);
        if (l.size() == 1)
            methodToSites.remove(d);
        else
            l.remove(site);            
    }

    /** This method is to be called after the imitator has been addSite'd. */
    public void copyTargets(Stmt roleModel, Stmt imitator)
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
                    Stmt site = (Stmt)sitesIt.next();
                    Iterator targetsIt = getTargetsOf(site).iterator();
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

