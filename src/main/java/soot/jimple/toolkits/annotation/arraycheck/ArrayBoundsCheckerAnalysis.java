package soot.jimple.toolkits.annotation.arraycheck;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.Hierarchy;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.EqExpr;
import soot.jimple.FieldRef;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LtExpr;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.Stmt;
import soot.jimple.SubExpr;
import soot.options.Options;
import soot.toolkits.graph.ArrayRefBlockGraph;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.SlowPseudoTopologicalOrderer;

class ArrayBoundsCheckerAnalysis {
  private static final Logger logger = LoggerFactory.getLogger(ArrayBoundsCheckerAnalysis.class);
  protected Map<Block, WeightedDirectedSparseGraph> blockToBeforeFlow;
  protected Map<Unit, WeightedDirectedSparseGraph> unitToBeforeFlow;

  private final Map<FlowGraphEdge, WeightedDirectedSparseGraph> edgeMap;
  private final Set<FlowGraphEdge> edgeSet;

  private HashMap<Block, Integer> stableRoundOfUnits;

  private ArrayRefBlockGraph graph;

  private final IntContainer zero = new IntContainer(0);

  private boolean fieldin = false;
  private HashMap<Object, HashSet<Value>> localToFieldRef;
  private HashMap<Object, HashSet<Value>> fieldToFieldRef;
  private final int strictness = 2;

  private boolean arrayin = false;

  private boolean csin = false;
  private HashMap<Value, HashSet<Value>> localToExpr;

  private boolean classfieldin = false;
  private ClassFieldAnalysis cfield;

  private boolean rectarray = false;
  private HashSet<Local> rectarrayset;
  private HashSet<Local> multiarraylocals;

  private final ArrayIndexLivenessAnalysis ailanalysis;

  /* A little bit different from ForwardFlowAnalysis */
  public ArrayBoundsCheckerAnalysis(Body body, boolean takeClassField, boolean takeFieldRef, boolean takeArrayRef,
      boolean takeCSE, boolean takeRectArray) {
    classfieldin = takeClassField;
    fieldin = takeFieldRef;
    arrayin = takeArrayRef;
    csin = takeCSE;
    rectarray = takeRectArray;

    SootMethod thismethod = body.getMethod();

    if (Options.v().debug()) {
      logger.debug("ArrayBoundsCheckerAnalysis started on  " + thismethod.getName());
    }

    ailanalysis = new ArrayIndexLivenessAnalysis(new ExceptionalUnitGraph(body), fieldin, arrayin, csin, rectarray);

    if (fieldin) {
      this.localToFieldRef = ailanalysis.getLocalToFieldRef();
      this.fieldToFieldRef = ailanalysis.getFieldToFieldRef();
    }

    if (arrayin) {
      if (rectarray) {
        this.multiarraylocals = ailanalysis.getMultiArrayLocals();
        this.rectarrayset = new HashSet<Local>();

        RectangularArrayFinder pgbuilder = RectangularArrayFinder.v();

        Iterator<Local> localIt = multiarraylocals.iterator();
        while (localIt.hasNext()) {
          Local local = localIt.next();

          MethodLocal mlocal = new MethodLocal(thismethod, local);

          if (pgbuilder.isRectangular(mlocal)) {
            this.rectarrayset.add(local);
          }
        }
      }
    }

    if (csin) {
      this.localToExpr = ailanalysis.getLocalToExpr();
    }

    if (classfieldin) {
      this.cfield = ClassFieldAnalysis.v();
    }

    this.graph = new ArrayRefBlockGraph(body);

    blockToBeforeFlow = new HashMap<Block, WeightedDirectedSparseGraph>(graph.size() * 2 + 1, 0.7f);

    edgeMap = new HashMap<FlowGraphEdge, WeightedDirectedSparseGraph>(graph.size() * 2 + 1, 0.7f);

    edgeSet = buildEdgeSet(graph);

    doAnalysis();

    convertToUnitEntry();

    if (Options.v().debug()) {
      logger.debug("ArrayBoundsCheckerAnalysis finished.");
    }
  }

  private void convertToUnitEntry() {
    unitToBeforeFlow = new HashMap<Unit, WeightedDirectedSparseGraph>();
    Iterator<Block> blockIt = blockToBeforeFlow.keySet().iterator();
    while (blockIt.hasNext()) {
      Block block = blockIt.next();
      Unit first = block.getHead();
      unitToBeforeFlow.put(first, blockToBeforeFlow.get(block));
    }
  }

