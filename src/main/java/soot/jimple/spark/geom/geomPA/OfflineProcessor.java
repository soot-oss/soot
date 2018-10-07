package soot.jimple.spark.geom.geomPA;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2011 Richard Xiao
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
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import soot.SootClass;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.spark.geom.dataRep.PlainConstraint;
import soot.jimple.spark.geom.utils.ZArrayNumberer;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.GlobalVarNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.SparkField;
import soot.jimple.spark.pag.VarNode;
import soot.jimple.spark.sets.P2SetVisitor;

/**
 * Implementation of pre-processing algorithms performed prior to the pointer analysis.
 *
 * Currently supported techniques are: 1. Pointer distillation: the library code that does not impact the application code
 * pointers is removed; 2. Pointer ranking for worklist prioritizing.
 *
 * @author xiao
 *
 */
public class OfflineProcessor {
  class off_graph_edge {
    // Start and end of this edge
    int s, t;
    // If this edge is created via complex constraint (e.g. p.f = q), base_var = p
    IVarAbstraction base_var;

    off_graph_edge next;
  }

  // Used in anonymous class visitor
  private boolean visitedFlag;

  GeomPointsTo geomPTA;
  ZArrayNumberer<IVarAbstraction> int2var;
  ArrayList<off_graph_edge> varGraph;
  int pre[], low[], count[], rep[], repsize[];
  Deque<Integer> queue;
  int pre_cnt;
  int n_var;

  public OfflineProcessor(GeomPointsTo pta) {
    int2var = pta.pointers;
    int size = int2var.size();
    varGraph = new ArrayList<off_graph_edge>(size);
    queue = new LinkedList<Integer>();
    pre = new int[size];
    low = new int[size];
    count = new int[size];
    rep = new int[size];
    repsize = new int[size];
    geomPTA = pta;

    for (int i = 0; i < size; ++i) {
      varGraph.add(null);
    }
  }

  /**
   * Call it before running the optimizations.
   */
  public void init() {
    // We prepare the essential data structures first
    // The size of the pointers may shrink after each round of analysis
    n_var = int2var.size();

    for (int i = 0; i < n_var; ++i) {
      varGraph.set(i, null);
      int2var.get(i).willUpdate = false;
    }
  }

  public void defaultFeedPtsRoutines() {
    switch (Parameters.seedPts) {
      case Constants.seedPts_allUser:
        setAllUserCodeVariablesUseful();
        break;

      case Constants.seedPts_all:
        // All pointers will be processed
        for (int i = 0; i < n_var; ++i) {
          IVarAbstraction pn = int2var.get(i);
          if (pn != null && pn.getRepresentative() == pn) {
            pn.willUpdate = true;
          }
        }
        return;
    }

    // We always refine the callsites that have multiple call targets
    Set<Node> multiBaseptrs = new HashSet<Node>();

    for (Stmt callsite : geomPTA.multiCallsites) {
      InstanceInvokeExpr iie = (InstanceInvokeExpr) callsite.getInvokeExpr();
      VarNode vn = geomPTA.findLocalVarNode(iie.getBase());
      multiBaseptrs.add(vn);
    }

    addUserDefPts(multiBaseptrs);
  }

  /**
   * Compute the refined points-to results for specified pointers.
   *
   * @param initVars
   */
  public void addUserDefPts(Set<Node> initVars) {
    for (Node vn : initVars) {
      IVarAbstraction pn = geomPTA.findInternalNode(vn);
      if (pn == null) {
        // I don't know where is this pointer
        continue;
      }

      pn = pn.getRepresentative();
      if (pn.reachable()) {
        pn.willUpdate = true;
      }
    }
  }

  public void releaseSparkMem() {
    for (int i = 0; i < n_var; ++i) {
      IVarAbstraction pn = int2var.get(i);
      // Keep only the points-to results for representatives
      if (pn != pn.getRepresentative()) {
        continue;
      }

      if (pn.willUpdate) {
        Node vn = pn.getWrappedNode();
        vn.discardP2Set();
      }
    }

    System.gc();
    System.gc();
    System.gc();
    System.gc();
  }

  /**
   * Preprocess the pointers and constraints before running geomPA.
   *
   * @param useSpark
   * @param multiCallsites
   */
  public void runOptimizations() {
    /*
     * Optimizations based on the dependence graph.
     */
    buildDependenceGraph();
    distillConstraints();

    /*
     * Optimizations based on the impact graph.
     */
    buildImpactGraph();
    computeWeightsForPts();
  }

