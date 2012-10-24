/*
 * Please attach the following author information if you would like to redistribute the source code:
 * Developer: Xiao Xiao
 * Address: Room 4208, Hong Kong University of Science and Technology
 * Contact: frogxx@gmail.com
 */
package soot.jimple.spark.geom.geomPA;

import java.util.Set;

import soot.jimple.spark.pag.SparkField;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.scalar.Pair;

/**
 * The constraint descriptor class.
 * 
 * @author xiao
 *
 */
public class PlainConstraint {
	// Plain constraint descriptor
	// This is a full description that we can read/write without context
	// A constraint has the form : lhs -> rhs, where lhs/rhs is a pointer p or a field p.f, which assigns the value of lhs to rhs
	
	/** The type of this constraint, e.g. allocation, assignment or complex */
	public int type;
	/** The two pointers involved in this constraint */
	public Pair<IVarAbstraction, IVarAbstraction> expr = new Pair<IVarAbstraction, IVarAbstraction>();
	/** Used in complex constraint. If this constraint is a store p.f = q, we say otherSide = q */
	public IVarAbstraction otherSide = null;
	/** Indicate the mapping relation between the two pointers, 1-1, 1-many, ... */
	public int code;
	/** The field that is involved in a complex constraint */
	public SparkField f = null;
	/** If this constraint represents a parameter passing or function return, the corresponding call edge is identified here */
	public Set<Edge> interCallEdges = null;
	/** To indicate if this constraint is useful or not (may be deleted by cycle detection) */
	public boolean isViable = true;
}
