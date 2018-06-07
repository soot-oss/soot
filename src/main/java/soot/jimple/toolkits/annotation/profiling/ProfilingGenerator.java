package soot.jimple.toolkits.annotation.profiling;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Feng Qian
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
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Scene;
import soot.Singletons;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.options.ProfilingOptions;
import soot.util.Chain;

public class ProfilingGenerator extends BodyTransformer {
  public ProfilingGenerator(Singletons.Global g) {
  }

  public static ProfilingGenerator v() {
    return G.v().soot_jimple_toolkits_annotation_profiling_ProfilingGenerator();
  }

  public String mainSignature = "void main(java.lang.String[])";

  // private String mainSignature = "long runBenchmark(java.lang.String[])";

  protected void internalTransform(Body body, String phaseName, Map opts) {
    ProfilingOptions options = new ProfilingOptions(opts);
    if (options.notmainentry()) {
      mainSignature = "long runBenchmark(java.lang.String[])";
    }

    {
      SootMethod m = body.getMethod();

      SootClass counterClass = Scene.v().loadClassAndSupport("MultiCounter");
      SootMethod reset = counterClass.getMethod("void reset()");
      SootMethod report = counterClass.getMethod("void report()");

      boolean isMainMethod = m.getSubSignature().equals(mainSignature);

      Chain units = body.getUnits();

      if (isMainMethod) {
        units.addFirst(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(reset.makeRef())));
      }

      Iterator stmtIt = body.getUnits().snapshotIterator();
      while (stmtIt.hasNext()) {
        Stmt stmt = (Stmt) stmtIt.next();

        if (stmt instanceof InvokeStmt) {
          InvokeExpr iexpr = ((InvokeStmt) stmt).getInvokeExpr();

          if (iexpr instanceof StaticInvokeExpr) {
            SootMethod tempm = ((StaticInvokeExpr) iexpr).getMethod();

            if (tempm.getSignature().equals("<java.lang.System: void exit(int)>")) {
              units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(report.makeRef())), stmt);

            }
          }
        } else if (isMainMethod && (stmt instanceof ReturnStmt || stmt instanceof ReturnVoidStmt)) {
          units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(report.makeRef())), stmt);
        }
      }
    }
  }
}
