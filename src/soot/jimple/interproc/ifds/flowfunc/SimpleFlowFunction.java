package soot.jimple.interproc.ifds.flowfunc;

import java.util.Set;

public interface SimpleFlowFunction<A> {

	@interface Nullable{}
	
	Set<A> computeTargets(@Nullable A source);

	Set<A> computeSources(@Nullable A target);

}
