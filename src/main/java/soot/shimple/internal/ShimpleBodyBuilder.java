package soot.shimple.internal;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.DefinitionStmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.base.Aggregator;
import soot.jimple.toolkits.scalar.DeadAssignmentEliminator;
import soot.jimple.toolkits.scalar.LocalNameStandardizer;
import soot.jimple.toolkits.scalar.NopEliminator;
import soot.jimple.toolkits.scalar.UnconditionalBranchFolder;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;
import soot.options.ShimpleOptions;
import soot.shimple.DefaultShimpleFactory;
import soot.shimple.PhiExpr;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;
import soot.shimple.ShimpleFactory;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.DominatorNode;
import soot.toolkits.graph.DominatorTree;
import soot.toolkits.scalar.UnusedLocalEliminator;

/**
 * This class does the real high-level work. It takes a Jimple body or Jimple/Shimple hybrid body and produces pure Shimple.
 *
 * <p>
 * The work is done in two main steps:
 *
 * <ol>
 * <li>Trivial Phi nodes are added.
 * <li>A renaming algorithm is executed.
 * </ol>
 *
 * <p>
 * This class can also translate out of Shimple by producing an equivalent Jimple body with all Phi nodes removed.
 *
 * <p>
 * Note that this is an internal class, understanding it should not be necessary from a user point-of-view and relying on it
 * directly is not recommended.
 *
 * @author Navindra Umanee
 * @see soot.shimple.ShimpleBody
 * @see <a href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently Computing Static Single Assignment Form and
 *      the Control Dependence Graph</a>
 */
public class ShimpleBodyBuilder {

  private static String freshSeparator = "_";

  protected final ShimpleBody body;
  protected final ShimpleOptions options;
  protected final ShimpleFactory sf;

  // A fixed list of all original Locals.
  protected List<Local> origLocals;

  // Maps new name Strings to Locals.
  protected Map<String, Local> newLocals;
  // Maps renamed Locals to original Locals.
  protected Map<Local, Local> newLocalsToOldLocal;

  protected int[] assignmentCounters;
  protected Stack<Integer>[] namingStacks;

  public final PhiNodeManager phi;
  public final PiNodeManager pi;

  /**
   * Transforms the provided body to pure SSA form.
   */
  public ShimpleBodyBuilder(ShimpleBody body) {
    // Must remove nops prior to building the CFG because NopStmt appearing
    // before the IdentityStmt in a trap handler that is itself protected
    // by a trap cause Phi nodes to be inserted before the NopStmt and
    // therefore before the IdentityStmt. This introduces a validation
    // problem if the Phi nodes leave residual assignment statements after
    // their removal.
    NopEliminator.v().transform(body);
    this.body = body;
    this.options = body.getOptions();
    this.sf = new DefaultShimpleFactory(body);
    sf.clearCache();
    this.phi = new PhiNodeManager(body, sf);
    this.pi = new PiNodeManager(body, false, sf);
    makeUniqueLocalNames();
  }

  public void update() {
    this.origLocals = Collections.unmodifiableList(new ArrayList<Local>(body.getLocals()));
  }

  public void transform() {
    // Must clear cached graph, etc. in case of rebuilding Shimple
    // after Body modifications that may introduce new blocks.
    sf.clearCache();
    update();

    phi.insertTrivialPhiNodes();

    if (options.extended()) {
      for (boolean change = pi.insertTrivialPiNodes(); change;) {
        if (phi.insertTrivialPhiNodes()) {
          change = pi.insertTrivialPiNodes();
        } else {
          break;
        }
      }
    }

    renameLocals();
    phi.trimExceptionalPhiNodes();
    makeUniqueLocalNames();
  }

  public void preElimOpt() {
    // boolean optElim = options.node_elim_opt();

    // *** FIXME: 89e9a0470601091906j26489960j65290849dbe0481f@mail.gmail.com
    // if(optElim)
    // DeadAssignmentEliminator.v().transform(body);
  }

