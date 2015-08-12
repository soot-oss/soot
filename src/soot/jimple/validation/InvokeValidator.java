package soot.jimple.validation;

import java.util.List;

import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

public enum InvokeValidator implements BodyValidator {
	INSTANCE;
	
	public static InvokeValidator v() {
		return INSTANCE;
	}


	@Override
	public void validate(Body body, List<ValidationException> exceptions) {
		if (true)
			return;
		SootClass bodyDeclaredClass = body.getMethod().getDeclaringClass();
		for (Unit unit : body.getUnits()) {
			if (unit instanceof Stmt) {
				Stmt statement = (Stmt)unit;
				if (statement.containsInvokeExpr()) {
					InvokeExpr invokeExpr = statement.getInvokeExpr();
					SootMethodRef referencedMethod = invokeExpr.getMethodRef();
					boolean shouldBeVirtual = true;
					
					if (referencedMethod.isStatic()) {
						shouldBeVirtual = false;
						if (!(invokeExpr instanceof StaticInvokeExpr)) {
							exceptions.add(new ValidationException(unit, "staticinvoke should be used."));
						}
					}
					
					try {
						SootMethod method = referencedMethod.resolve();
						SootClass clazzDeclaring = method.getDeclaringClass();
						boolean superClassMethod = false;
						SootClass clazzSearch = bodyDeclaredClass;
						while (clazzSearch.hasSuperclass())
						{
							clazzSearch = clazzSearch.getSuperclass();
							//specialinvoke is also used at methods of superclasses.
							if (clazzSearch.getName().equals(clazzDeclaring.getName()))
							{
								superClassMethod = true;
								break;
							}
						}
						
						if (clazzDeclaring.isInterface()) {
							shouldBeVirtual = false;
							if (!(invokeExpr instanceof InterfaceInvokeExpr)) {
								exceptions.add(new ValidationException(unit, "Invokes a interface method. Should be interfaceinvoke instead."));
							}
						}
						if (method.isEntryMethod())
						{
							shouldBeVirtual = false;
							exceptions.add(new ValidationException(unit, "Call to <clinit> methods not allowed."));
						}
							
						if (method.isPrivate() || method.isConstructor() || superClassMethod) {
							shouldBeVirtual = false;
							if (!(invokeExpr instanceof SpecialInvokeExpr)) {
								exceptions.add(new ValidationException(unit, "specialinvoke should be used on private or constructor methods. Should be specialinvoke instead."));
							}
						}
						if (shouldBeVirtual) {
							if (!(invokeExpr instanceof VirtualInvokeExpr)) {
								exceptions.add(new ValidationException(unit, "virtualinvoke should be used."));
							}
						}
					} catch (Exception e) {
						//Error on resolving
					}
				}
			}
		}
	}


	@Override
	public boolean isBasicValidator() {
		return false;
	}


}
