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

/** Implementation of Class Hierarchy Analysis. */
public class ClassHierarchyAnalysis
{
    /** Creates a new InvokeGraph based on CHA from the current Scene. */
    public static InvokeGraph newInvokeGraph()
    {
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

        InvokeGraph g = new InvokeGraph();

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
                    b = m.getBodyFromMethodSource("cha.jb");
                else
                    b = m.getActiveBody();

                Iterator unitsIt = b.getUnits().iterator();
                while (unitsIt.hasNext())
                {
                    Stmt s = (Stmt)unitsIt.next();
                    if (s.containsInvokeExpr())
                    {
                        InvokeExpr ie = (InvokeExpr)s.getInvokeExpr();
                        if (ie instanceof VirtualInvokeExpr ||
                            ie instanceof InterfaceInvokeExpr)
                        {
                            Type receiverType = ((InstanceInvokeExpr)ie).getBase().getType();

                            g.addSite(s, m);
                            
                            if(receiverType instanceof RefType)
                            {   
                                // since Type might be Null
                                Iterator targetsIt = h.resolveAbstractDispatch(((RefType)receiverType).getSootClass(), 
                                    ie.getMethod()).iterator();
                            
                                while (targetsIt.hasNext())
                                    g.addTarget(s, (SootMethod)targetsIt.next());
                            }
                        }
                        else if (ie instanceof StaticInvokeExpr)
                        {
                            g.addSite(s, m);
                            g.addTarget(s, ie.getMethod());
                        }
                        else if (ie instanceof SpecialInvokeExpr)
                        {
                            g.addSite(s, m);
                            g.addTarget(s, h.resolveSpecialDispatch((SpecialInvokeExpr)ie, m));
                        }
                    }
                }
            }
        }

        return g;
    }
}