  /**
   * buildEdgeSet creates a set of edges from directed graph.
   */
  public Set<FlowGraphEdge> buildEdgeSet(DirectedGraph<Block> dg) {
    HashSet<FlowGraphEdge> edges = new HashSet<FlowGraphEdge>();

    Iterator<Block> blockIt = dg.iterator();
    while (blockIt.hasNext()) {
      Block s = blockIt.next();

      List<Block> preds = graph.getPredsOf(s);
      List<Block> succs = graph.getSuccsOf(s);

      /* Head units has in edge from itself to itself. */
      if (preds.size() == 0) {
        edges.add(new FlowGraphEdge(s, s));
      }

      /* End units has out edge from itself to itself. */
      if (succs.size() == 0) {
        edges.add(new FlowGraphEdge(s, s));
      } else {
        Iterator succIt = succs.iterator();
        while (succIt.hasNext()) {
          edges.add(new FlowGraphEdge(s, succIt.next()));
        }
      }
    }

    return edges;
  }

  public Object getFlowBefore(Object s) {
    return unitToBeforeFlow.get(s);
  }

  /* merge all preds' out set */
  private void mergebunch(Object ins[], Object s, Object prevOut, Object out) {
    WeightedDirectedSparseGraph prevgraph = (WeightedDirectedSparseGraph) prevOut,
        outgraph = (WeightedDirectedSparseGraph) out;

    WeightedDirectedSparseGraph[] ingraphs = new WeightedDirectedSparseGraph[ins.length];

    for (int i = 0; i < ins.length; i++) {
      ingraphs[i] = (WeightedDirectedSparseGraph) ins[i];
    }

    {
      outgraph.addBoundedAll(ingraphs[0]);

      for (int i = 1; i < ingraphs.length; i++) {
        outgraph.unionSelf(ingraphs[i]);
        outgraph.makeShortestPathGraph();
      }

      // if (flowStable)
      /*
       * Integer round = (Integer)stableRoundOfUnits.get(s);
       *
       * if (round.intValue() < 2) { stableRoundOfUnits.put(s, new Integer(round.intValue()+1)); } else { // To make output
       * stable. compare with previous output value. outgraph.wideEdges((WeightedDirectedSparseGraph)prevOut);
       * outgraph.makeShortestPathGraph(); }
       */

      outgraph.widenEdges(prevgraph);

      // outgraph.makeShortestPathGraph();
      /*
       * for (int i=0; i<ins.length; i++) { logger.debug("in " + i); logger.debug(""+ins[i]); }
       *
       * logger.debug("out "); logger.debug(""+out);
       */
    }
  }

