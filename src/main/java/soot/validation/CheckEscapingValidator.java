package soot.validation;

import java.util.List;

import soot.Body;
import soot.SootMethodRef;
import soot.Type;
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
					SootMethodRef ref = iexpr.getMethodRef();
					if (ref.name().contains("'") || ref.declaringClass().getName().contains("'"))
						throw new ValidationException(stmt, "Escaped name in signature found");
					for (Type paramType : ref.parameterTypes())
						if (paramType.toString().contains("'"))
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
