package ca.mcgill.sable.soot.jimple.toolkit.invoke;

import java.util.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;

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
    public List getInvokeExprsIn(SootMethod container) 
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
        return (List)invokeExprToTargetMethods.get(ie);
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

    public void addInvokeExpr(InvokeExpr ie, SootMethod container) 
    {
        invokeExprToDeclaringMethod.put(ie, container);
        invokeExprToTargetMethods.put(ie, new ArrayList());

        List l = (List)methodToInvokeExprs.get(container);
        if (l == null) 
            l = new ArrayList();
        l.add(ie);

        methodToInvokeExprs.put(container, l);
    }

    public void removeInvokeExpr(InvokeExpr ie) 
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
    public void imitateInvokeExpr(InvokeExpr imitator, InvokeExpr roleModel)
    {
        Iterator it = getTargetsOf(roleModel).iterator();
        while (it.hasNext())
            addTarget(imitator, (SootMethod)it.next());
    }
}