  /*
   * override ForwardFlowAnalysis
   */
  private void doAnalysis() {
    Date start = new Date();
    if (Options.v().debug()) {
      logger.debug("Building PseudoTopological order list on " + start);
    }

    LinkedList allUnits = (LinkedList) SlowPseudoTopologicalOrderer.v().newList(this.graph, false);

    BoundedPriorityList changedUnits = new BoundedPriorityList(allUnits);

    // LinkedList changedUnits = new LinkedList(allUnits);

    Date finish = new Date();
    if (Options.v().debug()) {
      long runtime = finish.getTime() - start.getTime();
      long mins = runtime / 60000;
      long secs = (runtime % 60000) / 1000;
      logger.debug("Building PseudoTopological order finished. " + "It took " + mins + " mins and " + secs + " secs.");
    }

    start = new Date();

    HashSet changedUnitsSet = new HashSet(allUnits);

    List<Object> changedSuccs;

    /* An temporary object. */
    FlowGraphEdge tmpEdge = new FlowGraphEdge();

    /*
     * If any output flow set has unknow value, it will be put in this set
     */
    HashSet<Block> unvisitedNodes = new HashSet<Block>(graph.size() * 2 + 1, 0.7f);

    /* adjust livelocals set */
    {
      Iterator blockIt = graph.iterator();
      while (blockIt.hasNext()) {
        Block block = (Block) blockIt.next();
        HashSet<IntContainer> livelocals = (HashSet<IntContainer>) ailanalysis.getFlowBefore(block.getHead());
        livelocals.add(zero);
      }
    }

    /* Set initial values and nodes to visit. */
    {
      stableRoundOfUnits = new HashMap<Block, Integer>();

      Iterator it = graph.iterator();

      while (it.hasNext()) {
        Block block = (Block) it.next();

        unvisitedNodes.add(block);
        stableRoundOfUnits.put(block, new Integer(0));

        /* only take out the necessary node set. */
        HashSet livelocals = (HashSet) ailanalysis.getFlowBefore(block.getHead());

        blockToBeforeFlow.put(block, new WeightedDirectedSparseGraph(livelocals, false));
      }

      Iterator<FlowGraphEdge> edgeIt = edgeSet.iterator();
      while (edgeIt.hasNext()) {
        FlowGraphEdge edge = edgeIt.next();

        Block target = (Block) edge.to;
        HashSet livelocals = (HashSet) ailanalysis.getFlowBefore(target.getHead());

        edgeMap.put(edge, new WeightedDirectedSparseGraph(livelocals, false));
      }
    }

    /* perform customized initialization. */
    {
      List headlist = graph.getHeads();
      Iterator headIt = headlist.iterator();

      while (headIt.hasNext()) {
        Object head = headIt.next();
        FlowGraphEdge edge = new FlowGraphEdge(head, head);

        WeightedDirectedSparseGraph initgraph = edgeMap.get(edge);

        initgraph.setTop();
      }
    }

    /*
     * Perform fixed point flow analysis.
     */
    {
      WeightedDirectedSparseGraph beforeFlow = new WeightedDirectedSparseGraph(null, false);

      // DebugMsg.counter1 += allUnits.size();

      while (!changedUnits.isEmpty()) {
        Block s = (Block) changedUnits.removeFirst();
        changedUnitsSet.remove(s);

        // DebugMsg.counter2++;

        /* previousAfterFlow, old-out, it is null initially */
        WeightedDirectedSparseGraph previousBeforeFlow = blockToBeforeFlow.get(s);

        beforeFlow.setVertexes(previousBeforeFlow.getVertexes());

        // Compute and store beforeFlow
        {
          List preds = graph.getPredsOf(s);

          /* the init node */
          if (preds.size() == 0) {
            tmpEdge.changeEndUnits(s, s);
            copy(edgeMap.get(tmpEdge), beforeFlow);
          } else if (preds.size() == 1) {
            tmpEdge.changeEndUnits(preds.get(0), s);
            copy(edgeMap.get(tmpEdge), beforeFlow);

            // widenGraphs(beforeFlow, previousBeforeFlow);
          } else {
            /* has 2 or more preds, Updated by Feng. */
            Object predFlows[] = new Object[preds.size()];

            boolean allUnvisited = true;
            Iterator predIt = preds.iterator();

            int index = 0;

            int lastVisited = 0;

            while (predIt.hasNext()) {
              Object pred = predIt.next();

              tmpEdge.changeEndUnits(pred, s);

              if (!unvisitedNodes.contains(pred)) {
                allUnvisited = false;
                lastVisited = index;
              }

              predFlows[index++] = edgeMap.get(tmpEdge);
            }
            // put the visited node as the first one

            if (allUnvisited) {
              logger.debug("Warning : see all unvisited node");
            } else {
              Object tmp = predFlows[0];
              predFlows[0] = predFlows[lastVisited];
              predFlows[lastVisited] = tmp;
            }

            mergebunch(predFlows, s, previousBeforeFlow, beforeFlow);
          }

          copy(beforeFlow, previousBeforeFlow);
        }

        /*
         * Compute afterFlow and store it. Now we do not have afterFlow, we only have the out edges. Different out edges may
         * have different flow set. It returns back a list of succ units that edge has been changed.
         */
        {
          changedSuccs = flowThrough(beforeFlow, s);
        }

        {
          for (int i = 0; i < changedSuccs.size(); i++) {
            Object succ = changedSuccs.get(i);
            if (!changedUnitsSet.contains(succ)) {
              changedUnits.add(succ);
              changedUnitsSet.add(succ);
            }
          }
        }

        /* Decide to remove or add unit from/to unvisitedNodes set */
        {
          unvisitedNodes.remove(s);
        }
      }
    }

    finish = new Date();
    if (Options.v().debug()) {
      long runtime = finish.getTime() - start.getTime();
      long mins = runtime / 60000;
      long secs = (runtime / 60000) / 1000;
      logger.debug("Doing analysis finished." + " It took " + mins + " mins and " + secs + "secs.");
    }
  }

  /*
   * Flow go through a node, the output will be put into edgeMap, and also the changed succ will be in a list to return back.
   */
  private List<Object> flowThrough(Object inValue, Object unit) {
    ArrayList<Object> changedSuccs = new ArrayList<Object>();

    WeightedDirectedSparseGraph ingraph = (WeightedDirectedSparseGraph) inValue;

    Block block = (Block) unit;

    List succs = block.getSuccs();

    // leave out the last element.
    Unit s = block.getHead();
    Unit nexts = block.getSuccOf(s);
    while (nexts != null) {
      /* deal with array references */
      assertArrayRef(ingraph, s);

      /* deal with normal expressions. */
      assertNormalExpr(ingraph, s);

      s = nexts;
      nexts = block.getSuccOf(nexts);
    }

    // at the end of block, it should update the out edges.
    if (s instanceof IfStmt) {
      if (!assertBranchStmt(ingraph, s, block, succs, changedSuccs)) {
        updateOutEdges(ingraph, block, succs, changedSuccs);
      }
    } else {
      assertArrayRef(ingraph, s);
      assertNormalExpr(ingraph, s);
      updateOutEdges(ingraph, block, succs, changedSuccs);
    }

    return changedSuccs;
  }

