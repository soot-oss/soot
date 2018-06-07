package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.options.Options;

public class Timers {
  private static final Logger logger = LoggerFactory.getLogger(Timers.class);

  public Timers(Singletons.Global g) {
  }

  public static Timers v() {
    return G.v().soot_Timers();
  }

  public int totalFlowNodes;

  public int totalFlowComputations;

  public Timer copiesTimer = new Timer("copies");

  public Timer defsTimer = new Timer("defs");

  public Timer usesTimer = new Timer("uses");

  public Timer liveTimer = new Timer("live");

  public Timer splitTimer = new Timer("split");

  public Timer packTimer = new Timer("pack");

  public Timer cleanup1Timer = new Timer("cleanup1");

  public Timer cleanup2Timer = new Timer("cleanup2");

  public Timer conversionTimer = new Timer("conversion");

  public Timer cleanupAlgorithmTimer = new Timer("cleanupAlgorithm");

  public Timer graphTimer = new Timer("graphTimer");

  public Timer assignTimer = new Timer("assignTimer");

  public Timer resolveTimer = new Timer("resolveTimer");

  public Timer totalTimer = new Timer("totalTimer");

  public Timer splitPhase1Timer = new Timer("splitPhase1");

  public Timer splitPhase2Timer = new Timer("splitPhase2");

  public Timer usePhase1Timer = new Timer("usePhase1");

  public Timer usePhase2Timer = new Timer("usePhase2");

  public Timer usePhase3Timer = new Timer("usePhase3");

  public Timer defsSetupTimer = new Timer("defsSetup");

  public Timer defsAnalysisTimer = new Timer("defsAnalysis");

  public Timer defsPostTimer = new Timer("defsPost");

  public Timer liveSetupTimer = new Timer("liveSetup");

  public Timer liveAnalysisTimer = new Timer("liveAnalysis");

  public Timer livePostTimer = new Timer("livePost");

  public Timer aggregationTimer = new Timer("aggregation");

  public Timer grimpAggregationTimer = new Timer("grimpAggregation");

  public Timer deadCodeTimer = new Timer("deadCode");

  public Timer propagatorTimer = new Timer("propagator");

  public Timer buildJasminTimer = new Timer("buildjasmin");

  public Timer assembleJasminTimer = new Timer("assembling jasmin");

  public Timer resolverTimer = new Timer("resolver");

  public int conversionLocalCount;

  public int cleanup1LocalCount;

  public int splitLocalCount;

  public int assignLocalCount;

  public int packLocalCount;

  public int cleanup2LocalCount;

  public int conversionStmtCount;

  public int cleanup1StmtCount;

  public int splitStmtCount;

  public int assignStmtCount;

  public int packStmtCount;

  public int cleanup2StmtCount;

  public long stmtCount;

  public Timer fieldTimer = new soot.Timer();

  public Timer methodTimer = new soot.Timer();

  public Timer attributeTimer = new soot.Timer();

  public Timer locatorTimer = new soot.Timer();

  public Timer readTimer = new soot.Timer();

  public Timer orderComputation = new soot.Timer("orderComputation");