  public void postElimOpt() {
    if (options.node_elim_opt()) {
      DeadAssignmentEliminator.v().transform(body);
      UnreachableCodeEliminator.v().transform(body);
      UnconditionalBranchFolder.v().transform(body);
      Aggregator.v().transform(body);
      UnusedLocalEliminator.v().transform(body);
    }
  }

  /**
   * Remove Phi nodes from current body, high probability this destroys SSA form.
   *
   * <p>
   * Dead code elimination + register aggregation are performed as recommended by Cytron. The Aggregator looks like it could
   * use some improvements.
   *
   * @see soot.options.ShimpleOptions
   */
  public void eliminatePhiNodes() {
    if (phi.doEliminatePhiNodes()) {
      makeUniqueLocalNames();
    }
  }

  public void eliminatePiNodes() {
    pi.eliminatePiNodes(options.node_elim_opt());
  }

  /**
   * Variable Renaming Algorithm from Cytron et al 91, P26-8, implemented in various bits and pieces by the next functions.
   * Must be called after trivial nodes have been added.
   */
  public void renameLocals() {
    update();

    this.newLocals = new HashMap<String, Local>();
    this.newLocalsToOldLocal = new HashMap<Local, Local>();
    final int size = origLocals.size();
    this.assignmentCounters = new int[size];
    @SuppressWarnings("unchecked")
    Stack<Integer>[] temp = new Stack[size];
    for (int i = 0; i < size; i++) {
      temp[i] = new Stack<Integer>();
    }
    this.namingStacks = temp;

    List<Block> heads = sf.getBlockGraph().getHeads();
    switch (heads.size()) {
      case 0:
        return;
      case 1:
        renameLocalsSearch(heads.get(0));
        return;
      default:
        throw new RuntimeException("Assertion failed: Only one head expected.");
    }
  }

  /**
   * Driven by renameLocals().
   */
  public void renameLocalsSearch(Block block) {
    // accumulated in Step 1 to be re-processed in Step 4
    List<Local> lhsLocals = new ArrayList<Local>();

    // Step 1 of 4 -- Rename block's uses (ordinary) and defs
    // accumulated and re-processed in a later loop
    for (Unit unit : block) {
      // Step 1A
      if (!Shimple.isPhiNode(unit)) {
        for (ValueBox useBox : unit.getUseBoxes()) {
          Value use = useBox.getValue();
          int localIndex = indexOfLocal(use);

          // not one of our locals
          if (localIndex == -1) {
            continue;
          }
          Stack<Integer> namingStack = namingStacks[localIndex];
          if (namingStack.empty()) {
            continue;
          }
          Integer subscript = namingStack.peek();
          Local renamedLocal = fetchNewLocal((Local) use, subscript);
          useBox.setValue(renamedLocal);
        }
      }
      // Step 1B
      if (unit instanceof DefinitionStmt) {
        DefinitionStmt defStmt = (DefinitionStmt) unit;
        ValueBox lhsLocalBox = defStmt.getLeftOpBox();
        Value lhsValue = lhsLocalBox.getValue();

        // not something we're interested in
        if (!origLocals.contains(lhsValue)) {
          continue;
        }

        Local lhsLocal = (Local) lhsValue;

        // re-processed in Step 4
        lhsLocals.add(lhsLocal);

        int localIndex = indexOfLocal(lhsLocal);
        if (localIndex == -1) {
          throw new RuntimeException("Assertion failed.");
        }

        Integer subscript = assignmentCounters[localIndex];
        Local newLhsLocal = fetchNewLocal(lhsLocal, subscript);
        lhsLocalBox.setValue(newLhsLocal);

        namingStacks[localIndex].push(subscript);
        assignmentCounters[localIndex]++;
      }
    }

    // Step 2 of 4 -- Rename Phi node uses in Successors
    for (Block succ : sf.getBlockGraph().getSuccsOf(block)) {
      // Ignore dummy blocks
      if (block.getHead() == null && block.getTail() == null) {
        continue;
      }

      for (Unit unit : succ) {
        PhiExpr phiExpr = Shimple.getPhiExpr(unit);
        if (phiExpr != null) {
          // simulate whichPred
          ValueBox phiArgBox = phiExpr.getArgBox(block);
          if (phiArgBox == null) {
            throw new RuntimeException("Assertion failed. Cannot find " + block + " in " + phiExpr);
          }

          Local phiArg = (Local) phiArgBox.getValue();

          int localIndex = indexOfLocal(phiArg);
          if (localIndex == -1) {
            throw new RuntimeException("Assertion failed.");
          }

          Stack<Integer> namingStack = namingStacks[localIndex];
          if (!namingStack.empty()) {
            Integer subscript = namingStack.peek();
            Local newPhiArg = fetchNewLocal(phiArg, subscript);
            phiArgBox.setValue(newPhiArg);
          }
        }
      }
    }

    // Step 3 of 4 -- Recurse over children.
    {
      DominatorTree<Block> dt = sf.getDominatorTree();
      for (DominatorNode<Block> childNode : dt.getChildrenOf(dt.getDode(block))) {
        renameLocalsSearch(childNode.getGode());
      }
    }

    // Step 4 of 4 -- Tricky name stack updates.
    for (Local lhsLocal : lhsLocals) {
      int lhsLocalIndex = indexOfLocal(lhsLocal);
      if (lhsLocalIndex == -1) {
        throw new RuntimeException("Assertion failed.");
      }
      namingStacks[lhsLocalIndex].pop();
    }

    // And we're done. The renaming process is complete.
  }