  public void destroy() {
    pre = null;
    low = null;
    count = null;
    rep = null;
    repsize = null;
    varGraph = null;
    queue = null;
  }

  /**
   * The dependence graph reverses the assignment relations. E.g., p = q => p -> q Note that, the assignments that are
   * eliminated by local variable merging should be used here. Otherwise, the graph would be erroneously disconnected.
   */
  protected void buildDependenceGraph() {
    for (PlainConstraint cons : geomPTA.constraints) {
      // In our constraint representation, lhs -> rhs means rhs = lhs.
      final IVarAbstraction lhs = cons.getLHS();
      final IVarAbstraction rhs = cons.getRHS();
      final SparkField field = cons.f;

      IVarAbstraction rep;

      // Now we use this constraint for graph construction
      switch (cons.type) {

        // rhs = lhs
        case Constants.ASSIGN_CONS:
          add_graph_edge(rhs.id, lhs.id);
          break;

        // rhs = lhs.f
        case Constants.LOAD_CONS: {
          rep = lhs.getRepresentative();

          if (rep.hasPTResult() == false) {
            lhs.getWrappedNode().getP2Set().forall(new P2SetVisitor() {
              @Override
              public void visit(Node n) {
                IVarAbstraction padf = geomPTA.findInstanceField((AllocNode) n, field);
                if (padf == null || padf.reachable() == false) {
                  return;
                }
                off_graph_edge e = add_graph_edge(rhs.id, padf.id);
                e.base_var = lhs;
              }
            });
          } else {
            // Use geom
            for (AllocNode o : rep.get_all_points_to_objects()) {
              IVarAbstraction padf = geomPTA.findInstanceField((AllocNode) o, field);
              if (padf == null || padf.reachable() == false) {
                continue;
              }
              off_graph_edge e = add_graph_edge(rhs.id, padf.id);
              e.base_var = lhs;
            }
          }
        }

          break;

        // rhs.f = lhs
        case Constants.STORE_CONS: {
          rep = rhs.getRepresentative();

          if (rep.hasPTResult() == false) {
            rhs.getWrappedNode().getP2Set().forall(new P2SetVisitor() {
              @Override
              public void visit(Node n) {
                IVarAbstraction padf = geomPTA.findInstanceField((AllocNode) n, field);
                if (padf == null || padf.reachable() == false) {
                  return;
                }
                off_graph_edge e = add_graph_edge(padf.id, lhs.id);
                e.base_var = rhs;
              }
            });
          } else {
            // use geom
            for (AllocNode o : rep.get_all_points_to_objects()) {
              IVarAbstraction padf = geomPTA.findInstanceField((AllocNode) o, field);
              if (padf == null || padf.reachable() == false) {
                continue;
              }
              off_graph_edge e = add_graph_edge(padf.id, lhs.id);
              e.base_var = rhs;
            }
          }
        }

          break;
      }
    }
  }

  /**
   * All the pointers that we need their points-to information are marked.
   *
   * @param virtualBaseSet
   */
  protected void setAllUserCodeVariablesUseful() {
    for (int i = 0; i < n_var; ++i) {
      IVarAbstraction pn = int2var.get(i);
      if (pn != pn.getRepresentative()) {
        continue;
      }

      Node node = pn.getWrappedNode();
      int sm_id = geomPTA.getMethodIDFromPtr(pn);
      if (!geomPTA.isReachableMethod(sm_id)) {
        continue;
      }

      if (node instanceof VarNode) {
        // flag == true if node is defined in the Java library
        boolean defined_in_lib = false;

        if (node instanceof LocalVarNode) {
          defined_in_lib = ((LocalVarNode) node).getMethod().isJavaLibraryMethod();
        } else if (node instanceof GlobalVarNode) {
          SootClass sc = ((GlobalVarNode) node).getDeclaringClass();
          if (sc != null) {
            defined_in_lib = sc.isJavaLibraryClass();
          }
        }

        if (!defined_in_lib && !geomPTA.isExceptionPointer(node)) {
          // Defined in the user code
          pn.willUpdate = true;
        }
      }
    }
  }

