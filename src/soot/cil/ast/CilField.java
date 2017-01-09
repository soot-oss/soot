package soot.cil.ast;

/**
 * A field definition in CIL
 * 
 * @author Steven Arzt
 *
 */
public class CilField {
	
	private final String fieldName;
	
	public CilField(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getFieldName() {
		return this.fieldName;
	}

}
