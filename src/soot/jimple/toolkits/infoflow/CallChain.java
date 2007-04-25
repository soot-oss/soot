
package soot.jimple.toolkits.infoflow;

import soot.*;
import soot.jimple.toolkits.callgraph.*;
import java.util.*;

/** CallChain written by Richard L. Halpert 2007-03-07
 *  Stores a list of edges, and has a "next pointer" to a continuation of the list
 */

public class CallChain
{
//	List edges;
	Edge edge;
	CallChain next;
	
	public CallChain(Edge edge, CallChain next)
	{
		this.edge = edge;
		if(next != null && next.edge == null && next.next == null) // ignore the empty chain...
			this.next = null;
		else
			this.next = next;
	}
	
	// reconstructs the whole chain
	public List getEdges()
	{
		List ret = new LinkedList();
		if(edge != null)
			ret.add(edge);
		CallChain current = next;
		while(current != null)
		{
			ret.add(current.edge);
			current = current.next;
		}
		return ret;
	}
	
	public int size()
	{
		return 1 + (next == null ? 0 : next.size());
	}
	
	public Iterator iterator()
	{
		return getEdges().iterator();
	}
	
	public boolean contains(Edge e)
	{
		return (edge == e) || (next != null && next.contains(e));
	}
	
	public boolean containsMethod(SootMethod sm)
	{
		return (edge != null && edge.tgt() == sm) || (next != null && next.containsMethod(sm));
	}
	
	// returns a shallow clone of this list...
	// which requires a deep clone of the CallChain objects in it
	public CallChain cloneAndExtend(CallChain extension)
	{
		if(next == null)
			return new CallChain(edge, extension);
			
		return new CallChain(edge, next.cloneAndExtend(extension));
	}
	
	public Object clone()
	{
		if(next == null)
			return new CallChain(edge, null);
			
		return new CallChain(edge, (CallChain) next.clone());	
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof CallChain)
		{
			CallChain other = (CallChain) o;
			if(edge == other.edge && 
			   ((next == null && other.next == null) ||
				(next != null && other.next != null && next.equals(other.next))))
				return true;
		}
		return false;
	}
}
