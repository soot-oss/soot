package soot.jimple.interproc.ifds.flowfunc;

public interface FlowFunctions<N,A> {
	
	  public SimpleFlowFunction<A> getNormalFlowFunction(N src, N dest);

	  public SimpleFlowFunction<A> getCallFlowFunction(N src, N dest);

	  public SimpleFlowFunction<A> getReturnFlowFunction();

	  public SimpleFlowFunction<A> getCallToReturnFlowFunction(N call, N returnSite);

}