  private void assertArrayRef(Object in, Unit unit) {
    if (!(unit instanceof AssignStmt)) {
      return;
    }

    Stmt s = (Stmt) unit;

    WeightedDirectedSparseGraph ingraph = (WeightedDirectedSparseGraph) in;

    /*
     * ArrayRef op = null;
     *
     * Value leftOp = ((AssignStmt)s).getLeftOp(); Value rightOp = ((AssignStmt)s).getRightOp();
     *
     * if (leftOp instanceof ArrayRef) op = (ArrayRef)leftOp;
     *
     * if (rightOp instanceof ArrayRef) op = (ArrayRef)rightOp;
     *
     * if (op == null) return;
     */

    if (!s.containsArrayRef()) {
      return;
    }

    ArrayRef op = s.getArrayRef();

    Value base = (op).getBase();
    Value index = (op).getIndex();

    HashSet livelocals = (HashSet) ailanalysis.getFlowAfter(s);
    if (!livelocals.contains(base) && !livelocals.contains(index)) {
      return;
    }

    if (index instanceof IntConstant) {
      int weight = ((IntConstant) index).value;
      weight = -1 - weight;

      ingraph.addEdge(base, zero, weight);
    } else {
      // add two edges.
      // index <= a.length -1;
      ingraph.addEdge(base, index, -1);

      // index >= 0
      ingraph.addEdge(index, zero, 0);
    }
  }

