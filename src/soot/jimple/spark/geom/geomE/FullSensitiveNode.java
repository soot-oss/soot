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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import soot.Scene;
import soot.SootMethod;
import soot.Type;
import soot.jimple.spark.geom.geomPA.CallsiteContextVar;
import soot.jimple.spark.geom.geomPA.CgEdge;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.IEncodingBroker;
import soot.jimple.spark.geom.geomPA.IVarAbstraction;
import soot.jimple.spark.geom.geomPA.IWorklist;
import soot.jimple.spark.geom.geomPA.PlainConstraint;
import soot.jimple.spark.geom.geomPA.RectangleNode;
import soot.jimple.spark.geom.geomPA.SegmentNode;
import soot.jimple.spark.geom.geomPA.ZArrayNumberer;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.StringConstantNode;
import soot.jimple.spark.sets.P2SetVisitor;


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
	
	public static String symbols[] = {"/", "[]", "|", "-" };
	
	public FullSensitiveNode( Node thisVar )
	{		
		me = thisVar;
		reconstruct();
	}
	
	@Override
	public void reconstruct() 
	{
		flowto = new HashMap<FullSensitiveNode, GeometricManager>();
		pt_objs = new HashMap<AllocNode, GeometricManager>();
		new_pts = new HashMap<AllocNode, GeometricManager>();
		complex_cons = null;
	}

	@Override
	public void discard()
	{
		flowto = null;
		pt_objs = null;
		new_pts = null;
		complex_cons = null;
	}
	
	@Override
	public void do_before_propagation() 
	{
//		if ( complex_cons == null )
			do_pts_interval_merge();
		
//		if ( !(me instanceof LocalVarNode) )
			do_flow_edge_interval_merge();
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
	public boolean is_empty() {
		return pt_objs.size() == 0;
	}

	@Override
	public boolean has_new_pts() {
		return new_pts.size() != 0;
	}

	@Override
	public int num_of_diff_objs() {
		return pt_objs.size();
	}

	@Override
	public int num_of_diff_edges() {
		return flowto.size();
	}

	@Override
	public boolean add_points_to_3(AllocNode obj, long I1, long I2,
			long L) 
	{			
		pres.I1 = I1;
		pres.I2 = I2;
		pres.L = L;

		return add_points_to_tuple(GeomPointsTo.ONE_TO_ONE, obj);
	}

	@Override
	public boolean add_points_to_4(AllocNode obj, long I1, long I2,
			long L1, long L2) 
	{
		pres.I1 = I1;
		pres.I2 = I2;
		pres.L = L1;
		pres.L_prime = L2;
		
		return add_points_to_tuple(GeomPointsTo.MANY_TO_MANY, obj);
	}

	@Override
	public boolean add_simple_constraint_3(IVarAbstraction qv, long I1,
			long I2, long L) 
	{
		pres.I1 = I1;
		pres.I2 = I2;
		pres.L = L;
		
		return add_flow_to_edge(GeomPointsTo.ONE_TO_ONE, qv);
	}

	@Override
	public boolean add_simple_constraint_4(IVarAbstraction qv, long I1,
			long I2, long L1, long L2) 
	{
		pres.I1 = I1;
		pres.I2 = I2;
		pres.L = L1;
		pres.L_prime = L2;
		
		return add_flow_to_edge(GeomPointsTo.MANY_TO_MANY, qv);
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
			gm.remove_useless_lines();
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
		
		// We first build the new flow edges via the field dereferences
		if ( complex_cons != null ) {
			
			for ( Map.Entry<AllocNode, GeometricManager> entry : new_pts.entrySet() ) {
				obj = entry.getKey();
				entry_pts = entry.getValue().getObjects();
				
				for ( PlainConstraint pcons : complex_cons ) {
					// For each newly points-to object, construct its instance field
					qn = (FullSensitiveNode) pcons.otherSide;
					objn = (FullSensitiveNode)ptAnalyzer.findAndInsertInstanceField(obj, pcons.f);
					
					for ( i = 0; i < GeometricManager.Divisions; ++i ) {
						pts = entry_pts[i];
						
						while ( pts != null && pts.is_new == true ) {
							switch ( pcons.type ) {
							case GeomPointsTo.STORE_CONS:
								// Store, qv -> pv.field
								if ( instantiateStoreConstraint( qn, objn, pts, (pcons.code<<8) | i ) )
									worklist.push( qn );
								break;
								
							case GeomPointsTo.LOAD_CONS:
								// Load, pv.field -> qv
								if ( instantiateLoadConstraint( objn, qn, pts, (pcons.code<<8) | i ) )
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
		
		// First, we get the flow-to shapes
		for (Map.Entry<FullSensitiveNode, GeometricManager> entry1 : flowto.entrySet()) {	
			added = false;
			qn = entry1.getKey();
			gm1 = entry1.getValue();
			entry_pe = gm1.getObjects();
			
			// We specialize the two cases that we hope it running faster
			// We have new flow-to edges
			if ( gm1.isThereUnprocessedObject() ) {
				
				// Second, we get the points-to shapes
				for ( Map.Entry<AllocNode, GeometricManager> entry2 : pt_objs.entrySet() ) {
					obj = entry2.getKey();
					// Type filtering and flow-to-this filtering, a simple approach
					if (!ptAnalyzer.castNeverFails(obj.getType(), qn.getType()))
						continue;
							
					gm2 = entry2.getValue();
					entry_pts = gm2.getObjects();
					hasNewPointsTo = gm2.isThereUnprocessedObject();
					
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
					// Type filtering and flow-to-this filtering, a simple approach
					if (!ptAnalyzer.castNeverFails(obj.getType(), qn.getType()))
						continue;
							
					gm2 = entry2.getValue();
					entry_pts = gm2.getObjects();
					
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
	}

	public boolean add_points_to_tuple(int code, AllocNode obj) 
	{
		GeometricManager gm = pt_objs.get(obj);
		
		if ( gm == null ) {
			gm = new GeometricManager();
			pt_objs.put(obj, gm);
		}
		
		SegmentNode p = gm.addNewObject( code, pres );
		if ( p != null ) {
			new_pts.put(obj, gm);
			++IEncodingBroker.n_added_pts;
			return true;
		}
		
		return false;
	}

	public boolean add_flow_to_edge(int code, IVarAbstraction qv) 
	{
		GeometricManager gm = flowto.get(qv);
		
		if ( gm == null ) {
			gm = new GeometricManager();
			flowto.put( (FullSensitiveNode)qv, gm);
		}
		
		if (gm.addNewObject( code, pres ) != null) {
			++IEncodingBroker.n_added_flowedge;
			return true;
		}
		
		return false;
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
		
		qn = (FullSensitiveNode)qv;
		
		for (Iterator<AllocNode> it = pt_objs.keySet().iterator(); it.hasNext();) {
			AllocNode an = it.next();
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
							if ( i <= j ) {
								if ( quick_intersecting_test(p, q, (i<<8) | j ) )
									return true;
							}
							else {
								if ( quick_intersecting_test(q, p, (j<<8) | i ) )
									return true;
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

	// We test all combinations
	private boolean quick_intersecting_test(SegmentNode p, SegmentNode q, int code) 
	{
		RectangleNode rect_q, rect_p;
		
		switch (code>>8) {
		case GeomPointsTo.ONE_TO_ONE:
			switch (code&255) {
			case GeomPointsTo.ONE_TO_ONE:
				if ( (p.I2 - p.I1) == (q.I2 - q.I1) ) {
					if ( p.I1 < (q.I1 + q.L) && q.I1 < (p.I1 + p.L) )
						return true;
				}
				break;
				
			case GeomPointsTo.MANY_TO_MANY:
				rect_q = (RectangleNode)q;
				
				// If one of the end point is in the body of the rectangle
				if ( point_within_rectangle(p.I1, p.I2, rect_q) ||
						point_within_rectangle(p.I1 + p.L - 1, p.I2 + p.L - 1, rect_q) )
					return true;
				
				// Otherwise, the diagonal line must intersect with one of the boundary lines
				if ( diagonal_line_intersect_horizontal(p, rect_q.I1, rect_q.I2, rect_q.L) ||
						diagonal_line_intersect_horizontal(p, rect_q.I1, rect_q.I2 + rect_q.L_prime - 1, rect_q.L) ||
						diagonal_line_intersect_vertical(p, rect_q.I1, rect_q.I2 , rect_q.L_prime) ||
						diagonal_line_intersect_vertical(p, rect_q.I1 + rect_q.L - 1, rect_q.I2, rect_q.L_prime) )
					return true;
				break;
			}
			
			break;
			
		case GeomPointsTo.MANY_TO_MANY:
			rect_p = (RectangleNode)p;
			rect_q = (RectangleNode)q;
			
			// If p is not entirely above, below, to the left, to the right of q
			// then, p and q must intersect
			
			if ( rect_p.I2 >= rect_q.I2 + rect_q.L_prime )
				return false;
			
			if ( rect_p.I2 + rect_p.L_prime <= rect_q.I2 )
				return false;
			
			if ( rect_p.I1 + rect_p.L <= rect_q.I1 )
				return false;
			
			if ( rect_p.I1 >= rect_q.I1 + rect_q.L )
				return false;
			
			return true;
		}
		
		return false;
	}
	
	private boolean point_within_rectangle( long x, long y, RectangleNode rect )
	{
		if ( x >= rect.I1 && x < rect.I1 + rect.L )
			if ( y >= rect.I2 && y < rect.I2 + rect.L_prime )
				return true;
		
		return false;
	}
	
	private boolean diagonal_line_intersect_vertical( SegmentNode p, long x, long y, long L)
	{
		if ( x >= p.I1 && x < (p.I1 + p.L) ) {
			long y_cross = x - p.I1 + p.I2;
			if ( y_cross >= y && y_cross < y + L )
				return true;
		}
		
		return false;
	}
	
	private boolean diagonal_line_intersect_horizontal( SegmentNode p, long x, long y, long L)
	{
		if ( y >= p.I2 && y < (p.I2 + p.L) ) {
			long x_cross = y - p.I2 + p.I1;
			if ( x_cross >= x && x_cross < x + L )
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean pointer_sensitive_points_to(long context, AllocNode obj) 
	{
		SegmentNode[] int_entry = find_points_to(obj);
		
		for ( int i = 0; i < GeometricManager.Divisions; ++i ) {
			SegmentNode p = int_entry[i];
			while ( p != null ) {
				if ( context >= p.I1 && context < p.I1 + p.L )
					return true;
				p = p.next;
			}
		}
		
		return false;
	}

	@Override
	public boolean test_points_to_has_types(Set<Type> types) 
	{
		for (Iterator<AllocNode> it = pt_objs.keySet().iterator(); it.hasNext();) {
			AllocNode an = it.next();
			if (types.contains( an.getType() ) ) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public Set<AllocNode> get_all_points_to_objects() 
	{
		if ( top_value == Integer.MIN_VALUE ) {
			// This pointer is considered useless for application code
			// We construct its points-to set from SPARK
			me.getP2Set().forall( new P2SetVisitor() {
				@Override
				public void visit(Node n) {
					pt_objs.put((AllocNode)n, stubManager);
				}
			});
			
			top_value = Integer.MAX_VALUE;
			new_pts = null;
		}
		
		if ( pt_objs == null )
			System.err.println();
		
		return pt_objs.keySet();
	}

	@Override
	public void print_context_sensitive_points_to(PrintStream outPrintStream) 
	{
		for (Iterator<AllocNode> it = new_pts.keySet().iterator(); it.hasNext();) {
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

	private void do_pts_interval_merge()
	{
		for ( GeometricManager gm : new_pts.values() ) {
			gm.mergeObjects( GeomPointsTo.max_pts_budget );
		}
	}
	
	private void do_flow_edge_interval_merge()
	{
		for ( Map.Entry<FullSensitiveNode, GeometricManager> entry : flowto.entrySet() ) {
			GeometricManager gm = entry.getValue();
			if ( gm.isThereUnprocessedObject() )
				gm.mergeObjects( GeomPointsTo.max_cons_budget );
		}
	}
	
	private SegmentNode[] find_flowto(FullSensitiveNode qv) {
		GeometricManager im = flowto.get(qv);
		return im == null ? null : im.getObjects();
	}

	private SegmentNode[] find_points_to(AllocNode obj) {
		GeometricManager im = pt_objs.get(obj);
		return im == null ? null : im.getObjects();
	}
	
	private int infer_pts_is_one_to_one( SegmentNode pts, SegmentNode pe, int code )
	{
		long interI, interJ;
		
		// The left-end is the larger one
		interI = pe.I1 < pts.I1 ? pts.I1 : pe.I1;
		// The right-end is the smaller one
		interJ = (pe.I1 + pe.L < pts.I1 + pts.L ? pe.I1 + pe.L : pts.I1 + pts.L);
		
		if ( interI < interJ ) {
			switch ( code ) {
			case GeomPointsTo.ONE_TO_ONE:
				// pe is a 1-1 mapping
				pres.I1 = interI - pe.I1 + pe.I2;
				pres.I2 = interI - pts.I1 + pts.I2;
				pres.L = interJ - interI;
				return GeomPointsTo.ONE_TO_ONE;
				
			case GeomPointsTo.MANY_TO_MANY:
				// pe is a many-many mapping
				pres.I1 = pe.I2;
				pres.I2 = interI - pts.I1 + pts.I2;
				pres.L = ((RectangleNode)pe).L_prime;
				pres.L_prime = interJ - interI;
				return GeomPointsTo.MANY_TO_MANY;
			}
		}
		
		return GeomPointsTo.Undefined_Mapping;
	}
	
	private int infer_pts_is_many_to_many( RectangleNode pts, SegmentNode pe, int code )
	{
		long interI, interJ;
		
		// The left-end is the larger one
		interI = pe.I1 < pts.I1 ? pts.I1 : pe.I1;
		// The right-end is the smaller one
		interJ = (pe.I1 + pe.L < pts.I1 + pts.L ? pe.I1 + pe.L : pts.I1 + pts.L);
		
		if ( interI < interJ ) {
			switch ( code ) {
			case GeomPointsTo.ONE_TO_ONE:
				// pe is a 1-1 mapping
				pres.I1 = interI - pe.I1 + pe.I2;
				pres.I2 = pts.I2;
				pres.L = interJ - interI;
				pres.L_prime = pts.L_prime;
				break;
				
			case GeomPointsTo.MANY_TO_MANY:
				// pe is a many-many mapping
				pres.I1 = pe.I2;
				pres.I2 = pts.I2;
				pres.L = ((RectangleNode)pe).L_prime;
				pres.L_prime = pts.L_prime;
				break;
			}
			
			return GeomPointsTo.MANY_TO_MANY;
		}
		
		return GeomPointsTo.Undefined_Mapping;
	}
	
	/**
	 * Implements the pointer assignment inference rules.
	 * The pts and pe are the points-to tuple and flow edge
	 * pres is the computed result
	 * code indicates the types of the pts and pe
	 * 
	 * Return value is used to indicate the type of the result
	 */
	private boolean reasonAndPropagate( FullSensitiveNode qn, AllocNode obj, SegmentNode pts, SegmentNode pe, int code )
	{
		int ret_type = GeomPointsTo.Undefined_Mapping;
		
		switch ( code >> 8 ) {
		case GeomPointsTo.ONE_TO_ONE:
			// pts is a 1-1 mapping
			ret_type = infer_pts_is_one_to_one(pts, pe, code & 255 );
			break;
			
		case GeomPointsTo.MANY_TO_MANY:
			// pts is a mangy-many mapping
			ret_type = infer_pts_is_many_to_many((RectangleNode)pts, pe, code & 255 );
			break;
		}
		
		if (ret_type != GeomPointsTo.Undefined_Mapping)
			return qn.add_points_to_tuple( ret_type, obj );
		
		return false;
	}
	
	// flow edge mapping can only be 1-1 and many-1
	private boolean instantiateLoadConstraint(FullSensitiveNode objn,
			FullSensitiveNode qn, SegmentNode pts, int code ) 
	{
		int ret_type = GeomPointsTo.Undefined_Mapping;
		
		if ( (code>>8) == GeomPointsTo.ONE_TO_ONE ) {
			// pe is a 1-1 mapping
			
			pres.I1 = pts.I2;
			pres.I2 = pts.I1;
			
			switch ( code & 255 ) {
			case GeomPointsTo.ONE_TO_ONE:
				// pts is a 1-1 mapping
				pres.L = pts.L;
				ret_type = GeomPointsTo.ONE_TO_ONE;
				break;
				
			case GeomPointsTo.MANY_TO_MANY:
				// pts is a many-many mapping
				pres.L = ((RectangleNode)pts).L_prime;
				pres.L_prime = pts.L;
				ret_type = GeomPointsTo.MANY_TO_MANY;
				break;
			}
		}
		else {
			// The target pointer must be a global, in JIMPLE's case
			pres.I1 = pts.I2;
			pres.I2 = 1;
			pres.L_prime = 1;
			
			switch ( code & 255 ) {
			case GeomPointsTo.ONE_TO_ONE:
				// pts is a 1-1 mapping or 1-many mapping	
				pres.L = pts.L;
				ret_type = GeomPointsTo.MANY_TO_MANY;
				break;
				
			case GeomPointsTo.MANY_TO_MANY:
				// pts is a many-many mapping
				pres.L = ((RectangleNode)pts).L_prime;
				ret_type = GeomPointsTo.MANY_TO_MANY;
				break;
			}
		}
		
//		assert ret_type != IntervalPointsTo.Undefined_Mapping;
//		if ( !( pres.I1 != 0 && pres.I2 != 0 && pres.L > 0 ) )
//			assert false;
		
		return objn.add_flow_to_edge(ret_type, qn);
	}

	// code can only be 1-1 and 1-many
	private boolean instantiateStoreConstraint(FullSensitiveNode qn,
			FullSensitiveNode objn, SegmentNode pts, int code) 
	{
		int ret_type = GeomPointsTo.Undefined_Mapping;
		
		if ( (code>>8) == GeomPointsTo.ONE_TO_ONE ) {
			// pe is a 1-1 mapping
			
			pres.I1 = pts.I1;
			pres.I2 = pts.I2;
			pres.L = pts.L;
			
			switch ( code & 255 ) {
			case GeomPointsTo.ONE_TO_ONE:
				// pts is a 1-1 mapping			
				ret_type = GeomPointsTo.ONE_TO_ONE;
				break;
				
			case GeomPointsTo.MANY_TO_MANY:
				// pts is a many-many mapping
				pres.L_prime = ((RectangleNode)pts).L_prime;
				ret_type = GeomPointsTo.MANY_TO_MANY;
				break;
			}
		}
		else {
			// The source pointer must be a global, in JIMPLE's case
			pres.I1 = 1;
			pres.I2 = pts.I2;
			pres.L = 1;
			
			switch ( code & 255 ) {
			case GeomPointsTo.ONE_TO_ONE:
				// pts is a 1-1 mapping			
				pres.L_prime = pts.L;
				ret_type = GeomPointsTo.MANY_TO_MANY;
				break;
				
			case GeomPointsTo.MANY_TO_MANY:
				// pts is a many-many mapping
				pres.L_prime = ((RectangleNode)pts).L_prime;
				ret_type = GeomPointsTo.MANY_TO_MANY;
				break;
			}
		}

		return qn.add_flow_to_edge(ret_type, objn);
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

	/**
	 * Given the pointers falling in the context range [l, r), we compute the set of 1-CFA objects pointed to by those pointers.
	 */
	@Override
	public int get_all_context_sensitive_objects( long l, long r, 
			ZArrayNumberer<CallsiteContextVar> all_objs, Vector<CallsiteContextVar> outList ) 
	{
		GeomPointsTo ptsProvider = (GeomPointsTo)Scene.v().getPointsToAnalysis();
		CallsiteContextVar cobj = new CallsiteContextVar();
		CallsiteContextVar res;
		
		
		outList.clear();
		
		for ( Map.Entry<AllocNode, GeometricManager> entry : pt_objs.entrySet() ) {
			AllocNode obj = entry.getKey();
			SootMethod sm = obj.getMethod();
			int sm_int = 0;
			if ( sm != null ) {
				sm_int = ptsProvider.getIDFromSootMethod(sm);
			}
			List<CgEdge> edges = ptsProvider.getCallEdgesInto(sm_int);
			
			GeometricManager gm = entry.getValue();
			SegmentNode[] int_entry = gm.getObjects();
			boolean flag = true;
			cobj.var = obj;
			
			for ( int i = 0; i < GeometricManager.Divisions; ++i ) {
				SegmentNode p = int_entry[i];
				while ( p != null ) {
					long R = p.I1 + p.L;
					long objL = -1, objR = -1;
					
					// Now we compute which context sensitive objects are pointed to by this pointer
					if ( l <= p.I1 && p.I1 < r ) {	
						if ( i == GeomPointsTo.ONE_TO_ONE ) {
							long d = r - p.I1;
							if ( d > p.L ) d = p.L;
							objL = p.I2;
							objR = objL + d;
						}
						else {
							objL = p.I2;
							objR = p.I2 + ((RectangleNode)p).L_prime;
						}
					}
					else if (p.I1 <= l && l < R) {
						if ( i == GeomPointsTo.ONE_TO_ONE ) {
							long d = R - l;
							if ( R > r ) d = r - l;
							objL = p.I2 + l - p.I1;
							objR = objL + d;
						}
						else {
							objL = p.I2;
							objR = p.I2 + ((RectangleNode)p).L_prime;
						}
					}
					
					// Now we test which context versions should this interval [objL, objR) maps to
					if ( objL != -1 && objR != -1 ) {
						if ( edges != null ) {
							for ( CgEdge e : edges ) {
								long rangeL = e.map_offset;
								long rangeR = rangeL + ptsProvider.max_context_size_block[e.s];
								if ( (objL <= rangeL && rangeL < objR) ||
										(rangeL <= objL && objL < rangeR) ) {
									cobj.context = e;
									res = all_objs.searchFor(cobj);
									if ( res.inQ == false ) {
										outList.add(res);
										res.inQ = true;
									}
								}
							}
						}
						else {
							cobj.context = null;
							res = all_objs.searchFor(cobj);
							if ( res.inQ == false ) {
								outList.add(res);
								res.inQ = true;
							}
							flag = false;
							break;
						}
					}
					
					p = p.next; 
				}
				
				if ( flag == false )
					break;
			}
		}
		
		return outList.size();
	}

	@Override
	public int count_new_pts_intervals() 
	{
		int ans = 0;
		
		for ( GeometricManager gm : new_pts.values() ) {
			SegmentNode[] int_entry = gm.getObjects();
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
}
