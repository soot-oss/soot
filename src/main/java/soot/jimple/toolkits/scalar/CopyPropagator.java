package soot.jimple.toolkits.scalar;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.NullType;
import soot.RefLikeType;
import soot.Scene;
import soot.Singletons;
import soot.Timers;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.options.CPOptions;
import soot.options.Options;
import soot.shimple.PhiExpr;
import soot.tagkit.TagManager;
import soot.toolkits.exceptions.ThrowAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.toolkits.graph.PseudoTopologicalOrderer;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.ValueUnitPair;

public class CopyPropagator extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(CopyPropagator.class);

  protected ThrowAnalysis throwAnalysis = null;
  protected boolean forceOmitExceptingUnitEdges = false;

  public CopyPropagator(Singletons.Global g) {
  }

  public CopyPropagator(ThrowAnalysis ta) {
    this.throwAnalysis = ta;
  }

  public CopyPropagator(ThrowAnalysis ta, boolean forceOmitExceptingUnitEdges) {
    this.throwAnalysis = ta;
    this.forceOmitExceptingUnitEdges = forceOmitExceptingUnitEdges;
  }

  public static CopyPropagator v() {
    return G.v().soot_jimple_toolkits_scalar_CopyPropagator();
  }

  /**
   * Cascaded copy propagator.
   *
   * <p>
   * If it encounters situations of the form: A: a = ...; B: ... x = a; C:... use (x); where a has only one definition, and x
   * has only one definition (B), then it can propagate immediately without checking between B and C for redefinitions of a
   * (namely) A because they cannot occur. In this case the propagator is global.
   *
   * <p>
   * Otherwise, if a has multiple definitions then it only checks for redefinitions of Propagates constants and copies in
   * extended basic blocks.
   *
   * <p>
   * Does not propagate stack locals when the "only-regular-locals" option is true.
   */
  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> opts) {

    Options o = Options.v();
    if (o.verbose()) {
      logger.debug("[" + b.getMethod().getName() + "] Propagating copies...");
    }

    if (o.time()) {
      Timers.v().propagatorTimer.start();
    }

    // Count number of definitions for each local.
    Map<Local, Integer> localToDefCount = new HashMap<Local, Integer>(b.getLocalCount() * 2 + 1);
    for (Unit u : b.getUnits()) {
      if (u instanceof DefinitionStmt) {
        DefinitionStmt def = ((DefinitionStmt) u);
        Value leftOp = def.getLeftOp();
        if (leftOp instanceof Local) {
          Local loc = (Local) leftOp;

          Integer old = localToDefCount.get(loc);
          Value rop = def.getRightOp();
          int count = 1;
          if (rop instanceof PhiExpr) {
            PhiExpr e = (PhiExpr) rop;
            count = e.getArgCount();
          }
          localToDefCount.put(loc, (old == null) ? count : (old + count));
        }
      }
    }

    if (throwAnalysis == null) {
      throwAnalysis = Scene.v().getDefaultThrowAnalysis();
    }

    if (!forceOmitExceptingUnitEdges) {
      forceOmitExceptingUnitEdges = o.omit_excepting_unit_edges();
    }

    {
      // Go through the definitions, building the webs
      int fastCopyPropagationCount = 0;
      int slowCopyPropagationCount = 0;

      ExceptionalUnitGraph graph
          = ExceptionalUnitGraphFactory.createExceptionalUnitGraph(b, throwAnalysis, forceOmitExceptingUnitEdges);
      LocalDefs localDefs = G.v().soot_toolkits_scalar_LocalDefsFactory().newLocalDefs(graph);
      CPOptions options = new CPOptions(opts);
      boolean onlyRegularLocals = options.only_regular_locals();
      boolean onlyStackLocals = options.only_stack_locals();
      boolean allLocals = onlyRegularLocals && onlyStackLocals;
      boolean isDotNet = o.src_prec() == Options.src_prec_dotnet;
      TagManager tagManager = TagManager.v();

      // Perform a local propagation pass.
      for (Unit u : (new PseudoTopologicalOrderer<Unit>()).newList(graph, false)) {
        nextUseBox: for (Iterator<ValueBox> iterator = u.getUseBoxesIterator(); iterator.hasNext();) {
          ValueBox useBox = iterator.next();
          Value value = useBox.getValue();
          if (value instanceof Local) {
            Local l = (Local) value;

            // We force propagating nulls. If a target can only be
            // null due to typing, we always inline that constant.
            if (!allLocals && !(l.getType() instanceof NullType)) {
              if ((onlyRegularLocals && l.isStackLocal()) || (onlyStackLocals && !l.isStackLocal())) {
                continue;
              }
            }

            // We can propagate the definition if we either only have one definition
            // or all definitions are side-effect free and equal. For starters, we
            // only support constants in the case of multiple definitions.
            Iterator<Unit> defsOfUse = localDefs.getDefsOfAtIterator(l, u);
            Unit firstElement = defsOfUse.hasNext() ? defsOfUse.next() : null;
            boolean propagateDef = !defsOfUse.hasNext() && firstElement != null;
            Value rightOp = null;
            if (firstElement instanceof AssignStmt) {
              AssignStmt f = (AssignStmt) firstElement;
              rightOp = f.getRightOp();
              if (rightOp instanceof PhiExpr) {
                PhiExpr phi = (PhiExpr) rightOp;
                Value v = null;
                for (ValueUnitPair expr : phi.getArgs()) {
                  if (v == null) {
                    v = expr.getValue();
                  } else if (!v.equivTo(expr.getValue())) {
                    continue nextUseBox;
                  }
                }
                rightOp = v;
              }
            }
            if (!propagateDef && firstElement != null) {
              boolean agrees = false;
              Constant constVal = null;
              if (firstElement instanceof AssignStmt) {
                if (rightOp instanceof Constant) {
                  constVal = (Constant) rightOp;
                  agrees = true;
                }

              }
              if (agrees) {
                while (defsOfUse.hasNext()) {
                  Unit defUnit = defsOfUse.next();
                  boolean defAgrees = false;
                  if (defUnit instanceof AssignStmt) {
                    Value rightOpN = ((AssignStmt) defUnit).getRightOp();
                    if (rightOpN instanceof Constant) {
                      if (constVal == null) {
                        constVal = (Constant) rightOpN;
                        defAgrees = true;
                      } else if (constVal.equals(rightOpN)) {
                        defAgrees = true;
                      }
                    }
                  }
                  if (!defAgrees) {
                    agrees = false;
                    break;
                  }
                }
              }
              propagateDef = agrees;
            }

            if (propagateDef) {
              final DefinitionStmt def = (DefinitionStmt) firstElement;

              if (rightOp instanceof Constant) {
                if (ConstantPropagatorUtils.mayPropagate(graph, rightOp, def, u, useBox)) {
                  useBox.setValue(rightOp);
                  tagManager.copyLineTags(useBox, def);
                }

              } else if (rightOp instanceof CastExpr) {
                CastExpr ce = (CastExpr) rightOp;
                if (ce.getCastType() instanceof RefLikeType) {
                  Value op = ce.getOp();
                  if ((op instanceof IntConstant && ((IntConstant) op).value == 0)
                      || (op instanceof LongConstant && ((LongConstant) op).value == 0)) {
                    final NullConstant nc = NullConstant.v();
                    if (useBox.canContainValue(nc)) {
                      // for .NET, we cannot eliminate casts to enums, since we might lose information otherwise
                      // But even for non-casts, using 0 as a ref-like type is legal here
                      if (!isDotNet) {
                        useBox.setValue(nc);
                        tagManager.copyLineTags(useBox, def);
                      }
                    }
                  }
                }
              } else if (rightOp instanceof Local) {
                Local m = (Local) rightOp;
                if (l != m) {
                  Integer defCount = localToDefCount.get(m);
                  if (defCount == null || defCount == 0) {
                    throw new RuntimeException("Variable " + m + " used without definition!");
                  } else if (defCount == 1) {
                    useBox.setValue(m);
                    tagManager.copyLineTags(useBox, def);
                    fastCopyPropagationCount++;
                    continue;
                  }

                  if (!localDefs.hasDefsOfAt(m, u) || !localDefs.hasDefsOfAt(m, def)) {
                    // Use the slow approach
                    List<Unit> path = graph.getExtendedBasicBlockPathBetween(def, u);
                    if (path == null) {
                      // no path in the extended basic block
                      continue;
                    }

                    Iterator<Unit> pathIt = path.iterator();
                    // Skip first node
                    pathIt.next();
                    // Make sure that m is not redefined along path
                    while (pathIt.hasNext()) {
                      Stmt s = (Stmt) pathIt.next();

                      if (u == s) {
                        // Don't look at the last statement
                        // since it is evaluated after the uses.
                        break;
                      }
                      if (s instanceof DefinitionStmt) {
                        if (((DefinitionStmt) s).getLeftOp() == m) {
                          // was redefined
                          continue nextUseBox;
                        }
                      }
                    }

                  } else {
                    boolean agree = localDefs.doDefsAgreeAt(m, def, u);
                    if (!agree) {
                      // definitions disagree, there must be a definition in-between
                      continue;
                    }
                  }

                  useBox.setValue(m);
                  slowCopyPropagationCount++;
                }
              }
            }
          }
        }
      }

      if (o.verbose()) {
        logger.debug("[" + b.getMethod().getName() + "]     Propagated: " + fastCopyPropagationCount + " fast copies  "
            + slowCopyPropagationCount + " slow copies");
      }
    }

    if (o.time()) {
      Timers.v().propagatorTimer.end();
    }
  }
}