  /**
   * Compute a set of pointers that required to refine the seed pointers. Prerequisite: dependence graph
   */
  protected void computeReachablePts() {
    int i;
    IVarAbstraction pn;
    off_graph_edge p;

    // We first collect the initial seeds
    queue.clear();
    for (i = 0; i < n_var; ++i) {
      pn = int2var.get(i);
      if (pn.willUpdate == true) {
        queue.add(i);
      }
    }

    // Worklist based graph traversal
    while (!queue.isEmpty()) {
      i = queue.getFirst();
      queue.removeFirst();

      p = varGraph.get(i);
      while (p != null) {
        pn = int2var.get(p.t);
        if (pn.willUpdate == false) {
          pn.willUpdate = true;
          queue.add(p.t);
        }

        pn = p.base_var;
        if (pn != null && pn.willUpdate == false) {
          pn.willUpdate = true;
          queue.add(pn.id);
        }

        p = p.next;
      }
    }
  }

  /**
   * Eliminate the constraints that do not contribute points-to information to the seed pointers. Prerequisite: dependence
   * graph
   */
  protected void distillConstraints() {
    IVarAbstraction pn;

    // Mark the pointers
    computeReachablePts();

    // Mark the constraints
    for (PlainConstraint cons : geomPTA.constraints) {
      // We only look at the receiver pointers
      pn = cons.getRHS();
      final SparkField field = cons.f;
      visitedFlag = false;

      switch (cons.type) {
        case Constants.NEW_CONS:
        case Constants.ASSIGN_CONS:
        case Constants.LOAD_CONS:
          visitedFlag = pn.willUpdate;
          break;

        case Constants.STORE_CONS:
          /**
           * Interesting point in store constraint p.f = q: For example, pts(p) = { o1, o2 }; If any of the o1.f and the o2.f
           * (e.g. o1.f) will be updated, this constraint should be kept. However, in the points-to analysis, we only assign
           * to o1.f.
           */
          pn = pn.getRepresentative();

          if (pn.hasPTResult() == false) {
            pn.getWrappedNode().getP2Set().forall(new P2SetVisitor() {
              @Override
              public void visit(Node n) {
                if (visitedFlag) {
                  return;
                }
                IVarAbstraction padf = geomPTA.findInstanceField((AllocNode) n, field);
                if (padf == null || padf.reachable() == false) {
                  return;
                }
                visitedFlag |= padf.willUpdate;
              }
            });
          } else {
            // Use the geometric points-to result
            for (AllocNode o : pn.get_all_points_to_objects()) {
              IVarAbstraction padf = geomPTA.findInstanceField((AllocNode) o, field);
              if (padf == null || padf.reachable() == false) {
                continue;
              }
              visitedFlag |= padf.willUpdate;
              if (visitedFlag) {
                break;
              }
            }
          }

          break;
      }

      cons.isActive = visitedFlag;
    }
  }

  /**
   * The dependence graph will be destroyed and the impact graph will be built. p = q means q impacts p. Therefore, we add en
   * edge q -> p in impact graph.
   */
  protected void buildImpactGraph() {
    for (int i = 0; i < n_var; ++i) {
      varGraph.set(i, null);
    }
    queue.clear();

    for (PlainConstraint cons : geomPTA.constraints) {
      if (!cons.isActive) {
        continue;
      }

      final IVarAbstraction lhs = cons.getLHS();
      final IVarAbstraction rhs = cons.getRHS();
      final SparkField field = cons.f;

      IVarAbstraction rep;

      switch (cons.type) {
        case Constants.NEW_CONS:
          // We enqueue the pointers that are allocation result receivers
          queue.add(rhs.id);
          break;

        case Constants.ASSIGN_CONS:
          add_graph_edge(lhs.id, rhs.id);
          break;

        case Constants.LOAD_CONS:
          rep = lhs.getRepresentative();

          if (rep.hasPTResult() == false) {
            lhs.getWrappedNode().getP2Set().forall(new P2SetVisitor() {
              @Override
              public void visit(Node n) {
                IVarAbstraction padf = geomPTA.findInstanceField((AllocNode) n, field);
                if (padf == null || padf.reachable() == false) {
                  return;
                }
                add_graph_edge(padf.id, rhs.id);
              }
            });
          } else {
            // use geomPA
            for (AllocNode o : rep.get_all_points_to_objects()) {
              IVarAbstraction padf = geomPTA.findInstanceField((AllocNode) o, field);
              if (padf == null || padf.reachable() == false) {
                continue;
              }
              add_graph_edge(padf.id, rhs.id);
            }
          }
          break;

        case Constants.STORE_CONS:
          rep = rhs.getRepresentative();

          if (rep.hasPTResult() == false) {
            rhs.getWrappedNode().getP2Set().forall(new P2SetVisitor() {
              @Override
              public void visit(Node n) {
                IVarAbstraction padf = geomPTA.findInstanceField((AllocNode) n, field);
                if (padf == null || padf.reachable() == false) {
                  return;
                }
                add_graph_edge(lhs.id, padf.id);
              }
            });
          } else {
            // use geomPA
            for (AllocNode o : rep.get_all_points_to_objects()) {
              IVarAbstraction padf = geomPTA.findInstanceField((AllocNode) o, field);
              if (padf == null || padf.reachable() == false) {
                continue;
              }
              add_graph_edge(lhs.id, padf.id);
            }
          }

          break;
      }
    }
  }

