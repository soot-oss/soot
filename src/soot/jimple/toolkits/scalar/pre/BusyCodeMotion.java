/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Florian Loitsch
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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
import soot.options.*;
import soot.jimple.toolkits.graph.*;
import soot.jimple.toolkits.scalar.*;
import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;
import soot.jimple.toolkits.pointer.PASideEffectTester;
import soot.options.BCMOptions;

/** 
 * Performs a partial redundancy elimination (= code motion). This is
 * done, by moving <b>every</b>computation as high as possible (it is
 * easy to show, that they are computationally optimal), and then
 * replacing the original computation by a reference to this new high
 * computation. This implies, that we introduce <b>many</b> new
 * helper-variables (that can easily be eliminated afterwards).<br> In
 * order to catch every redundant expression, this transformation must
 * be done on a graph without critical edges. Therefore the first
 * thing we do, is removing them. A subsequent pass can then easily
 * remove the synthetic nodes we have introduced.<br> The term "busy"
 * refers to the fact, that we <b>always</b> move computations as high
 * as possible. Even, if this is not necessary.
 *
 * @see soot.jimple.toolkits.graph.CriticalEdgeRemover
 */
public class BusyCodeMotion extends BodyTransformer {
    public BusyCodeMotion( Singletons.Global g ) {}
    public static BusyCodeMotion v() { return G.v().soot_jimple_toolkits_scalar_pre_BusyCodeMotion(); }

  private static final String PREFIX = "$bcm";

  /**
   * performs the busy code motion.
   */
  protected void internalTransform(Body b, String phaseName, Map opts) {
    BCMOptions options = new BCMOptions( opts );
    int counter = 0;
    HashMap expToHelper = new HashMap();
    Chain unitChain = b.getUnits();

    if(Options.v().verbose())
      G.v().out.println("[" + b.getMethod().getName() +
          "]     performing Busy Code Motion...");

    CriticalEdgeRemover.v().transform(b, phaseName + ".cer");

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
    SideEffectTester sideEffect;
    if( Scene.v().hasCallGraph() && !options.naive_side_effect() ) {
        sideEffect = new PASideEffectTester();
    } else {
        sideEffect = new NaiveSideEffectTester();
    }
    sideEffect.newMethod( b.getMethod() );
    UpSafetyAnalysis upSafe = new UpSafetyAnalysis(graph, unitToEquivRhs,
            sideEffect );
    DownSafetyAnalysis downSafe = new DownSafetyAnalysis(graph,
        unitToNoExceptionEquivRhs, sideEffect );
    EarliestnessComputation earliest = new EarliestnessComputation(graph,
        upSafe, downSafe, sideEffect );

    LocalCreation localCreation = new LocalCreation(b.getLocals(), PREFIX);

    Iterator unitIt = unitChain.snapshotIterator();

    { /* insert the computations at the earliest positions */
      while (unitIt.hasNext()) {
        Unit currentUnit = (Unit)unitIt.next();
        Iterator earliestIt =
          ((FlowSet)earliest.getFlowBefore(currentUnit)).iterator();
        while (earliestIt.hasNext()) {
          EquivalentValue equiVal = (EquivalentValue)earliestIt.next();
          Value exp = equiVal.getValue();
          /* get the unic helper-name for this expression */
          Local helper = (Local)expToHelper.get(equiVal);
          if (helper == null) {
            helper = localCreation.newLocal(equiVal.getType());
            expToHelper.put(equiVal, helper);
          }

          /* insert a new Assignment-stmt before the currentUnit */
          Value insertValue = Jimple.cloneIfNecessary(equiVal.getValue());
          Unit firstComp = Jimple.v().newAssignStmt(helper, insertValue);
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
    if(Options.v().verbose())
      G.v().out.println("[" + b.getMethod().getName() +
          "]     Busy Code Motion done!");
  }
}
