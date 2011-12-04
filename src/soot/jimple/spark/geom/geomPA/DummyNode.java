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
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;

public class DummyNode extends IVarAbstraction 
{
	public DummyNode(Node thisVarNode)
	{
		me = thisVarNode;
	}
	
	
	public boolean add_points_to_3(AllocNode obj, long I1, long I2, long L) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean add_points_to_4(AllocNode obj, long I1, long I2, long L1,
			long L2) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean add_simple_constraint_3(IVarAbstraction qv, long I1,
			long I2, long L) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean add_simple_constraint_4(IVarAbstraction qv, long I1,
			long I2, long L1, long L2) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void put_complex_constraint(PlainConstraint cons) {
		// TODO Auto-generated method stub

	}

	
	public void reconstruct() {
		// TODO Auto-generated method stub

	}

	
	public void do_before_propagation() {
		// TODO Auto-generated method stub

	}

	
	public void do_after_propagation() {
		// TODO Auto-generated method stub

	}

	
	public void propagate(GeomPointsTo ptAnalyzer, IWorklist worklist) {
		// TODO Auto-generated method stub

	}

	
	public void drop_duplicates() {
		// TODO Auto-generated method stub

	}

	
	public void remove_points_to(AllocNode obj) {
		// TODO Auto-generated method stub

	}

	
	public boolean is_empty() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean has_new_pts() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public int num_of_diff_objs() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int num_of_diff_edges() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int count_pts_intervals(AllocNode obj) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int count_new_pts_intervals() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int count_flow_intervals(IVarAbstraction qv) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public boolean heap_sensitive_intersection(IVarAbstraction qv) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean pointer_sensitive_points_to(long context, AllocNode obj) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean pointer_interval_points_to(long l, long r, AllocNode obj) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean test_points_to_has_types(Set<Type> types) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public Set<AllocNode> get_all_points_to_objects() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public int get_all_context_sensitive_objects(long l, long r,
			ZArrayNumberer<CallsiteContextVar> all_objs, Vector<CallsiteContextVar> outList) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public void print_context_sensitive_points_to(PrintStream outPrintStream) {
		// TODO Auto-generated method stub

	}

	
	public void discard() {
		// TODO Auto-generated method stub
		
	}
}
