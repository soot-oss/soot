package soot.cil;

import java.util.Collections;

import soot.Body;
import soot.G;
import soot.Local;
import soot.MethodSource;
import soot.RefType;
import soot.Scene;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.VoidType;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;

/**
 * Class implementing a specialized method source for fake dispatcher methods
 * that are required to deal with delegates.
 * 
 * @author Steven Arzt
 *
 */
class CilDelegateMethodSource implements MethodSource {
	
	/**
	 * Gets whether this method source can provide an implementation for the
	 * given method
	 * @param m The method to check
	 * @return True if this method source supports the given method, otherwise
	 * false
	 */
	public static boolean supportsMethod(SootMethod m) {
		return CilDelegateClassSource.supportsClass(m.getDeclaringClass().getName())
				|| G.v().soot_cil_CilNameMangling().getTargetSigForDispatcherName(
						m.getDeclaringClass().getName()) != null;
	}

	@Override
	public Body getBody(SootMethod m, String phaseName) {
		// Make sure that we don't generate any spurious method bodies
		if (!supportsMethod(m))
			return null;
		
		JimpleBody jb = Jimple.v().newBody(m);
		LocalGenerator localGenerator = new LocalGenerator(jb);
		
		// We need to check which method we are generating
		if (m.getSignature().equals("<System.Action: void <init>(System.Object,_cil_delegate_)>"))
			createActionConstructor(jb, localGenerator);
		else {
			// This is a method in a dispatcher class
			if (m.getName().equals("<init>"))
				createDispatcherConstructor(jb, localGenerator);
			else if (m.getName().equals("invoke"))
				createDispatcherInvoke(jb, localGenerator);
			else
				return null;
		}
		
		return jb;
	}

	/**
	 * Creates the invoke() method of the dispatcher class
	 * @param jb The body in which to place the dispatcher's invoke() method
	 * @param localGenerator The local generator to use
	 */
	private void createDispatcherInvoke(JimpleBody jb,
			LocalGenerator localGenerator) {
		RefType thisType = jb.getMethod().getDeclaringClass().getType();
		RefType objectType = RefType.v("System.Object");
		
		// Get the original function reference
		String funcRef = G.v().soot_cil_CilNameMangling().getTargetSigForDispatcherName(
				jb.getMethod().getDeclaringClass().getName());
		RefType targetType = (RefType) Cil_Utils.getSootType(
				Cil_Utils.getClassNameFromMethodSignature(funcRef));
		
		// Allocate the "this" local
		Local locThis = localGenerator.generateLocal(thisType);
		jb.getUnits().add(Jimple.v().newIdentityStmt(locThis,
				Jimple.v().newThisRef(thisType)));
		
		// Get the target object
		SootFieldRef fldTarget = Scene.v().makeFieldRef(thisType.getSootClass(),
				"target", objectType, false);
		Local locTarget = localGenerator.generateLocal(objectType);
		jb.getUnits().add(Jimple.v().newAssignStmt(locTarget,
				Jimple.v().newInstanceFieldRef(locThis, fldTarget)));
		
		// Cast the target object to the right type
		Local locCast = localGenerator.generateLocal(targetType);
		jb.getUnits().add(Jimple.v().newAssignStmt(locCast,
				Jimple.v().newCastExpr(locTarget, targetType)));
		
		// Parse the method signature
		SootMethodRef targetMethodRef = Cil_Utils.getMethodRef(funcRef, false);		
		
		// Call the target method
		jb.getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(
				locTarget, targetMethodRef)));
		
		// Finish the method
		jb.getUnits().add(Jimple.v().newReturnVoidStmt());
	}

	/**
	 * Creates the constructor of a dispatcher class
	 * @param jb The body in which to place the dispatcher constructor
	 * @param localGenerator The local generator to use
	 */
	private void createDispatcherConstructor(JimpleBody jb,
			LocalGenerator localGenerator) {
		RefType thisType = jb.getMethod().getDeclaringClass().getType();
		
		// Allocate the "this" local
		Local locThis = localGenerator.generateLocal(thisType);
		jb.getUnits().add(Jimple.v().newIdentityStmt(locThis,
				Jimple.v().newThisRef(thisType)));
		
		// Finish the method
		jb.getUnits().add(Jimple.v().newReturnVoidStmt());
	}

	/**
	 * Creates the body of the constructor of the System.Action class
	 * @param jb The body to fill with the implementation code
	 * @param localGenerator The local generator to use
	 */
	private void createActionConstructor(Body jb, LocalGenerator localGenerator) {
		RefType thisType = jb.getMethod().getDeclaringClass().getType();
		RefType objectType = RefType.v("System.Object");
		RefType dispatcherType = RefType.v("_cil_delegate_");
		
		// Allocate the "this" local
		Local locThis = localGenerator.generateLocal(thisType);
		jb.getUnits().add(Jimple.v().newIdentityStmt(locThis,
				Jimple.v().newThisRef(thisType)));
		
		// Allocate the parameter locals
		Local locTarget = localGenerator.generateLocal(objectType);
		jb.getUnits().add(Jimple.v().newIdentityStmt(locTarget,
				Jimple.v().newParameterRef(objectType, 0)));
		
		Local locDispatcher = localGenerator.generateLocal(dispatcherType);
		jb.getUnits().add(Jimple.v().newIdentityStmt(locDispatcher,
				Jimple.v().newParameterRef(dispatcherType, 1)));
		
		// Save the target into a field
		SootFieldRef fldTarget = Scene.v().makeFieldRef(thisType.getSootClass(),
				"target", objectType, false);
		jb.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(
				locThis, fldTarget), locTarget));
		
		// Save the dispatcher into a field
		SootFieldRef fldDispatcher = Scene.v().makeFieldRef(thisType.getSootClass(),
				"dispatcher", dispatcherType, false);
		jb.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(
				locThis, fldDispatcher), locDispatcher));		
		
		// Set the target into the dispatcher
		SootMethodRef setTargetRef = Scene.v().makeMethodRef(dispatcherType.getSootClass(),
				"setTarget", Collections.<Type>singletonList(objectType),
				VoidType.v(), false);
		jb.getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newInterfaceInvokeExpr(
				locDispatcher, setTargetRef, locTarget)));
		
		// Finish the method
		jb.getUnits().add(Jimple.v().newReturnVoidStmt());
	}

}
