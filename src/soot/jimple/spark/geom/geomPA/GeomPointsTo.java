/* Soot - a J*va Optimization Framework
 * Copyright (C) 2011 Richard Xiao
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
package soot.jimple.spark.geom.geomPA;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import soot.AnySubType;
import soot.ArrayType;
import soot.Context;
import soot.G;
import soot.Local;
import soot.MethodOrMethodContext;
import soot.PointsToSet;
import soot.RefType;
import soot.Scene;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.spark.geom.geomE.FullSensitiveNodeGenerator;
import soot.jimple.spark.geom.heapinsE.HeapInsNodeGenerator;
import soot.jimple.spark.geom.ptinsE.PtInsNodeGenerator;
import soot.jimple.spark.internal.TypeManager;
import soot.jimple.spark.pag.AllocDotField;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.ArrayElement;
import soot.jimple.spark.pag.ContextVarNode;
import soot.jimple.spark.pag.FieldRefNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.SparkField;
import soot.jimple.spark.pag.VarNode;
import soot.jimple.spark.sets.EmptyPointsToSet;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.SparkOptions;
import soot.toolkits.scalar.Pair;
import soot.util.queue.QueueReader;

 
/**
 * The main interface for the points-to analysis with geometric encodings.
 * Since we need SPARK to bootstrap our analysis, thus, we identify ourself to be a subclass of SPARK.
 * 
 * @author richardxx
 *
 */
public class GeomPointsTo extends PAG 
{	
	// The constants for the constraints type identification
	public static final int NEW_CONS = 0;
	public static final int ASSIGN_CONS = 1;
	public static final int LOAD_CONS = 2;
	public static final int STORE_CONS = 3;
	public static final int FIELD_ADDRESS = 4;
	
	// The constants for the mapping relationships
	public static final int Undefined_Mapping = -1;
	public static final int ONE_TO_ONE = 0;
	public static final int MANY_TO_MANY = 1;
	public static final int ALL_TO_MANY = 0;
	public static final int MANY_TO_ALL = 1;
	
	// The constants for the call graph
	public static final int SUPER_MAIN = 0, UNKNOWN_FUNCTION = -1;
	
	// The number of contexts that is natively supported by Java (2^63)
	// Using big integer would not bring too many benefits.
	public static final long MAX_CONTEXTS = Long.MAX_VALUE - 1;
	
	// Some commonly referred to information
	public static final RefType exeception_type = RefType.v( "java.lang.Throwable" );
	
	// The parameters that are used to tune the precision and performance tradeoff
	public static int max_cons_budget = 40;
	public static int max_pts_budget = 80;
	public static int cfa_blocks = Integer.MAX_VALUE;
	public static int cg_refine_times = 1;
	
	// Worklist, the core data structure for fixed point computation
	// Other choice, FIFO_Worklist
	protected IWorklist worklist = null;
	
	// The generator that is used to generate the internal representations for the pointers and objects
	protected IEncodingBroker nodeGenerator = null;
	
	// The same type manager used by SPARK
	protected TypeManager typeManager = null;
	
	// The offline processing strategies for the constraints
	protected OfflineProcessor offlineProcessor = null;
	
	// A table that maps the SPARK nodes to the geometric nodes 
	public Map<Node, IVarAbstraction> consG = new HashMap<Node, IVarAbstraction>();
	
	// Stores all the pointers including the instance fields
	public ZArrayNumberer<IVarAbstraction> pointers = new ZArrayNumberer<IVarAbstraction>();
	
	// Stores all the symbolic objects
	public ZArrayNumberer<IVarAbstraction> allocations = new ZArrayNumberer<IVarAbstraction>();
	
	// Store all the constraints, initially generated from SPARK
	public Vector<PlainConstraint> constraints = new Vector<PlainConstraint>();
	
	// All the callsites that spawn a new thread
	public Set<Stmt> thread_run_callsites = new HashSet<Stmt>();
	
	// the internal ID of the main method
	public int mainID = -1;			
	
	/*
	 * Context size records the total number of instances for a function.
	 * max_context_size_block is the context size of the largest block for a function in cycle  
	 */
	public long context_size[], max_context_size_block[];
	
	// Number of context blocks for a function
	public int block_num[];
	
	// Analysis statistics
	public int max_scc_size, max_scc_id;
	public int n_var, n_alloc_sites, n_func, n_calls;
	public int n_reach_methods, n_reach_user_methods;
	
	// Output options
	public String dump_file_name = null;
	public int solver_encoding;
	public PrintStream ps = null;
	
	
	/*
	 * This container contains the methods that are considered "valid" by user.
	 * For example, we want to compare the geometric points-to result with 1-obj analysis.
	 * They may compute different set of reachable functions due to the different precision.
	 * To make the comparison fairly, we only evaluate the functions that are reachable in both analyses. 
	 */
	protected Map<String, Boolean> validMethods = null;
	
	// Call graph related components
	protected CgEdge call_graph[];
	protected Vector<CgEdge> obsoletedEdges = new Vector<CgEdge>();
	protected Map<Integer, LinkedList<CgEdge>> rev_call_graph;
	protected Deque<Integer> queue_cg = new LinkedList<Integer>();
	
	// Containers used for call graph traversal
	protected int vis_cg[], low_cg[], rep_cg[], indeg_cg[], scc_size[];
	protected int pre_cnt;			// preorder time-stamp for constructing the SCC condensed call graph
	
