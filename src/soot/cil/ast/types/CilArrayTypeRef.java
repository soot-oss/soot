package soot.cil.ast.types;

/**
 * Reference to an array type in CIL
 * 
 * @author Steven Arzt
 *
 */
public class CilArrayTypeRef extends CilTypeRef {
	
	private CilTypeRef baseType;
	
	public CilArrayTypeRef(CilTypeRef baseType) {
		this.baseType = baseType;
	}
	
	public CilTypeRef getBaseType() {
		return this.baseType;
	}

}
