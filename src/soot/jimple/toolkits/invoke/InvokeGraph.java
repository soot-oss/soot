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

    public List getTargetsOf(SootMethod m) 
    {
        throw new RuntimeException("not implemented yet!");
    }

    public List getTransitiveTargetsOf(SootMethod m) 
    {
        throw new RuntimeException("not implemented yet!");
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
        System.out.println("copy for: " + roleModel);
        Iterator it = getTargetsOf(roleModel).iterator();
        while (it.hasNext())
            addTarget(imitator, (SootMethod)it.next());
    }
}

