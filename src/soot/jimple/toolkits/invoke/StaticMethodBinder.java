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

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;
import soot.util.*;

public class StaticMethodBinder extends SceneTransformer
{
    private static StaticMethodBinder instance = new StaticMethodBinder();
    private StaticMethodBinder() {}

    public static StaticMethodBinder v() { return instance; }

    protected void internalTransform(Map options)
    {
        boolean enableNullPointerCheckInsertion = !Options.getBoolean(options, "no-insert-null-checks");

        HashMap instanceToStaticMap = new HashMap();

        InvokeGraph g = Scene.v().getActiveInvokeGraph();

        Iterator classesIt = Scene.v().getApplicationClasses().iterator();
        while (classesIt.hasNext())
        {
            SootClass c = (SootClass)classesIt.next();
            
            LinkedList methodsList = new LinkedList(); 
            methodsList.addAll(c.getMethods());

            while (!methodsList.isEmpty())
            {
                SootMethod container = (SootMethod)methodsList.removeFirst();
                JimpleBody b = (JimpleBody)container.getActiveBody();

                if (g.getSitesOf(container).size() == 0)
                    continue;

                List unitList = new ArrayList(); unitList.addAll(b.getUnits());
                Iterator unitIt = unitList.iterator();

                while (unitIt.hasNext())
                {
                    Stmt s = (Stmt)unitIt.next();
                    if (!s.containsInvokeExpr())
                        continue;

                    InvokeExpr ie = (InvokeExpr)s.getInvokeExpr();

                    List targets = g.getTargetsOf(ie);

                    if (targets.size() != 1 || 
                        ie instanceof StaticInvokeExpr || 
                        ie instanceof SpecialInvokeExpr)
                        continue;

                    // Ok, we have an Interface or VirtualInvoke going to 1.

                    SootMethod target = ie.getMethod();
                    if (!target.getDeclaringClass().isApplicationClass())
                        continue;

                    boolean targetUsesThis = methodUsesThis(target);

                    if (!instanceToStaticMap.containsKey(target))
                    {
                        List newParameterTypes = new ArrayList();
                        if (targetUsesThis)
                            newParameterTypes.add
                                (RefType.v(target.getDeclaringClass().getName()));

                        newParameterTypes.addAll(target.getParameterTypes());

                        // Check for signature conflicts.
                        String newName = target.getName() + "_static";
                        while (target.getDeclaringClass().declaresMethod(newName, 
                                                newParameterTypes,
                                                target.getReturnType()))
                            newName = newName + "_static";

                        SootMethod ct = new SootMethod(newName, newParameterTypes,
                                                       target.getReturnType(), target.getModifiers() | Modifier.STATIC);
                        target.getDeclaringClass().addMethod(ct);

                        methodsList.addLast(ct);

                        ct.setActiveBody((Body)target.getActiveBody().clone());

                        // Make the invoke graph take into account the newly-cloned body.
                        {
                            Iterator oldUnits = target.getActiveBody().getUnits().iterator();
                            Iterator newUnits = ct.getActiveBody().getUnits().iterator();

                            while (newUnits.hasNext())
                            {
                                Stmt oldStmt, newStmt;
                                oldStmt = (Stmt)oldUnits.next(); 
                                newStmt = (Stmt)newUnits.next();

                                if (newStmt.containsInvokeExpr())
                                {
                                    InvokeExpr newIE = (InvokeExpr)newStmt.getInvokeExpr();
                                    InvokeExpr oldIE = (InvokeExpr)oldStmt.getInvokeExpr();

                                    g.addSite(newIE, ct);
                                    g.copyTargets(oldIE, newIE);
                                }
                            }
                        }

                        // Shift the parameter list to apply to the new this parameter.
                        // If the method uses this, then we replace 
                        //              the r0 := @this with r0 := @parameter0 & shift.
                        // Otherwise, just zap the r0 := @this.
                        {
                            Body newBody = ct.getActiveBody();

                            Chain units = newBody.getUnits();

                            Iterator unitsIt = newBody.getUnits().snapshotIterator();
                            while (unitsIt.hasNext())
                            {
                                Stmt st = (Stmt)unitsIt.next();
                                if (st instanceof IdentityStmt)
                                {
                                    IdentityStmt is = (IdentityStmt)st;
                                    if (is.getRightOp() instanceof ThisRef)
                                    {
                                        if (targetUsesThis)
                                            units.swapWith(st, Jimple.v().newIdentityStmt(is.getLeftOp(),
                                                    Jimple.v().newParameterRef(is.getRightOp().getType(), 0)));
                                        else
                                            { units.remove(st); break; }
                                    }
                                    else if (targetUsesThis)
                                    {
                                        if (is.getRightOp() instanceof ParameterRef)
                                        {
                                            ParameterRef ro = (ParameterRef)is.getRightOp();
                                            ro.setIndex(ro.getIndex() + 1);
                                        }
                                    }
                                }
                            }
                            
                        }

                        instanceToStaticMap.put(target, ct);
                    }

                    SootMethod clonedTarget = (SootMethod)instanceToStaticMap.get(target);

                    // Now rebind the method call & fix the invoke graph.
                    {
                        List newArgs = new ArrayList();
                        if (targetUsesThis)
                            newArgs.add(((InstanceInvokeExpr)ie).getBase());
                        newArgs.addAll(ie.getArgs());

                        StaticInvokeExpr sie = Jimple.v().newStaticInvokeExpr
                            (clonedTarget, newArgs);
                        
                        ValueBox ieBox = s.getInvokeExprBox();
                        ieBox.setValue(sie);

                        g.removeSite(ie);
                        g.addSite(sie, container);
                        g.addTarget(sie, clonedTarget);
                    }

                    // Finally, (if enabled), add a null pointer check.
                    if (enableNullPointerCheckInsertion)
                    {
                        Stmt throwPoint = ThrowManager.getNullPointerExceptionThrower(b);
                        b.getUnits().insertBefore(Jimple.v().newIfStmt(Jimple.v().newEqExpr(((InstanceInvokeExpr)ie).getBase(), 
                                                                                            NullConstant.v()), throwPoint),
                                                  s);
                    }
                }
            }
        }
    }

    private static boolean methodUsesThis(SootMethod m)
    {
        JimpleBody b = (JimpleBody)m.getActiveBody();
        CompleteUnitGraph g = new CompleteUnitGraph(b);
        LocalDefs ld = new SimpleLocalDefs(g);
        LocalUses lu = new SimpleLocalUses(g, ld);

        // Look for the first identity stmt assigning from @this.
        {
            Iterator unitsIt = b.getUnits().iterator();
            while (unitsIt.hasNext())
            {
                Stmt s = (Stmt)unitsIt.next();
                if (s instanceof IdentityStmt && 
                    ((IdentityStmt)s).getRightOp() instanceof ThisRef)
                    return lu.getUsesOf(s).size() != 0;
            }
        }

        throw new RuntimeException("couldn't find identityref!");
    }
}
