package soot.jimple.interproc.ifds.flowfunc;

import java.util.Collections;
import java.util.Set;

public class Identity<A> implements SimpleFlowFunction<A> {
	
	@SuppressWarnings("rawtypes")
	private final static Identity instance = new Identity();
	
	private Identity(){} //use v() instead

	public Set<A> computeTargets(A source) {
		return Collections.singleton(source);
	}

	public Set<A> computeSources(A target) {
		return Collections.singleton(target);
	}
	
	@SuppressWarnings("unchecked")
	public static <A> Identity<A> v() {
		return instance;
	}

}
