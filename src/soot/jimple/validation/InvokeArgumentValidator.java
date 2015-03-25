package soot.jimple.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import soot.Body;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

/**
 * A basic validator that checks whether the length of the invoke statement's
 * argument list matches the length of the target methods's parameter type list.
 * 
 * @author Steven Arzt
 */
public class InvokeArgumentValidator implements BodyValidator {

	private static final Logger logger =LoggerFactory.getLogger(InvokeArgumentValidator.class);

	private static InvokeArgumentValidator INSTANCE;
	
	public static InvokeArgumentValidator v() {
		if (INSTANCE == null)
			INSTANCE = new InvokeArgumentValidator();
		return INSTANCE;
	}
	
	@Override
	public void validate(Body body, List<ValidationException> exceptions) {
		for (Unit u : body.getUnits()) {
			Stmt s = (Stmt) u;
			if (s.containsInvokeExpr()) {
				InvokeExpr iinvExpr = s.getInvokeExpr();
				if (iinvExpr.getArgCount() != iinvExpr.getMethod().getParameterCount())
					exceptions.add(new ValidationException(s, "Invalid number of arguments"));
			}
		}
	}

	@Override
	public boolean isBasicValidator() {
		return true;
	}

}
