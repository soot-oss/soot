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
package soot.jimple.spark.geom.geomE;

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
import soot.jimple.spark.geom.dataMgr.PtSensVisitor;
import soot.jimple.spark.geom.dataRep.PlainConstraint;
import soot.jimple.spark.geom.dataRep.RectangleNode;
import soot.jimple.spark.geom.dataRep.SegmentNode;
import soot.jimple.spark.geom.geomPA.Constants;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.IVarAbstraction;
import soot.jimple.spark.geom.geomPA.IWorklist;
import soot.jimple.spark.geom.geomPA.Parameters;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.ClassConstantNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.StringConstantNode;
import soot.jimple.spark.sets.P2SetVisitor;

/**
 * This class defines an abstract pointer in the geometric points-to solver.
 * All the points-to/flows-to information and the load/store constraints related to this pointer are stored here.
 * The pointer assignment inference rules and the complex constraints initialization rules are also implemented here.
 *  
 * @author xiao
 *
 */
public class FullSensitiveNode extends IVarAbstraction 
{
	// The targets of directed edges on the constraint graph
	public Map<FullSensitiveNode, GeometricManager> flowto;

	// The objects this variable points to
	public Map<AllocNode, GeometricManager> pt_objs;
	
	// Newly added points-to tuple
	public Map<AllocNode, GeometricManager> new_pts;
	
	// store/load complex constraints
	public Vector<PlainConstraint> complex_cons;
	
	// Symbolicize the 1-to-1 and many-to-many mappings
	public static String symbols[] = {"/", "[]"};
	
	static {
		stubManager = new GeometricManager();
		pres = new RectangleNode(1, 1, Constants.MAX_CONTEXTS, Constants.MAX_CONTEXTS);
		stubManager.addNewFigure(GeometricManager.MANY_TO_MANY, pres);
		deadManager = new GeometricManager();
	}
	
	public FullSensitiveNode( Node thisVar )
	{		
		me = thisVar;
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
		flowto = new HashMap<FullSensitiveNode, GeometricManager>();
		pt_objs = new HashMap<AllocNode, GeometricManager>();
		new_pts = new HashMap<AllocNode, GeometricManager>();
		complex_cons = null;
		lrf_value = 0;
	}

	@Override
	public void keepPointsToOnly()
	{
		flowto = null;
		new_pts = null;
		complex_cons = null;
	}
	
