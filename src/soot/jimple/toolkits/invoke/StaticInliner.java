/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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

import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.scalar.*;
import soot.jimple.toolkits.callgraph.*;
import soot.toolkits.graph.*;
import java.util.*;
import soot.util.*;

/** Uses the Scene's currently-active InvokeGraph to inline monomorphic call sites. */
public class StaticInliner extends SceneTransformer
{
    public StaticInliner( Singletons.Global g ) {}
    public static StaticInliner v() { return G.v().soot_jimple_toolkits_invoke_StaticInliner(); }

    protected void internalTransform(String phaseName, Map options)
    {
        Filter explicitInvokesFilter = new Filter( new ExplicitEdgesPred() );
        if(Options.v().verbose())
            G.v().out.println("[] Inlining methods...");

        boolean enableNullPointerCheckInsertion = PhaseOptions.getBoolean(options, "insert-null-checks");
        boolean enableRedundantCastInsertion = PhaseOptions.getBoolean(options, "insert-redundant-casts");
        String modifierOptions = PhaseOptions.getString(options, "allowed-modifier-changes");
        float expansionFactor = PhaseOptions.getFloat(options, "expansion-factor");
        int maxContainerSize = PhaseOptions.getInt(options, "max-container-size");
        int maxInlineeSize = PhaseOptions.getInt(options, "max-inlinee-size");
        boolean rerunJb = PhaseOptions.getBoolean(options, "rerun-jb");

        HashMap instanceToStaticMap = new HashMap();

        CallGraph cg = Scene.v().getCallGraph();
        Hierarchy hierarchy = Scene.v().getActiveHierarchy();

        ArrayList sitesToInline = new ArrayList();

        computeAverageMethodSizeAndSaveOriginalSizes();
        // Visit each potential site in reverse pseudo topological order.
        {
            TopologicalOrderer orderer = new TopologicalOrderer(cg);
            orderer.go();
            List order = orderer.order();
            ListIterator it = order.listIterator(order.size());
    
            while (it.hasPrevious())
            {
                SootMethod container = (SootMethod)it.previous();
                if( methodToOriginalSize.get(container) == null ) continue;
    
                if (!container.isConcrete())
                    continue;
    
                if (!explicitInvokesFilter.wrap( cg.edgesOutOf(container) ).hasNext())
                    continue;
    
                JimpleBody b = (JimpleBody)container.retrieveActiveBody();
                    
                List unitList = new ArrayList(); unitList.addAll(b.getUnits());
                Iterator unitIt = unitList.iterator();
    
                while (unitIt.hasNext())
                {
                    Stmt s = (Stmt)unitIt.next();
                    if (!s.containsInvokeExpr())
                        continue;
                    
                    Iterator targets = new Targets(
                            explicitInvokesFilter.wrap( cg.edgesOutOf(s) ) );
                    if( !targets.hasNext() ) continue;
                    SootMethod target = (SootMethod)targets.next();
                    if( targets.hasNext() ) continue;
    
                    if (!target.getDeclaringClass().isApplicationClass() || !target.isConcrete())
                        continue;
    
                    if(!InlinerSafetyManager.ensureInlinability(target, s, container, modifierOptions))
                        continue;
                        
                    List l = new ArrayList();
                    l.add(target); l.add(s); l.add(container);
                    
                    sitesToInline.add(l);
                }
            }
        }
        
        // Proceed to inline the sites, one at a time, keeping track of
        // expansion rates.
        {

            Iterator sitesIt = sitesToInline.iterator();
            while (sitesIt.hasNext())
            {
                List l = (List)sitesIt.next();
                SootMethod inlinee = (SootMethod)l.get(0);
                int inlineeSize = ((JimpleBody)(inlinee.retrieveActiveBody())).getUnits().size();

                Stmt invokeStmt = (Stmt)l.get(1);

                SootMethod container = (SootMethod)l.get(2);
                int containerSize = ((JimpleBody)(container.retrieveActiveBody())).getUnits().size();
                
                if (inlineeSize + containerSize > maxContainerSize)
                    continue;

                if (inlineeSize > maxInlineeSize)
                    continue;

                if (inlineeSize + containerSize > 
                         expansionFactor * ((Integer)methodToOriginalSize.get(container)).intValue())
                    continue;

                if(InlinerSafetyManager.ensureInlinability(inlinee, invokeStmt, container, modifierOptions))
                {
                    // Not that it is important to check right before inlining if the site is still valid.
                    
                    SiteInliner.inlineSite(inlinee, invokeStmt, container, options);
                    if( rerunJb ) {
                        PackManager.v().getPack("jb").apply(container.getActiveBody());
                    }
                }
            }
        }
    }

    private HashMap methodToOriginalSize = new HashMap();
    private int avgSize = 0;

    private void computeAverageMethodSizeAndSaveOriginalSizes()
    {
        long sum = 0, count = 0;
        Iterator classesIt = Scene.v().getApplicationClasses().iterator();

        while (classesIt.hasNext())
        {
            SootClass c = (SootClass) classesIt.next();

            Iterator methodsIt = c.methodIterator();
            while (methodsIt.hasNext())
            {
                SootMethod m = (SootMethod) methodsIt.next();
                if (m.isConcrete())
                {
                    int size = ((JimpleBody)m.retrieveActiveBody()).getUnits().size();
                    sum += size;
                    methodToOriginalSize.put(m, new Integer(size));
                    count++;
                }
            }
        }
        if (count == 0)
            return;
        avgSize = (int)(sum/count);
    }
}



