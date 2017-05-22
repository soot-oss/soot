package soot.validation;

import java.util.List;

import soot.Body;
import soot.Local;
import soot.Value;
import soot.ValueBox;

public enum LocalsValidator implements BodyValidator {
	INSTANCE;	
	
	public static LocalsValidator v() {
		return INSTANCE;
	}


	@Override
	/** Verifies that each Local of getUseAndDefBoxes() is in this body's locals Chain. */
	public void validate(Body body, List<ValidationException> exception) {
        for (ValueBox vb : body.getUseBoxes()) {
            validateLocal(body, vb, exception);
        }
        for (ValueBox vb : body.getDefBoxes()) {
            validateLocal(body, vb, exception);
        }
    }
	

    private void validateLocal(Body body, ValueBox vb, List<ValidationException> exception ) {
        Value value;
        if( (value = vb.getValue()) instanceof Local) {
            if(!body.getLocals().contains(value))
                exception.add(new ValidationException(value, "Local not in chain : "+value+" in "+ body.getMethod()));
        }
    }


	@Override
	public boolean isBasicValidator() {
		return true;
	}
}
