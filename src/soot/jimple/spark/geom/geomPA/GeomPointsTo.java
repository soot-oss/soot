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
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.spark.geom.geomE.FullSensitiveNodeGenerator;
import soot.jimple.spark.geom.heapinsE.HeapInsNodeGenerator;
import soot.jimple.spark.geom.ptinsE.PtInsNodeGenerator;
import soot.jimple.spark.geom.geomPA.CgEdge;
import soot.jimple.spark.geom.geomPA.FIFO_Worklist;
import soot.jimple.spark.geom.geomPA.GeomEvaluator;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.IEncodingBroker;
import soot.jimple.spark.geom.geomPA.IVarAbstraction;
import soot.jimple.spark.geom.geomPA.IWorklist;
import soot.jimple.spark.geom.geomPA.OfflineProcessor;
import soot.jimple.spark.geom.geomPA.PQ_Worklist;
import soot.jimple.spark.geom.geomPA.PlainConstraint;
import soot.jimple.spark.geom.geomPA.ZArrayNumberer;
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
 * @author xiao
 *
 */
public class GeomPointsTo extends PAG 
{
	// Available encodings
	public static final String[] encodingNames = {"Geom", "HeapIns", "PtIns"};
	
	// The constants for the constraints type identification
	public static final int NEW_CONS = 0;
	public static final int ASSIGN_CONS = 1;
	public static final int LOAD_CONS = 2;
	public static final int STORE_CONS = 3;
	public static final int FIELD_ADDRESS = 4;
	
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
	public int n_reach_methods, n_reach_user_methods, n_reach_spark_user_methods;
	
	// Output options
	public String dump_file_name = null;
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
	
	/**
	 * Using the user specified arguments to parametrize our geometric points-to engine.
	 * @param spark_run_time
	 */
	public void parametrize( double spark_run_time )
	{
		// We first setup the encoding methodology
		int solver_encoding = opts.geom_encoding();
    	
    	if ( solver_encoding == SparkOptions.geom_encoding_Geom )
    		nodeGenerator = new FullSensitiveNodeGenerator();
    	else if ( solver_encoding == SparkOptions.geom_encoding_HeapIns )
    		nodeGenerator = new HeapInsNodeGenerator();
    	else if ( solver_encoding == SparkOptions.geom_encoding_PtIns )
    		nodeGenerator = new PtInsNodeGenerator();
    	
    	String encoding_name = encodingNames[ nodeGenerator.getEncodingType() - 1 ];
    	
    	if ( nodeGenerator == null )
    		throw new RuntimeException( "The encoding " + encoding_name 
    		                                        + " is unavailable for geometric points-to analysis." );
    	
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
			File file = new File( dump_file_name + "_" + 
										encoding_name + 
										( opts.geom_blocking() == true ? "_blocked" : "_unblocked" ) +
										"_frac" + opts.geom_frac_base() +
										"_runs" + opts.geom_runs() +
										"_log.txt" );
			try {
				ps = new PrintStream(file);
				G.v().out.println( "Detailed analysis info can be found in the file: " + file.toString() );
			} catch (FileNotFoundException e) {
				G.v().out.println( "The dump file: " + file.toString() + " cannot be created." );
				System.exit(-1);
			}
		}
		else
			ps = G.v().out;
    	
    	// Load the method signatures computed by other points-to analysis
    	// With these methods, we can compare the points-to results fairly. 
		String method_verify_file = opts.geom_verify_name();
		if ( method_verify_file != null ) {
			try {
				FileReader fr = new  FileReader( method_verify_file );
				java.util.Scanner fin = new java.util.Scanner(fr);
				validMethods = new HashMap<String, Boolean>();
				
				while ( fin.hasNextLine() ) {
					validMethods.put( fin.nextLine(), Boolean.FALSE );
				}
				
				fin.close();
				fr.close();
				G.v().out.println( "Read in verification file successfully.\n" );
			} catch (FileNotFoundException e) {
				validMethods = null;
			} catch (IOException e) {
				
			}
		}
		
		// The tunable parameters
		max_cons_budget = opts.geom_frac_base();
		max_pts_budget = max_cons_budget * 2;
		cg_refine_times = opts.geom_runs();
		
