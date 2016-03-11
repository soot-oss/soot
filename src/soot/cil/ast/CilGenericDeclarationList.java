package soot.cil.ast;

import java.util.List;

import soot.cil.ast.base.NamedElementList;

/**
 * A set of generic declarations in a Cil class
 * 
 * @author Steven Arzt
 *
 */
public class CilGenericDeclarationList extends NamedElementList<CilGenericDeclaration> {
	
	public CilGenericDeclarationList() {
		super();
	}
	
	public CilGenericDeclarationList(List<CilGenericDeclaration> generics) {
		super(generics);
	}
	
}
