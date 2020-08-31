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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Singletons;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.jimple.ConditionExpr;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.StmtBody;
import soot.jimple.SwitchStmt;
import soot.options.Options;
import soot.util.Chain;

public class UnconditionalBranchFolder extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(UnconditionalBranchFolder.class);

  public UnconditionalBranchFolder(Singletons.Global g) {
  }

  public static UnconditionalBranchFolder v() {
    return G.v().soot_jimple_toolkits_scalar_UnconditionalBranchFolder();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    StmtBody body = (StmtBody) b;

    if (Options.v().verbose()) {
      logger.debug("[" + body.getMethod().getName() + "] Folding unconditional branches...");
    }
    int iter = 0;
    Result res;
    do {
      res = transform(body);
      if (Options.v().verbose()) {
        iter++;
        logger.debug("[" + body.getMethod().getName() + "]     " + res.getNumFixed(BranchType.TOTAL_COUNT) + " of "
            + res.getNumFound(BranchType.TOTAL_COUNT) + " branches folded in iteration " + iter + ".");
      }
    } while (res.modified);
  }

  private static enum BranchType {
    TOTAL_COUNT,
    GOTO_GOTO,
    IF_TO_GOTO,
    GOTO_IF,
    IF_TO_IF,
    IF_SAME_TARGET,
    GOTO_SUCCESSOR;
  } // BranchType

  private static class HandleRes {

    final BranchType type;
    final boolean fixed;

    public HandleRes(BranchType type, boolean fixed) {
      this.type = type;
      this.fixed = fixed;
    }
  }

  private static class Result {

    private final int[] numFound = new int[BranchType.values().length];
    private final int[] numFixed = new int[BranchType.values().length];
    private boolean modified;

    public void updateCounters(HandleRes r) {
      updateCounters(r.type, r.fixed);
    }

    public void updateCounters(BranchType type, boolean fixed) {
      final int indexTotal = BranchType.TOTAL_COUNT.ordinal();
      final int indexUpdate = type.ordinal();
      final boolean updatingTotal = indexUpdate == indexTotal;

      if (updatingTotal && fixed) {
        throw new IllegalArgumentException("Cannot mark TOTAL as fixed!");
      }

      numFound[indexTotal]++;
      if (!updatingTotal) {
        numFound[indexUpdate]++;
      }
      if (fixed) {
        modified = true;
        numFixed[indexTotal]++;
        numFixed[indexUpdate]++;
      }
    }

    public int getNumFound(BranchType type) {
      final int indexCurrType = type.ordinal();
      return numFound[indexCurrType];
    }

    public int getNumFixed(BranchType type) {
      final int indexCurrType = type.ordinal();
      return numFixed[indexCurrType];
    }
  } // Result

  private static Result transform(StmtBody body) {
    Result res = new Result();
    Map<Stmt, Stmt> stmtMap = new HashMap<Stmt, Stmt>();
    Chain<Unit> units = body.getUnits();

    NextUnit: for (Iterator<Unit> stmtIt = units.iterator(); stmtIt.hasNext();) {
      Unit stmt = stmtIt.next();
      if (stmt instanceof GotoStmt) {
        final GotoStmt stmtAsGotoStmt = (GotoStmt) stmt;
        final Stmt gotoTarget = (Stmt) stmtAsGotoStmt.getTarget();

        // Handle special successor case
        if (stmtIt.hasNext()) {
          Unit successor = units.getSuccOf(stmt);
          // "goto [successor]" -> remove the statement
          if (successor == gotoTarget) {
            stmtIt.remove();
            res.updateCounters(BranchType.GOTO_SUCCESSOR, true);
            continue NextUnit;
          }
        }

        // Main handling for GotoStmt
        res.updateCounters(handle(stmtAsGotoStmt, gotoTarget, stmtMap));
      } else if (stmt instanceof IfStmt) {
        final IfStmt stmtAsIfStmt = (IfStmt) stmt;
        Stmt ifTarget = stmtAsIfStmt.getTarget();

        // Handle special successor cases
        if (stmtIt.hasNext()) {
          final Unit successor = units.getSuccOf(stmt);
          if (successor == ifTarget) {
            // "if C goto [successor]" -> remove the IfStmt
            stmtIt.remove();
            res.updateCounters(BranchType.IF_SAME_TARGET, true);
            continue NextUnit;
          } else if (successor instanceof GotoStmt) {
            final GotoStmt succAsGoto = (GotoStmt) successor;
            final Stmt gotoTarget = (Stmt) succAsGoto.getTarget();
            if (gotoTarget == ifTarget) {
              // "if C goto X";"goto X" -> remove the IfStmt
              stmtIt.remove();
              res.updateCounters(BranchType.IF_SAME_TARGET, true);
              continue NextUnit;
            } else {
              final Unit afterSuccessor = units.getSuccOf(successor);
              if (afterSuccessor == ifTarget) {
                // "if C goto Y";"goto X";"Y" -> "if !C goto X";"goto Y";"Y"
                Value oldCondition = stmtAsIfStmt.getCondition();
                assert (oldCondition instanceof ConditionExpr);// JIfStmt forces this
                stmtAsIfStmt.setCondition(reverseCondition((ConditionExpr) oldCondition));
                succAsGoto.setTarget(ifTarget);
                stmtAsIfStmt.setTarget(gotoTarget);
                ifTarget = gotoTarget;
                // NOTE: No need to remove the goto [successor] because it
                // is processed by the next iteration of the main loop.
                // NOTE: Nothing is removed here, it is a simple refactoring.
                // Thus, it can fall through to the main handler below where
                // the condition (and possible removal) will be counted.
              }
            }
          }
        }

        // Main handling for IfStmt
        res.updateCounters(handle(stmtAsIfStmt, ifTarget, stmtMap));
      } else if (stmt instanceof SwitchStmt) {
        final SwitchStmt stmtAsSwitchStmt = (SwitchStmt) stmt;
        for (UnitBox ub : stmtAsSwitchStmt.getUnitBoxes()) { // includes all cases and default
          Stmt caseTarget = (Stmt) ub.getUnit();
          if (caseTarget instanceof GotoStmt) {
            // "goto [goto X]" -> "goto X"
            Stmt newTarget = getFinalTarget(caseTarget, stmtMap);
            if (newTarget == null) {
              res.updateCounters(BranchType.GOTO_GOTO, false);
            } else {
              ub.setUnit(newTarget);
              res.updateCounters(BranchType.GOTO_GOTO, true);
            }
          }
        }
      }
    }
    return res;
  } // transform

  // NOTE: factored out to ensure all cases return a result and thus are counted
  private static HandleRes handle(GotoStmt gotoStmt, Stmt target, Map<Stmt, Stmt> stmtMap) {
    assert (gotoStmt.getTarget() == target);// pre-conditions
    if (target instanceof GotoStmt) {
      // "goto [goto X]" -> "goto X"
      Stmt newTarget = getFinalTarget(target, stmtMap);
      if (newTarget == null) {
        newTarget = gotoStmt;
      }
      if (newTarget == target) {
        return new HandleRes(BranchType.GOTO_GOTO, false);
      } else {
        gotoStmt.setTarget(newTarget);
        return new HandleRes(BranchType.GOTO_GOTO, true);
      }
    } else if (target instanceof IfStmt) {
      // "goto [if ...]" -> no change
      return new HandleRes(BranchType.GOTO_IF, false);
    } else {
      return new HandleRes(BranchType.TOTAL_COUNT, false);
    }
  }

  // NOTE: factored out to ensure all cases return a result and thus are counted
  private static HandleRes handle(IfStmt ifStmt, Stmt target, Map<Stmt, Stmt> stmtMap) {
    assert (ifStmt.getTarget() == target);// pre-conditions
    if (target instanceof GotoStmt) {
      // "if C goto [goto X]" -> "if C goto X"
      Stmt newTarget = getFinalTarget(target, stmtMap);
      if (newTarget == null) {
        newTarget = ifStmt;
      }
      if (newTarget == target) {
        return new HandleRes(BranchType.IF_TO_GOTO, false);
      } else {
        ifStmt.setTarget(newTarget);
        return new HandleRes(BranchType.IF_TO_GOTO, true);
      }
    } else if (target instanceof IfStmt) {
      // "if C goto [if C goto X]" -> "if C goto X"
      final IfStmt targetAsIfStmt = (IfStmt) target;
      // Perform "jump threading" optimization. If the target IfStmt
      // has the same condition as the first IfStmt, then the first
      // should jump directly to the target of the target IfStmt.
      // TODO: This could also be done when the first condition
      // implies the second but that's obviously more complicated
      // to check. Could even do something if the first implies
      // the negation of the second.
      if (ifStmt.getCondition().equivTo(targetAsIfStmt.getCondition())) {
        ifStmt.setTarget(targetAsIfStmt.getTarget());
        return new HandleRes(BranchType.IF_TO_IF, true);
      } else {
        return new HandleRes(BranchType.IF_TO_IF, false);
      }
    } else {
      return new HandleRes(BranchType.TOTAL_COUNT, false);
    }
  }

  /**
   * @param stmt
   * @param stmtMap
   *
   * @return the given {@link Stmt} if not a {@link GotoStmt}, otherwise, the final transitive target of the {@link GotoStmt}
   *         or {@code null} if that target is itselfF
   */
  private static Stmt getFinalTarget(Stmt stmt, Map<Stmt, Stmt> stmtMap) {
    // if not a goto, this is the final target
    if (!(stmt instanceof GotoStmt)) {
      return stmt;
    }

    // first map this statement to itself, so we can detect cycles
    stmtMap.put(stmt, stmt);

    Stmt target = (Stmt) ((GotoStmt) stmt).getTarget();

    // check if target is in statement map
    Stmt finalTarget;
    if (stmtMap.containsKey(target)) {
      // see if it maps to itself
      finalTarget = stmtMap.get(target);
      if (finalTarget == target) { // this is part of a cycle
        finalTarget = null;
      }
    } else {
      finalTarget = getFinalTarget(target, stmtMap);
    }

    stmtMap.put(stmt, finalTarget);
    return finalTarget;
  } // getFinalTarget

  public static ConditionExpr reverseCondition(ConditionExpr cond) {
    // NOTE: Adapted from the private reverseCondition(..) method in JimpleBodyBuilder.
    ConditionExpr newExpr;
    if (cond instanceof soot.jimple.EqExpr) {
      newExpr = Jimple.v().newNeExpr(cond.getOp1(), cond.getOp2());
    } else if (cond instanceof soot.jimple.NeExpr) {
      newExpr = Jimple.v().newEqExpr(cond.getOp1(), cond.getOp2());
    } else if (cond instanceof soot.jimple.GtExpr) {
      newExpr = Jimple.v().newLeExpr(cond.getOp1(), cond.getOp2());
    } else if (cond instanceof soot.jimple.GeExpr) {
      newExpr = Jimple.v().newLtExpr(cond.getOp1(), cond.getOp2());
    } else if (cond instanceof soot.jimple.LtExpr) {
      newExpr = Jimple.v().newGeExpr(cond.getOp1(), cond.getOp2());
    } else if (cond instanceof soot.jimple.LeExpr) {
      newExpr = Jimple.v().newGtExpr(cond.getOp1(), cond.getOp2());
    } else {
      throw new RuntimeException("Unknown ConditionExpr");
    }

    newExpr.getOp1Box().addAllTagsOf(cond.getOp1Box());
    newExpr.getOp2Box().addAllTagsOf(cond.getOp2Box());
    return newExpr;
  }
}
