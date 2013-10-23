/* Soot - a J*va Optimization Framework
 * Copyright (C) 2012 Richard Xiao
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
package soot.jimple.spark.geom.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.PointsToSet;
import soot.jimple.spark.geom.dataRep.ContextVar;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.VarNode;
import soot.jimple.spark.sets.PointsToSetInternal;

/**
 * A container for storing context sensitive querying result of geomPTA.
 * Similar to the class PointsToSetInternal for SPARK.
 * 
 * This class maintains two views for the results:
 * 1. Table view: every object has a separate list of its context sensitive versions;
 * 2. List view: all context sensitive objects are put in a single list.
 * 
 * 
 * @author xiao
 */
public abstract class PtSensVisitor<VarType extends ContextVar>
{
	// Indicates if this visitor is prepared
	protected boolean readyToUse = false;
	
	// The list view
	public List<VarType> outList = new ArrayList<VarType>();
	
	// The table view (cannot be accessed directly outside)
	protected Map<Node, List<VarType>> tableView = new HashMap<Node, List<VarType>>();
	
	/**
	 * Called before each round of collection.
	 */
	public void prepare()
	{
		tableView.clear();
		readyToUse = false;
	}
	
	/**
	 * Called after each round of collection.
	 */
	public void finish() 
	{
		// We flatten the list
		readyToUse = true;
		outList.clear();
				
		if ( tableView.size() == 0 ) return;
		
		for ( Map.Entry<Node, List<VarType>> entry : tableView.entrySet() ) {
			List<VarType> resList = entry.getValue();
			outList.addAll(resList);
		}
	}
	
	/**
	 * The visitor contains valid information only when this function returns true.
	 * @return
	 */
	public boolean getUsageState() 
	{ 
		return readyToUse; 
	}
	
	/**
	 * Calculate the intersection with other container. 
	 * Can be used to answer alias query.
	 * @param other
	 * @return
	 */
	public boolean hasIntersection(PtSensVisitor<VarType> other) 
	{
		if ( !readyToUse ) finish();
		if ( other.getUsageState() == false ) other.finish();
		
		// Using table view for comparison, that's faster
		for ( Map.Entry<Node, List<VarType>> entry : tableView.entrySet() ) {
			Node var = entry.getKey();
			List<VarType> list1 = entry.getValue();
			List<VarType> list2 = other.getCSList(var);
			
			for ( VarType cv1 : list1 ) {
				for ( VarType cv2 : list2 )
					if ( cv1.intersect(cv2) ) return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Obtain the list of context sensitive objects pointed to by var.
	 * @param var
	 * @return
	 */
	public List<VarType> getCSList(Node var)
	{
		return tableView.get(var);
	}
	
	/**
	 * Transform the result to SPARK style context insensitive points-to set.
	 * The transformed result is stored in the points-to set of the querying pointer.
	 * @param vn: the querying pointer
	 * @return
	 */
	public PointsToSet toSparkCompatiableResult(VarNode vn)
	{
		if ( !readyToUse) finish();
		
		PointsToSetInternal ptset = vn.makeP2Set();
		
		for ( VarType cv : outList ) {
			ptset.add(cv.var);
		}
		
		return ptset;
	}
	
	/**
	 * Print the objects.
	 */
	public void debugPrint() 
	{
		if ( !readyToUse ) finish();
		
		for ( VarType cv : outList ) {
			System.out.printf("\t%s\n", cv.toString());
		}
	}
	
	
	/**
	 * We use visitor pattern to collect contexts.
	 * Derived classes decide how to deal with the variable with the contexts [L, R).
	 * Returning false means this interval [L, R) is covered by other intervals.
	 * 
	 * @param var
	 * @param L
	 * @param R
	 * @param sm_int : the integer ID of the SootMethod
	 */
	public abstract boolean visit( Node var, long L, long R, int sm_int );
}