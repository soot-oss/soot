package soot.jimple.spark.geom.helper;

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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.AnySubType;
import soot.ArrayType;
import soot.FastHierarchy;
import soot.Local;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.spark.geom.dataRep.CgEdge;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.IVarAbstraction;
import soot.jimple.spark.geom.utils.Histogram;
import soot.jimple.spark.pag.AllocDotField;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.VarNode;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

/**
 * We provide a set of methods to evaluate the quality of geometric points-to analysis. The evaluation methods are:
 *
 * 1. Count the basic points-to information, such as average points-to set size, constraints evaluation graph size, etc; 2.
 * Virtual function resolution comparison; 3. Static casts checking; 4. All pairs alias analysis; 5. Building heap graph (not
 * used yet).
 *
 * @author xiao
 *
 */
public class GeomEvaluator {
  private static final Logger logger = LoggerFactory.getLogger(GeomEvaluator.class);
  private GeomPointsTo ptsProvider;
  private PrintStream outputer;
  private EvalResults evalRes;
  private boolean solved; // Used in the anonymous class visitor

  public GeomEvaluator(GeomPointsTo gpts, PrintStream ps) {
    ptsProvider = gpts;
    outputer = ps;
    evalRes = new EvalResults();
  }

  /**
   * Collecting basic statistical information for SPARK.
   */
  public void profileSparkBasicMetrics() {
    int n_legal_var = 0;

    int[] limits = new int[] { 1, 5, 10, 25, 50, 75, 100 };
    evalRes.pts_size_bar_spark = new Histogram(limits);

    for (IVarAbstraction pn : ptsProvider.pointers) {
      // We don't consider exception pointers
      Node var = pn.getWrappedNode();
      if (ptsProvider.isExceptionPointer(var)) {
        continue;
      }

      ++n_legal_var;

      int size = var.getP2Set().size();
      evalRes.pts_size_bar_spark.addNumber(size);
      evalRes.total_spark_pts += size;
      if (size > evalRes.max_pts_spark) {
        evalRes.max_pts_spark = size;
      }
    }

    evalRes.avg_spark_pts = (double) evalRes.total_spark_pts / n_legal_var;
  }

  /**
   * Summarize the geometric points-to analysis and report the basic metrics.
   */
  public void profileGeomBasicMetrics(boolean testSpark) {
    int n_legal_var = 0, n_alloc_dot_fields = 0;

    int[] limits = new int[] { 1, 5, 10, 25, 50, 75, 100 };
    evalRes.pts_size_bar_geom = new Histogram(limits);

    if (testSpark) {
      evalRes.total_spark_pts = 0;
      evalRes.max_pts_spark = 0;
      evalRes.pts_size_bar_spark = new Histogram(limits);
    }

    // We first count the LOC
    for (SootMethod sm : ptsProvider.getAllReachableMethods()) {
      if (!sm.isConcrete()) {
        continue;
      }
      if (!sm.hasActiveBody()) {
        sm.retrieveActiveBody();
      }

      evalRes.loc += sm.getActiveBody().getUnits().size();
    }

    for (IVarAbstraction pn : ptsProvider.pointers) {
      // We don't consider those un-processed pointers because their
      // points-to information is equivalent to SPARK
      if (!pn.hasPTResult()) {
        continue;
      }

      pn = pn.getRepresentative();
      Node var = pn.getWrappedNode();

      if (ptsProvider.isExceptionPointer(var)) {
        continue;
      }

      if (var instanceof AllocDotField) {
        ++n_alloc_dot_fields;
      }
      ++n_legal_var;

      // ...spark
      int size;

      if (testSpark) {
        size = var.getP2Set().size();
        evalRes.pts_size_bar_spark.addNumber(size);
        evalRes.total_spark_pts += size;
        if (size > evalRes.max_pts_spark) {
          evalRes.max_pts_spark = size;
        }
      }

      // ...geom
      size = pn.num_of_diff_objs();
      evalRes.pts_size_bar_geom.addNumber(size);
      evalRes.total_geom_ins_pts += size;
      if (size > evalRes.max_pts_geom) {
        evalRes.max_pts_geom = size;
      }
    }

    evalRes.avg_geom_ins_pts = (double) evalRes.total_geom_ins_pts / n_legal_var;
    if (testSpark) {
      evalRes.avg_spark_pts = (double) evalRes.total_spark_pts / n_legal_var;
    }

    outputer.println("");
    outputer.println("----------Statistical Result of geomPTA <Data Format: geomPTA (SPARK)>----------");
    outputer.printf("Lines of code (jimple): %.1fK\n", (double) evalRes.loc / 1000);
    outputer.printf("Reachable Methods: %d (%d)\n", ptsProvider.getNumberOfMethods(), ptsProvider.getNumberOfSparkMethods());
    outputer.printf("Reachable User Methods: %d (%d)\n", ptsProvider.n_reach_user_methods,
        ptsProvider.n_reach_spark_user_methods);
    outputer.println("#All Pointers: " + ptsProvider.getNumberOfPointers());
    outputer.println("#Core Pointers: " + n_legal_var + ", in which #AllocDot Fields: " + n_alloc_dot_fields);
    outputer.printf("Total/Average Projected Points-to Tuples [core pointers]: %d (%d) / %.3f (%.3f) \n",
        evalRes.total_geom_ins_pts, evalRes.total_spark_pts, evalRes.avg_geom_ins_pts, evalRes.avg_spark_pts);
    outputer.println(
        "The largest points-to set size [core pointers]: " + evalRes.max_pts_geom + " (" + evalRes.max_pts_spark + ")");

    outputer.println();
    evalRes.pts_size_bar_geom.printResult(outputer, "Points-to Set Sizes Distribution [core pointers]:",
        evalRes.pts_size_bar_spark);
  }

