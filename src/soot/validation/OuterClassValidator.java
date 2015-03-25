package soot.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.SootClass;

/**
 * Validates classes to make sure that the outer class chain is not recursive
 * 
 * @author Steven Arzt
 */
public class OuterClassValidator implements ClassValidator {

	private static final Logger logger =LoggerFactory.getLogger(OuterClassValidator.class);

	public static OuterClassValidator INSTANCE;
	
	
	public static OuterClassValidator v() {
		if (INSTANCE == null)
			INSTANCE = new OuterClassValidator();
		return INSTANCE;
	}

	@Override
	public void validate(SootClass sc, List<ValidationException> exceptions) {
		Set<SootClass> outerClasses = new HashSet<SootClass>();
		SootClass curClass = sc;
		while (curClass != null) {
			if (!outerClasses.add(curClass)) {
				exceptions.add(new ValidationException(curClass, "Circular outer class chain"));
				break;
			}
			curClass = curClass.hasOuterClass() ? curClass.getOuterClass() : null;
		}
	}

	@Override
	public boolean isBasicValidator() {
		return true;
	}

}
