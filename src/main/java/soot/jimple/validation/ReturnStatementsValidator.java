package soot.jimple.validation;

import java.util.List;

import soot.Body;
import soot.Unit;
import soot.jimple.GotoStmt;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.ThrowStmt;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

public enum ReturnStatementsValidator implements BodyValidator {
	INSTANCE;	
	
	public static ReturnStatementsValidator v() {
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
	    /**
	     * Checks that this Jimple body actually contains a return statement
	     */
		for (Unit u : body.getUnits())
			if ((u instanceof ReturnStmt) || (u instanceof ReturnVoidStmt)
					|| (u instanceof RetStmt)
					|| (u instanceof ThrowStmt))
				return;


        // A method can have an infinite loop 
		// and no return statement:
		//
        //  public class Infinite {
        //  public static void main(String[] args) {
        //  int i = 0; while (true) {i += 1;}      } }
        //
        // Only check that the execution cannot fall off the code.
        Unit last = body.getUnits().getLast();
        if (last instanceof GotoStmt || last instanceof ThrowStmt)
            return;

        exception.add(new ValidationException(body.getMethod(), "The method does not contain a return statement", "Body of method " + body.getMethod().getSignature()
				+ " does not contain a return statement"));
    }


	@Override
	public boolean isBasicValidator() {
		return true;
	}
}
