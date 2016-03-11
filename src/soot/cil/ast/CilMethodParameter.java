package soot.cil.ast;

import soot.cil.ast.base.INamedElement;

/**
 * Class representing a formal parameter in a CIL method
 * 
 * @author Steven Arzt
 *
 */
public class CilMethodParameter implements INamedElement {
	
	private int id;
	private String name;
	private String type;
	
	public CilMethodParameter(int id, String name, String type) {
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
	
	public String getType() {
		return this.type;
	}

}
