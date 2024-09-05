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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.dotnet.types.DotNetBasicTypes;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StringConstant;
import soot.options.Options;
import soot.util.NumberedString;

/**
 * Representation of a reference to a method as it appears in a class file. Note that the method directly referred to may not
 * actually exist; the actual target of the reference is determined according to the resolution procedure in the Java Virtual
 * Machine Specification, 2nd ed, section 5.4.3.3.
 *
 * @author Manuel Benz 22.10.19 - Delegate method resolution behavior to FastHierarchy to have one common place for extension
 */
public class SootMethodRefImpl implements SootMethodRef {

  private static final Logger logger = LoggerFactory.getLogger(SootMethodRefImpl.class);

  private final SootClass declaringClass;
  private final String name;
  private final List<Type> parameterTypes;
  private final Type returnType;
  private final boolean isStatic;

  private SootMethod resolveCache = null;

  private NumberedString subsig;

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
  public SootMethodRefImpl(SootClass declaringClass, String name, List<Type> parameterTypes, Type returnType,
      boolean isStatic) {
    if (declaringClass == null) {
      throw new IllegalArgumentException("Attempt to create SootMethodRef with null class");
    }
    if (name == null) {
      throw new IllegalArgumentException("Attempt to create SootMethodRef with null name");
    }
    if (returnType == null) {
      throw new IllegalArgumentException("Attempt to create SootMethodRef with null returnType (Method: " + name
          + " at declaring class: " + declaringClass.getName() + ")");
    }

    this.declaringClass = declaringClass;
    this.name = name;
    this.parameterTypes = createParameterTypeList(parameterTypes);
    this.returnType = returnType;
    this.isStatic = isStatic;
  }

  // Not everbody likes creating a copy of the parameter type list. If you're careful, you can
  // safe precious CPU cycles and a bit of memory.
  protected List<Type> createParameterTypeList(List<Type> parameterTypes) {
    // initialize with unmodifiable collection
    if (parameterTypes == null) {
      return Collections.emptyList();
    } else {
      return Collections.unmodifiableList(new ArrayList<>(parameterTypes));
    }
  }

  @Override
  public SootClass declaringClass() {
    return getDeclaringClass();
  }

  @Override
  public SootClass getDeclaringClass() {
    return declaringClass;
  }

