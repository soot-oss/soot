package soot.jimple.interproc.ifds;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.toolkits.scalar.Pair;

public class SummaryEdges<N,A> {
	
	protected Map<Pair<N, A>,Set<Pair<N,A>>> inToOuts = new HashMap<Pair<N,A>, Set<Pair<N,A>>>();
	
	public void insertEdge(N entryPoint, A dataAtEntry, N exitPoint, A dataAtExit) {
		Pair<N, A> entryPair = new Pair<N, A>(entryPoint, dataAtEntry);
		Set<Pair<N, A>> set = inToOuts.get(entryPair);
		if(set==null) {
			set = new HashSet<Pair<N,A>>();
			inToOuts.put(entryPair, set);
		}
		set.add(new Pair<N,A>(exitPoint,dataAtExit));
	}

	public Set<Pair<N, A>> targetsOf(N entryPoint, A dataAtEntry) {
		Set<Pair<N, A>> set = inToOuts.get(new Pair<N, A>(entryPoint, dataAtEntry));
		if(set==null) return Collections.emptySet();
		return set; 
	}

}
