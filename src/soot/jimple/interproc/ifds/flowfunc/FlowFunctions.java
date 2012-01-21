package soot.jimple.interproc.ifds.flowfunc;

public interface FlowFunctions<N,A,M> {
	
	  public SimpleFlowFunction<A> getNormalFlowFunction(N src, N dest);

	  public SimpleFlowFunction<A> getCallFlowFunction(N callStmt, M destinationMethod);

	  public SimpleFlowFunction<A> getReturnFlowFunction(M calleeMethod, N returnSite);

	  public SimpleFlowFunction<A> getCallToReturnFlowFunction(N callStmt, N returnSite);

}
