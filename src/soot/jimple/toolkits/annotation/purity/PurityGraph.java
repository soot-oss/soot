/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Antoine Mine
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/**
 * Implementation of the paper "A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard, within the
 * Soot Optimization Framework.
 *
 * by Antoine Mine, 2005/01/24
 */

package soot.jimple.toolkits.annotation.purity;
import java.util.*;
import soot.*;
import soot.util.*;
import soot.util.dot.*;
import soot.jimple.*;

/**
 * Purity graphs are mutable structures that are updated in-place.
 * You can safely hash graphs. Equality comparison means isomorphism
 * (equal nodes, equal edges).
 */

/**
 * Modifications with respect to the article:
 *
 * - "unanalizable call" are treated by first constructing a conservative
 * calee graph where all parameters escape globally and return points to
 * the global node, and then applying the standard analysable call construction
 *
 * - unanalysable calls add a mutation on the global node; the "field" is named
 * "outside-world" and models the mutation of any static field, but also
 * side-effects by native methods, such as I/O, that make methods impure
 * (see below).
 *
 * - Whenever a method mutates the global node, it is marked as "impure"
 * (this can be due to a side-effect or static field mutation), even if the
 * global node is not rechable from parameter nodes through outside edges.
 * It seems to me it was a defect from the article ?
 * TODO: see if we must take the global node  into account also when stating
 * whether a parameter is read-only or safe.
 *
 * - "simplifyXXX" functions are experimental... they may be unsound, and
 * thus, not used now.
 *
 *
 *
 *
 * NOTE:
 * A lot of precision degradation comes from sequences of the form
 *   this.field = y; z = this.field
 * in initialisers: the second statement creates a load node because, as a
 * parameter, this may have escaped and this.field may be externally modified
 * in-between the two instructions. I am not sure this can actually happend
 * in an initialiser... in a a function called directly and only by
 * initialisers.
 *
 * For the moment, summary of unanalised methods are either pure, completely
 * impure (modify args & side-effects) or partially impure (modify args but
 * not the gloal node). We should really be able to specify more precisely
 * which arguments are r/o or safe within this methods.
 * E.g., the analysis java.lang.String: void getChars(int,int,char [],int)
 * imprecisely finds that this is not safe (because of the internal call to
 * System.arraycopy that, in general, may introduce aliases) => it pollutes 
 * many things (e.g., StringBuffer append(String), and thus, exception 
 * constructors, etc.)
 *
 */
public class PurityGraph
{
    public static final boolean doCheck = false;

    protected Set      nodes;      // all nodes
    protected Set      paramNodes; // only parameter & this nodes
    protected MultiMap edges;      // source node -> edges
    protected MultiMap locals;     // local -> nodes
    protected Set      ret;        // return -> nodes
    protected Set      globEscape; // nodes escaping globally
    protected MultiMap backEdges;  // target node -> edges
    protected MultiMap backLocals; // target node -> local node sources
    protected MultiMap mutated;    // node -> field such that (node,field) is mutated

    /**
     * Initially empty graph.
     */
    PurityGraph()
    {
	// nodes & paramNodes are added lazily
	nodes      = new HashSet();
	paramNodes = new HashSet();
	edges      = new HashMultiMap();
	locals     = new HashMultiMap();
	ret        = new HashSet();
	globEscape = new HashSet();
	backEdges  = new HashMultiMap();
	backLocals = new HashMultiMap();
	mutated    = new HashMultiMap();
	if (doCheck) sanityCheck();
    }

    /**
     * Copy constructor.
     */
    PurityGraph(PurityGraph x)
    {
	nodes      = new HashSet(x.nodes);
	paramNodes = new HashSet(x.paramNodes);
	edges      = new HashMultiMap(x.edges);
	locals     = new HashMultiMap(x.locals);
	ret        = new HashSet(x.ret);
	globEscape = new HashSet(x.globEscape);
	backEdges  = new HashMultiMap(x.backEdges);
	backLocals = new HashMultiMap(x.backLocals);
	mutated    = new HashMultiMap(x.mutated);
	if (doCheck) sanityCheck();
    }

    public int hashCode() 
    { 
	return nodes.hashCode()
	    //+  paramNodes.hashCode()  // redundant info
	    +  edges.hashCode()
	    +  locals.hashCode()
	    +  ret.hashCode()
	    +  globEscape.hashCode()
	    //+  backEdges.hashCode()   // redundant info
	    //+  backLocals.hashCode()  // redundant info
	    + mutated.hashCode()
	    ;
    }

    public boolean equals(Object o)
    {
	if (!(o instanceof PurityGraph)) return false;
	PurityGraph g = (PurityGraph)o;
	return nodes.equals(g.nodes)
	    //&& paramNodes.equals(g.paramNodes)  // redundant info
	    && edges.equals(g.edges)
	    && locals.equals(g.locals) 
	    && ret.equals(g.ret)
	    && globEscape.equals(g.globEscape)
	    //&& backEdges.equals(g.backEdges)    // redundant info
	    //&& backLocals.equals(g.backLocals)  // redundant info
	    && mutated.equals(g.mutated)
	    ;
    }