  private void assertNormalExpr(Object in, Unit s) {

    WeightedDirectedSparseGraph ingraph = (WeightedDirectedSparseGraph) in;

    /* If it is a contains invoke expr, the parameter should be analyzed. */
    if (fieldin) {
      Stmt stmt = (Stmt) s;
      if (stmt.containsInvokeExpr()) {
        HashSet tokills = new HashSet();
        Value expr = stmt.getInvokeExpr();
        List parameters = ((InvokeExpr) expr).getArgs();

        /* kill only the locals in hierarchy. */
        if (strictness == 0) {
          Hierarchy hierarchy = Scene.v().getActiveHierarchy();

          for (int i = 0; i < parameters.size(); i++) {
            Value para = (Value) parameters.get(i);
            Type type = para.getType();
            if (type instanceof RefType) {
              SootClass pclass = ((RefType) type).getSootClass();

              /* then we are looking for the possible types. */
              Iterator<Object> keyIt = localToFieldRef.keySet().iterator();
              while (keyIt.hasNext()) {
                Value local = (Value) keyIt.next();

                Type ltype = local.getType();

                SootClass lclass = ((RefType) ltype).getSootClass();

                if (hierarchy.isClassSuperclassOfIncluding(pclass, lclass)
                    || hierarchy.isClassSuperclassOfIncluding(lclass, pclass)) {
                  HashSet toadd = localToFieldRef.get(local);
                  tokills.addAll(toadd);
                }
              }
            }
          }

          if (expr instanceof InstanceInvokeExpr) {
            Value base = ((InstanceInvokeExpr) expr).getBase();
            Type type = base.getType();
            if (type instanceof RefType) {
              SootClass pclass = ((RefType) type).getSootClass();

              /* then we are looking for the possible types. */
              Iterator<Object> keyIt = localToFieldRef.keySet().iterator();
              while (keyIt.hasNext()) {
                Value local = (Value) keyIt.next();

                Type ltype = local.getType();

                SootClass lclass = ((RefType) ltype).getSootClass();

                if (hierarchy.isClassSuperclassOfIncluding(pclass, lclass)
                    || hierarchy.isClassSuperclassOfIncluding(lclass, pclass)) {
                  HashSet toadd = localToFieldRef.get(local);
                  tokills.addAll(toadd);
                }
              }
            }
          }
        } else if (strictness == 1) {
          /* kill all instance field reference. */
          boolean killall = false;
          if (expr instanceof InstanceInvokeExpr) {
            killall = true;
          } else {
            for (int i = 0; i < parameters.size(); i++) {
              Value para = (Value) parameters.get(i);
              if (para.getType() instanceof RefType) {
                killall = true;
                break;
              }
            }
          }

          if (killall) {
            Iterator<Object> keyIt = localToFieldRef.keySet().iterator();
            while (keyIt.hasNext()) {
              HashSet toadd = localToFieldRef.get(keyIt.next());
              tokills.addAll(toadd);
            }
          }
        } else if (strictness == 2) {
          // tokills.addAll(allFieldRefs);
          HashSet vertexes = ingraph.getVertexes();
          Iterator nodeIt = vertexes.iterator();
          while (nodeIt.hasNext()) {
            Object node = nodeIt.next();
            if (node instanceof FieldRef) {
              ingraph.killNode(node);
            }
          }
        }

        /*
         * Iterator killIt = tokills.iterator(); while (killIt.hasNext()) ingraph.killNode(killIt.next());
         */
      }
    }

    if (arrayin) {
      Stmt stmt = (Stmt) s;

      if (stmt.containsInvokeExpr()) {
        if (strictness == 2) {
          /*
           * Iterator killIt = allArrayRefs.iterator(); while (killIt.hasNext()) { ingraph.killNode(killIt.next()); }
           */
          HashSet vertexes = ingraph.getVertexes();
          Iterator nodeIt = vertexes.iterator();
          while (nodeIt.hasNext()) {
            Object node = nodeIt.next();
            if (node instanceof ArrayRef) {
              ingraph.killNode(node);
            }

            /*
             * if (rectarray) if (node instanceof Array2ndDimensionSymbol) ingraph.killNode(node);
             */
          }
        }
      }
    }

    if (!(s instanceof AssignStmt)) {
      return;
    }

    Value leftOp = ((AssignStmt) s).getLeftOp();
    Value rightOp = ((AssignStmt) s).getRightOp();

    HashSet livelocals = (HashSet) ailanalysis.getFlowAfter(s);

    if (fieldin) {
      if (leftOp instanceof Local) {
        HashSet fieldrefs = localToFieldRef.get(leftOp);

        if (fieldrefs != null) {
          Iterator refsIt = fieldrefs.iterator();
          while (refsIt.hasNext()) {
            Object ref = refsIt.next();
            if (livelocals.contains(ref)) {
              ingraph.killNode(ref);
            }
          }
        }
      } else if (leftOp instanceof InstanceFieldRef) {
        SootField field = ((InstanceFieldRef) leftOp).getField();

        HashSet fieldrefs = fieldToFieldRef.get(field);

        if (fieldrefs != null) {
          Iterator refsIt = fieldrefs.iterator();
          while (refsIt.hasNext()) {
            Object ref = refsIt.next();
            if (livelocals.contains(ref)) {
              ingraph.killNode(ref);
            }
          }
        }
      }
    }

    if (arrayin) {
      /*
       * a = ..; kill all references of a; i = ..; kill all references with index i;
       */
      if (leftOp instanceof Local) {
        /*
         * HashSet arrayrefs = (HashSet)localToArrayRef.get(leftOp);
         *
         * if (arrayrefs != null) { Iterator refsIt = arrayrefs.iterator(); while (refsIt.hasNext()) { Object ref =
         * refsIt.next(); ingraph.killNode(ref); } }
         */
        HashSet vertexes = ingraph.getVertexes();
        Iterator nodeIt = vertexes.iterator();
        while (nodeIt.hasNext()) {
          Object node = nodeIt.next();
          if (node instanceof ArrayRef) {
            Value base = ((ArrayRef) node).getBase();
            Value index = ((ArrayRef) node).getIndex();

            if (base.equals(leftOp) || index.equals(leftOp)) {
              ingraph.killNode(node);
            }
          }

          if (rectarray) {
            if (node instanceof Array2ndDimensionSymbol) {
              Object base = ((Array2ndDimensionSymbol) node).getVar();
              if (base.equals(leftOp)) {
                ingraph.killNode(node);
              }
            }
          }
        }
      } else if (leftOp instanceof ArrayRef) {
        /* kill all array references */
        /*
         * Iterator allrefsIt = allArrayRefs.iterator(); while (allrefsIt.hasNext()) { Object ref = allrefsIt.next();
         * ingraph.killNode(ref); }
         */
        HashSet vertexes = ingraph.getVertexes();
        {
          Iterator nodeIt = vertexes.iterator();
          while (nodeIt.hasNext()) {
            Object node = nodeIt.next();
            if (node instanceof ArrayRef) {
              ingraph.killNode(node);
            }
          }
        }

        /* only when multiarray was given a new value to its sub dimension, we kill all second dimensions of arrays */
        /*
         * if (rectarray) { Value base = ((ArrayRef)leftOp).getBase();
         *
         * if (multiarraylocals.contains(base)) { Iterator nodeIt = vertexes.iterator(); while (nodeIt.hasNext()) { Object
         * node = nodeIt.next(); if (node instanceof Array2ndDimensionSymbol) ingraph.killNode(node); } } }
         */
      }
    }

    if (!livelocals.contains(leftOp) && !livelocals.contains(rightOp)) {
      return;
    }

    // i = i;
    if (rightOp.equals(leftOp)) {
      return;
    }

    if (csin) {
      HashSet exprs = localToExpr.get(leftOp);
      if (exprs != null) {
        Iterator exprIt = exprs.iterator();
        while (exprIt.hasNext()) {
          ingraph.killNode(exprIt.next());
        }
      }
    }

    // i = i + c; is special
    if (rightOp instanceof AddExpr) {
      Value op1 = ((AddExpr) rightOp).getOp1();
      Value op2 = ((AddExpr) rightOp).getOp2();

      if (op1 == leftOp && op2 instanceof IntConstant) {
        int inc_w = ((IntConstant) op2).value;
        ingraph.updateWeight(leftOp, inc_w);
        return;
      } else if (op2 == leftOp && op1 instanceof IntConstant) {
        int inc_w = ((IntConstant) op1).value;
        ingraph.updateWeight(leftOp, inc_w);
        return;
      }
    }

    // i = i - c; is also special
    if (rightOp instanceof SubExpr) {
      Value op1 = ((SubExpr) rightOp).getOp1();
      Value op2 = ((SubExpr) rightOp).getOp2();

      if ((op1 == leftOp) && (op2 instanceof IntConstant)) {
        int inc_w = -((IntConstant) op2).value;
        ingraph.updateWeight(leftOp, inc_w);
        return;
      }
    }

    // i = j; i = j + c; i = c + j; need two operations, kill node and add new relationship.
    // kill left hand side,

    ingraph.killNode(leftOp);

    // add new relationship.

    // i = c;
    if (rightOp instanceof IntConstant) {
      int inc_w = ((IntConstant) rightOp).value;
      ingraph.addMutualEdges(zero, leftOp, inc_w);
      return;
    }

    // i = j;
    if (rightOp instanceof Local) {
      ingraph.addMutualEdges(rightOp, leftOp, 0);
      return;
    }

    if (rightOp instanceof FieldRef) {
      if (fieldin) {
        ingraph.addMutualEdges(rightOp, leftOp, 0);
      }

      if (classfieldin) {
        SootField field = ((FieldRef) rightOp).getField();
        IntValueContainer flength = (IntValueContainer) cfield.getFieldInfo(field);

        if (flength != null) {
          if (flength.isInteger()) {
            ingraph.addMutualEdges(zero, leftOp, flength.getValue());
          }
        }
      }

      return;
    }

    /*
     * if (rectarray) { Type leftType = leftOp.getType();
     *
     * if ((leftType instanceof ArrayType) && (rightOp instanceof ArrayRef)) { Local base =
     * (Local)((ArrayRef)rightOp).getBase();
     *
     * SymbolArrayLength sv = (SymbolArrayLength)lra_analysis.getSymbolLengthAt(base, s); if (sv != null) {
     * ingraph.addMutualEdges(leftOp, sv.next(), 0); } } }
     */

    if (arrayin) {
      if (rightOp instanceof ArrayRef) {
        ingraph.addMutualEdges(rightOp, leftOp, 0);

        if (rectarray) {
          Value base = ((ArrayRef) rightOp).getBase();

          if (rectarrayset.contains(base)) {
            ingraph.addMutualEdges(leftOp, Array2ndDimensionSymbol.v(base), 0);
          }
        }

        return;
      }
    }

    if (csin) {
      Value rhs = rightOp;

      if (rhs instanceof BinopExpr) {
        Value op1 = ((BinopExpr) rhs).getOp1();
        Value op2 = ((BinopExpr) rhs).getOp2();

        if (rhs instanceof AddExpr) {
          if ((op1 instanceof Local) && (op2 instanceof Local)) {
            ingraph.addMutualEdges(rhs, leftOp, 0);
            return;
          }
        } else if (rhs instanceof MulExpr) {
          if ((op1 instanceof Local) || (op2 instanceof Local)) {
            ingraph.addMutualEdges(rhs, leftOp, 0);
            return;
          }
        } else if (rhs instanceof SubExpr) {
          if (op2 instanceof Local) {
            ingraph.addMutualEdges(rhs, leftOp, 0);
            return;
          }
        }
      }
    }

    // i = j + c; or i = c + j;
    if (rightOp instanceof AddExpr) {
      Value op1 = ((AddExpr) rightOp).getOp1();
      Value op2 = ((AddExpr) rightOp).getOp2();

      if ((op1 instanceof Local) && (op2 instanceof IntConstant)) {
        int inc_w = ((IntConstant) op2).value;
        ingraph.addMutualEdges(op1, leftOp, inc_w);
        return;
      }

      if ((op2 instanceof Local) && (op1 instanceof IntConstant)) {
        int inc_w = ((IntConstant) op1).value;
        ingraph.addMutualEdges(op2, leftOp, inc_w);
        return;
      }
    }

    // only i = j - c was considered.
    if (rightOp instanceof SubExpr) {
      Value op1 = ((SubExpr) rightOp).getOp1();
      Value op2 = ((SubExpr) rightOp).getOp2();

      if ((op1 instanceof Local) && (op2 instanceof IntConstant)) {
        int inc_w = -((IntConstant) op2).value;
        ingraph.addMutualEdges(op1, leftOp, inc_w);
        return;
      }
    }

    // new experessions can also generate relationship
    // a = new A[i]; a = new A[c];
    if (rightOp instanceof NewArrayExpr) {
      Value size = ((NewArrayExpr) rightOp).getSize();
      if (size instanceof Local) {
        ingraph.addMutualEdges(size, leftOp, 0);
        return;
      }

      if (size instanceof IntConstant) {
        int inc_w = ((IntConstant) size).value;
        ingraph.addMutualEdges(zero, leftOp, inc_w);
        return;
      }
    }

    // a = new A[i][].. ; a = new A[c]...;
    if (rightOp instanceof NewMultiArrayExpr) {
      Value size = ((NewMultiArrayExpr) rightOp).getSize(0);
      if (size instanceof Local) {
        ingraph.addMutualEdges(size, leftOp, 0);
      } else if (size instanceof IntConstant) {
        int inc_w = ((IntConstant) size).value;
        ingraph.addMutualEdges(zero, leftOp, inc_w);
      }

      if (arrayin && rectarray) {
        if (((NewMultiArrayExpr) rightOp).getSizeCount() > 1) {
          size = ((NewMultiArrayExpr) rightOp).getSize(1);
          if (size instanceof Local) {
            ingraph.addMutualEdges(size, Array2ndDimensionSymbol.v(leftOp), 0);
          } else if (size instanceof IntConstant) {
            int inc_w = ((IntConstant) size).value;
            ingraph.addMutualEdges(zero, Array2ndDimensionSymbol.v(leftOp), inc_w);
          }
        }
      }

      return;
    }

    // i = a.length
    if (rightOp instanceof LengthExpr) {
      Value base = ((LengthExpr) rightOp).getOp();
      ingraph.addMutualEdges(base, leftOp, 0);
      return;
    }
  }

