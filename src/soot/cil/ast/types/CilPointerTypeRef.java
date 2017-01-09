package soot.cil.ast.types;

/**
 * References a pointer type in CIL
 * 
 * @author Steven Arzt
 *
 */
public class CilPointerTypeRef extends CilTypeRef {
	
	private final CilTypeRef baseType;
	
	public CilPointerTypeRef(CilTypeRef baseType) {
		this.baseType = baseType;
	}
	
	public CilTypeRef getBaseType() {
		return this.baseType;
	}

}