  /**
   * We assess the quality of building the 1-cfa call graph with the geometric points-to result.
   */
  private void test_1cfa_call_graph(LocalVarNode vn, SootMethod caller, SootMethod callee_signature, Histogram ce_range) {
    long l, r;
    IVarAbstraction pn = ptsProvider.findInternalNode(vn);
    if (pn == null) {
      return;
    }
    pn = pn.getRepresentative();
    Set<SootMethod> tgts = new HashSet<SootMethod>();
    Set<AllocNode> set = pn.get_all_points_to_objects();

    LinkedList<CgEdge> list = ptsProvider.getCallEdgesInto(ptsProvider.getIDFromSootMethod(caller));

    FastHierarchy hierarchy = Scene.v().getOrMakeFastHierarchy();

    for (Iterator<CgEdge> it = list.iterator(); it.hasNext();) {
      CgEdge p = it.next();

      l = p.map_offset;
      r = l + ptsProvider.max_context_size_block[p.s];
      tgts.clear();

      for (AllocNode obj : set) {
        if (!pn.pointer_interval_points_to(l, r, obj)) {
          continue;
        }

        Type t = obj.getType();

        if (t == null) {
          continue;
        } else if (t instanceof AnySubType) {
          t = ((AnySubType) t).getBase();
        } else if (t instanceof ArrayType) {
          t = RefType.v("java.lang.Object");
        }

        try {
          tgts.add(hierarchy.resolveConcreteDispatch(((RefType) t).getSootClass(), callee_signature));
        } catch (Exception e) {
          logger.debug(e.getMessage(), e);
        }
      }

      tgts.remove(null);
      ce_range.addNumber(tgts.size());
    }
  }

