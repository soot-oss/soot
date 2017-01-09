package soot.cil.ast;

/**
 * A property definition in CIL
 * 
 * @author Steven Arzt
 *
 */
public class CilProperty {
	
	private final String propertyName;
	
	public CilProperty(String propertyName) {
		this.propertyName = propertyName;
	}
	
	public String getPropertyName() {
		return this.propertyName;
	}

}
