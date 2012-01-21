package soot.jimple.interproc.ifds;

import java.util.List;
import java.util.Set;

public interface InterproceduralCFG<N,M>  {
	
	public M getMethodOf(N n);

	public List<N> getSuccsOf(N n);

	public Set<M> getCalleesOfCallAt(N n);

	public Set<N> getCallersOf(M m);

	public N getStartPointOf(M m);

	/*
	 * In the RHS paper, for every call there is just one return site.
	 * We, however, use as return site the successor statements, of which
	 * there can be many in case of exceptional flow.
	 */
	public List<N> getReturnSitesOfCallAt(N n);

	public boolean isCallStmt(N stmt);

	public boolean isReturnStmt(N stmt);

}
