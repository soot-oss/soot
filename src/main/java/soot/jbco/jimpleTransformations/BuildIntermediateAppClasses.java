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

import static soot.SootMethod.constructorName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import soot.Body;
import soot.FastHierarchy;
import soot.Hierarchy;
import soot.Local;
import soot.Modifier;
import soot.PatchingChain;
import soot.PrimType;
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
import soot.jbco.Main;
import soot.jbco.util.BodyBuilder;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NullConstant;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.ThisRef;
import soot.util.Chain;

/**
 * @author Michael Batchelder
 *         <p>
 *         Created on 1-Feb-2006
 *         <p>
 *         This class builds buffer classes between Application classes and their corresponding library superclasses. This
 *         allows for the hiding of all library method overrides to be hidden in a different class, thereby cloaking somewhat
 *         the mechanisms.
 */
public class BuildIntermediateAppClasses extends SceneTransformer implements IJbcoTransform {

  private static int newclasses = 0;
  private static int newmethods = 0;

  public void outputSummary() {
    out.println("New buffer classes created: " + newclasses);
    out.println("New buffer class methods created: " + newmethods);
  }

  public static String dependancies[] = new String[] { "wjtp.jbco_bapibm" };

  public String[] getDependencies() {
    return dependancies;
  }

  public static String name = "wjtp.jbco_bapibm";

  public String getName() {
    return name;
  }

