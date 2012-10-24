/*
 * Please attach the following author information if you would like to redistribute the source code:
 * Developer: Xiao Xiao
 * Address: Room 4208, Hong Kong University of Science and Technology
 * Contact: frogxx@gmail.com
 */
package soot.jimple.spark.geom.geomPA;

import soot.jimple.spark.pag.Node;

/**
 * An abstract class for hiding different encoding methods, e.g. Geom, HeapIns, PtIns.
 * 
 * @author xiao
 *
 */
public abstract class IEncodingBroker 
{
	/**
	 * Generate a node of proper kind.
	 * @param v
	 * @return
	 */
	public abstract IVarAbstraction generateNode( Node v );
	
	/**
	 * Build the initial encoding of the pointer assignments and points-to facts.
	 */
	public abstract void initFlowGraph( GeomPointsTo ptAnalyzer );
	
	/**
	 * Return the signature of the implemented sub-class, may be useful in somewhere.
	 * @return
	 */
	public abstract String getSignature();
}
