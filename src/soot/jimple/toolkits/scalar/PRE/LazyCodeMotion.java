/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Florian Loitsch
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.jimple.toolkits.scalar.PRE;
import soot.jimple.toolkits.graph.*;
import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;

/** 
 * performs a partial redundancy elimination (= code motion). This is done, by
 * introducing helper-vars, that store an already computed value, or if a
 * compuation only arrives partially (not from all predecessors) inserts a new
 * computation on these paths.
 * afterwards).<br>
 * In order to catch every redundant expression, this transformation must be
 * done on a graph without critical edges. Therefore the first thing we do, is
 * removing them. A subsequent pass can then easily remove the synthetic nodes
 * we have introduced.<br>
 * The term "lazy" refers to the fact, that we move computations only if
 * necessary.
 *
 * @see soot.jimple.toolkits.graph.CriticalEdgeRemover
 */
public class LazyCodeMotion extends BodyTransformer {
  public static int counterGlobalInserts = 0;  //inserted
  public static int counterGlobalNewInserts = 0; //not at original computation
  public static int counterGlobalUses = 0; //uses
  private int counterInserts;
  private int counterNewInserts;
  private int counterUses;

  private static final String PREFIX = "$lcm";

  private static LazyCodeMotion instance = new LazyCodeMotion();
  private LazyCodeMotion() {}

  public static LazyCodeMotion v() { return instance; }

  public String getDeclaredOptions() {
    return super.getDeclaredOptions() + " safe unroll";
  }

  // safe is one out of "safe" "medium" "unsafe"
  public String getDefaultOptions() { return "safe:medium unroll:true"; }
        
