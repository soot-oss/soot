package soot.jimple.spark.geom.dataRep;

import soot.jimple.spark.pag.Node;

/**
 * A particular class to encode contexts in interval manner.
 * 
 * @author xiao
 *
 */
public class IntervalContextVar extends ContextVar 
			implements Comparable<IntervalContextVar> {

	// The interval is [L, R), which stands for a set of consecutive contexts
	public long L = 0, R = 0;
	
	public IntervalContextVar() {}
	
	public IntervalContextVar( long l, long r, Node v )
	{
		L = l;
		R = r;
		var = v;
	}
	
	@Override
	public boolean equals( Object o )
	{
		IntervalContextVar other = (IntervalContextVar)o;
		return ( other.L == L ) && (other.R == R) && (other.var == var);
	}

	@Override
	public int hashCode()
	{
		int ch = (int) ((L+R) % Integer.MAX_VALUE);
		int ans = var.hashCode() + ch;
		if ( ans < 0 ) ans = var.hashCode();
		return ans;
	}

	@Override
	public int compareTo(IntervalContextVar o) 
	{
		if ( L == o.L )
			return R < o.R ? -1 : 1;
		
		return L < o.L ? -1 : 1;
	}
}
