package soot.validation;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import soot.Body;
import soot.Unit;
import soot.ValueBox;

import static java.util.Collections.newSetFromMap;

public enum ValueBoxesValidator implements BodyValidator {
	INSTANCE;
	
	public static ValueBoxesValidator v() {
		return INSTANCE;
	}


	@Override
	/** Verifies that a ValueBox is not used in more than one place. */
	public void validate(Body body, List<ValidationException> exception) {
		Set<ValueBox> set = newSetFromMap(new IdentityHashMap<ValueBox, Boolean>());
		
		for (ValueBox vb: body.getUseAndDefBoxes()) {
			if (set.add(vb))
				continue;			
			
			exception.add(new ValidationException(vb, "Aliased value box : "+vb+" in "+body.getMethod()));
            
			for (Unit u : body.getUnits()) {
                System.err.println(u);
            }
		}
    }

	@Override
	public boolean isBasicValidator() {
		return false;
	}
}