  /**
   * Clever convenience function to fetch or create new Local's given a Local and the desired subscript.
   */
  protected Local fetchNewLocal(Local local, Integer subscript) {
    Local oldLocal = origLocals.contains(local) ? local : newLocalsToOldLocal.get(local);
    if (subscript == 0) {
      return oldLocal;
    }

    // If the name already exists, makeUniqueLocalNames() will take care of it.
    String name = oldLocal.getName() + freshSeparator + subscript;
    Local newLocal = newLocals.get(name);
    if (newLocal == null) {
      newLocal = new JimpleLocal(name, oldLocal.getType());
      newLocals.put(name, newLocal);
      newLocalsToOldLocal.put(newLocal, oldLocal);

      // add proper Local declation
      body.getLocals().add(newLocal);
    }

    return newLocal;
  }

  /**
   * Convenient function that maps new Locals to the originating Local, and finds the appropriate array index into the naming
   * structures.
   */
  protected int indexOfLocal(Value local) {
    int localIndex = origLocals.indexOf(local);
    if (localIndex == -1) {
      // might be null
      Local oldLocal = newLocalsToOldLocal.get(local);
      localIndex = origLocals.indexOf(oldLocal);
    }
    return localIndex;
  }

  /**
   * Make sure the locals in the given body all have unique String names. Renaming is done if necessary.
   */
  public void makeUniqueLocalNames() {
    if (options.standard_local_names()) {
      LocalNameStandardizer.v().transform(body);
      return;
    }

    Set<String> localNames = new HashSet<String>();
    for (Local local : body.getLocals()) {
      String localName = local.getName();
      if (localNames.contains(localName)) {
        String uniqueName = makeUniqueLocalName(localName, localNames);
        local.setName(uniqueName);
        localNames.add(uniqueName);
      } else {
        localNames.add(localName);
      }
    }
  }

  /**
   * Given a set of Strings, return a new name for dupName that is not currently in the set.
   */
  public static String makeUniqueLocalName(String dupName, Set<String> localNames) {
    int counter = 1;
    String newName = dupName;
    while (localNames.contains(newName)) {
      newName = dupName + freshSeparator + counter++;
    }
    return newName;
  }

  public static void setSeparator(String sep) {
    freshSeparator = sep;
  }
}