	// The mappings between Soot constructs and our internal representations
	protected Map<SootMethod, Integer> func2int = new HashMap<SootMethod, Integer>(5011);
	protected Map<Integer, SootMethod> int2func = new HashMap<Integer, SootMethod>(5011);
	protected Map<Edge, CgEdge> edgeMapping = new HashMap<Edge, CgEdge>();
	
	// Others
	private boolean hasTransformed = false;
	// Because we override the points-to query interface for SPARK, we need this flag to know how to answer queries
	private boolean hasExecuted = false;		
	
	
	// -------------------Constructors--------------------
	public GeomPointsTo( final SparkOptions opts ) {
		super(opts);
	}
	
	public String toString()
	{
		return "Geometric Points-To Analysis";
	}
	
	public void parametrize()
	{
		// We first setup the encoding methodology
		solver_encoding = opts.geom_encoding();
    			
    	if ( solver_encoding == SparkOptions.geom_encoding_Geom )
    		nodeGenerator = new FullSensitiveNodeGenerator();
    	else if ( solver_encoding == SparkOptions.geom_encoding_HeapIns )
    		nodeGenerator = new HeapInsNodeGenerator();
    	else if ( solver_encoding == SparkOptions.geom_encoding_PtIns )
    		nodeGenerator = new PtInsNodeGenerator();
    	
    	if ( nodeGenerator == null )
    		throw new RuntimeException( "The encoding " + solver_encoding + " is unavailable for geometric points-to analysis." );
    	
    	// The encoding may not be specified by the user
    	solver_encoding = nodeGenerator.getEncodingType();
    	
    	// Then, we set the worklist
    	switch ( opts.geom_worklist() ) {
	    	case SparkOptions.geom_worklist_FIFO:
	    		worklist = new FIFO_Worklist();
	    		break;
	    		
	    	case SparkOptions.geom_worklist_PQ:
	    		worklist = new PQ_Worklist();
	    		break;
    	}
    	
    	// We dump the processing statistics to an external file if needed by the user
    	dump_file_name = opts.geom_dump_verbose();
    	if ( !dump_file_name.isEmpty() ) {
			File file = new File( dump_file_name + "_" + solver_encoding + "_log.txt" );
			try {
				ps = new PrintStream(file);
			} catch (FileNotFoundException e) {
				System.err.println( "The dump file: " + dump_file_name + " cannot be created." );
				System.exit(-1);
			}
		}
		else
			ps = G.v().out;
    	
    	// Load the method signatures computed by other points-to analysis
    	// With these methods, we can compare the points-to results fairly. 
		String method_verify_file = opts.geom_verify_name();
		try {
			FileReader fr = new  FileReader( method_verify_file );
			java.util.Scanner fin = new java.util.Scanner(fr);
			validMethods = new HashMap<String, Boolean>();
			
			while ( fin.hasNextLine() ) {
				validMethods.put( fin.nextLine(), Boolean.FALSE );
			}
		} catch (FileNotFoundException e) {
			validMethods = null;
		}
		
		// The tunable parameters
		max_cons_budget = opts.geom_frac_base();
		max_pts_budget = max_cons_budget * 2;
		if ( opts.geom_blocking() )
			cfa_blocks = Integer.MAX_VALUE;
		else
			cfa_blocks = 1;
		cg_refine_times = opts.geom_runs();
		
		// Prepare other stuff
		consG.clear();
		constraints.clear();
		func2int.clear();
		edgeMapping.clear();
		typeManager = getTypeManager();
		
		ps.println();
		ps.println( solver_encoding + " starts working on " + (dump_file_name.isEmpty() ? "untitled" : dump_file_name) + " benchmark." );
	}
	
