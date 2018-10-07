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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.javaToJimple.LocalGenerator;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NewExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StringConstant;
import soot.options.Options;
import soot.util.NumberedString;

/**
 * Representation of a reference to a method as it appears in a class file. Note that the method directly referred to may not
 * actually exist; the actual target of the reference is determined according to the resolution procedure in the Java Virtual
 * Machine Specification, 2nd ed, section 5.4.3.3.
 */

public class SootMethodRefImpl implements SootMethodRef {
  private static final Logger logger = LoggerFactory.getLogger(SootMethodRefImpl.class);

  public SootMethodRefImpl(SootClass declaringClass, String name, List<Type> parameterTypes, Type returnType,
      boolean isStatic) {
    this.declaringClass = declaringClass;
    this.name = name;

    if (parameterTypes == null) {
      this.parameterTypes = null;
    } else {
      List<Type> l = new ArrayList<Type>();
      l.addAll(parameterTypes);
      this.parameterTypes = Collections.unmodifiableList(l);
    }

    this.returnType = returnType;
    this.isStatic = isStatic;
    if (declaringClass == null) {
      throw new RuntimeException("Attempt to create SootMethodRef with null class");
    }
    if (name == null) {
      throw new RuntimeException("Attempt to create SootMethodRef with null name");
    }
    if (returnType == null) {
      throw new RuntimeException("Attempt to create SootMethodRef with null returnType");
    }
  }

  private final SootClass declaringClass;
  private final String name;
  protected List<Type> parameterTypes;
  private final Type returnType;
  private final boolean isStatic;

  private NumberedString subsig;

