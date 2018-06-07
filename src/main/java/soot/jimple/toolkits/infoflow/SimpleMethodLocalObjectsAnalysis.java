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
import soot.SootField;
import soot.SootMethod;
import soot.Value;
import soot.toolkits.graph.UnitGraph;

// SimpleMethodLocalObjectsAnalysis written by Richard L. Halpert, 2007-02-23
// Finds objects that are local to the scope of the LocalObjectsScopeAnalysis
// that is provided.
// This is a specialized version of SimpleMethodInfoFlowAnalysis, in which the data
// source is the abstract "shared" data source.

public class SimpleMethodLocalObjectsAnalysis extends SimpleMethodInfoFlowAnalysis {
  private static final Logger logger = LoggerFactory.getLogger(SimpleMethodLocalObjectsAnalysis.class);
  public static int mlocounter = 0;

  public SimpleMethodLocalObjectsAnalysis(UnitGraph g, ClassLocalObjectsAnalysis cloa, InfoFlowAnalysis dfa) {
    super(g, dfa, true, true); // special version doesn't run analysis yet

    mlocounter++;

    printMessages = false;

    SootMethod method = g.getBody().getMethod();

    AbstractDataSource sharedDataSource = new AbstractDataSource(new String("SHARED"));

    // Add a source for every parameter that is shared
    for (int i = 0; i < method.getParameterCount(); i++) // no need to worry about return value...
    {
      EquivalentValue paramEqVal = InfoFlowAnalysis.getNodeForParameterRef(method, i);
      if (!cloa.parameterIsLocal(method, paramEqVal)) {
        addToEntryInitialFlow(sharedDataSource, paramEqVal.getValue());
        addToNewInitialFlow(sharedDataSource, paramEqVal.getValue());
      }
    }

    for (SootField sf : cloa.getSharedFields()) {
      EquivalentValue fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(method, sf);
      addToEntryInitialFlow(sharedDataSource, fieldRefEqVal.getValue());
      addToNewInitialFlow(sharedDataSource, fieldRefEqVal.getValue());
    }

    if (printMessages) {
      logger.debug("----- STARTING SHARED/LOCAL ANALYSIS FOR " + g.getBody().getMethod() + " -----");
    }
    doFlowInsensitiveAnalysis();
    if (printMessages) {
      logger.debug("----- ENDING   SHARED/LOCAL ANALYSIS FOR " + g.getBody().getMethod() + " -----");
    }
  }

  public SimpleMethodLocalObjectsAnalysis(UnitGraph g, CallLocalityContext context, InfoFlowAnalysis dfa) {
    super(g, dfa, true, true); // special version doesn't run analysis yet

    mlocounter++;

    printMessages = false;

    SootMethod method = g.getBody().getMethod();

    AbstractDataSource sharedDataSource = new AbstractDataSource(new String("SHARED"));

    List<Object> sharedRefs = context.getSharedRefs();
    Iterator<Object> sharedRefEqValIt = sharedRefs.iterator(); // returns a list of (correctly structured) EquivalentValue
                                                               // wrapped refs that should be
                                                               // treated as shared
    while (sharedRefEqValIt.hasNext()) {
      EquivalentValue refEqVal = (EquivalentValue) sharedRefEqValIt.next();
      addToEntryInitialFlow(sharedDataSource, refEqVal.getValue());
      addToNewInitialFlow(sharedDataSource, refEqVal.getValue());
    }

    if (printMessages) {
      logger.debug("----- STARTING SHARED/LOCAL ANALYSIS FOR " + g.getBody().getMethod() + " -----");
      logger.debug("      " + context.toString().replaceAll("\n", "\n      "));
      logger.debug("found " + sharedRefs.size() + " shared refs in context.");
    }
    doFlowInsensitiveAnalysis();
    if (printMessages) {
      logger.debug("----- ENDING   SHARED/LOCAL ANALYSIS FOR " + g.getBody().getMethod() + " -----");
    }
  }

  // Interesting sources are summarized (and possibly printed)
  public boolean isInterestingSource(Value source) {
    return (source instanceof AbstractDataSource);
  }

  // Interesting sinks are possibly printed
  public boolean isInterestingSink(Value sink) {
    return true; // (sink instanceof Local); // we're interested in all values
  }

  //
  public boolean isObjectLocal(Value local) // to this analysis of this method (which depends on context)
  {
    EquivalentValue source = new CachedEquivalentValue(new AbstractDataSource(new String("SHARED")));
    if (infoFlowGraph.containsNode(source)) {
      List sinks = infoFlowGraph.getSuccsOf(source);
      if (printMessages) {
        logger.debug("      Requested value " + local + " is "
            + (!sinks.contains(new CachedEquivalentValue(local)) ? "Local" : "Shared") + " in " + sm + " ");
      }
      return !sinks.contains(new CachedEquivalentValue(local));
    } else {
      if (printMessages) {
        logger.debug("      Requested value " + local + " is Local (LIKE ALL VALUES) in " + sm + " ");
      }
      return true; // no shared data in this method
    }
  }
}
