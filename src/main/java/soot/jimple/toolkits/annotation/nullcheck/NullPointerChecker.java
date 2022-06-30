package soot.jimple.toolkits.annotation.nullcheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.PhaseOptions;
import soot.Scene;
import soot.Singletons;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LengthExpr;
import soot.jimple.MonitorStmt;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soot.jimple.toolkits.annotation.tags.NullCheckTag;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.util.Chain;

/*
ArrayRef
GetField
PutField
InvokeVirtual
InvokeSpecial
InvokeInterface
ArrayLength
-	AThrow
-	MonitorEnter
-	MonitorExit
*/

public class NullPointerChecker extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(NullPointerChecker.class);

  public NullPointerChecker(Singletons.Global g) {
  }

  public static NullPointerChecker v() {
    return G.v().soot_jimple_toolkits_annotation_nullcheck_NullPointerChecker();
  }

  @Override
  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    final boolean isProfiling = PhaseOptions.getBoolean(options, "profiling");
    final boolean enableOther = !PhaseOptions.getBoolean(options, "onlyarrayref");

    final Date start = new Date();
    if (Options.v().verbose()) {
      logger.debug("[npc] Null pointer check for " + body.getMethod().getName() + " started on " + start);
    }

    final BranchedRefVarsAnalysis analysis
        = new BranchedRefVarsAnalysis(ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body));

    final SootMethod increase
        = isProfiling ? Scene.v().loadClassAndSupport("MultiCounter").getMethod("void increase(int)") : null;

    final Chain<Unit> units = body.getUnits();
    for (Iterator<Unit> stmtIt = units.snapshotIterator(); stmtIt.hasNext();) {
      Stmt s = (Stmt) stmtIt.next();

      Value obj = null;
      if (s.containsArrayRef()) {
        obj = s.getArrayRef().getBase();
      } else if (enableOther) {
        // Throw
        if (s instanceof ThrowStmt) {
          obj = ((ThrowStmt) s).getOp();
        } else if (s instanceof MonitorStmt) {
          // Monitor enter and exit
          obj = ((MonitorStmt) s).getOp();
        } else {
          for (ValueBox vBox : s.getDefBoxes()) {
            Value v = vBox.getValue();
            // putfield, and getfield
            if (v instanceof InstanceFieldRef) {
              obj = ((InstanceFieldRef) v).getBase();
              break;
            } else if (v instanceof InstanceInvokeExpr) {
              // invokevirtual, invokespecial, invokeinterface
              obj = ((InstanceInvokeExpr) v).getBase();
              break;
            } else if (v instanceof LengthExpr) {
              // arraylength
              obj = ((LengthExpr) v).getOp();
              break;
            }
          }
          for (ValueBox vBox : s.getUseBoxes()) {
            Value v = vBox.getValue();
            // putfield, and getfield
            if (v instanceof InstanceFieldRef) {
              obj = ((InstanceFieldRef) v).getBase();
              break;
            } else if (v instanceof InstanceInvokeExpr) {
              // invokevirtual, invokespecial, invokeinterface
              obj = ((InstanceInvokeExpr) v).getBase();
              break;
            } else if (v instanceof LengthExpr) {
              // arraylength
              obj = ((LengthExpr) v).getOp();
              break;
            }
          }
        }
      }

      // annotate it or now
      if (obj != null) {
        boolean needCheck = (analysis.anyRefInfo(obj, analysis.getFlowBefore(s)) != BranchedRefVarsAnalysis.kNonNull);
        if (isProfiling) {
          final int count = needCheck ? 5 : 6;
          final Jimple jimp = Jimple.v();
          units.insertBefore(jimp.newInvokeStmt(jimp.newStaticInvokeExpr(increase.makeRef(), IntConstant.v(count))), s);
        }
        s.addTag(new NullCheckTag(needCheck));
      }
    }

    if (Options.v().verbose()) {
      Date finish = new Date();
      long runtime = finish.getTime() - start.getTime();
      long mins = runtime / 60000;
      long secs = (runtime % 60000) / 1000;
      logger.debug("[npc] Null pointer checker finished. It took " + mins + " mins and " + secs + " secs.");
    }
  }
}