  @Override
  public SootClass declaringClass() {
    return declaringClass;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public List<Type> parameterTypes() {
    return parameterTypes == null ? Collections.<Type>emptyList() : parameterTypes;
  }

  @Override
  public Type returnType() {
    return returnType;
  }

  @Override
  public boolean isStatic() {
    return isStatic;
  }

  @Override
  public NumberedString getSubSignature() {
    if (subsig == null) {
      subsig = Scene.v().getSubSigNumberer().findOrAdd(SootMethod.getSubSignature(name, parameterTypes, returnType));
    }
    return subsig;
  }

  @Override
  public String getSignature() {
    return SootMethod.getSignature(declaringClass, name, parameterTypes, returnType);
  }

  @Override
  public Type parameterType(int i) {
    return parameterTypes.get(i);
  }

  public class ClassResolutionFailedException extends ResolutionFailedException {
    /**
     *
     */
    private static final long serialVersionUID = 5430199603403917938L;

    public ClassResolutionFailedException() {
      super("Class " + declaringClass + " doesn't have method " + name + "(" + (parameterTypes == null ? "" : parameterTypes)
          + ")" + " : " + returnType + "; failed to resolve in superclasses and interfaces");
    }

    @Override
    public String toString() {
      StringBuffer ret = new StringBuffer();
      ret.append(super.toString());
      resolve(ret);
      return ret.toString();
    }
  }

  @Override
  public SootMethod resolve() {
    return resolve(null);
  }

  @Override
  public SootMethod tryResolve() {
    return tryResolve(null);
  }

  private SootMethod checkStatic(SootMethod ret) {
    if ((Options.v().wrong_staticness() == Options.wrong_staticness_fail
          || Options.v().wrong_staticness() == Options.wrong_staticness_fixstrict)
          && ret.isStatic() != isStatic() && !ret.isPhantom()) {
      throw new ResolutionFailedException("Resolved " + this + " to " + ret + " which has wrong static-ness");
    }
    return ret;
  }

  private SootMethod tryResolve(StringBuffer trace) {
    SootClass cl = declaringClass;
    while (cl != null) {
      if (trace != null) {
        trace.append("Looking in " + cl + " which has methods " + cl.getMethods() + "\n");
      }
      SootMethod sm = cl.getMethodUnsafe(getSubSignature());
      if (sm != null) {
        return checkStatic(sm);
      }
      if (Scene.v().allowsPhantomRefs() && cl.isPhantom()) {
        SootMethod m = Scene.v().makeSootMethod(name, parameterTypes, returnType, isStatic() ? Modifier.STATIC : 0);
        m.setPhantom(true);
        m = cl.getOrAddMethod(m);
        return checkStatic(m);
      }
      cl = cl.getSuperclassUnsafe();
    }

    cl = declaringClass;
    while (cl != null) {
      ArrayDeque<SootClass> queue = new ArrayDeque<SootClass>();
      queue.addAll(cl.getInterfaces());
      while (!queue.isEmpty()) {
        SootClass iface = queue.poll();
        if (trace != null) {
          trace.append("Looking in " + iface + " which has methods " + iface.getMethods() + "\n");
        }
        SootMethod sm = iface.getMethodUnsafe(getSubSignature());
        if (sm != null) {
          return checkStatic(sm);
        }
        queue.addAll(iface.getInterfaces());
      }
      cl = cl.getSuperclassUnsafe();
    }

    // If we don't have a method yet, we try to fix it on the fly
    if (cl == null && Scene.v().allowsPhantomRefs() && Options.v().ignore_resolution_errors()) {
      SootMethod m = Scene.v().makeSootMethod(name, parameterTypes, returnType, isStatic() ? Modifier.STATIC : 0);
      m.setPhantom(true);
      m = declaringClass.getOrAddMethod(m);
      return checkStatic(m);
    }

    return null;
  }

  private SootMethod resolve(StringBuffer trace) {
    SootMethod resolved = tryResolve(trace);
    if (resolved != null) {
      return resolved;
    }

    // when allowing phantom refs we also allow for references to
    // non-existing methods;
    // we simply create the methods on the fly; the method body will throw
    // an appropriate error just in case the code *is* actually reached at runtime
    boolean treatAsPhantomClass = Options.v().allow_phantom_refs() && !declaringClass.isInterface();

    // declaring class of dynamic invocations not known at compile time, treat as 
    // phantom class regardless if phantom classes are enabled
    treatAsPhantomClass = treatAsPhantomClass || declaringClass.getName().equals(SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME);

    if (treatAsPhantomClass) {
      return createUnresolvedErrorMethod(declaringClass);
    }

    if (trace == null) {
      ClassResolutionFailedException e = new ClassResolutionFailedException();
      if (Options.v().ignore_resolution_errors()) {
        logger.debug("" + e.getMessage());
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
    SootMethod m = Scene.v().makeSootMethod(name, parameterTypes, returnType, isStatic() ? Modifier.STATIC : 0);
    int modifiers = Modifier.PUBLIC; // we don't know who will be calling us
    if (isStatic()) {
      modifiers |= Modifier.STATIC;
    }
    m.setModifiers(modifiers);
    JimpleBody body = Jimple.v().newBody(m);
    m.setActiveBody(body);

    final LocalGenerator lg = new LocalGenerator(body);

    // For producing valid Jimple code, we need to access all parameters.
    // Otherwise, methods like "getThisLocal()" will fail.
    body.insertIdentityStmts(declaringClass);

    // exc = new Error
    RefType runtimeExceptionType = RefType.v("java.lang.Error");
    NewExpr newExpr = Jimple.v().newNewExpr(runtimeExceptionType);
    Local exceptionLocal = lg.generateLocal(runtimeExceptionType);
    AssignStmt assignStmt = Jimple.v().newAssignStmt(exceptionLocal, newExpr);
    body.getUnits().add(assignStmt);

    // exc.<init>(message)
    SootMethodRef cref = Scene.v().makeConstructorRef(runtimeExceptionType.getSootClass(),
        Collections.<Type>singletonList(RefType.v("java.lang.String")));
    SpecialInvokeExpr constructorInvokeExpr = Jimple.v().newSpecialInvokeExpr(exceptionLocal, cref,
        StringConstant.v("Unresolved compilation error: Method " + getSignature() + " does not exist!"));
    InvokeStmt initStmt = Jimple.v().newInvokeStmt(constructorInvokeExpr);
    body.getUnits().insertAfter(initStmt, assignStmt);

    // throw exc
    body.getUnits().insertAfter(Jimple.v().newThrowStmt(exceptionLocal), initStmt);

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
    result = prime * result + ((subsig == null) ? 0 : subsig.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SootMethodRefImpl other = (SootMethodRefImpl) obj;
    if (declaringClass == null) {
      if (other.declaringClass != null) {
        return false;
      }
    } else if (!declaringClass.equals(other.declaringClass)) {
      return false;
    }
    if (isStatic != other.isStatic) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (parameterTypes == null) {
      if (other.parameterTypes != null) {
        return false;
      }
    } else if (!parameterTypes.equals(other.parameterTypes)) {
      return false;
    }
    if (returnType == null) {
      if (other.returnType != null) {
        return false;
      }
    } else if (!returnType.equals(other.returnType)) {
      return false;
    }
    if (subsig == null) {
      if (other.subsig != null) {
        return false;
      }
    } else if (!subsig.equals(other.subsig)) {
      return false;
    }
    return true;
  }

}
