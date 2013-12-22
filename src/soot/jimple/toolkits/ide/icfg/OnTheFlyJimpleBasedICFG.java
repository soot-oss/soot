package soot.jimple.toolkits.ide.icfg;

import java.util.Set;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;


public class OnTheFlyJimpleBasedICFG extends AbstractJimpleBasedICFG {

	@Override
	public Set<SootMethod> getCalleesOfCallAt(Unit n) {
		Stmt stmt = (Stmt)n;
		InvokeExpr ie = stmt.getInvokeExpr();
		if(ie instanceof InstanceInvokeExpr) {
			
		} else {
			
		}
		return null;
	}

	@Override
	public Set<Unit> getCallersOf(SootMethod m) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Unit> getCallsFromWithin(SootMethod m) {
		// TODO Auto-generated method stub
		return null;
	}


	
}