	/**
	 *	Read in the program facts generated by SPARK.
	 *  We also construct our own call graph and node representations.
	 */
	@SuppressWarnings("unchecked")
	private Set<Node> preprocess() 
	{
		int id;
		int s, t;
		Set<Node> virtualBases = new HashSet<Node>();
		
		// Build the call graph
		n_func = Scene.v().getReachableMethods().size() + 1;
		call_graph = new CgEdge[n_func];

		n_calls = 0;
		id = 1;
		QueueReader<MethodOrMethodContext> smList = Scene.v().getReachableMethods().listener();
		while (smList.hasNext()) {
			final SootMethod func = (SootMethod) smList.next();
			func2int.put(func, id);
			int2func.put(id, func);
			if ( Scene.v().getCallGraph().isEntryMethod(func) || 
					func.isEntryMethod() ) {
				CgEdge p = new CgEdge(SUPER_MAIN, id, null, call_graph[SUPER_MAIN]);
				call_graph[SUPER_MAIN] = p;
				n_calls++;
			}
			
			if ( func.isMain() )
				mainID = id;
			
			// Clear the points-to set of THIS parameter
//			if ( !func.isStatic() ) {
//				MethodPAG funcpag = MethodPAG.v(spark_pag, func);
//				Node nthis = funcpag.nodeFactory().caseThis();
//				nthis.discardP2Set();
//			}
			
			id++;
		}

		// Next, we scan all the call edges and rebuild the call graph in our own vocabulary
		QueueReader<Edge> edgeList = Scene.v().getCallGraph().listener();
		while (edgeList.hasNext()) {
			Edge edge = edgeList.next();
			if ( edge.isClinit() ) {
				continue;
			}
			
			SootMethod src_func = edge.src();
			SootMethod tgt_func = edge.tgt();
			s = func2int.get(src_func);
			t = func2int.get(tgt_func);
			
			// Create a new call edge in our own format
			CgEdge p = new CgEdge(s, t, edge, call_graph[s]);
			call_graph[s] = p;
			edgeMapping.put(edge, p);
			
			// We only care about the virtual callsites
			if ( edge.isVirtual() ) {
				VirtualInvokeExpr expr = (VirtualInvokeExpr)edge.srcStmt().getInvokeExpr();
				p.base_var = findLocalVarNode( expr.getBase() );
				
				// A forward or callback edge?
				if ( (!edge.src().isJavaLibraryMethod()) ||
						(!edge.tgt().isJavaLibraryMethod()) )
					virtualBases.add(p.base_var);
			}
			
			// We don't modify the treatment of the Thread.start calls
			if ( edge.tgt().getSignature().equals("<java.lang.Thread: void start()>") )
				thread_run_callsites.add(p.sootEdge.srcStmt());
			
			++n_calls;
		}
		
		// We build the wrappers for all the pointers built by SPARK
		for ( Iterator<VarNode> it = getVarNodeNumberer().iterator(); it.hasNext(); ) {
			VarNode vn = it.next();
			IVarAbstraction pn = getInternalNode(vn);
			pointers.add(pn);
		}
		
		for ( Iterator<AllocDotField> it = getAllocDotFieldNodeNumberer().iterator(); it.hasNext(); ) {
			AllocDotField adf = it.next();
			IVarAbstraction pn = getInternalNode(adf);
			pointers.add(pn);
		}
		
		for ( Iterator<AllocNode> it = getAllocNodeNumberer().iterator(); it.hasNext(); ) {
			AllocNode obj = it.next();
			IVarAbstraction pn = getInternalNode(obj);
			allocations.add(pn);
		}
		
		n_var = pointers.size();
		n_alloc_sites = allocations.size();

		// Now we extract all the constraints from SPARK
		// The address constraints, new obj -> p
		for (Object object : allocSources()) {
			IVarAbstraction obj = getInternalNode( (AllocNode)object );
			Node[] succs = allocLookup( (AllocNode)object );
			for (Node element0 : succs) {
				PlainConstraint cons = new PlainConstraint();
				IVarAbstraction p = getInternalNode(element0);
				cons.expr.setPair(obj, p);
				cons.type = NEW_CONS;
				constraints.add( cons );
			}
		}

		// The assign constraints, p -> q
		Pair<Node, Node> intercall = new Pair<Node, Node>();
		for (Object object : simpleSources()) {
			IVarAbstraction p = getInternalNode( (VarNode) object );
			Node[] succs = simpleLookup( (VarNode)object );
			for (Node element0 : succs) {
				PlainConstraint cons = new PlainConstraint();
				IVarAbstraction q = getInternalNode( element0 );
				cons.expr.setPair( p, q );
				cons.type = ASSIGN_CONS;
				intercall.setPair( (VarNode)object, element0 );
				cons.interCallEdges = lookupEdgesForAssignment(intercall);
				constraints.add( cons );
			}
		}
		intercall = null;
		assign2edges.clear();
		
		// The load constraints, p.f -> q
		for (Object object : loadSources()) {
			FieldRefNode frn = (FieldRefNode)object;
			IVarAbstraction p = getInternalNode( frn.getBase() );
			Node[] succs = loadLookup( frn );
			for (Node element0 : succs) {
				PlainConstraint cons = new PlainConstraint();
				IVarAbstraction q = getInternalNode( element0 );
				cons.f = frn.getField();
				cons.expr.setPair( p, q );
				cons.type = LOAD_CONS;
				constraints.add( cons );
			}
		}

		// The store constraints, p -> q.f
		for (Object object : storeSources()) {
			IVarAbstraction p = getInternalNode( (VarNode)object );
			Node[] succs = storeLookup( (VarNode)object );
			for (Node element0 : succs) {
				PlainConstraint cons = new PlainConstraint();
				FieldRefNode frn = (FieldRefNode)element0;
				IVarAbstraction q = getInternalNode( frn.getBase() );
				cons.f = frn.getField();
				cons.expr.setPair( p, q );
				cons.type = STORE_CONS;
				constraints.add( cons );
			}
		}
		
		// Initialize other stuff
		low_cg = new int[n_func];
		vis_cg = new int[n_func];
		rep_cg = new int[n_func];
		indeg_cg = new int[n_func];
		scc_size = new int[n_func];
		block_num = new int[n_func];
		context_size = new long[n_func];
		max_context_size_block = new long[n_func];
		
		return virtualBases;
	}
	
	private void callGraphDFS(int s) {
		int t;
		CgEdge p;

		vis_cg[s] = low_cg[s] = pre_cnt++;
		queue_cg.addLast(s);
		p = call_graph[s];
		
		while (p != null) {
			t = p.t;
			if (vis_cg[t] == 0)
				callGraphDFS(t);
			if (low_cg[t] < low_cg[s])
				low_cg[s] = low_cg[t];
			p = p.next;
		}

		if (low_cg[s] < vis_cg[s]) {
			scc_size[s] = 1;
			return;
		}

		scc_size[s] = queue_cg.size();

		do {
			t = queue_cg.getLast();
			queue_cg.removeLast();
			rep_cg[t] = s;
			low_cg[t] += n_func;
		} while (s != t);

		scc_size[s] -= queue_cg.size();
		if (scc_size[s] > max_scc_size) {
			max_scc_size = scc_size[s];
			max_scc_id = s;
		}
	}