  /*
   * assert the branch statement return true, if the out condition changed, false, otherwise
   */
  private boolean assertBranchStmt(Object in, Unit s, Block current, List succs, List<Object> changedSuccs) {
    IfStmt ifstmt = (IfStmt) s;

    // take out the condition.
    Value cmpcond = ifstmt.getCondition();

    if (!(cmpcond instanceof ConditionExpr)) {
      return false;
    }

    // how may succs?
    if (succs.size() != 2) {
      return false;
    }

    Stmt targetUnit = ifstmt.getTarget();
    Block targetBlock = (Block) succs.get(0);
    Block nextBlock = (Block) succs.get(1);

    if (!targetUnit.equals(targetBlock.getHead())) {
      Block swap = targetBlock;
      targetBlock = nextBlock;
      nextBlock = swap;
    }

    Value op1 = ((ConditionExpr) cmpcond).getOp1();
    Value op2 = ((ConditionExpr) cmpcond).getOp2();

    HashSet livelocals = (HashSet) ailanalysis.getFlowAfter(s);

    if (!livelocals.contains(op1) && !livelocals.contains(op2)) {
      return false;
    }

    WeightedDirectedSparseGraph ingraph = (WeightedDirectedSparseGraph) in;

    WeightedDirectedSparseGraph targetgraph = ingraph.dup();

    // EqExpr, GeExpr, GtExpr, LeExpr, LtExpr, NeExpr
    if ((cmpcond instanceof EqExpr) || (cmpcond instanceof NeExpr)) {
      Object node1 = op1, node2 = op2;
      int weight = 0;
      if (node1 instanceof IntConstant) {
        weight = ((IntConstant) node1).value;
        node1 = zero;
      }
      if (node2 instanceof IntConstant) {
        weight = ((IntConstant) node2).value;
        node2 = zero;
      }

      if (node1 == node2) {
        return false;
      }

      if (cmpcond instanceof EqExpr) {
        targetgraph.addMutualEdges(node1, node2, weight);
      } else {
        ingraph.addMutualEdges(node1, node2, weight);
      }
    } else if ((cmpcond instanceof GtExpr) ||
    // i >= j
        (cmpcond instanceof GeExpr)) {
      Object node1 = op1, node2 = op2;
      int weight = 0;

      if (node1 instanceof IntConstant) {
        weight += ((IntConstant) node1).value;
        node1 = zero;
      }

      if (node2 instanceof IntConstant) {
        weight -= ((IntConstant) node2).value;
        node2 = zero;
      }

      if (node1 == node2) {
        return false;
      }

      if (cmpcond instanceof GtExpr) {
        targetgraph.addEdge(node1, node2, weight - 1);
        ingraph.addEdge(node2, node1, -weight);
      } else {
        targetgraph.addEdge(node1, node2, weight);
        ingraph.addEdge(node2, node1, -weight - 1);
      }
    } else if ((cmpcond instanceof LtExpr) || (cmpcond instanceof LeExpr)) {
      // i < j
      Object node1 = op1, node2 = op2;
      int weight = 0;

      if (node1 instanceof IntConstant) {
        weight -= ((IntConstant) node1).value;
        node1 = zero;
      }

      if (node2 instanceof IntConstant) {
        weight += ((IntConstant) node2).value;
        node2 = zero;
      }

      if (node1 == node2) {
        return false;
      }

      if (cmpcond instanceof LtExpr) {
        targetgraph.addEdge(node2, node1, weight - 1);
        ingraph.addEdge(node1, node2, -weight);
      } else {
        targetgraph.addEdge(node2, node1, weight);
        ingraph.addEdge(node1, node2, -weight - 1);
      }
    } else {
      return false;
    }

    // update out edges and changed succs.
    // targetgraph -> targetBlock

    FlowGraphEdge targetEdge = new FlowGraphEdge(current, targetBlock);
    WeightedDirectedSparseGraph prevtarget = edgeMap.get(targetEdge);
    boolean changed = false;

    targetgraph.makeShortestPathGraph();

    WeightedDirectedSparseGraph tmpgraph = new WeightedDirectedSparseGraph(prevtarget.getVertexes(), true);

    copy(targetgraph, tmpgraph);

    if (!tmpgraph.equals(prevtarget)) {
      prevtarget.replaceAllEdges(tmpgraph);
      changed = true;
    }

    if (changed) {
      changedSuccs.add(targetBlock);
    }

    // ingraph -> nextBlock
    FlowGraphEdge nextEdge = new FlowGraphEdge(current, nextBlock);
    WeightedDirectedSparseGraph prevnext = edgeMap.get(nextEdge);
    changed = false;

    ingraph.makeShortestPathGraph();

    tmpgraph = new WeightedDirectedSparseGraph(prevnext.getVertexes(), true);

    copy(ingraph, tmpgraph);

    if (!tmpgraph.equals(prevnext)) {
      prevnext.replaceAllEdges(tmpgraph);
      changed = true;
    }

    if (changed) {
      changedSuccs.add(nextBlock);
    }

    return true;

  }