  /**
   * Report the virtual callsites resolution result for the user's code.
   */
  public void checkCallGraph() {
    int[] limits = new int[] { 1, 2, 4, 8 };
    evalRes.total_call_edges = new Histogram(limits);

    CallGraph cg = Scene.v().getCallGraph();

    for (Stmt callsite : ptsProvider.multiCallsites) {
      Iterator<Edge> edges = cg.edgesOutOf(callsite);
      if (!edges.hasNext()) {
        continue;
      }
      evalRes.n_callsites++;

      // get an edge
      Edge anyEdge = edges.next();
      SootMethod src = anyEdge.src();

      if (!ptsProvider.isReachableMethod(src) || !ptsProvider.isValidMethod(src)) {
        continue;
      }

      // get the base pointer
      CgEdge p = ptsProvider.getInternalEdgeFromSootEdge(anyEdge);
      LocalVarNode vn = (LocalVarNode) p.base_var;

      // test the call graph
      int edge_cnt = 1;
      while (edges.hasNext()) {
        ++edge_cnt;
        edges.next();
      }
      evalRes.n_geom_call_edges += edge_cnt;
      if (edge_cnt == 1) {
        ++evalRes.n_geom_solved_all;
      }

      // test app method
      if (!src.isJavaLibraryMethod()) {
        InvokeExpr ie = callsite.getInvokeExpr();

        if (edge_cnt == 1) {
          ++evalRes.n_geom_solved_app;

          if (ptsProvider.getOpts().verbose()) {
            outputer.println();
            outputer.println("<<<<<<<<<   Additional Solved Call   >>>>>>>>>>");
            outputer.println(src.toString());
            outputer.println(ie.toString());
          }
        } else {
          // We try to test if this callsite is solvable
          // under some contexts
          Histogram call_edges = new Histogram(limits);
          test_1cfa_call_graph(vn, src, ie.getMethod(), call_edges);
          evalRes.total_call_edges.merge(call_edges);
          call_edges = null;
        }

        evalRes.n_geom_user_edges += edge_cnt;
        evalRes.n_user_callsites++;
      }
    }

    ptsProvider.ps.println();
    ptsProvider.ps.println("--------> Virtual Callsites Evaluation <---------");
    ptsProvider.ps.printf("Total virtual callsites (app code): %d (%d)\n", evalRes.n_callsites, evalRes.n_user_callsites);
    ptsProvider.ps.printf("Total virtual call edges (app code): %d (%d)\n", evalRes.n_geom_call_edges,
        evalRes.n_geom_user_edges);
    ptsProvider.ps.printf("Virtual callsites additionally solved by geomPTA compared to SPARK (app code) = %d (%d)\n",
        evalRes.n_geom_solved_all, evalRes.n_geom_solved_app);
    evalRes.total_call_edges.printResult(ptsProvider.ps, "Testing of unsolved callsites on 1-CFA call graph: ");

    if (ptsProvider.getOpts().verbose()) {
      ptsProvider.outputNotEvaluatedMethods();
    }
  }

  /**
   * Count how many aliased base pointers appeared in all user's functions.
   */
  public void checkAliasAnalysis() {
    Set<IVarAbstraction> access_expr = new HashSet<IVarAbstraction>();
    ArrayList<IVarAbstraction> al = new ArrayList<IVarAbstraction>();
    Value[] values = new Value[2];

    for (SootMethod sm : ptsProvider.getAllReachableMethods()) {
      if (sm.isJavaLibraryMethod()) {
        continue;
      }
      if (!sm.isConcrete()) {
        continue;
      }
      if (!sm.hasActiveBody()) {
        sm.retrieveActiveBody();
      }
      if (!ptsProvider.isValidMethod(sm)) {
        continue;
      }

      // We first gather all the pointers
      // access_expr.clear();
      for (Iterator<Unit> stmts = sm.getActiveBody().getUnits().iterator(); stmts.hasNext();) {
        Stmt st = (Stmt) stmts.next();

        if (st instanceof AssignStmt) {
          AssignStmt a = (AssignStmt) st;
          values[0] = a.getLeftOp();
          values[1] = a.getRightOp();

          for (Value v : values) {
            // We only care those pointers p involving in the
            // expression: p.f
            if (v instanceof InstanceFieldRef) {
              InstanceFieldRef ifr = (InstanceFieldRef) v;
              final SootField field = ifr.getField();
              if (!(field.getType() instanceof RefType)) {
                continue;
              }

              LocalVarNode vn = ptsProvider.findLocalVarNode((Local) ifr.getBase());
              if (vn == null) {
                continue;
              }

              if (ptsProvider.isExceptionPointer(vn)) {
                continue;
              }

              IVarAbstraction pn = ptsProvider.findInternalNode(vn);
              if (pn == null) {
                continue;
              }
              pn = pn.getRepresentative();
              if (pn.hasPTResult()) {
                access_expr.add(pn);
              }
            }
          }
        }
      }
    }

    access_expr.remove(null);
    al.addAll(access_expr);
    access_expr = null;

    // Next, we pair up all the pointers
    Date begin = new Date();
    int size = al.size();

    for (int i = 0; i < size; ++i) {
      IVarAbstraction pn = al.get(i);
      VarNode n1 = (VarNode) pn.getWrappedNode();

      for (int j = i + 1; j < size; ++j) {
        IVarAbstraction qn = al.get(j);
        VarNode n2 = (VarNode) qn.getWrappedNode();

        if (pn.heap_sensitive_intersection(qn)) {
          evalRes.n_hs_alias++;
        }

        // We directly use the SPARK points-to sets
        if (n1.getP2Set().hasNonEmptyIntersection(n2.getP2Set())) {
          evalRes.n_hi_alias++;
        }
      }
    }

    evalRes.n_alias_pairs = size * (size - 1) / 2;
    Date end = new Date();

    ptsProvider.ps.println();
    ptsProvider.ps.println("--------> Alias Pairs Evaluation <---------");
    ptsProvider.ps.println("Number of pointer pairs in app code: " + evalRes.n_alias_pairs);
    ptsProvider.ps.printf("Heap sensitive alias pairs (by Geom): %d, Percentage = %.3f%%\n", evalRes.n_hs_alias,
        (double) evalRes.n_hs_alias / evalRes.n_alias_pairs * 100);
    ptsProvider.ps.printf("Heap insensitive alias pairs (by SPARK): %d, Percentage = %.3f%%\n", evalRes.n_hi_alias,
        (double) evalRes.n_hi_alias / evalRes.n_alias_pairs * 100);
    ptsProvider.ps.printf("Using time: %dms \n", end.getTime() - begin.getTime());
    ptsProvider.ps.println();
  }

