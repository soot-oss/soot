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

import soot.tagkit.AnnotationTag;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;

/**
 * Special class to treat the methods with polymorphic signatures in the classes {@link java.lang.invoke.MethodHandle} and
 * {@link java.lang.invoke.VarHandle}. As described in
 * https://docs.oracle.com/javase/specs/jls/se11/html/jls-15.html#jls-15.12.4.4 method references to the methods with a
 * polymorphic signature, which are the methods in `java.lang.invoke.MethodHandle` and `java.lang.invoke.VarHandle`, require
 * special treatment
 * 
 * @author Andreas Dann created on 06.02.19
 * @author Manuel Benz 27.2.19
 */
public class PolymorphicMethodRef extends SootMethodRefImpl {

  public static final String METHODHANDLE_SIGNATURE = "java.lang.invoke.MethodHandle";

  public static final String VARHANDLE_SIGNATURE = "java.lang.invoke.VarHandle";

  public static final String POLYMORPHIC_SIGNATURE = "java/lang/invoke/MethodHandle$PolymorphicSignature";

  /**
   * Check if the declaring class "has the rights" to declare polymorphic methods
   * {@see http://docs.oracle.com/javase/specs/jls/se11/html/jls-15.html#jls-15.12.4.4}
   * 
   * @param declaringClass
   *          the class to check
   * @return if the class is allowed according to the JVM Spec
   */
  public static boolean handlesClass(SootClass declaringClass) {
    return handlesClass(declaringClass.getName());
  }

  public static boolean handlesClass(String declaringClassName) {
    return PolymorphicMethodRef.METHODHANDLE_SIGNATURE.equals(declaringClassName)
        || PolymorphicMethodRef.VARHANDLE_SIGNATURE.equals(declaringClassName);
  }

  /**
   * Constructor.
   *
   * @param declaringClass
   *          the declaring class. Must not be {@code null}
   * @param name
   *          the method name. Must not be {@code null}
   * @param parameterTypes
   *          the types of parameters. May be {@code null}
   * @param returnType
   *          the type of return value. Must not be {@code null}
   * @param isStatic
   *          the static modifier value
   * @throws IllegalArgumentException
   *           is thrown when {@code declaringClass}, or {@code name}, or {@code returnType} is null
   */
  public PolymorphicMethodRef(SootClass declaringClass, String name, List<Type> parameterTypes, Type returnType,
      boolean isStatic) {
    super(declaringClass, name, parameterTypes, returnType, isStatic);
  }

  @Override
  public SootMethod resolve() {
    SootMethod method = getDeclaringClass().getMethodUnsafe(getName(), getParameterTypes(), getReturnType());
    if (method != null) {
      return method;
    }

    // No method with matching parameter types or return types found for polymorphic methods,
    // we don't care about the return or parameter types. We just check if a method with the
    // name exists and has a polymorphic type signature.

    // Note(MB): We cannot use getMethodByName here since the method name is ambiguous after
    // adding the first method with same name and refined signature.
    for (SootMethod candidateMethod : getDeclaringClass().getMethods()) {
      if (candidateMethod.getName().equals(getName())) {
        Tag annotationsTag = candidateMethod.getTag(VisibilityAnnotationTag.NAME);
        if (annotationsTag != null) {
          for (AnnotationTag annotation : ((VisibilityAnnotationTag) annotationsTag).getAnnotations()) {
            // check the annotation's type
            if (('L' + POLYMORPHIC_SIGNATURE + ';').equals(annotation.getType())) {
              // The method is polymorphic, add a fitting method to the MethodHandle
              // or VarHandle class, as the JVM does on runtime.
              return addPolyMorphicMethod(candidateMethod);
            }
          }
        }
      }
    }

    return super.resolve();
  }

  private SootMethod addPolyMorphicMethod(SootMethod originalPolyMorphicMethod) {
    SootMethod newMethod
        = new SootMethod(getName(), getParameterTypes(), getReturnType(), originalPolyMorphicMethod.modifiers);
    getDeclaringClass().addMethod(newMethod);
    return newMethod;
  }
}
