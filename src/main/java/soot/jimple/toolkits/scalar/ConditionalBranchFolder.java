package soot.jimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Phong Co
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Singletons;
import soot.Unit;
import soot.Value;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.StmtBody;
import soot.options.Options;
import soot.util.Chain;

public class ConditionalBranchFolder extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(ConditionalBranchFolder.class);

  public ConditionalBranchFolder(Singletons.Global g) {
  }

  public static ConditionalBranchFolder v() {
    return G.v().soot_jimple_toolkits_scalar_ConditionalBranchFolder();
  }

  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    StmtBody stmtBody = (StmtBody) body;

    int numTrue = 0, numFalse = 0;

    if (Options.v().verbose()) {
      logger.debug("[" + stmtBody.getMethod().getName() + "] Folding conditional branches...");
    }

    Chain<Unit> units = stmtBody.getUnits();

    for (Unit stmt : units.toArray(new Unit[units.size()])) {
      if (stmt instanceof IfStmt) {
        IfStmt ifs = (IfStmt) stmt;
        // check for constant-valued conditions
        Value cond = ifs.getCondition();
        if (Evaluator.isValueConstantValued(cond)) {
          cond = Evaluator.getConstantValueOf(cond);

          if (((IntConstant) cond).value == 1) {
            // if condition always true, convert if to goto
            Stmt newStmt = Jimple.v().newGotoStmt(ifs.getTarget());
            units.insertAfter(newStmt, stmt);
            numTrue++;
          } else {
            numFalse++;
          }

          // remove if
          units.remove(stmt);
        }
      }
    }

    if (Options.v().verbose()) {
      logger.debug(
          "[" + stmtBody.getMethod().getName() + "]     Folded " + numTrue + " true, " + numFalse + " conditional branches");
    }

  } // foldBranches

} // BranchFolder