  /**
   * Count how many static casts can be determined safe.
   */
  public void checkCastsSafety() {

    for (SootMethod sm : ptsProvider.getAllReachableMethods()) {
      if (sm.isJavaLibraryMethod()) {
        continue;
      }
      if (!sm.isConcrete()) {
        continue;
      }
      if (!sm.hasActiveBody()) {
        sm.retrieveActiveBody();
      }
      if (!ptsProvider.isValidMethod(sm)) {
        continue;
      }

      // All the statements in the method
      for (Iterator<Unit> stmts = sm.getActiveBody().getUnits().iterator(); stmts.hasNext();) {
        Stmt st = (Stmt) stmts.next();

        if (st instanceof AssignStmt) {
          Value rhs = ((AssignStmt) st).getRightOp();
          Value lhs = ((AssignStmt) st).getLeftOp();
          if (rhs instanceof CastExpr && lhs.getType() instanceof RefLikeType) {

            Value v = ((CastExpr) rhs).getOp();
            VarNode node = ptsProvider.findLocalVarNode(v);
            if (node == null) {
              continue;
            }
            IVarAbstraction pn = ptsProvider.findInternalNode(node);
            if (pn == null) {
              continue;
            }

            pn = pn.getRepresentative();
            if (!pn.hasPTResult()) {
              continue;
            }

            evalRes.total_casts++;
            final Type targetType = (RefLikeType) ((CastExpr) rhs).getCastType();

            // We first use the geometric points-to result to
            // evaluate
            solved = true;
            Set<AllocNode> set = pn.get_all_points_to_objects();
            for (AllocNode obj : set) {
              solved = ptsProvider.castNeverFails(obj.getType(), targetType);
              if (solved == false) {
                break;
              }
            }

            if (solved) {
              evalRes.geom_solved_casts++;
            }

            // Second is the SPARK result
            solved = true;
            node.getP2Set().forall(new P2SetVisitor() {
              public void visit(Node arg0) {
                if (solved == false) {
                  return;
                }
                solved = ptsProvider.castNeverFails(arg0.getType(), targetType);
              }
            });

            if (solved) {
              evalRes.spark_solved_casts++;
            }
          }
        }
      }
    }

    ptsProvider.ps.println();
    ptsProvider.ps.println("-----------> Static Casts Safety Evaluation <------------");
    ptsProvider.ps.println("Total casts (app code): " + evalRes.total_casts);
    ptsProvider.ps.println("Safe casts: Geom = " + evalRes.geom_solved_casts + ", SPARK = " + evalRes.spark_solved_casts);
  }

