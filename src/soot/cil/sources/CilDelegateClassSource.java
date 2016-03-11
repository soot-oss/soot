package soot.cil.sources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import soot.ClassSource;
import soot.G;
import soot.Modifier;
import soot.RefType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;
import soot.cil.Cil_Utils;
import soot.cil.ast.CilClassReference;
import soot.javaToJimple.IInitialResolver.Dependencies;

/**
 * Special class source for handling delegates
 * 
 * @author Steven Arzt
 *
 */
public class CilDelegateClassSource extends ClassSource {

	private final CilDelegateMethodSource methodSource = new CilDelegateMethodSource();
	
	public CilDelegateClassSource(String className) {
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
		
		// Artificially create the system classes required for dispatching
		// delegate invocations
		if (sc.getName().equals("System.Action") && className.equals("System.Action"))
			return createActionClass(sc);
		else if (sc.getName().equals("_cil_delegate_") && className.equals("_cil_delegate_"))
			return createDelegateInterface(sc);
		else if (G.v().soot_cil_CilNameMangling().getTargetSigForDispatcherName(
				className) != null)
			return createDispatcherClass(sc);
		
		// We only support some special classes
		return null;
	}

	/**
	 * Creates a fake dispatcher class to model delegates
	 * @param sc The class in which to construct the dispatcher
	 * @return The dependencies of the dispatcher class
	 */
	private Dependencies createDispatcherClass(SootClass sc) {
		// Create the constructor
		SootMethod smCons = new SootMethod("<init>",
				Collections.<Type>emptyList(), VoidType.v());
		sc.addMethod(smCons);
		smCons.setModifiers(Modifier.PUBLIC);
		smCons.setSource(methodSource);
		
		// We need the target class type to call the correct method
		String originalSig = G.v().soot_cil_CilNameMangling()
				.getTargetSigForDispatcherName(className);
		String targetClass = Cil_Utils.getClassNameFromMethodSignature(originalSig);
		Type tpTargetClass = Cil_Utils.getSootType(null, new CilClassReference(targetClass));
		
		// Create a field to store the target object
		SootField sfTarget = new SootField("target", tpTargetClass);
		sc.addField(sfTarget);
		sfTarget.setModifiers(Modifier.PRIVATE);
		
		// Create the invoke method
		SootMethod smInvoke = new SootMethod("invoke",
				Collections.<Type>emptyList(), VoidType.v());
		sc.addMethod(smInvoke);
		smInvoke.setModifiers(Modifier.PUBLIC);
		smInvoke.setSource(methodSource);
		
		// Implement the interface method to set the target 
		SootMethod smSetDispatcher = new SootMethod("setTarget",
				Collections.<Type>singletonList(RefType.v("System.Object")),
				VoidType.v());
		sc.addMethod(smSetDispatcher);
		smSetDispatcher.setModifiers(Modifier.PUBLIC | Modifier.ABSTRACT);
		smSetDispatcher.setSource(methodSource);
		
		// We need to implement the dispatcher interface
		sc.addInterface(RefType.v("_cil_delegate").getSootClass());
		sc.setModifiers(sc.getModifiers() | Modifier.PUBLIC);
		
		// Model the dependencies
		Dependencies deps = new Dependencies();
		deps.typesToSignature.add(RefType.v("_cil_delegate_"));
		return deps;
	}

	/**
	 * Creates the common interface for all delegates
	 * @param sc The class which to turn into the interface
	 * @return The dependencies of the interface
	 */
	private Dependencies createDelegateInterface(SootClass sc) {		
		// Create the method signatures
		SootMethod smInvoke = new SootMethod("invoke",
				Collections.<Type>emptyList(), VoidType.v());
		sc.addMethod(smInvoke);
		smInvoke.setModifiers(Modifier.PUBLIC | Modifier.ABSTRACT);
		
		// We need a method to set the target object
		SootMethod smSetDispatcher = new SootMethod("setTarget",
				Collections.<Type>singletonList(RefType.v("System.Object")),
				VoidType.v());
		sc.addMethod(smSetDispatcher);
		smSetDispatcher.setModifiers(Modifier.PUBLIC | Modifier.ABSTRACT);
		
		sc.setModifiers(sc.getModifiers() | Modifier.INTERFACE);
		return new Dependencies();
	}

	/**
	 * Gets the dependencies for the System.Action class
	 * @param sc The Soot class to fill with the methods for the System.Action
	 * class
	 * @return The dependencies for the System.Action class
	 */
	private Dependencies createActionClass(SootClass sc) {
		// Create the constructor
		List<Type> paramList = new ArrayList<Type>();
		paramList.add(RefType.v("System.Object"));
		paramList.add(RefType.v("_cil_delegate_"));

		SootMethod smCons = new SootMethod("<init>", paramList, VoidType.v());
		sc.addMethod(smCons);
		smCons.setModifiers(Modifier.PUBLIC);
		smCons.setSource(methodSource);
		
		// We need fields to store the dispatcher class and the target object
		SootField sfDispatcher = new SootField("dispatcher", RefType.v("_cil_delegate_"));
		sc.addField(sfDispatcher);
		sfDispatcher.setModifiers(Modifier.PRIVATE);
		
		SootField sfTarget = new SootField("target", RefType.v("System.Object"));
		sc.addField(sfTarget);
		sfTarget.setModifiers(Modifier.PRIVATE);
		
		// Model the dependencies
		Dependencies deps = new Dependencies();
		deps.typesToSignature.add(RefType.v("_cil_delegate_"));
		deps.typesToHierarchy.add(RefType.v("System.Object"));
		return deps;
	}
	
	/**
	 * Checks whether this class source supports a given class
	 * @param className The name of class to check
	 * @return True if the class with the given name is supported by this class
	 * source, otherwise false
	 */
	public static boolean supportsClass(String className) {
		return className.equals("System.Action")
				|| className.equals("_cil_delegate_")
				|| G.v().soot_cil_CilNameMangling()
						.getTargetSigForDispatcherName(className) != null;
	}

}