    /** 
     * Caching: this semm to actually improve both speed and memory 
     * consumption!
     */
    private static final Map<PurityNode, PurityNode> nodeCache =  new HashMap<PurityNode, PurityNode>();
    private static final Map<PurityEdge, PurityEdge> edgeCache =  new HashMap<PurityEdge, PurityEdge>();
    private static PurityNode cacheNode(PurityNode p)
    {
	if (!nodeCache.containsKey(p)) nodeCache.put(p,p);
	return nodeCache.get(p);
    }
    private static PurityEdge cacheEdge(PurityEdge e)
    {
	if (!edgeCache.containsKey(e)) edgeCache.put(e,e);
	return edgeCache.get(e);
    }

    /**
     * Conservative constructor for unanalysable calls.
     *
     * <p>Note: this gives a valid summary for all native methods, including
     * Thread.start().
     *
     * @param withEffect add a mutated abstract field for the global node to
     * account for side-effects in the environment (I/O, etc.).
     */
    public static PurityGraph conservativeGraph(SootMethod m,
						boolean withEffect)
    {
	PurityGraph g = new PurityGraph();
	PurityNode glob = PurityGlobalNode.node;
	g.nodes.add(glob);

	// parameters & this escape globally
	Iterator it = m.getParameterTypes().iterator();
	int i = 0;
	while (it.hasNext()) {
	    if (it.next() instanceof RefLikeType) {
		PurityNode n = cacheNode(new PurityParamNode(i));
		g.globEscape.add(n);
		g.nodes.add(n);
		g.paramNodes.add(n);
	    }
	    i++;
	}

	// return value escapes globally
	if (m.getReturnType() instanceof RefLikeType) g.ret.add(glob);

	// add a side-effect on the environment
	// added by [AM]
	if (withEffect) g.mutated.put(glob,"outside-world");

	if (doCheck) g.sanityCheck();
	return g;
    }


    /**
     * Special constructor for "pure" methods returning a fresh object.
     * (or simply pure if returns void or primitive).
     */
    public static PurityGraph freshGraph(SootMethod m)
    {
	PurityGraph g = new PurityGraph();
	if (m.getReturnType() instanceof RefLikeType) {
	    PurityNode n = cacheNode(new PurityMethodNode(m));
	    g.ret.add(n);
	    g.nodes.add(n);
	}
	if (doCheck) g.sanityCheck();
	return g;
    }


    /**
     * Replace the current graph with its union with arg.
     * arg is not modified.
     */
    void union(PurityGraph arg)
    {
	nodes.addAll(arg.nodes);
	paramNodes.addAll(arg.paramNodes);
	edges.putAll(arg.edges);
	locals.putAll(arg.locals);
	ret.addAll(arg.ret);
	globEscape.addAll(arg.globEscape);
	backEdges.putAll(arg.backEdges);
	backLocals.putAll(arg.backLocals);
	mutated.putAll(arg.mutated);
	if (doCheck) sanityCheck();
    }


