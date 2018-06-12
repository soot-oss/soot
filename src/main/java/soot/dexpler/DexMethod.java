package soot.dexpler;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 *
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
import java.util.List;

import org.jf.dexlib2.iface.Annotation;
import org.jf.dexlib2.iface.AnnotationElement;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.value.ArrayEncodedValue;
import org.jf.dexlib2.iface.value.EncodedValue;
import org.jf.dexlib2.iface.value.TypeEncodedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.MethodSource;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootResolver;
import soot.Type;
import soot.jimple.Jimple;
import soot.jimple.toolkits.typing.TypeAssigner;
import soot.options.Options;

/**
 * DexMethod is a container for all methods that are declared in a class. It holds information about its name, the class it
 * belongs to, its access flags, thrown exceptions, the return type and parameter types as well as the encoded method itself.
 *
 */
public class DexMethod {
  private static final Logger logger = LoggerFactory.getLogger(DexMethod.class);

  protected final DexFile dexFile;
  protected final SootClass declaringClass;

  public DexMethod(final DexFile dexFile, final SootClass declaringClass) {
    this.dexFile = dexFile;
    this.declaringClass = declaringClass;
  }

  /**
   * Retrieve the SootMethod equivalent of this method
   *
   * @return the SootMethod of this method
   */
  public SootMethod makeSootMethod(final Method method) {
    int accessFlags = method.getAccessFlags();

    // get the name of the method
    String name = method.getName();

    List<SootClass> thrownExceptions = getThrownExceptions(method);
    List<Type> parameterTypes = getParameterTypes(method);

    // retrieve the return type of this method
    Type returnType = DexType.toSoot(method.getReturnType());

    // Build soot method by all available parameters
    SootMethod sm = declaringClass.getMethodUnsafe(name, parameterTypes, returnType);
    if (sm == null) {
      sm = Scene.v().makeSootMethod(name, parameterTypes, returnType, accessFlags, thrownExceptions);
    }

    // if the method is abstract or native, no code needs to be transformed
    int flags = method.getAccessFlags();
    if (Modifier.isAbstract(flags) || Modifier.isNative(flags)) {
      return sm;
    }

    if (Options.v().oaat() && declaringClass.resolvingLevel() <= SootClass.SIGNATURES) {
      return sm;
    }

    // sets the method source by adding its body as the active body
    sm.setSource(createMethodSource(method));

    return sm;
  }

  protected MethodSource createMethodSource(final Method method) {
    return new MethodSource() {

      @Override
      public Body getBody(SootMethod m, String phaseName) {
        Body b = Jimple.v().newBody(m);
        try {
          // add the body of this code item
          DexBody dexBody = new DexBody(dexFile, method, declaringClass.getType());
          dexBody.jimplify(b, m);
        } catch (InvalidDalvikBytecodeException e) {
          String msg = "Warning: Invalid bytecode in method " + m + ": " + e;
          logger.debug("" + msg);
          Util.emptyBody(b);
          Util.addExceptionAfterUnit(b, "java.lang.RuntimeException", b.getUnits().getLast(),
              "Soot has detected that this method contains invalid Dalvik bytecode,"
                  + " which would have throw an exception at runtime. [" + msg + "]");
          TypeAssigner.v().transform(b);
        }
        m.setActiveBody(b);

        return m.getActiveBody();
      }
    };
  }

  protected List<Type> getParameterTypes(final Method method) {
    // retrieve all parameter types
    List<Type> parameterTypes = new ArrayList<Type>();
    if (method.getParameters() != null) {
      List<? extends CharSequence> parameters = method.getParameterTypes();

      for (CharSequence t : parameters) {
        Type type = DexType.toSoot(t.toString());
        parameterTypes.add(type);
      }
    }
    return parameterTypes;
  }

  protected List<SootClass> getThrownExceptions(final Method method) {
    // the following snippet retrieves all exceptions that this method
    // throws by analyzing its annotations
    List<SootClass> thrownExceptions = new ArrayList<SootClass>();
    for (Annotation a : method.getAnnotations()) {
      Type atype = DexType.toSoot(a.getType());
      String atypes = atype.toString();
      if (!(atypes.equals("dalvik.annotation.Throws"))) {
        continue;
      }
      for (AnnotationElement ae : a.getElements()) {
        EncodedValue ev = ae.getValue();
        if (ev instanceof ArrayEncodedValue) {
          for (EncodedValue evSub : ((ArrayEncodedValue) ev).getValue()) {
            if (evSub instanceof TypeEncodedValue) {
              TypeEncodedValue valueType = (TypeEncodedValue) evSub;
              String exceptionName = valueType.getValue();
              String dottedName = Util.dottedClassName(exceptionName);
              thrownExceptions.add(SootResolver.v().makeClassRef(dottedName));
            }
          }
        }
      }
    }
    return thrownExceptions;
  }
}
