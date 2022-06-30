package soot.shimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.PhaseOptions;
import soot.Singletons;
import soot.Unit;
import soot.UnitBox;
import soot.UnitBoxOwner;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.Stmt;
import soot.jimple.TableSwitchStmt;
import soot.options.Options;
import soot.shimple.ShimpleBody;
import soot.shimple.toolkits.scalar.SEvaluator.BottomConstant;
import soot.shimple.toolkits.scalar.SEvaluator.MetaConstant;
import soot.shimple.toolkits.scalar.SEvaluator.TopConstant;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;
import soot.toolkits.scalar.Pair;
import soot.toolkits.scalar.UnitValueBoxPair;
import soot.util.Chain;

/**
 * A powerful constant propagator and folder based on an algorithm sketched by Cytron et al that takes conditional control
 * flow into account. This optimization demonstrates some of the benefits of SSA -- particularly the fact that Phi nodes
 * represent natural merge points in the control flow.
 *
 * @author Navindra Umanee
 * @see <a href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently Computing Static Single Assignment Form and
 *      the Control Dependence Graph</a>
 **/
public class SConstantPropagatorAndFolder extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(SConstantPropagatorAndFolder.class);

  public SConstantPropagatorAndFolder(Singletons.Global g) {
  }

  public static SConstantPropagatorAndFolder v() {
    return G.v().soot_shimple_toolkits_scalar_SConstantPropagatorAndFolder();
  }

  protected ShimpleBody sb;
  protected boolean debug;

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    if (!(b instanceof ShimpleBody)) {
      throw new RuntimeException("SConstantPropagatorAndFolder requires a ShimpleBody.");
    }
    ShimpleBody castBody = (ShimpleBody) b;
    if (!castBody.isSSA()) {
      throw new RuntimeException("ShimpleBody is not in proper SSA form as required by SConstantPropagatorAndFolder. "
          + "You may need to rebuild it or use ConstantPropagatorAndFolder instead.");
    }

    this.sb = castBody;
    this.debug = Options.v().debug() || castBody.getOptions().debug();

    if (Options.v().verbose()) {
      logger.debug("[" + castBody.getMethod().getName() + "] Propagating and folding constants (SSA)...");
    }

    // *** FIXME: What happens when Shimple is built with another UnitGraph?
    SCPFAnalysis scpf = new SCPFAnalysis(ExceptionalUnitGraphFactory.createExceptionalUnitGraph(castBody));
    propagateResults(scpf.getResults());
    if (PhaseOptions.getBoolean(options, "prune-cfg")) {
      removeStmts(scpf.getDeadStmts());
      replaceStmts(scpf.getStmtsToReplace());
    }
  }

  /**
   * Propagates constants to the definition and uses of the relevant locals given a mapping. Notice that we use the Shimple
   * implementation of LocalDefs and LocalUses.
   **/
  protected void propagateResults(Map<Local, Constant> localToConstant) {
    ShimpleLocalDefs localDefs = new ShimpleLocalDefs(sb);
    ShimpleLocalUses localUses = new ShimpleLocalUses(sb);

    for (Local local : sb.getLocals()) {
      Constant constant = localToConstant.get(local);
      if (constant instanceof MetaConstant) {
        continue;
      }
      // update definition
      {
        DefinitionStmt stmt = (DefinitionStmt) localDefs.getDefsOf(local).get(0);

        ValueBox defSrcBox = stmt.getRightOpBox();
        Value defSrcOld = defSrcBox.getValue();

        if (defSrcBox.canContainValue(constant)) {
          defSrcBox.setValue(constant);

          // remove dangling pointers
          if (defSrcOld instanceof UnitBoxOwner) {
            ((UnitBoxOwner) defSrcOld).clearUnitBoxes();
          }
        } else if (debug) {
          logger.warn("Couldn't propagate constant " + constant + " to box " + defSrcBox.getValue() + " in unit " + stmt);
        }
      }
      // update uses
      for (UnitValueBoxPair pair : localUses.getUsesOf(local)) {
        ValueBox useBox = pair.getValueBox();

        if (useBox.canContainValue(constant)) {
          useBox.setValue(constant);
        } else if (debug) {
          logger.warn(
              "Couldn't propagate constant " + constant + " to box " + useBox.getValue() + " in unit " + pair.getUnit());
        }
      }
    }
  }

  /**
   * Removes the given list of fall through IfStmts from the body.
   **/
  protected void removeStmts(List<IfStmt> deadStmts) {
    Chain<Unit> units = sb.getUnits();
    for (IfStmt dead : deadStmts) {
      units.remove(dead);
      dead.clearUnitBoxes();
    }
  }

  /**
   * Replaces conditional branches by unconditional branches as given by the mapping.
   **/
  protected void replaceStmts(Map<Stmt, GotoStmt> stmtsToReplace) {
    Chain<Unit> units = sb.getUnits();
    for (Map.Entry<Stmt, GotoStmt> e : stmtsToReplace.entrySet()) {
      // important not to call clearUnitBoxes() on key since
      // replacement uses the same UnitBox
      units.swapWith(e.getKey(), e.getValue());
    }
  }
}

