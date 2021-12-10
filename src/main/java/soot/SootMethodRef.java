package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Ondrej Lhotak
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.List;

import soot.SootMethodRefImpl.ClassResolutionFailedException;
import soot.options.Options;
import soot.util.NumberedString;

/**
 * Representation of a reference to a method as it appears in a class file. Note that the method directly referred to may not
 * actually exist; the actual target of the reference is determined according to the resolution procedure in the Java Virtual
 * Machine Specification, 2nd ed, section 5.4.3.3.
 */
public interface SootMethodRef extends SootMethodInterface {

  /**
   * Use {@link #getDeclaringClass()} instead
   */
  public @Deprecated SootClass declaringClass();

  /**
   * Use {@link #getName()} instead
   */
  public @Deprecated String name();

  /**
   * Use {@link #getParameterTypes()} instead
   */
  public @Deprecated List<Type> parameterTypes();

  /**
   * Use {@link #getReturnType()} instead
   */
  public @Deprecated Type returnType();

  /**
   * Use {@link #getParameterType(int)} instead
   */
  public @Deprecated Type parameterType(int i);

  /**
   * Gets whether this method reference points to a constructor
   * 
   * @return True if this reference points to a constructor, false otherwise
   */
  public default boolean isConstructor() {
    return getReturnType() == VoidType.v() && "<init>".equals(getName());
  }

  public NumberedString getSubSignature();

  /**
   * Resolves this method call, i.e., finds the method to which this reference points. This method does not handle virtual
   * dispatch, it just gives the immediate target, which can also be an abstract method.
   *
   * @return The immediate target if this method reference
   * @throws ClassResolutionFailedException
   *           (can be suppressed by {@link Options#set_ignore_resolution_errors(boolean)})
   */
  public SootMethod resolve();

  /**
   * Tries to resolve this method call, i.e., tries to finds the method to which this reference points. This method does not
   * handle virtual dispatch, it just gives the immediate target, which can also be an abstract method. This method is
   * different from {@link #resolve()} in the following ways:
   *
   * (1) This method does not fail when the target method does not exist and phantom references are not allowed. In that
   * case, it returns <code>null</code>. (2) While {@link #resolve()} creates fake methods that throw exceptions when a
   * target method does not exist and phantom references are allowed, this method returns <code>null</code>.
   *
   * @return The immediate target if this method reference if available, <code>null</code> otherwise
   */
  public SootMethod tryResolve();

}
