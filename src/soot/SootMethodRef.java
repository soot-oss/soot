/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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

package soot;
import java.util.*;
import soot.util.*;

/** Representation of a reference to a method as it appears in a class file.
 * Note that the method directly referred to may not actually exist; the
 * actual target of the reference is determined according to the resolution
 * procedure in the Java Virtual Machine Specification, 2nd ed, section 5.4.3.3.
 */

public interface SootMethodRef {
    public SootClass declaringClass();
    public String name();
    public List<Type> parameterTypes();
    public Type returnType();
    public boolean isStatic();

    public NumberedString getSubSignature();

    public String getSignature();

    public Type parameterType(int i);

    /**
     * Resolves this method call, i.e., finds the method to which this reference
     * points. This method does not handle virtual dispatch, it just gives the
     * immediate target, which can also be an abstract method.
     * @return The immediate target if this method reference
     */
    public SootMethod resolve();
    
    /**
     * Tries to resolve this method call, i.e., tries to finds the method to
     * which this reference points. This method does not handle virtual dispatch,
     * it just gives the immediate target, which can also be an abstract method.
     * This method is different from resolve() in the following ways:
     * 
     * (1) This method does not fail when the target method does not exist and
     * phantom references are not allowed. In that case, it returns null.
     * (2) While resolve() creates fake methods that throw exceptions when a target
     * method does not exist and phantom references are allowed, this method
     * returns null.
     * 
     * @return The immediate target if this method reference if available, null
     * otherwise
     */
    public SootMethod tryResolve();
    
}
