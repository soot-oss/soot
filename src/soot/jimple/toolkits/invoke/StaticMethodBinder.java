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

import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.scalar.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;
import soot.util.*;

/** Uses the Scene's currently-active InvokeGraph to statically bind monomorphic call sites. */
public class StaticMethodBinder extends SceneTransformer
{
    public StaticMethodBinder( Singletons.Global g ) {}
    public static StaticMethodBinder v() { return G.v().StaticMethodBinder(); }

    protected void internalTransform(String phaseName, Map options)
    {
        Date start = new Date();

        Date finish = new Date();
        if (Options.v().verbose()) {
            G.v().out.println("[stb] Done building invoke graph.");
            long runtime = finish.getTime() - start.getTime();
            G.v().out.println("[stb] Invoke graph building took "+ (runtime/60000)+" min. "+ ((runtime%60000)/1000)+" sec.");
        }

        boolean enableNullPointerCheckInsertion = PackManager.getBoolean(options, "insert-null-checks");
        boolean enableRedundantCastInsertion = PackManager.getBoolean(options, "insert-redundant-casts");
        String modifierOptions = PackManager.getString(options, "allowed-modifier-changes");
        HashMap instanceToStaticMap = new HashMap();

        InvokeGraph graph = Scene.v().getActiveInvokeGraph();

        Hierarchy hierarchy = Scene.v().getActiveHierarchy();

        if (Options.v().verbose())
            G.v().out.println(graph.computeStats());

        Iterator classesIt = Scene.v().getApplicationClasses().iterator();
        while (classesIt.hasNext())
        {
            SootClass c = (SootClass)classesIt.next();
            
            LinkedList methodsList = new LinkedList(); 
            for( Iterator it = c.methodIterator(); it.hasNext(); ) {
                methodsList.add(it.next());
            }

            while (!methodsList.isEmpty())
            {
                SootMethod container = (SootMethod)methodsList.removeFirst();

                if (!container.isConcrete())
                    continue;

                if (graph.getSitesOf(container).size() == 0)
                    continue;

                JimpleBody b = (JimpleBody)container.getActiveBody();
                
                List unitList = new ArrayList(); unitList.addAll(b.getUnits());
                Iterator unitIt = unitList.iterator();

                while (unitIt.hasNext())
                {
                    Stmt s = (Stmt)unitIt.next();
                    if (!s.containsInvokeExpr())
                        continue;


                    InvokeExpr ie = (InvokeExpr)s.getInvokeExpr();

                    if (ie instanceof StaticInvokeExpr || 
                        ie instanceof SpecialInvokeExpr)
                        continue;

                    List targets = graph.getTargetsOf(s);

                    if (targets.size() != 1)
                        continue;

                    // Ok, we have an Interface or VirtualInvoke going to 1.

                    SootMethod target = (SootMethod)targets.get(0);
                    
                    if (!AccessManager.ensureAccess(container, target, modifierOptions))
                        continue;
                    
                    if (!target.getDeclaringClass().isApplicationClass() || !target.isConcrete())
                        continue;

                    // Don't modify java.lang.Object
                    if (target.getDeclaringClass()==Scene.v().getSootClass("java.lang.Object"))
                        continue;

                    boolean targetUsesThis = true; //methodUsesThis(target);
                    //                    targetUsesThis = true;

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
                                                       target.getReturnType(), target.getModifiers() | Modifier.STATIC,
                                                       target.getExceptions());
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

                                    if(!(newIE instanceof SpecialInvokeExpr))
                                    {
                                        // Might have been added by the ThrowManager without patching graph
                                        
                                        graph.addSite(newStmt, ct);
                                        graph.copyTargets(oldStmt, newStmt);
                                    }
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
                    Value thisToAdd = ((InstanceInvokeExpr)ie).getBase();

                    // Insert casts to please the verifier.
                    if (enableRedundantCastInsertion && targetUsesThis)
                    {
                        // The verifier will complain if targetUsesThis, and:
                        //    the argument passed to the method is not the same type.
                        // For instance, Bottle.price_static takes a cost.
                        // Cost is an interface implemented by Bottle.
                        SootClass localType, parameterType;
                        localType = ((RefType)((InstanceInvokeExpr)ie).getBase().getType()).getSootClass();
                        parameterType = target.getDeclaringClass();

                        if (localType.isInterface() ||
                             hierarchy.isClassSuperclassOf(localType, parameterType))
                        {
                            Local castee = Jimple.v().newLocal("__castee", parameterType.getType());
                            b.getLocals().add(castee);
                            b.getUnits().insertBefore(Jimple.v().newAssignStmt(castee,
                                                         Jimple.v().newCastExpr(((InstanceInvokeExpr)ie).getBase(),
                                                                                parameterType.getType())), s);
                            thisToAdd = castee;
                        }
                    }

                    // Now rebind the method call & fix the invoke graph.
                    {
                        List newArgs = new ArrayList();
                        if (targetUsesThis)
                            newArgs.add(thisToAdd);
                        newArgs.addAll(ie.getArgs());

                        StaticInvokeExpr sie = Jimple.v().newStaticInvokeExpr
                            (clonedTarget, newArgs);
                        
                        ValueBox ieBox = s.getInvokeExprBox();
                        ieBox.setValue(sie);

                        graph.removeSite(s);
                        graph.addSite(s, container);
                        graph.addTarget(s, clonedTarget);
                    }

                    // (If enabled), add a null pointer check.
                    if (enableNullPointerCheckInsertion)
                    {
                        boolean caught = TrapManager.isExceptionCaughtAt
                            (Scene.v().getSootClass("java.lang.NullPointerException"), s, b);

                        /* Ah ha.  Caught again! */
                        if (caught)
                        {
                            /* In this case, we don't use throwPoint;
                             * instead, put the code right there. */
                            Stmt insertee = Jimple.v().newIfStmt(Jimple.v().newNeExpr(((InstanceInvokeExpr)ie).getBase(), 
                                NullConstant.v()), s);

                            b.getUnits().insertBefore(insertee, s);

                            // This sucks (but less than before).
                            ((IfStmt)insertee).setTarget(s);

                            ThrowManager.addThrowAfter(b, insertee);
                        }
                        else
                        {
                            Stmt throwPoint = 
                                ThrowManager.getNullPointerExceptionThrower(b);
                            b.getUnits().insertBefore
                                (Jimple.v().newIfStmt(Jimple.v().newEqExpr(((InstanceInvokeExpr)ie).getBase(), 
                                NullConstant.v()), throwPoint),
                                 s);
                        }
                    }
                    
                    // Add synchronizing stuff.
                    {
                        if (target.isSynchronized())
                        {
                            clonedTarget.setModifiers(clonedTarget.getModifiers() & ~Modifier.SYNCHRONIZED);
                            SynchronizerManager.v().synchronizeStmtOn(s, b, (Local)((InstanceInvokeExpr)ie).getBase());
                        }
                    }

                    // Resolve name collisions.
                        LocalNameStandardizer.v().transform(b, phaseName + ".lns");
                }
            }
        }
        
        Scene.v().releaseActiveInvokeGraph();
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


