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

import java.io.PrintStream;
import java.util.Set;
import java.util.Vector;

import soot.Type;
import soot.jimple.spark.geom.geomE.GeometricManager;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.util.Numberable;

/**
 * An interface makes the points-to solver automatically adapt to different kind of encodings.
 * This interface defines the operations that are needed for manipulating a variable (pointer/object).
 * @author richardxx
 *
 */
public abstract class IVarAbstraction implements Numberable {
	
	// Used for the context insensitive points-to information recharged from SPARK
	protected static GeometricManager stubManager;
	protected static RectangleNode pres;
	
	static {
		stubManager = new GeometricManager();
		pres = new RectangleNode(1, 1, GeomPointsTo.MAX_CONTEXTS, GeomPointsTo.MAX_CONTEXTS);
		stubManager.addNewObject(GeomPointsTo.MANY_TO_MANY, pres);
	}
	
	// Corresponding SPARK node
	public Node me;
	// The integer mapping for this node
	public int id = -1;
	// Position in the queue
	public int Qpos = 0;
	// top_value: the topological value for this node on the symbolic assignment graph
	// lrf_value: the least recently fired time for this node
	public int top_value = Integer.MIN_VALUE, lrf_value = 0;
	// union-find tree link
	private IVarAbstraction parent;
	
	public IVarAbstraction()
	{
		parent = this;
	}
	
	public Node getWrappedNode()
	{
		return me;
	}
	
	public Type getType()
	{
		return me.getType();
	}
	
	public boolean lessThan( IVarAbstraction other )
	{
		if ( lrf_value != other.lrf_value ) {
			return lrf_value < other.lrf_value;
		}
		
		return top_value < other.top_value;
	}
	
	public IVarAbstraction getRepresentative()
	{
		return parent == this ? this : (parent = parent.getRepresentative());
	}
	
	public IVarAbstraction merge( IVarAbstraction other )
	{
		other = other.getRepresentative();
		other.parent = getRepresentative();
		return parent;
	}
	
	
	public void setNumber( int number )
	{
		id = number;
	}
	
	
    public int getNumber()
    {
    	return id;
    }
    
	// Initiation
	public abstract boolean add_points_to_3( AllocNode obj, long I1, long I2, long L );
	public abstract boolean add_points_to_4( AllocNode obj, long I1, long I2, long L1, long L2 );
	public abstract boolean add_simple_constraint_3( IVarAbstraction qv, long I1, long I2, long L );
	public abstract boolean add_simple_constraint_4( IVarAbstraction qv, long I1, long I2, long L1, long L2 );
	public abstract void put_complex_constraint(PlainConstraint cons);
	public abstract void reconstruct();
	
	// Points-to analysis core components
	public abstract void do_before_propagation();
	public abstract void do_after_propagation();
	public abstract void propagate(GeomPointsTo ptAnalyzer, IWorklist worklist);
	
	// Points-to post-processing
	public abstract void drop_duplicates();
	public abstract void remove_points_to( AllocNode obj );
	public abstract void discard();
	
	// Querying points-to information
	public abstract boolean is_empty();
	public abstract boolean has_new_pts();
	public abstract int num_of_diff_objs();
	public abstract int num_of_diff_edges();
	public abstract int count_pts_intervals( AllocNode obj );
	public abstract int count_new_pts_intervals();
	public abstract int count_flow_intervals( IVarAbstraction qv );
	public abstract boolean heap_sensitive_intersection( IVarAbstraction qv );
	public abstract boolean pointer_sensitive_points_to( long context, AllocNode obj );
	public abstract boolean pointer_interval_points_to( long l, long r, AllocNode obj);
	public abstract boolean test_points_to_has_types( Set<Type> types );
	
	// Querying large bulk of data
	public abstract Set<AllocNode> get_all_points_to_objects();
	public abstract int get_all_context_sensitive_objects( long l, long r, ZArrayNumberer<CallsiteContextVar> all_objs, Vector<CallsiteContextVar> outList);
	
	// Debugging facilities
	public abstract void print_context_sensitive_points_to( PrintStream outPrintStream );
}
