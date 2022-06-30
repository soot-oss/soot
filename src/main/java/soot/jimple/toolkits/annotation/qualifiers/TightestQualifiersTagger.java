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

import soot.Body;
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
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.tagkit.ColorTag;
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

  private final HashMap<SootMethod, Integer> methodResultsMap = new HashMap<SootMethod, Integer>();
  private final HashMap<SootField, Integer> fieldResultsMap = new HashMap<SootField, Integer>();
  private MethodToContexts methodToContexts;

  protected void internalTransform(String phaseName, Map options) {

    handleMethods();
    handleFields();
  }

  private void handleMethods() {
    Iterator classesIt = Scene.v().getApplicationClasses().iterator();
    while (classesIt.hasNext()) {
      SootClass appClass = (SootClass) classesIt.next();
      Iterator methsIt = appClass.getMethods().iterator();
      while (methsIt.hasNext()) {
        SootMethod sm = (SootMethod) methsIt.next();
        // for now if its unreachable do nothing
        if (!Scene.v().getReachableMethods().contains(sm)) {
          continue;
        }
        analyzeMethod(sm);
      }
    }

    Iterator<SootMethod> methStatIt = methodResultsMap.keySet().iterator();
    while (methStatIt.hasNext()) {
      SootMethod meth = methStatIt.next();
      int result = methodResultsMap.get(meth).intValue();
      String sRes = "Public";
      if (result == RESULT_PUBLIC) {
        sRes = "Public";
      } else if (result == RESULT_PROTECTED) {
        sRes = "Protected";
      } else if (result == RESULT_PACKAGE) {
        sRes = "Package";
      } else if (result == RESULT_PRIVATE) {
        sRes = "Private";
      }

      String actual = null;
      if (Modifier.isPublic(meth.getModifiers())) {
        actual = "Public";
      } else if (Modifier.isProtected(meth.getModifiers())) {
        actual = "Protected";
      } else if (Modifier.isPrivate(meth.getModifiers())) {
        actual = "Private";
      } else {
        actual = "Package";
      }

      // System.out.println("Method: "+meth.getName()+" has "+actual+" level access, can have: "+sRes+" level access.");

      if (!sRes.equals(actual)) {
        if (meth.getName().equals("<init>")) {
          meth.addTag(new StringTag("Constructor: " + meth.getDeclaringClass().getName() + " has " + actual
              + " level access, can have: " + sRes + " level access.", "Tightest Qualifiers"));
        } else {
          meth.addTag(new StringTag(
              "Method: " + meth.getName() + " has " + actual + " level access, can have: " + sRes + " level access.",
              "Tightest Qualifiers"));
        }
        meth.addTag(new ColorTag(255, 10, 0, true, "Tightest Qualifiers"));
      }
    }
  }

  private void analyzeMethod(SootMethod sm) {

    CallGraph cg = Scene.v().getCallGraph();

    // Iterator eIt = Scene.v().getEntryPoints().iterator();
    // while (eIt.hasNext()){
    // System.out.println(eIt.next());
    // }

    if (methodToContexts == null) {
      methodToContexts = new MethodToContexts(Scene.v().getReachableMethods().listener());
    }

    for (Iterator momcIt = methodToContexts.get(sm).iterator(); momcIt.hasNext();) {
      final MethodOrMethodContext momc = (MethodOrMethodContext) momcIt.next();
      Iterator callerEdges = cg.edgesInto(momc);
      while (callerEdges.hasNext()) {
        Edge callEdge = (Edge) callerEdges.next();
        if (!callEdge.isExplicit()) {
          continue;
        }
        SootMethod methodCaller = callEdge.src();
        // System.out.println("Caller edge type: "+Edge.kindToString(callEdge.kind()));
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

    // System.out.println("protected method: "+sm.getName()+" in class: "+methodClass.getName()+" calling class:
    // "+callingClass.getName());

    boolean insidePackageAccess = isCallSamePackage(callingClass, methodClass);
    boolean subClassAccess = isCallClassSubClass(callingClass, methodClass);
    boolean sameClassAccess = isCallClassMethodClass(callingClass, methodClass);

    if (!insidePackageAccess && subClassAccess) {
      methodResultsMap.put(sm, new Integer(RESULT_PROTECTED));
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

    // System.out.println("package method: "+sm.getName()+" in class: "+methodClass.getName()+" calling class:
    // "+callingClass.getName());
    boolean insidePackageAccess = isCallSamePackage(callingClass, methodClass);
    boolean subClassAccess = isCallClassSubClass(callingClass, methodClass);
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

    // System.out.println("public method: "+sm.getName()+" in class: "+methodClass.getName()+" calling class:
    // "+callingClass.getName());

    boolean insidePackageAccess = isCallSamePackage(callingClass, methodClass);
    boolean subClassAccess = isCallClassSubClass(callingClass, methodClass);
    boolean sameClassAccess = isCallClassMethodClass(callingClass, methodClass);

    if (!insidePackageAccess && !subClassAccess) {
      methodResultsMap.put(sm, new Integer(RESULT_PUBLIC));
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
    if (!methodResultsMap.containsKey(sm)) {
      methodResultsMap.put(sm, new Integer(RESULT_PROTECTED));
    } else {
      if (methodResultsMap.get(sm).intValue() != RESULT_PUBLIC) {
        methodResultsMap.put(sm, new Integer(RESULT_PROTECTED));
      }
    }
  }

  private void updateToPackage(SootMethod sm) {
    if (!methodResultsMap.containsKey(sm)) {
      methodResultsMap.put(sm, new Integer(RESULT_PACKAGE));
    } else {
      if (methodResultsMap.get(sm).intValue() == RESULT_PRIVATE) {
        methodResultsMap.put(sm, new Integer(RESULT_PACKAGE));
      }
    }
  }

  private void updateToPrivate(SootMethod sm) {
    if (!methodResultsMap.containsKey(sm)) {
      methodResultsMap.put(sm, new Integer(RESULT_PRIVATE));
    }
  }

  private boolean isCallClassMethodClass(SootClass call, SootClass check) {
    if (call.equals(check)) {
      return true;
    }
    return false;
  }

  private boolean isCallClassSubClass(SootClass call, SootClass check) {
    if (!call.hasSuperclass()) {
      return false;
    }
    if (call.getSuperclass().equals(check)) {
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
    Iterator classesIt = Scene.v().getApplicationClasses().iterator();
    while (classesIt.hasNext()) {
      SootClass appClass = (SootClass) classesIt.next();
      Iterator fieldsIt = appClass.getFields().iterator();
      while (fieldsIt.hasNext()) {
        SootField sf = (SootField) fieldsIt.next();
        analyzeField(sf);
      }
    }

    Iterator<SootField> fieldStatIt = fieldResultsMap.keySet().iterator();
    while (fieldStatIt.hasNext()) {
      SootField f = fieldStatIt.next();
      int result = fieldResultsMap.get(f).intValue();
      String sRes = "Public";
      if (result == RESULT_PUBLIC) {
        sRes = "Public";
      } else if (result == RESULT_PROTECTED) {
        sRes = "Protected";
      } else if (result == RESULT_PACKAGE) {
        sRes = "Package";
      } else if (result == RESULT_PRIVATE) {
        sRes = "Private";
      }

      String actual = null;
      if (Modifier.isPublic(f.getModifiers())) {
        // System.out.println("Field: "+f.getName()+" is public");
        actual = "Public";
      } else if (Modifier.isProtected(f.getModifiers())) {
        actual = "Protected";
      } else if (Modifier.isPrivate(f.getModifiers())) {
        actual = "Private";
      } else {
        actual = "Package";
      }

      // System.out.println("Field: "+f.getName()+" has "+actual+" level access, can have: "+sRes+" level access.");

      if (!sRes.equals(actual)) {
        f.addTag(
            new StringTag("Field: " + f.getName() + " has " + actual + " level access, can have: " + sRes + " level access.",
                "Tightest Qualifiers"));
        f.addTag(new ColorTag(255, 10, 0, true, "Tightest Qualifiers"));
      }
    }
  }

  private void analyzeField(SootField sf) {

    // from all bodies get all use boxes and eliminate used fields
    Iterator classesIt = Scene.v().getApplicationClasses().iterator();
    while (classesIt.hasNext()) {
      SootClass appClass = (SootClass) classesIt.next();
      Iterator mIt = appClass.getMethods().iterator();
      while (mIt.hasNext()) {
        SootMethod sm = (SootMethod) mIt.next();
        if (!sm.hasActiveBody()) {
          continue;
        }
        if (!Scene.v().getReachableMethods().contains(sm)) {
          continue;
        }
        Body b = sm.getActiveBody();

        Iterator usesIt = b.getUseBoxes().iterator();
        while (usesIt.hasNext()) {
          ValueBox vBox = (ValueBox) usesIt.next();
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
      fieldResultsMap.put(sf, new Integer(RESULT_PUBLIC));
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
      fieldResultsMap.put(sf, new Integer(RESULT_PROTECTED));
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
    boolean subClassAccess = isCallClassSubClass(callingClass, fieldClass);
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
    if (!fieldResultsMap.containsKey(sf)) {
      fieldResultsMap.put(sf, new Integer(RESULT_PROTECTED));
    } else {
      if (fieldResultsMap.get(sf).intValue() != RESULT_PUBLIC) {
        fieldResultsMap.put(sf, new Integer(RESULT_PROTECTED));
      }
    }
  }

  private void updateToPackage(SootField sf) {
    if (!fieldResultsMap.containsKey(sf)) {
      fieldResultsMap.put(sf, new Integer(RESULT_PACKAGE));
    } else {
      if (fieldResultsMap.get(sf).intValue() == RESULT_PRIVATE) {
        fieldResultsMap.put(sf, new Integer(RESULT_PACKAGE));
      }
    }
  }

  private void updateToPrivate(SootField sf) {
    if (!fieldResultsMap.containsKey(sf)) {
      fieldResultsMap.put(sf, new Integer(RESULT_PRIVATE));
    }
  }
}