    /**
     * Sanity check. Used internally for debugging!
     */
    protected void sanityCheck()
    {
	boolean err = false;
	Iterator it = edges.keySet().iterator();
	while (it.hasNext()) {
	    PurityNode src = (PurityNode)it.next();
	    Iterator itt = edges.get(src).iterator();
	    while (itt.hasNext()) {
		PurityEdge e = (PurityEdge)itt.next();
		if (!src.equals(e.getSource()))
		    {G.v().out.println("invalid edge source "+e+", should be "+src);err=true;}
		if (!nodes.contains(e.getSource()))
		    {G.v().out.println("nodes does not contain edge source "+e);err=true;}
		if (!nodes.contains(e.getTarget()))
		    {G.v().out.println("nodes does not contain edge target "+e);err=true;}
		if (!backEdges.get(e.getTarget()).contains(e))
		    {G.v().out.println("backEdges does not contain edge "+e);err=true;}
		if (!e.isInside() && !e.getTarget().isLoad())
		    {G.v().out.println("target of outside edge is not a load node "+e);err=true;}
	    }
	}
	it = backEdges.keySet().iterator();
	while (it.hasNext()) {
	    PurityNode dst = (PurityNode)it.next();
	    Iterator itt = backEdges.get(dst).iterator();
	    while (itt.hasNext()) {
		PurityEdge e = (PurityEdge)itt.next();
		if (!dst.equals(e.getTarget()))
		    {G.v().out.println("invalid backEdge dest "+e+", should be "+dst);err=true;}
		if (!edges.get(e.getSource()).contains(e))
		    {G.v().out.println("backEdge not in edges "+e);err=true;}
	    }
	}
	it = nodes.iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode)it.next();
	    if (n.isParam() && !paramNodes.contains(n))
		{G.v().out.println("paramNode not in paramNodes "+n);err=true;}
	}
	it = paramNodes.iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode)it.next();
	    if (!n.isParam())
		{G.v().out.println("paramNode contains a non-param node "+n);err=true;}
	    if (!nodes.contains(n))
		{G.v().out.println("paramNode not in nodes "+n);err=true;}
	}
	it = globEscape.iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode)it.next();
	    if (!nodes.contains(n))
		{G.v().out.println("globEscape not in nodes "+n);err=true;}
	}
	it = locals.keySet().iterator();
	while (it.hasNext()) {
	    Local l = (Local)it.next();
	    Iterator itt = locals.get(l).iterator();
	    while (itt.hasNext()) {
		PurityNode n = (PurityNode)itt.next();
		if (!nodes.contains(n))
		    {G.v().out.println("target of local node in nodes "+l+" / "+n);err=true;}
		if (!backLocals.get(n).contains(l))
		    {G.v().out.println("backLocals does contain local "+l+" / "+n);err=true;}
	    }
	}
	it = backLocals.keySet().iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode)it.next();
	    Iterator itt = backLocals.get(n).iterator();
	    while (itt.hasNext()) {
		Local l = (Local)itt.next();
		if (!nodes.contains(n))
		    {G.v().out.println("backLocal node not in in nodes "+l+" / "+n);err=true;}
		if (!locals.get(l).contains(n))
		    {G.v().out.println("locals does contain backLocal "+l+" / "+n);err=true;}
	    }
	}
	it = ret.iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode)it.next();
	    if (!nodes.contains(n))
		{G.v().out.println("target of ret not in nodes "+n);err=true;}
	}
	it = mutated.keySet().iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode)it.next();
	    if (!nodes.contains(n))
		{G.v().out.println("mutated node not in nodes "+n);err=true;}
	}
	if (err) {
	    dump();
	    DotGraph dot = new DotGraph("sanityCheckFailure");
	    fillDotGraph("chk",dot);
	    dot.plot("sanityCheckFailure.dot");
	    throw new Error("PurityGraph sanity check failed!!!");
	}
    }

    ////////////////////////
    // ESCAPE INFORMATION //
    ////////////////////////

    protected void internalPassEdges(Set toColor, Set<PurityNode> dest, 
				     boolean consider_inside)
    {
	Iterator it = toColor.iterator();
	while (it.hasNext()) {
	    PurityEdge edge = (PurityEdge) it.next();
	    if (consider_inside || !edge.isInside()) {
		PurityNode node = edge.getTarget();
		if (!dest.contains(node)) {
		    dest.add(node);
		    internalPassEdges(edges.get(node),dest,consider_inside);
		}
	    }
	}	
    }

    protected void internalPassNode(PurityNode node, Set<PurityNode> dest,
				    boolean consider_inside)
    {
	if (!dest.contains(node)) {
	    dest.add(node);
	    internalPassEdges(edges.get(node),dest,consider_inside);
	}	
    }

    protected void internalPassNodes(Set toColor, Set<PurityNode> dest, 
				     boolean consider_inside)
    {
	Iterator it = toColor.iterator();
	while (it.hasNext()) 
	    internalPassNode((PurityNode)it.next(),
			     dest, consider_inside);
    }

    protected Set<PurityNode> getEscaping()
    {
	Set<PurityNode> escaping = new HashSet<PurityNode>();
	internalPassNodes(ret,escaping,true);
	internalPassNodes(globEscape,escaping,true);
	internalPassNode(PurityGlobalNode.node,escaping,true);
	internalPassNodes(paramNodes,escaping,true);
	return escaping;
    }


    /**
     * Call this on the merge of graphs at all return points of a method to
     * know whether the method is pure.
     */
    public boolean isPure()
    {
	if (!mutated.get(PurityGlobalNode.node).isEmpty()) return false;
	Set<PurityNode> A = new HashSet<PurityNode>();
	Set<PurityNode> B = new HashSet<PurityNode>();
	internalPassNodes(paramNodes, A, false);
	internalPassNodes(globEscape, B, true);
	internalPassNode(PurityGlobalNode.node,B,true);
	Iterator<PurityNode> it = A.iterator();
	while (it.hasNext()) {
	    PurityNode n = it.next();
	    if (B.contains(n) || !mutated.get(n).isEmpty()) return false;
	}
	return true;
    }

   /**
    * We use a less restrictive notion of purity for constructors: pure 
    * constructors can mutate fields of this. 
    *
    * @see isPure
    */
    public boolean isPureConstructor()
    {
	if (!mutated.get(PurityGlobalNode.node).isEmpty()) return false;
	Set<PurityNode> A = new HashSet<PurityNode>();
	Set<PurityNode> B = new HashSet<PurityNode>();
	internalPassNodes(paramNodes, A, false);
	internalPassNodes(globEscape, B, true);
	internalPassNode(PurityGlobalNode.node,B,true);
	PurityNode th = PurityThisNode.node;
	Iterator<PurityNode> it = A.iterator();
	while (it.hasNext()) {
	    PurityNode n = it.next();
	    if (B.contains(n) || 
		(!n.equals(th) && !mutated.get(n).isEmpty())) return false;
	}
	return true;
    }

    /**
     * A parameter (or this) can be:
     * - read and write
     * - read only
     * - safe (read only & no externally visible alias is created)
     */
    static final int PARAM_RW   = 0;
    static final int PARAM_RO   = 1;
    static final int PARAM_SAFE = 2;

    protected int internalParamStatus(PurityNode p)
    {
	if (!paramNodes.contains(p)) return PARAM_RW;

	Set<PurityNode> S1 = new HashSet<PurityNode>();
	internalPassNode(p, S1, false);
	Iterator<PurityNode> it = S1.iterator();
	while (it.hasNext()) {
	    PurityNode n = it.next();
	    if (n.isLoad() || n.equals(p)) {
		if (!mutated.get(n).isEmpty() ||
		    globEscape.contains(n)) return PARAM_RW;
	    }
	}

	Set<PurityNode> S2 = new HashSet<PurityNode>();
	internalPassNodes(ret,S2,true);
	internalPassNodes(paramNodes,S2,true);
	it = S2.iterator();
	while (it.hasNext()) {
	    Iterator itt = edges.get(it.next()).iterator();
	    while (itt.hasNext()) {
		PurityEdge e = (PurityEdge)itt.next();
		if (e.isInside() && S1.contains(e.getTarget()))
		    return PARAM_RO;
	    }
	}

	return PARAM_SAFE;
    }

    /**
     * Call this on the merge of graphs at all return points of a method to
     * know whether an object passed as method parameter is read only
     * (PARAM_RO), read write (PARAM_RW), or safe (PARAM_SAFE).
     * Returns PARAM_RW for primitive-type parameters.
     */
    public int paramStatus(int param)
    { return internalParamStatus(cacheNode(new PurityParamNode(param))); }

    /**
     * @see isParamReadOnly
     */
    public int thisStatus()
    { return internalParamStatus(PurityThisNode.node); }


    /////////////////////////
    // GRAPH MANUPULATIONS //
    /////////////////////////

    public Object clone()
    {
	return new PurityGraph(this);
    }

    // utility functions to update local / backLocals constitently
    protected final boolean localsRemove(Local local)
    {
	Iterator it = locals.get(local).iterator();
	while (it.hasNext()) {
	    Object node = it.next();
	    backLocals.remove(node,local);
	}
	return locals.remove(local);
    }

    protected final boolean localsPut(Local local, PurityNode node)
    {
	backLocals.put(node,local);
	return locals.put(local,node);
    }

    protected final boolean localsPutAll(Local local, Set nodes)
    {
	Iterator it = nodes.iterator();
	while (it.hasNext()) {
	    Object node = it.next();
	    backLocals.put(node,local);
	}
	return locals.putAll(local,nodes);
    }

    /** Utility function to remove a node & all adjacent edges */
    protected final void removeNode(PurityNode n)
    {
	Iterator it = edges.get(n).iterator();
	while (it.hasNext()) {
	    PurityEdge e = (PurityEdge)it.next();
	    backEdges.remove(e.getTarget(),e);
	}
	it = backEdges.get(n).iterator();
	while (it.hasNext()) {
	    PurityEdge e = (PurityEdge)it.next();
	    edges.remove(e.getSource(),e);
	}
	it = backLocals.get(n).iterator();
	while (it.hasNext()) {
	    Local l = (Local)it.next();
	    locals.remove(l,n);
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
    protected final void mergeNodes(PurityNode src, PurityNode dst)
    {
	Iterator it = (new LinkedList(edges.get(src))).iterator();
	while (it.hasNext()) {
	    PurityEdge e = (PurityEdge)it.next();
	    PurityNode n = e.getTarget();
	    if (n.equals(src)) n = dst;	    
	    PurityEdge ee = 
		cacheEdge(new PurityEdge(dst, e.getField(), n, e.isInside()));
	    edges.remove(src, e);
	    edges.put(dst, ee);
	    backEdges.remove(n, e);
	    backEdges.put(n, ee);
	}
	it = (new LinkedList(backEdges.get(src))).iterator();
	while (it.hasNext()) {
	    PurityEdge e = (PurityEdge)it.next();
	    PurityNode n = e.getSource();
	    if (n.equals(src)) n = dst;
	    PurityEdge ee = 
		cacheEdge(new PurityEdge(n, e.getField(), dst, e.isInside()));
	    edges.remove(n, e);
	    edges.put(n, ee);
	    backEdges.remove(src, e);
	    backEdges.put(dst, ee);
	}
	it = (new LinkedList(backLocals.get(src))).iterator();
	while (it.hasNext()) {
	    Local l = (Local)it.next();
	    locals.remove(l, src);
	    backLocals.remove(src, l);
	    locals.put(l,dst);
	    backLocals.put(dst, l);
	}
	{
	    Set m = mutated.get(src);
	    mutated.remove(src);
	    mutated.putAll(dst,m);
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
	if (dst.isParam()) paramNodes.add(dst);
    }

    /** Experimental simplification: merge redundant load nodes. */
    void simplifyLoad()
    {
	Iterator it = (new LinkedList(nodes)).iterator();
	while (it.hasNext()) {
	    PurityNode p = (PurityNode)it.next();
	    Map<String, PurityNode> fmap = new HashMap<String, PurityNode>();
	    Iterator itt = (new LinkedList(edges.get(p))).iterator();
	    while (itt.hasNext()) {
		PurityEdge e   = (PurityEdge)itt.next();
		PurityNode tgt = e.getTarget();
		if (!e.isInside() && !tgt.equals(p)) {
		    String f = e.getField();
		    if (fmap.containsKey(f) && nodes.contains(fmap.get(f))) 
			mergeNodes(tgt, fmap.get(f));
		    else fmap.put(f,tgt);
		}
	    }
	}
	if (doCheck) sanityCheck();
    }

    /** Experimental simplification: remove inside nodes not reachable
	from escaping nodes (params, ret, globEscape) or load nodes. */
    void simplifyInside()
    {
	Set<PurityNode> r = new HashSet<PurityNode>();
	internalPassNodes(paramNodes,r,true);
	internalPassNodes(ret,r,true);
	internalPassNodes(globEscape,r,true);
	internalPassNode(PurityGlobalNode.node,r,true);
	Iterator it = nodes.iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode) it.next();
	    if (n.isLoad()) internalPassNode(n,r,true);
	} 
	it = (new LinkedList(nodes)).iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode) it.next();
	    if (n.isInside() && !r.contains(n)) removeNode(n);
	}
	if (doCheck) sanityCheck();
    }

    /** 
     * Remove all local bindings (except ret).
     * This info is indeed superfluous on summary purity graphs representing
     * the effect of a method. This saves a little memory, but also,
     * simplify summary graph drawings a lot!
     *
     * DO NOT USE DURING INTRA-PROCEDURAL ANALYSIS!
     */
    void removeLocals()
    {
	locals = new HashMultiMap();
	backLocals = new HashMultiMap();
    }

    /** Copy assignment left = right. */
    void assignParamToLocal(int right, Local left)
    {
	// strong update on local
	PurityNode node = cacheNode(new PurityParamNode(right));
	localsRemove(left);
	localsPut(left,node);
	nodes.add(node);
	paramNodes.add(node);
	if (doCheck) sanityCheck();
    }

    /** Copy assignment left = this. */
    void assignThisToLocal(Local left)
    {
	// strong update on local
	PurityNode node = PurityThisNode.node;
	localsRemove(left);
	localsPut(left,node);
	nodes.add(node);
	paramNodes.add(node);
	if (doCheck) sanityCheck();
    }

    /** Copy assignment left = right. */
    void assignLocalToLocal(Local right, Local left)
    {
	// strong update on local
	localsRemove(left);
	localsPutAll(left,locals.get(right));
	if (doCheck) sanityCheck();
    }

    /** return right statement . */
    void returnLocal(Local right)
    {
	// strong update on ret
	ret.clear();
	ret.addAll(locals.get(right));
	if (doCheck) sanityCheck();
    }

    /** 
     * Load non-static: left = right.field, or left = right[?] if field is [].
     */
    void assignFieldToLocal(Stmt stmt, Local right, String field, Local left)
    {
	Set<PurityNode> esc = new HashSet<PurityNode>();
	Set<PurityNode> escaping = getEscaping();

	// strong update on local
	localsRemove(left);
	Iterator itRight = locals.get(right).iterator();
	while (itRight.hasNext()) {
	    PurityNode nodeRight = (PurityNode) itRight.next();

	    Iterator itEdges = edges.get(nodeRight).iterator();
	    while (itEdges.hasNext()) {
		PurityEdge edge = (PurityEdge) itEdges.next();
		if (edge.isInside() && edge.getField().equals(field))
		    localsPut(left, edge.getTarget());
	    }

	    if (escaping.contains(nodeRight)) esc.add(nodeRight);
	}
    
	if (!esc.isEmpty()) {
	    // right can escape
	    
	    // we add a label load node & outside edges
	    PurityNode loadNode = cacheNode(new PurityStmtNode(stmt,false));
	    nodes.add(loadNode);
	    
	    Iterator<PurityNode> itEsc = esc.iterator();
	    while (itEsc.hasNext()) {
		PurityNode node = itEsc.next();
		PurityEdge edge = 
		    cacheEdge(new PurityEdge(node, field, loadNode, false));
		if (edges.put(node, edge))
		    backEdges.put(loadNode, edge);
	    }
	    localsPut(left, loadNode);
	}
	if (doCheck) sanityCheck();
    }

    /** 
     * Store non-static: left.field = right, or left[?] = right if field is [].
     */
    void assignLocalToField(Local right, Local left, String field)
    {
	// weak update on inside edges
	Iterator itLeft = locals.get(left).iterator();
	while (itLeft.hasNext()) {
	    PurityNode nodeLeft = (PurityNode) itLeft.next();
	    Iterator itRight = locals.get(right).iterator();
	    while (itRight.hasNext()) {
		PurityNode nodeRight = (PurityNode) itRight.next();
		PurityEdge edge = 
		    cacheEdge(new PurityEdge(nodeLeft, field, nodeRight, true));
		if (edges.put(nodeLeft, edge))
		    backEdges.put(nodeRight, edge);
	    }
	    if (!nodeLeft.isInside())
		mutated.put(nodeLeft, field);
	}
	if (doCheck) sanityCheck();
    }

    /** Allocation: left = new or left = new[?]. */
    void assignNewToLocal(Stmt stmt, Local left)
    {
	// strong update on local
	// we add a label inside node
	PurityNode node = cacheNode(new PurityStmtNode(stmt,true));
	localsRemove(left);
	localsPut(left, node);
	nodes.add(node);
	if (doCheck) sanityCheck();
    }

    /** A local variable is used in an unknown construct. */
    void localEscapes(Local l)
    {
	// nodes escape globally
	globEscape.addAll(locals.get(l));
	if (doCheck) sanityCheck();
    }

    /** A local variable is assigned to some outside value. */
    void localIsUnknown(Local l)
    {
	// strong update on local
	PurityNode node = PurityGlobalNode.node;
	localsRemove(l);
	localsPut(l, node);
	nodes.add(node);
	if (doCheck) sanityCheck();
    }

    /** 
     * Store static: C.field = right.
     */
    void assignLocalToStaticField(Local right, String field)
    {
	PurityNode node = PurityGlobalNode.node;
	localEscapes(right);
	mutated.put(node, field);
	nodes.add(node);
	if (doCheck) sanityCheck();	
    }

    /**
     * Store a primitive type into a non-static field left.field = v
     */
    void mutateField(Local left, String field)
    {
	Iterator it = locals.get(left).iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode)it.next();
	    if (!n.isInside())
		mutated.put(n, field);
	}
	if (doCheck) sanityCheck();
    }

    /**
     * Store a primitive type into a static field left.field = v
     */
    void mutateStaticField(String field)
    {
	PurityNode node = PurityGlobalNode.node;
	mutated.put(node, field);
	nodes.add(node);
	if (doCheck) sanityCheck();
    }

    /** 
     * Method call left = right.method(args).
     *
     * @param g is method's summary PurityGraph
     * @param left can be null (no return value)
     * @param right can be null (static call)
     * @param args is a list of Value
     */
    void methodCall(PurityGraph g, Local right, List args, Local left)
    {
	MultiMap mu = new HashMultiMap();

	// compute mapping relation g -> this
	/////////////////////////////////////

	Iterator it = args.iterator(); // (1) rule
	int nb = 0;
	while (it.hasNext()) {
	    Value arg = (Value)it.next();
	    if (arg instanceof Local && 
		((Local)arg).getType() instanceof RefLikeType) {
		mu.putAll(cacheNode(new PurityParamNode(nb)),locals.get(arg));
	    }
	    nb++;
	}
	if (right!=null) // (1) rule for "this" argument
	    mu.putAll(PurityThisNode.node,locals.get(right));

	// COULD BE OPTIMIZED!
	// many times, we need to copy sets cause we mutate them within iterators
	boolean hasChanged = true;
	while (hasChanged) { // (2) & (3) rules fixpoint
	    hasChanged = false;

	    // (2)
	    it = (new LinkedList(mu.keySet())).iterator();
	    while (it.hasNext()) {
		PurityNode n1 = (PurityNode)it.next();
		Iterator it3 = (new LinkedList(mu.get(n1))).iterator();
		while (it3.hasNext()) {
		    PurityNode n3 = (PurityNode)it3.next();
		    Iterator it12 = g.edges.get(n1).iterator();
		    while (it12.hasNext()) {
			PurityEdge e12 = (PurityEdge)it12.next();
			if (!e12.isInside()) {
			    Iterator it34 = edges.get(n3).iterator();
			    while (it34.hasNext()) {
				PurityEdge e34 = (PurityEdge)it34.next();
				if (e34.isInside() &&
				    e12.getField().equals(e34.getField()))
				    if (mu.put(e12.getTarget(),e34.getTarget()))
					hasChanged = true;
			    }
			}
		    }
		}
	    }
	    
	    // (3)
	    it = g.edges.keySet().iterator();
	    while (it.hasNext()) {
		PurityNode n1 = (PurityNode)it.next();
		Iterator it3 = g.edges.keySet().iterator();
		while (it3.hasNext()) {
		    PurityNode n3 = (PurityNode)it3.next();

		    // ((mu(n1) U {n1}) inter (mu(n3) U {n3})) not empty
		    Set mu1 = new HashSet(mu.get(n1));
		    Set mu3 = new HashSet(mu.get(n3));
		    boolean cond = n1.equals(n3) || 
			mu1.contains(n3) || mu3.contains(n1);
		    Iterator itt = mu1.iterator();
		    while (!cond && itt.hasNext()) {
			cond = cond || mu3.contains(itt.next());
		    }
		    
		    // add (mu(n4) U ({n4} inter PNodes)) to mu(n2)
		    if (cond && (!n1.equals(n3) || n1.isLoad())) {
			Iterator it12 = g.edges.get(n1).iterator();
			while (it12.hasNext()) {
			    PurityEdge e12 = (PurityEdge)it12.next();
			    if (!e12.isInside()) {
				Iterator it34 = g.edges.get(n3).iterator();
				while (it34.hasNext()) {
				    PurityEdge e34 = (PurityEdge)it34.next();
				    if (e34.isInside()) {
					if (e12.getField().equals(e34.getField())) {
					    PurityNode n2 = e12.getTarget();
					    PurityNode n4 = e34.getTarget();
					    
					    // add n4 (if not param node) to mu(n2)
					    if (!n4.isParam() && mu.put(n2,n4))
						hasChanged = true;
					    
					    // add mu(n4) to mu(n2)
					    if (mu.putAll(n2,mu.get(n4)))
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
	
	// extend mu into mu'
	it = g.nodes.iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode)it.next();
	    if (!n.isParam()) { 
		mu.put(n,n);
		nodes.add(n);
	    }
	}


	// combine g into this
	//////////////////////

	// project edges
	it = g.edges.keySet().iterator();
	while (it.hasNext()) {
	    PurityNode n1 = (PurityNode)it.next();
	    Iterator it12 = g.edges.get(n1).iterator();
	    while (it12.hasNext()) {
		PurityEdge e12 = (PurityEdge)it12.next();
		String     f   = e12.getField();
		PurityNode n2  = e12.getTarget();
		Iterator  itm1 = mu.get(n1).iterator();
		while (itm1.hasNext()) {
		    PurityNode mu1 = (PurityNode)itm1.next();

		    if (e12.isInside()) {
			Iterator itm2 = mu.get(n2).iterator();
			while (itm2.hasNext()) {
			    PurityNode mu2  = (PurityNode)itm2.next();
			    PurityEdge edge = 
				cacheEdge(new PurityEdge(mu1,f,mu2,true));
			    edges.put(mu1,edge);
			    backEdges.put(mu2,edge);
			}
		    }
		    else {
			PurityEdge edge = 
			    cacheEdge(new PurityEdge(mu1,f,n2,false));
			edges.put(mu1,edge);
			backEdges.put(n2,edge);
		    }
		}
	    }
	}

	// return value
	if (left!=null) {
	    // strong update on locals
	    localsRemove(left);
	    it = g.ret.iterator();
	    while (it.hasNext())
		localsPutAll(left, mu.get(it.next()));
	}
	
	// global escape
	it = g.globEscape.iterator();
	while (it.hasNext())
	    globEscape.addAll(mu.get(it.next()));

	if (doCheck) sanityCheck();


	// simplification
	/////////////////	
		
	Set<PurityNode> escaping = getEscaping();
	it = (new LinkedList(nodes)).iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode)it.next();
	    if (!escaping.contains(n)) 
		if (n.isLoad()) 
		    // remove captured load nodes
		    removeNode(n);
		else {
		    // ... and outside edges from captured nodes
		    Iterator itt = (new LinkedList(edges.get(n))).iterator();
		    while (itt.hasNext()) {
			PurityEdge e = (PurityEdge)itt.next();
			if (!e.isInside()) {
			    edges.remove(n,e);
			    backEdges.remove(e.getTarget(),e);
			}
		    }
		}  
	}

	// update mutated
	/////////////////

	it = g.mutated.keySet().iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode)it.next();
	    Iterator itt = mu.get(n).iterator();
	    while (itt.hasNext()) {
		PurityNode nn = (PurityNode)itt.next();
		if (nodes.contains(nn) && !nn.isInside()) {
		    Iterator ittt = g.mutated.get(n).iterator();
		    while (ittt.hasNext()) {
			String f = (String)ittt.next();
			mutated.put(nn,f);
		    }
		}
	    }
	}

	if (doCheck) sanityCheck();
    }


    /////////////
    // DRAWING //
    /////////////
    
    /** 
     * Fills a dot graph or subgraph with the graphical representation
     * of the purity graph.
     *
     * @param prefix is used to prefix all dot node and edge names. Use it
     * to avoid collision when several subgraphs are laid in the same dot
     * file!
     * 
     * @param out is a newly created dot graph or subgraph where to put the
     * result.
     *
     * <p>Note: outside edges, param and load nodes are gray dashed, while
     * inside edges and nodes are solid black.
     * Globally escaping nodes have a red label.
     */
    void fillDotGraph(String prefix, DotGraph out)
    {
        Map<PurityNode, String> nodeId = new HashMap<PurityNode, String>();
	int id = 0;

	// add nodes 
	Iterator it = nodes.iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode) it.next();
	    String label = "N"+prefix+"_"+id;
	    DotGraphNode node = out.drawNode(label);
	    node.setLabel(n.toString());
	    if (!n.isInside()) {
		node.setStyle("dashed");
		node.setAttribute("color","gray50");
	    }
	    if (globEscape.contains(n)) node.setAttribute("fontcolor","red");
	    nodeId.put(n,label);
	    id++;
	}
	
	// add edges
	it = edges.keySet().iterator();
	while (it.hasNext()) {
	    PurityNode src = (PurityNode) it.next();
	    Iterator itt = edges.get(src).iterator();
	    while (itt.hasNext()) {
		PurityEdge e = (PurityEdge) itt.next();
		DotGraphEdge edge = 
		    out.drawEdge(nodeId.get(e.getSource()),
				 nodeId.get(e.getTarget()));
		edge.setLabel(e.getField());
		if (!e.isInside()) {
		    edge.setStyle("dashed");
		    edge.setAttribute("color","gray50");
		    edge.setAttribute("fontcolor","gray40");
		}
	    }
	}

	// add locals
	it = locals.keySet().iterator();
	while (it.hasNext()) {
	    Local local = (Local) it.next();
	    if (!locals.get(local).isEmpty()) {
		String label = "L"+prefix+"_"+id;
		DotGraphNode node = out.drawNode(label);
		node.setLabel(local.toString());
		node.setShape("plaintext");	    
		Iterator itt = locals.get(local).iterator();
		while (itt.hasNext()) {
		    PurityNode dst = (PurityNode) itt.next();
		    out.drawEdge(label,nodeId.get(dst));
		}
		id++;
	    }
	}

	// ret
	if (!ret.isEmpty()) {
	    DotGraphNode node = out.drawNode("ret_"+prefix);
	    node.setLabel("ret");
	    node.setShape("plaintext");	    
	    Iterator itt = ret.iterator();
	    while (itt.hasNext()) {
		PurityNode dst = (PurityNode) itt.next();
		out.drawEdge("ret_"+prefix,nodeId.get(dst));
	    }	    
	}

	// add mutated
	it = mutated.keySet().iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode)it.next();
	    Iterator itt = mutated.get(n).iterator();
	    while (itt.hasNext()) {
		String f = (String)itt.next();
		String label = "M"+prefix+"_"+id;
		DotGraphNode node = out.drawNode(label);
		node.setLabel("");
		node.setShape("plaintext");
		DotGraphEdge edge = out.drawEdge(nodeId.get(n),label);
		edge.setLabel(f);
		id++;
	    }
	}
    }

    /** Debugging... */

    static private void dumpSet(String name, Set s) {
	G.v().out.println(name);
	Iterator it = s.iterator();
	while (it.hasNext()) G.v().out.println("  "+it.next().toString());
    }

    static private void dumpMultiMap(String name, MultiMap s) {
	G.v().out.println(name);
	Iterator it = s.keySet().iterator();
	while (it.hasNext()) {
	    Object o = it.next();
	    G.v().out.println("  "+o.toString());
	    Iterator itt = s.get(o).iterator();
	    while (itt.hasNext()) 
		G.v().out.println("    "+itt.next().toString());
	}
    }

    void dump()
    {
	dumpSet("nodes Set:",nodes);
	dumpSet("paramNodes Set:",paramNodes);
	dumpMultiMap("edges MultiMap:",edges);
	dumpMultiMap("locals MultiMap:",locals);
	dumpSet("ret Set:",ret);
	dumpSet("globEscape Set:",globEscape);
	dumpMultiMap("backEdges MultiMap:",backEdges);
	dumpMultiMap("backLocals MultiMap:",backLocals);
	dumpMultiMap("mutated MultiMap:",mutated);
	G.v().out.println("");
    }


    /** Simple statistics on maximal graph sizes.*/

    static private int maxInsideNodes = 0;
    static private int maxLoadNodes = 0;
    static private int maxInsideEdges = 0;
    static private int maxOutsideEdges = 0;
    static private int maxMutated = 0;

    void dumpStat()
    {
	G.v().out.println("Stat: "+
			  maxInsideNodes+" inNodes, "+
			  maxLoadNodes+" loadNodes, "+
			  maxInsideEdges+" inEdges, "+
			  maxOutsideEdges+" outEdges, "+
			  maxMutated+" mutated.");
    }

    void updateStat()
    {
	Iterator it = nodes.iterator();
	int insideNodes = 0;
	int loadNodes = 0;
	while (it.hasNext()) {
	    PurityNode n = (PurityNode)it.next();
	    if (n.isInside()) insideNodes++;
	    else if (n.isLoad()) loadNodes++;
	}
	int insideEdges = 0;
	int outsideEdges = 0;
	it = edges.keySet().iterator();
	while (it.hasNext()) {
	    Iterator itt = edges.get(it.next()).iterator();
	    while (itt.hasNext()) {
		PurityEdge e = (PurityEdge)itt.next();
		if (e.isInside()) insideEdges++;
		else outsideEdges++;
	    }
	}
	int mutatedFields = 0;
	it = mutated.keySet().iterator();
	while (it.hasNext()) mutatedFields += mutated.get(it.next()).size();

	boolean changed = false;
	if (insideNodes>maxInsideNodes) 
	    { maxInsideNodes=insideNodes; changed=true; }
	if (loadNodes>maxLoadNodes)
	    { maxLoadNodes=loadNodes; changed=true; }
	if (insideEdges>maxInsideEdges)
	    { maxInsideEdges=insideEdges; changed=true; }
	if ( outsideEdges>maxOutsideEdges)
	    { maxOutsideEdges=outsideEdges; changed=true; }
	if (mutatedFields>maxMutated)
	    { maxMutated=mutatedFields; changed=true; }
	if (changed) dumpStat();
    }
}
