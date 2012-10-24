package soot.jimple.spark.geom.geomPA;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.SootField;
import soot.Type;
import soot.jimple.spark.geom.dataRep.CallsiteContextVar;
import soot.jimple.spark.geom.helper.PtSensVisitor;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.SparkField;

/**
 * It is used to represent the non-pointer variables (e.g. heap variable) in the encoded PAG.
 * 
 * @author xiao
 *
 */
public class DummyNode extends IVarAbstraction 
{
	public DummyNode(Node thisVarNode)
	{
		me = thisVarNode;
	}
	
	@Override
	public void deleteAll()
	{
	}
	
	@Override
	public boolean add_points_to_3(AllocNode obj, long I1, long I2, long L) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean add_points_to_4(AllocNode obj, long I1, long I2, long L1,
			long L2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean add_simple_constraint_3(IVarAbstraction qv, long I1,
			long I2, long L) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean add_simple_constraint_4(IVarAbstraction qv, long I1,
			long I2, long L1, long L2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void put_complex_constraint(PlainConstraint cons) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reconstruct() {
		// TODO Auto-generated method stub

	}

	@Override
	public void do_before_propagation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void do_after_propagation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagate(GeomPointsTo ptAnalyzer, IWorklist worklist) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drop_duplicates() {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove_points_to(AllocNode obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public int num_of_diff_objs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int num_of_diff_edges() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int count_pts_intervals(AllocNode obj) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int count_new_pts_intervals() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int count_flow_intervals(IVarAbstraction qv) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean heap_sensitive_intersection(IVarAbstraction qv) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pointer_sensitive_points_to(long context, AllocNode obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pointer_interval_points_to(long l, long r, AllocNode obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean test_points_to_has_types(Set<Type> types) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<AllocNode> get_all_points_to_objects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void print_context_sensitive_points_to(PrintStream outPrintStream) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keepPointsToOnly() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void injectPts() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDeadObject(AllocNode obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void get_all_context_sensitive_objects(long l, long r,
			PtSensVisitor visitor) {
		// TODO Auto-generated method stub
		
	}
}
