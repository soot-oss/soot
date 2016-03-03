package soot.cil;

import java.lang.reflect.Modifier;
import java.util.Collections;

import soot.ClassSource;
import soot.G;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;
import soot.javaToJimple.IInitialResolver.Dependencies;

/**
 * Special class source for handling type/method/field references by token
 * 
 * @author Steven Arzt
 *
 */
class CilTokenRefClassSource extends ClassSource {

	private final CilTokenRefMethodSource methodSource = new CilTokenRefMethodSource();
	
	public CilTokenRefClassSource(String className) {
		super(className);
	}

	@Override
	public Dependencies resolve(SootClass sc) {
		// Make sure not to construct weird classes
		if (!supportsClass(sc.getName()))
			return null;
		
		// We must mark the artificial classes as application classes, else we
		// cannot add methods to them.
		sc.setApplicationClass();
		
		// Artificially create the derived classes that model token references
		if (G.v().soot_cil_CilNameMangling().getTypeRefFromMangled(
				className) != null)
			return createTypeRefClass(sc);
		else if (G.v().soot_cil_CilNameMangling().getMethodRefFromMangled(
				className) != null)
			return createMethodRefClass(sc);
		else if (G.v().soot_cil_CilNameMangling().getFieldRefFromMangled(
				className) != null)
			return createFieldRefClass(sc);
		
		// We only support some special classes
		return null;
	}

	/**
	 * Creates an artificial type reference class
	 * @param sc The class in which to construct the type reference
	 * @return The dependencies of the type reference class
	 */
	private Dependencies createTypeRefClass(SootClass sc) {
		// Set the correct superclass
		sc.setSuperclass(RefType.v("System.Reflection.RuntimeTypeHandle").getSootClass());
		
		// We need an implicit empty constructor
		SootMethod smCons = new SootMethod("<init>", Collections.<Type>emptyList(), VoidType.v());
		sc.addMethod(smCons);
		smCons.setModifiers(Modifier.PUBLIC);
		smCons.setSource(methodSource);
		
		// Model the dependencies
		Dependencies deps = new Dependencies();
		deps.typesToSignature.add(RefType.v("System.Reflection.RuntimeTypeHandle"));
		return deps;
	}

	/**
	 * Creates an artificial method reference class
	 * @param sc The class in which to construct the method reference
	 * @return The dependencies of the method reference class
	 */
	private Dependencies createMethodRefClass(SootClass sc) {
		// Set the correct superclass
		sc.setSuperclass(RefType.v("System.Reflection.RuntimeMethodHandle").getSootClass());
		
		// We need an implicit empty constructor
		SootMethod smCons = new SootMethod("<init>", Collections.<Type>emptyList(), VoidType.v());
		sc.addMethod(smCons);
		smCons.setModifiers(Modifier.PUBLIC);
		smCons.setSource(methodSource);
		
		// Model the dependencies
		Dependencies deps = new Dependencies();
		deps.typesToSignature.add(RefType.v("System.Reflection.RuntimeMethodHandle"));
		return deps;
	}

	/**
	 * Creates an artificial field reference class
	 * @param sc The class in which to construct the field reference
	 * @return The dependencies of the field reference class
	 */
	private Dependencies createFieldRefClass(SootClass sc) {
		// Set the correct superclass
		sc.setSuperclass(RefType.v("System.Reflection.RuntimeFieldHandle").getSootClass());
		
		// We need an implicit empty constructor
		SootMethod smCons = new SootMethod("<init>", Collections.<Type>emptyList(), VoidType.v());
		sc.addMethod(smCons);
		smCons.setModifiers(Modifier.PUBLIC);
		smCons.setSource(methodSource);
		
		// Model the dependencies
		Dependencies deps = new Dependencies();
		deps.typesToSignature.add(RefType.v("System.Reflection.RuntimeFieldHandle"));
		return deps;
	}

	/**
	 * Checks whether this class source supports a given class
	 * @param className The name of class to check
	 * @return True if the class with the given name is supported by this class
	 * source, otherwise false
	 */
	public static boolean supportsClass(String className) {
		return G.v().soot_cil_CilNameMangling()
						.getTypeRefFromMangled(className) != null;
	}

}
