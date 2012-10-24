package soot.jimple.spark.geom.helper;

import soot.jimple.spark.pag.Node;

public abstract class PtSensVisitor {

	/**
	 * The user should implement how to deal with the variable with the contexts [L, R).
	 * The return value indicates that the method sm_int has no call edges.
	 * We use this information to terminate the context enumeration.
	 * 
	 * @param var
	 * @param L
	 * @param R
	 * @param sm_int : the integer ID of the SootMethod
	 */
	public abstract boolean visit( Node var, long L, long R, int sm_int );
	
	/**
	 * Called before each round of collection.
	 */
	public abstract void prepare();
	
	/**
	 * Called after each round of collection.
	 */
	public abstract void finish();
}
