package soot.jimple.interproc.ifds.flowfunc;

public interface FlowFunctions<N> {
	
	  public SimpleFlowFunction getNormalFlowFunction(N src, N dest);

	  public SimpleFlowFunction getCallFlowFunction(N src, N dest);

	  public MergingFlowFunction getReturnFlowFunction(N call, N src, N dest);

	  public SimpleFlowFunction getCallToReturnFlowFunction(N src, N dest);

}
