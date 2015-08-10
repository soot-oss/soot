package soot.validation;

import java.util.List;

import soot.ArrayType;
import soot.Body;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.NullType;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

public enum CheckTypesValidator implements BodyValidator {
	INSTANCE;

	public static CheckTypesValidator v() {
		return INSTANCE;
	}

	@Override
	public void validate(Body body, List<ValidationException> exception) {
		for (Unit u : body.getUnits()) {
			String errorSuffix = " at " + u + " in " + body.getMethod();
			
			if (u instanceof DefinitionStmt) {
				DefinitionStmt astmt = (DefinitionStmt) u;
				if (!(astmt.getRightOp() instanceof CaughtExceptionRef)) {
					Type leftType = Type.toMachineType(astmt.getLeftOp()
							.getType());
					Type rightType = Type.toMachineType(astmt.getRightOp()
							.getType());

					checkCopy(astmt, body, exception, leftType, rightType,
							errorSuffix);
				}
			}
			
			if (u instanceof Stmt) {
				Stmt stmt = (Stmt) u;
				if (stmt.containsInvokeExpr()) {
					SootMethodRef called = stmt.getInvokeExpr().getMethodRef();
					InvokeExpr iexpr = stmt.getInvokeExpr();
					
					if (iexpr instanceof InstanceInvokeExpr) {
						InstanceInvokeExpr iiexpr = (InstanceInvokeExpr) iexpr;
						checkCopy(stmt, body, exception, called.declaringClass()
								.getType(), iiexpr.getBase().getType(),
								" in receiver of call" + errorSuffix);
					}
	
					if (called.parameterTypes().size() != iexpr.getArgCount())
						exception
								.add(new ValidationException(
										stmt,
										"Argument count does not match the signature of the called function",
										"Warning: Argument count doesn't match up with signature in call"
												+ errorSuffix + " in "
												+ body.getMethod()));
					else
						for (int i = 0; i < iexpr.getArgCount(); i++)
							checkCopy(stmt, body, exception,
									Type.toMachineType(called.parameterType(i)),
									Type.toMachineType(iexpr.getArg(i).getType()),
									" in argument " + i + " of call" + errorSuffix);
				}
			}
		}
	}

	private void checkCopy(Unit stmt, Body body,
			List<ValidationException> exception, Type leftType, Type rightType,
			String errorSuffix) {
		if (leftType instanceof PrimType || rightType instanceof PrimType) {
			if (leftType instanceof IntType && rightType instanceof IntType)
				return;
			if (leftType instanceof LongType && rightType instanceof LongType)
				return;
			if (leftType instanceof FloatType && rightType instanceof FloatType)
				return;
			if (leftType instanceof DoubleType
					&& rightType instanceof DoubleType)
				return;
			exception.add(new ValidationException(stmt, "",
					"Warning: Bad use of primitive type" + errorSuffix + " in "
							+ body.getMethod()));
		}

		if (rightType instanceof NullType)
			return;
		if (leftType instanceof RefType
				&& ((RefType) leftType).getClassName().equals(
						"java.lang.Object"))
			return;

		if (leftType instanceof ArrayType || rightType instanceof ArrayType) {
			if (leftType instanceof ArrayType && rightType instanceof ArrayType)
				return;
			// it is legal to assign arrays to variables of type Serializable,
			// Cloneable or Object
			if (rightType instanceof ArrayType) {
				if (leftType.equals(RefType.v("java.io.Serializable"))
						|| leftType.equals(RefType.v("java.lang.Cloneable"))
						|| leftType.equals(RefType.v("java.lang.Object")))
					return;
			}

			exception.add(new ValidationException(stmt,
					"Warning: Bad use of array type" + errorSuffix + " in "
							+ body.getMethod()));
		}

		if (leftType instanceof RefType && rightType instanceof RefType) {
			SootClass leftClass = ((RefType) leftType).getSootClass();
			SootClass rightClass = ((RefType) rightType).getSootClass();
			if (leftClass.isPhantom() || rightClass.isPhantom()) {
				return;
			}

			if (leftClass.isInterface()) {
				if (rightClass.isInterface()) {
					if (!(leftClass.getName().equals(rightClass.getName()) || Scene
							.v().getActiveHierarchy()
							.isInterfaceSubinterfaceOf(rightClass, leftClass)))
						exception.add(new ValidationException(stmt,
								"Warning: Bad use of interface type"
										+ errorSuffix + " in "
										+ body.getMethod()));
				} else {
					// No quick way to check this for now.
				}
			} else {
				if (rightClass.isInterface()) {
					exception.add(new ValidationException(stmt,
							"Warning: trying to use interface type where non-Object class expected"
									+ errorSuffix + " in " + body.getMethod()));
				} else {
					if (!Scene.v().getActiveHierarchy()
							.isClassSubclassOfIncluding(rightClass, leftClass))
						exception.add(new ValidationException(stmt,
								"Warning: Bad use of class type" + errorSuffix
										+ " in " + body.getMethod()));
				}
			}
			return;
		}
		exception.add(new ValidationException(stmt, "Warning: Bad types"
				+ errorSuffix + " in " + body.getMethod()));
	}

	@Override
	public boolean isBasicValidator() {
		return false;
	}
}
