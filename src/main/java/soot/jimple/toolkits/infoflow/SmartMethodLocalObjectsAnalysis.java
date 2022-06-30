package soot.jimple.toolkits.infoflow;

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

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.EquivalentValue;
import soot.SootMethod;
import soot.Value;
import soot.jimple.Constant;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.Ref;
import soot.toolkits.graph.UnitGraph;

// SmartMethodLocalObjectsAnalysis written by Richard L. Halpert, 2007-02-23
// Uses a SmartMethodInfoFlowAnalysis to determine if a Local or FieldRef is
// LOCAL or SHARED in the given method.

public class SmartMethodLocalObjectsAnalysis {
  private static final Logger logger = LoggerFactory.getLogger(SmartMethodLocalObjectsAnalysis.class);
  public static int counter = 0;
  static boolean printMessages;

  SootMethod method;
  InfoFlowAnalysis dfa;
  SmartMethodInfoFlowAnalysis smdfa;

  public SmartMethodLocalObjectsAnalysis(SootMethod method, InfoFlowAnalysis dfa) {
    this.method = method;
    this.dfa = dfa;
    this.smdfa = dfa.getMethodInfoFlowAnalysis(method);

    printMessages = dfa.printDebug();
    counter++;
  }

  public SmartMethodLocalObjectsAnalysis(UnitGraph g, InfoFlowAnalysis dfa) {
    this(g.getBody().getMethod(), dfa);
  }

  public Value getThisLocal() {
    return smdfa.getThisLocal();
  }

  //
  public boolean isObjectLocal(Value local, CallLocalityContext context) // to this analysis of this method (which depends on
                                                                         // context)
  {
    EquivalentValue localEqVal;
    if (local instanceof InstanceFieldRef) {
      localEqVal = InfoFlowAnalysis.getNodeForFieldRef(method, ((FieldRef) local).getField());
    } else {
      localEqVal = new CachedEquivalentValue(local);
    }

    List<EquivalentValue> sources = smdfa.sourcesOf(localEqVal);
    Iterator<EquivalentValue> sourcesIt = sources.iterator();
    while (sourcesIt.hasNext()) {
      EquivalentValue source = sourcesIt.next();
      if (source.getValue() instanceof Ref) {
        if (!context.isFieldLocal(source)) {
          if (printMessages) {
            logger.debug("      Requested value " + local + " is SHARED in " + method + " ");
          }
          return false;
        }
      } else if (source.getValue() instanceof Constant) {
        if (printMessages) {
          logger.debug("      Requested value " + local + " is SHARED in " + method + " ");
        }
        return false;
      }
    }
    if (printMessages) {
      logger.debug("      Requested value " + local + " is LOCAL in " + method + " ");
    }
    return true;
  }

  public static boolean isObjectLocal(InfoFlowAnalysis dfa, SootMethod method, CallLocalityContext context, Value local) {
    SmartMethodInfoFlowAnalysis smdfa = dfa.getMethodInfoFlowAnalysis(method);

    EquivalentValue localEqVal;
    if (local instanceof InstanceFieldRef) {
      localEqVal = InfoFlowAnalysis.getNodeForFieldRef(method, ((FieldRef) local).getField());
    } else {
      localEqVal = new CachedEquivalentValue(local);
    }

    List<EquivalentValue> sources = smdfa.sourcesOf(localEqVal);
    Iterator<EquivalentValue> sourcesIt = sources.iterator();
    while (sourcesIt.hasNext()) {
      EquivalentValue source = sourcesIt.next();
      if (source.getValue() instanceof Ref) {
        if (!context.isFieldLocal(source)) {
          if (printMessages) {
            logger.debug("      Requested value " + local + " is LOCAL in " + method + " ");
          }
          return false;
        }
      }
    }
    if (printMessages) {
      logger.debug("      Requested value " + local + " is SHARED in " + method + " ");
    }
    return true;
  }
}
