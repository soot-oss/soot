package soot.jimple.interproc.ifds.flowfunc;

import java.util.Set;

public interface SimpleFlowFunction {

	//TODO should better operate on N instead of int
	Set<Integer> computeTargets(int source);

	Set<Integer> computeSources(int target);

}