/**
 * The actual branching flow analysis implementation. Briefly, a sketch of the sketch from the Cytron et al paper:
 *
 * <p>
 * Initially the algorithm assumes that each edge is unexecutable (the entry nodes are reachable) and that each variable is
 * constant with an unknown value, Top. Assumptions are corrected until they stabilise.
 *
 * <p>
 * For example, if <tt>q</tt> is found to be not a constant (Bottom) in <tt>if(q == 0) goto label1</tt> then both edges
 * leaving the the statement are considered executable, if <tt>q</tt> is found to be a constant then only one of the edges
 * are executable.
 *
 * <p>
 * Whenever a reachable definition statement such as "x = 3" is found, the information is propagated to all uses of x (this
 * works due to the SSA property).
 *
 * <p>
 * Perhaps the crucial point is that if a node such as <tt>x =
 * Phi(x_1, x_2)</tt> is ever found, information on <tt>x</tt> is assumed as follows:
 *
 * <ul>
 * <li>If <tt>x_1</tt> and <tt>x_2</tt> are the same assumed constant, <tt>x</tt> is assumed to be that constant. If they are
 * not the same constant, <tt>x</tt> is Bottom.</li>
 *
 * <li>If either one is Top and the other is a constant, <tt>x</tt> is assumed to be the same as the known constant.</li>
 *
 * <li>If either is Bottom, <tt>x</tt> is assumed to be Bottom.</li>
 * </ul>
 *
 * <p>
 * The crucial point about the crucial point is that if definitions of <tt>x_1</tt> or <tt>x_2</tt> are never reached, the
 * Phi node will still assume them to be Top and hence they will not influence the decision as to whether <tt>x</tt> is a
 * constant or not.
 **/
class SCPFAnalysis extends ForwardBranchedFlowAnalysis<FlowSet<Object>> {
  protected final static ArraySparseSet<Object> EMPTY_SET = new ArraySparseSet<Object>();

  /**
   * A mapping of the locals to their current assumed constant value (which may be Top or Bottom).
   **/
  protected final Map<Local, Constant> localToConstant;

  /**
   * A map from conditional branches to their possible replacement unit, an unconditional branch.
   **/
  protected final Map<Stmt, GotoStmt> stmtToReplacement;

  /**
   * A list of IfStmts that always fall through.
   **/
  protected final List<IfStmt> deadStmts;

  public SCPFAnalysis(UnitGraph graph) {
    super(graph);
    this.stmtToReplacement = new HashMap<Stmt, GotoStmt>();
    this.deadStmts = new ArrayList<IfStmt>();
    this.localToConstant = new HashMap<Local, Constant>(graph.size() * 2 + 1, 0.7f);

    // initialise localToConstant map -- assume all scalars are constant (Top)
    {
      Map<Local, Constant> ref = this.localToConstant;
      for (Local local : graph.getBody().getLocals()) {
        ref.put(local, TopConstant.v());
      }
    }

    doAnalysis();
  }

