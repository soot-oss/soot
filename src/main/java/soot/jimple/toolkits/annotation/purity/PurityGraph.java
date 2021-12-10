package soot.jimple.toolkits.annotation.purity;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Antoine Mine
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Local;
import soot.RefLikeType;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.jimple.Stmt;
import soot.util.HashMultiMap;
import soot.util.MultiMap;
import soot.util.dot.DotGraph;
import soot.util.dot.DotGraphEdge;
import soot.util.dot.DotGraphNode;

/**
 * Purity graphs are mutable structures that are updated in-place. You can safely hash graphs. Equality comparison means
 * isomorphism (equal nodes, equal edges).
 * 
 * Modifications with respect to the article:
 *
 * - "unanalizable call" are treated by first constructing a conservative callee graph where all parameters escape globally
 * and return points to the global node, and then applying the standard analysable call construction
 *
 * - unanalysable calls add a mutation on the global node; the "field" is named "outside-world" and models the mutation of
 * any static field, but also side-effects by native methods, such as I/O, that make methods impure (see below).
 *
 * - Whenever a method mutates the global node, it is marked as "impure" (this can be due to a side-effect or static field
 * mutation), even if the global node is not reachable from parameter nodes through outside edges. It seems to me it was a
 * defect from the article ? TODO: see if we must take the global node into account also when stating whether a parameter is
 * read-only or safe.
 *
 * - "simplifyXXX" functions are experimental... they may be unsound, and thus, not used now.
 *
 * NOTE: A lot of precision degradation comes from sequences of the form this.field = y; z = this.field in initialisers: the
 * second statement creates a load node because, as a parameter, this may have escaped and this.field may be externally
 * modified in-between the two instructions. I am not sure this can actually happen in an initialiser... in a a function
 * called directly and only by initialisers.
 *
 * For the moment, summary of unanalised methods are either pure, completely impure (modify args & side-effects) or partially
 * impure (modify args but not the global node). We should really be able to specify more precisely which arguments are r/o
 * or safe within this methods. E.g., the analysis java.lang.String: void getChars(int,int,char [],int) imprecisely finds
 * that this is not safe (because of the internal call to System.arraycopy that, in general, may introduce aliases) => it
 * pollutes many things (e.g., StringBuffer append(String), and thus, exception constructors, etc.)
 *
 */
public class PurityGraph {
  private static final Logger logger = LoggerFactory.getLogger(PurityGraph.class);
  public static final boolean doCheck = false;

  // Caching: this seems to actually improve both speed and memory consumption!
  private static final Map<PurityNode, PurityNode> nodeCache = new HashMap<PurityNode, PurityNode>();
  private static final Map<PurityEdge, PurityEdge> edgeCache = new HashMap<PurityEdge, PurityEdge>();

  // A parameter (or this) can be: - read and write - read only - safe (read only & no externally visible alias is created)
  static final int PARAM_RW = 0;
  static final int PARAM_RO = 1;
  static final int PARAM_SAFE = 2;

  // Simple statistics on maximal graph sizes.
  private static int maxInsideNodes = 0;
  private static int maxLoadNodes = 0;
  private static int maxInsideEdges = 0;
  private static int maxOutsideEdges = 0;
  private static int maxMutated = 0;

  protected Set<PurityNode> nodes; // all nodes
  protected Set<PurityNode> paramNodes; // only parameter & this nodes
  protected MultiMap<PurityNode, PurityEdge> edges; // source node -> edges
  protected MultiMap<Local, PurityNode> locals; // local -> nodes
  protected Set<PurityNode> ret; // return -> nodes
  protected Set<PurityNode> globEscape; // nodes escaping globally
  protected MultiMap<PurityNode, PurityEdge> backEdges; // target node -> edges
  protected MultiMap<PurityNode, Local> backLocals; // target node -> local node sources
  protected MultiMap<PurityNode, String> mutated; // node -> field such that (node,field) is mutated

  /**
   * Initially empty graph.
   */
  PurityGraph() {
    // nodes & paramNodes are added lazily
    this.nodes = new HashSet<PurityNode>();
    this.paramNodes = new HashSet<PurityNode>();
    this.edges = new HashMultiMap<PurityNode, PurityEdge>();
    this.locals = new HashMultiMap<Local, PurityNode>();
    this.ret = new HashSet<PurityNode>();
    this.globEscape = new HashSet<PurityNode>();
    this.backEdges = new HashMultiMap<PurityNode, PurityEdge>();
    this.backLocals = new HashMultiMap<PurityNode, Local>();
    this.mutated = new HashMultiMap<PurityNode, String>();
    if (doCheck) {
      sanityCheck();
    }
  }

