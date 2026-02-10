package soot.jimple.toolkits.annotation.qualifiers;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;

import soot.Body;
import soot.ClassMember;
import soot.G;
import soot.MethodOrMethodContext;
import soot.MethodToContexts;
import soot.Modifier;
import soot.Scene;
import soot.SceneTransformer;
import soot.Singletons;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Value;
import soot.ValueBox;
import soot.jimple.FieldRef;
import soot.jimple.toolkits.annotation.qualifiers.TightestQualifiersTag.AccessLevel;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.tagkit.StringTag;

/**
 * a scene transformer that add tags to indicate the tightest qualifies possible for fields and methods (ie: private,
 * protected or public)
 */
public class TightestQualifiersTagger extends SceneTransformer {

  public TightestQualifiersTagger(Singletons.Global g) {
  }

  public static TightestQualifiersTagger v() {
    return G.v().soot_jimple_toolkits_annotation_qualifiers_TightestQualifiersTagger();
  }

  public final static int RESULT_PUBLIC = 0;
  public final static int RESULT_PACKAGE = 1;
  public final static int RESULT_PROTECTED = 2;
  public final static int RESULT_PRIVATE = 3;
  private static final BiFunction<? super ClassMember, ? super Integer, ? extends Integer> UPDATE_TO_PACKAGE
      = new BiFunction<ClassMember, Integer, Integer>() {

        @Override
        public Integer apply(ClassMember t, Integer old) {
          if (old == null || old == RESULT_PRIVATE) {
            return RESULT_PACKAGE;
          }
          return old;
        }

      };
  private static final BiFunction<? super ClassMember, ? super Integer, ? extends Integer> UPDATE_TO_PROTECTED
      = new BiFunction<ClassMember, Integer, Integer>() {

        @Override
        public Integer apply(ClassMember t, Integer old) {
          if (old != RESULT_PUBLIC) {
            return RESULT_PROTECTED;
          }
          return old;
        }

      };

  private final HashMap<SootMethod, Integer> methodResultsMap = new HashMap<SootMethod, Integer>();
  private final HashMap<SootField, Integer> fieldResultsMap = new HashMap<SootField, Integer>();
  private MethodToContexts methodToContexts;

  @Override
  protected void internalTransform(String phaseName, Map<String, String> options) {
    handleMethods();
    handleFields();
  }

  private void handleMethods() {
    Iterator<SootClass> classesIt = Scene.v().getApplicationClasses().iterator();
    while (classesIt.hasNext()) {
      SootClass appClass = (SootClass) classesIt.next();
      Iterator<SootMethod> methsIt = appClass.getMethods().iterator();
      while (methsIt.hasNext()) {
        SootMethod sm = (SootMethod) methsIt.next();
        // for now if its unreachable do nothing
        if (skipMethod(sm)) {
          continue;
        }
        analyzeMethod(sm);
      }
    }

    Iterator<SootMethod> methStatIt = methodResultsMap.keySet().iterator();
    while (methStatIt.hasNext()) {
      SootMethod meth = methStatIt.next();
      int result = methodResultsMap.get(meth).intValue();
      AccessLevel sRes = resultToLevel(result);

      int modifiers = meth.getModifiers();
      AccessLevel actual = getAccessLevelFromModifiers(modifiers);

      if (!sRes.equals(actual)) {
        if (meth.isConstructor()) {
          meth.addTag(new StringTag(String.format("Constructor: %s has %s level access, can have: %s level access.",
              meth.getDeclaringClass().getName(), actual, sRes), "Tightest Qualifiers"));
        } else {
          meth.addTag(new StringTag(
              String.format("Method: %s has %s level access, can have: %s level access.", meth.getName(), actual, sRes),
              "Tightest Qualifiers"));
        }
        meth.addTag(TightestQualifiersTag.v(actual, sRes));
      }
    }
  }

  /**
   * Returns whether to skip a method. By default, the analysis skips unreachable methods
   * 
   * @param sm
   *          the method
   * @return true if the method should be skipped
   */
  protected boolean skipMethod(SootMethod sm) {
    return !Scene.v().getReachableMethods().contains(sm);
  }

  private static AccessLevel getAccessLevelFromModifiers(int modifiers) {
    if (Modifier.isPublic(modifiers)) {
      return AccessLevel.PUBLIC;
    } else if (Modifier.isProtected(modifiers)) {
      return AccessLevel.PROTECTED;
    } else if (Modifier.isPrivate(modifiers)) {
      return AccessLevel.PRIVATE;
    } else {
      return AccessLevel.PACKAGE_PROTECTED;
    }
  }

