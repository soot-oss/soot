package soot.jimple.interproc.ifds;

import java.util.List;

import soot.SootMethod;

public class SuperGraph<N>  {
	
	public SootMethod getMethodOf(N n) {
		return null;
	}

	public List<N> getSuccsOf(N n) {
		return null;
	}

	public List<N> getCallersOfCallAt(N n) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<N> getCalleesOfCallAt(N n) {
		// TODO Auto-generated method stub
		return null;
	}

	public N getReturnSiteOfCallAt(N n) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isCallStmt(N stmt) {
		return false;
	}

	public boolean isReturnStmt(N stmt) {
		return false;
	}
}