  /**
   * Returns the localToConstant map.
   **/
  public Map<Local, Constant> getResults() {
    return localToConstant;
  }

  /**
   * Returns the list of fall through IfStmts.
   **/
  public List<IfStmt> getDeadStmts() {
    return deadStmts;
  }

  /**
   * Returns a Map from conditional branches to the unconditional branches that can replace them.
   **/
  public Map<Stmt, GotoStmt> getStmtsToReplace() {
    return stmtToReplacement;
  }

  // *** NOTE: this is here because ForwardBranchedFlowAnalysis does
  // *** not handle exceptional control flow properly in the
  // *** dataflow analysis. this should be removed when
  // *** ForwardBranchedFlowAnalysis is fixed.
  @Override
  protected boolean treatTrapHandlersAsEntries() {
    return true;
  }

  /**
   * If a node has empty IN sets we assume that it is not reachable. Hence, we initialise the entry sets to be non-empty to
   * indicate that they are reachable.
   **/
  @Override
  protected FlowSet<Object> entryInitialFlow() {
    FlowSet<Object> entrySet = EMPTY_SET.emptySet();
    entrySet.add(TopConstant.v());
    return entrySet;
  }

  /**
   * All other nodes are assumed to be unreachable by default.
   **/
  @Override
  protected FlowSet<Object> newInitialFlow() {
    return EMPTY_SET.emptySet();
  }

  /**
   * Since we are interested in control flow from all branches, take the union.
   **/
  @Override
  protected void merge(FlowSet<Object> in1, FlowSet<Object> in2, FlowSet<Object> out) {
    in1.union(in2, out);
  }

  /**
   * Defer copy to FlowSet.
   **/
  @Override
  protected void copy(FlowSet<Object> source, FlowSet<Object> dest) {
    source.copy(dest);
  }

