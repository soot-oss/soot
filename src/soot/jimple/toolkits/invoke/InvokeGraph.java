/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Raja Vallee-Rai, Felix Kwok
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
    HashMap methodToContainedSites = new HashMap();
    HashMap targetToCallingSites = new HashMap();

    public MethodCallGraph mcg;

    /** Rebuilds the call graph to include only reachable methods. */
    public void refreshReachableMethods() {
        mcg.refresh();
    }

    /** Computes call graph characteristics, and stores them in the data structure <code>CallGraphStats</code>. */
    public CallGraphStats computeStats() {

        CallGraphStats stats = new CallGraphStats();

        Iterator methodsIt = mcg.getReachableMethods().iterator();
        while (methodsIt.hasNext()) {
            SootMethod m = (SootMethod)methodsIt.next();
            boolean isBench = false;
            String t = m.getDeclaringClass().getName();

            if (! (t.startsWith("java.")  || t.startsWith("sun.") || t.startsWith("sunw.") ||
                   t.startsWith("javax.") || t.startsWith("com.") || t.startsWith("org.")))
                isBench = true;

            stats.nodes++;
            if (isBench)
                stats.benchNodes++;
            
            List lst = (List)methodToContainedSites.get(m);
            if (lst==null)
                continue;

            Iterator sitesIt = lst.iterator();

            while (sitesIt.hasNext()) {
                Stmt s = (Stmt)sitesIt.next();
                int numOfEdges = ((List)siteToTargetMethods.get(s)).size();
                if (numOfEdges <= 1) {
                    stats.monoCS++;
                    stats.monoEdges += numOfEdges;
                    if (isBench) {
                        stats.benchMonoCS++;
                        stats.benchMonoEdges += numOfEdges;
                    }
                    
                }
                else {
                    stats.polyCS++;
                    stats.polyEdges += numOfEdges;
                    if (isBench) {
                        stats.benchPolyCS++;
                        stats.benchPolyEdges += numOfEdges;
                        if (Main.isVerbose) {
                            System.out.println("Polymorphic site: "+s);
                            System.out.println("in method: "+m);
                            System.out.println("Targets: "+siteToTargetMethods.get(s));
                        }
                    }
                }
            }
        }
        return stats;
    }  

    /** Returns the method that contains <code>site</code>. */
    public SootMethod getDeclaringMethod(Stmt site) 
    {
        return (SootMethod)siteToDeclaringMethod.get(site);
    }

    /** Returns the sites of container added via addSite.
        This captures all of the callsites within container. 
	If you want all of the sites which <i>call</i> a given method, use
	getCallingSitesOf. */
    public List getSitesOf(SootMethod container) 
    {
        List l = (List)methodToContainedSites.get(container);
        if (l != null)
            return l;
        else
            return new ArrayList();
    }

    /** Checks whether a site is included in the invoke graph. */
    public boolean containsSite(Stmt site) {
        return siteToTargetMethods.containsKey(site);
    }

    /** Returns the callsites which potentially invoke target. */
    public List getCallingSitesOf(SootMethod target)
    {
        List l = (List)targetToCallingSites.get(target);
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

        List lst = (List)methodToContainedSites.get(m);
        if (lst == null)
            return new ArrayList();

        Iterator sitesIt = lst.iterator();

        // Use a set as temporary structure to ensure unique targets
        Set targets = new HashSet();

        // The targets of a method is the union of the targets
        // for all invoke expressions in the method
        while (sitesIt.hasNext())
        {
            Stmt s = (Stmt)sitesIt.next();
            targets.addAll(getTargetsOf(s));
        }

        // Transfer the results to a list to match return type 
        ArrayList retList = new ArrayList();
        retList.addAll(targets);
        return retList;
    }

    /** Returns a list of <code>SootMethod</code>s reachable from <code>m</code>. */
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

	l = (List)targetToCallingSites.get(target);
	l.remove(site);
	if (l.isEmpty())
	  targetToCallingSites.remove(target);
    }

    public void removeAllTargets(Stmt site)
    {
	List l = (List)siteToTargetMethods.get(site);
	Iterator it = l.iterator();
	while (it.hasNext())
	{
	    SootMethod m = (SootMethod)it.next();
	    List l0 = (List)targetToCallingSites.get(m);
	    l0.remove(site);
	    if (l0.isEmpty())
	      targetToCallingSites.remove(m);
	}

        siteToTargetMethods.put(site, new ArrayList());
    }

    /** Add an InvokeGraph target to an Stmt site. 
      * Note that site must previously have been addSite'd. */
    public void addTarget(Stmt site, SootMethod target) 
    {
        List l = (List)siteToTargetMethods.get(site);
        l.add(target);

	l = (List)targetToCallingSites.get(target);
	if (l == null)
	{
	    l = new ArrayList();
	    targetToCallingSites.put(target, l);
	}
	l.add(site);
    }

    public void addSite(Stmt site, SootMethod container) 
    {
        siteToDeclaringMethod.put(site, container);
        siteToTargetMethods.put(site, new ArrayList());

        List l = (List)methodToContainedSites.get(container);
        if (l == null) 
            l = new ArrayList();
        l.add(site);
        methodToContainedSites.put(container, l);
    }

    public void removeSite(Stmt site) 
    {
        SootMethod d = (SootMethod)siteToDeclaringMethod.remove(site);
        siteToTargetMethods.remove(site);
        List l = (List)methodToContainedSites.get(d);
        if (l.size() == 1)
            methodToContainedSites.remove(d);
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
        return new MethodCallGraph(this);
    }

    public MutableDirectedGraph newMethodGraph(Collection methodSet)
    {
        return new MethodCallGraph(this, methodSet);
    }


  /* used for point to analysis */
  HashSet reachableMethods;
  
  public HashSet getReachableMethods() {
    return reachableMethods;
  }
  
  public void setReachableMethods(HashSet methods) {
    reachableMethods = methods;
  }

  public int numOfMethods() {
    return reachableMethods.size();
  }
}

