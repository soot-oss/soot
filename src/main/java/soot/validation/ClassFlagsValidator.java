package soot.validation;

import java.util.List;

import soot.SootClass;

/**
 * Validator that checks for impossible combinations of class flags
 * 
 * @author Steven Arzt
 *
 */
public enum ClassFlagsValidator implements ClassValidator {
	INSTANCE;

	public static ClassFlagsValidator v() {
		return INSTANCE;
	}

	@Override
	public void validate(SootClass sc, List<ValidationException> exceptions) {
		if (sc.isInterface() && sc.isEnum()) {
			exceptions.add(new ValidationException(sc, "Class is both an interface and an enum"));
		}
		if (sc.isSynchronized()) {
			exceptions.add(new ValidationException(sc, "Classes cannot be synchronized"));
		}
	}

	@Override
	public boolean isBasicValidator() {
		return true;
	}

}
