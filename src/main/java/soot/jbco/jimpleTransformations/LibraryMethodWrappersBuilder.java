package soot.jbco.jimpleTransformations;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FastHierarchy;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.Modifier;
import soot.PatchingChain;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.BodyBuilder;
import soot.jbco.util.Rand;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.util.Chain;

/**
 * Creates methods that "wraps" library method calls.
 *
 * @author Michael Batchelder
 *         <p>
 *         Created on 7-Feb-2006
 */
public class LibraryMethodWrappersBuilder extends SceneTransformer implements IJbcoTransform {

  private static final Logger logger = LoggerFactory.getLogger(LibraryMethodWrappersBuilder.class);

  public static final String name = "wjtp.jbco_blbc";
  public static final String dependencies[] = new String[] { "wjtp.jbco_blbc" };

  private static final Map<SootClass, Map<SootMethod, SootMethodRef>> libClassesToMethods = new HashMap<>();
  public static List<SootMethod> builtByMe = new ArrayList<>();

  private int newmethods = 0;
  private int methodcalls = 0;

  @Override
  public String[] getDependencies() {
    return Arrays.copyOf(dependencies, dependencies.length);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void outputSummary() {
    logger.info("Created {} new methods. Replaced {} method calls.", newmethods, methodcalls);
  }

  protected void internalTransform(String phaseName, Map<String, String> options) {
    if (isVerbose()) {
      logger.info("Building Library Wrapper Methods...");
    }

    BodyBuilder.retrieveAllBodies();
    // iterate through application classes to find library calls
    final Iterator<SootClass> applicationClassesIterator = Scene.v().getApplicationClasses().snapshotIterator();
    while (applicationClassesIterator.hasNext()) {
      final SootClass applicationClass = applicationClassesIterator.next();

      if (isVerbose()) {
        logger.info("\tProcessing class {}", applicationClass.getName());
      }

      // create local copy to prevent java.util.ConcurrentModificationException
      final List<SootMethod> methods = new ArrayList<>(applicationClass.getMethods());
      for (SootMethod method : methods) {
        if (!method.isConcrete() || builtByMe.contains(method)) {
          continue;
        }

        final Body body = getBodySafely(method);
        if (body == null) {
          continue;
        }

        int localName = 0;

        final Unit first = getFirstNotIdentityStmt(body);

        final Iterator<Unit> unitIterator = body.getUnits().snapshotIterator();
        while (unitIterator.hasNext()) {
          final Unit unit = unitIterator.next();

          for (ValueBox valueBox : unit.getUseBoxes()) {
            final Value value = valueBox.getValue();
            // skip calls to 'super' as they cannot be called from static method and/or on object from the
            // outside (this is prohibited on language level as that would violate encapsulation)
            if (!(value instanceof InvokeExpr) || value instanceof SpecialInvokeExpr) {
              continue;
            }

            final InvokeExpr invokeExpr = (InvokeExpr) value;
            final SootMethod invokedMethod = getMethodSafely(invokeExpr);
            if (invokedMethod == null) {
              continue;
            }

            SootMethodRef invokedMethodRef = getNewMethodRef(invokedMethod);
            if (invokedMethodRef == null) {
              invokedMethodRef = buildNewMethod(applicationClass, invokedMethod, invokeExpr);
              setNewMethodRef(invokedMethod, invokedMethodRef);
              newmethods++;
            }

            if (isVerbose()) {
              logger.info("\t\t\tChanging {} to {}\tUnit: ", invokedMethod.getSignature(), invokedMethodRef.getSignature(),
                  unit);
            }

            List<Value> args = invokeExpr.getArgs();
            List<Type> parameterTypes = invokedMethodRef.parameterTypes();
            int argsCount = args.size();
            int paramCount = parameterTypes.size();

            if (invokeExpr instanceof InstanceInvokeExpr || invokeExpr instanceof StaticInvokeExpr) {
              if (invokeExpr instanceof InstanceInvokeExpr) {
                argsCount++;
                args.add(((InstanceInvokeExpr) invokeExpr).getBase());
              }

              while (argsCount < paramCount) {
                Type pType = parameterTypes.get(argsCount);
                Local newLocal = Jimple.v().newLocal("newLocal" + localName++, pType);
                body.getLocals().add(newLocal);
                body.getUnits().insertBeforeNoRedirect(Jimple.v().newAssignStmt(newLocal, getConstantType(pType)), first);
                args.add(newLocal);
                argsCount++;
              }
              valueBox.setValue(Jimple.v().newStaticInvokeExpr(invokedMethodRef, args));
            }
            methodcalls++;
          }
        }
      }
    }

    Scene.v().releaseActiveHierarchy();
    Scene.v().setFastHierarchy(new FastHierarchy());
  }

  private SootMethodRef getNewMethodRef(SootMethod method) {
    Map<SootMethod, SootMethodRef> methods
        = libClassesToMethods.computeIfAbsent(method.getDeclaringClass(), key -> new HashMap<>());
    return methods.get(method);
  }

  private void setNewMethodRef(SootMethod sm, SootMethodRef smr) {
    Map<SootMethod, SootMethodRef> methods
        = libClassesToMethods.computeIfAbsent(sm.getDeclaringClass(), key -> new HashMap<>());
    methods.put(sm, smr);
  }

  private SootMethodRef buildNewMethod(SootClass fromC, SootMethod sm, InvokeExpr origIE) {
    final List<SootClass> availableClasses = getVisibleApplicationClasses(sm);

    final int classCount = availableClasses.size();
    if (classCount == 0) {
      throw new RuntimeException("There appears to be no public non-interface Application classes!");
    }

    SootClass randomClass;
    String methodNewName;
    do {
      int index = Rand.getInt(classCount);
      if ((randomClass = availableClasses.get(index)) == fromC && classCount > 1) {
        index = Rand.getInt(classCount);
        randomClass = availableClasses.get(index);
      }

      final List<SootMethod> methods = randomClass.getMethods();
      index = Rand.getInt(methods.size());
      final SootMethod randMethod = methods.get(index);
      methodNewName = randMethod.getName();
    } while (methodNewName.equals(SootMethod.constructorName) || methodNewName.equals(SootMethod.staticInitializerName));

    final List<Type> smParamTypes = new ArrayList<>(sm.getParameterTypes());
    if (!sm.isStatic()) {
      smParamTypes.add(sm.getDeclaringClass().getType());
    }

    // add random class params until we don't match any other method
    int extraParams = 0;
    if (randomClass.declaresMethod(methodNewName, smParamTypes)) {
      int rtmp = Rand.getInt(classCount + 7);
      if (rtmp >= classCount) {
        rtmp -= classCount;
        smParamTypes.add(getPrimType(rtmp));
      } else {
        smParamTypes.add(availableClasses.get(rtmp).getType());
      }
      extraParams++;
    }

    final int mods = ((((sm.getModifiers() | Modifier.STATIC | Modifier.PUBLIC) & (Modifier.ABSTRACT ^ 0xFFFF))
        & (Modifier.NATIVE ^ 0xFFFF)) & (Modifier.SYNCHRONIZED ^ 0xFFFF));
    SootMethod newMethod = Scene.v().makeSootMethod(methodNewName, smParamTypes, sm.getReturnType(), mods);
    randomClass.addMethod(newMethod);

    JimpleBody body = Jimple.v().newBody(newMethod);
    newMethod.setActiveBody(body);
    Chain<Local> locals = body.getLocals();
    PatchingChain<Unit> units = body.getUnits();

    List<Local> args = BodyBuilder.buildParameterLocals(units, locals, smParamTypes);
    while (extraParams-- > 0) {
      args.remove(args.size() - 1);
    }

    InvokeExpr ie = null;
    if (sm.isStatic()) {
      ie = Jimple.v().newStaticInvokeExpr(sm.makeRef(), args);
    } else {
      Local libObj = args.remove(args.size() - 1);
      if (origIE instanceof InterfaceInvokeExpr) {
        ie = Jimple.v().newInterfaceInvokeExpr(libObj, sm.makeRef(), args);
      } else if (origIE instanceof VirtualInvokeExpr) {
        ie = Jimple.v().newVirtualInvokeExpr(libObj, sm.makeRef(), args);
      }
    }
    if (sm.getReturnType() instanceof VoidType) {
      units.add(Jimple.v().newInvokeStmt(ie));
      units.add(Jimple.v().newReturnVoidStmt());
    } else {
      Local assign = Jimple.v().newLocal("returnValue", sm.getReturnType());
      locals.add(assign);
      units.add(Jimple.v().newAssignStmt(assign, ie));
      units.add(Jimple.v().newReturnStmt(assign));
    }

    if (isVerbose()) {
      logger.info("{} was replaced by {} which calls {}", sm.getName(), newMethod.getName(), ie);
    }

    if (units.size() < 2) {
      logger.warn("THERE AREN'T MANY UNITS IN THIS METHOD {}", units);
    }

    builtByMe.add(newMethod);

    return newMethod.makeRef();
  }

  private static Type getPrimType(int idx) {
    switch (idx) {
      case 0:
        return IntType.v();
      case 1:
        return CharType.v();
      case 2:
        return ByteType.v();
      case 3:
        return LongType.v();
      case 4:
        return BooleanType.v();
      case 5:
        return DoubleType.v();
      case 6:
        return FloatType.v();
      default:
        return IntType.v();
    }
  }

  private static Value getConstantType(Type t) {
    if (t instanceof BooleanType) {
      return IntConstant.v(Rand.getInt(1));
    }
    if (t instanceof IntType) {
      return IntConstant.v(Rand.getInt());
    }
    if (t instanceof CharType) {
      return Jimple.v().newCastExpr(IntConstant.v(Rand.getInt()), CharType.v());
    }
    if (t instanceof ByteType) {
      return Jimple.v().newCastExpr(IntConstant.v(Rand.getInt()), ByteType.v());
    }
    if (t instanceof LongType) {
      return LongConstant.v(Rand.getLong());
    }
    if (t instanceof FloatType) {
      return FloatConstant.v(Rand.getFloat());
    }
    if (t instanceof DoubleType) {
      return DoubleConstant.v(Rand.getDouble());
    }

    return Jimple.v().newCastExpr(NullConstant.v(), t);
  }

  private static Body getBodySafely(SootMethod method) {
    try {
      return method.getActiveBody();
    } catch (Exception exception) {
      logger.warn("Getting Body from SootMethod {} caused exception that was suppressed.", exception);

      return method.retrieveActiveBody();
    }
  }

  private static Unit getFirstNotIdentityStmt(Body body) {
    final Iterator<Unit> unitIterator = body.getUnits().snapshotIterator();
    while (unitIterator.hasNext()) {
      final Unit unit = unitIterator.next();
      if (unit instanceof IdentityStmt) {
        continue;
      }
      return unit;
    }

    logger.debug("There are no non-identity units in the method body.");
    return null;
  }

  private static SootMethod getMethodSafely(InvokeExpr invokeExpr) {
    try {
      final SootMethod invokedMethod = invokeExpr.getMethod();
      if (invokedMethod == null) {
        return null;
      }

      if (SootMethod.constructorName.equals(invokedMethod.getName())
          || SootMethod.staticInitializerName.equals(invokedMethod.getName())) {
        logger.debug("Skipping wrapping method {} as it is constructor/initializer.", invokedMethod);
        return null;
      }

      final SootClass invokedMethodClass = invokedMethod.getDeclaringClass();

      if (!invokedMethodClass.isLibraryClass()) {
        logger.debug("Skipping wrapping method {} as it is not library one.", invokedMethod);
        return null;
      }

      if (invokeExpr.getMethodRef().declaringClass().isInterface() && !invokedMethodClass.isInterface()) {
        logger.debug(
            "Skipping wrapping method {} as original code suppose to execute it on interface {}"
                + " but resolved code trying to execute it on class {}",
            invokedMethod, invokeExpr.getMethodRef().declaringClass(), invokedMethodClass);
        return null;
      }

      return invokedMethod;
    } catch (RuntimeException exception) {
      logger.debug("Cannot resolve method of InvokeExpr: " + invokeExpr.toString(), exception);
      return null;
    }
  }

  private static List<SootClass> getVisibleApplicationClasses(SootMethod visibleBy) {
    final List<SootClass> result = new ArrayList<>();

    final Iterator<SootClass> applicationClassesIterator = Scene.v().getApplicationClasses().snapshotIterator();
    while (applicationClassesIterator.hasNext()) {
      final SootClass applicationClass = applicationClassesIterator.next();

      if (applicationClass.isConcrete() && !applicationClass.isInterface() && applicationClass.isPublic()
          && Scene.v().getActiveHierarchy().isVisible(applicationClass, visibleBy)) {
        result.add(applicationClass);
      }
    }

    return result;
  }

}
