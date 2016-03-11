package soot.cil.sources;

import soot.ClassSource;
import soot.SootClass;
import soot.cil.ast.CilClassReference;
import soot.javaToJimple.IInitialResolver.Dependencies;

/**
 * Class source for generating concrete instances of generic classes
 * 
 * @author Steven Arzt
 *
 */
public class CilGenericsClassSource extends ClassSource {
	
	private final CilClassReference classRef;
	
	public CilGenericsClassSource(CilClassReference classRef) {
		super(classRef.getClassName());
		this.classRef = classRef;
	}

	@Override
	public Dependencies resolve(SootClass sc) {
		// We need a reference to the base class
		// TODO
		
		System.out.println("x");
		
		return new Dependencies();
	}

}