	/**
	 *  Build a call graph, merge the SCCs and name the contexts.
	 */
	private void encodeContexts() 
	{
		int i, j;
		int n_reachable = 0, n_scc_reachable = 0;
		int n_full = 0;
		long max_contexts = Long.MIN_VALUE;
		CgEdge p;

		pre_cnt = 1;
		max_scc_size = 1;
		for (i = 0; i < n_func; ++i) {
			vis_cg[i] = 0;
			indeg_cg[i] = 0;
			max_context_size_block[i] = 0;
		}

		// We only consider all the methods which are reachable from SUPER_MAIN
		queue_cg.clear();
		callGraphDFS(SUPER_MAIN);

		// Then, we topologically number the contexts starting from the SUPER_MAIN function
		// We count the in-degree of each function.
		// And, we classify the call edges into SCC/non-SCC edges
		for (i = 0; i < n_func; ++i) {
			if (vis_cg[i] == 0)
				continue;

			p = call_graph[i];
			while (p != null) {
				// If this is an edge linking two functions in the same SCC
				if (rep_cg[i] == rep_cg[p.t]) {
					p.scc_edge = true;
					// 0-CFA modeling for the SCC, the default mode
					p.map_offset = 1;
				} else {
					p.scc_edge = false;
					++indeg_cg[rep_cg[p.t]];
				}
				
				p = p.next;
			}

			++n_reachable;
			if ( rep_cg[i] == i ) ++n_scc_reachable;
			
			// Un-comment the following code to see which functions are contained in the maximum SCC
//			if ( rep_cg[i] == max_scc_i ) {
//				SootMethod sm = int2func.get(i);
//				if ( !sm.is_java_library_method() )
//					ps.println( sm );
//			}
		}

		// Next, we condense the SCCs
		// Later, we have to restore the call graph in order to serve the
		// context sensitive queries
		for (i = 0; i < n_func; ++i)
			if (vis_cg[i] != 0 && rep_cg[i] != i) {
				// Any node in a SCC must have at least one outgoing edge
				p = call_graph[i];
				while (p.next != null)
					p = p.next;
				p.next = call_graph[rep_cg[i]];
				// Note that, call_graph[i] is not cleared after merging
				call_graph[rep_cg[i]] = call_graph[i];
			}

		// Now, we add all the source nodes to the queue
		max_context_size_block[SUPER_MAIN] = 1;
		queue_cg.addLast(SUPER_MAIN);

		while ( !queue_cg.isEmpty() ) {
			i = queue_cg.getFirst();
			queue_cg.removeFirst();
			p = call_graph[i];
			
			while (p != null) {
				if (p.scc_edge == false) {
					// Consider the representative only
					j = rep_cg[p.t];

					/*
					 * We can control how many contexts created for a specified
					 * function. And, for any call edge, we can manually move
					 * the mapping interval from caller to callee.
					 */
					if (MAX_CONTEXTS - max_context_size_block[i] < max_context_size_block[j]) {
						// The are more than 2^63 - 1 execution paths, terrible!
						// We have to merge some contexts in order to make the
						// analysis sound!
						p.map_offset = MAX_CONTEXTS - max_context_size_block[i] + 1;
						max_context_size_block[j] = MAX_CONTEXTS;
//						Edge e = p.sootEdge;
//						if ( e == null )
//							ps
//								.println("~~~~~~~~~~~~Max Block For " + j + " is full!!!");
//						else
//							ps
//								.println("~~~~~~~~~~~~Max Block For " + e.getTgt().method() + " is full!!!");
					} else {
						p.map_offset = max_context_size_block[j] + 1;
						max_context_size_block[j] += max_context_size_block[i];
					}

					// Add to the worklist
					if (--indeg_cg[j] == 0)
						queue_cg.addLast(j);
				}

				p = p.next;
			}
			
			if ( max_context_size_block[i] > max_contexts )
				max_contexts = max_context_size_block[i];
		}

		// Sanity check
//		for ( i = 0; i < n_func; ++i ) {
//			if ( rep_cg[i] != i ) continue;
//			assert indeg_cg[i] == 0 && max_context_size_block[i] != 0;
//		}
			
		// Now we restore the call graph
		for (i = n_func - 1; i > -1; --i) {
			if ( vis_cg[i] == 0 ) continue;
			if ( rep_cg[i] != i ) {
				// We recharge the information into the none SCC representative nodes
				max_context_size_block[i] = max_context_size_block[rep_cg[i]];
				
				// Put all the call edges back
				p = call_graph[i];
				while (p.next.s == i)
					// p.next.s may not be i because it would be linked to another scc member
					p = p.next;
				
				call_graph[rep_cg[i]] = p.next;
				p.next = null;
			}
			
			if ( max_context_size_block[i] == MAX_CONTEXTS )
				++n_full;
			context_size[i] = max_context_size_block[i];
			block_num[i] = 1;
		}

		// Sanity check
//		for ( i = 0; i < n_func; ++i ) {
//			p = call_graph[i];
//			while ( p != null ) {
//				assert p.s == i;
//				p = p.next;
//			}
//		}
		
		if ( cfa_blocks > 1 
//				&& max_contexts < MAX_CONTEXTS 
				) {
			// We scan all the edges again, and tune the SCC related call edges
			// We don't manipulate the non-SCC edges, because they don't induce problems
			for ( i = 0; i < n_func; ++i ) {
				if ( vis_cg[i] == 0 ) continue;
				
				p = call_graph[i];
				while ( p != null ) {
					j = p.t;
					if ( j != i && p.scc_edge == true ) {
						// This is not a self-loop, and a self-loop is treated specially in the initial encoding phase
						if ( block_num[j] < cfa_blocks && 
								context_size[j] <= MAX_CONTEXTS - max_context_size_block[i] ) {
							p.map_offset = context_size[j] + 1;
							context_size[j] += max_context_size_block[i];
							++block_num[j];
						}
						else {
							if ( block_num[j] < cfa_blocks ) {
								context_size[j] = MAX_CONTEXTS;
								++block_num[j];
							}
							
							p.map_offset = context_size[j] - max_context_size_block[i] + 1;
							
//							if ( context_size[j] > MAX_CONTEXTS - max_context_size_block[i] )
//								ps.println("~~~~~~~~~~~~Context Range For " + j + " is full!!!");
						}
					}
					
					p = p.next;
				}
			}
		}
			
		// Print debug info
		ps.printf("Reachable Methods = %d, in which #Condensed Nodes = %d, #Full Context Nodes = %d \n", 
				n_reachable - 1, n_scc_reachable, n_full );
		ps.printf("Maximum SCC = %d \n", max_scc_size);
		ps.printf("The maximum context size = %e\n", (double)max_contexts );
	}
	
