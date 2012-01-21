package soot.jimple.interproc.ifds.flowfunc;

import java.util.Collections;
import java.util.Set;

public class Kill<A> implements SimpleFlowFunction<A> {
	
	private final A killValue;
	
	public Kill(A killValue){
		this.killValue = killValue;
	} 

	public Set<A> computeTargets(A source) {
		if(source==killValue) {
			return Collections.emptySet();
		} else
			return Collections.singleton(source);
	}

	public Set<A> computeSources(A target) {
		if(target==killValue) {
			return Collections.emptySet();
		} else
			return Collections.singleton(target);
	}
	
}
