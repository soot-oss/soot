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


package soot.jimple.toolkits.scalar.pre;
import soot.jimple.toolkits.graph.*;
import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;

/** 
 * performs a partial redundancy elimination (= code motion). This is done, by
 * moving <b>every</b>computation as high as possible (it is easy to show, that they are
 * computationally optimal), and then replacing the original computation by a
 * reference to this new high computation. This implies, that we introduce
 * <b>many</b> new helper-variables (that can easily be eliminated
 * afterwards).<br>
 * In order to catch every redundant expression, this transformation must be
 * done on a graph without critical edges. Therefore the first thing we do, is
 * removing them. A subsequent pass can then easily remove the synthetic nodes
 * we have introduced.<br>
 * The term "busy" refers to the fact, that we <b>always</b> move computations
 * as high as possible. Even, if this is not necessary.
 *
 * @see soot.jimple.toolkits.graph.CriticalEdgeRemover
 */
public class BusyCodeMotion extends BodyTransformer {
  private static final String PREFIX = "$bcm";

  private static BusyCodeMotion instance = new BusyCodeMotion();
  private BusyCodeMotion() {}

  public static BusyCodeMotion v() { return instance; }

  public String getDeclaredOptions() { return super.getDeclaredOptions(); }

  public String getDefaultOptions() { return ""; }

  /**
   * performs the busy code motion.
   */
  protected void internalTransform(Body b, String phaseName, Map options) {
    int counter = 0;
    HashMap expToHelper = new HashMap();
    Chain unitChain = b.getUnits();

    if(Main.isVerbose)
      System.out.println("[" + b.getMethod().getName() +
          "]     performing Busy Code Motion...");

    CriticalEdgeRemover.removeCriticalEdges(b);

    UnitGraph graph = new BriefUnitGraph(b);

    Map unitToEquivRhs = new UnitMap(b, graph.size() + 1, 0.7f) {
      protected Object mapTo(Unit unit) {
        Value tmp = SootFilter.noInvokeRhs(unit);
        return SootFilter.equiVal(SootFilter.noLocal(tmp));
      }
    };
      //new UnitEquivRHSMap(b, graph.size() + 1, 0.7f);

    Map unitToNoExceptionEquivRhs = new UnitMap(b, graph.size() + 1, 0.7f) {
      protected Object mapTo(Unit unit) {
        Value tmp = SootFilter.noExceptionThrowingRhs(unit);
        return SootFilter.equiVal(SootFilter.noLocal(tmp));
      }
    };

    UpSafetyAnalysis upSafe = new UpSafetyAnalysis(graph, unitToEquivRhs);
    DownSafetyAnalysis downSafe = new DownSafetyAnalysis(graph,
        unitToNoExceptionEquivRhs);
    EarliestnessComputation earliest = new EarliestnessComputation(graph,
        upSafe, downSafe);

    Iterator unitIt = unitChain.snapshotIterator();

    { /* insert the computations at the earliest positions */
      while (unitIt.hasNext()) {
        Unit currentUnit = (Unit)unitIt.next();
        Iterator earliestIt =
          ((List)earliest.getEarliestBefore(currentUnit)).iterator();
        while (earliestIt.hasNext()) {
          EquivalentValue equiVal = (EquivalentValue)earliestIt.next();
          Value exp = equiVal.getValue();
          /* get the unic helper-name for this expression */
          Local helper = (Local)expToHelper.get(equiVal);
          if (helper == null) {
            String helperName = PREFIX + counter++;
            helper = Jimple.v().newLocal(helperName,
                Type.toMachineType(exp.getType()));
            b.getLocals().add(helper);
            expToHelper.put(equiVal, helper);
          }

          /* insert a new Assignment-stmt before the currentUnit */
          Unit firstComp = Jimple.v().newAssignStmt(helper, equiVal.getValue());
          unitChain.insertBefore(firstComp, currentUnit);
        }
      }
    }

    { /* replace old computations by the helper-vars */
      unitIt = unitChain.iterator();
      while (unitIt.hasNext()) {
        Unit currentUnit = (Unit)unitIt.next();
        EquivalentValue rhs = (EquivalentValue)unitToEquivRhs.get(currentUnit);
        if (rhs != null) {
          Local helper = (Local)expToHelper.get(rhs);
          if (helper != null)
            ((AssignStmt)currentUnit).setRightOp(helper);
        }
      }
    }
    if(Main.isVerbose)
      System.out.println("[" + b.getMethod().getName() +
          "]     Busy Code Motion done!");
  }
}
