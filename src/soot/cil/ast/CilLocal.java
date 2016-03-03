package soot.cil.ast;

/**
 * Class modeling a local variable in CIL disassembly
 * 
 * @author Steven Arzt
 *
 */
public class CilLocal {
	
	public enum TypeFlag {
		Class,
		ValueType
	}
	
	private int id;
	private String name;
	private String type;
	private TypeFlag typeFlag;
	
	public CilLocal(int id, String name, String type, TypeFlag typeFlag) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.typeFlag = typeFlag;
	}
	
	public int getID() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getType() {
		return this.type;
	}
	
	public TypeFlag getTypeFlag() {
		return this.typeFlag;
	}
	
	@Override
	public String toString() {
		return "[" + id + "]" + typeFlag.toString().toLowerCase() + " "
				+ type + " " + name;
	}

}
