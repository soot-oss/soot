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
 *
 * <p> Note: we could greatly benefit from a purely functional implementation!
 */
public class PurityGraph
{
    protected Set      nodes;      // all nodes
    protected Set      paramNodes; // only parameter & this nodes
    protected MultiMap edges;      // source node -> edges
    protected MultiMap locals;     // local -> nodes
    protected Set      ret;        // return -> nodes
    protected Set      globEscape; // nodes escaping globally + global node
    protected MultiMap backEdges;  // target node -> edges
    protected MultiMap backLocals; // target node -> local node sources
    protected MultiMap mutated;    // node -> field such that (node,field) is mutated

    /**
     * Initially empty graph.
     */
    PurityGraph()
    {
	nodes      = new HashSet();
	paramNodes = new HashSet();
	edges      = new HashMultiMap();
	locals     = new HashMultiMap();
	ret        = new HashSet();
	globEscape = new HashSet();
	backEdges  = new HashMultiMap();
	backLocals = new HashMultiMap();
	mutated    = new HashMultiMap();

	// fill initial nodes
	// parameters and this are added lazyly
	nodes.add(new PurityGlobalNode());
	globEscape.add(new PurityGlobalNode());

	// please only access escape info through doesEscape
	escaping = new HashSet();
	upToDate = false;
	//sanityCheck();
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
	escaping   = new HashSet(x.escaping);
	upToDate   = x.upToDate;
	//sanityCheck();
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

	// we do not take escaping & upToDate into account, they are a cache
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

	// we do not take escaping into account, it is a cache
    }

    /**
     * Conservative constructor for unanalysable calls.
     *
     * <p>Note: this gives a valid summary for all native methods, including
     * Thread.start().
     */
    public static PurityGraph conservativeGraph(SootMethod m)
    {
	PurityGraph g = new PurityGraph();

	// parameters & this escape globally
	Iterator it = m.getParameterTypes().iterator();
	int i = 0;
	while (it.hasNext()) {
	    if (it.next() instanceof RefLikeType) {
		PurityNode n = new PurityParamNode(i);
		g.globEscape.add(n);
		g.nodes.add(n);
		g.paramNodes.add(n);
	    }
	    i++;
	}

	// return value escapes globally
	if (m.getReturnType() instanceof RefLikeType)
	    g.ret.add(new PurityGlobalNode());
	
	g.upToDate = false;
	//g.sanityCheck();
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

	// we do not update escaping on purpose: it is a cache
	upToDate = false;
	//sanityCheck();
    }


