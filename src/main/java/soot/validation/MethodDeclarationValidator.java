package soot.validation;

import java.util.List;

import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;

/**
 * Validates classes to make sure that all method signatures are valid
 * 
 * @author Steven Arzt
 */
public enum MethodDeclarationValidator implements ClassValidator {
	INSTANCE;	
	
	public static MethodDeclarationValidator v() {
		return INSTANCE;
	}

	@Override
	public void validate(SootClass sc, List<ValidationException> exceptions) {
		if (sc.isConcrete())
			for (SootMethod sm : sc.getMethods())
				for (Type tp : sm.getParameterTypes()) {
					if (tp == null)
						exceptions.add(new ValidationException(sm, "Null parameter types are invalid"));
					if (tp instanceof VoidType)
						exceptions.add(new ValidationException(sm, "Void parameter types are invalid"));
					if (!tp.isAllowedInFinalCode())
						exceptions.add(new ValidationException(sm, "Parameter type not allowed in final code"));
				}
	}

	@Override
	public boolean isBasicValidator() {
		return true;
	}

}
