/*
 * Please attach the following author information if you would like to redistribute the source code:
 * Developer: Xiao Xiao
 * Address: Room 4208, Hong Kong University of Science and Technology
 * Contact: frogxx@gmail.com
 */
package soot.jimple.spark.geom.heapinsE;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import soot.Hierarchy;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.jimple.spark.geom.geomE.GeometricManager;
import soot.jimple.spark.geom.geomPA.Constants;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.IVarAbstraction;
import soot.jimple.spark.geom.geomPA.IWorklist;
import soot.jimple.spark.geom.geomPA.PlainConstraint;
import soot.jimple.spark.geom.geomPA.RectangleNode;
import soot.jimple.spark.geom.geomPA.SegmentNode;
import soot.jimple.spark.geom.helper.PtSensVisitor;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.StringConstantNode;
import soot.jimple.spark.sets.P2SetVisitor;

/**
 * This class defines a pointer variable for use in the HeapIns encoding based points-to solver.
 * HeapIns is a simpler form of geometric encoding.
 * HeapIns is faster and uses less memory, but also, it is less precise than geometric encoding.
 * NOT recommended to use.
 * 
 * @author xiao
 *
 */
public class HeapInsNode extends IVarAbstraction
{
	// The targets of directed edges on the constraint graph
	public HashMap<HeapInsNode, HeapInsIntervalManager> flowto;

	// The objects this variable points to
	public HashMap<AllocNode, HeapInsIntervalManager> pt_objs;
	
	// Newly added points-to tuple
	public Map<AllocNode, HeapInsIntervalManager> new_pts;
	
	// store/load complex constraints
	public Vector<PlainConstraint> complex_cons = null;
	
	static {
		stubManager = new HeapInsIntervalManager();
		pres = new RectangleNode(0, 0, Constants.MAX_CONTEXTS, Constants.MAX_CONTEXTS);
		stubManager.addNewFigure(HeapInsIntervalManager.ALL_TO_ALL, pres);
		deadManager = new HeapInsIntervalManager();
	}
	
	public HeapInsNode( Node thisVar ) 
	{
		me = thisVar;
		flowto = new HashMap<HeapInsNode, HeapInsIntervalManager>();
		pt_objs = new HashMap<AllocNode, HeapInsIntervalManager>();
		new_pts = new HashMap<AllocNode, HeapInsIntervalManager>();
	}
	
	@Override
	public void deleteAll()
	{
		flowto = null;
		pt_objs = null;
		new_pts = null;
		complex_cons = null;
	}
	
	@Override
	public void reconstruct() 
	{
		new_pts = new HashMap<AllocNode, HeapInsIntervalManager>();
	
		if ( complex_cons != null )
			complex_cons.clear();
		
		flowto.clear();
		pt_objs.clear();
	}

