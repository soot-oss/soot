package soot.jimple.toolkits.annotation.methods;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import soot.G;
import soot.Scene;
import soot.SceneTransformer;
import soot.Singletons;
import soot.SootClass;
import soot.SootMethod;
import soot.tagkit.ColorTag;
import soot.tagkit.StringTag;

/** A scene transformer that adds tags to unused methods. */
public class UnreachableMethodsTagger extends SceneTransformer {
  public UnreachableMethodsTagger(Singletons.Global g) {
  }

  public static UnreachableMethodsTagger v() {
    return G.v().soot_jimple_toolkits_annotation_methods_UnreachableMethodsTagger();
  }

  protected void internalTransform(String phaseName, Map options) {

    // make list of all unreachable methods
    ArrayList<SootMethod> methodList = new ArrayList<SootMethod>();

    Iterator getClassesIt = Scene.v().getApplicationClasses().iterator();
    while (getClassesIt.hasNext()) {
      SootClass appClass = (SootClass) getClassesIt.next();

      Iterator getMethodsIt = appClass.getMethods().iterator();
      while (getMethodsIt.hasNext()) {
        SootMethod method = (SootMethod) getMethodsIt.next();
        // System.out.println("adding method: "+method);
        if (!Scene.v().getReachableMethods().contains(method)) {
          methodList.add(method);
        }
      }
    }

    // tag unused methods
    Iterator<SootMethod> unusedIt = methodList.iterator();
    while (unusedIt.hasNext()) {
      SootMethod unusedMethod = unusedIt.next();
      unusedMethod.addTag(new StringTag("Method " + unusedMethod.getName() + " is not reachable!", "Unreachable Methods"));
      unusedMethod.addTag(new ColorTag(255, 0, 0, true, "Unreachable Methods"));
      // System.out.println("tagged method: "+unusedMethod);

    }
  }

}
