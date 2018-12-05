package soot.jimple.toolkits.thread;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.SootMethod;
import soot.toolkits.graph.ExceptionalUnitGraph;

// EncapsulatedObjectAnalysis written by Richard L. Halpert, 2006-12-26
// Checks if all methods of a class are "object-pure", meaning that
// they read and write only themselves and new local objects

public class EncapsulatedObjectAnalysis // extends ForwardFlowAnalysis
{
  private static final Logger logger = LoggerFactory.getLogger(EncapsulatedObjectAnalysis.class);
  List cachedClasses;
  List<SootMethod> objectPureMethods;
  List<SootMethod> objectPureInitMethods;

  public EncapsulatedObjectAnalysis() {
    cachedClasses = new ArrayList();
    objectPureMethods = new ArrayList<SootMethod>();
    objectPureInitMethods = new ArrayList<SootMethod>();
  }

  public boolean isMethodPureOnObject(SootMethod sm) {
    if (!cachedClasses.contains(sm.getDeclaringClass()) && sm.isConcrete()) // NOT A COMPLETE SOLUTION (ignores subclassing)
    {
      SootMethod initMethod = null;
      Collection methods = sm.getDeclaringClass().getMethods();
      Iterator methodsIt = methods.iterator();
      List<SootMethod> mayBePureMethods = new ArrayList<SootMethod>(methods.size());
      while (methodsIt.hasNext()) {
        SootMethod method = (SootMethod) methodsIt.next();
        if (method.isConcrete()) {
          if (method.getSubSignature().startsWith("void <init>")) {
            initMethod = method;
          }
          Body b = method.retrieveActiveBody();
          EncapsulatedMethodAnalysis ema = new EncapsulatedMethodAnalysis(new ExceptionalUnitGraph(b));
          if (ema.isPure()) {
            mayBePureMethods.add(method);
          }
        }
      }

      if (mayBePureMethods.size() == methods.size()) {
        objectPureMethods.addAll(mayBePureMethods);
      } else if (initMethod != null) {
        objectPureMethods.add(initMethod);
      }
      if (initMethod != null) {
        objectPureInitMethods.add(initMethod);
      }
    }

    return objectPureMethods.contains(sm);
  }

  public boolean isInitMethodPureOnObject(SootMethod sm) {
    // logger.debug("Testing Init Method Encapsulation: " + sm + " Encapsulated: ");
    if (isMethodPureOnObject(sm)) {
      boolean ret = objectPureInitMethods.contains(sm);
      // logger.debug(""+ret);
      return ret;
    }
    // logger.debug("false");
    return false;
  }

  public List<SootMethod> getObjectPureMethodsSoFar() {
    return objectPureMethods;
  }
}
