package soot.cil.sources;

import java.util.ArrayList;
import java.util.List;

import soot.ClassSource;
import soot.Modifier;
import soot.SootClass;
import soot.SootMethod;
import soot.SootResolver;
import soot.Type;
import soot.cil.Cil_Utils;
import soot.cil.ast.CilClass;
import soot.cil.ast.CilMethod;
import soot.cil.ast.CilMethodParameter;
import soot.cil.parser.cilParser;
import soot.javaToJimple.IInitialResolver.Dependencies;

/**
 * CIL class source
 * 
 * @author Tobias Kussmaul
 *
 */

public class CilClassSource extends ClassSource {
	private final CilClass clazz;
	
	private Dependencies deps = new Dependencies();

	public CilClassSource(CilClass clazz) {
		super(clazz.getClassName());
		this.clazz = clazz;
	}
	
	@Override
	public Dependencies resolve(SootClass sc) {
		// Make sure that we're not loading the wrong class
		if (!sc.getName().equals(clazz.getClassName()))
			throw new RuntimeException("Class name mismatch");
		
		// Set the class flags
		int modifiers = clazz.getAccessModifiers();
		if (clazz.isInterface())
			modifiers |= Modifier.INTERFACE;
		sc.setModifiers(modifiers);
		
		// Set the superclass
		SootClass superclass = SootResolver.v().makeClassRef(clazz.getSuperclass().getClassName());
		sc.setSuperclass(superclass);
		deps.typesToHierarchy.add(superclass.getType());
		
		// Load the methods
		loadMethods(sc);
		
		// TODO: Dependencies
		/*
		for (CilClassReference ref : depen) {
			RefType tp = (RefType) Cil_Utils.getSootType(clazz, ref);
			if (ref.isGenericClass())
				dependencyManager.addReference(tp.getClassName(), ref);
			deps.typesToSignature.add(tp);
		}
		*/
		
		return deps;
	}

	/**
	 * Loads all methods in the given class
	 * @param sc The Soot class to which to add the methods
	 */
	private void loadMethods(SootClass sc) {
		for (CilMethod method : clazz.getMethods()) {
			List<Type> parameterTypes = new ArrayList<>();
			for (CilMethodParameter cilParam : method.getParameters()) {
				parameterTypes.add(Cil_Utils.getSootTypeForCilType(cilParam.getType()));
			}
			Type returnType = Cil_Utils.getSootTypeForCilType(method.getReturnType());
			
			SootMethod sm = new SootMethod(method.getMethodName(), parameterTypes, returnType);
			sc.addMethod(sm);
		}
	}
	
}
