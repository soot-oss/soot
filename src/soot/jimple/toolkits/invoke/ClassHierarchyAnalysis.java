package soot.jimple.toolkits.invoke;

import java.util.*;
import soot.*;
import soot.jimple.*;

public class ClassHierarchyAnalysis
{
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
                if (!m.hasActiveBody())
                    m.setActiveBody(new JimpleBody(new ClassFileBody(m)));

                Iterator unitsIt = m.getActiveBody().getUnits().iterator();
                while (unitsIt.hasNext())
                {
                    Stmt s = (Stmt)unitsIt.next();
                    if (s.containsInvokeExpr())
                    {
                        InvokeExpr ie = (InvokeExpr)s.getInvokeExpr();
                        if (ie instanceof VirtualInvokeExpr ||
                            ie instanceof InterfaceInvokeExpr)
                        {
                            Iterator targetsIt = h.resolveAbstractDispatch
                                (((RefType)((InstanceInvokeExpr)ie).getBase().getType()).getSootClass(), 
                                ie.getMethod()).iterator();
                            g.addInvokeExpr(ie, m);
                            
                            while (targetsIt.hasNext())
                                g.addTarget(ie, (SootMethod)targetsIt.next());
                        }
                        else if (ie instanceof StaticInvokeExpr)
                        {
                            g.addInvokeExpr(ie, m);
                            g.addTarget(ie, ie.getMethod());
                        }
                        else if (ie instanceof SpecialInvokeExpr)
                        {
                            g.addInvokeExpr(ie, m);
                            g.addTarget(ie, h.resolveSpecialDispatch((SpecialInvokeExpr)ie, m));
                        }
                    }
                }
            }
        }

        return g;
    }
}
