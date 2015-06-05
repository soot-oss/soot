package soot.jimple.validation;

import java.util.List;

import soot.Body;
import soot.ResolutionFailedException;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.util.Chain;
import soot.validation.BodyValidator;
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
					if (!v.getField().isStatic()) {
						exception.add(new ValidationException(unit, formatMsg("trying to get a static field which is non-static: " + v, unit, body)));
					}
				} catch (ResolutionFailedException e) {
					exception.add(new ValidationException(unit, formatMsg("trying to get a static field which is non-static: " + v, unit, body)));
				}
			} else if (fr instanceof InstanceFieldRef) {
				InstanceFieldRef v = (InstanceFieldRef) fr;

				try {
					if (v.getField().isStatic()) {
						exception.add(new ValidationException(unit, formatMsg("trying to get an instance field which is static: " + v, unit, body)));
					}
				} catch (ResolutionFailedException e) {
					exception.add(new ValidationException(unit, formatMsg("trying to get an instance field which is static: " + v, unit, body)));
				}
			} else {
				throw new RuntimeException("unknown field ref");
			}
		}
    }

	private String formatMsg(String s, Unit u, Body b) {
		StringBuilder sb = new StringBuilder();
		sb.append(s + "\n");
		sb.append("in unit: " + u + "\n");
		sb.append("in body: \n " + b + "\n");
		return sb.toString();
	}


	@Override
	public boolean isBasicValidator() {
		return true;
	}

}
