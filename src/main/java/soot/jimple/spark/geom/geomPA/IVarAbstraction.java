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

import soot.SootMethod;
import soot.Type;
import soot.jimple.spark.geom.dataMgr.PtSensVisitor;
import soot.jimple.spark.geom.dataRep.PlainConstraint;
import soot.jimple.spark.geom.dataRep.RectangleNode;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.Node;
import soot.util.Numberable;

/**
 * Pointer/object representation in geomPTA.
 * This interface defines the operations needed for manipulating a pointer/object.
 * 
 * @author xiao
 *
 */
public abstract class IVarAbstraction implements Numberable 
{	
	// A shape manager that has only one all map to all member, representing the context insensitive points-to info
	protected static IFigureManager stubManager = null;
	// This is used to indicate the corresponding object should be removed
	protected static IFigureManager deadManager = null;
	// A temporary rectangle holds the candidate figure 
	protected static RectangleNode pres = null;
	
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
	
	/**
	 * Used by ordering the nodes in priority worklist.
	 */
	public boolean lessThan( IVarAbstraction other )
	{
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
	
	/**
	 * This pointer/object is reachable if its enclosing method is reachable.
	 * Pleas always call this method to check the status before querying points-to information.
	 */
	public boolean reachable()
	{
		return id != -1;
	}
	
	/**
	 * Test if this pointer currently has points-to result.
	 * The result can be computed in the last iteration of geomPTA, although its willUpdate = false this round.
	 */
	public boolean hasPTResult()
	{
		return num_of_diff_objs() != -1;
	}
	
	/**
	 * Processing the wrapped SPARK node.
	 */
	public Node getWrappedNode()
	{
		return me;
	}
	
	public Type getType()
	{
		return me.getType();
	}
	
	public boolean isLocalPointer()
	{
		return me instanceof LocalVarNode;
	}
	
	public SootMethod enclosingMethod()
	{
		if ( me instanceof LocalVarNode )
			return ((LocalVarNode) me).getMethod();
		return null;
	}
	
	// Initiation
	public abstract boolean add_points_to_3( AllocNode obj, long I1, long I2, long L );
	public abstract boolean add_points_to_4( AllocNode obj, long I1, long I2, long L1, long L2 );
	public abstract boolean add_simple_constraint_3( IVarAbstraction qv, long I1, long I2, long L );
	public abstract boolean add_simple_constraint_4( IVarAbstraction qv, long I1, long I2, long L1, long L2 );
	public abstract void put_complex_constraint(PlainConstraint cons);
	public abstract void reconstruct();
	
	
	// Points-to facts propagation
	public abstract void do_before_propagation();
	public abstract void do_after_propagation();
	public abstract void propagate(GeomPointsTo ptAnalyzer, IWorklist worklist);
	
	
	// Manipulate points-to results
	public abstract void drop_duplicates();
	public abstract void remove_points_to( AllocNode obj );
	public abstract void deleteAll();
	public abstract void keepPointsToOnly();
	public abstract void injectPts();
	
	
	// Obtaining points-to information statistics
	/**
	 * Return -1 if this pointer does not have points-to information.
	 * This function can be used for testing if the pointer has been processed by geomPTA.
	 */
	public abstract int num_of_diff_objs();
	public abstract int num_of_diff_edges();
	public abstract int count_pts_intervals( AllocNode obj );
	public abstract int count_new_pts_intervals();
	public abstract int count_flow_intervals( IVarAbstraction qv );
	
	
	// Querying procedures
	/**
	 * Perform context sensitive alias checking with qv.
	 * @param qv
	 * @return
	 */
	public abstract boolean heap_sensitive_intersection( IVarAbstraction qv );
	/**
	 * Test if the pointer in the context range [l, R) points to object obj.
	 * @param l
	 * @param r
	 * @param obj
	 * @return
	 */
	public abstract boolean pointer_interval_points_to( long l, long r, AllocNode obj);
	/**
	 * Test if the particular object has been obsoleted.
	 * It's mainly for points-to developer use.
	 * @param obj
	 * @return
	 */
	public abstract boolean isDeadObject( AllocNode obj );
	

	/**
	 * Obtain context insensitive points-to result (by removing contexts).
	 * @return
	 */
	public abstract Set<AllocNode> get_all_points_to_objects();
	/**
	 * Given the pointers falling in the context range [l, r), we compute the set of context sensitive objects pointed to by those pointers.
	 * This function is designed in visitor pattern.
	 * @see Obj_1cfa_extractor
	 * @see Obj_full_extractor
	 */
	public abstract void get_all_context_sensitive_objects( long l, long r, PtSensVisitor visitor);
	
	
	// Debugging facilities
	public abstract void print_context_sensitive_points_to( PrintStream outPrintStream );
}
