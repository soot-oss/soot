package soot.jimple.toolkits.invoke;

import soot.*;
import soot.jimple.*;

public class AccessManager
{
    /** Returns true iff target is legally accessible from container.
     *  Illegal access occurs when any of the following cases holds:
     *    1. container is private, but container.declaringClass() != 
     *              container.declaringClass(); or,
     *    2. container is package-visible, and its package differs from
     *              that of container; or,
     *    3. container is protected, and either:
     *          a. container doesn't belong to container.declaringClass,
     *                 or any superclass of container; 
     */
    public static boolean isAccessLegal(SootMethod container, ClassMember target)
    {
        SootClass targetClass = target.getDeclaringClass();
        SootClass containerClass = container.getDeclaringClass();

        if (!isAccessLegal(container, targetClass))
            return false;

        // Condition 1 above.
        if (target.isPrivate() && 
                !targetClass.getName().equals(containerClass.getName()))
            return false;

        // Condition 2. Check the package names.
        if (!target.isPrivate() && !target.isProtected() 
            && !target.isPublic())
        {
            if (!targetClass.getPackageName().equals
                     (containerClass.getPackageName()))
                return false;
        }

        // Condition 3.  
        if (target.isProtected())
        {
            Hierarchy h = Scene.v().getActiveHierarchy();

            // protected means that you can be accessed by your children.
            // i.e. container must be in a child of target.
            if (h.isClassSuperclassOfIncluding(targetClass, containerClass))
                return true;

            return false;
        }

        return true;        
    }

    public static boolean isAccessLegal(SootMethod container, SootClass target)
    {
        return target.isPublic() || 
            container.getDeclaringClass().getPackageName().equals(target.getPackageName());
    }

    public static boolean ensureAccess(SootMethod container, ClassMember target, String options)
    {
        boolean allowChanges = !(options.equals("none"));
        boolean safeChangesOnly = !(options.equals("unsafe"));

        if (safeChangesOnly)
            throw new RuntimeException("not implemented yet!");

        SootClass targetClass = target.getDeclaringClass();
        if (!ensureAccess(container, targetClass, options))
            return false;

        if (isAccessLegal(container, target))
            return true;

        if (!allowChanges)
            return false;

        if (target.getDeclaringClass().isApplicationClass())
        {
            target.setModifiers(target.getModifiers() | Modifier.PUBLIC);
            return true;
        }
        else
            return false;
    }

    public static boolean ensureAccess(SootMethod container, SootClass target, String options)
    {
        boolean allowChanges = !(options.equals("none"));
        boolean safeChangesOnly = !(options.equals("unsafe"));

        if (safeChangesOnly)
            throw new RuntimeException("not implemented yet!");

        if (isAccessLegal(container, target))
            return true;

        if (!allowChanges)
            return false;

        if (target.isApplicationClass())
        {
            target.setModifiers(target.getModifiers() | Modifier.PUBLIC);
            return true;
        }
        else
            return false;
    }

}
