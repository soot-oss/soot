package soot.jimple.toolkits.invoke;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;
import soot.util.*;

public class StaticMethodBinder
{
    private static boolean enableNullPointerCheckInsertion = true;

    public static void bindStaticMethods()
    {
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

                if (g.getInvokeExprsIn(container).size() == 0)
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
                    // HACK
                    if (targets == null)
                        continue;

                    if (targets.size() > 1 || 
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
                                                       target.getReturnType(), target.getModifiers());
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

                                    g.addInvokeExpr(newIE, ct);
                                    g.imitateInvokeExpr(newIE, oldIE);
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

                        g.removeInvokeExpr(ie);
                        g.addInvokeExpr(sie, container);
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
