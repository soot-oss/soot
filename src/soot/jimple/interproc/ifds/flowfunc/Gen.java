package soot.jimple.interproc.ifds.flowfunc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Gen implements SimpleFlowFunction {
	
	private final int genValue;
	
	public Gen(int genValue){
		this.genValue = genValue;
	} 

	public Set<Integer> computeTargets(int source) {
		if(source==0) {
			HashSet<Integer> res = new HashSet<Integer>();
			res.add(source);
			res.add(genValue);
			return res;
		} else
			return Collections.singleton(source);
	}

	public Set<Integer> computeSources(int target) {
		if(target==0 || target==genValue)
			return Collections.singleton(0);
		else
			return Collections.singleton(target);
	}
	
}