  /**
   * Copy constructor.
   */
  PurityGraph(PurityGraph x) {
    this.nodes = new HashSet<PurityNode>(x.nodes);
    this.paramNodes = new HashSet<PurityNode>(x.paramNodes);
    this.edges = new HashMultiMap<PurityNode, PurityEdge>(x.edges);
    this.locals = new HashMultiMap<Local, PurityNode>(x.locals);
    this.ret = new HashSet<PurityNode>(x.ret);
    this.globEscape = new HashSet<PurityNode>(x.globEscape);
    this.backEdges = new HashMultiMap<PurityNode, PurityEdge>(x.backEdges);
    this.backLocals = new HashMultiMap<PurityNode, Local>(x.backLocals);
    this.mutated = new HashMultiMap<PurityNode, String>(x.mutated);
    if (doCheck) {
      sanityCheck();
    }
  }

  @Override
  public int hashCode() {
    return nodes.hashCode()
        // + paramNodes.hashCode() // redundant info
        + edges.hashCode() + locals.hashCode() + ret.hashCode() + globEscape.hashCode()
        // + backEdges.hashCode() // redundant info
        // + backLocals.hashCode() // redundant info
        + mutated.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof PurityGraph)) {
      return false;
    }
    PurityGraph g = (PurityGraph) o;
    return nodes.equals(g.nodes)
        // && paramNodes.equals(g.paramNodes) // redundant info
        && edges.equals(g.edges) && locals.equals(g.locals) && ret.equals(g.ret) && globEscape.equals(g.globEscape)
        // && backEdges.equals(g.backEdges) // redundant info
        // && backLocals.equals(g.backLocals) // redundant info
        && mutated.equals(g.mutated);
  }

  private static PurityNode cacheNode(PurityNode p) {
    if (!nodeCache.containsKey(p)) {
      nodeCache.put(p, p);
    }
    return nodeCache.get(p);
  }

  private static PurityEdge cacheEdge(PurityEdge e) {
    if (!edgeCache.containsKey(e)) {
      edgeCache.put(e, e);
    }
    return edgeCache.get(e);
  }

  /**
   * Conservative constructor for unanalysable calls.
   *
   * <p>
   * Note: this gives a valid summary for all native methods, including Thread.start().
   *
   * @param withEffect
   *          add a mutated abstract field for the global node to account for side-effects in the environment (I/O, etc.).
   */
  public static PurityGraph conservativeGraph(SootMethod m, boolean withEffect) {
    PurityGraph g = new PurityGraph();
    PurityNode glob = PurityGlobalNode.node;
    g.nodes.add(glob);

    // parameters & this escape globally
    int i = 0;
    for (Type next : m.getParameterTypes()) {
      if (next instanceof RefLikeType) {
        PurityNode n = cacheNode(new PurityParamNode(i));
        g.globEscape.add(n);
        g.nodes.add(n);
        g.paramNodes.add(n);
      }
      i++;
    }

    // return value escapes globally
    if (m.getReturnType() instanceof RefLikeType) {
      g.ret.add(glob);
    }

    // add a side-effect on the environment
    // added by [AM]
    if (withEffect) {
      g.mutated.put(glob, "outside-world");
    }

    if (doCheck) {
      g.sanityCheck();
    }
    return g;
  }

  /**
   * Special constructor for "pure" methods returning a fresh object. (or simply pure if returns void or primitive).
   */
  public static PurityGraph freshGraph(SootMethod m) {
    PurityGraph g = new PurityGraph();
    if (m.getReturnType() instanceof RefLikeType) {
      PurityNode n = cacheNode(new PurityMethodNode(m));
      g.ret.add(n);
      g.nodes.add(n);
    }
    if (doCheck) {
      g.sanityCheck();
    }
    return g;
  }

  /**
   * Replace the current graph with its union with arg. arg is not modified.
   */
  void union(PurityGraph arg) {
    this.nodes.addAll(arg.nodes);
    this.paramNodes.addAll(arg.paramNodes);
    this.edges.putAll(arg.edges);
    this.locals.putAll(arg.locals);
    this.ret.addAll(arg.ret);
    this.globEscape.addAll(arg.globEscape);
    this.backEdges.putAll(arg.backEdges);
    this.backLocals.putAll(arg.backLocals);
    this.mutated.putAll(arg.mutated);
    if (doCheck) {
      sanityCheck();
    }
  }

  /**
   * Sanity check. Used internally for debugging!
   */
  protected void sanityCheck() {
    boolean err = false;
    for (PurityNode src : edges.keySet()) {
      for (PurityEdge e : edges.get(src)) {
        if (!src.equals(e.getSource())) {
          logger.debug("invalid edge source " + e + ", should be " + src);
          err = true;
        }
        if (!nodes.contains(e.getSource())) {
          logger.debug("nodes does not contain edge source " + e);
          err = true;
        }
        if (!nodes.contains(e.getTarget())) {
          logger.debug("nodes does not contain edge target " + e);
          err = true;
        }
        if (!backEdges.get(e.getTarget()).contains(e)) {
          logger.debug("backEdges does not contain edge " + e);
          err = true;
        }
        if (!e.isInside() && !e.getTarget().isLoad()) {
          logger.debug("target of outside edge is not a load node " + e);
          err = true;
        }
      }
    }
    for (PurityNode dst : backEdges.keySet()) {
      for (PurityEdge e : backEdges.get(dst)) {
        if (!dst.equals(e.getTarget())) {
          logger.debug("invalid backEdge dest " + e + ", should be " + dst);
          err = true;
        }
        if (!edges.get(e.getSource()).contains(e)) {
          logger.debug("backEdge not in edges " + e);
          err = true;
        }
      }
    }
    for (PurityNode n : nodes) {
      if (n.isParam() && !paramNodes.contains(n)) {
        logger.debug("paramNode not in paramNodes " + n);
        err = true;
      }
    }
    for (PurityNode n : paramNodes) {
      if (!n.isParam()) {
        logger.debug("paramNode contains a non-param node " + n);
        err = true;
      }
      if (!nodes.contains(n)) {
        logger.debug("paramNode not in nodes " + n);
        err = true;
      }
    }
    for (PurityNode n : globEscape) {
      if (!nodes.contains(n)) {
        logger.debug("globEscape not in nodes " + n);
        err = true;
      }
    }
    for (Local l : locals.keySet()) {
      for (PurityNode n : locals.get(l)) {
        if (!nodes.contains(n)) {
          logger.debug("target of local node in nodes " + l + " / " + n);
          err = true;
        }
        if (!backLocals.get(n).contains(l)) {
          logger.debug("backLocals does contain local " + l + " / " + n);
          err = true;
        }
      }
    }
    for (PurityNode n : backLocals.keySet()) {
      for (Local l : backLocals.get(n)) {
        if (!nodes.contains(n)) {
          logger.debug("backLocal node not in in nodes " + l + " / " + n);
          err = true;
        }
        if (!locals.get(l).contains(n)) {
          logger.debug("locals does contain backLocal " + l + " / " + n);
          err = true;
        }
      }
    }
    for (PurityNode n : ret) {
      if (!nodes.contains(n)) {
        logger.debug("target of ret not in nodes " + n);
        err = true;
      }
    }
    for (PurityNode n : mutated.keySet()) {
      if (!nodes.contains(n)) {
        logger.debug("mutated node not in nodes " + n);
        err = true;
      }
    }
    if (err) {
      dump();
      DotGraph dot = new DotGraph("sanityCheckFailure");
      fillDotGraph("chk", dot);
      dot.plot("sanityCheckFailure.dot");
      throw new Error("PurityGraph sanity check failed!!!");
    }
  }

  ////////////////////////
  // ESCAPE INFORMATION //
  ////////////////////////

  protected void internalPassEdges(Set<PurityEdge> toColor, Set<PurityNode> dest, boolean consider_inside) {
    for (PurityEdge edge : toColor) {
      if (consider_inside || !edge.isInside()) {
        PurityNode node = edge.getTarget();
        if (!dest.contains(node)) {
          dest.add(node);
          internalPassEdges(edges.get(node), dest, consider_inside);
        }
      }
    }
  }

  protected void internalPassNode(PurityNode node, Set<PurityNode> dest, boolean consider_inside) {
    if (!dest.contains(node)) {
      dest.add(node);
      internalPassEdges(edges.get(node), dest, consider_inside);
    }
  }

  protected void internalPassNodes(Set<PurityNode> toColor, Set<PurityNode> dest, boolean consider_inside) {
    for (PurityNode n : toColor) {
      internalPassNode(n, dest, consider_inside);
    }
  }

  protected Set<PurityNode> getEscaping() {
    Set<PurityNode> escaping = new HashSet<PurityNode>();
    internalPassNodes(ret, escaping, true);
    internalPassNodes(globEscape, escaping, true);
    internalPassNode(PurityGlobalNode.node, escaping, true);
    internalPassNodes(paramNodes, escaping, true);
    return escaping;
  }

  /**
   * Call this on the merge of graphs at all return points of a method to know whether the method is pure.
   */
  public boolean isPure() {
    if (!mutated.get(PurityGlobalNode.node).isEmpty()) {
      return false;
    }
    Set<PurityNode> A = new HashSet<PurityNode>();
    Set<PurityNode> B = new HashSet<PurityNode>();
    internalPassNodes(paramNodes, A, false);
    internalPassNodes(globEscape, B, true);
    internalPassNode(PurityGlobalNode.node, B, true);
    for (PurityNode n : A) {
      if (B.contains(n) || !mutated.get(n).isEmpty()) {
        return false;
      }
    }
    return true;
  }

  /**
   * We use a less restrictive notion of purity for constructors: pure constructors can mutate fields of this.
   *
   * @see isPure
   */
  public boolean isPureConstructor() {
    if (!mutated.get(PurityGlobalNode.node).isEmpty()) {
      return false;
    }
    Set<PurityNode> A = new HashSet<PurityNode>();
    Set<PurityNode> B = new HashSet<PurityNode>();
    internalPassNodes(paramNodes, A, false);
    internalPassNodes(globEscape, B, true);
    internalPassNode(PurityGlobalNode.node, B, true);
    PurityNode th = PurityThisNode.node;
    for (PurityNode n : A) {
      if (B.contains(n) || (!n.equals(th) && !mutated.get(n).isEmpty())) {
        return false;
      }
    }
    return true;
  }

  protected int internalParamStatus(PurityNode p) {
    if (!paramNodes.contains(p)) {
      return PARAM_RW;
    }

    Set<PurityNode> S1 = new HashSet<PurityNode>();
    internalPassNode(p, S1, false);
    for (PurityNode n : S1) {
      if (n.isLoad() || n.equals(p)) {
        if (!mutated.get(n).isEmpty() || globEscape.contains(n)) {
          return PARAM_RW;
        }
      }
    }

    Set<PurityNode> S2 = new HashSet<PurityNode>();
    internalPassNodes(ret, S2, true);
    internalPassNodes(paramNodes, S2, true);
    for (PurityNode n : S2) {
      for (PurityEdge e : edges.get(n)) {
        if (e.isInside() && S1.contains(e.getTarget())) {
          return PARAM_RO;
        }
      }
    }

    return PARAM_SAFE;
  }

  /**
   * Call this on the merge of graphs at all return points of a method to know whether an object passed as method parameter
   * is read only (PARAM_RO), read write (PARAM_RW), or safe (PARAM_SAFE). Returns PARAM_RW for primitive-type parameters.
   */
  public int paramStatus(int param) {
    return internalParamStatus(cacheNode(new PurityParamNode(param)));
  }

  /**
   * @see isParamReadOnly
   */
  public int thisStatus() {
    return internalParamStatus(PurityThisNode.node);
  }

  /////////////////////////
  // GRAPH MANUPULATIONS //
  /////////////////////////

  @Override
  public Object clone() {
    return new PurityGraph(this);
  }

  // utility functions to update local / backLocals constitently
  protected final boolean localsRemove(Local local) {
    for (PurityNode node : locals.get(local)) {
      backLocals.remove(node, local);
    }
    return locals.remove(local);
  }

  protected final boolean localsPut(Local local, PurityNode node) {
    backLocals.put(node, local);
    return locals.put(local, node);
  }

  protected final boolean localsPutAll(Local local, Set<PurityNode> nodes) {
    for (PurityNode node : nodes) {
      backLocals.put(node, local);
    }
    return locals.putAll(local, nodes);
  }

  /** Utility function to remove a node & all adjacent edges */
  protected final void removeNode(PurityNode n) {
    for (PurityEdge e : edges.get(n)) {
      backEdges.remove(e.getTarget(), e);
    }
    for (PurityEdge e : backEdges.get(n)) {
      edges.remove(e.getSource(), e);
    }
    for (Local l : backLocals.get(n)) {
      locals.remove(l, n);
    }
    ret.remove(n);
    edges.remove(n);
    backEdges.remove(n);
    backLocals.remove(n);
    nodes.remove(n);
    paramNodes.remove(n);
    globEscape.remove(n);
    mutated.remove(n);
  }

  /** Utility function to merge node src into dst; src is removed */
  protected final void mergeNodes(PurityNode src, PurityNode dst) {
    for (PurityEdge e : new ArrayList<PurityEdge>(edges.get(src))) {
      PurityNode n = e.getTarget();
      if (n.equals(src)) {
        n = dst;
      }
      PurityEdge ee = cacheEdge(new PurityEdge(dst, e.getField(), n, e.isInside()));
      edges.remove(src, e);
      edges.put(dst, ee);
      backEdges.remove(n, e);
      backEdges.put(n, ee);
    }
    for (PurityEdge e : new ArrayList<PurityEdge>(backEdges.get(src))) {
      PurityNode n = e.getSource();
      if (n.equals(src)) {
        n = dst;
      }
      PurityEdge ee = cacheEdge(new PurityEdge(n, e.getField(), dst, e.isInside()));
      edges.remove(n, e);
      edges.put(n, ee);
      backEdges.remove(src, e);
      backEdges.put(dst, ee);
    }
    for (Local l : new ArrayList<Local>(backLocals.get(src))) {
      locals.remove(l, src);
      backLocals.remove(src, l);
      locals.put(l, dst);
      backLocals.put(dst, l);
    }
    {
      Set<String> m = mutated.get(src);
      mutated.remove(src);
      mutated.putAll(dst, m);
    }
    if (ret.contains(src)) {
      ret.remove(src);
      ret.add(dst);
    }
    if (globEscape.contains(src)) {
      globEscape.remove(src);
      globEscape.add(dst);
    }
    nodes.remove(src);
    nodes.add(dst);
    paramNodes.remove(src);
    if (dst.isParam()) {
      paramNodes.add(dst);
    }
  }

  /** Experimental simplification: merge redundant load nodes. */
  void simplifyLoad() {
    for (PurityNode p : new ArrayList<PurityNode>(nodes)) {
      Map<String, PurityNode> fmap = new HashMap<String, PurityNode>();
      for (PurityEdge e : new ArrayList<PurityEdge>(edges.get(p))) {
        PurityNode tgt = e.getTarget();
        if (!e.isInside() && !tgt.equals(p)) {
          String f = e.getField();
          if (fmap.containsKey(f) && nodes.contains(fmap.get(f))) {
            mergeNodes(tgt, fmap.get(f));
          } else {
            fmap.put(f, tgt);
          }
        }
      }
    }
    if (doCheck) {
      sanityCheck();
    }
  }

  /**
   * Experimental simplification: remove inside nodes not reachable from escaping nodes (params, ret, globEscape) or load
   * nodes.
   */
  void simplifyInside() {
    Set<PurityNode> r = new HashSet<PurityNode>();
    internalPassNodes(paramNodes, r, true);
    internalPassNodes(ret, r, true);
    internalPassNodes(globEscape, r, true);
    internalPassNode(PurityGlobalNode.node, r, true);
    for (PurityNode n : nodes) {
      if (n.isLoad()) {
        internalPassNode(n, r, true);
      }
    }
    for (PurityNode n : new ArrayList<PurityNode>(nodes)) {
      if (n.isInside() && !r.contains(n)) {
        removeNode(n);
      }
    }
    if (doCheck) {
      sanityCheck();
    }
  }

  /**
   * Remove all local bindings (except ret). This info is indeed superfluous on summary purity graphs representing the effect
   * of a method. This saves a little memory, but also, simplify summary graph drawings a lot!
   *
   * DO NOT USE DURING INTRA-PROCEDURAL ANALYSIS!
   */
  void removeLocals() {
    this.locals = new HashMultiMap<Local, PurityNode>();
    this.backLocals = new HashMultiMap<PurityNode, Local>();
  }

  /** Copy assignment left = right. */
  void assignParamToLocal(int right, Local left) {
    // strong update on local
    PurityNode node = cacheNode(new PurityParamNode(right));
    localsRemove(left);
    localsPut(left, node);
    nodes.add(node);
    paramNodes.add(node);
    if (doCheck) {
      sanityCheck();
    }
  }

  /** Copy assignment left = this. */
  void assignThisToLocal(Local left) {
    // strong update on local
    PurityNode node = PurityThisNode.node;
    localsRemove(left);
    localsPut(left, node);
    nodes.add(node);
    paramNodes.add(node);
    if (doCheck) {
      sanityCheck();
    }
  }

  /** Copy assignment left = right. */
  void assignLocalToLocal(Local right, Local left) {
    // strong update on local
    localsRemove(left);
    localsPutAll(left, locals.get(right));
    if (doCheck) {
      sanityCheck();
    }
  }

  /** return right statement . */
  void returnLocal(Local right) {
    // strong update on ret
    ret.clear();
    ret.addAll(locals.get(right));
    if (doCheck) {
      sanityCheck();
    }
  }

  /**
   * Load non-static: left = right.field, or left = right[?] if field is [].
   */
  void assignFieldToLocal(Stmt stmt, Local right, String field, Local left) {
    Set<PurityNode> esc = new HashSet<PurityNode>();
    Set<PurityNode> escaping = getEscaping();

    // strong update on local
    localsRemove(left);
    for (PurityNode nodeRight : locals.get(right)) {
      for (PurityEdge edge : edges.get(nodeRight)) {
        if (edge.isInside() && edge.getField().equals(field)) {
          localsPut(left, edge.getTarget());
        }
      }

      if (escaping.contains(nodeRight)) {
        esc.add(nodeRight);
      }
    }

    if (!esc.isEmpty()) {
      // right can escape

      // we add a label load node & outside edges
      PurityNode loadNode = cacheNode(new PurityStmtNode(stmt, false));
      nodes.add(loadNode);
      for (PurityNode node : esc) {
        PurityEdge edge = cacheEdge(new PurityEdge(node, field, loadNode, false));
        if (edges.put(node, edge)) {
          backEdges.put(loadNode, edge);
        }
      }
      localsPut(left, loadNode);
    }
    if (doCheck) {
      sanityCheck();
    }
  }

  /**
   * Store non-static: left.field = right, or left[?] = right if field is [].
   */
  void assignLocalToField(Local right, Local left, String field) {
    for (PurityNode nodeLeft : locals.get(left)) {
      for (PurityNode nodeRight : locals.get(right)) {
        PurityEdge edge = cacheEdge(new PurityEdge(nodeLeft, field, nodeRight, true));
        if (edges.put(nodeLeft, edge)) {
          backEdges.put(nodeRight, edge);
        }
      }
      if (!nodeLeft.isInside()) {
        mutated.put(nodeLeft, field);
      }
    }
    // weak update on inside edges
    if (doCheck) {
      sanityCheck();
    }
  }

  /** Allocation: left = new or left = new[?]. */
  void assignNewToLocal(Stmt stmt, Local left) {
    // strong update on local
    // we add a label inside node
    PurityNode node = cacheNode(new PurityStmtNode(stmt, true));
    localsRemove(left);
    localsPut(left, node);
    nodes.add(node);
    if (doCheck) {
      sanityCheck();
    }
  }

  /** A local variable is used in an unknown construct. */
  void localEscapes(Local l) {
    // nodes escape globally
    globEscape.addAll(locals.get(l));
    if (doCheck) {
      sanityCheck();
    }
  }

  /** A local variable is assigned to some outside value. */
  void localIsUnknown(Local l) {
    // strong update on local
    PurityNode node = PurityGlobalNode.node;
    localsRemove(l);
    localsPut(l, node);
    nodes.add(node);
    if (doCheck) {
      sanityCheck();
    }
  }

  /**
   * Store static: C.field = right.
   */
  void assignLocalToStaticField(Local right, String field) {
    PurityNode node = PurityGlobalNode.node;
    localEscapes(right);
    mutated.put(node, field);
    nodes.add(node);
    if (doCheck) {
      sanityCheck();
    }
  }

  /**
   * Store a primitive type into a non-static field left.field = v
   */
  void mutateField(Local left, String field) {
    for (PurityNode n : locals.get(left)) {
      if (!n.isInside()) {
        mutated.put(n, field);
      }
    }
    if (doCheck) {
      sanityCheck();
    }
  }

  /**
   * Store a primitive type into a static field left.field = v
   */
  void mutateStaticField(String field) {
    PurityNode node = PurityGlobalNode.node;
    mutated.put(node, field);
    nodes.add(node);
    if (doCheck) {
      sanityCheck();
    }
  }

  /**
   * Method call left = right.method(args).
   *
   * @param g
   *          is method's summary PurityGraph
   * @param left
   *          can be null (no return value)
   * @param right
   *          can be null (static call)
   * @param args
   *          is a list of Value
   */
  void methodCall(PurityGraph g, Local right, List<Value> args, Local left) {
    MultiMap<PurityNode, PurityNode> mu = new HashMultiMap<PurityNode, PurityNode>();

    // compute mapping relation g -> this
    /////////////////////////////////////

    // (1) rule
    int nb = 0;
    for (Value arg : args) {
      if (arg instanceof Local) {
        Local loc = (Local) arg;
        if (loc.getType() instanceof RefLikeType) {
          mu.putAll(cacheNode(new PurityParamNode(nb)), locals.get(loc));
        }
      }
      nb++;
    }
    if (right != null) {
      mu.putAll(PurityThisNode.node, locals.get(right));
    }

    // COULD BE OPTIMIZED!
    // many times, we need to copy sets cause we mutate them within iterators
    for (boolean hasChanged = true; hasChanged;) { // (2) & (3) rules fixpoint
      hasChanged = false;

      // (2)
      for (PurityNode n1 : new ArrayList<PurityNode>(mu.keySet())) {
        for (PurityNode n3 : new ArrayList<PurityNode>(mu.get(n1))) {
          for (PurityEdge e12 : g.edges.get(n1)) {
            if (!e12.isInside()) {
              for (PurityEdge e34 : edges.get(n3)) {
                if (e34.isInside() && e12.getField().equals(e34.getField())) {
                  if (mu.put(e12.getTarget(), e34.getTarget())) {
                    hasChanged = true;
                  }
                }
              }
            }
          }
        }
      }

      // (3)
      for (PurityNode n1 : g.edges.keySet()) {
        for (PurityNode n3 : g.edges.keySet()) {
          // ((mu(n1) U {n1}) inter (mu(n3) U {n3})) not empty
          Set<PurityNode> mu1 = mu.get(n1);
          Set<PurityNode> mu3 = mu.get(n3);
          boolean cond = n1.equals(n3) || mu1.contains(n3) || mu3.contains(n1);
          if (!cond) {
            for (PurityNode next : mu1) {
              cond |= mu3.contains(next);
              if (cond) {
                break;
              }
            }
          }
          // add (mu(n4) U ({n4} inter PNodes)) to mu(n2)
          if (cond && (!n1.equals(n3) || n1.isLoad())) {
            for (PurityEdge e12 : g.edges.get(n1)) {
              if (!e12.isInside()) {
                for (PurityEdge e34 : g.edges.get(n3)) {
                  if (e34.isInside()) {
                    if (e12.getField().equals(e34.getField())) {
                      PurityNode n2 = e12.getTarget();
                      PurityNode n4 = e34.getTarget();

                      // add n4 (if not param node) to mu(n2)
                      if (!n4.isParam() && mu.put(n2, n4)) {
                        hasChanged = true;
                      }

                      // add mu(n4) to mu(n2)
                      if (mu.putAll(n2, mu.get(n4))) {
                        hasChanged = true;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // extend mu into mu'
    for (PurityNode n : g.nodes) {
      if (!n.isParam()) {
        mu.put(n, n);
        nodes.add(n);
      }
    }

    // combine g into this
    //////////////////////

    // project edges
    for (PurityNode n1 : g.edges.keySet()) {
      for (PurityEdge e12 : g.edges.get(n1)) {
        String f = e12.getField();
        PurityNode n2 = e12.getTarget();
        for (PurityNode mu1 : mu.get(n1)) {
          if (e12.isInside()) {
            for (PurityNode mu2 : mu.get(n2)) {
              PurityEdge edge = cacheEdge(new PurityEdge(mu1, f, mu2, true));
              edges.put(mu1, edge);
              backEdges.put(mu2, edge);
            }
          } else {
            PurityEdge edge = cacheEdge(new PurityEdge(mu1, f, n2, false));
            edges.put(mu1, edge);
            backEdges.put(n2, edge);
          }
        }
      }
    }

    // return value
    if (left != null) {
      // strong update on locals
      localsRemove(left);
      for (PurityNode next : g.ret) {
        localsPutAll(left, mu.get(next));
      }
    }

    // global escape
    for (PurityNode next : g.globEscape) {
      globEscape.addAll(mu.get(next));
    }

    if (doCheck) {
      sanityCheck();
    }

    // simplification
    /////////////////

    Set<PurityNode> escaping = getEscaping();
    for (PurityNode n : new ArrayList<PurityNode>(nodes)) {
      if (!escaping.contains(n)) {
        if (n.isLoad()) {
          // remove captured load nodes
          removeNode(n);
        } else {
          // ... and outside edges from captured nodes
          for (PurityEdge e : new ArrayList<PurityEdge>(edges.get(n))) {
            if (!e.isInside()) {
              edges.remove(n, e);
              backEdges.remove(e.getTarget(), e);
            }
          }
        }
      }
    }

    // update mutated
    /////////////////
    for (PurityNode n : g.mutated.keySet()) {
      for (PurityNode nn : mu.get(n)) {
        if (nodes.contains(nn) && !nn.isInside()) {
          for (String next : g.mutated.get(n)) {
            mutated.put(nn, next);
          }
        }
      }
    }

    if (doCheck) {
      sanityCheck();
    }
  }

  /////////////
  // DRAWING //
  /////////////

  /**
   * Fills a dot graph or subgraph with the graphical representation of the purity graph.
   *
   * @param prefix
   *          is used to prefix all dot node and edge names. Use it to avoid collision when several subgraphs are laid in the
   *          same dot file!
   *
   * @param out
   *          is a newly created dot graph or subgraph where to put the result.
   *
   *          <p>
   *          Note: outside edges, param and load nodes are gray dashed, while inside edges and nodes are solid black.
   *          Globally escaping nodes have a red label.
   */
  void fillDotGraph(String prefix, DotGraph out) {
    Map<PurityNode, String> nodeId = new HashMap<PurityNode, String>();
    int id = 0;
    // add nodes

    for (PurityNode n : nodes) {
      String label = "N" + prefix + "_" + id;
      DotGraphNode node = out.drawNode(label);
      node.setLabel(n.toString());
      if (!n.isInside()) {
        node.setStyle("dashed");
        node.setAttribute("color", "gray50");
      }
      if (globEscape.contains(n)) {
        node.setAttribute("fontcolor", "red");
      }
      nodeId.put(n, label);
      id++;
    }

    // add edges
    for (PurityNode src : edges.keySet()) {
      for (PurityEdge e : edges.get(src)) {
        DotGraphEdge edge = out.drawEdge(nodeId.get(e.getSource()), nodeId.get(e.getTarget()));
        edge.setLabel(e.getField());
        if (!e.isInside()) {
          edge.setStyle("dashed");
          edge.setAttribute("color", "gray50");
          edge.setAttribute("fontcolor", "gray40");
        }
      }
    }

    // add locals
    for (Local local : locals.keySet()) {
      if (!locals.get(local).isEmpty()) {
        String label = "L" + prefix + "_" + id;
        DotGraphNode node = out.drawNode(label);
        node.setLabel(local.toString());
        node.setShape("plaintext");
        for (PurityNode dst : locals.get(local)) {
          out.drawEdge(label, nodeId.get(dst));
        }
        id++;
      }
    }

    // ret
    if (!ret.isEmpty()) {
      DotGraphNode node = out.drawNode("ret_" + prefix);
      node.setLabel("ret");
      node.setShape("plaintext");
      for (PurityNode dst : ret) {
        out.drawEdge("ret_" + prefix, nodeId.get(dst));
      }
    }

    // add mutated
    for (PurityNode n : mutated.keySet()) {
      for (String next : mutated.get(n)) {
        String label = "M" + prefix + "_" + id;
        DotGraphNode node = out.drawNode(label);
        node.setLabel("");
        node.setShape("plaintext");
        DotGraphEdge edge = out.drawEdge(nodeId.get(n), label);
        edge.setLabel(next);
        id++;
      }
    }
  }

  /** Debugging... */

  private static void dumpSet(String name, Set<PurityNode> s) {
    logger.debug(name);
    for (PurityNode next : s) {
      logger.debug("  " + next);
    }
  }

  private static <A, B> void dumpMultiMap(String name, MultiMap<A, B> s) {
    logger.debug(name);
    for (A key : s.keySet()) {
      logger.debug("  " + key);
      for (B value : s.get(key)) {
        logger.debug("    " + value);
      }
    }
  }

  void dump() {
    dumpSet("nodes Set:", nodes);
    dumpSet("paramNodes Set:", paramNodes);
    dumpMultiMap("edges MultiMap:", edges);
    dumpMultiMap("locals MultiMap:", locals);
    dumpSet("ret Set:", ret);
    dumpSet("globEscape Set:", globEscape);
    dumpMultiMap("backEdges MultiMap:", backEdges);
    dumpMultiMap("backLocals MultiMap:", backLocals);
    dumpMultiMap("mutated MultiMap:", mutated);
    logger.debug("");
  }

  static void dumpStat() {
    logger.debug("Stat: " + maxInsideNodes + " inNodes, " + maxLoadNodes + " loadNodes, " + maxInsideEdges + " inEdges, "
        + maxOutsideEdges + " outEdges, " + maxMutated + " mutated.");
  }

  void updateStat() {
    int insideNodes = 0;
    int loadNodes = 0;
    for (PurityNode n : nodes) {
      if (n.isInside()) {
        insideNodes++;
      } else if (n.isLoad()) {
        loadNodes++;
      }
    }

    int insideEdges = 0;
    int outsideEdges = 0;
    for (PurityNode next : edges.keySet()) {
      for (PurityEdge e : edges.get(next)) {
        if (e.isInside()) {
          insideEdges++;
        } else {
          outsideEdges++;
        }
      }
    }

    int mutatedFields = 0;
    for (PurityNode next : mutated.keySet()) {
      mutatedFields += mutated.get(next).size();
    }

    boolean changed = false;
    if (insideNodes > maxInsideNodes) {
      maxInsideNodes = insideNodes;
      changed = true;
    }
    if (loadNodes > maxLoadNodes) {
      maxLoadNodes = loadNodes;
      changed = true;
    }
    if (insideEdges > maxInsideEdges) {
      maxInsideEdges = insideEdges;
      changed = true;
    }
    if (outsideEdges > maxOutsideEdges) {
      maxOutsideEdges = outsideEdges;
      changed = true;
    }
    if (mutatedFields > maxMutated) {
      maxMutated = mutatedFields;
      changed = true;
    }
    if (changed) {
      dumpStat();
    }
  }
}
