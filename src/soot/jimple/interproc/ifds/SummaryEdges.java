package soot.jimple.interproc.ifds;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SummaryEdges<A> {
	
	protected Map<A,Set<A>> inToOuts = new HashMap<A, Set<A>>();
	
	public void insertEdge(A dataAtEntry, A dataAtExit) {
		Set<A> set = inToOuts.get(dataAtEntry);
		if(set==null) {
			set = new HashSet<A>();
			inToOuts.put(dataAtEntry, set);
		}
		set.add(dataAtExit);
	}

	public Set<A> targetsOf(A d2) {
		Set<A> set = inToOuts.get(d2);
		if(set==null) return Collections.emptySet();
		return set; 
	}

}