  /**
   * performs the lazy code motion.
   */
  protected void internalTransform(Body b, String phaseName, Map options) {
    counterInserts = 0;
    counterNewInserts = 0;
    counterUses = 0;
    int counter = 0;
    int unrolledConditions = 0;
    HashMap expToHelper = new HashMap();
    Chain unitChain = b.getUnits();
    String safe = Options.getString(options, "safe");
    boolean unroll = Options.getBoolean(options, "unroll");

    if(Main.isVerbose) System.out.println("[" + b.getMethod().getName() +
                                          "]     performing Lazy Code Motion...");

    if (unroll) {
      LoopConditionUnroller t = new LoopConditionUnroller(new
        BriefBlockGraph(b), 15);
      unrolledConditions = t.counterUnrolledConditions;
    }
    CriticalEdgeRemover.removeCriticalEdges(b);

    UnitGraph graph = new BriefUnitGraph(b);

    /* map each unit to its RHS. only take binary expressions */
    Map unitToEquivRhs = new UnitMap(b, graph.size() + 1, 0.7f) {
	protected Object mapTo(Unit unit) {
	  Value tmp = SootFilter.noInvokeRhs(unit);
	  Value tmp2 = SootFilter.binop(tmp);
	  if (tmp2 == null) tmp2 = SootFilter.concreteRef(tmp);
	  return SootFilter.equiVal(tmp2);
	}
      };

    /* same as before, but without exception-throwing expressions */
    Map unitToNoExceptionEquivRhs = new UnitMap(b, graph.size() + 1, 0.7f) {
	protected Object mapTo(Unit unit) {
	  Value tmp = SootFilter.binopRhs(unit);
	  tmp = SootFilter.noExceptionThrowing(tmp);
	  return SootFilter.equiVal(tmp);
	}
      };

    /* if a more precise sideeffect-tester comes out, please change it here! */
    SideEffectTester sideEffect = new NaiveSideEffectTester();
    UpSafetyAnalysis upSafe;
    DownSafetyAnalysis downSafe; 
    EarliestnessComputation earliest;
    DelayabilityAnalysis delay;
    NotIsolatedAnalysis notIsolated;
    LatestComputation latest;

    if ("safe".equals(safe))
      upSafe = new UpSafetyAnalysis(graph, unitToNoExceptionEquivRhs,
                                    sideEffect);
    else
      upSafe = new UpSafetyAnalysis(graph, unitToEquivRhs, sideEffect);

    if ("unsafe".equals(safe))
      downSafe = new DownSafetyAnalysis(graph, unitToEquivRhs, sideEffect);
    else {
      downSafe = new DownSafetyAnalysis(graph, unitToNoExceptionEquivRhs,
					sideEffect);
      /* we include the exception-throwing expressions at their uses */
      Iterator unitIt = unitChain.iterator();
      while (unitIt.hasNext()) {
	Unit currentUnit = (Unit)unitIt.next();
	Object rhs = unitToEquivRhs.get(currentUnit);
	if (rhs != null) {
	  FlowSet tmp = (FlowSet)downSafe.getFlowBefore(currentUnit);
	  tmp.add(rhs, tmp);
	}
      }
    }

    earliest = new EarliestnessComputation(graph, upSafe, downSafe, sideEffect);
    delay = new DelayabilityAnalysis(graph, earliest, unitToEquivRhs);
    latest = new LatestComputation(graph, earliest, delay, unitToEquivRhs);
    notIsolated = new NotIsolatedAnalysis(graph, earliest, unitToEquivRhs);

    /* debug */
    /*
    {
      System.out.println("========" + b.getMethod().getName());
      Iterator unitIt = unitChain.iterator();
      while (unitIt.hasNext()) {
	Unit currentUnit = (Unit) unitIt.next();
        FlowSet latestSet = latest.getLatestBefore(currentUnit);
        FlowSet notIsolatedSet =
          (FlowSet)notIsolated.getFlowAfter(currentUnit);
        FlowSet delaySet = (FlowSet)delay.getFlowBefore(currentUnit);
        Iterator earlyIt = earliest.getEarliestBefore(currentUnit).iterator();
	FlowSet upSet = (FlowSet)upSafe.getFlowBefore(currentUnit);
	FlowSet downSet = (FlowSet)downSafe.getFlowBefore(currentUnit);
	System.out.println(currentUnit);
	System.out.println(" up: " + upSet);
	System.out.println(" do: " + downSet);
	System.out.println(" is: " + notIsolatedSet);
	System.out.print(" ea: {");
	while (earlyIt.hasNext())
	  System.out.print(earlyIt.next() + ", ");
	System.out.println("}");
	System.out.println(" la: " + latestSet);
      }
    }
    */

    { /* insert the computations */
      Iterator unitIt = unitChain.snapshotIterator();
      while (unitIt.hasNext()) {
        Unit currentUnit = (Unit)unitIt.next();
        FlowSet latestSet = latest.getLatestBefore(currentUnit);
        FlowSet notIsolatedSet =
          (FlowSet)notIsolated.getFlowAfter(currentUnit);
        FlowSet insertHere = (FlowSet)latestSet.clone();
        insertHere.intersection(notIsolatedSet, insertHere);
        Iterator insertIt = insertHere.toList().iterator();
        while (insertIt.hasNext()) {
          EquivalentValue equiVal = (EquivalentValue)insertIt.next();
          /* get the unic helper-name for this expression */
          Local helper = (Local)expToHelper.get(equiVal);
          if (helper == null) {
            String helperName = PREFIX + counter++;
            helper = Jimple.v().newLocal(helperName,
                Type.toMachineType(equiVal.getType()));
            b.getLocals().add(helper);
            expToHelper.put(equiVal, helper);
          }

          /* insert a new Assignment-stmt before the currentUnit */
          Unit firstComp = Jimple.v().newAssignStmt(helper, equiVal.getValue());
          unitChain.insertBefore(firstComp, currentUnit);
          //	  System.out.print("x");
          counterInserts++;
          if ((currentUnit instanceof AssignStmt) &&
              equiVal.equivToValue(((AssignStmt)currentUnit).getRightOp()))
            counterNewInserts++;
        }
      }
    }

    { /* replace old computations by the helper-vars */
      Iterator unitIt = unitChain.iterator();
      while (unitIt.hasNext()) {
        Unit currentUnit = (Unit)unitIt.next();
        EquivalentValue rhs = (EquivalentValue)unitToEquivRhs.get(currentUnit);
        if (rhs != null) {
          FlowSet latestSet = latest.getLatestBefore(currentUnit);
          FlowSet notIsolatedSet =
            (FlowSet)notIsolated.getFlowAfter(currentUnit);
          if (!latestSet.contains(rhs) || notIsolatedSet.contains(rhs)) {
            Local helper = (Local)expToHelper.get(rhs);
            ((AssignStmt)currentUnit).setRightOp(helper);
            //	    System.out.print(".");
            counterInserts++;
          }
        }
      }
    }
    if(Main.isVerbose)
      System.out.println("[" + b.getMethod().getName() +
                         "]     Lazy Code Motion done!. [" + counterInserts + 
                         ", " + counterNewInserts + "," + counterUses + "," +
                         unrolledConditions + "]");
    counterGlobalInserts += counterInserts;
    counterGlobalNewInserts += counterNewInserts;
    counterGlobalUses += counterUses;
  }
}
