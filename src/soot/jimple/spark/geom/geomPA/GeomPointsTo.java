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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

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
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.spark.geom.dataRep.CgEdge;
import soot.jimple.spark.geom.dataRep.PlainConstraint;
import soot.jimple.spark.geom.geomE.FullSensitiveNodeGenerator;
import soot.jimple.spark.geom.heapinsE.HeapInsNodeGenerator;
import soot.jimple.spark.geom.helper.GeomEvaluator;
import soot.jimple.spark.geom.ptinsE.PtInsNodeGenerator;
import soot.jimple.spark.geom.utils.SootInfo;
import soot.jimple.spark.geom.utils.ZArrayNumberer;
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
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.VirtualCalls;
import soot.options.SparkOptions;
import soot.toolkits.scalar.Pair;
import soot.util.NumberedString;
import soot.util.queue.ChunkedQueue;
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
	public Map<Node, IVarAbstraction> consG = null;
	
	// Stores all the pointers including the instance fields
	public ZArrayNumberer<IVarAbstraction> pointers = null;
	
	// Stores all the symbolic objects
	public ZArrayNumberer<IVarAbstraction> allocations = null;
	
	// Store all the constraints, initially generated from SPARK
	public ZArrayNumberer<PlainConstraint> constraints = null;
	
	// All the callsites that spawn a new thread
	public Set<Stmt> thread_run_callsites = null;
	
	// The virtual callsites (and base pointers) that have multiple call targets
	public Set<Stmt> multiCallsites = null;
	
	/*
	 * Context size records the total number of instances for a function.
	 * max_context_size_block is the context size of the largest block for a function in cycle  
	 */
	public long context_size[], max_context_size_block[];
	
	// Number of context blocks for a function
	public int block_num[];
	
	// Analysis statistics
	public int max_scc_size, max_scc_id;
	public int n_func, n_calls;
	public int n_reach_methods, n_reach_user_methods, n_reach_spark_user_methods;
	public int n_init_constraints;
	
	// Output options
	public String dump_dir = null;
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
	// Only keep the obsoleted call edges decided in the last round
	protected Vector<CgEdge> obsoletedEdges = null;
	protected Map<Integer, LinkedList<CgEdge>> rev_call_graph = null;
	protected Deque<Integer> queue_cg = null;
	
	// Containers used for call graph traversal
	protected int vis_cg[], low_cg[], rep_cg[], indeg_cg[], scc_size[];
	protected int pre_cnt;			// preorder time-stamp for constructing the SCC condensed call graph
	
	// The mappings between Soot functions and call edges to our internal representations
	protected Map<SootMethod, Integer> func2int = null;
	protected Map<Integer, SootMethod> int2func = null;
	protected Map<Edge, CgEdge> edgeMapping = null;
	
	// Others
	private boolean hasTransformed = false;
	// Because we override the points-to query interface for SPARK, we need this flag to know how to answer queries
	private boolean hasExecuted = false;		
	// Prepare necessary structures when first time ddSolve is called
	private boolean ddPrepared = false;
	
	// -------------------Constructors--------------------
	public GeomPointsTo( final SparkOptions opts ) {
		super(opts);
	}
	
	public String toString()
	{
		return "Geometric Points-To Analysis";
	}
	
	/**
	 * Data structures that only specific to geometric solver are created here.
	 * The initialized container sizes are empirically chosen from the primes.
	 * We believe most of the machine today can afford the memory overhead.
	 */
	private void prepareContainers()
	{
		// All kinds of variables
		consG = new HashMap<Node, IVarAbstraction>(39341);
		
		// Only the pointer variables
		pointers = new ZArrayNumberer<IVarAbstraction>(25771);
		
		// Only the heap variables
		allocations = new ZArrayNumberer<IVarAbstraction>();
		
		// The constraints extracted from code
		constraints = new ZArrayNumberer<PlainConstraint>(25771);
		
		// The statements that fork a new thread
		thread_run_callsites = new HashSet<Stmt>(251);
		
		// The virtual callsites that have multiple call targets
		multiCallsites = new HashSet<Stmt>(251);
		
		// The fake virtual call edges created by SPARK
//		obsoletedEdges = new Vector<CgEdge>(4021);
		
		// A linkedlist used for traversing the call graph
		queue_cg = new LinkedList<Integer>();
		
		// Containers for functions and call graph edges
		func2int = new HashMap<SootMethod, Integer>(5011);
		int2func = new HashMap<Integer, SootMethod>(5011);
		edgeMapping = new HashMap<Edge, CgEdge>(19763);
		
		consG.clear();
		constraints.clear();
		func2int.clear();
		edgeMapping.clear();
	}
	
	/**
	 * Using the user specified arguments to parameterize the geometric points-to solver.
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
    	
    	String encoding_name = nodeGenerator.getSignature();
    	
    	if ( encoding_name == null )
    		throw new RuntimeException( "No encoding given for geometric points-to analysis." );

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
    	dump_dir = opts.geom_dump_verbose();
    	File dir = null;
    	if ( !dump_dir.isEmpty() ) {
    		// We create a new folder and put all the dump files in that folder
    		dir = new File( dump_dir );
    		if ( !dir.exists() ) dir.mkdirs();
    		
    		// We create the log file
			File log_file = new File( dump_dir,
										encoding_name + 
										( opts.geom_blocking() == true ? "_blocked" : "_unblocked" ) +
										"_frac" + opts.geom_frac_base() +
										"_runs" + opts.geom_runs() +
										"_log.txt" );
			try {
				ps = new PrintStream(log_file);
				G.v().out.println( "[Geom] Analysis log can be found in: " + log_file.toString() );
			} catch (FileNotFoundException e) {
				String msg = "[Geom] The dump file: " + log_file.toString() + " cannot be created. Abort.";
				G.v().out.println( msg );
				throw new RuntimeException(msg, e);
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
				G.v().out.println( "[Geom] Read in verification file successfully.\n" );
			} catch (FileNotFoundException e) {
				validMethods = null;
			} catch (IOException e) {
				
			}
		}
		
		// Set which pointers will be processed
		Parameters.seedPts = opts.geom_app_only() ? 
				Constants.seedPts_allUser : Constants.seedPts_all;
		
		// Output the SPARK running information
		double mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		ps.println();
		ps.printf("[Spark] Time: %.3f s\n", (double)spark_run_time/1000 );
		ps.printf("[Spark] Memory: %.1f MB\n", mem  / 1024 / 1024 );
				
		// Get type manager from SPARK
		typeManager = getTypeManager();
		
		// The tunable parameters
		Parameters.max_cons_budget = opts.geom_frac_base();
		Parameters.max_pts_budget = Parameters.max_cons_budget * 2;
		Parameters.cg_refine_times = opts.geom_runs();
		if ( Parameters.cg_refine_times < 1 ) Parameters.cg_refine_times = 1;
		
		// Prepare for the containers
		prepareContainers();
		
		// Now we start working
		ps.println( "[Geom]" + " Start working on <" + 
							(dir == null ? "NoName" : dir.getName()) + "> with <" + encoding_name + "> encoding." );
	}
	
	/**
	 *	Read in the program facts generated by SPARK.
	 *  We also construct our own call graph and pointer variables.
	 */
	private void preprocess() 
	{
		int id;
		int s, t;
		
		// Build the call graph
		n_func = Scene.v().getReachableMethods().size() + 1;
		call_graph = new CgEdge[n_func];

		n_calls = 0;
		n_reach_spark_user_methods = 0;
		id = 1;
		QueueReader<MethodOrMethodContext> smList = Scene.v().getReachableMethods().listener();
		CallGraph soot_callgraph = Scene.v().getCallGraph();

		while (smList.hasNext()) {
			final SootMethod func = (SootMethod) smList.next();
			func2int.put(func, id);
			int2func.put(id, func);
			
			/*
			 * We cannot identify all entry methods since some entry methods call themselves.
			 * In that case, the Soot CallGraph.isEntryMethod() function returns false.
			 */
			if ( soot_callgraph.isEntryMethod(func) ||
					func.isEntryMethod() ) {
				CgEdge p = new CgEdge(Constants.SUPER_MAIN, id, null, call_graph[Constants.SUPER_MAIN]);
				call_graph[Constants.SUPER_MAIN] = p;
				n_calls++;
			}
			
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
			
			// We collect callsite information
			Stmt callsite = edge.srcStmt();
			
			if ( edge.isThreadRunCall() ||
					edge.kind().isExecutor() ||
					edge.kind().isAsyncTask() ) {
				// We don't modify the treatment to the thread run() calls
				thread_run_callsites.add(callsite);
			}
			else if ( edge.isInstance() && !edge.isSpecial() ) {
				// We try to refine the virtual callsites (virtual + interface) with multiple call targets				
				InstanceInvokeExpr expr = (InstanceInvokeExpr)callsite.getInvokeExpr();
				p.base_var = findLocalVarNode( expr.getBase() );
				if ( SootInfo.countCallEdgesForCallsite(callsite, true) > 1 &&
						p.base_var != null ) {
					multiCallsites.add(callsite);
				}
			}
			
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

		// Now we extract all the constraints from SPARK
		// The address constraints, new obj -> p
		for (Object object : allocSources()) {
			IVarAbstraction obj = makeInternalNode( (AllocNode)object );
			Node[] succs = allocLookup( (AllocNode)object );
			for (Node element0 : succs) {
				PlainConstraint cons = new PlainConstraint();
				IVarAbstraction p = makeInternalNode(element0);
				cons.expr.setPair(obj, p);
				cons.type = Constants.NEW_CONS;
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
				cons.type = Constants.ASSIGN_CONS;
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
				cons.type = Constants.LOAD_CONS;
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
				cons.type = Constants.STORE_CONS;
				constraints.add( cons );
			}
		}
		
		n_init_constraints = constraints.size();
		
		// Initialize other stuff
		low_cg = new int[n_func];
		vis_cg = new int[n_func];
		rep_cg = new int[n_func];
		indeg_cg = new int[n_func];
		scc_size = new int[n_func];
		block_num = new int[n_func];
		context_size = new long[n_func];
		max_context_size_block = new long[n_func];
	}
	
	/**
	 * As pointed out by the single entry graph contraction, temporary variables incur high redundancy in points-to relations.
	 * Find and eliminate the redundancies as early as possible.
	 * 
	 * Methodology:
	 * If q has unique incoming edge p -> q, p and q are both local to the same function, and they have the same type, we merge them.
	 */
	private void mergeLocalVariables()
	{
		IVarAbstraction my_lhs, my_rhs;
		Node lhs, rhs;
		
		int[] count = new int[pointers.size()];
		
		// We count how many ways a local pointer can be assigned
		for ( PlainConstraint cons : constraints ) {
			my_lhs = cons.getLHS();
			my_rhs = cons.getRHS();
			
			switch (cons.type) {
			case Constants.NEW_CONS:
			case Constants.ASSIGN_CONS:
				count[ my_rhs.id ]++;
				break;
				
			case Constants.LOAD_CONS:
				lhs = my_lhs.getWrappedNode();
				count[ my_rhs.id ] += lhs.getP2Set().size();
				break;
			}
		}
		
		// Second time scan, we delete those constraints that only duplicate points-to information
		for ( Iterator<PlainConstraint> cons_it = constraints.iterator(); cons_it.hasNext(); ) {
			PlainConstraint cons = cons_it.next();
			
			if ( cons.type == Constants.ASSIGN_CONS ) {
				my_lhs = cons.getLHS();
				my_rhs = cons.getRHS();
				lhs = my_lhs.getWrappedNode();
				rhs = my_rhs.getWrappedNode();
				
				if ( (lhs instanceof LocalVarNode) &&
						(rhs instanceof LocalVarNode) ) {
					SootMethod sm1 = ((LocalVarNode)lhs).getMethod();
					SootMethod sm2 = ((LocalVarNode)rhs).getMethod();
					
					if ( sm1 == sm2 && 
							count[my_rhs.id] == 1 
							&& lhs.getType() == rhs.getType() ) {
						
						// They are local to the same function and the receiver pointer has unique incoming edge
						// More importantly, they have the same type.
						my_rhs.merge(my_lhs);
						cons_it.remove();
					}
				}
			}
		}
		
		// Third scan, update the constraints with the representatives
		for ( PlainConstraint cons : constraints ) {
			my_lhs = cons.getLHS();
			my_rhs = cons.getRHS();
			
			switch ( cons.type ) {
			case Constants.NEW_CONS:
				cons.setRHS(my_rhs.getRepresentative());
				break;
				
			case Constants.ASSIGN_CONS:
			case Constants.LOAD_CONS:
			case Constants.STORE_CONS:
				cons.setLHS(my_lhs.getRepresentative());
				cons.setRHS(my_rhs.getRepresentative());
				break;
			}
		}
	}
	
	/**
	 * Using Tarjan's algorithm to contract the SCCs.
	 */
	private void callGraphDFS(int s) 
	{
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
	 *  Also permit clients to decide whether to connect the disjoint parts in the call graph or not. 
	 */
	private void encodeContexts(boolean connectMissedEntries)
	{
		int i, j;
		int n_reachable = 0, n_scc_reachable = 0;
		int n_full = 0;
		long max_contexts = Long.MIN_VALUE;
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
		callGraphDFS(Constants.SUPER_MAIN);
		
		if (connectMissedEntries) {
			// We also scan rest of the functions
			for (i = Constants.SUPER_MAIN + 1; i < n_func; ++i)
				if (vis_cg[i] == 0)
					callGraphDFS(i);
		}

		// Then, we topologically number the contexts starting from the SUPER_MAIN function
		// We count the in-degree of each function.
		// And, we classify the call edges into SCC/non-SCC edges
		for (i = 0; i < n_func; ++i) {
			if (vis_cg[i] == 0)
				continue;

			CgEdge p = call_graph[i];
			while (p != null) {
				// Only count an edge that links two functions in the same SCC
				if (rep_cg[i] == rep_cg[p.t]) {
					p.scc_edge = true;
				} else {
					p.scc_edge = false;
					++indeg_cg[rep_cg[p.t]];
				}
				
				p = p.next;
			}

			// Do simple statistics
			++n_reachable;
			if ( rep_cg[i] == i ) ++n_scc_reachable;
		}
		
		if (connectMissedEntries) {
			// The functions other than SUPER_MAIN that have zero in-degrees are missed entry methods
			for (i = Constants.SUPER_MAIN + 1; i < n_func; ++i) {
				int rep_node = rep_cg[i];
				if (indeg_cg[rep_node] == 0) {
					CgEdge p = new CgEdge(Constants.SUPER_MAIN, i, null, call_graph[Constants.SUPER_MAIN]);
					call_graph[Constants.SUPER_MAIN] = p;
					n_calls++;
				}
			}
		}

		// Next, we condense the SCCs
		// Later, we have to restore the call graph in order to serve the
		// context sensitive queries
		for (i = 0; i < n_func; ++i)
			if (vis_cg[i] != 0 && rep_cg[i] != i) {
				// Any node in a SCC must have at least one outgoing edge
				CgEdge p = call_graph[i];
				while (p.next != null)
					p = p.next;
				p.next = call_graph[rep_cg[i]];
				// Note that, call_graph[i] is not cleared after merging
				call_graph[rep_cg[i]] = call_graph[i];
			}

		// Now, we add all the source nodes to the queue
		max_context_size_block[Constants.SUPER_MAIN] = 1;
		queue_cg.addLast(Constants.SUPER_MAIN);

		while ( !queue_cg.isEmpty() ) {
			i = queue_cg.getFirst();
			queue_cg.removeFirst();
			CgEdge p = call_graph[i];
			
			while (p != null) {
				if (p.scc_edge == false) {
					// Consider the representative only
					j = rep_cg[p.t];

					/*
					 * We can control how many contexts created for a specified
					 * function. And, for any call edge, we can manually move
					 * the mapping interval from caller to callee.
					 */
					if (Constants.MAX_CONTEXTS - max_context_size_block[i] < max_context_size_block[j]) {
						// The are more than 2^63 - 1 execution paths, terrible!
						// We have to merge some contexts in order to make the analysis sound!
						// The merging starting context is randomly picked
						long start = rGen.nextLong();
						if ( start < 0 ) start = -start;
						if ( start > Constants.MAX_CONTEXTS - max_context_size_block[i] ) {
							// We use the last max_context_size_block[i] bits for this mapping
							start = Constants.MAX_CONTEXTS - max_context_size_block[i];
							max_context_size_block[j] = Constants.MAX_CONTEXTS;
						}
						else {
							if ( max_context_size_block[j] < start + max_context_size_block[i] )
								// We compensate the difference
								max_context_size_block[j] = start + max_context_size_block[i];
						}
						
						p.map_offset = start + 1;
					} else {
						// Accumulate the contexts
						p.map_offset = max_context_size_block[j] + 1;
						max_context_size_block[j] += max_context_size_block[i];
					}

					// Add to the worklist
					if (--indeg_cg[j] == 0)
						queue_cg.addLast(j);
				}
				else {
					// 0-CFA modeling for the SCC, the default mode
					p.map_offset = 1;
				}

				p = p.next;
			}
			
			if ( max_context_size_block[i] > max_contexts )
				max_contexts = max_context_size_block[i];
		}
			
		// Now we restore the call graph
		for (i = n_func - 1; i > -1; --i) {
			if ( vis_cg[i] == 0 ) continue;
			if ( rep_cg[i] != i ) {
				// All nodes in the same SCC have the same number of contexts
				max_context_size_block[i] = max_context_size_block[rep_cg[i]];
				
				// Put all the call edges back
				CgEdge p = call_graph[i];
				while (p.next.s == i)
					// p.next.s may not be i because it would be linked to another scc member
					p = p.next;
				
				call_graph[rep_cg[i]] = p.next;
				p.next = null;
			}
			
			if ( max_context_size_block[i] == Constants.MAX_CONTEXTS )
				++n_full;
			context_size[i] = max_context_size_block[i];
			block_num[i] = 1;
		}
		
		// Now we apply the blocking scheme if necessary
		// The implementation is slightly different from our paper (the non-SCC edges are not moved, they still use their current context mappings)
		if ( getOpts().geom_blocking() ) {
			// We scan all the edges again, and tune the SCC related call edges
			// We don't manipulate the non-SCC edges, because they don't induce problems
			for ( i = 0; i < n_func; ++i ) {
				if ( vis_cg[i] == 0 ) continue;
				
				CgEdge p = call_graph[i];
				while ( p != null ) {
					j = p.t;
					if ( j != i 		// This is not a self-loop, and a self-loop is treated specially in the initial encoding phase
							&& p.scc_edge == true ) {
						// max_context_size_block[i] == max_context_size_block[j]
						// So, we don't distinguish them
						if ( context_size[j] <= Constants.MAX_CONTEXTS - max_context_size_block[i] ) {
							p.map_offset = context_size[j] + 1;
							context_size[j] += max_context_size_block[i];
							++block_num[j];
						}
						else {
							// We randomly pick a block for reuse (try best to avoid reusing the first block)
							int iBlock = 0;
							if ( block_num[j] > 1 )
								iBlock = rGen.nextInt(block_num[j]-1) + 1;
							p.map_offset = iBlock * max_context_size_block[j] + 1;
						}
					}
					
					p = p.next;
				}
			}
		}
			
		// Print debug info
		ps.printf("Reachable Methods = %d, in which #Condensed Nodes = %d, #Full Context Nodes = %d \n", 
				n_reachable - 1, n_scc_reachable - 1, n_full );
		ps.printf("Maximum SCC = %d \n", max_scc_size);
		ps.printf("The maximum context size = %e\n", (double)max_contexts );
	}
	
	/**
	 *  We iteratively update the call graph and the constraints list until our
	 *  demand is satisfied
	 */
	private void solveConstraints() 
	{
		IWorklist ptaList = worklist;
		
		while (ptaList.has_job()) {
			IVarAbstraction pn = ptaList.next();
			pn.do_before_propagation();
			pn.propagate(this, ptaList);
			pn.do_after_propagation();
		}
	}
	
	/**
	 * Obtain the set of possible call targets at given @param callsite.
	 */
	private void getCallTargets(IVarAbstraction pn, SootMethod src,
			Stmt callsite, ChunkedQueue<SootMethod> targetsQueue)
	{
		InstanceInvokeExpr iie = (InstanceInvokeExpr)callsite.getInvokeExpr();
		Local receiver = (Local)iie.getBase();
		NumberedString subSig = iie.getMethodRef().getSubSignature();
		
		// We first build the set of possible call targets
		for (AllocNode an : pn.get_all_points_to_objects()) {
			Type type = an.getType();
			if (type == null) continue;

			VirtualCalls.v().resolve(type, 
					receiver.getType(), subSig, src,
					targetsQueue);
		}
	}
	
	/**
	 * Remove unreachable call targets at the virtual callsites using the up-to-date points-to information.
	 */
	private int updateCallGraph() 
	{
		int all_virtual_edges = 0, n_obsoleted = 0;
		
		CallGraph cg = Scene.v().getCallGraph();
		ChunkedQueue<SootMethod> targetsQueue = new ChunkedQueue<SootMethod>();
		QueueReader<SootMethod> targets = targetsQueue.reader();
		Set<SootMethod> resolvedMethods = new HashSet<SootMethod>();
//		obsoletedEdges.clear();
		
		// We first update the virtual callsites
		for ( Iterator<Stmt> csIt = multiCallsites.iterator(); csIt.hasNext(); ) {
			Stmt callsite = csIt.next();
			Iterator<Edge> edges = cg.edgesOutOf(callsite);
			if ( !edges.hasNext() ) {
				csIt.remove();
				continue;
			}
			
			Edge anyEdge = edges.next();
			CgEdge p = edgeMapping.get(anyEdge);
			SootMethod src = anyEdge.src();
			
			if ( !isReachableMethod(src) ) {
				// The source method is no longer reachable
				// We move this callsite
				csIt.remove();
				continue;
			}
			
			if ( !edges.hasNext() ) {
				// We keep this resolved site for call graph profiling
				continue;
			}
			
			IVarAbstraction pn = consG.get(p.base_var);
			if ( pn != null ) {
				pn = pn.getRepresentative();
				
				// We resolve the call targets with the new points-to result
				getCallTargets(pn, src, callsite, targetsQueue);
				resolvedMethods.clear();
				while ( targets.hasNext() ) {
					resolvedMethods.add(targets.next());
				}
				
				// We delete the edges that are proven to be spurious
				while (true) {
					SootMethod tgt = anyEdge.tgt();
					if ( !resolvedMethods.contains(tgt) ) {
						p = edgeMapping.get(anyEdge);
						p.is_obsoleted = true;
					}
					
					if ( !edges.hasNext() ) break;
					anyEdge = edges.next();
				}
			}
		}
		
		// We delete the spurious edges
		for (int i = 1; i < n_func; ++i) {
			// New outgoing edge list is pointed to by q
			CgEdge p = call_graph[i];
			CgEdge q = null;

			while (p != null) {
				
				if ( vis_cg[i] == 0 ) {
					// If this method is unreachable, we delete all its outgoing edges
					p.is_obsoleted = true;
				}
				
				if ( p.base_var != null ) {
					++all_virtual_edges;
				}
				
				CgEdge temp = p.next;
				
				if (p.is_obsoleted == false) {
					p.next = q;
					q = p;
				}
				else {
					// Update the corresponding SOOT call graph
					cg.removeEdge(p.sootEdge);
					
					// We record this obsoleted edge
//					obsoletedEdges.add(p);
					++n_obsoleted;
				}
				
				p = temp;
			}

			call_graph[i] = q;
		}
		
		ps.printf( "%d of %d virtual call edges are proved to be spurious.\n", n_obsoleted, all_virtual_edges );
		return n_obsoleted;
	}
	
	/**
	 * Prepare for the next iteration.
	 */
	private void prepareNextRun() 
	{
		// Clean the context sensitive points-to results for the representative pointers
		for (IVarAbstraction pn : pointers) {
			if ( pn.willUpdate == true ) {
				pn.reconstruct();
			}
		}

		// Reclaim
		System.gc();
	}
	
	/**
	 * Scan the call graph and mark the reachable methods.
	 */
	private void markReachableMethods()
	{
		int ans = 0;
		CgEdge p;
		
		for ( int i = 0; i < n_func; ++i ) vis_cg[i] = 0;
		
		queue_cg.clear();
		queue_cg.add(Constants.SUPER_MAIN);
		vis_cg[Constants.SUPER_MAIN] = 1;
		
		while ( queue_cg.size() > 0 ) {
			int s = queue_cg.removeFirst();
			p = call_graph[s];
			while ( p != null ) {
				int t = p.t;
				if ( vis_cg[t] == 0 ) {
					queue_cg.add(t);
					vis_cg[t] = 1;
					++ans;
				}

				p = p.next;
			}
		}
		
		n_reach_methods = ans;
		
		// Scan again to remove unreachable methods
		ans = 0;
		for (int i = 1; i < n_func; ++i) {
			SootMethod sm = int2func.get(i);
			
			if (vis_cg[i] == 0) {
				func2int.remove(sm);
				int2func.remove(i);
			}
			else {
				if ( !sm.isJavaLibraryMethod() )
					++ans;
			}
		}
		
		n_reach_user_methods = ans;
	}
	
	/**
	 * The reversed call graph might be used by evaluating queries.
	 */
	private void buildRevCallGraph()
	{
		rev_call_graph = new HashMap<Integer, LinkedList<CgEdge>>();
		
		for (int i = 0; i < n_func; ++i) {
			CgEdge p = call_graph[i];
			
			while (p != null) {
				LinkedList<CgEdge> list = rev_call_graph.get(p.t);
				if (list == null) {
					list = new LinkedList<CgEdge>();
					rev_call_graph.put(p.t, list);
				}

				list.add(p);
				p = p.next;
			}
		}
	}
	
	/**
	 * 1. Update the call graph;
	 * 2. Eliminate the pointers, objects, and constraints related to the unreachable code.
	 */
	private void finalizeInternalData()
	{
		// Compute the set of reachable functions after the points-to analysis
		markReachableMethods();
			
		// Clean the unreachable objects
 		for ( Iterator<IVarAbstraction> it = allocations.iterator(); it.hasNext(); ) {
 			IVarAbstraction po = it.next();
			AllocNode obj = (AllocNode)po.getWrappedNode();
			SootMethod sm = obj.getMethod();
			if ( sm != null &&
					func2int.containsKey(sm) == false )
				it.remove();
		}
		
 		// Clean the unreachable pointers
 		final Vector<AllocNode> removeSet = new Vector<AllocNode>();
 		
		for ( Iterator<IVarAbstraction> it = pointers.iterator(); it.hasNext(); ) {
			IVarAbstraction pn = it.next();
			
			// Is this pointer obsoleted?
			Node vn = pn.getWrappedNode();
			SootMethod sm = null;
			
			if ( vn instanceof LocalVarNode ) {
				sm = ((LocalVarNode)vn).getMethod();
			}
			else if ( vn instanceof AllocDotField ) {
				sm = ((AllocDotField)vn).getBase().getMethod();
			}
			
			if ( sm != null ) {
				if ( func2int.containsKey(sm) == false ) {
					pn.deleteAll();
					vn.discardP2Set();
					it.remove();
					continue;
				}
			}
			
			if ( pn.getRepresentative() != pn )
				continue;
			
			removeSet.clear();
			
			if ( pn.hasPTResult() ) {
				// We remove the useless shapes or objects
				Set<AllocNode> objSet = pn.get_all_points_to_objects();
				
				for ( Iterator<AllocNode> oit = objSet.iterator(); oit.hasNext(); ) {
					AllocNode obj = oit.next();
					IVarAbstraction po = consG.get(obj);
					if ( !po.reachable() || pn.isDeadObject(obj) ) {
						removeSet.add(obj);
					}
				}
				
				for ( AllocNode obj : removeSet )
					pn.remove_points_to(obj);
				
				pn.drop_duplicates();
			}
			else {
				// We also remove unreachable objects for SPARK nodes
				PointsToSetInternal pts = vn.getP2Set();
				pts.forall( new P2SetVisitor() {
					@Override
					public void visit(Node n) {
						IVarAbstraction pan = findInternalNode(n);
						// The removeSet is misused as a contains set
						if ( pan.reachable() )
							removeSet.add((AllocNode)n);
					}
				});
				
				pts = vn.makeP2Set();
				for ( AllocNode an : removeSet ) pts.add(an);
			}
		}
		
		// Clean the useless constraints
		for (Iterator<PlainConstraint> cIt = constraints.iterator(); 
				cIt.hasNext();) {
			PlainConstraint cons = cIt.next();

			IVarAbstraction lhs = cons.getLHS();
			IVarAbstraction rhs = cons.getRHS();

			if (!lhs.reachable() || 
					!rhs.reachable() ||
					getMethodIDFromPtr(lhs) == Constants.UNKNOWN_FUNCTION ||
					getMethodIDFromPtr(rhs) == Constants.UNKNOWN_FUNCTION ) {
				cIt.remove();
			}
		}
				
		// We reassign the IDs to the pointers, objects and constraints
		pointers.reassign();
		allocations.reassign();
		constraints.reassign();
	}
	
	/**
	 * Stuff that is useless for querying is released.
	 */
	private void releaseUselessResources()
	{
		offlineProcessor.destroy();
		offlineProcessor = null;
		IFigureManager.cleanCache();
		System.gc();
	}
	
	/**
	 * Update the reachable methods and SPARK points-to results.
	 */
	private void finalizeSootData()
	{
		// We remove the unreachable functions from Soot internal structures
		Scene.v().releaseReachableMethods();
		// The we rebuild it from the updated Soot call graph
		Scene.v().getReachableMethods();
		
		if ( !opts.geom_trans() ) {
			// We remove the SPARK points-to information for pointers that have geomPTA results (willUpdate = true)
			// At querying time, the SPARK points-to container acts as a query cache
			for ( IVarAbstraction pn : pointers ) {
				// Keep only the points-to results for representatives
				if ( pn != pn.getRepresentative() ) {
					continue;
				}
				
				// Simplify
				if ( pn.hasPTResult() ) {
					pn.keepPointsToOnly();
					Node vn = pn.getWrappedNode();
					vn.discardP2Set();
				}
			}
		}
		else {
			// Do we need to obtain the context insensitive points-to result?
			transformToCIResult();
		}
	}
	
	/**
	 * For many applications, they only need the context insensitive points-to result.
	 * We provide a way to transfer our result back to SPARK.
	 * After the transformation, we discard the context sensitive points-to information.
	 * Therefore, if context sensitive queries are needed in future, please call ddSolve() for queried pointers first. 
	 */
	public void transformToCIResult()
	{	
		for ( IVarAbstraction pn : pointers ) {
			if ( pn.getRepresentative() != pn ) continue;
			
			Node node = pn.getWrappedNode();
			node.discardP2Set();
			PointsToSetInternal ptSet = node.makeP2Set();
			for ( AllocNode obj : pn.get_all_points_to_objects() ) {
				ptSet.add( obj );
			}
			
			pn.deleteAll();
		}
		
		hasTransformed = true;
	}
	
	/**
	 * The starting point of the geometric points-to analysis engine.
	 * This function computes the whole program points-to information.
	 */
	public void solve() 
	{
		long solve_time = 0, prepare_time = 0;
		long mem;
		int rounds;
		int n_obs;
		
		// Flush all accumulated outputs
		G.v().out.flush();
		
		// Collect and process the basic information from SPARK
		preprocess();
		mergeLocalVariables();
		
		worklist.initialize(pointers.size());
		offlineProcessor = new OfflineProcessor(this);
		IFigureManager.cleanCache();
		
		int evalLevel = opts.geom_eval();
		GeomEvaluator ge = new GeomEvaluator(this, ps);
		if ( evalLevel == Constants.eval_basicInfo )
			ge.profileSparkBasicMetrics();
		
		// Start our constraints solving phase
		Date begin = new Date();
				
		// Main loop
		for ( rounds = 0, n_obs = 1000; 
				rounds < Parameters.cg_refine_times && n_obs > 0; ++rounds ) {

			ps.println("\n" + "[Geom] Propagation Round " + rounds + " ==> ");
			
			// Encode the contexts
			encodeContexts(rounds == 0);
						
			// Offline processing: 
			// substantially use the points-to result for redundancy elimination prior to the analysis
			Date prepare_begin = new Date();
				offlineProcessor.init();
				offlineProcessor.defaultFeedPtsRoutines();
				offlineProcessor.runOptimizations();
			Date prepare_end = new Date();
			prepare_time += prepare_end.getTime() - prepare_begin.getTime();	

			if ( rounds == 0 ) {
				if ( evalLevel <= Constants.eval_basicInfo ) {
					offlineProcessor.releaseSparkMem();
				}
			}
					
			// Clear the points-to results in previous runs
			prepareNextRun();
			
			// We construct the initial flow graph
			nodeGenerator.initFlowGraph(this);

			// Solve the constraints
			solveConstraints();
			
			// We update the call graph and other internal data when the new points-to information is ready
			n_obs = updateCallGraph();
			finalizeInternalData();
		}

		if ( rounds < Parameters.cg_refine_times )
			ps.printf( "\nThe points-to information has converged. We stop here.\n" );
		
		Date end = new Date();
		solve_time += end.getTime() - begin.getTime();
		mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		
		ps.println();
		ps.printf("[Geom] Preprocessing time: %.2f s\n", (double) prepare_time / 1000);
		ps.printf("[Geom] Total time: %.2f s\n", (double) solve_time / 1000 );
		ps.printf("[Geom] Memory: %.1f MB\n", (double) (mem) / 1024 / 1024 );
		
		// We perform a set of tests to assess the quality of the points-to results for user pointers
		if ( evalLevel != Constants.eval_nothing ) {
			ge.profileGeomBasicMetrics(evalLevel > Constants.eval_basicInfo);
			if ( evalLevel > Constants.eval_basicInfo ) {
				ge.checkCallGraph();
				ge.checkCastsSafety();
				ge.checkAliasAnalysis();
//				ge.estimateHeapDefuseGraph();
			}
		}
		
		// Make changes available to Soot
		finalizeSootData();
		
		// Finish
		releaseUselessResources();
		hasExecuted = true;
	}

	/**
	 * The demand-driven mode for precisely computing points-to information for given pointers.
	 * Call graph will not be updated in this mode.
	 * @param qryNodes: the set of nodes that would be refined by geomPA.
	 */
	public void ddSolve(Set<Node> qryNodes)
	{
		long solve_time = 0, prepare_time = 0;
		
		if ( hasExecuted == false )
			solve();
		
		if ( ddPrepared == false || offlineProcessor == null ) {
			offlineProcessor = new OfflineProcessor(this);
			IFigureManager.cleanCache();
			ddPrepared = true;
			
			// First time entering into the demand-driven mode
			ps.println();
			ps.println("==> Entering demand-driven mode (experimental).");
		}
		
		int init_size = qryNodes.size();
		
		if ( init_size == 0 ) {
			ps.println("Please provide at least one pointer.");
			return;
		}
		
		// We must not encode the contexts again, 
		// otherwise the points-to information is invalid due to context mapping change
		// encodeContexts();

		// We first perform the offline optimizations
		Date prepare_begin = new Date();

		offlineProcessor.init();
		offlineProcessor.addUserDefPts(qryNodes);
		offlineProcessor.runOptimizations();

		Date prepare_end = new Date();
		prepare_time += prepare_end.getTime() - prepare_begin.getTime();

		// Run geomPA again
		Date begin = new Date();

		prepareNextRun();
		nodeGenerator.initFlowGraph(this);
		solveConstraints();

		Date end = new Date();
		solve_time += end.getTime() - begin.getTime();
		
		ps.println();
		ps.printf("[ddGeom] Preprocessing time: %.2f seconds\n", (double) prepare_time / 1000);
		ps.printf("[ddGeom] Main propagation time: %.2f seconds\n", (double) solve_time / 1000);
	}
	
	/**
	 * We thoroughly delete the geometric points-to result for space saving.
	 * Some applications such as those needing the call graph only may want to clean the points-to result.
	 */
	public void cleanResult()
	{
		consG.clear();
		pointers.clear();
		allocations.clear();
		constraints.clear();
		func2int.clear();
		int2func.clear();
		edgeMapping.clear();
		hasTransformed = false;
		hasExecuted = false;
		
		System.gc(); System.gc(); 
		System.gc(); System.gc();
	}
	
	/**
	 * Keep only the pointers the users are interested in.
	 * Just used for reducing memory occupation.
	 */
	public void keepOnly( Set<IVarAbstraction> usefulPointers )
	{
		Set<IVarAbstraction> reps = new HashSet<IVarAbstraction>();
		
		for ( IVarAbstraction pn : usefulPointers ) {
			reps.add( pn.getRepresentative() );
		}
		
		usefulPointers.addAll(reps);
		reps = null;
		
		for ( IVarAbstraction pn : pointers ) {
			if ( !usefulPointers.contains(pn) )
				pn.deleteAll();
		}
		
		System.gc();
	}
	
	/**
	 * Get Internal ID for soot method @param sm
	 * @return -1 if the given method is unreachable
	 */
	public int getIDFromSootMethod( SootMethod sm )
	{
		Integer ans = func2int.get(sm);
		return ans == null ? Constants.UNKNOWN_FUNCTION : ans.intValue();
	}
	
	/**
	 * Get soot method from given internal ID @param fid
	 * @return null if such ID is illegal.
	 */
	public SootMethod getSootMethodFromID( int fid )
	{
		return int2func.get(fid); 
	}
	
	/**
	 * Deciding if the given method represented by @param fid is reachable.
	 */
	public boolean isReachableMethod( int fid )
	{
		return fid == Constants.UNKNOWN_FUNCTION ? false : vis_cg[fid] != 0;
	}
	
	/**
	 * Deciding if the given method represented by @param sm is reachable.
	 */
	public boolean isReachableMethod( SootMethod sm )
	{
		int id = getIDFromSootMethod(sm);
		return isReachableMethod(id);
	}

	/**
	 * Telling if the given method is in the file given by the option "cg.spark geom-verify-name".
	 */
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
	
	/**
	 * A replacement of the Scene.v().getReachableMethods.
	 * @return
	 */
	public Set<SootMethod> getAllReachableMethods()
	{
		return func2int.keySet();
	}
	
	/**
	 * Get the call edges calling from the method @param fid.
	 */
	public CgEdge getCallEgesOutFrom( int fid )
	{
		return call_graph[fid];
	}
	
	/**
	 * Get the call edges calling into the method @param fid.
	 */
	public LinkedList<CgEdge> getCallEdgesInto( int fid )
	{
		if ( rev_call_graph == null )
			// We build the reversed call graph on demand
			buildRevCallGraph();
		
		return rev_call_graph.get(fid);
	}
	
	/**
	 * Get the index of the enclosing function of the specified node.
	 */
	public int getMethodIDFromPtr( IVarAbstraction pn )
	{
		SootMethod sm = null;
		int ret = Constants.SUPER_MAIN;
		
		Node node = pn.getWrappedNode();
		
		if ( node instanceof AllocNode ) {
			sm = ((AllocNode)node).getMethod();
		}
		else if ( node instanceof LocalVarNode ) {
			sm = ((LocalVarNode)node).getMethod();
		}
		else if ( node instanceof AllocDotField ) {
			sm = ((AllocDotField)node).getBase().getMethod();
		}
		
		if ( sm != null &&
				func2int.containsKey(sm) ) {
			int id = func2int.get( sm );
			if ( vis_cg[id] == 0 )
				ret = Constants.UNKNOWN_FUNCTION;
			
			if (ret == -1)
				System.out.println();
		}
		
		return ret;
	}
	
	/**
	 * Transform the SPARK node @param v representation to our representation.
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
	 * Find our representation for the SPARK node @param v.
	 * We don't create a new node if nothing found.
	 */
	public IVarAbstraction findInternalNode( Node v )
	{
		return consG.get(v);
	}
	
	/**
	 * Type compatibility test.
	 * @param src
	 * @param dst
	 */
	public boolean castNeverFails( Type src, Type dst )
	{
		return typeManager.castNeverFails(src, dst);
	}
	
	/**
	 * Get the number of valid pointers currently reachable by geomPTA.
	 */
	public int getNumberOfPointers()
	{
		return pointers.size();
	}
	
	/**
	 * Get the number of valid objects current in the container.
	 * @return
	 */
	public int getNumberOfObjects()
	{
		return allocations.size();
	}
	
	/**
	 * Return the number of functions that are reachable by SPARK.
	 */
	public int getNumberOfSparkMethods()
	{
		return n_func;
	}
	
	/**
	 * Return the number of functions that are reachable after the geometric points-to analysis.
	 */
	public int getNumberOfMethods()
	{
		return n_reach_methods;
	}
	
	public IWorklist getWorklist()
	{
		return worklist;
	}
	
	/**
	 * Obtain the internal representation of an object field.
	 */
	public IVarAbstraction findInstanceField(AllocNode obj, SparkField field)
	{
		AllocDotField af = findAllocDotField(obj, field);
		return consG.get(af);
	}
	
	/**
	 * Obtain or create an internal representation of an object field.
	 */
	public IVarAbstraction findAndInsertInstanceField(AllocNode obj, SparkField field) 
	{
		AllocDotField af = findAllocDotField(obj, field);
		IVarAbstraction pn = null;
		
		if ( af == null ) {
			// We create a new instance field node w.r.t type compatiblity
			Type decType = ((SootField) field).getDeclaringClass().getType();
			Type baseType = obj.getType();
			// baseType must be a sub type of decType
			if ( typeManager.castNeverFails(baseType, decType) ) {
				af = makeAllocDotField(obj, field);
				pn = makeInternalNode(af);
				pointers.add(pn);
			}
		}
		else {
			pn = consG.get(af);
		}
		
		return pn;
	}
	
	/**
	 * Obtain the edge representation internal to geomPTA.
	 */
	public CgEdge getInternalEdgeFromSootEdge( Edge e )
	{
		return edgeMapping.get(e);
	}
	
	public boolean isExceptionPointer( Node v )
	{
		if ( v.getType() instanceof RefType ) {
			SootClass sc = ((RefType)v.getType()).getSootClass();
			if ( !sc.isInterface() && Scene.v().getActiveHierarchy().isClassSubclassOfIncluding(
					sc, Constants.exeception_type.getSootClass()) ) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Given a valid SPARK node, we test if it is still valid after the geometric analysis.
	 */
	public boolean isValidGeometricNode( Node sparkNode )
	{
		IVarAbstraction pNode = consG.get(sparkNode);
		return pNode != null && pNode.reachable();
	}
	
	/**
	 * Is this a Spark or Geom? 
	 * @return
	 */
	public boolean hasGeomExecuted()
	{
		return hasExecuted;
	}
	
	/**
	 * Create all output files under the uniform location.
	 * @param file_name
	 * @return
	 * @throws FileNotFoundException
	 */
	public FileOutputStream createOutputFile( String file_name ) throws FileNotFoundException
	{
		return new FileOutputStream(
				new File( dump_dir, file_name ) );
	}
	
	// --------------------------------------------------------------------------------------------------------
	// -------------------------------Soot Standard Points-to Query Interface----------------------------------
	// --------------------------------------------------------------------------------------------------------
	
	private PointsToSetInternal field_p2set( PointsToSet s, final SparkField f )
	{
		if ( !(s instanceof PointsToSetInternal) )
			throw new RuntimeException( "Base pointers must be stored in *PointsToSetInternal*." );
		
		PointsToSetInternal bases = (PointsToSetInternal) s;
		final PointsToSetInternal ret = getSetFactory().newSet(f.getType(), this);
		
		bases.forall(new P2SetVisitor() {
			public final void visit(Node n) {
				Node nDotF = ((AllocNode) n).dot(f);
				if (nDotF != null) {
					//nDotF.getP2Set() has been discarded in solve()
					IVarAbstraction pn = consG.get(nDotF);
					if (pn == null
							|| hasTransformed 
							|| nDotF.getP2Set() != EmptyPointsToSet.v()) {
						ret.addAll(nDotF.getP2Set(), null);
						return;
					}
					
					pn = pn.getRepresentative();
					//PointsToSetInternal ptSet = nDotF.makeP2Set();
					for ( AllocNode obj : pn.get_all_points_to_objects() ) {
						ret.add( obj );
						//ptSet.add(obj);
					}
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
		if ( vn == null ) 
			return EmptyPointsToSet.v();
		
		IVarAbstraction pn = consG.get(vn);
		
		// In case this pointer has no geomPTA result
		// This is perhaps a bug
		if ( pn == null ) 
			return vn.getP2Set();
		
		// Return the cached result
		if ( hasTransformed ||
				vn.getP2Set() != EmptyPointsToSet.v() ) return vn.getP2Set();
		
		// Compute and cache the result
		pn = pn.getRepresentative();
		PointsToSetInternal ptSet = vn.makeP2Set();
		for ( AllocNode obj : pn.get_all_points_to_objects() ) {
			ptSet.add( obj );
		}
		
		return ptSet;
	}

	/*
	 * Currently, we only accept one call unit context (1CFA).
	 * For querying K-CFA (K >1), please see GeomQueries.contextsByCallChain
	 */
	@Override
	public PointsToSet reachingObjects(Context c, Local l) 
	{
		if ( !hasExecuted ) return super.reachingObjects(c, l);
		
		if ( hasTransformed ||
				!(c instanceof Unit) )
			return reachingObjects(l);
		
		LocalVarNode vn = findLocalVarNode(l);
		if ( vn == null ) 
			return EmptyPointsToSet.v();
		
		// Lookup the context sensitive points-to information for this pointer
		IVarAbstraction pn = consG.get(vn);
		if ( pn == null ) 
			return vn.getP2Set();
		
		pn = pn.getRepresentative();
		
		// Obtain the context sensitive points-to result
		SootMethod callee = vn.getMethod();
		Edge e = Scene.v().getCallGraph().findEdge((Unit) c, callee);
		if (e == null)
			return vn.getP2Set();

		// Compute the contexts interval
		CgEdge myEdge = getInternalEdgeFromSootEdge(e);
		if (myEdge == null)
			return vn.getP2Set();

		long low = myEdge.map_offset;
		long high = low + max_context_size_block[myEdge.s];
		
		// Lookup the cache
		ContextVarNode cvn = vn.context(c);
		if ( cvn != null ) {
			PointsToSetInternal ans = cvn.getP2Set();
			if ( ans != EmptyPointsToSet.v() ) return ans;
		}
		else {
			// Create a new context sensitive variable
			// The points-to vector is set to empty at start
			cvn = makeContextVarNode(vn, c);
		}

		// Fill
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
        if ( vn == null ) 
        	return EmptyPointsToSet.v();
        
        IVarAbstraction pn = consG.get(vn);
        if( pn == null ) 
        	return vn.getP2Set();
        
        // Lookup the cache
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
		
		// No such pointer seen by SPARK
		if ( adf == null )
			return EmptyPointsToSet.v();
		
		// Not seen by geomPTA
		if ( pn == null )
			return adf.getP2Set();
		
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
