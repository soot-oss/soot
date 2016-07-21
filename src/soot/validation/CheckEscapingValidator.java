package soot.validation;

import java.util.List;

import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

public enum CheckEscapingValidator implements BodyValidator {
	INSTANCE;
	
	public static CheckEscapingValidator v() {
		return INSTANCE;
	}
	
	@Override
	public void validate(Body body, List<ValidationException> exception) {
		for (Unit u : body.getUnits()) {
			if (u instanceof Stmt) {
				Stmt stmt = (Stmt) u;
				if (stmt.containsInvokeExpr()) {
					InvokeExpr iexpr = stmt.getInvokeExpr();
					SootMethod sm = iexpr.getMethod();
					if (sm.getName().contains("'")
							|| sm.getDeclaringClass().getName().contains("'"))
						throw new ValidationException(stmt, "Escaped name in signature found");
					for (int i = 0; i < sm.getParameterCount(); i++)
						if (sm.getParameterType(i).toString().contains("'"))
							throw new ValidationException(stmt, "Escaped name in signature found");
				}
			}
		}
	}
	
	@Override
	public boolean isBasicValidator() {
		return false;
	}

}
