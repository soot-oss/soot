package soot.cil.ast;

/**
 * Class modeling a local variable in CIL disassembly
 * 
 * @author Steven Arzt
 *
 */
public class CilLocal {
	
	public enum TypeFlag {
		Unspecified,
		Class,
		ValueType
	}
	
	private int id;
	private String name;
	private CilClassReference type;
	private TypeFlag typeFlag;
	
	public CilLocal(int id, String name, CilClassReference type, TypeFlag typeFlag) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.typeFlag = typeFlag;
		
		if (name == null || name.isEmpty())
			throw new RuntimeException("Every local must have a name");
	}
	
	public int getID() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public CilClassReference getType() {
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
