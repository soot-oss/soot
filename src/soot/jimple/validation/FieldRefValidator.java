package soot.jimple.validation;

import java.util.List;

import soot.Body;
import soot.ResolutionFailedException;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.util.Chain;
import soot.validation.BodyValidator;
import soot.validation.UnitValidationException;
import soot.validation.ValidationException;

public enum FieldRefValidator implements BodyValidator {
	INSTANCE;	
	
	public static FieldRefValidator v() {
		return INSTANCE;
	}


	@Override
	/**
	 * Checks the consistency of field references.
	 */
	public void validate(Body body, List<ValidationException> exception) {
		SootMethod method = body.getMethod();
		if (method.isAbstract())
			return;
		
		Chain<Unit> units = body.getUnits().getNonPatchingChain();

		for (Unit unit : units) {
			Stmt s = (Stmt) unit;
			if (!s.containsFieldRef()) {
				continue;
			}
			FieldRef fr = s.getFieldRef();

			if (fr instanceof StaticFieldRef) {
				StaticFieldRef v = (StaticFieldRef) fr;
				try {
					SootField field = v.getField();
					if (field == null)
						exception.add(new UnitValidationException(unit, body, "Resolved field is null: " + fr.toString()));

					if (!field.isStatic() && !field.isPhantom()) {
						exception.add(new UnitValidationException(unit, body, "Trying to get a static field which is non-static: " + v));
					}
				} catch (ResolutionFailedException e) {
					exception.add(new UnitValidationException(unit, body, "Trying to get a static field which is non-static: " + v));
				}
			} else if (fr instanceof InstanceFieldRef) {
				InstanceFieldRef v = (InstanceFieldRef) fr;

				try {
					SootField field = v.getField();
					if (field == null)
						exception.add(new UnitValidationException(unit, body, "Resolved field is null: " + fr.toString()));
					
					if (field.isStatic() && !field.isPhantom()) {
						exception.add(new UnitValidationException(unit, body, "Trying to get an instance field which is static: " + v));
					}
				} catch (ResolutionFailedException e) {
					exception.add(new UnitValidationException(unit, body, "Trying to get an instance field which is static: " + v));
				}
			} else {
				throw new RuntimeException("unknown field ref");
			}
		}
    }


	@Override
	public boolean isBasicValidator() {
		return true;
	}

}
