/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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
import soot.util.*;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.*;

/** Incomplete implementation of Variable Type Analysis (as defined in Vijay Sundaresan's thesis).
 * It remains to implement some notion of a VTA-graph editor (for native methods); also, something
 * needs to be done to properly account for start nodes. */
public class VariableTypeAnalysis
{
    VTATypeGraph vtg;
    HashMap superNodesToReachingTypes = new HashMap();
    InvokeGraph ig;

    StronglyConnectedComponents scc;
    DirectedGraph superGraph;

    List computeReachingTypes(List superNode)
    {
        HashSet retVal = new HashSet();
        Iterator snIt = superNode.iterator();

        while (snIt.hasNext())
        {
            Object o = snIt.next();
            
            retVal.addAll((List)vtg.nodeToReachingTypes.get(o));
        }

        List retList = new LinkedList();
        retList.addAll(retVal);
        return retList;
    }

    /** Constructs a VariableTypeAnalysis object for the given InvokeGraph.
     * Calling trimInvokeGraph will modify the associated invokeGraph according
     * to this VTA's results. */
    public VariableTypeAnalysis(InvokeGraph ig)
    {
        if (Main.isVerbose)
            System.out.println("[vta] Constructing Variable Type Analysis graph.");

        this.ig = ig;
        vtg = new VTATypeGraph(ig);

        // ha!
        scc = new StronglyConnectedComponents(vtg);

        // Now we need a new graph.
        superGraph = scc.getSuperGraph();

        Iterator sgHeadsIt = superGraph.getHeads().iterator();

        while (sgHeadsIt.hasNext())
        {
            List sgNode = (List)sgHeadsIt.next();
            List sgTypes = computeReachingTypes(sgNode);

            superNodesToReachingTypes.put(sgNode, sgTypes);
            visitNode(superGraph, sgNode, sgTypes);
        }
        if (Main.isVerbose)
            System.out.println("[vta] Done constructing Variable Type Analysis graph.");
    }

    /** Uses the results of this analysis to trim the active invoke graph. */
    public void trimActiveInvokeGraph()
    {
        if (Main.isVerbose)
            System.out.println("[vta] Trimming active invoke graph.");

        // Is there a better way to do this?

        List appAndLibClasses = new ArrayList();
        appAndLibClasses.addAll(Scene.v().getApplicationClasses());
        appAndLibClasses.addAll(Scene.v().getLibraryClasses());

        Hierarchy h = null;

        if (!Scene.v().hasActiveHierarchy())
        {
            h = new Hierarchy();
            Scene.v().setActiveHierarchy(h);
        }
        else
            h = Scene.v().getActiveHierarchy();

        Iterator classesIt = appAndLibClasses.iterator();
        while (classesIt.hasNext())
        {
            SootClass c = (SootClass)classesIt.next();
            Iterator methodsIt = c.getMethods().iterator();
            while (methodsIt.hasNext())
            {
                SootMethod m = (SootMethod)methodsIt.next();
                
                if(!m.isConcrete())
                    continue;

                Body b = null;

                if (!m.hasActiveBody())
                    b = m.getBodyFromMethodSource("vta.jb");
                else
                    b = m.getActiveBody();

                Iterator unitsIt = b.getUnits().iterator();
                while (unitsIt.hasNext())
                {
                    Stmt s = (Stmt)unitsIt.next();
                    if (!s.containsInvokeExpr())
                        continue;

                    InvokeExpr ie = (InvokeExpr)s.getInvokeExpr();
                    
                     List ieSites = ig.getTargetsOf(s);

                    if (ie instanceof VirtualInvokeExpr ||
                        ie instanceof InterfaceInvokeExpr)
                    {
                        Value base = ((InstanceInvokeExpr)ie).getBase();
                        Type receiverType = base.getType();

                        if(receiverType instanceof RefType)
                        {
                            // we have, now, a set of reaching types for receiver.
                            // remove extra targets (by clearing targets and re-adding
                            // the ones that VTA doesn't rule out.)
                            ig.removeAllTargets(s);

                            System.out.println("stmt "+s);
                            System.out.println("resolving concrete dispatch; list = "+scc.getComponentOf(VTATypeGraph.getVTALabel(m, base)));
                            System.out.println("reaching types = "+ ((List)superNodesToReachingTypes.get(
                                           scc.getComponentOf(VTATypeGraph.getVTALabel(m, base)))));

                            Iterator targetsIt = h.resolveConcreteDispatch
                                ((List)superNodesToReachingTypes.get(
                                                 scc.getComponentOf(VTATypeGraph.getVTALabel(m, base))), 
                                 ie.getMethod()).iterator();
                            
                            while (targetsIt.hasNext())
                                ig.addTarget(s, (SootMethod)targetsIt.next());
                        }
                    }
                    else if (ie instanceof SpecialInvokeExpr)
                    {
                        ig.addSite(s, m);
                        ig.addTarget(s, h.resolveSpecialDispatch((SpecialInvokeExpr)ie, m));
                    }
                }
            }
        }

        if (Main.isVerbose)
            System.out.println("[vta] Done trimming active invoke graph based on VTA results.");
    }

    // You can also ask about the reaching types for any variable.
    // The API is not yet defined.
    
    private static final int 
            WHITE = 0,
            GRAY = 1,
            BLACK = 2;

    private void visitNode(DirectedGraph graph, Object startNode, List startTypes)
    {
        HashMap nodeToColor = new HashMap();

        HashMap nodeToOutTypes = new HashMap();

        LinkedList nodeStack = new LinkedList();
        LinkedList indexStack = new LinkedList();
        LinkedList typesStack = new LinkedList();
        
        nodeToColor.put(startNode, new Integer(GRAY));
        
        nodeStack.addLast(startNode);
        indexStack.addLast(new Integer(-1));
        typesStack.addLast(startTypes);
        nodeToOutTypes.put(startNode, startTypes);
        
        while(!nodeStack.isEmpty())
        {
            int toVisitIndex = ((Integer) indexStack.removeLast()).intValue();
            Object toVisitNode = nodeStack.getLast();
            List toVisitTypes = (List)typesStack.getLast();

            List outTypes = (List)nodeToOutTypes.get(toVisitNode);
            if (outTypes == null)
            {
                outTypes = new LinkedList(); outTypes.addAll(toVisitTypes); 
                outTypes.addAll(computeReachingTypes((List)toVisitNode));
                nodeToOutTypes.put(toVisitNode, outTypes);
            }
            
            toVisitIndex++;
            
            indexStack.addLast(new Integer(toVisitIndex));
            
            if(toVisitIndex >= graph.getSuccsOf(toVisitNode).size())
            {
                // Visit this node now that we ran out of children 
                    superNodesToReachingTypes.put(toVisitNode, toVisitTypes);
                    nodeToColor.put(toVisitNode, new Integer(BLACK));                
                
                // Pop this node off
                    nodeStack.removeLast();
                    indexStack.removeLast();
                    typesStack.removeLast();
            }
            else
            {
                Object childNode = graph.getSuccsOf(toVisitNode).get(toVisitIndex);
                
                // Visit this child next if not already visited (or on stack)
                // (not-in-map is tantamount to being WHITE)
                    if(nodeToColor.get(childNode) == null)
                    {
                        nodeToColor.put(childNode, new Integer(GRAY));
                        
                        nodeStack.addLast(childNode);
                        indexStack.addLast(new Integer(-1));
                        typesStack.addLast(outTypes);
                    }
            }
        }
    }
}
