package soot.jimple.interproc.ifds.flowfunc;

import java.util.Set;

public interface SimpleFlowFunction {

	Set<Integer> computeTargets(int d);

}
