package soot.cil.ast;

import soot.cil.ast.base.INamedElement;

/**
 * Represents a generic declaration in a class
 * 
 * @author Steven Arzt
 *
 */
public class CilGenericDeclaration implements INamedElement {
	
	private String name;
	private CilClassReference superType;
	
	public CilGenericDeclaration(String name) {
		this(name, null);
	}

	public CilGenericDeclaration(String name, CilClassReference superType) {
		this.name = name.trim();
		this.superType = superType;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	public CilClassReference getSuperType() {
		return this.superType;
	}
	
	@Override
	public String toString() {
		return "(" + this.superType + ") " + this.name;
	}

}
