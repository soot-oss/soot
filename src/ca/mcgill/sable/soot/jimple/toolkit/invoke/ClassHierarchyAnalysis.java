package ca.mcgill.sable.soot.jimple.toolkit.invoke;

import java.util.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;

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
            h = new Hierarchy(Scene.v());
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
                    }
                }
            }
        }

        return g;
    }
}