  /**
   * Prepare for a near optimal worklist selection strategy inspired by Ben's PLDI 07 work. Prerequisite: impact graph
   */
  protected void computeWeightsForPts() {
    int i;
    int s, t;
    off_graph_edge p;
    IVarAbstraction node;

    // prepare the data
    pre_cnt = 0;
    for (i = 0; i < n_var; ++i) {
      pre[i] = -1;
      count[i] = 0;
      rep[i] = i;
      repsize[i] = 1;
      node = int2var.get(i);
      node.top_value = Integer.MIN_VALUE;
    }

    // perform the SCC identification
    for (i = 0; i < n_var; ++i) {
      if (pre[i] == -1) {
        tarjan_scc(i);
      }
    }

    // In-degree counting
    for (i = 0; i < n_var; ++i) {
      p = varGraph.get(i);
      s = find_parent(i);
      while (p != null) {
        t = find_parent(p.t);
        if (t != s) {
          count[t]++;
        }
        p = p.next;
      }
    }

    // Reconstruct the graph with condensed cycles
    for (i = 0; i < n_var; ++i) {
      p = varGraph.get(i);
      if (p != null && rep[i] != i) {
        t = find_parent(i);
        while (p.next != null) {
          p = p.next;
        }
        p.next = varGraph.get(t);
        varGraph.set(t, varGraph.get(i));
        varGraph.set(i, null);
      }
    }

    queue.clear();
    for (i = 0; i < n_var; ++i) {
      if (rep[i] == i && count[i] == 0) {
        queue.addLast(i);
      }
    }

    // Assign the topological value to every node
    // We also reserve space for the cycle members, i.e. linearize all the nodes not only the SCCs
    i = 0;
    while (!queue.isEmpty()) {
      s = queue.getFirst();
      queue.removeFirst();
      node = int2var.get(s);
      node.top_value = i;
      i += repsize[s];

      p = varGraph.get(s);
      while (p != null) {
        t = find_parent(p.t);
        if (t != s) {
          if (--count[t] == 0) {
            queue.addLast(t);
          }
        }
        p = p.next;
      }
    }

    // Assign the non-representative node with the reserved positions
    for (i = n_var - 1; i > -1; --i) {
      if (rep[i] != i) {
        node = int2var.get(find_parent(i));
        IVarAbstraction me = int2var.get(i);
        me.top_value = node.top_value + repsize[node.id] - 1;
        --repsize[node.id];
      }
    }
  }

  private off_graph_edge add_graph_edge(int s, int t) {
    off_graph_edge e = new off_graph_edge();

    e.s = s;
    e.t = t;
    e.next = varGraph.get(s);
    varGraph.set(s, e);

    return e;
  }

  // Contract the graph
  private void tarjan_scc(int s) {
    int t;
    off_graph_edge p;

    pre[s] = low[s] = pre_cnt++;
    queue.addLast(s);
    p = varGraph.get(s);

    while (p != null) {
      t = p.t;
      if (pre[t] == -1) {
        tarjan_scc(t);
      }
      if (low[t] < low[s]) {
        low[s] = low[t];
      }
      p = p.next;
    }

    if (low[s] < pre[s]) {
      return;
    }

    int w = s;

    do {
      t = queue.getLast();
      queue.removeLast();
      low[t] += n_var;
      w = merge_nodes(w, t);
    } while (t != s);
  }

  // Find-union
  private int find_parent(int v) {
    return v == rep[v] ? v : (rep[v] = find_parent(rep[v]));
  }

  // Find-union
  private int merge_nodes(int v1, int v2) {
    v1 = find_parent(v1);
    v2 = find_parent(v2);

    if (v1 != v2) {
      // Select v1 as the representative
      if (repsize[v1] < repsize[v2]) {
        int t = v1;
        v1 = v2;
        v2 = t;
      }

      rep[v2] = v1;
      repsize[v1] += repsize[v2];
    }

    return v1;
  }
}
