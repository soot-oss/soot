package soot.jimple.interproc.ifds.flowfunc;

import java.util.Collections;
import java.util.Set;

public class KillAll<A> implements SimpleFlowFunction<A> {
	
	@SuppressWarnings("rawtypes")
	private final static KillAll instance = new KillAll();
	
	private KillAll(){} //use v() instead

	public Set<A> computeTargets(A source) {
		return Collections.emptySet();
	}

	public Set<A> computeSources(A target) {
		return Collections.emptySet();
	}
	
	@SuppressWarnings("unchecked")
	public static <A> KillAll<A> v() {
		return instance;
	}

}