    /**
     * Sanity check. Used internally for debugging!
     */
    private void sanityCheck()
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
		if (!backEdges.containsKey(e.getTarget()) ||
		    !backEdges.get(e.getTarget()).contains(e))
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
		if (!(edges.containsKey(e.getSource()) &&
		      edges.get(e.getSource()).contains(e)))
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
		if (!backLocals.containsKey(n) ||
		    !backLocals.get(n).contains(l))
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
		if (!locals.containsKey(l) ||
		    !locals.get(l).contains(n))
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
	if (upToDate) {
	    Set esc = new HashSet(escaping);
	    upToDate = false;
	    rebuildEscape();
	    if (!esc.equals(escaping))
		{G.v().out.println("escaping not up to date");err=true;}
	}
	if (err) {
	    DotGraph dot = new DotGraph("sanityCheckFailure");
	    fillGraph("chk",dot);
	    dot.plot("sanityCheckFailure.dot");
	    throw new Error("PurityGraph sanity check failed!!!");
	}
    }

    ////////////////////////
    // ESCAPE INFORMATION //
    ////////////////////////

    private void internalPassEdges(Set toColor, Set dest, boolean conside_inside)
    {
	Iterator it = toColor.iterator();
	while (it.hasNext()) {
	    PurityEdge edge = (PurityEdge) it.next();
	    if (conside_inside || !edge.isInside()) {
		PurityNode node = edge.getTarget();
		if (!dest.contains(node)) {
		    dest.add(node);
		    internalPassEdges(edges.get(node),dest,conside_inside);
		}
	    }
	}	
    }
    private void internalPassNodes(Set toColor, Set dest, boolean conside_inside)
    {
	Iterator it = toColor.iterator();
	while (it.hasNext()) {
	    PurityNode node = (PurityNode) it.next();
	    if (!dest.contains(node)) {
		dest.add(node);
		internalPassEdges(edges.get(node),dest,conside_inside);
	    }
	}	
    }


    private Set escaping;     // set of escaping nodes
    private boolean upToDate;

    // rebuild entirely from scrath
    private void rebuildEscape()
    {
	escaping = new HashSet();
	internalPassNodes(ret,escaping,true);
	internalPassNodes(globEscape,escaping,true);
	internalPassNodes(paramNodes,escaping,true);
    }

    boolean doesEscape(PurityNode node)
    {
	if (!upToDate) rebuildEscape();
	return escaping.contains(node);
    }


    /**
     * Call this on the merge of graphs at all return points of a method to know
     * whether the method is pure.
     */
    public boolean isPure()
    {
	Set A = new HashSet();
	Set B = new HashSet();
	internalPassNodes(paramNodes, A, false);
	internalPassNodes(globEscape, B, true);
	Iterator it = A.iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode)it.next();
	    if (B.contains(n) || !mutated.get(n).isEmpty()) return false;
	}
	return true;
    }

   /**
    * We use a less restrictive notion of purity for constructors: pure constructors 
    * can mutate fields of this. 
    *
    * @see isPure
    */
    public boolean isPureConstructor()
    {
	Set A = new HashSet();
	Set B = new HashSet();
	internalPassNodes(paramNodes, A, false);
	internalPassNodes(globEscape, B, true);
	PurityNode th = new PurityThisNode();
	Iterator it = A.iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode)it.next();
	    if (B.contains(n) || 
		(!n.equals(th) && !mutated.get(n).isEmpty())) return false;
	}
	return true;
    }


    /////////////////////////
    // GRAPH MANUPULATIONS //
    /////////////////////////

    public Object clone()
    {
	return new PurityGraph(this);
    }

    // utility functions to update local / backLocals constitently
    private final boolean localsRemove(Local local)
    {
	Iterator it = locals.get(local).iterator();
	while (it.hasNext()) {
	    Object node = it.next();
	    backLocals.remove(node,local);
	}
	return locals.remove(local);
    }
    private final boolean localsPut(Local local, PurityNode node)
    {
	backLocals.put(node,local);
	return locals.put(local,node);
    }
    private final boolean localsPutAll(Local local, Set nodes)
    {
	Iterator it = nodes.iterator();
	while (it.hasNext()) {
	    Object node = it.next();
	    backLocals.put(node,local);
	}
	return locals.putAll(local,nodes);
    }

    // utility function to remove a node & all adjacent edges
    private final void removeNode(PurityNode n)
    {
	// we need a copy cause we'll mutate backEdges
	List back = new LinkedList(backEdges.get(n));
	Iterator it = edges.get(n).iterator();
	while (it.hasNext()) {
	    PurityEdge e = (PurityEdge)it.next();
	    backEdges.remove(e.getTarget(),e);
	}
	it = back.iterator();
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

    /** Copy assignment left = right. */
    void assignParamToLocal(int right, Local left)
    {
	// strong update on local
	PurityNode node = new PurityParamNode(right);
	localsRemove(left);
	localsPut(left,node);
	nodes.add(node);
	paramNodes.add(node);
	//sanityCheck();
    }

    /** Copy assignment left = this. */
    void assignThisToLocal(Local left)
    {
	// strong update on local
	PurityNode node = new PurityThisNode();
	localsRemove(left);
	localsPut(left,node);
	nodes.add(node);
	paramNodes.add(node);
	//sanityCheck();
    }

    /** Copy assignment left = right. */
    void assignLocalToLocal(Local right, Local left)
    {
	// strong update on local
	localsRemove(left);
	localsPutAll(left,locals.get(right));
	//sanityCheck();
    }

    /** return right statement . */
    void returnLocal(Local right)
    {
	// strong update on ret
	ret.clear();
	ret.addAll(locals.get(right));
	upToDate = false;
	//sanityCheck();
    }

    /** 
     * Load non-static: left = right.field, or left = right[?] if field is [].
     */
    void assignFieldToLocal(Stmt stmt, Local right, String field, Local left)
    {
	Set esc = new HashSet();

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

	    if (doesEscape(nodeRight)) esc.add(nodeRight);
	}
    
	if (!esc.isEmpty()) {
	    // right can escape
	    
	    // we add a label load node & outside edges
	    PurityNode loadNode = new PurityStmtNode(stmt,false);
	    nodes.add(loadNode);
	    
	    Iterator itEsc = esc.iterator();
	    while (itEsc.hasNext()) {
		PurityNode node = (PurityNode) itEsc.next();
		PurityEdge edge = new PurityEdge(node, field, loadNode, false);
		if (edges.put(node, edge)) {
		    upToDate = false;
		    backEdges.put(loadNode, edge);
		}
	    }
	    localsPut(left, loadNode);
	}
	//sanityCheck();
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
		PurityEdge edge = new PurityEdge(nodeLeft, field, nodeRight, true);
		if (edges.put(nodeLeft, edge)) {
		    upToDate = false;
		    backEdges.put(nodeRight, edge);
		}
	    }
	    if (!nodeLeft.isInside())
		mutated.put(nodeLeft, field);
	}
	//sanityCheck();
    }

    /** Allocation: left = new or left = new[?]. */
    void assignNewToLocal(Stmt stmt, Local left)
    {
	// strong update on local
	// we add a label inside node
	PurityNode node = new PurityStmtNode(stmt,true);
	localsRemove(left);
	localsPut(left, node);
	nodes.add(node);
	//sanityCheck();
    }

    /** A local variable is used in an unknown construct. */
    void localEscapes(Local l)
    {
	// nodes escape globally
	globEscape.addAll(locals.get(l));
	upToDate = false;
	//sanityCheck();
    }

    /** A local variable is assigned to some outside value. */
    void localIsUnknown(Local l)
    {
	// strong update on local
	PurityNode node = new PurityGlobalNode();
	localsRemove(l);
	localsPut(l, node);
	//sanityCheck();
    }

    /** 
     * Store static: C.field = right.
     */
    void assignLocalToStaticField(Local right, String field)
    {
	localEscapes(right);
	mutated.put(new PurityGlobalNode(), field);
	//sanityCheck();	
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
	//sanityCheck();
    }

    /**
     * Store a primitive type into a static field left.field = v
     */
    void mutateStaticField(String field)
    {
	mutated.put(new PurityGlobalNode(), field);
	//sanityCheck();
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
		mu.putAll(new PurityParamNode(nb),locals.get(arg));
	    }
	    nb++;
	}
	if (right!=null) // (1) rule for "this" argument
	    mu.putAll(new PurityThisNode(),locals.get(right));

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
			    PurityEdge edge = new PurityEdge(mu1,f,mu2,true);
			    edges.put(mu1,edge);
			    backEdges.put(mu2,edge);
			}
		    }
		    else {
			PurityEdge edge = new PurityEdge(mu1,f,n2,false);
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

	upToDate = false;
	//sanityCheck();

	// simplification
	/////////////////	
		
	it = (new LinkedList(nodes)).iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode)it.next();
	    if (!doesEscape(n)) 
		if (n.isLoad()) removeNode(n);
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

	//sanityCheck();
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
     * <p> Note: escaping nodes have a red label and some inside diagonal
     * lines (unfortunately, the lines do not show in dotty, hence the
     * color).
     * Load nodes and outside edges are blue and dashed while inside nodes
     * are plain (unfortunately, dash doesn't show in dotty, hence the color).
     */
    void fillGraph(final String prefix, final DotGraph out)
    {
	final Map nodeId = new HashMap();
	int id = 0;

	// add nodes 
	// (inside = black with diagonals; load, param = gray; 
	// escaping = red; globally escaping = purple)
	Iterator it = nodes.iterator();
	while (it.hasNext()) {
	    PurityNode n = (PurityNode) it.next();
	    if (!edges.get(n).isEmpty() ||
		!backEdges.get(n).isEmpty() ||
		!backLocals.get(n).isEmpty() ||
		!mutated.get(n).isEmpty() ||
		n.isParam() ||
		ret.contains(n)) {
		String label = "N"+prefix+"_"+id;
		DotGraphNode node = out.drawNode(label);
		node.setLabel(n.toString());
		if (!n.isInside()) {
		    node.setStyle("dashed");
		    node.setAttribute("color","gray50");
		}
		if (globEscape.contains(n))
		    node.setAttribute("fontcolor","purple");
		else if (doesEscape(n))
		    node.setAttribute("fontcolor","red");
		nodeId.put(n,label);
		id++;
	    }
	}

	/*  this is debugging code
	class Zut {
	    int id = 0;
	    public void makeSureNodeIsThere(Object obj) {
		if (!nodeId.containsKey(obj)) {
		    PurityNode n = (PurityNode)obj;
		    DotGraphNode node = out.drawNode("N"+prefix+"_XXX_"+id);
		    nodeId.put(n,"N"+prefix+"_XXX_"+id);
		    node.setLabel(" XXX "+n.toString()+" XXX ");
		    G.v().out.println(" inexistent node: "+n.toString()+" => "+"N"+prefix+"_XXX_"+id);
		    id++;
		}
	    }
	};
	Zut zut = new Zut();
	*/
	
	// add edges (inside = black, solid; outside = gray, dashed)
	it = edges.keySet().iterator();
	while (it.hasNext()) {
	    PurityNode src = (PurityNode) it.next();
	    Iterator itt = edges.get(src).iterator();
	    while (itt.hasNext()) {
		PurityEdge e = (PurityEdge) itt.next();
		//zut.makeSureNodeIsThere(e.getSource());
		//zut.makeSureNodeIsThere(e.getTarget());
		DotGraphEdge edge = 
		    out.drawEdge((String)nodeId.get(e.getSource()),
				 (String)nodeId.get(e.getTarget()));
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
		    //zut.makeSureNodeIsThere(dst);
		    out.drawEdge(label,(String)nodeId.get(dst));
		}
		id++;
	    }
	}

	// ret
	if (!ret.isEmpty()) {
	    DotGraphNode node = out.drawNode("ret"+id);
	    node.setLabel("ret");
	    node.setShape("plaintext");	    
	    Iterator itt = ret.iterator();
	    while (itt.hasNext()) {
		PurityNode dst = (PurityNode) itt.next();
		//zut.makeSureNodeIsThere(dst);
		out.drawEdge("ret"+id,(String)nodeId.get(dst));
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
		//zut.makeSureNodeIsThere(n);
		DotGraphEdge edge = out.drawEdge((String)nodeId.get(n),label);
		edge.setLabel(f);
		id++;
	    }
	}
    }

}