  @Override
  public String name() {
    return getName();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<Type> parameterTypes() {
    return getParameterTypes();
  }

  @Override
  public List<Type> getParameterTypes() {
    return parameterTypes;
  }

  @Override
  public Type returnType() {
    return getReturnType();
  }

  @Override
  public Type getReturnType() {
    return returnType;
  }

  @Override
  public boolean isStatic() {
    return isStatic;
  }

  @Override
  public NumberedString getSubSignature() {
    if (subsig != null) {
      return subsig;
    }
    if (resolveCache != null) {
      subsig = resolveCache.getNumberedSubSignature();
      return subsig;
    }

    // This method is expensive, so it makes sense to cache the result
    subsig = Scene.v().getSubSigNumberer().findOrAdd(SootMethod.getSubSignature(name, parameterTypes, returnType));
    return subsig;
  }

  @Override
  public String getSignature() {
    return SootMethod.getSignature(declaringClass, name, parameterTypes, returnType);
  }

  @Override
  public Type parameterType(int i) {
    return getParameterType(i);
  }

  @Override
  public Type getParameterType(int i) {
    return parameterTypes.get(i);
  }

  public class ClassResolutionFailedException extends ResolutionFailedException {
    /** */
    private static final long serialVersionUID = 5430199603403917938L;

    public ClassResolutionFailedException() {
      super("Class " + declaringClass + " doesn't have method " + name + "(" + (parameterTypes == null ? "" : parameterTypes)
          + ")" + " : " + returnType + "; failed to resolve in superclasses and interfaces");
    }

    @Override
    public String toString() {
      final StringBuilder ret = new StringBuilder(super.toString());
      resolve(ret);
      return ret.toString();
    }
  }

  @Override
  public SootMethod resolve() {
    SootMethod cached = this.resolveCache;
    // Use the cached SootMethod if available and still valid
    if (cached == null || !cached.isValidResolve(this)) {
      cached = resolve(null);
      this.resolveCache = cached;
    }
    return cached;
  }

  @Override
  public SootMethod tryResolve() {
    return tryResolve(null);
  }

  private void checkStatic(SootMethod method) {
    final int opt = Options.v().wrong_staticness();
    if ((opt == Options.wrong_staticness_fail || opt == Options.wrong_staticness_fixstrict)
        && method.isStatic() != isStatic() && !method.isPhantom()) {
      throw new ResolutionFailedException("Resolved " + this + " to " + method + " which has wrong static-ness");
    }
  }

  protected SootMethod tryResolve(final StringBuilder trace) {
    // let's do a dispatch and allow abstract method for resolution
    // we do not have a base object for call so we just take the type of the declaring class
    SootMethod resolved = Scene.v().getOrMakeFastHierarchy().resolveMethod(declaringClass, declaringClass, name,
        parameterTypes, returnType, true, this.getSubSignature());

    if (resolved != null) {
      checkStatic(resolved);
      return resolved;
    } else if (Scene.v().allowsPhantomRefs()) {
      // Try to resolve in the current class and the interface,
      // if not found check for phantom class in the superclass.
      for (SootClass selectedClass = declaringClass; selectedClass != null;) {
        if (selectedClass.isPhantom()) {
          SootMethod phantomMethod
              = Scene.v().makeSootMethod(name, parameterTypes, returnType, isStatic() ? Modifier.STATIC : 0);
          phantomMethod.setPhantom(true);
          phantomMethod = selectedClass.getOrAddMethod(phantomMethod);
          checkStatic(phantomMethod);
          return phantomMethod;
        }
        selectedClass = selectedClass.getSuperclassUnsafe();
      }
    }

    // If we don't have a method yet, we try to fix it on the fly
    if (Scene.v().allowsPhantomRefs() && Options.v().ignore_resolution_errors()) {
      // if we have a phantom class in the hierarchy, we add the method to it.
      SootClass classToAddTo = declaringClass;
      while (classToAddTo != null && !classToAddTo.isPhantom()) {
        classToAddTo = classToAddTo.getSuperclassUnsafe();
      }

      // We just take the declared class, otherwise.
      if (classToAddTo == null) {
        classToAddTo = declaringClass;
      }

      SootMethod method = Scene.v().makeSootMethod(name, parameterTypes, returnType, isStatic() ? Modifier.STATIC : 0);
      method.setPhantom(true);
      method = classToAddTo.getOrAddMethod(method);
      checkStatic(method);
      return method;
    }

    return null;
  }

  private SootMethod resolve(final StringBuilder trace) {
    SootMethod resolved = tryResolve(trace);
    if (resolved != null) {
      return resolved;
    }

    // When allowing phantom refs we also allow for references to non-existing
    // methods. We simply create the methods on the fly. The method body will
    // throw an appropriate error just in case the code *is* actually reached
    // at runtime. Furthermore, the declaring class of dynamic invocations is
    // not known at compile time, treat as phantom class regardless if phantom
    // classes are enabled or not.
    if (Options.v().allow_phantom_refs() || SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME.equals(declaringClass.getName())) {
      return createUnresolvedErrorMethod(declaringClass);
    }

    if (trace == null) {
      ClassResolutionFailedException e = new ClassResolutionFailedException();
      if (Options.v().ignore_resolution_errors()) {
        logger.debug(e.getMessage());
      } else {
        throw e;
      }
    }

    return null;
  }

  /**
   * Creates a method body that throws an "unresolved compilation error" message
   *
   * @param declaringClass
   *          The class that was supposed to contain the method
   * @return The created SootMethod
   */
  private SootMethod createUnresolvedErrorMethod(SootClass declaringClass) {
    final Jimple jimp = Jimple.v();

    SootMethod m = Scene.v().makeSootMethod(name, parameterTypes, returnType, isStatic() ? Modifier.STATIC : 0);
    int modifiers = Modifier.PUBLIC; // we don't know who will be calling us
    if (isStatic()) {
      modifiers |= Modifier.STATIC;
    }
    m.setModifiers(modifiers);
    JimpleBody body = jimp.newBody(m);
    m.setActiveBody(body);

    final LocalGenerator lg = Scene.v().createLocalGenerator(body);

    // For producing valid Jimple code, we need to access all parameters.
    // Otherwise, methods like "getThisLocal()" will fail.
    body.insertIdentityStmts(declaringClass);

    // exc = new Error
    RefType runtimeExceptionType = RefType.v("java.lang.Error");
    if (Options.v().src_prec() == Options.src_prec_dotnet) {
      runtimeExceptionType = RefType.v(DotNetBasicTypes.SYSTEM_EXCEPTION);
    }
    Local exceptionLocal = lg.generateLocal(runtimeExceptionType);
    AssignStmt assignStmt = jimp.newAssignStmt(exceptionLocal, jimp.newNewExpr(runtimeExceptionType));
    body.getUnits().add(assignStmt);

    // exc.<init>(message)
    SootMethodRef cref = Scene.v().makeConstructorRef(runtimeExceptionType.getSootClass(),
        Collections.<Type>singletonList(RefType.v("java.lang.String")));
    if (Options.v().src_prec() == Options.src_prec_dotnet) {
      cref = Scene.v().makeConstructorRef(runtimeExceptionType.getSootClass(),
          Collections.<Type>singletonList(RefType.v(DotNetBasicTypes.SYSTEM_STRING)));
    }
    SpecialInvokeExpr constructorInvokeExpr = jimp.newSpecialInvokeExpr(exceptionLocal, cref,
        StringConstant.v("Unresolved compilation error: Method " + getSignature() + " does not exist!"));
    InvokeStmt initStmt = jimp.newInvokeStmt(constructorInvokeExpr);
    body.getUnits().insertAfter(initStmt, assignStmt);

    // throw exc
    body.getUnits().insertAfter(jimp.newThrowStmt(exceptionLocal), initStmt);

    return declaringClass.getOrAddMethod(m);
  }

  @Override
  public String toString() {
    return getSignature();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((declaringClass == null) ? 0 : declaringClass.hashCode());
    result = prime * result + (isStatic ? 1231 : 1237);
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((parameterTypes == null) ? 0 : parameterTypes.hashCode());
    result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    SootMethodRefImpl other = (SootMethodRefImpl) obj;
    if (this.isStatic != other.isStatic) {
      return false;
    }
    if (this.declaringClass == null) {
      if (other.declaringClass != null) {
        return false;
      }
    } else if (!this.declaringClass.equals(other.declaringClass)) {
      return false;
    }
    if (this.name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!this.name.equals(other.name)) {
      return false;
    }
    if (this.parameterTypes == null) {
      if (other.parameterTypes != null) {
        return false;
      }
    } else if (!this.parameterTypes.equals(other.parameterTypes)) {
      return false;
    }
    if (this.returnType == null) {
      if (other.returnType != null) {
        return false;
      }
    } else if (!this.returnType.equals(other.returnType)) {
      return false;
    }
    return true;
  }
}