	/**
	 *  We iteratively update the call graph and the constraints list until our
	 *  demand is satisfied
	 */
	private void solveConstraints() 
	{
//		ps.println( "\nInitially we inserted " + IEncodingBroker.n_added_flowedge + " flow edges.\n" );
		
		while (worklist.has_job()) {
			IVarAbstraction pn = worklist.next();
//			ps.println( "New points-to : " + pn.count_new_pts_intervals() );
//			debug_context_sensitive_objects(pn);
			pn.do_before_propagation();
			pn.propagate(this, worklist);
			pn.do_after_propagation();
			
//			++n_trigger;
//			if ( ( n_trigger % (n_trigger > 15000 ? 1000: 10) ) == 0 ) {
//				ps.println( "After propagating " + n_trigger + 
//						" pointers, we have " + IEncodingBroker.n_added_flowedge + " flow edges and " + 
//						IEncodingBroker.n_added_pts + " points-to facts." );
//				ps.println( "propagating : " + n_trigger + " pointers." );
//			}
//			
//			if ( n_trigger > 1270000 )
//				break;
		}
		
//		ps.println( "\nFinally we have " + IEncodingBroker.n_added_flowedge + " flow edges.\n" );
	}
	
	private int updateCallGraph() 
	{
		int all_virtual_edges = 0, n_obsoleted = 0;
		boolean keep_this_edge;
		CgEdge p, q, temp;
		
		for (int i = 1; i < n_func; ++i) {
			// New outgoing edge list is pointed to by q
			p = call_graph[i];
			q = null;

			while (p != null) {
				
				if ( vis_cg[i] == 0 ) {
					// If this method is unreachable, we delete all its outgoing edges
					p.is_obsoleted = true;
				}
				else if ( vis_cg[i] != 0 
						&& p.base_var != null 
						&& (!thread_run_callsites.contains( p.sootEdge.srcStmt() ) ) 
						) {
					// It is a virtual call
					keep_this_edge = false;
					++all_virtual_edges;
					IVarAbstraction pn = consG.get(p.base_var).getRepresentative();
					
					if ( pn != null ) {
						SootMethod sm = p.sootEdge.tgt();
						
						for (AllocNode an : pn.get_all_points_to_objects()) {
							Type t = an.getType();
							if ( t instanceof AnySubType ||
									 t instanceof ArrayType ) {
								keep_this_edge = true;
								break;
							}
							
							// Only the virtual calls do the following test
							// We should care about the thread start method, it is treated specially
							if ( Scene.v().getOrMakeFastHierarchy().
									resolveConcreteDispatch( ((RefType)t).getSootClass(), sm) == sm ) {
								keep_this_edge = true;
								break;
							}
						}
						
						if (keep_this_edge == false) {
							p.is_obsoleted = true;
							// We only count the edges from the user code
							if ( !p.sootEdge.src().isJavaLibraryMethod() )
								++n_obsoleted;
						}
					}
					else {
						System.err.println();
						throw new RuntimeException("In update_call_graph: oops, what's up?");
					}
				}
								
				temp = p.next;
				if (p.is_obsoleted == false) {
					p.next = q;
					q = p;
				}
				else {
					// We put this obsoleted edge into a special container
					obsoletedEdges.add(p);
				}
				p = temp;
			}

			call_graph[i] = q;
		}

		ps.println( "Totally " + all_virtual_edges + " virtual edges, " + 
						"we find " + n_obsoleted + " of them are obsoleted.");
		return n_obsoleted;
	}

	/**
	 * Prepare for the next iteration.
	 */
	private void prepareNextRun() 
	{
		// Clean the context sensitive points-to results
		for (IVarAbstraction pn : pointers) {
			pn.reconstruct();
		}
		
		// Clean the obsoleted constraints
		offlineProcessor.recleanConstraints();
		
		// Reclaim
		System.gc();
		System.gc();
		System.gc();
		System.gc();
		System.gc();
	}
	
