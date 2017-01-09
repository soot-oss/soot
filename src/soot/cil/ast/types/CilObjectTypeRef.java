package soot.cil.ast.types;

/**
 * Class that represents a type reference in CIL code
 * 
 * @author Steven Arzt
 *
 */
public class CilObjectTypeRef extends CilTypeRef {
	
	private final String assemblyName;
	private final String className;
	
	public CilObjectTypeRef(String assemblyName, String className) {
		this.assemblyName = assemblyName;
		this.className = className;
	}
	
	public String getAssemblyName() {
		return this.assemblyName;
	}
	
	public String getClassName() {
		return this.className;
	}

}
