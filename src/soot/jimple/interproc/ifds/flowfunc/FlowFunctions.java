package soot.jimple.interproc.ifds.flowfunc;

public interface FlowFunctions<N> {
	
	  public SimpleFlowFunction getNormalFlowFunction(N src, N dest);

	  public SimpleFlowFunction getCallFlowFunction(N src, N dest);

	  public SimpleFlowFunction getReturnFlowFunction();

	  public SimpleFlowFunction getCallToReturnFlowFunction(N call, N returnSite);

}
