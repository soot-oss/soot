package soot.cil.ast.types;

/**
 * Reference to a primitive type in CIL
 * 
 * @author Steven Arzt
 *
 */
public class CilPrimTypeRef extends CilTypeRef {
	
	private CilPrimType primType;
	
	public CilPrimTypeRef(CilPrimType primType) {
		this.primType = primType;
	}
	
	public CilPrimType getPrimType() {
		return this.primType;
	}

}
