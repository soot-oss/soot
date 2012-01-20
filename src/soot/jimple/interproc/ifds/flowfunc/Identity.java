package soot.jimple.interproc.ifds.flowfunc;

import java.util.Collections;
import java.util.Set;

public class Identity implements SimpleFlowFunction {
	
	private final static Identity instance = new Identity();
	
	private Identity(){} //use v() instead

	public Set<Integer> computeTargets(int source) {
		return Collections.singleton(source);
	}

	public Set<Integer> computeSources(int target) {
		return Collections.singleton(target);
	}
	
	public static Identity v() {
		return instance;
	}

}
