package soot.cil.ast;

import soot.cil.ast.base.INamedElement;
import soot.cil.ast.types.CilTypeRef;

/**
 * Class representing a formal parameter in a CIL method
 * 
 * @author Steven Arzt
 *
 */
public class CilMethodParameter implements INamedElement {
	
	private int id;
	private String name;
	private CilTypeRef type;
	
	public CilMethodParameter(int id, String name, CilTypeRef type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}
	
	public int getID() {
		return this.id;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	public CilTypeRef getType() {
		return this.type;
	}

}