	@Override
	public void do_before_propagation() 
	{
		// We first perform the geometric merging
		do_pts_interval_merge();
		do_flow_edge_interval_merge();
		
		/*
		 * The following code eliminates the spurious points-to relation for THIS pointer.
		 * For example we have two classes A and B, B is a child class of A.
		 * We have a virtual function foo defined in both A and B.
		 * We have a pointer p in type A.
		 * pts(p) = { o1, o2 }, where o1 is in type A and o2 is in type B.
		 * Therefore, the call p.foo() will be resolved to call both A::foo and B::foo.
		 * Then, in the points-to analysis, we have two assignments: p -> A::foo.THIS, p -> B::foo.THIS
		 * At this time, obviously, although with the type filter, A::foo.THIS will receive the object o2, which is definitely a fake.
		 * Thus, we need a new filter to guarantee that A::foo.THIS only points to o1.
		 * We call this filter "this pointer filter".
		 */
		Node wrappedNode = getWrappedNode();
		if ( wrappedNode instanceof LocalVarNode &&
				((LocalVarNode)wrappedNode).isThisPtr() ) {
			SootMethod func = ((LocalVarNode)wrappedNode).getMethod();
			if ( !func.isConstructor() ) {
				// We don't process the specialinvoke call edge
				SootClass defClass = func.getDeclaringClass();
				Hierarchy typeHierarchy = Scene.v().getActiveHierarchy();
				
				for ( Iterator<AllocNode> it = new_pts.keySet().iterator(); it.hasNext(); ) {
					AllocNode obj = it.next();
					if ( obj.getType() instanceof RefType ) {
						SootClass sc = ((RefType)obj.getType()).getSootClass();
						if ( defClass != sc ) {
							try {
								SootMethod rt_func = typeHierarchy.resolveConcreteDispatch(sc, func);
								if ( rt_func != func ) {
									it.remove();
									// Also preclude it from propagation again
									pt_objs.put(obj, (GeometricManager)deadManager);
								}
							}
							catch ( RuntimeException e ) {
								// If the input program has a wrong type cast, resolveConcreteDispatch fails and it goes here
								// We simply ignore this error
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void do_after_propagation() 
	{
		if ( new_pts.size() > 0 ) {
			for (GeometricManager gm : new_pts.values()) {
				gm.flush();
			}
		}
		new_pts = new HashMap<AllocNode, GeometricManager>();
	}

	@Override
	public int num_of_diff_objs() 
	{
		// If this pointer is not a representative pointer
		if ( parent != this )
			return getRepresentative().num_of_diff_objs();
				
		if ( pt_objs == null )
			return -1;
		
		return pt_objs.size();
	}

	@Override
	public int num_of_diff_edges() 
	{
		if ( parent != this )
			return getRepresentative().num_of_diff_objs();
		
		if ( flowto == null ) 
			return -1;
		
		return flowto.size();
	}

	@Override
	public boolean add_points_to_3(AllocNode obj, long I1, long I2,
			long L) 
	{			
		pres.I1 = I1;
		pres.I2 = I2;
		pres.L = L;

		return addPointsTo(GeometricManager.ONE_TO_ONE, obj);
	}

	@Override
	public boolean add_points_to_4(AllocNode obj, long I1, long I2,
			long L1, long L2) 
	{
		pres.I1 = I1;
		pres.I2 = I2;
		pres.L = L1;
		pres.L_prime = L2;
		
		return addPointsTo(GeometricManager.MANY_TO_MANY, obj);
	}

	@Override
	public boolean add_simple_constraint_3(IVarAbstraction qv, long I1,
			long I2, long L) 
	{
		pres.I1 = I1;
		pres.I2 = I2;
		pres.L = L;
		
		return addFlowsTo(GeometricManager.ONE_TO_ONE, qv);
	}

	@Override
	public boolean add_simple_constraint_4(IVarAbstraction qv, long I1,
			long I2, long L1, long L2) 
	{
		pres.I1 = I1;
		pres.I2 = I2;
		pres.L = L1;
		pres.L_prime = L2;
		
		return addFlowsTo(GeometricManager.MANY_TO_MANY, qv);
	}

	@Override
	public void put_complex_constraint(PlainConstraint cons) 
	{
		if ( complex_cons == null )
			complex_cons = new Vector<PlainConstraint>();
		complex_cons.add( cons );
	}

	@Override
	public void drop_duplicates() 
	{
		for ( GeometricManager gm : pt_objs.values() ) {
			gm.removeUselessSegments();
		}
	}
	
	/**
	 * The place where you implement the pointer assignment reasoning.
	 */
	@Override
	public void propagate(GeomPointsTo ptAnalyzer, IWorklist worklist) 
	{
		int i, j;
		AllocNode obj;
		SegmentNode pts, pe, entry_pts[], entry_pe[];
		GeometricManager gm1, gm2;
		FullSensitiveNode qn, objn;
		boolean added, hasNewPointsTo;

		if ( pt_objs.size() == 0 ) return;
//		System.err.println("+++ Process assignment for: " + toString());
		
		// We first build the flow edges that flow in to/out of object fields
		if ( complex_cons != null ) {
			
			for ( Map.Entry<AllocNode, GeometricManager> entry : new_pts.entrySet() ) {
				obj = entry.getKey();
				entry_pts = entry.getValue().getFigures();
				
				for ( PlainConstraint pcons : complex_cons ) {
					// For each newly points-to object, construct its instance field
					objn = (FullSensitiveNode)ptAnalyzer.findInstanceField(obj, pcons.f);
					if ( objn == null ) {
						// This combination of allocdotfield must be invalid
						// This expression p.f also renders that p cannot point to obj, so we remove it
						// We label this event and sweep the garbage later
						pt_objs.put(obj, (GeometricManager)deadManager);
						entry.setValue( (GeometricManager)deadManager );
						break;
					}
					
					if ( objn.willUpdate == false ) {
						// This must be a store constraint
						// This object field is not need for computing 
						// the points-to information of the seed pointers
						continue;
					}
					
					qn = (FullSensitiveNode) pcons.otherSide;
//					System.err.println("--load/store-->: " + qn.toString());
					
					for ( i = 0; i < GeometricManager.Divisions; ++i ) {
						pts = entry_pts[i];
						
						while ( pts != null && pts.is_new == true ) {
							switch ( pcons.type ) {
							case Constants.STORE_CONS:
								// Store, qv -> pv.field
								if ( instantiateStoreConstraint( qn, objn, pts, (pcons.code<<8) | i ) )
									worklist.push( qn );
								break;
								
							case Constants.LOAD_CONS:
								// Load, pv.field -> qv
								if ( instantiateLoadConstraint( objn, qn, pts, (pcons.code<<8) | i ) )
									worklist.push( objn );
								break;
							}
							
							pts = pts.next;
						}
					}
				}
			}
		}
		
		if ( flowto.size() == 0 ) return;
		
		// Next, we process the assignments (e.g. p = q)
		for (Map.Entry<FullSensitiveNode, GeometricManager> entry1 : flowto.entrySet()) {	
			added = false;
			qn = entry1.getKey();
			gm1 = entry1.getValue();
			entry_pe = gm1.getFigures();
			
//			System.err.println("-assign->: " + qn.toString());
			
			// We specialize the two cases that we hope it running faster
			// We have new flow-to edges
			if ( gm1.isThereUnprocessedFigures() ) {
				
				// Second, we get the points-to shapes
				for ( Map.Entry<AllocNode, GeometricManager> entry2 : pt_objs.entrySet() ) {
					obj = entry2.getKey();
					gm2 = entry2.getValue();
					
					// Avoid the garbage
					if ( gm2 == deadManager ) continue;
					
					// Type filtering and flow-to-this filtering, a simple approach
					if (!ptAnalyzer.castNeverFails(obj.getType(), qn.getType())) continue;
					
					entry_pts = gm2.getFigures();
					hasNewPointsTo = gm2.isThereUnprocessedFigures();
					
					// We pair up all the geometric points-to tuples and flow edges
					for ( j = 0; j < GeometricManager.Divisions; ++j ) {
						pe = entry_pe[j];
												
						while ( pe != null ) {
							if ( pe.is_new == false && hasNewPointsTo == false )
								break;
							
							for (i = 0; i < GeometricManager.Divisions ; ++i) {
								pts = entry_pts[i];
								
								while ( pts != null &&
										( pts.is_new || pe.is_new ) ) {
									// Propagate this object
									if ( reasonAndPropagate( qn, obj, pts, pe, (i<<8)|j) )
										added = true;
									pts = pts.next;
								}
							}
							
							pe = pe.next;
						}
					}
				}
				
				gm1.flush();
			}
			else {
				// We don't have new edges, thereby we can do the pairing up faster
				
				for ( Map.Entry<AllocNode, GeometricManager> entry2 : new_pts.entrySet() ) {
					obj = entry2.getKey();
					gm2 = entry2.getValue();
					
					// Avoid the garbage
					if ( gm2 == deadManager ) continue;
					
					// Type filtering and flow-to-this filtering, a simple approach
					if (!ptAnalyzer.castNeverFails(obj.getType(), qn.getType())) continue;
					
					entry_pts = gm2.getFigures();
					
					// We pair up all the geometric points-to tuples and flow edges
					for ( i = 0; i < GeometricManager.Divisions; ++i ) {
						pts = entry_pts[i];
						
						while ( pts != null && pts.is_new == true ) {
							for (j = 0; j < GeometricManager.Divisions ; ++j) {
								pe = entry_pe[j];
								
								while ( pe != null ) {
									// Propagate this object
									if ( reasonAndPropagate( qn, obj, pts, pe, (i<<8)|j) )
										added = true;
									pe = pe.next;
								}
							}
							
							pts = pts.next;
						}
					}
				}
			}
			
			if ( added )
				worklist.push( qn );
		}
		
//		System.err.println();
	}

	
	@Override
	public boolean isDeadObject( AllocNode obj )
	{
		return pt_objs.get(obj) == deadManager;
	}
	
	@Override
	public int count_pts_intervals(AllocNode obj) 
	{
		int ret = 0;
		SegmentNode[] int_entry = find_points_to(obj);
		
		for (int j = 0; j < GeometricManager.Divisions; ++j) {
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
		SegmentNode[] int_entry = find_flowto( (FullSensitiveNode)qv );
		
		for (int j = 0; j < GeometricManager.Divisions; ++j) {
			SegmentNode p = int_entry[j];
			while (p != null) {
				++ret;
				p = p.next;
			}
		}
		
		return ret;
	}

	@Override
	public boolean heap_sensitive_intersection(IVarAbstraction qv) 
	{
		int i, j;
		FullSensitiveNode qn;
		SegmentNode p, q, pt[], qt[];
		boolean localToSameMethod;
		
		qn = (FullSensitiveNode)qv;
		localToSameMethod = (enclosingMethod() == qv.enclosingMethod());
		
		for (Iterator<AllocNode> it = pt_objs.keySet().iterator(); it.hasNext();) {
			AllocNode an = it.next();
			if ( an instanceof ClassConstantNode ) continue;
			if ( an instanceof StringConstantNode ) continue;
			qt = qn.find_points_to(an);
			if (qt == null) continue;
			pt = find_points_to(an);

			for (i = 0; i < GeometricManager.Divisions; ++i) {
				p = pt[i];
				while (p != null) {
					for (j = 0; j < GeometricManager.Divisions; ++j) {
						q = qt[j];
						while (q != null) {
							if ( localToSameMethod ) {
								// We can use a more precise alias testing
								if ( p.intersect(q) ) return true;
							}
							else {
								if ( p.projYIntersect(q) ) return true;
							}
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
	public void print_context_sensitive_points_to(PrintStream outPrintStream) 
	{
		for (Iterator<AllocNode> it = pt_objs.keySet().iterator(); it.hasNext();) {
			AllocNode obj = it.next();		
			SegmentNode[] int_entry = find_points_to( obj );
			
			for (int j = 0; j < GeometricManager.Divisions; ++j) {
				SegmentNode p = int_entry[j];
				while (p != null) {
					outPrintStream.print("(" + obj.toString() + ", " + p.I1 + ", "
							+ p.I2 + ", " + p.L + ", " );
					if ( p instanceof RectangleNode )
						outPrintStream.print( ((RectangleNode)p).L_prime + ", " );
					
					outPrintStream.println( symbols[j] + ")");
					p = p.next;
				}
			}
		}
	}

	/**
	 *  We transfer the SPARK results to current pointer if this pointer is not involved in the geometric analysis.
	 *  Note that, the unreachable objects will not be inserted.
	 */
	@Override
	public void injectPts()
	{
		final GeomPointsTo geomPTA = (GeomPointsTo)Scene.v().getPointsToAnalysis();
		pt_objs = new HashMap<AllocNode, GeometricManager>();
		
		me.getP2Set().forall( new P2SetVisitor() {
			@Override
			public void visit(Node n) {
				if ( geomPTA.isValidGeometricNode(n) )
					pt_objs.put((AllocNode)n, (GeometricManager)stubManager);
			}
		});
		
		new_pts = null;
	}
	
	@Override
	public boolean pointer_interval_points_to(long l, long r, AllocNode obj) 
	{	
		SegmentNode[] int_entry = find_points_to(obj);
		
		for ( int i = 0; i < GeometricManager.Divisions; ++i ) {
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
	public void get_all_context_sensitive_objects( long l, long r, PtSensVisitor visitor ) 
	{	
		if ( parent != this ) {
			getRepresentative().get_all_context_sensitive_objects(l, r, visitor);
			return;
		}
		
		GeomPointsTo geomPTA = (GeomPointsTo)Scene.v().getPointsToAnalysis();
		
		for ( Map.Entry<AllocNode, GeometricManager> entry : pt_objs.entrySet() ) {
			AllocNode obj = entry.getKey();
			SootMethod sm = obj.getMethod();
			int sm_int = geomPTA.getIDFromSootMethod(sm);
			if ( sm_int == -1 ) continue;
			
			GeometricManager gm = entry.getValue();
			SegmentNode[] int_entry = gm.getFigures();
			
			for ( int i = 0; i < GeometricManager.Divisions; ++i ) {
				// We iterate all the figures
				SegmentNode p = int_entry[i];
				
				while ( p != null ) {
					long L = p.I1;
					long R = L + p.L;
					long objL = -1, objR = -1;
					
					// Now we compute which context sensitive objects are pointed to by this pointer
					if ( l <= L && L < r ) {
						// l----------r
						//    L----R            or
						//    L------------R
						if ( i == GeometricManager.ONE_TO_ONE ) {
							long d = r - L;
							if ( R < r ) d = p.L;
							objL = p.I2;
							objR = objL + d;
						}
						else {
							objL = p.I2;
							objR = p.I2 + ((RectangleNode)p).L_prime;
						}
					}
					else if (L <= l && l < R) {
						//     l---------r
						// L-------R                or
						// L--------------------R
						if ( i == GeometricManager.ONE_TO_ONE ) {
							long d = R - l;
							if ( R > r ) d = r - l;
							objL = p.I2 + l - L;
							objR = objL + d;
						}
						else {
							objL = p.I2;
							objR = p.I2 + ((RectangleNode)p).L_prime;
						}
					}
					
					// Now we test which context versions this interval [objL, objR) maps to
					if ( objL != -1 && objR != -1 )
						visitor.visit(obj, objL, objR, sm_int);
					
					p = p.next; 
				}
			}
		}
	}

	@Override
	public int count_new_pts_intervals() 
	{
		int ans = 0;
		
		for ( GeometricManager gm : new_pts.values() ) {
			SegmentNode[] int_entry = gm.getFigures();
			for ( int i = 0; i < GeometricManager.Divisions; ++i ) {
				SegmentNode p = int_entry[i];
				while ( p != null && p.is_new == true ) {
					++ans;
					p = p.next;
				}
			}
		}
		
		return ans;
	}
	
	
	// -----------------------------------Private Functions---------------------------------------
	/**
	 * A non-interface public function.
	 * It adds the points-to tuple to the geometric manager.
	 */
	private boolean addPointsTo(int code, AllocNode obj) 
	{
		GeometricManager gm = pt_objs.get(obj);
		
		if ( gm == null ) {
			gm = new GeometricManager();
			pt_objs.put(obj, gm);
		}
		else if ( gm == deadManager ) {
			// We preclude the propagation of this object
			return false;
		}
		
		SegmentNode p = gm.addNewFigure( code, pres );
		if ( p != null ) {
			new_pts.put(obj, gm);
			return true;
		}
		
		return false;
	}

	/**
	 * A non-interface public function.
	 * It adds the flows-to tuple to the geometric manager.
	 */
	private boolean addFlowsTo(int code, IVarAbstraction qv) 
	{
		GeometricManager gm = flowto.get(qv);
		
		if ( gm == null ) {
			gm = new GeometricManager();
			flowto.put( (FullSensitiveNode)qv, gm);
		}
		
		if (gm.addNewFigure( code, pres ) != null) {
			return true;
		}
		
		return false;
	}
	
	private void do_pts_interval_merge()
	{
		for ( GeometricManager gm : new_pts.values() ) {
			gm.mergeFigures( Parameters.max_pts_budget );
		}
	}
	
	private void do_flow_edge_interval_merge()
	{
		for ( GeometricManager gm : flowto.values() ) {
			gm.mergeFigures( Parameters.max_cons_budget );
		}
	}
	
	private SegmentNode[] find_flowto(FullSensitiveNode qv) {
		GeometricManager im = flowto.get(qv);
		return im == null ? null : im.getFigures();
	}

	private SegmentNode[] find_points_to(AllocNode obj) {
		GeometricManager im = pt_objs.get(obj);
		return im == null ? null : im.getFigures();
	}
	
	/**
	 * Implement the inference rules when the input points-to figure is a one-to-one mapping.
	 */
	private static int infer_pts_is_one_to_one( SegmentNode pts, SegmentNode pe, int code )
	{
		long interI, interJ;
		
		// The left-end is the larger one
		interI = pe.I1 < pts.I1 ? pts.I1 : pe.I1;
		// The right-end is the smaller one
		interJ = (pe.I1 + pe.L < pts.I1 + pts.L ? pe.I1 + pe.L : pts.I1 + pts.L);
		
		if ( interI < interJ ) {
			switch ( code ) {
			case GeometricManager.ONE_TO_ONE:
				// assignment is a 1-1 mapping
				pres.I1 = interI - pe.I1 + pe.I2;
				pres.I2 = interI - pts.I1 + pts.I2;
				pres.L = interJ - interI;
				return GeometricManager.ONE_TO_ONE;
				
			case GeometricManager.MANY_TO_MANY:
				// assignment is a many-many mapping
				pres.I1 = pe.I2;
				pres.I2 = interI - pts.I1 + pts.I2;
				pres.L = ((RectangleNode)pe).L_prime;
				pres.L_prime = interJ - interI;
				return GeometricManager.MANY_TO_MANY;
			}
		}
		
		return GeometricManager.Undefined_Mapping;
	}
	
	/**
	 * Implement the inference rules when the input points-to figure is a many-to-many mapping.
	 */
	private static int infer_pts_is_many_to_many( RectangleNode pts, SegmentNode pe, int code )
	{
		long interI, interJ;
		
		// The left-end is the larger one
		interI = pe.I1 < pts.I1 ? pts.I1 : pe.I1;
		// The right-end is the smaller one
		interJ = (pe.I1 + pe.L < pts.I1 + pts.L ? pe.I1 + pe.L : pts.I1 + pts.L);
		
		if ( interI < interJ ) {
			switch ( code ) {
			case GeometricManager.ONE_TO_ONE:
				// assignment is a 1-1 mapping
				pres.I1 = interI - pe.I1 + pe.I2;
				pres.I2 = pts.I2;
				pres.L = interJ - interI;
				pres.L_prime = pts.L_prime;
				break;
				
			case GeometricManager.MANY_TO_MANY:
				// assignment is a many-many mapping
				pres.I1 = pe.I2;
				pres.I2 = pts.I2;
				pres.L = ((RectangleNode)pe).L_prime;
				pres.L_prime = pts.L_prime;
				break;
			}
			
			return GeometricManager.MANY_TO_MANY;
		}
		
		return GeometricManager.Undefined_Mapping;
	}
	
	/**
	 * Implements the pointer assignment inference rules.
	 * The pts and pe are the points-to tuple and flow edge
	 * pres is the computed result
	 * code indicates the types of the pts and pe
	 * 
	 * Return value is used to indicate the type of the result
	 */
	private static boolean reasonAndPropagate( FullSensitiveNode qn, AllocNode obj, 
			SegmentNode pts, SegmentNode pe, int code )
	{
		int ret_type = GeometricManager.Undefined_Mapping;
		
		switch ( code >> 8 ) {
		case GeometricManager.ONE_TO_ONE:
			// points-to is a 1-1 mapping
			ret_type = infer_pts_is_one_to_one(pts, pe, code & 255 );
			break;
			
		case GeometricManager.MANY_TO_MANY:
			// points-to is a mangy-many mapping
			ret_type = infer_pts_is_many_to_many((RectangleNode)pts, pe, code & 255 );
			break;
		}
		
		if (ret_type != GeometricManager.Undefined_Mapping)
			return qn.addPointsTo( ret_type, obj );
		
		return false;
	}
	
	/**
	 * The last parameter code can only be 1-1 and many-1
	 */
	private static boolean instantiateLoadConstraint(FullSensitiveNode objn,
			FullSensitiveNode qn, SegmentNode pts, int code ) 
	{
		int ret_type = GeometricManager.Undefined_Mapping;
		
		if ( (code>>8) == GeometricManager.ONE_TO_ONE ) {
			// assignment is a 1-1 mapping
			
			pres.I1 = pts.I2;
			pres.I2 = pts.I1;
			
			switch ( code & 255 ) {
			case GeometricManager.ONE_TO_ONE:
				// points-to is a 1-1 mapping
				pres.L = pts.L;
				ret_type = GeometricManager.ONE_TO_ONE;
				break;
				
			case GeometricManager.MANY_TO_MANY:
				// points-to is a many-many mapping
				pres.L = ((RectangleNode)pts).L_prime;
				pres.L_prime = pts.L;
				ret_type = GeometricManager.MANY_TO_MANY;
				break;
			}
		}
		else {
			// The target pointer must be a global, in JIMPLE's case
			pres.I1 = pts.I2;
			pres.I2 = 1;
			pres.L_prime = 1;
			
			switch ( code & 255 ) {
			case GeometricManager.ONE_TO_ONE:
				// points-to is a 1-1 mapping or 1-many mapping	
				pres.L = pts.L;
				ret_type = GeometricManager.MANY_TO_MANY;
				break;
				
			case GeometricManager.MANY_TO_MANY:
				// points-to is a many-many mapping
				pres.L = ((RectangleNode)pts).L_prime;
				ret_type = GeometricManager.MANY_TO_MANY;
				break;
			}
		}
		
		return objn.addFlowsTo(ret_type, qn);
	}

	// code can only be 1-1 and 1-many
	private static boolean instantiateStoreConstraint(FullSensitiveNode qn,
			FullSensitiveNode objn, SegmentNode pts, int code) 
	{
		int ret_type = GeometricManager.Undefined_Mapping;
		
		if ( (code>>8) == GeometricManager.ONE_TO_ONE ) {
			// assignment is a 1-1 mapping
			
			pres.I1 = pts.I1;
			pres.I2 = pts.I2;
			pres.L = pts.L;
			
			switch ( code & 255 ) {
			case GeometricManager.ONE_TO_ONE:
				// points-to is a 1-1 mapping			
				ret_type = GeometricManager.ONE_TO_ONE;
				break;
				
			case GeometricManager.MANY_TO_MANY:
				// points-to is a many-many mapping
				pres.L_prime = ((RectangleNode)pts).L_prime;
				ret_type = GeometricManager.MANY_TO_MANY;
				break;
			}
		}
		else {
			// The source pointer must be a global, in JIMPLE's case
			pres.I1 = 1;
			pres.I2 = pts.I2;
			pres.L = 1;
			
			switch ( code & 255 ) {
			case GeometricManager.ONE_TO_ONE:
				// points-to is a 1-1 mapping			
				pres.L_prime = pts.L;
				ret_type = GeometricManager.MANY_TO_MANY;
				break;
				
			case GeometricManager.MANY_TO_MANY:
				// points-to is a many-many mapping
				pres.L_prime = ((RectangleNode)pts).L_prime;
				ret_type = GeometricManager.MANY_TO_MANY;
				break;
			}
		}

		return qn.addFlowsTo(ret_type, objn);
	}
}