  private static AccessLevel resultToLevel(int result) {
    switch (result) {
      case RESULT_PUBLIC:
      default:
        return AccessLevel.PUBLIC;
      case RESULT_PROTECTED:
        return AccessLevel.PROTECTED;
      case RESULT_PACKAGE:
        return AccessLevel.PACKAGE_PROTECTED;
      case RESULT_PRIVATE:
        return AccessLevel.PRIVATE;
    }
  }

  private void analyzeMethod(SootMethod sm) {

    CallGraph cg = Scene.v().getCallGraph();

    if (methodToContexts == null) {
      methodToContexts = new MethodToContexts(Scene.v().getReachableMethods().listener());
    }

    for (Iterator<MethodOrMethodContext> momcIt = methodToContexts.get(sm).iterator(); momcIt.hasNext();) {
      final MethodOrMethodContext momc = momcIt.next();
      Iterator<Edge> callerEdges = cg.edgesInto(momc);
      while (callerEdges.hasNext()) {
        Edge callEdge = callerEdges.next();
        if (!callEdge.isExplicit()) {
          continue;
        }
        SootMethod methodCaller = callEdge.src();

        SootClass callingClass = methodCaller.getDeclaringClass();
        // public methods
        if (Modifier.isPublic(sm.getModifiers())) {
          analyzePublicMethod(sm, callingClass);
        }
        // protected methods
        else if (Modifier.isProtected(sm.getModifiers())) {
          analyzeProtectedMethod(sm, callingClass);
        }
        // private methods - do nothing
        else if (Modifier.isPrivate(sm.getModifiers())) {
        }
        // package level methods
        else {
          analyzePackageMethod(sm, callingClass);
        }

      }
    }

  }

  private boolean analyzeProtectedMethod(SootMethod sm, SootClass callingClass) {
    SootClass methodClass = sm.getDeclaringClass();

    boolean insidePackageAccess = isCallSamePackage(callingClass, methodClass);
    boolean subClassAccess = isCallClassSubClass(callingClass, methodClass);
    boolean sameClassAccess = isCallClassMethodClass(callingClass, methodClass);

    if (!insidePackageAccess && subClassAccess) {
      methodResultsMap.put(sm, RESULT_PROTECTED);
      return true;
    } else if (insidePackageAccess && !sameClassAccess) {
      updateToPackage(sm);
      return false;
    } else {
      updateToPrivate(sm);
      return false;
    }
  }

  private boolean analyzePackageMethod(SootMethod sm, SootClass callingClass) {
    SootClass methodClass = sm.getDeclaringClass();

    boolean insidePackageAccess = isCallSamePackage(callingClass, methodClass);
    boolean sameClassAccess = isCallClassMethodClass(callingClass, methodClass);

    if (insidePackageAccess && !sameClassAccess) {
      updateToPackage(sm);
      return true;
    } else {
      updateToPrivate(sm);
      return false;
    }
  }

  private boolean analyzePublicMethod(SootMethod sm, SootClass callingClass) {

    SootClass methodClass = sm.getDeclaringClass();

    boolean insidePackageAccess = isCallSamePackage(callingClass, methodClass);
    boolean subClassAccess = isCallClassSubClass(callingClass, methodClass);
    boolean sameClassAccess = isCallClassMethodClass(callingClass, methodClass);

    if (!insidePackageAccess && !subClassAccess) {
      methodResultsMap.put(sm, RESULT_PUBLIC);
      return true;
    } else if (!insidePackageAccess && subClassAccess) {
      updateToProtected(sm);
      return false;
    } else if (insidePackageAccess && !sameClassAccess) {
      updateToPackage(sm);
      return false;
    } else {
      updateToPrivate(sm);
      return false;
    }

  }

  private void updateToProtected(SootMethod sm) {
    methodResultsMap.compute(sm, UPDATE_TO_PROTECTED);
  }

  private void updateToPackage(SootMethod sm) {
    methodResultsMap.compute(sm, UPDATE_TO_PACKAGE);
  }

  private void updateToPrivate(SootMethod sm) {
    methodResultsMap.putIfAbsent(sm, RESULT_PRIVATE);
  }

  private boolean isCallClassMethodClass(SootClass call, SootClass check) {
    return call.equals(check);
  }

  private boolean isCallClassSubClass(SootClass call, SootClass check) {
    if (call != check && Scene.v().getOrMakeFastHierarchy().canStoreClass(call, check)) {
      return true;
    }
    return false;
  }

  private boolean isCallSamePackage(SootClass call, SootClass check) {
    if (call.getPackageName().equals(check.getPackageName())) {
      return true;
    }
    return false;
  }