  public void printProfilingInformation() {
    long totalTime = totalTimer.getTime();

    logger.debug("Time measurements");

    logger.debug("      Building graphs: " + toTimeString(graphTimer, totalTime));
    logger.debug("  Computing LocalDefs: " + toTimeString(defsTimer, totalTime));
    // logger.debug(" setup: " + toTimeString(defsSetupTimer, totalTime));
    // logger.debug(" analysis: " + toTimeString(defsAnalysisTimer, totalTime));
    // logger.debug(" post: " + toTimeString(defsPostTimer, totalTime));
    logger.debug("  Computing LocalUses: " + toTimeString(usesTimer, totalTime));
    // logger.debug(" Use phase1: " + toTimeString(usePhase1Timer, totalTime));
    // logger.debug(" Use phase2: " + toTimeString(usePhase2Timer, totalTime));
    // logger.debug(" Use phase3: " + toTimeString(usePhase3Timer, totalTime));

    logger.debug("     Cleaning up code: " + toTimeString(cleanupAlgorithmTimer, totalTime));
    logger.debug("Computing LocalCopies: " + toTimeString(copiesTimer, totalTime));
    logger.debug(" Computing LiveLocals: " + toTimeString(liveTimer, totalTime));
    // logger.debug(" setup: " + toTimeString(liveSetupTimer, totalTime));
    // logger.debug(" analysis: " + toTimeString(liveAnalysisTimer, totalTime));
    // logger.debug(" post: " + toTimeString(livePostTimer, totalTime));

    logger.debug("Coading coffi structs: " + toTimeString(resolveTimer, totalTime));

    // Print out time stats.
    {
      float timeInSecs;

      logger.debug("       Resolving classfiles: " + toTimeString(resolverTimer, totalTime));
      logger.debug(" Bytecode -> jimple (naive): " + toTimeString(conversionTimer, totalTime));
      logger.debug("        Splitting variables: " + toTimeString(splitTimer, totalTime));
      logger.debug("            Assigning types: " + toTimeString(assignTimer, totalTime));
      logger.debug("  Propagating copies & csts: " + toTimeString(propagatorTimer, totalTime));
      logger.debug("      Eliminating dead code: " + toTimeString(deadCodeTimer, totalTime));
      logger.debug("                Aggregation: " + toTimeString(aggregationTimer, totalTime));
      logger.debug("            Coloring locals: " + toTimeString(packTimer, totalTime));
      logger.debug("     Generating jasmin code: " + toTimeString(buildJasminTimer, totalTime));
      logger.debug("          .jasmin -> .class: " + toTimeString(assembleJasminTimer, totalTime));

      // logger.debug(" Cleaning up code: " + toTimeString(cleanup1Timer, totalTime) +
      // "\t" + cleanup1LocalCount + " locals " + cleanup1StmtCount + " stmts");

      // logger.debug(" Split phase1: " + toTimeString(splitPhase1Timer, totalTime));
      // logger.debug(" Split phase2: " + toTimeString(splitPhase2Timer, totalTime));

      /*
       * logger.debug("cleanup2Timer:   " + cleanup2Time + "(" + (cleanup2Time * 100 / totalTime) + "%) " +
       * cleanup2LocalCount + " locals  " + cleanup2StmtCount + " stmts");
       */

      timeInSecs = totalTime / 1000.0f;
      // float memoryUsed = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000.0f;

      logger.debug("totalTime:" + toTimeString(totalTimer, totalTime));

      if (Options.v().subtract_gc()) {
        logger.debug("Garbage collection was subtracted from these numbers.");
        logger.debug("           forcedGC:" + toTimeString(G.v().Timer_forcedGarbageCollectionTimer, totalTime));
      }

      logger.debug("stmtCount: " + stmtCount + "(" + toFormattedString(stmtCount / timeInSecs) + " stmt/s)");

      logger.debug("totalFlowNodes: " + totalFlowNodes + " totalFlowComputations: " + totalFlowComputations + " avg: "
          + truncatedOf((double) totalFlowComputations / totalFlowNodes, 2));
    }
  }

  private String toTimeString(Timer timer, long totalTime) {
    DecimalFormat format = new DecimalFormat("00.0");
    DecimalFormat percFormat = new DecimalFormat("00.0");

    long time = timer.getTime();

    String timeString = format.format(time / 1000.0); // paddedLeftOf(new Double(truncatedOf(time / 1000.0, 1)).toString(),
                                                      // 5);

    return (timeString + "s" + " (" + percFormat.format(time * 100.0 / totalTime) + "%" + ")");
  }

  private String toFormattedString(double value) {
    return paddedLeftOf(new Double(truncatedOf(value, 2)).toString(), 5);
  }

  public double truncatedOf(double d, int numDigits) {
    double multiplier = 1;

    for (int i = 0; i < numDigits; i++) {
      multiplier *= 10;
    }

    return ((long) (d * multiplier)) / multiplier;
  }

  public String paddedLeftOf(String s, int length) {
    if (s.length() >= length) {
      return s;
    } else {
      int diff = length - s.length();
      char[] padding = new char[diff];

      for (int i = 0; i < diff; i++) {
        padding[i] = ' ';
      }

      return new String(padding) + s;
    }
  }

}