		// Prepare other stuffparametrize
		consG.clear();
		constraints.clear();
		func2int.clear();
		edgeMapping.clear();
		typeManager = getTypeManager();
		
		// Output the SPARK running information
		double mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		ps.printf("Spark [Time] : %.3fs\n", (double)spark_run_time/1000 );
		ps.printf("Spark [Memory] : %.3fMB\n", mem  / 1024 / 1024 );
		
		ps.println();
		ps.println( encoding_name + " starts working on " + (dump_file_name.isEmpty() ? "untitled" : dump_file_name) + " benchmark." );
	}
	
	/**
	 *	Read in the program facts generated by SPARK.
	 *  We also construct our own call graph and pointer variables.
	 */
	private Set<VarNode> preprocess() 
	{
		int id;
		int s, t;
		Set<VarNode> basePointers = new HashSet<VarNode>();
		
		// Build the call graph
		n_func = Scene.v().getReachableMethods().size() + 1;
		call_graph = new CgEdge[n_func];

		n_calls = 0;
		n_reach_spark_user_methods = 0;
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
			
			if ( !func.isJavaLibraryMethod() )
				++n_reach_spark_user_methods;
			
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
				if ( p.base_var != null )
					basePointers.add(p.base_var);
			}
			
			// We don't modify the treatment to the Thread.start calls
			if ( edge.tgt().getSignature().equals("<java.lang.Thread: void start()>") )
				thread_run_callsites.add(p.sootEdge.srcStmt());
			
			++n_calls;
		}
		
		// We build the wrappers for all the pointers built by SPARK
		for ( Iterator<VarNode> it = getVarNodeNumberer().iterator(); it.hasNext(); ) {
			VarNode vn = it.next();
			IVarAbstraction pn = makeInternalNode(vn);
			pointers.add(pn);
		}
		
		for ( Iterator<AllocDotField> it = getAllocDotFieldNodeNumberer().iterator(); it.hasNext(); ) {
			AllocDotField adf = it.next();
			
			// Some allocdotfield is invalid, we check and remove them
			SparkField field = adf.getField();
			if ( field instanceof SootField ) {
				// This is an instance field of a class
				Type decType = ((SootField) field).getDeclaringClass().getType();
				Type baseType = adf.getBase().getType();
				// baseType must be a sub type of decType
				if ( !castNeverFails(baseType, decType) )
					continue;
			}
			
			IVarAbstraction pn = makeInternalNode(adf);
			pointers.add(pn);
		}
		
		for ( Iterator<AllocNode> it = getAllocNodeNumberer().iterator(); it.hasNext(); ) {
			AllocNode obj = it.next();
			IVarAbstraction pn = makeInternalNode(obj);
			allocations.add(pn);
		}
		
		/*
		 * Recording the initial sizes because later we may remove some stuff from the containers.
		 */
		n_var = pointers.size();
		n_alloc_sites = allocations.size();

		// Now we extract all the constraints from SPARK
		// The address constraints, new obj -> p
		for (Object object : allocSources()) {
			IVarAbstraction obj = makeInternalNode( (AllocNode)object );
			Node[] succs = allocLookup( (AllocNode)object );
			for (Node element0 : succs) {
				PlainConstraint cons = new PlainConstraint();
				IVarAbstraction p = makeInternalNode(element0);
				cons.expr.setPair(obj, p);
				cons.type = NEW_CONS;
				constraints.add( cons );
			}
		}

		// The assign constraints, p -> q
		Pair<Node, Node> intercall = new Pair<Node, Node>();
		for (Object object : simpleSources()) {
			IVarAbstraction p = makeInternalNode( (VarNode) object );
			Node[] succs = simpleLookup( (VarNode)object );
			for (Node element0 : succs) {
				PlainConstraint cons = new PlainConstraint();
				IVarAbstraction q = makeInternalNode( element0 );
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
			IVarAbstraction p = makeInternalNode( frn.getBase() );
			Node[] succs = loadLookup( frn );
			for (Node element0 : succs) {
				PlainConstraint cons = new PlainConstraint();
				IVarAbstraction q = makeInternalNode( element0 );
				cons.f = frn.getField();
				cons.expr.setPair( p, q );
				cons.type = LOAD_CONS;
				constraints.add( cons );
			}
		}

		// The store constraints, p -> q.f
		for (Object object : storeSources()) {
			IVarAbstraction p = makeInternalNode( (VarNode)object );
			Node[] succs = storeLookup( (VarNode)object );
			for (Node element0 : succs) {
				PlainConstraint cons = new PlainConstraint();
				FieldRefNode frn = (FieldRefNode)element0;
				IVarAbstraction q = makeInternalNode( frn.getBase() );
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
		
		return basePointers;
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
		Random rGen = new Random();
		
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

			// Do simple statistics
			++n_reachable;
			if ( rep_cg[i] == i ) ++n_scc_reachable;
			
			// Un-comment the following code to see which functions are contained in the maximum SCC
//			if ( rep_cg[i] == max_scc_id ) {
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
						// We have to merge some contexts in order to make the analysis sound!
						// The merging starting context is randomly picked
						long start = rGen.nextLong();
						if ( start < 0 ) start = -start;
						if ( start > MAX_CONTEXTS - max_context_size_block[i] ) {
							start = MAX_CONTEXTS - max_context_size_block[i];
							max_context_size_block[j] = MAX_CONTEXTS;
						}
						else {
							if ( max_context_size_block[j] < start + max_context_size_block[i] )
								max_context_size_block[j] = start + max_context_size_block[i];
						}
						p.map_offset = start + 1;
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
		
		// Now we apply the blocking scheme if necessary
		// The implementation is slightly different from our paper (the non-SCC edges are not moved, they still use their current context mappings)
		if ( getOpts().geom_blocking() ) {
			// We scan all the edges again, and tune the SCC related call edges
			// We don't manipulate the non-SCC edges, because they don't induce problems
			for ( i = 0; i < n_func; ++i ) {
				if ( vis_cg[i] == 0 ) continue;
				
				p = call_graph[i];
				while ( p != null ) {
					j = p.t;
					if ( j != i 		// This is not a self-loop, and a self-loop is treated specially in the initial encoding phase
							&& p.scc_edge == true ) {
						// max_context_size_block[i] == max_context_size_block[j]
						// So, we don't distinguish them
						if ( context_size[j] <= MAX_CONTEXTS - max_context_size_block[i] ) {
							p.map_offset = context_size[j] + 1;
							context_size[j] += max_context_size_block[i];
							++block_num[j];
						}
						else {
							// We randomly pick a block for reuse (try best to avoid reusing the first block)
							int iBlock = 0;
							if ( block_num[j] > 1 )
								iBlock = rGen.nextInt(block_num[j]) + 1;
							p.map_offset = iBlock * max_context_size_block[j] + 1;
							
//							ps.println("~~~~~~~~~~~~Context Range For " + j + " is full!!!");
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
		while (worklist.has_job()) {
			IVarAbstraction pn = worklist.next();
//			ps.printf( " pointer %d has %d new points-to tuple\n", pn.id, pn.count_new_pts_intervals() );
//			debug_context_sensitive_objects(pn);
			pn.do_before_propagation();
//			ps.println( "------finish preprocess");
//			if ( pn.id == 716 )
//				System.err.println();
			pn.propagate(this, worklist);
//			ps.println( "------finish propagtion");
			pn.do_after_propagation();
//			ps.println( "------finish postprocess");
		}
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
				else if ( p.base_var != null 
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
							if ( t == null )
								continue;
							else if ( t instanceof AnySubType )
								t = ((AnySubType)t).getBase();
							else if ( t instanceof ArrayType )
								t = RefType.v( "java.lang.Object" );
							
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
//							if ( !p.sootEdge.src().isJavaLibraryMethod() )
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

		ps.printf( "Totally %d virtual edges, we find %d of them are obsoleted.\n", all_virtual_edges, n_obsoleted );
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
	
	/**
	 * Update the call graph and eliminate the pointers and objects appeared in the unreachable code.
	 */
	private void postProcess()
	{
		// Compute the set of reachable functions after the points-to analysis
		for ( int i = 0; i < n_func; ++i ) vis_cg[i] = 0;
		n_reach_user_methods = 0;
		n_reach_methods = countReachableMethods(SUPER_MAIN) - 1;
		
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
 		for ( Iterator<IVarAbstraction> it = allocations.iterator(); it.hasNext(); ) {
 			IVarAbstraction po = it.next();
			AllocNode obj = (AllocNode)po.getWrappedNode();
			SootMethod sm = obj.getMethod();
			if ( sm != null &&
					func2int.containsKey(sm) == false )
				it.remove();
		}
		
		for ( Iterator<IVarAbstraction> it = pointers.iterator(); it.hasNext(); ) {
			IVarAbstraction pn = it.next();
			
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
					it.remove();
					continue;
				}
			}
			
			if ( pn.getRepresentative() != pn )
				continue;
			
			// Otherwise, we remove the useless shapes or objects
			Set<AllocNode> objSet = pn.get_all_points_to_objects();
			for ( Iterator<AllocNode> oit = objSet.iterator(); oit.hasNext(); ) {
				AllocNode obj = oit.next();
				IVarAbstraction po = consG.get(obj);
				if ( po.getNumber() == -1 || pn.isDeadObject(obj) )
					oit.remove();
			}
			
			pn.drop_duplicates();
		}
		
		// Finally, we reassign the ids to the pointers and objects
		pointers.reassign();
		allocations.reassign();
		
		// Release the useless resource
		offlineProcessor.destroy();
		for ( IVarAbstraction pn : pointers )
			pn.discard();
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
	
	/**
	 * The starting point of the geometric points-to analysis engine.
	 */
	public void solve() 
	{
		long solve_time = 0, prepare_time = 0;
		long mem;
		int rounds;
		
		// Flush all accumulated outputs
		G.v().out.flush();
		System.gc(); System.gc(); System.gc(); System.gc(); System.gc();
		
		// Start our constraints solving phase
		Date begin = new Date();
		
		// Collect the basic information from SPARK
		Set<VarNode> basePointers = preprocess();
		worklist.initialize(n_var);
		offlineProcessor = new OfflineProcessor(n_var, this);
		
		for ( rounds = 0; rounds < cg_refine_times; ++rounds ) {

			ps.println("\n" + "Round " + rounds + " : ");
			
			// Encode the contexts
			encodeContexts();
			
			// Offline process: 
			// substantially use the points-to result for redundancy elimination prior to the analysis
			Date prepare_begin = new Date();
				offlineProcessor.runOptimizations( rounds == 0, basePointers );
			Date prepare_end = new Date();
			prepare_time += prepare_end.getTime() - prepare_begin.getTime();	

			prepareNextRun();
			
			// We construct the initial flow graph
			nodeGenerator.initFlowGraph(this);

			// Solve the constraints
			solveConstraints();
			
			// We update the call graph when the new points-to information is ready
			// The call graph update time is not included in the points-to analysis
			Date update_cg_begin = new Date();
				int n_obs = updateCallGraph();
			Date update_cg_end = new Date();
			solve_time -= update_cg_end.getTime() - update_cg_begin.getTime();
			if ( n_obs == 0 ) break;
		}

		if ( rounds < cg_refine_times )
			ps.printf( "\nSorry, it's not necessary to iterate more times. We stop here.\n" );
		
		Date end = new Date();
		solve_time += end.getTime() - begin.getTime();
		mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		
		ps.println();
		ps.printf("Preprocess [Time] : %.3fs \n", (double) prepare_time / 1000);
		ps.printf("Geometric [Time] : %.3fs \n", (double) solve_time / 1000 );
		ps.printf("Geometric [Memory] : %.3fMB \n", (double) (mem) / 1024 / 1024 );
		
		// Prepare for use in various of clients
		postProcess();
		
		// Do we need to obtain the context insensitive points-to result?
		if ( opts.geom_trans() ) {
			transformToCIResult();
			hasTransformed = true;
		}
		else {
			// Otherwise, we inject the SPARK points-to result into our unprocessed pointers
			for ( IVarAbstraction pn : pointers ) {
				if ( !offlineProcessor.isUsefulVar(pn) )
					pn.injectPts();
			}
		}
		
		// Do we perform some precision tests?
		int evalLevel = opts.geom_eval();
		if ( evalLevel > 0 ) {
			GeomEvaluator ge = new GeomEvaluator(this, ps);
			ge.reportBasicMetrics();
			
			if ( evalLevel == 2 ) {
				ge.checkCallGraph();
				ge.checkCastsSafety();
				ge.checkAliasAnalysis();
			}
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
	
	/**
	 * Get the index of the enclosing function of the specified node.
	 */
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
	
	/**
	 * Transform the SPARK node representation to our representation.
	 * @param v
	 * @return
	 */
	public IVarAbstraction makeInternalNode( Node v )
	{
		IVarAbstraction ret = consG.get(v);
		if ( ret == null ) {
			ret = nodeGenerator.generateNode(v);
			consG.put(v, ret);
		}
		return ret;
	}
	
	/**
	 * Find our representation for the SPARK node.
	 * We don't create a new node if nothing found.
	 * @param v
	 * @return
	 */
	public IVarAbstraction findInternalNode( Node v )
	{
		return consG.get(v);
	}
	
	/**
	 * Type compatibility test.
	 * @param src
	 * @param dst
	 * @return
	 */
	public boolean castNeverFails( Type src, Type dst )
	{
		return typeManager.castNeverFails(src, dst);
	}
	
	/**
	 * Get the number of valid pointers current in the container.
	 * @return
	 */
	public int getNumberOfPointers()
	{
		return pointers.size();
	}
	
	public int getNumberOfSparkPointers()
	{
		return n_var;
	}
	
	/**
	 * Get the number of valid objects current in the container.
	 * @return
	 */
	public int getNumberOfObjects()
	{
		return allocations.size();
	}
	
	public int getNumberOfSparkObjects()
	{
		return n_alloc_sites;
	}
	
	/**
	 * Return the number of functions that are reachable by SPARK.
	 */
	public int getNumberOfFunctions()
	{
		return n_func;
	}
	
	/**
	 * Return the number of functions that are reachable after the geometric points-to analysis.
	 */
	public int getNumberOfReachableFunctions()
	{
		return n_reach_methods;
	}
	
	public IWorklist getWorklist()
	{
		return worklist;
	}
	
	public IVarAbstraction findAndInsertInstanceField(AllocNode obj, SparkField field) 
	{
		AllocDotField af = findAllocDotField(obj, field);
		if ( af == null ) {
			// We create a new instance field node with restrict type compatible check
			Type decType = ((SootField) field).getDeclaringClass().getType();
			Type baseType = obj.getType();
			// baseType must be a sub type of decType
			if ( !typeManager.castNeverFails(baseType, decType) ) return null;
			
			af = makeAllocDotField(obj, field);
			IVarAbstraction pn = makeInternalNode(af);
			pointers.add(pn);
			return pn;
		}
		return consG.get(af);
	}
	
	public CgEdge getInternalEdgeFromSootEdge( Edge e )
	{
		return edgeMapping.get(e);
	}
	
	public boolean isLegalPointer( IVarAbstraction pn )
	{
		Node v = pn.getWrappedNode();
		SootMethod sm = null;
		int method = 0;
		
		// We do not count the exception handler pointers
		if ( v.getType() instanceof RefType ) {
			SootClass sc = ((RefType)v.getType()).getSootClass();
			if ( !sc.isInterface() && Scene.v().getActiveHierarchy().isClassSubclassOfIncluding(
					sc, GeomPointsTo.exeception_type.getSootClass()) ) {
				return false;
			}
		}
		
		method = getMappedMethodID(v);
		sm = getSootMethodFromID(method);
		
		// Global variable?
		if ( method == GeomPointsTo.SUPER_MAIN )
			return true;
		
		// Is the enclosing method obsoleted?
		if ( method == GeomPointsTo.UNKNOWN_FUNCTION )
			return false;
		
		// Is this a valid method in the verification list?
		if ( !isValidMethod(sm) )
			return false;
		
		// A useful variable that is processed in the geometric analysis?
		if ( !offlineProcessor.isUsefulVar(v) )
			return false;
		
		return !sm.isJavaLibraryMethod();
	}
	
	/**
	 * Given a valid SPARK node, we test if it is still valid after the geometric analysis.
	 */
	public boolean isValidGeometricNode( Node sparkNode )
	{
		IVarAbstraction pNode = consG.get(sparkNode);
		return pNode.getNumber() != -1;
	}
	
	public boolean hasGeomExecuted()
	{
		return hasExecuted;
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
		for ( AllocNode obj : pn.get_all_points_to_objects() ) {
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