  private void handleFields() {
    Iterator<SootClass> classesIt = Scene.v().getApplicationClasses().iterator();
    while (classesIt.hasNext()) {
      SootClass appClass = (SootClass) classesIt.next();
      Iterator<SootField> fieldsIt = appClass.getFields().iterator();
      while (fieldsIt.hasNext()) {
        SootField sf = fieldsIt.next();
        analyzeField(sf);
      }
    }

    Iterator<SootField> fieldStatIt = fieldResultsMap.keySet().iterator();
    while (fieldStatIt.hasNext()) {
      SootField f = fieldStatIt.next();
      int result = fieldResultsMap.get(f).intValue();
      AccessLevel sRes = resultToLevel(result);

      AccessLevel actual = getAccessLevelFromModifiers(f.getModifiers());

      if (!sRes.equals(actual)) {
        f.addTag(
            new StringTag("Field: " + f.getName() + " has " + actual + " level access, can have: " + sRes + " level access.",
                "Tightest Qualifiers"));
        f.addTag(TightestQualifiersTag.v(actual, sRes));
      }
    }
  }

  private void analyzeField(SootField sf) {

    // from all bodies get all use boxes and eliminate used fields
    Iterator<SootClass> classesIt = Scene.v().getApplicationClasses().iterator();
    while (classesIt.hasNext()) {
      SootClass appClass = (SootClass) classesIt.next();
      Iterator<SootMethod> mIt = appClass.getMethods().iterator();
      while (mIt.hasNext()) {
        SootMethod sm = (SootMethod) mIt.next();
        if (!sm.hasActiveBody() || !Scene.v().getReachableMethods().contains(sm)) {
          continue;
        }
        Body b = sm.getActiveBody();

        Iterator<ValueBox> usesIt = b.getUseBoxesIterator();
        while (usesIt.hasNext()) {
          ValueBox vBox = usesIt.next();
          Value v = vBox.getValue();
          if (v instanceof FieldRef) {
            FieldRef fieldRef = (FieldRef) v;
            SootField f = fieldRef.getField();
            if (f.equals(sf)) {
              if (Modifier.isPublic(sf.getModifiers())) {
                if (analyzePublicField(sf, appClass)) {
                  return;
                }
              } else if (Modifier.isProtected(sf.getModifiers())) {
                analyzeProtectedField(sf, appClass);
              } else if (Modifier.isPrivate(sf.getModifiers())) {
              } else {
                analyzePackageField(sf, appClass);
              }
            }
          }
        }
      }
    }
  }

  private boolean analyzePublicField(SootField sf, SootClass callingClass) {
    SootClass fieldClass = sf.getDeclaringClass();

    boolean insidePackageAccess = isCallSamePackage(callingClass, fieldClass);
    boolean subClassAccess = isCallClassSubClass(callingClass, fieldClass);
    boolean sameClassAccess = isCallClassMethodClass(callingClass, fieldClass);

    if (!insidePackageAccess && !subClassAccess) {
      fieldResultsMap.put(sf, RESULT_PUBLIC);
      return true;
    } else if (!insidePackageAccess && subClassAccess) {
      updateToProtected(sf);
      return false;
    } else if (insidePackageAccess && !sameClassAccess) {
      updateToPackage(sf);
      return false;
    } else {
      updateToPrivate(sf);
      return false;
    }

  }

  private boolean analyzeProtectedField(SootField sf, SootClass callingClass) {
    SootClass fieldClass = sf.getDeclaringClass();

    boolean insidePackageAccess = isCallSamePackage(callingClass, fieldClass);
    boolean subClassAccess = isCallClassSubClass(callingClass, fieldClass);
    boolean sameClassAccess = isCallClassMethodClass(callingClass, fieldClass);

    if (!insidePackageAccess && subClassAccess) {
      fieldResultsMap.put(sf, RESULT_PROTECTED);
      return true;
    } else if (insidePackageAccess && !sameClassAccess) {
      updateToPackage(sf);
      return false;
    } else {
      updateToPrivate(sf);
      return false;
    }
  }

  private boolean analyzePackageField(SootField sf, SootClass callingClass) {
    SootClass fieldClass = sf.getDeclaringClass();

    boolean insidePackageAccess = isCallSamePackage(callingClass, fieldClass);
    boolean sameClassAccess = isCallClassMethodClass(callingClass, fieldClass);

    if (insidePackageAccess && !sameClassAccess) {
      updateToPackage(sf);
      return true;
    } else {
      updateToPrivate(sf);
      return false;
    }
  }

  private void updateToProtected(SootField sf) {
    fieldResultsMap.compute(sf, UPDATE_TO_PROTECTED);
  }

  private void updateToPackage(SootField sf) {
    fieldResultsMap.compute(sf, UPDATE_TO_PACKAGE);
  }

  private void updateToPrivate(SootField sf) {
    fieldResultsMap.putIfAbsent(sf, RESULT_PRIVATE);
  }
}
