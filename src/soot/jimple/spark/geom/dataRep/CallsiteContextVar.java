/*
 * Please attach the following author information if you would like to redistribute the source code:
 * Developer: Xiao Xiao
 * Address: Room 4208, Hong Kong University of Science and Technology
 * Contact: frogxx@gmail.com
 */
package soot.jimple.spark.geom.dataRep;

import soot.jimple.spark.geom.geomPA.CgEdge;
import soot.jimple.spark.pag.Node;
import soot.util.Numberable;

/**
 * A general interface for generating the traditional context sensitive variable representation.
 * 
 * @author xiao
 *
 */
public class CallsiteContextVar extends ContextVar
{
	/*
	 * If var is a local pointer or object, context is the callsite for the creation of the pointer or object.
	 * If var is a instance field, context is the callsite for the creation of its base object.
	 */
	public CgEdge context = null;
	
	
	public CallsiteContextVar() {}
	
	public CallsiteContextVar( CgEdge c, Node v )
	{
		context = c;
		var = v;
	}
	
	@Override
	public boolean equals( Object o )
	{
		CallsiteContextVar other = (CallsiteContextVar)o;
		return (other.context == context) && (other.var == var); 
	}

	@Override
	public int hashCode()
	{
		int ch = 0;
		if ( context != null ) ch = context.hashCode();
		
		int ans = var.hashCode() + ch;
		if ( ans < 0 ) ans = var.hashCode();
		return ans;
	}
}

