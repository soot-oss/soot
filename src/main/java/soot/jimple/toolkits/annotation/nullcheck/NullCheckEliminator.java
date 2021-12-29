package soot.jimple.toolkits.annotation.nullcheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Ganesh Sittampalam
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

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Immediate;
import soot.Unit;
import soot.Value;
import soot.jimple.BinopExpr;
import soot.jimple.EqExpr;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;
import soot.jimple.NeExpr;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;

public class NullCheckEliminator extends BodyTransformer {

  public static class AnalysisFactory {
    public NullnessAnalysis newAnalysis(UnitGraph g) {
      return new NullnessAnalysis(g);
    }
  }

  private AnalysisFactory analysisFactory;

  public NullCheckEliminator() {
    this(new AnalysisFactory());
  }

  public NullCheckEliminator(AnalysisFactory f) {
    this.analysisFactory = f;
  }

  @Override
  public void internalTransform(Body body, String phaseName, Map<String, String> options) {
    // really, the analysis should be able to use its own results to determine
    // that some branches are dead, but since it doesn't we just iterate.
    boolean changed;
    do {
      changed = false;

      final NullnessAnalysis analysis
          = analysisFactory.newAnalysis(ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body));
      final Chain<Unit> units = body.getUnits();
      for (Unit u = units.getFirst(); u != null; u = units.getSuccOf(u)) {
        if (u instanceof IfStmt) {
          final IfStmt is = (IfStmt) u;
          final Value c = is.getCondition();
          if (!(c instanceof EqExpr || c instanceof NeExpr)) {
            continue;
          }
          final BinopExpr e = (BinopExpr) c;
          final Immediate i;
          if (e.getOp2() instanceof NullConstant) {
            i = (Immediate) e.getOp1();
          } else if (e.getOp1() instanceof NullConstant) {
            i = (Immediate) e.getOp2();
          } else {
            i = null;
          }
          if (i != null) {
            int elim = 0; // -1 => condition is false, 1 => condition is true
            if (analysis.isAlwaysNonNullBefore(u, i)) {
              elim = c instanceof EqExpr ? -1 : 1;
            }
            if (analysis.isAlwaysNullBefore(u, i)) {
              elim = c instanceof EqExpr ? 1 : -1;
            }
            Stmt newstmt;
            switch (elim) {
              case -1:
                newstmt = Jimple.v().newNopStmt();
                break;
              case 1:
                newstmt = Jimple.v().newGotoStmt(is.getTarget());
                break;
              default:
                continue;
            }
            assert (newstmt != null);
            units.swapWith(u, newstmt);
            u = newstmt;
            changed = true;
          }
        }
      }
    } while (changed);
  }
}