package soot.jimple.interproc.ifds.flowfunc;

import java.util.Collections;
import java.util.Set;

public class Kill implements SimpleFlowFunction {
	
	private final int killValue;
	
	public Kill(int killValue){
		this.killValue = killValue;
	} 

	public Set<Integer> computeTargets(int source) {
		if(source==killValue) {
			return Collections.emptySet();
		} else
			return Collections.singleton(source);
	}

	public Set<Integer> computeSources(int target) {
		if(target==killValue) {
			return Collections.emptySet();
		} else
			return Collections.singleton(target);
	}
	
}