	@Override
	public void do_before_propagation()
	{
//		if ( complex_cons == null )
			do_pts_interval_merge();
		
//		if ( !(me instanceof LocalVarNode) )
			do_flow_edge_interval_merge();

		// This pointer filter, please read the comments at this line in file FullSensitiveNode.java
		Node wrappedNode = getWrappedNode();
		if (wrappedNode instanceof LocalVarNode
				&& ((LocalVarNode) wrappedNode).isThisPtr()) {
			SootMethod func = ((LocalVarNode) wrappedNode).getMethod();
			if (!func.isConstructor()) {
				// We don't process the specialinvoke call edge
				SootClass defClass = func.getDeclaringClass();
				Hierarchy typeHierarchy = Scene.v().getActiveHierarchy();

				for (Iterator<AllocNode> it = new_pts.keySet().iterator(); it
						.hasNext();) {
					AllocNode obj = it.next();
					if (obj.getType() instanceof RefType) {
						SootClass sc = ((RefType) obj.getType()).getSootClass();
						if (defClass != sc) {
							try {
								SootMethod rt_func = typeHierarchy
										.resolveConcreteDispatch(sc, func);
								if (rt_func != func) {
									it.remove();
									// Also preclude it from propagation again
									pt_objs.put(
											obj,
											(HeapInsIntervalManager) deadManager);
								}
							} catch (RuntimeException e) {
								// If the input program has a wrong type cast, resolveConcreteDispatch fails and it goes here
								// We simply ignore this error
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Remember to clean the is_new flag
	 */
	@Override
	public void do_after_propagation() 
	{
		for (HeapInsIntervalManager im : new_pts.values() ) {
			im.flush();
		}
		
		new_pts = new HashMap<AllocNode, HeapInsIntervalManager>();
	}

	@Override
	public int num_of_diff_objs() {
		// If this pointer is not a representative pointer
		if ( parent != this )
			return getRepresentative().num_of_diff_objs();
		
		// If this pointer is not updated in the points-to analysis (willUpdate = false)
		if ( pt_objs == null )
			injectPts();
		
		return pt_objs.size();
	}

	@Override
	public int num_of_diff_edges() {
		return flowto.size();
	}
	
	@Override
	public boolean add_points_to_3(AllocNode obj, long I1, long I2, long L) 
	{	
		int code = 0;
		
		pres.I1 = I1;
		pres.I2 = I2;
		pres.L = L;
		
		if ( I1 == 0 )
			code = ( I2 == 0 ? HeapInsIntervalManager.ALL_TO_ALL : HeapInsIntervalManager.ALL_TO_MANY );
		else
			code = ( I2 == 0 ? HeapInsIntervalManager.MANY_TO_ALL : HeapInsIntervalManager.ONE_TO_ONE );
		
		return addPointsTo(code, obj);
	}
	
	@Override
	public boolean add_points_to_4(AllocNode obj, long I1, long I2,
			long L1, long L2) 
	{
		return false;
	}
	
	@Override
	public boolean add_simple_constraint_3(IVarAbstraction qv, long I1, long I2, long L) 
	{
		int code = 0;
		
		pres.I1 = I1;
		pres.I2 = I2;
		pres.L = L;
		
		if ( I1 == 0 )
			code = ( I2 == 0 ? HeapInsIntervalManager.ALL_TO_ALL : HeapInsIntervalManager.ALL_TO_MANY );
		else
			code = ( I2 == 0 ? HeapInsIntervalManager.MANY_TO_ALL : HeapInsIntervalManager.ONE_TO_ONE );
		
		return addFlowsTo(code, (HeapInsNode)qv);
	}

	@Override
	public boolean add_simple_constraint_4(IVarAbstraction qv, long I1, long I2,
			long L1, long L2) 
	{
		return false;
	}
	
	@Override
	public void put_complex_constraint(PlainConstraint cons) 
	{
		if ( complex_cons == null )
			complex_cons = new Vector<PlainConstraint>();
		complex_cons.add( cons );
	}

	/**
	 *  Discard all the ONE_TO_ONE figures which are covered by the ALL_TO_MANY and MANY_TO_ALL figures
	 */
	@Override
	public void drop_duplicates()
	{
		for ( HeapInsIntervalManager im : pt_objs.values() ) {
			im.removeUselessSegments();
		}
	}
	
	/**
	 * An efficient implementation of differential propagation.
	 */
	@Override
	public void propagate(GeomPointsTo ptAnalyzer, IWorklist worklist) 
	{
		int i, j;
		AllocNode obj;
		SegmentNode pts, pe, int_entry1[], int_entry2[];
		HeapInsIntervalManager him1, him2;
		HeapInsNode qn, objn;
		boolean added, has_new_edges;
		
		// We first build the new flow edges via the field dereferences
		if ( complex_cons != null ) {
			for ( Map.Entry<AllocNode, HeapInsIntervalManager> entry : new_pts.entrySet() ) {
				obj = entry.getKey();
				int_entry1 = entry.getValue().getFigures();
				
				for (PlainConstraint pcons : complex_cons) {
					// Construct the two variables in assignment
					objn = (HeapInsNode)ptAnalyzer.findAndInsertInstanceField(obj, pcons.f);
					if ( objn == null ) {
						// This combination of allocdotfield must be invalid
						// This expression p.f also renders that p cannot point to obj, so we remove it
						// We label this event and sweep the garbage later
						pt_objs.put(obj, (HeapInsIntervalManager)deadManager);
						entry.setValue( (HeapInsIntervalManager)deadManager );
						break;
					}
					qn = (HeapInsNode) pcons.otherSide;
					
					for ( i = 0; i < HeapInsIntervalManager.Divisions; ++i ) {
						pts = int_entry1[i];
						while ( pts != null && pts.is_new ) {
							switch ( pcons.type ) {
							case Constants.STORE_CONS:
								// Store, qv -> pv.field
								// pts.I2 may be zero, pts.L may be less than zero
								if ( qn.add_simple_constraint_3( objn,
										pcons.code == GeometricManager.ONE_TO_ONE ? pts.I1 : 0,
										pts.I2,
										pts.L < 0  ? -pts.L : pts.L
								) )
									worklist.push( qn );
								break;
								
							case Constants.LOAD_CONS:
								// Load, pv.field -> qv
								if ( objn.add_simple_constraint_3( qn, 
										pts.I2, 
										pcons.code == GeometricManager.ONE_TO_ONE ? pts.I1 : 0,
										pts.L < 0 ? -pts.L : pts.L
								) )
									worklist.push( objn );
								break;
								
							default:
								throw new RuntimeException("Wrong Complex Constraint");
							}
							
							pts = pts.next;
						}
					}
				}
			}
		}
		
		for ( Map.Entry<HeapInsNode, HeapInsIntervalManager> entry1 : flowto.entrySet() ) {
			// First, we pick one flow-to figure
			added = false;
			qn = entry1.getKey();
			him1 = entry1.getValue();
			int_entry1 = him1.getFigures();		// Figure collection for the flows-to tuple
			has_new_edges = him1.isThereUnprocessedFigures();
			Map<AllocNode, HeapInsIntervalManager> objs = ( has_new_edges ? pt_objs : new_pts );
			
			
			for (Map.Entry<AllocNode, HeapInsIntervalManager> entry2 : objs.entrySet()) {
				// Second, we get the points-to intervals
				obj = entry2.getKey();
				him2 = entry2.getValue();
				
				if ( him2 == deadManager ) continue;
				if (!ptAnalyzer.castNeverFails(obj.getType(), qn.getWrappedNode().getType())) continue;
				
				// Figure collection for the points-to tuple
				int_entry2 = him2.getFigures();
				
				// We pair up all the interval points-to tuples and interval flow edges
				// Loop over all points-to figures
				for (i = 0; i < HeapInsIntervalManager.Divisions; ++i) {
					pts = int_entry2[i];
					while ( pts != null ) {
						if (  !has_new_edges && !pts.is_new )
							break;
						
						// Loop over all flows-to figures
						for ( j = 0; j < HeapInsIntervalManager.Divisions; ++j) {
							pe = int_entry1[j];
							while ( pe != null ) {
								if ( pts.is_new || pe.is_new ) {
									// Propagate this object
									if ( add_new_points_to_tuple(pts, pe, obj, qn) )
										added = true;
								}
								else
									break;
								
								pe = pe.next;
							}
						}
						
						pts = pts.next;
					}
				}
			}
			
			if ( added )
				worklist.push( qn );
			
			// Now, we clean the new edges if necessary
			if ( has_new_edges ) him1.flush();
		}
	}
	
	@Override
	public int count_pts_intervals(AllocNode obj) 
	{
		int ret = 0;
		SegmentNode[] int_entry = find_points_to(obj);
		
		for (int j = 0; j < HeapInsIntervalManager.Divisions; ++j) {
			SegmentNode p = int_entry[j];
			while (p != null) {
				++ret;
				p = p.next;
			}
		}
		
		return ret;
	}

	@Override
	public int count_flow_intervals(IVarAbstraction qv) 
	{
		int ret = 0;
		SegmentNode[] int_entry = find_flowto( (HeapInsNode)qv );
		
		for (int j = 0; j < HeapInsIntervalManager.Divisions; ++j) {
			SegmentNode p = int_entry[j];
			while (p != null) {
				++ret;
				p = p.next;
			}
		}
		
		return ret;
	}

	/**
	 * Query if this pointer and qv could point to the same object under any contexts
	 */
	@Override
	public boolean heap_sensitive_intersection(IVarAbstraction qv) 
	{
		int i, j;
		HeapInsNode qn;
		SegmentNode p, q, pt[], qt[];
		
		qn = (HeapInsNode)qv;
		
		for (Iterator<AllocNode> it = pt_objs.keySet().iterator(); it.hasNext();) {
			AllocNode an = it.next();
			if ( an instanceof StringConstantNode ) continue;
			qt = qn.find_points_to(an);
			if (qt == null) continue;
			pt = find_points_to(an);

			for (i = 0; i < HeapInsIntervalManager.Divisions; ++i) {
				p = pt[i];
				while (p != null) {
					for (j = 0; j < HeapInsIntervalManager.Divisions; ++j) {
						q = qt[j];
						while (q != null) {
							if (quick_intersecting_test(p, q))
								return true;
							q = q.next;
						}
					}
					p = p.next;
				}
			}
		}

		return false;
	}

	@Override
	public Set<AllocNode> get_all_points_to_objects() 
	{
		// If this pointer is not a representative pointer
		if ( parent != this )
			return getRepresentative().get_all_points_to_objects();
		
		return pt_objs.keySet();
	}

	@Override
	public void print_context_sensitive_points_to( PrintStream outPrintStream ) 
	{
		for (Iterator<AllocNode> it = pt_objs.keySet().iterator(); it.hasNext();) {
			AllocNode obj = it.next();
			SegmentNode[] int_entry = find_points_to( obj );
			for (int j = 0; j < HeapInsIntervalManager.Divisions; ++j) {
				SegmentNode p = int_entry[j];
				while (p != null) {
					outPrintStream.println("(" + obj.toString() + ", " + p.I1 + ", "
							+ p.I2 + ", " + p.L + ")");
					p = p.next;
				}
			}
		}
	}
	
	@Override
	public boolean pointer_interval_points_to(long l, long r, AllocNode obj) 
	{
		SegmentNode[] int_entry = find_points_to(obj);
		
		// Check all-to-many figures
		if ( int_entry[HeapInsIntervalManager.ALL_TO_MANY] != null ) return true;
		
		for ( int i = 1; i < HeapInsIntervalManager.Divisions; ++i ) {
			SegmentNode p = int_entry[i];
			while ( p != null ) {
				long R = p.I1 + p.L;
				if ( (l <= p.I1 && p.I1 < r) || ( p.I1 <= l && l < R) )
					return true;
				p = p.next;
			}
		}
		
		return false;
	}

	@Override
	public void remove_points_to(AllocNode obj) 
	{
		pt_objs.remove(obj);
	}

	@Override
	public void keepPointsToOnly() 
	{
		flowto = null;
		new_pts = null;
		complex_cons = null;
	}

	@Override
	public int count_new_pts_intervals() 
	{
		int ans = 0;
		
		for ( HeapInsIntervalManager im : new_pts.values() ) {
			SegmentNode[] int_entry = im.getFigures();
			for ( int i = 0; i < HeapInsIntervalManager.Divisions; ++i ) {
				SegmentNode p = int_entry[i];
				while ( p != null && p.is_new == true ) {
					++ans;
					p = p.next;
				}
			}
		}
		
		return ans;
	}

	@Override
	public void get_all_context_sensitive_objects(long l, long r, PtSensVisitor visitor) 
	{
		if ( parent != this ) {
			getRepresentative().get_all_context_sensitive_objects(l, r, visitor);
			return;
		}
		
		for ( Map.Entry<AllocNode, HeapInsIntervalManager> entry : pt_objs.entrySet() ) {
			AllocNode obj = entry.getKey();
			HeapInsIntervalManager im = entry.getValue();
			SegmentNode[] int_entry = im.getFigures();
			
			// We first get the 1-CFA contexts for the object
			SootMethod sm = obj.getMethod();
			int sm_int = 0;
			long n_contexts = 1;
			if ( sm != null ) {
				sm_int = ptsProvider.getIDFromSootMethod(sm);
				n_contexts = ptsProvider.context_size[sm_int];
			}
			
			// We search for all the pointers falling in the range [1, r) that may point to this object
			for ( int i = 0; i < HeapInsIntervalManager.Divisions; ++i ) {
				SegmentNode p = int_entry[i];
				while ( p != null ) {
					long R = p.I1 + p.L;
					long objL = -1, objR = -1;
					
					// Now we compute which context sensitive objects are pointed to by this pointer
					if ( i == HeapInsIntervalManager.ALL_TO_MANY ) {
						// all-to-many figures
						objL = p.I2;
						objR = p.I2 + p.L;
					}
					else {
						// We compute the intersection
						if ( l <= p.I1 && p.I1 < r ) {	
							if ( i != HeapInsIntervalManager.MANY_TO_ALL ) {
								long d = r - p.I1;
								if ( d > p.L ) d = p.L;
								objL = p.I2;
								objR = objL + d;
							}
							else {
								objL = 1;
								objR = 1 + n_contexts;
							}
						}
						else if (p.I1 <= l && l < R) {
							if ( i != HeapInsIntervalManager.MANY_TO_ALL ) {
								long d = R - l;
								if ( R > r ) d = r - l;
								objL = p.I2 + l - p.I1;
								objR = objL + d;
							}
							else {
								objL = 1;
								objR = 1 + n_contexts;
							}
						}
					}
					
					// Now we test which context versions should this interval [objL, objR) maps to
					if ( objL != -1 && objR != -1 )
						visitor.visit(obj, objL, objR, sm_int);
					
					p = p.next; 
				}
			}
		}
	}

	@Override
	public void injectPts() 
	{
		pt_objs = new HashMap<AllocNode, HeapInsIntervalManager>();
		
		me.getP2Set().forall( new P2SetVisitor() {
			@Override
			public void visit(Node n) {
				if ( ptsProvider.isValidGeometricNode(n) )
					pt_objs.put((AllocNode)n, (HeapInsIntervalManager)stubManager);
			}
		});
		
		new_pts = null;
	}

	@Override
	public boolean isDeadObject(AllocNode obj) 
	{
		return pt_objs.get(obj) == deadManager;
	}
	
	//---------------------------------Private Functions----------------------------------------
	private SegmentNode[] find_flowto(HeapInsNode qv) {
		HeapInsIntervalManager im = flowto.get(qv);
		if ( im == null ) return null;
		return im.getFigures();
	}

	private SegmentNode[] find_points_to(AllocNode obj) {
		HeapInsIntervalManager im = pt_objs.get(obj);
		if ( im == null ) return null;
		return im.getFigures();
	}
	
	/**
	 *  Merge the context sensitive tuples, and make a single insensitive tuple
	 */
	private void do_pts_interval_merge()
	{
		for ( HeapInsIntervalManager him : new_pts.values() ) {
			him.mergeFigures( Constants.max_pts_budget );
		}
	}
	
	private void do_flow_edge_interval_merge()
	{
		for ( HeapInsIntervalManager him : flowto.values() ) {
			him.mergeFigures( Constants.max_cons_budget );
		}
	}
	
	private boolean addPointsTo( int code, AllocNode obj )
	{
		HeapInsIntervalManager im = pt_objs.get(obj);
		
		if ( im == null ) {
			im = new HeapInsIntervalManager();
			pt_objs.put(obj, im);
		}
		else if ( im == deadManager ) {
			// We preclude the propagation of this object
			return false;
		}
		
		// pres has been filled properly before calling this method
		if ( im.addNewFigure(code, pres) != null ) {
			new_pts.put(obj, im);
			return true;
		}
		
		return false;
	}
	
	private boolean addFlowsTo( int code, HeapInsNode qv )
	{
		HeapInsIntervalManager im = flowto.get(qv);
		
		if ( im == null ) {
			im = new HeapInsIntervalManager();
			flowto.put(qv, im);
		}
		
		// pres has been filled properly before calling this method
		return im.addNewFigure(code, pres) != null;
	}
	
	// Apply the inference rules
	private boolean add_new_points_to_tuple( SegmentNode pts, SegmentNode pe, 
			AllocNode obj, HeapInsNode qn )
	{
		long interI, interJ;
		int code = 0;
		
		// Special Cases
		if (pts.I1 == 0 || pe.I1 == 0) {
			
			if ( pe.I2 != 0 ) {
				// pointer sensitive, heap insensitive
				pres.I1 = pe.I2;
				pres.I2 = 0;
				pres.L = pe.L;
				code = HeapInsIntervalManager.MANY_TO_ALL;
			}
			else {
				// pointer insensitive, heap sensitive
				pres.I1 = 0;
				pres.I2 = pts.I2;
				pres.L = pts.L;
				code = ( pts.I2 == 0 ? HeapInsIntervalManager.ALL_TO_ALL : HeapInsIntervalManager.ALL_TO_MANY );
			}
		}
		else { 
			// The left-end is the larger one
			interI = pe.I1 < pts.I1 ? pts.I1 : pe.I1;
			// The right-end is the smaller one
			interJ = (pe.I1 + pe.L < pts.I1 + pts.L ? pe.I1 + pe.L : pts.I1 + pts.L);
			
			if (interI >= interJ) return false;
			
			// The intersection is non-empty
			pres.I1 = (pe.I2 == 0 ? 0 : interI - pe.I1 + pe.I2);
			pres.I2 = (pts.I2 == 0 ? 0 : interI - pts.I1 + pts.I2);
			pres.L = interJ - interI;
			
			if ( pres.I1 == 0 )
				code = ( pres.I2 == 0 ? HeapInsIntervalManager.ALL_TO_ALL : HeapInsIntervalManager.ALL_TO_MANY );
			else
				code = ( pres.I2 == 0 ? HeapInsIntervalManager.MANY_TO_ALL : HeapInsIntervalManager.ONE_TO_ONE );
		}
		
		return qn.addPointsTo( code, obj );
	}
	
	// We only test if their points-to objects intersected under context
	// insensitive manner
	private boolean quick_intersecting_test(SegmentNode p, SegmentNode q) {
		if ( p.I2 == 0 || q.I2 == 0 )
			return true;
		
		if (p.I2 >= q.I2)
			return p.I2 < q.I2 + (q.L < 0 ? -q.L : q.L);
		return q.I2 < p.I2 + (p.L < 0 ? -p.L : p.L);
	}
}