	protected int countReachableMethods( int s )
	{
		int ans = 1;
		CgEdge p;
		
		p = call_graph[s];
		vis_cg[s] = 1;
		while ( p != null ) {
			if ( vis_cg[p.t] == 0 )
				ans += countReachableMethods(p.t);
			p = p.next;
		}
		
		if ( s != SUPER_MAIN ) {
			SootMethod sm = int2func.get(s);
			if ( !sm.isJavaLibraryMethod() )
				++n_reach_user_methods;
		}
		
		return ans;
	}
	
	private void postProcess()
	{
		// Compute the set of reachable functions after the points-to analysis
		for ( int i = 0; i < n_func; ++i ) vis_cg[i] = 0;
		n_reach_user_methods = 0;
		n_reach_methods = countReachableMethods(SUPER_MAIN);
		
		// Update our reachable methods record and rebuild the reverse call graph 
		rev_call_graph = new HashMap<Integer, LinkedList<CgEdge>>();
		for (int i = 0; i < n_func; ++i) {
			if (vis_cg[i] == 0) {
				func2int.remove( int2func.get(i) );
				int2func.remove(i);
				continue;
			}
			
			// Construct the reverse call graph
			CgEdge p = call_graph[i];
			while ( p != null ) {
				LinkedList<CgEdge> list = rev_call_graph.get(p.t);
				if ( list == null ) {
					list = new LinkedList<CgEdge>();
					rev_call_graph.put(p.t, list);
				}
				list.add(p);
				
				p = p.next;
			}
		}
		
		// Clean the unreachable pointers and objects
		Set<AllocNode> obs_objs = new HashSet<AllocNode>();
		List<IVarAbstraction> obs_pts = new LinkedList<IVarAbstraction>();
		List<AllocNode> temp_list = new LinkedList<AllocNode>();
		
		// We only collect the obsoleted objects but not remove them from the allocations
 		for ( Iterator<IVarAbstraction> it = allocations.iterator(); it.hasNext(); ) {
			AllocNode obj = (AllocNode)it.next().getWrappedNode();
			SootMethod sm = obj.getMethod();
			if ( sm != null &&
					func2int.containsKey(sm) == false )
				obs_objs.add(obj);
		}
		
		for ( IVarAbstraction pn : pointers ) {
			// Is this pointer obsoleted?
			Node node = pn.getWrappedNode();
			SootMethod sm = null;
			
			if ( node instanceof LocalVarNode ) {
				sm = ((LocalVarNode)node).getMethod();
			}
			else if ( node instanceof AllocDotField ) {
				sm = ((AllocDotField)node).getBase().getMethod();
			}
			
			if ( sm != null ) {
				if ( func2int.containsKey(sm) == false ) {
					obs_pts.add(pn);
					continue;
				}
			}
			
			if ( pn.getRepresentative() != pn )
				continue;
			
			// Otherwise, we remove the useless shapes or objects
			temp_list.clear();
			for ( AllocNode obj : pn.get_all_points_to_objects() ) {
				if ( obs_objs.contains(obj) )
					temp_list.add(obj);
			}
			
			for ( AllocNode obj : temp_list )
				pn.remove_points_to(obj);
			
			pn.drop_duplicates();
		}
		
		// No we delete the useless pointers and objects
		for ( IVarAbstraction pn : obs_pts )
			pointers.remove(pn);
		
		for ( AllocNode an : obs_objs ) {
			IVarAbstraction po = consG.get(an);
			allocations.remove(po);
		}
		
		obs_objs = null;
		temp_list = null;
	}
	
	/**
	 * For many applications, they only need the context insensitive points-to result.
	 * We provide a way to transfer our result back to SPARK.
	 * After the transformation, we discard the context sensitive points-to information.
	 * Therefore, the context sensitive queries are not served since then.
	 */
	public void transformToCIResult()
	{
		// We first update the Soot call graph
		for ( CgEdge p : obsoletedEdges ) {
			Scene.v().getCallGraph().removeEdge(p.sootEdge);
		}
		
		// We remove the unreachable functions from Soot internal structures
		Scene.v().releaseReachableMethods();
		// The we rebuild it from the updated Soot call graph
		Scene.v().getReachableMethods();
		
		// Finally, we transform the points-to facts to context insensitive form
		for ( IVarAbstraction pn : pointers ) {
			Node node = pn.getWrappedNode();
			
			if ( node.getP2Set() == EmptyPointsToSet.v() ) {
				PointsToSetInternal ptSet = node.makeP2Set();
				for ( AllocNode obj : pn.getRepresentative().get_all_points_to_objects() ) {
					ptSet.add( obj );
				}
			}
		}
		
		// Release the resource occupied by the geometric points-to analysis
		for ( IVarAbstraction pn : pointers )
			pn.discard();
	}
	
