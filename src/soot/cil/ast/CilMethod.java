package soot.cil.ast;

import java.util.List;

import soot.cil.ast.types.CilTypeRef;

/**
 * Class representing a method in a CIL disassembly file
 * 
 * @author Steven Arzt
 *
 */
public class CilMethod {
	
	private final CilClass clazz;
	private final String methodName;
	private final List<CilMethodParameter> parameters;
	private final CilTypeRef returnType;
	private final List<CilInstruction> instructions;
	private final List<CilTrap> traps;
	
	public CilMethod(CilClass clazz,
			String methodName,
			List<CilMethodParameter> parameters,
			CilTypeRef returnType,
			List<CilInstruction> instructions,
			List<CilTrap> traps) {
		this.clazz = clazz;
		this.methodName = methodName;
		this.parameters = parameters;
		this.returnType = returnType;
		this.instructions = instructions;
		this.traps = traps;
	}
	
	public CilClass getCilClass() {
		return this.clazz;
	}
	
	public String getMethodName() {
		return this.methodName;
	}
	
	public List<CilMethodParameter> getParameters() {
		return this.parameters;
	}
	
	public CilTypeRef getReturnType() {
		return this.returnType;
	}
	
	public List<CilInstruction> getInstructions() {
		return this.instructions;
	}
	
	public List<CilTrap> getTraps() {
		return this.traps;
	}

}
