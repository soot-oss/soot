package soot.jimple.interproc.ifds;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SummaryEdges {
	
	protected Map<Integer,Set<Integer>> inToOuts = new HashMap<Integer, Set<Integer>>();
	
	public void insertEdge(int dataAtEntry, int dataAtExit) {
		Set<Integer> set = inToOuts.get(dataAtEntry);
		if(set==null) {
			set = new HashSet<Integer>();
			inToOuts.put(dataAtEntry, set);
		}
		set.add(dataAtExit);
	}

	public Set<Integer> targetsOf(int d2) {
		Set<Integer> set = inToOuts.get(d2);
		if(set==null) return Collections.emptySet();
		return set; 
	}

}