class CallGraphStats 
{  
    int nodes;
    int benchNodes;
    int monoCS;
    int polyCS;
    int monoEdges;
    int polyEdges;
    int benchMonoCS;
    int benchPolyCS;
    int benchMonoEdges;
    int benchPolyEdges;

    public CallGraphStats() 
    {
        nodes = 0;
        benchNodes = 0;
        monoCS = 0;
        polyCS = 0;
        monoEdges = 0;
        polyEdges = 0;
        benchMonoCS = 0;
        benchPolyCS = 0;
        benchMonoEdges = 0;
        benchPolyEdges = 0;
    }

    public String toString() 
    {
        String s = "\n";
        s = s + "          Call Graph Statistics\n";
        s = s + "============================================\n";
        s = s + "Number of nodes = "+nodes+"\n";
        s = s + "Number of sites = "+(monoCS+polyCS)+"\n";
        s = s + "Number of resolved sites = "+monoCS+"\n";
        s = s + "Number of unresolved sites = "+polyCS+"\n";
        s = s + "Number of resolved edges = "+monoEdges+"\n";
        s = s + "Number of unresolved edges = "+polyEdges+"\n";
        s = s + "Number of benchmark nodes = "+benchNodes+"\n";
        s = s + "Number of benchmark sites = "+(benchMonoCS+benchPolyCS)+"\n";
        s = s + "Number of resolved benchmark sites = "+benchMonoCS+"\n";
        s = s + "Number of unresolved benchmark sites = "+benchPolyCS+"\n";
        s = s + "Number of resolved benchmark edges = "+benchMonoEdges+"\n";
        s = s + "Number of unresolved benchmark edges = "+benchPolyEdges+"\n";
        return s;
    }
}