  /**
   * If a node has an empty in set, it is considered unreachable. Otherwise the node is examined and if any assumptions have
   * to be corrected, a Pair containing the corrected assumptions is flowed to the reachable nodes. If no assumptions have to
   * be corrected then no information other than the in set is propagated to the reachable nodes.
   *
   * <p>
   * Pair serves no other purpose than to keep the analysis flowing for as long as needed. The final results are accumulated
   * in the localToConstant map.
   **/
  @Override
  protected void flowThrough(FlowSet<Object> in, final Unit s, List<FlowSet<Object>> fallOut,
      List<FlowSet<Object>> branchOuts) {
    if (in.isEmpty()) {
      return; // not reachable
    }

    FlowSet<Object> fin = in.clone();

    // If s is a definition, check if any assumptions have to be corrected.
    Pair<Unit, Constant> pair = processDefinitionStmt(s);
    if (pair != null) {
      fin.add(pair);
    }

    // normal, non-branching statement
    if (!s.branches() && s.fallsThrough()) {
      for (FlowSet<Object> fallSet : fallOut) {
        fallSet.union(fin);
      }
      return;
    }

    /* determine which nodes are reachable. */

    boolean conservative = true;
    boolean fall = false;
    boolean branch = false;
    FlowSet<Object> oneBranch = null;

    if (s instanceof IfStmt) {
      IfStmt ifStmt = (IfStmt) s;
      Constant constant = SEvaluator.getFuzzyConstantValueOf(ifStmt.getCondition(), localToConstant);

      if (constant instanceof TopConstant) {
        // no flow
        return;
      } else if (constant instanceof BottomConstant) {
        // flow both ways
        deadStmts.remove(ifStmt);
        stmtToReplacement.remove(ifStmt);
      } else {
        // determine whether to flow through or branch
        conservative = false;

        if (IntConstant.v(0).equals(constant)) {
          fall = true;
          deadStmts.add(ifStmt);
        } else if (IntConstant.v(1).equals(constant)) {
          branch = true;
          stmtToReplacement.put(ifStmt, Jimple.v().newGotoStmt(ifStmt.getTargetBox()));
        } else {
          throw new RuntimeException("IfStmt condition must be 0 or 1! Found: " + constant);
        }
      }
    } else if (s instanceof TableSwitchStmt) {
      TableSwitchStmt table = (TableSwitchStmt) s;
      Constant keyC = SEvaluator.getFuzzyConstantValueOf(table.getKey(), localToConstant);

      if (keyC instanceof TopConstant) {
        // no flow
        return;
      } else if (keyC instanceof BottomConstant) {
        // flow all branches
        stmtToReplacement.remove(table);
      } else if (keyC instanceof IntConstant) {
        // find the one branch we need to flow to
        conservative = false;

        int index = ((IntConstant) keyC).value - table.getLowIndex();
        UnitBox branchBox
            = (index < 0 || index > table.getHighIndex()) ? table.getDefaultTargetBox() : table.getTargetBox(index);

        stmtToReplacement.put(table, Jimple.v().newGotoStmt(branchBox));
        oneBranch = branchOuts.get(table.getUnitBoxes().indexOf(branchBox));
      } else {
        // flow all branches
      }
    } else if (s instanceof LookupSwitchStmt) {
      LookupSwitchStmt lookup = (LookupSwitchStmt) s;
      Constant keyC = SEvaluator.getFuzzyConstantValueOf(lookup.getKey(), localToConstant);

      if (keyC instanceof TopConstant) {
        // no flow
        return;
      } else if (keyC instanceof BottomConstant) {
        // flow all branches
        stmtToReplacement.remove(lookup);
      } else if (keyC instanceof IntConstant) {
        // find the one branch we need to flow to
        conservative = false;

        int index = lookup.getLookupValues().indexOf(keyC);
        UnitBox branchBox = (index < 0) ? lookup.getDefaultTargetBox() : lookup.getTargetBox(index);

        stmtToReplacement.put(lookup, Jimple.v().newGotoStmt(branchBox));
        oneBranch = branchOuts.get(lookup.getUnitBoxes().indexOf(branchBox));
      } else {
        // flow all branches
      }
    }

    // conservative control flow estimates
    if (conservative) {
      fall = s.fallsThrough();
      branch = s.branches();
    }

    if (fall) {
      for (FlowSet<Object> fallSet : fallOut) {
        fallSet.union(fin);
      }
    }

    if (branch) {
      for (FlowSet<Object> branchSet : branchOuts) {
        branchSet.union(fin);
      }
    }

    if (oneBranch != null) {
      oneBranch.union(fin);
    }
  }

  /**
   * Returns (Unit, Constant) Pair if an assumption has changed due to the fact that u is reachable, else returns null.
   **/
  protected Pair<Unit, Constant> processDefinitionStmt(Unit u) {
    if (u instanceof DefinitionStmt) {
      DefinitionStmt dStmt = (DefinitionStmt) u;
      Value value = dStmt.getLeftOp();
      if (value instanceof Local) {
        Local local = (Local) value;
        /* update assumptions */
        if (merge(local, SEvaluator.getFuzzyConstantValueOf(dStmt.getRightOp(), localToConstant))) {
          return new Pair<>(u, localToConstant.get(local));
        }
      }
    }
    return null;
  }

  /**
   * Verifies if the given assumption "constant" changes the previous assumption about "local" and merges the information
   * into the localToConstant map. Returns true if something changed.
   **/
  protected boolean merge(Local local, Constant constant) {
    Constant current = localToConstant.get(local);
    if (current instanceof BottomConstant) {
      return false;
    }
    if (current instanceof TopConstant) {
      localToConstant.put(local, constant);
      return true;
    }
    if (current.equals(constant)) {
      return false;
    }

    // not equal
    localToConstant.put(local, BottomConstant.v());
    return true;
  }
}
