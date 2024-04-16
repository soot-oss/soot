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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import soot.jimple.BranchableStmt;
import soot.jimple.ConditionExpr;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.StmtBody;
import soot.jimple.SwitchStmt;
import soot.options.Options;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;
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
    final boolean verbose = Options.v().verbose();

    if (verbose) {
      logger.debug("[" + b.getMethod().getName() + "] Folding unconditional branches...");
    }
    final Transformer transformer = new Transformer((StmtBody) b);
    int iter = 0;
    Result res;
    do {
      res = transformer.transform();
      if (verbose) {
        iter++;
        logger.debug("[" + b.getMethod().getName() + "]     " + res.getNumFixed(BranchType.TOTAL_COUNT) + " of "
            + res.getNumFound(BranchType.TOTAL_COUNT) + " branches folded in iteration " + iter + ".");
      }
    } while (res.modified);
  }

  private static enum BranchType {
    TOTAL_COUNT, GOTO_GOTO, IF_TO_GOTO, GOTO_IF, IF_TO_IF, IF_SAME_TARGET, GOTO_SUCCESSOR;
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

  private static class Transformer {

    private final Map<Stmt, Stmt> stmtMap;
    private final boolean isShimple;
    private final Chain<Unit> units;

    public Transformer(StmtBody body) {
      this.stmtMap = new HashMap<Stmt, Stmt>();
      this.isShimple = body instanceof ShimpleBody;
      this.units = body.getUnits();
    }

    public Result transform() {
      stmtMap.clear();// reset in case of multiple passes
      Result res = new Result();
      NextUnit: for (final Iterator<Unit> stmtIt = units.iterator(); stmtIt.hasNext();) {
        Unit stmt = stmtIt.next();
        if (stmt instanceof GotoStmt) {
          final GotoStmt stmtAsGotoStmt = (GotoStmt) stmt;
          final Stmt gotoTarget = (Stmt) stmtAsGotoStmt.getTarget();

          // Handle special successor case
          if (stmtIt.hasNext()) {
            Unit successor = units.getSuccOf(stmt);
            // "goto [successor]" -> remove the statement
            if (successor == gotoTarget) {
              if (!isShimple || removalIsSafeInShimple(stmt)) {
                stmtIt.remove();
                res.updateCounters(BranchType.GOTO_SUCCESSOR, true);
              } else {
                res.updateCounters(BranchType.GOTO_SUCCESSOR, false);
              }
              continue NextUnit;
            }
          }

          // Main handling for GotoStmt
          res.updateCounters(handle(stmtAsGotoStmt, gotoTarget));
        } else if (stmt instanceof IfStmt) {
          final IfStmt stmtAsIfStmt = (IfStmt) stmt;
          Stmt ifTarget = stmtAsIfStmt.getTarget();

          // Handle special successor cases
          if (stmtIt.hasNext()) {
            final Unit successor = units.getSuccOf(stmt);
            if (successor == ifTarget) {
              // "if C goto [successor]" -> remove the IfStmt
              if (!isShimple || removalIsSafeInShimple(stmt)) {
                stmtIt.remove();
                res.updateCounters(BranchType.IF_SAME_TARGET, true);
              } else {
                res.updateCounters(BranchType.IF_SAME_TARGET, false);
              }
              continue NextUnit;
            } else if (successor instanceof GotoStmt) {
              final GotoStmt succAsGoto = (GotoStmt) successor;
              final Stmt gotoTarget = (Stmt) succAsGoto.getTarget();
              if (gotoTarget == ifTarget) {
                // "if C goto X";"goto X" -> remove the IfStmt
                if (!isShimple || removalIsSafeInShimple(stmt)) {
                  stmtIt.remove();
                  res.updateCounters(BranchType.IF_SAME_TARGET, true);
                } else {
                  res.updateCounters(BranchType.IF_SAME_TARGET, false);
                }
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
                  // We need to check whether anyone has a goto to the "goto X", because this no has to also go
                  // to Y

                  // If we wouldn't do that, we would e.g. go from
                  // if $i0 == 6 goto label04;
                  // label03:
                  // goto label21;
                  // ...
                  // goto label3;

                  // to
                  // if $i0 != 6 goto label21;
                  // label03:
                  // goto label04;
                  // label04:
                  // ...
                  // goto label03;
                  // this would alter the semantics, since the previous go-tos now go to the other branch!
                  if (!succAsGoto.getBoxesPointingToThis().isEmpty()) {
                    // we cannot simply use getBoxesPointingToThis, because we do not want to update
                    // trap references
                    for (Unit i : units) {
                      if (i instanceof BranchableStmt) {
                        BranchableStmt b = (BranchableStmt) i;
                        if (b.getTarget() == succAsGoto) {
                          b.setTarget(gotoTarget);
                        }
                      }
                    }
                  }

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
          res.updateCounters(handle(stmtAsIfStmt, ifTarget));
        } else if (stmt instanceof SwitchStmt) {
          final SwitchStmt stmtAsSwitchStmt = (SwitchStmt) stmt;
          for (UnitBox ub : stmtAsSwitchStmt.getUnitBoxes()) { // includes all cases and default
            Stmt caseTarget = (Stmt) ub.getUnit();
            if (caseTarget instanceof GotoStmt) {
              // "goto [goto X]" -> "goto X"
              Stmt newTarget = getFinalTarget(caseTarget);
              if (newTarget == null || newTarget == caseTarget) {
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
    private HandleRes handle(GotoStmt gotoStmt, Stmt target) {
      assert (gotoStmt.getTarget() == target);// pre-conditions
      if (target instanceof GotoStmt) {
        // "goto [goto X]" -> "goto X"
        if (!isShimple || removalIsSafeInShimple(target)) {
          Stmt newTarget = getFinalTarget(target);
          if (newTarget == null) {
            newTarget = gotoStmt;
          }
          if (newTarget != target) {
            if (isShimple) {
              // NOTE: It is safe to redirect all PhiExpr pointers since removalIsSafeInShimple(..) ensures there is a single
              // predecessor for 'target' and we know 'gotoStmt' is that predecessor. Hence, 'target' becomes unreachable
              // which means any PhiExpr that had 'target' as a predecessor now has 'gotoStmt' as a predecessor instead.
              assert (hasNoPointersOrSingleJumpPred(target, gotoStmt));
              Shimple.redirectPointers(target, gotoStmt);
            }
            gotoStmt.setTarget(newTarget);
            return new HandleRes(BranchType.GOTO_GOTO, true);
          }
        }
        return new HandleRes(BranchType.GOTO_GOTO, false);
      } else if (target instanceof IfStmt) {
        // "goto [if ...]" -> no change
        return new HandleRes(BranchType.GOTO_IF, false);
      } else {
        return new HandleRes(BranchType.TOTAL_COUNT, false);
      }
    }

    // NOTE: factored out to ensure all cases return a result and thus are counted
    private HandleRes handle(final IfStmt ifStmt, final Stmt target) {
      assert (ifStmt.getTarget() == target);// pre-conditions
      if (target instanceof GotoStmt) {
        // "if C goto [goto X]" -> "if C goto X"
        if (!isShimple || removalIsSafeInShimple(target)) {
          Stmt newTarget = getFinalTarget(target);
          if (newTarget == null) {
            newTarget = ifStmt;
          }
          if (newTarget != target) { // skip if target would not change
            if (isShimple) {
              // NOTE: It is safe to redirect all PhiExpr pointers since removalIsSafeInShimple(..) ensures there is a single
              // predecessor for 'target' and we know 'ifStmt' is that predecessor. Hence, 'target' becomes unreachable which
              // means any PhiExpr that had 'target' as a predecessor now has 'ifStmt' as a predecessor instead.
              assert (hasNoPointersOrSingleJumpPred(target, ifStmt));
              Shimple.redirectPointers(target, ifStmt);
            }
            ifStmt.setTarget(newTarget);
            return new HandleRes(BranchType.IF_TO_GOTO, true);
          }
        }
        return new HandleRes(BranchType.IF_TO_GOTO, false);
      } else if (target instanceof IfStmt) {
        // "if C goto [if C goto X]" -> "if C goto X"
        if (ifStmt != target) { // skip when IfStmt jumps to itself
          if (!isShimple || removalIsSafeInShimple(target)) {
            final IfStmt targetAsIfStmt = (IfStmt) target;
            Stmt newTarget = targetAsIfStmt.getTarget();
            if (newTarget != target) { // skip if target would not change
              // Perform "jump threading" optimization. If the target IfStmt
              // has the same condition as the first IfStmt, then the first
              // should jump directly to the target of the target IfStmt.
              // TODO: This could also be done when the first condition
              // implies the second but that's obviously more complicated
              // to check. Could even do something if the first implies
              // the negation of the second.
              if (ifStmt.getCondition().equivTo(targetAsIfStmt.getCondition())) {
                if (isShimple) {
                  // NOTE: It is safe to redirect all PhiExpr pointers since removalIsSafeInShimple(..)
                  // ensures there is a single predecessor for 'target' and we know 'ifStmt' is that
                  // predecessor. Hence, 'target' becomes unreachable which means any PhiExpr that
                  // had 'target' as a predecessor now has 'ifStmt' as a predecessor instead.
                  assert (hasNoPointersOrSingleJumpPred(target, ifStmt));
                  Shimple.redirectPointers(target, ifStmt);
                }
                ifStmt.setTarget(newTarget);
                return new HandleRes(BranchType.IF_TO_IF, true);
              }
            }
          }
        }
        return new HandleRes(BranchType.IF_TO_IF, false);
      } else {
        return new HandleRes(BranchType.TOTAL_COUNT, false);
      }
    }

    /**
     * @param stmt
     * @param stmtMap
     *
     * @return the given {@link Stmt} if not a {@link GotoStmt}, otherwise, the final transitive target of the
     *         {@link GotoStmt} or {@code null} if that target is itself
     */
    private Stmt getFinalTarget(Stmt stmt) {
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
        finalTarget = getFinalTarget(target);
      }

      stmtMap.put(stmt, finalTarget);
      return finalTarget;
    }

    /**
     * Checks if removing the given {@link Unit} is "safe" when the body contains {@link soot.shimple.PhiExpr}. Specifically,
     * can {@link soot.shimple.Shimple#redirectToPreds(soot.Body, soot.Unit)} properly handle the phi back-reference update
     * after the {@link Unit} is removed (removal is trivially safe if there are no phi back-references to the Unit).
     * 
     * For instance, a jump cannot (easily) be removed if the Unit that jumps is the target of a Shimple back-reference and
     * has multiple graph predecessors. Removing it would leave the related PhiExpr without a reference for every predecessor
     * (i.e. the existing back-reference would move to whatever node is before the removed Unit in the Chain but any other
     * predecessor of the Unit would not have a back-reference in the PhiExpr).
     */
    private boolean removalIsSafeInShimple(Unit stmt) {
      // To check this condition without building a graph, first check if there exist any UnitBox pointing to the Unit such
      // that UnitBox.isBranchTarget() is false. That indicates the presence of a PhiExpr back-reference. In that case, count
      // the remaining boxes pointing to the Unit and add one (1) if the chain predecessor falls through.
      Unit chainPred = units.getPredOf(stmt);
      if (chainPred == null) {
        // Removing the head of the chain is safe.
        return true;
      }

      List<UnitBox> boxesPointingToThis = stmt.getBoxesPointingToThis();
      if (boxesPointingToThis.isEmpty()) {
        // Removal is safe when there are no UnitBox that reference the Unit
        // (because it implies that no PhiExpr/SUnitBox references the Unit).
        return true;
      }

      // Now, scan all UnitBox pointing to the current Unit and determine if there exists a phi back-reference pointing to
      // the current Unit. At the same time, collect the predecessors from the UnitBox references (i.e. jumps).
      boolean hasBackref = false;
      // NOTE: just counting won't work because fall-through pred could be an IfStmt and shouldn't be counted twice.
      HashSet<UnitBox> predBoxes = new HashSet<UnitBox>();
      for (UnitBox ub : boxesPointingToThis) {
        if (ub.isBranchTarget()) {
          assert (stmt == ub.getUnit());// sanity check
          predBoxes.add(ub);
        } else {
          assert (ub instanceof soot.shimple.internal.SUnitBox);// sanity check
          hasBackref = true;
        }
      }
      if (!hasBackref) {
        // Removal is safe when no PhiExpr/SUnitBox references the Unit.
        return true;
      }

      // Removal is safe if there is exactly one predecessor (either fall-through or jump)
      int predecessorCount = predBoxes.size();
      if (predecessorCount > 1) {
        return false;
      }
      // Add the fall-through predecessor (if applicable) and check the condition
      if (chainPred.fallsThrough()) {
        // Avoid counting it twice if the fall-through pred also jumps (i.e. was added above).
        if (Collections.disjoint(chainPred.getUnitBoxes(), predBoxes)) {
          predecessorCount++;
        }
      }
      return predecessorCount == 1;
    }

    private boolean hasNoPointersOrSingleJumpPred(Unit toRemove, Unit jumpPred) {
      boolean hasBackref = false;
      HashSet<UnitBox> predBoxes = new HashSet<UnitBox>();
      for (UnitBox ub : toRemove.getBoxesPointingToThis()) {
        if (ub.isBranchTarget()) {
          assert (toRemove == ub.getUnit());// sanity check
          predBoxes.add(ub);
        } else {
          assert (ub instanceof soot.shimple.internal.SUnitBox);// sanity check
          hasBackref = true;
        }
      }
      // Has no Phi pointers
      if (!hasBackref) {
        return true;
      }

      // Ensure there exists a single branching predecessor and it is 'jumpPred' itself
      if (predBoxes.size() != 1 || !jumpPred.getUnitBoxes().containsAll(predBoxes)) {
        return false;
      }

      // If the unit chain has a predecessor and it falls through, then 'jumpPred' is not the only predecessor
      Unit chainPred = units.getPredOf(toRemove);
      return chainPred == null || !chainPred.fallsThrough();
    }
  } // Transformer

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
