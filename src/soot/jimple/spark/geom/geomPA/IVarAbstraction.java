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
import soot.Type;
import soot.jimple.spark.geom.helper.PtSensVisitor;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.util.Numberable;

/**
 * An interface makes the points-to solver automatically adapt to different kind of encodings.
 * This interface defines the operations that are needed for manipulating a variable (pointer/object).
 * 
 * @author xiao
 *
 */
public abstract class IVarAbstraction implements Numberable {
	
	// A shape manager that has only one all map to all member, representing the context insensitive points-to info
	protected static IFigureManager stubManager;
	// This is used to indicate the corresponding object should be removed
	protected static IFigureManager deadManager;
	// A temporary rectangle for public use 
	protected static RectangleNode pres;
	
	// Corresponding SPARK node
	public Node me;
	// The integer mapping for this node
	public int id = -1;
	// Position in the queue
	public int Qpos = 0;
	// Will we update the points-to information for this node in the geometric analysis?
	// Because of constraints distillation, not all the pointers will be updated.
	public boolean willUpdate = false;
	// top_value: the topological value for this node on the symbolic assignment graph
	// lrf_value: the number of processing times for this pointer
	// top_value will be modified in the offlineProcessor and every pointer has a different value
	public int top_value = 1, lrf_value = 0;
	// union-find tree link
	protected IVarAbstraction parent;
	
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
		// NEED IMPROVE
		
		if ( lrf_value != other.lrf_value ) 
			return lrf_value < other.lrf_value;
		
		return top_value < other.top_value;
	}
	
	public IVarAbstraction getRepresentative()
	{
		return parent == this ? this : (parent = parent.getRepresentative());
	}
	
	/**
	 * Make the variable other be the parent of this variable.
	 * @param other
	 * @return
	 */
	public IVarAbstraction merge( IVarAbstraction other )
	{
		getRepresentative();
		parent = other.getRepresentative();
		return parent;
	}
	
	@Override
	public void setNumber( int number )
	{
		id = number;
	}
	
	@Override
    public int getNumber()
    {
    	return id;
    }
    
	@Override
	public String toString()
	{
		if ( me != null ) return me.toString();
		return super.toString();
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
	public abstract void deleteAll();
	public abstract void keepPointsToOnly();
	public abstract void injectPts();
	public abstract boolean isDeadObject( AllocNode obj );
	
	// Querying points-to information
	public abstract int num_of_diff_objs();
	public abstract int num_of_diff_edges();
	public abstract int count_pts_intervals( AllocNode obj );
	public abstract int count_new_pts_intervals();
	public abstract int count_flow_intervals( IVarAbstraction qv );
	/**
	 * Check if qv and this pointer form an alias pair context sensitively.
	 * @param qv
	 * @return
	 */
	public abstract boolean heap_sensitive_intersection( IVarAbstraction qv );
	public abstract boolean pointer_sensitive_points_to( long context, AllocNode obj );
	public abstract boolean pointer_interval_points_to( long l, long r, AllocNode obj);
	/**
	 * Determine if any of the points-to object has a type in the given set.
	 * @param types
	 * @return
	 */
	public abstract boolean test_points_to_has_types( Set<Type> types );
	
	// Querying large bulk of data
	public abstract Set<AllocNode> get_all_points_to_objects();
	/**
	 * Given the pointers falling in the context range [l, r), we compute the set of 1-CFA objects pointed to by those pointers.
	 * This function is designed in visitor pattern. The user can define what to do with the generated variables.
	 */
	public abstract void get_all_context_sensitive_objects( long l, long r, PtSensVisitor visitor);
	
	// Debugging facilities
	public abstract void print_context_sensitive_points_to( PrintStream outPrintStream );
}
