package soot.jimple.toolkits.invoke;

import soot.*;
import soot.jimple.*;

public class InlinerSafetyManager
{
    /** Checks the safety criteria enumerated in section 3.1.4 
     * (Safety Criteria for Method Inlining) of Vijay's thesis. */
    public static boolean canSafelyInlineInto(SootMethod inlinee,
                                              Stmt toInline,
                                              SootMethod container,
                                              InvokeGraph g)
    {
        /* first, check the simple (one-line) safety criteria. */

        // Rule 0: Don't inline constructors.
        if (inlinee.getName().equals("<init>"))
            return false;

        // Rule 2: inlinee != container.
        if (inlinee.getSignature().equals(container.getSignature()))
            return false;

        // Rule 3: inlinee is neither native nor abstract.
        if (inlinee.isNative() || inlinee.isAbstract())
            return false;

        // Ok, that wraps up the simple criteria.  Now for the more
        // complicated criteria.

        // Rule 4: Don't inline away IllegalAccessErrors of the original
        //         source code (e.g. by moving a call to a private method
        //         *from* a bad class *to* a good class) occuring in the
        //         toInline statement.
        // Does not occur for static methods, because there is no base?

        InvokeExpr ie = (InvokeExpr)toInline.getInvokeExpr();
        Value base = (ie instanceof InstanceInvokeExpr) ? 
            ((InstanceInvokeExpr)ie).getBase() : null;

        if (base != null && invokeThrowsAccessErrorIn(((RefType)base.getType()).getSootClass(), inlinee, container))
            return false;

        // Rule 5: Don't inline away any class, method or field access 
        //         (in inlinee) resulting in an IllegalAccess error.

        // Rule 6: Don't introduce a spurious IllegalAccessError from
        //         inlining (by twiddling modifiers).

        // This is better handled by a pre-phase Scene transformation.
        // Inliner Safety should just report the absence of such 
        // IllegalAccessErrors after the transformation (and, conversely,
        // their presence without the twiddling.)

        // Rule 7: Don't change semantics of program by moving 
        //         an invokespecial.
        if (ie instanceof SpecialInvokeExpr && 
                (specialInvokePerformsLookupIn(ie, inlinee.getDeclaringClass()) ||
                specialInvokePerformsLookupIn(ie, container.getDeclaringClass())))
            return false;

        return true;
    }

    /** Returns true if any of the following cases holds:
     *    1. inlinee is private, but container.declaringClass() != 
     *              inlinee.declaringClass(); or,
     *    2. inlinee is package-visible, and its package differs from
     *              that of container; or,
     *    3. inlinee is protected, and either:
     *          a. inlinee doesn't belong to container.declaringClass,
     *                 or any superclass of container; 
     *          b. the class of the base is not a (non-strict) subclass
     *                 of container's declaringClass. 
     *   The base class may be null, in which case 3b is omitted. 
     *     (for instance, for a static method invocation.) */
    private static boolean invokeThrowsAccessErrorIn(SootClass base,
                                                     SootMethod inlinee,
                                                     SootMethod container)
    {
        SootClass inlineeClass = inlinee.getDeclaringClass();
        SootClass containerClass = container.getDeclaringClass();

        // Condition 1 above.
        if (inlinee.isPrivate() && 
                !inlineeClass.getName().equals(containerClass.getName()))
            return true;

        // Condition 2. Check the package names.
        if (!inlinee.isPrivate() && !inlinee.isProtected() 
            && !inlinee.isPublic())
        {
            if (!inlineeClass.getPackageName().equals
                     (containerClass.getPackageName()))
                return true;
        }

        // Condition 3.  
        if (inlinee.isProtected())
        {
            Hierarchy h = Scene.v().getActiveHierarchy();
            boolean saved = false;

            // protected means that you can be accessed by your children.
            // i.e. container must be in a child of inlinee.
            if (h.isClassSuperclassOfIncluding(inlineeClass, containerClass) ||
                ((base != null) && 
                 h.isClassSuperclassOfIncluding(base, containerClass)))
                saved = true;

            if (!saved)
                return true;
        }

        return false;
    }

    // m is the method being called; container is the class from which m
    // is being called.
    static boolean specialInvokePerformsLookupIn
        (InvokeExpr ie, SootClass containerClass)
    {
        // If all of the conditions are true, a lookup is performed.
        SootMethod m = ie.getMethod();


        if (m.getName().equals("<init>"))
        {
            return false;
        }

        if (m.isPrivate())
        {
            return false;
        }

        Hierarchy h = Scene.v().getActiveHierarchy();

        System.out.println(h.isClassSuperclassOf(m.getDeclaringClass(), 
                                   containerClass));

        if (!h.isClassSuperclassOf(m.getDeclaringClass(), 
                                   containerClass))
            return false;

        // ACC_SUPER must always be set, eh?

        return true;
    }
}
