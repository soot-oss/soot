/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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

import soot.*;
import soot.jimple.*;
import java.util.*;

/** Methods for checking safety requirements for inlining. */
public class InlinerSafetyManager
{
	// true if safe to inline
	public static boolean checkSpecialInlineRestrictions(SootMethod container, SootMethod target, String options) {
	 // Check the body of the method to inline for specialinvoke's 
    
    	boolean accessors=options.equals("accessors");
    	
        Body inlineeBody = (JimpleBody) target.getActiveBody();
        
        Iterator unitsIt = inlineeBody.getUnits().iterator();
        while (unitsIt.hasNext())
        {
            Stmt st = (Stmt)unitsIt.next();
            if (st.containsInvokeExpr())
            {
                InvokeExpr ie1 = (InvokeExpr)st.getInvokeExpr();
                
                if (ie1 instanceof SpecialInvokeExpr) 
                {
                    if((InlinerSafetyManager.specialInvokePerformsLookupIn(ie1, container.getDeclaringClass()) ||
                      InlinerSafetyManager.specialInvokePerformsLookupIn(ie1, target.getDeclaringClass())))
                    {                    	
                        return false;
                        
                    }
                 
                    SootMethod specialTarget = ie1.getMethod();
                    
                    if(specialTarget.isPrivate())
                    {
                        if(specialTarget.getDeclaringClass() != container.getDeclaringClass())
                        {
                            // Do not inline a call which contains a specialinvoke call to a private method outside
                            // the current class.  This avoids a verifier error and we assume will not have a big
                            // impact because we are inlining methods bottom-up, so such a call will be rare
                        	  
                        	if (!accessors)
                        		return false;
                        }
                    }
                }
              }               
        }
    
        
        return true;
	}
	
	public static boolean checkAccessRestrictions(SootMethod container, SootMethod target, String modifierOptions) {
//		 Check the body of the method to inline for
        //   method or field access restrictions
        {
            Body inlineeBody = (JimpleBody) target.getActiveBody();
            
            Iterator unitsIt = inlineeBody.getUnits().iterator();
            while (unitsIt.hasNext())
            {
                Stmt st = (Stmt)unitsIt.next();
                if (st.containsInvokeExpr())
                {
                    InvokeExpr ie1 = (InvokeExpr)st.getInvokeExpr();                
                    
                    if (!AccessManager.ensureAccess(container, ie1.getMethod(), modifierOptions))
                        return false;
                  }

                if (st instanceof AssignStmt)
                {
                    Value lhs = ((AssignStmt)st).getLeftOp();
                    Value rhs = ((AssignStmt)st).getRightOp();

                    if (lhs instanceof FieldRef && 
                        !AccessManager.ensureAccess(container, ((FieldRef)lhs).getField(), 
                                                    modifierOptions))
                        return false;
                        
                                                                               
                    if (rhs instanceof FieldRef &&
                        !AccessManager.ensureAccess(container, ((FieldRef)rhs).getField(), 
                                                    modifierOptions))
                        return false;
                        
                }
            }
        }
        
        return true;
		 
	}
	
    /** Returns true if this method can be inlined at the given site.
        Will try as hard as it can to change things to allow
        inlining (modifierOptions controls what it's allowed to do:
        safe, unsafe and nochanges)
        
        Returns false otherwise.
    */
	
    public static boolean ensureInlinability(SootMethod target,
                                             Stmt toInline,
                                             SootMethod container, 
                                             String modifierOptions)
    {
        if(!InlinerSafetyManager.canSafelyInlineInto(target, toInline, container)) {
        	//System.out.println("canSafelyInlineInto failed");
            return false;
        }
    
        if(!AccessManager.ensureAccess(container, target, modifierOptions)) {
        	//System.out.println("ensure access failed");
            return false;
        }
            
        if (!checkSpecialInlineRestrictions(container, target, modifierOptions)) {
        	//System.out.println("checkSpecialInlineRestrictions failed");
        	return false;
        }
        
        if (!checkAccessRestrictions(container, target, modifierOptions)) {
        	//System.out.println("checkAccessRestrictions failed");
        	return false;
        }
        
        return true;
    }
    
    /** Checks the safety criteria enumerated in section 3.1.4 
     * (Safety Criteria for Method Inlining) of Vijay's thesis. */
    private static boolean canSafelyInlineInto(SootMethod inlinee,
                                              Stmt toInline,
                                              SootMethod container)

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

        if (base != null && base.getType() instanceof RefType &&
        		invokeThrowsAccessErrorIn(((RefType)base.getType()).getSootClass(), inlinee, container))
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

        if (!h.isClassSuperclassOf(m.getDeclaringClass(), 
                                   containerClass))
            return false;

        // ACC_SUPER must always be set, eh?

        return true;
    }
}
