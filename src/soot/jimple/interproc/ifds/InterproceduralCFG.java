package soot.jimple.interproc.ifds;

import java.util.List;
import java.util.Set;

import soot.SootMethod;

public interface InterproceduralCFG<N>  {
	
	public SootMethod getMethodOf(N n);

	public List<N> getSuccsOf(N n);

	public Set<N> getCalleesOfCallAt(N n);

	public Set<N> getCallersOf(SootMethod m);

	public N getStartPointOf(SootMethod m);

	/*
	 * In the RHS paper, for every call there is just one return site.
	 * We, however, use as return site the successor statements, of which
	 * there can be many in case of exceptional flow.
	 */
	public List<N> getReturnSitesOfCallAt(N n);

	public boolean isCallStmt(N stmt);

	public boolean isReturnStmt(N stmt);

	public Set<N> entryPoints();

}