  /**
   * Estimate the size of the def-use graph for the heap memory. The heap graph is estimated without context information.
   */
  public void estimateHeapDefuseGraph() {
    final Map<IVarAbstraction, int[]> defUseCounterForGeom = new HashMap<IVarAbstraction, int[]>();
    final Map<AllocDotField, int[]> defUseCounterForSpark = new HashMap<AllocDotField, int[]>();

    Date begin = new Date();

    for (SootMethod sm : ptsProvider.getAllReachableMethods()) {
      if (sm.isJavaLibraryMethod()) {
        continue;
      }
      if (!sm.isConcrete()) {
        continue;
      }
      if (!sm.hasActiveBody()) {
        sm.retrieveActiveBody();
      }
      if (!ptsProvider.isValidMethod(sm)) {
        continue;
      }

      // We first gather all the memory access expressions
      for (Iterator<Unit> stmts = sm.getActiveBody().getUnits().iterator(); stmts.hasNext();) {
        Stmt st = (Stmt) stmts.next();

        if (!(st instanceof AssignStmt)) {
          continue;
        }

        AssignStmt a = (AssignStmt) st;
        final Value lValue = a.getLeftOp();
        final Value rValue = a.getRightOp();

        InstanceFieldRef ifr = null;

        if (lValue instanceof InstanceFieldRef) {
          // Def statement
          ifr = (InstanceFieldRef) lValue;
        } else if (rValue instanceof InstanceFieldRef) {
          // Use statement
          ifr = (InstanceFieldRef) rValue;
        }

        if (ifr != null) {
          final SootField field = ifr.getField();

          LocalVarNode vn = ptsProvider.findLocalVarNode((Local) ifr.getBase());
          if (vn == null) {
            continue;
          }
          IVarAbstraction pn = ptsProvider.findInternalNode(vn);
          if (pn == null) {
            continue;
          }
          pn = pn.getRepresentative();
          if (!pn.hasPTResult()) {
            continue;
          }

          // Spark
          vn.getP2Set().forall(new P2SetVisitor() {

            @Override
            public void visit(Node n) {
              IVarAbstraction padf = ptsProvider.findAndInsertInstanceField((AllocNode) n, field);
              AllocDotField adf = (AllocDotField) padf.getWrappedNode();
              int[] defUseUnit = defUseCounterForSpark.get(adf);
              if (defUseUnit == null) {
                defUseUnit = new int[2];
                defUseCounterForSpark.put(adf, defUseUnit);
              }

              if (lValue instanceof InstanceFieldRef) {
                defUseUnit[0]++;
              } else {
                defUseUnit[1]++;
              }
            }
          });

          // Geom

          Set<AllocNode> objsSet = pn.get_all_points_to_objects();
          for (AllocNode obj : objsSet) {
            /*
             * We will create a lot of instance fields. Because in points-to analysis, we concern only the reference type
             * fields. But here, we concern all the fields read write including the primitive type fields.
             */
            IVarAbstraction padf = ptsProvider.findAndInsertInstanceField(obj, field);
            int[] defUseUnit = defUseCounterForGeom.get(padf);
            if (defUseUnit == null) {
              defUseUnit = new int[2];
              defUseCounterForGeom.put(padf, defUseUnit);
            }

            if (lValue instanceof InstanceFieldRef) {
              defUseUnit[0]++;
            } else {
              defUseUnit[1]++;
            }
          }
        }
      }
    }

    for (int[] defUseUnit : defUseCounterForSpark.values()) {
      evalRes.n_spark_du_pairs += ((long) defUseUnit[0]) * defUseUnit[1];
    }

    for (int[] defUseUnit : defUseCounterForGeom.values()) {
      evalRes.n_geom_du_pairs += ((long) defUseUnit[0]) * defUseUnit[1];
    }

    Date end = new Date();

    ptsProvider.ps.println();
    ptsProvider.ps.println("-----------> Heap Def Use Graph Evaluation <------------");
    ptsProvider.ps.println("The edges in the heap def-use graph is (by Geom): " + evalRes.n_geom_du_pairs);
    ptsProvider.ps.println("The edges in the heap def-use graph is (by Spark): " + evalRes.n_spark_du_pairs);
    ptsProvider.ps.printf("Using time: %dms \n", end.getTime() - begin.getTime());
    ptsProvider.ps.println();
  }
}

class EvalResults {
  // Basic metrics
  public int loc = 0;
  public long total_geom_ins_pts = 0, total_spark_pts = 0;
  public double avg_geom_ins_pts = .0, avg_spark_pts = .0;
  public int max_pts_geom = 0, max_pts_spark = 0;
  public Histogram pts_size_bar_geom = null, pts_size_bar_spark = null;

  // Call graph metrics
  public int n_callsites = 0, n_user_callsites = 0;
  public int n_geom_call_edges = 0, n_geom_user_edges = 0;
  public int n_geom_solved_all = 0, n_geom_solved_app = 0;
  public Histogram total_call_edges = null;

  // Alias metrics
  public long n_alias_pairs = 0;
  public long n_hs_alias = 0, n_hi_alias = 0;

  // Static cast metrics
  public int total_casts = 0;
  public int geom_solved_casts = 0, spark_solved_casts = 0;

  // Heap def-use graph metrics
  public long n_geom_du_pairs = 0, n_spark_du_pairs = 0;
}
