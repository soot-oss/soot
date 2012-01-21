package soot.jimple.interproc.ifds.flowfunc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Gen<A> implements SimpleFlowFunction<A> {
	
	private final A genValue;
	
	public Gen(A genValue){
		this.genValue = genValue;
	} 

	public Set<A> computeTargets(A source) {
		if(source==null) {
			HashSet<A> res = new HashSet<A>();
			res.add(source);
			res.add(genValue);
			return res;
		} else
			return Collections.singleton(source);
	}

	public Set<A> computeSources(A target) {
		if(target==null || target==genValue)
			return Collections.singleton(null);
		else
			return Collections.singleton(target);
	}
	
}