	public void solve() 
	{
		Date begin, end;
		long time;
		long mem2;
		int rounds;
		
		// Otherwise the outputs may interleave
		G.v().out.flush();
		
		// We first clean the memory system
		System.gc(); System.gc(); System.gc(); System.gc(); System.gc();
//		mem1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//		ps.println("Spark [Time] : " + spark_run_time + "s" );
//		ps.println("Spark [Memory] : " + (mem1  / 1024 / 1024) + "MB" );
		
		// Substantially use the Anderson's points-to result for redundancy elimination
		begin = new Date();
		Set<Node> virtualBaseSet = preprocess();
		offlineProcessor = new OfflineProcessor(n_var, this);
		offlineProcessor.runOptimizations( virtualBaseSet );
		worklist.initialize(n_var);
		end = new Date();
		
		time = end.getTime() - begin.getTime();
		ps.printf("Preprocess Time : %.3fs \n", (double) time / 1000);
		
		System.gc(); System.gc(); System.gc(); System.gc(); System.gc();
		
		// Start our constraints solving phase
		begin = new Date();
		rounds = 0;
		while (true) {

			ps.println("\n" + "Round " + rounds + " : ");
			
			// Encode the contexts
			encodeContexts();

			// We construct the initial flow graph
			nodeGenerator.initFlowGraph(this);

			// Solve the constraints
			solveConstraints();
			
			if (++rounds >= cg_refine_times)
				break;
			
			// We update the call graph
			updateCallGraph();
			
			// Clean current result and prepare for the re-iteration
			prepareNextRun();
		}

		end = new Date();
		time = end.getTime() - begin.getTime();
		mem2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		
		updateCallGraph();
		postProcess();
		ps.printf( "Current Reachable Methods = %d, Originally = %d \n", n_reach_methods, n_func - 1 );
		
		ps.println();
		ps.printf("Geometric [Time] : %.3fs \n", (double) time / 1000 );
		ps.printf("Geometric [Memory] : %.3fMB \n", (double) (mem2) / 1024 / 1024 );
		
		int evalLevel = opts.geom_eval();
		if ( evalLevel > 0 ) {
			GeomEvaluator ge = new GeomEvaluator(this, ps);
			ge.reportBasicMetrics();
			
			if ( evalLevel > 1 ) {
				ge.check_virtual_functions();
				ge.check_casts_safety();
				ge.check_alias_analysis();
			}
		}
		
		if ( opts.geom_trans() ) {
			transformToCIResult();
			hasTransformed = true;
		}
		
		System.gc(); System.gc(); System.gc(); System.gc(); System.gc();
		hasExecuted = true;
	}
	
	public int getIDFromSootMethod( SootMethod sm )
	{
		return func2int.get(sm);
	}
	
	public SootMethod getSootMethodFromID( int fid )
	{
		return int2func.get(fid);
	}
	
	public boolean isReachableMethod( int fid )
	{
		return vis_cg[fid] != 0;
	}

	public boolean isValidMethod( SootMethod sm )
	{
		if ( validMethods != null ) {
			String sig = sm.toString();
			if ( !validMethods.containsKey(sig) )
				return false;
			
			// We mark this method for future inspection
			validMethods.put(sig, Boolean.TRUE);
		}
		
		return true;
	}
	
	public void outputNotEvaluatedMethods()
	{
		if ( validMethods != null ) {
			ps.println( "\nThe following methods are not evaluated because they are unreachable:" );
			for ( Map.Entry<String, Boolean> entry : validMethods.entrySet() ) {
				if ( entry.getValue().equals( Boolean.FALSE ) ) {
					ps.println( entry.getKey() );
				}
			}
			ps.println();
		}
	}
	
	public Set<SootMethod> getAllReachableMethods()
	{
		return func2int.keySet();
	}
	
	public CgEdge getCallEgesOutFrom( int fid )
	{
		return call_graph[fid];
	}
	
	public LinkedList<CgEdge> getCallEdgesInto( int fid )
	{
		return rev_call_graph.get(fid);
	}
	
	public int getMappedMethodID( Node node )
	{
		SootMethod sm = null;
		int ret = SUPER_MAIN;
		
		if ( node instanceof AllocNode ) {
			sm = ((AllocNode)node).getMethod();
		}
		else if ( node instanceof LocalVarNode ) {
			sm = ((LocalVarNode)node).getMethod();
		}
		else if ( node instanceof AllocDotField ) {
			sm = ((AllocDotField)node).getBase().getMethod();
		}
		
		if ( sm != null ) {
			ret = func2int.get( sm );
			if ( vis_cg[ret] == 0 )
				ret = UNKNOWN_FUNCTION;
		}
		
		return ret;
	}
	
	public IVarAbstraction getInternalNode( Node v )
	{
		IVarAbstraction ret = consG.get(v);
		if ( ret == null ) {
			ret = nodeGenerator.generateNode(v);
			consG.put(v, ret);
		}
		return ret;
	}
	
	public boolean castNeverFails( Type src, Type dst )
	{
		return typeManager.castNeverFails(src, dst);
	}
	
	public int getNumberOfPointers()
	{
		return pointers.size();
	}
	
	public int getNumberOfObjects()
	{
		return allocations.size();
	}
	
	public int getNumberOfFunctions()
	{
		return n_func;
	}
	
	public IWorklist getWorklist()
	{
		return worklist;
	}
	
	public IVarAbstraction findAndInsertInstanceField(AllocNode obj, SparkField field) 
	{
		AllocDotField af = findAllocDotField(obj, field);
		assert af != null;
		return consG.get(af);
	}
	
	public CgEdge getInternalEdgeFromSootEdge( Edge e )
	{
		return edgeMapping.get(e);
	}
	
	// -------------------------------Soot Standard Points-to Query Interface----------------------------------
	
