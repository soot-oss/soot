package soot.jimple.validation;

import java.util.List;

import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.IdentityStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;
import soot.util.Chain;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

public enum IdentityStatementsValidator implements BodyValidator {
	INSTANCE;	
	
	public static IdentityStatementsValidator v() {
		return INSTANCE;
	}


	@Override
	/**
     * Checks the following invariants on this Jimple body:
     * <ol>
     * <li> this-references may only occur in instance methods
     * <li> this-references may only occur as the first statement in a method, if they occur at all
     * <li> param-references must precede all statements that are not themselves param-references or this-references,
     *      if they occur at all
     * </ol>
     */
	public void validate(Body body, List<ValidationException> exception) {
		SootMethod method = body.getMethod();
		if (method.isAbstract())
			return;
		
		Chain<Unit> units=body.getUnits().getNonPatchingChain();

		boolean foundNonThisOrParamIdentityStatement = false;
		boolean firstStatement = true;
		
		for (Unit unit : units) {
			if(unit instanceof IdentityStmt) {
				IdentityStmt identityStmt = (IdentityStmt) unit;
				if(identityStmt.getRightOp() instanceof ThisRef) {					
					if(method.isStatic()) {
						exception.add(new ValidationException(identityStmt, "@this-assignment in a static method!"));
					}					
					if(!firstStatement) {
						exception.add(new ValidationException(identityStmt, "@this-assignment statement should precede all other statements"
						        +"\n method: "+ method));
					}
				} else if(identityStmt.getRightOp() instanceof ParameterRef) {
					if(foundNonThisOrParamIdentityStatement) {
						exception.add(new ValidationException(identityStmt, "@param-assignment statements should precede all non-identity statements"
						        +"\n method: "+ method));
					}
				} else {
					//@caughtexception statement					
					foundNonThisOrParamIdentityStatement = true;
				}
			} else {
				//non-identity statement
				foundNonThisOrParamIdentityStatement = true;
			}
			firstStatement = false;
		}
    }


	@Override
	public boolean isBasicValidator() {
		return true;
	}

}