  private void updateOutEdges(Object in, Block current, List succs, List<Object> changedSuccs) {
    WeightedDirectedSparseGraph ingraph = (WeightedDirectedSparseGraph) in;

    ingraph.makeShortestPathGraph();

    for (int i = 0; i < succs.size(); i++) {
      Object next = succs.get(i);
      FlowGraphEdge nextEdge = new FlowGraphEdge(current, next);

      WeightedDirectedSparseGraph prevs = edgeMap.get(nextEdge);
      boolean changed = false;

      /* equals should be used to take out sub graph first */

      WeightedDirectedSparseGraph tmpgraph = new WeightedDirectedSparseGraph(prevs.getVertexes(), true);

      copy(ingraph, tmpgraph);

      if (!tmpgraph.equals(prevs)) {
        prevs.replaceAllEdges(tmpgraph);
        changed = true;
      }

      if (changed) {
        changedSuccs.add(next);
      }
    }
  }

  protected void copy(Object from, Object to) {
    WeightedDirectedSparseGraph fromgraph = (WeightedDirectedSparseGraph) from;
    WeightedDirectedSparseGraph tograph = (WeightedDirectedSparseGraph) to;

    tograph.clear();
    tograph.addBoundedAll(fromgraph);
  }

  protected void widenGraphs(Object current, Object before) {
    WeightedDirectedSparseGraph curgraphs = (WeightedDirectedSparseGraph) current;
    WeightedDirectedSparseGraph pregraphs = (WeightedDirectedSparseGraph) before;

    curgraphs.widenEdges(pregraphs);
    curgraphs.makeShortestPathGraph();
  }
}