  protected void internalTransform(String phaseName, Map<String, String> options) {
    if (output) {
      out.println("Building Intermediate Classes...");
    }

    BodyBuilder.retrieveAllBodies();

    // iterate through application classes, build intermediate classes
    Iterator<SootClass> it = Scene.v().getApplicationClasses().snapshotIterator();
    while (it.hasNext()) {
      List<SootMethod> initMethodsToRewrite = new ArrayList<>();
      Map<String, SootMethod> methodsToAdd = new HashMap<>();
      SootClass sc = it.next();
      SootClass originalSuperclass = sc.getSuperclass();

      if (output) {
        out.println("Processing " + sc.getName() + " with super " + originalSuperclass.getName());
      }

      Iterator<SootMethod> methodIterator = sc.methodIterator();
      while (methodIterator.hasNext()) {
        SootMethod method = methodIterator.next();
        if (!method.isConcrete()) {
          continue;
        }

        try {
          method.getActiveBody();
        } catch (Exception e) {
          if (method.retrieveActiveBody() == null) {
            throw new RuntimeException(method.getSignature() + " has no body. This was not expected dude.");
          }
        }

        String subSig = method.getSubSignature();
        if (subSig.equals("void main(java.lang.String[])") && method.isPublic() && method.isStatic()) {
          continue; // skip the main method - it needs to be named 'main'
        } else if (subSig.indexOf("init>(") > 0) {
          if (subSig.startsWith("void <init>(")) {
            initMethodsToRewrite.add(method);
          }
          continue; // skip constructors, just add for rewriting at the end
        } else {
          Scene.v().releaseActiveHierarchy();

          findAccessibleInSuperClassesBySubSig(sc, subSig).ifPresent(m -> methodsToAdd.put(subSig, m));
        }
      }

      if (methodsToAdd.size() > 0) {
        final String fullName = ClassRenamer.v().getNewName(ClassRenamer.getPackageName(sc.getName()), null);

        if (output) {
          out.println("\tBuilding " + fullName);
        }

        // make non-final soot class
        SootClass mediatingClass = new SootClass(fullName, sc.getModifiers() & (~Modifier.FINAL));
        Main.IntermediateAppClasses.add(mediatingClass);
        mediatingClass.setSuperclass(originalSuperclass);

        Scene.v().addClass(mediatingClass);
        mediatingClass.setApplicationClass();
        mediatingClass.setInScene(true);

        ThisRef thisRef = new ThisRef(mediatingClass.getType());

        for (String subSig : methodsToAdd.keySet()) {
          SootMethod originalSuperclassMethod = methodsToAdd.get(subSig);
          List<Type> paramTypes = originalSuperclassMethod.getParameterTypes();
          Type returnType = originalSuperclassMethod.getReturnType();
          List<SootClass> exceptions = originalSuperclassMethod.getExceptions();
          int modifiers = originalSuperclassMethod.getModifiers() & ~Modifier.ABSTRACT & ~Modifier.NATIVE;
          SootMethod newMethod;
          { // build new junk method to call original method
            String newMethodName = MethodRenamer.v().getNewName();
            newMethod = Scene.v().makeSootMethod(newMethodName, paramTypes, returnType, modifiers, exceptions);
            mediatingClass.addMethod(newMethod);

            Body body = Jimple.v().newBody(newMethod);
            newMethod.setActiveBody(body);
            Chain<Local> locals = body.getLocals();
            PatchingChain<Unit> units = body.getUnits();

            BodyBuilder.buildThisLocal(units, thisRef, locals);
            BodyBuilder.buildParameterLocals(units, locals, paramTypes);

            if (returnType instanceof VoidType) {
              units.add(Jimple.v().newReturnVoidStmt());
            } else if (returnType instanceof PrimType) {
              units.add(Jimple.v().newReturnStmt(IntConstant.v(0)));
            } else {
              units.add(Jimple.v().newReturnStmt(NullConstant.v()));
            }
            newmethods++;
          } // end build new junk method to call original method

          { // build copy of old method
            newMethod = Scene.v().makeSootMethod(originalSuperclassMethod.getName(), paramTypes, returnType, modifiers,
                exceptions);
            mediatingClass.addMethod(newMethod);

            Body body = Jimple.v().newBody(newMethod);
            newMethod.setActiveBody(body);
            Chain<Local> locals = body.getLocals();
            PatchingChain<Unit> units = body.getUnits();

            Local ths = BodyBuilder.buildThisLocal(units, thisRef, locals);
            List<Local> args = BodyBuilder.buildParameterLocals(units, locals, paramTypes);

            SootMethodRef superclassMethodRef = originalSuperclassMethod.makeRef();
            if (returnType instanceof VoidType) {
              units.add(Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(ths, superclassMethodRef, args)));
              units.add(Jimple.v().newReturnVoidStmt());
            } else {
              Local loc = Jimple.v().newLocal("retValue", returnType);
              body.getLocals().add(loc);

              units.add(Jimple.v().newAssignStmt(loc, Jimple.v().newSpecialInvokeExpr(ths, superclassMethodRef, args)));

              units.add(Jimple.v().newReturnStmt(loc));
            }
            newmethods++;
          } // end build copy of old method
        }
        sc.setSuperclass(mediatingClass);

        // rewrite class init methods to call the proper superclass inits
        int i = initMethodsToRewrite.size();
        while (i-- > 0) {
          SootMethod im = initMethodsToRewrite.remove(i);
          Body b = im.getActiveBody();
          Local thisLocal = b.getThisLocal();
          Iterator<Unit> uIt = b.getUnits().snapshotIterator();
          while (uIt.hasNext()) {
            for (ValueBox valueBox : uIt.next().getUseBoxes()) {
              Value v = valueBox.getValue();
              if (v instanceof SpecialInvokeExpr) {
                SpecialInvokeExpr sie = (SpecialInvokeExpr) v;
                SootMethodRef smr = sie.getMethodRef();
                if (sie.getBase().equivTo(thisLocal) && smr.declaringClass().getName().equals(originalSuperclass.getName())
                    && smr.getSubSignature().getString().startsWith("void " + constructorName)) {
                  SootMethod newSuperInit;
                  if (!mediatingClass.declaresMethod(constructorName, smr.parameterTypes())) {
                    List<Type> paramTypes = smr.parameterTypes();
                    newSuperInit = Scene.v().makeSootMethod(constructorName, paramTypes, smr.returnType());
                    mediatingClass.addMethod(newSuperInit);

                    JimpleBody body = Jimple.v().newBody(newSuperInit);
                    newSuperInit.setActiveBody(body);
                    PatchingChain<Unit> initUnits = body.getUnits();
                    Collection<Local> locals = body.getLocals();

                    Local ths = BodyBuilder.buildThisLocal(initUnits, thisRef, locals);
                    List<Local> args = BodyBuilder.buildParameterLocals(initUnits, locals, paramTypes);

                    initUnits.add(Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(ths, smr, args)));
                    initUnits.add(Jimple.v().newReturnVoidStmt());
                  } else {
                    newSuperInit = mediatingClass.getMethod(constructorName, smr.parameterTypes());
                  }

                  sie.setMethodRef(newSuperInit.makeRef());
                }
              }
            }
          }
        } // end of rewrite class init methods to call the proper superclass inits
      }
    }

    newclasses = Main.IntermediateAppClasses.size();

    Scene.v().releaseActiveHierarchy();
    Scene.v().getActiveHierarchy();
    Scene.v().setFastHierarchy(new FastHierarchy());
  }

  private Optional<SootMethod> findAccessibleInSuperClassesBySubSig(SootClass base, String subSig) {
    Hierarchy hierarchy = Scene.v().getActiveHierarchy();
    for (SootClass superClass : hierarchy.getSuperclassesOfIncluding(base.getSuperclass())) {
      if (superClass.isLibraryClass() && superClass.declaresMethod(subSig)) {
        SootMethod method = superClass.getMethod(subSig);
        if (hierarchy.isVisible(base, method)) {
          return Optional.of(method);
        }
      }
    }
    return Optional.empty();
  }
}