	private PointsToSetInternal field_p2set( PointsToSet s, final SparkField f )
	{
		PointsToSetInternal bases = (PointsToSetInternal) s;
		final PointsToSetInternal ret = getSetFactory().newSet(f.getType(), this);
		
		bases.forall(new P2SetVisitor() {
			public final void visit(Node n) {
				Node nDotF = ((AllocNode) n).dot(f);
				if (nDotF != null) {
					PointsToSetInternal temp = nDotF.getP2Set();
					ret.addAll(temp, null);
				}
			}
		});
		
		return ret;
	}
	
	@Override
	public PointsToSet reachingObjects(Local l) 
	{
		if ( !hasExecuted ) return super.reachingObjects(l);
		
		LocalVarNode vn = findLocalVarNode(l);
		IVarAbstraction pn = consG.get(vn);			// We directly access the map consG
		
		if ( vn == null ||
				pn == null ) return EmptyPointsToSet.v();
		
		if ( hasTransformed ||
				vn.getP2Set() != EmptyPointsToSet.v() ) return vn.getP2Set();
		
		// We transform and cache the result for the next query
		pn = pn.getRepresentative();
		PointsToSetInternal ptSet = vn.makeP2Set();
		for ( AllocNode obj : pn.getRepresentative().get_all_points_to_objects() ) {
			ptSet.add( obj );
		}
		
		return ptSet;
	}

	/*
	 * Currently, we only accept one call unit context (1CFA).
	 * (non-Javadoc)
	 * @see soot.jimple.spark.pag.PAG#reachingObjects(soot.Context, soot.Local)
	 */
	@Override
	public PointsToSet reachingObjects(Context c, Local l) 
	{
		if ( !hasExecuted ) return super.reachingObjects(c, l);
		
		if ( hasTransformed ||
				!(c instanceof Unit) )
			return G.v().soot_jimple_toolkits_pointer_FullObjectSet();
		
		LocalVarNode vn = findLocalVarNode(l);
		
		// We first lookup the cache
		ContextVarNode cvn = vn.context(c);
		if ( cvn != null ) return cvn.getP2Set();
		cvn = makeContextVarNode(vn, c);			// The points-to vector is set to empty at start
		
		// Otherwise we create a new points-to vector
		IVarAbstraction pn = consG.get(vn);
		pn = pn.getRepresentative();
		if ( pn == null ) {
			// The enclosing method of this pointer is obsoleted
			return EmptyPointsToSet.v();
		}
		
		SootMethod callee = vn.getMethod();
		Edge e = Scene.v().getCallGraph().findEdge((Unit)c, callee);
		if ( e == null ) {
			// This edge may be obsoleted
			return EmptyPointsToSet.v();
		}
		
		CgEdge myEdge = edgeMapping.get(e);
		long low = myEdge.map_offset;
		long high = low + max_context_size_block[myEdge.s];
		PointsToSetInternal ptset = cvn.makeP2Set();
		
		for ( AllocNode an : pn.get_all_points_to_objects() ) {
			if ( pn.pointer_interval_points_to(low, high, an) )
				ptset.add(an);
		}
		
		return ptset;
	}

	@Override
	public PointsToSet reachingObjects(SootField f) 
	{
		if ( !hasExecuted ) return super.reachingObjects(f);
		
		if( !f.isStatic() )
            throw new RuntimeException( "The parameter f must be a *static* field." );
		
        VarNode vn = findGlobalVarNode( f );
        IVarAbstraction pn = consG.get(f);
        
        if( vn == null || pn == null )
            return EmptyPointsToSet.v();
        
        if ( hasTransformed ||
        	vn.getP2Set() != EmptyPointsToSet.v() ) return vn.getP2Set();	
        	
        // We transform and cache the result for the next query
		pn = pn.getRepresentative();
		PointsToSetInternal ptSet = vn.makeP2Set();
		for ( AllocNode obj : pn.getRepresentative().get_all_points_to_objects() ) {
			ptSet.add( obj );
		}
		
		return ptSet;
	}

	@Override
	public PointsToSet reachingObjects(PointsToSet s, final SootField f) {
		if ( !hasExecuted ) return super.reachingObjects(s, f);
		return field_p2set(s, f);
	}

	@Override
	public PointsToSet reachingObjects(Local l, SootField f) {
		if ( !hasExecuted ) return super.reachingObjects(l, f);
		return reachingObjects( reachingObjects(l), f );
	}

	@Override
	public PointsToSet reachingObjects(Context c, Local l, SootField f) 
	{
		if ( !hasExecuted ) return super.reachingObjects(c, l, f);
		return reachingObjects( reachingObjects(c, l), f );
	}

	@Override
	public PointsToSet reachingObjectsOfArrayElement(PointsToSet s) {
		if ( !hasExecuted ) return super.reachingObjectsOfArrayElement(s);
		return field_p2set( s, ArrayElement.v() );
	}
	
	// An extra query interfaces not provided by SPARK
	public PointsToSet reachingObjects(AllocNode an, SootField f)
	{
		AllocDotField adf = an.dot(f);
		IVarAbstraction pn = consG.get(adf);
		
		if ( adf == null || pn == null )
			return EmptyPointsToSet.v();
		
		if ( hasTransformed ||
	        	adf.getP2Set() != EmptyPointsToSet.v() ) return adf.getP2Set();	
	        	
	        // We transform and cache the result for the next query
			pn = pn.getRepresentative();
			PointsToSetInternal ptSet = adf.makeP2Set();
			for ( AllocNode obj : pn.getRepresentative().get_all_points_to_objects() ) {
				ptSet.add( obj );
		}
			
		return ptSet;
	}
}
