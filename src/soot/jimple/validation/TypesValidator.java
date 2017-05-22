package soot.jimple.validation;

import java.util.List;

import soot.Body;
import soot.Local;
import soot.SootMethod;
import soot.Type;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

/**
 * Checks whether the types used for locals, method parameters, and method
 * return values are allowed in final Jimple code. This reports an error
 * if a method uses e.g., null_type.
 */
public enum TypesValidator implements BodyValidator {
	INSTANCE;	
	
	public static TypesValidator v() {
		return INSTANCE;
	}


	@Override
	public void validate(Body body, List<ValidationException> exception) {
		SootMethod method = body.getMethod();
		
		if(method!=null) {
			if(!method.getReturnType().isAllowedInFinalCode()) {
				exception.add(new ValidationException(method, "Return type not allowed in final code: " + method.getReturnType(), "return type not allowed in final code:"+method.getReturnType()
				        +"\n method: "+ method));
			}
			for(Type t: method.getParameterTypes()) {
				if(!t.isAllowedInFinalCode()) {
					exception.add(new ValidationException(method, "Parameter type not allowed in final code: " + t, "parameter type not allowed in final code:"+t
					        +"\n method: "+ method));
				}
			}
		}
		for(Local l: body.getLocals()) {
			Type t = l.getType();
			if(!t.isAllowedInFinalCode()) {
				exception.add(new ValidationException(l, "Local type not allowed in final code: " + t, "(" + method + ") local type not allowed in final code: " + t +" local: "+l));
			}
		}
    }


	@Override
	public boolean isBasicValidator() {
		return true;
	}
}
