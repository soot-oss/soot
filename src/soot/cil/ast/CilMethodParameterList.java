package soot.cil.ast;

import java.util.List;

import soot.cil.ast.base.NamedElementList;

/**
 * A list of method parameters in a CIL disassembly
 * 
 * @author Steven Arzt
 *
 */
public class CilMethodParameterList extends NamedElementList<CilMethodParameter> {
		
	public CilMethodParameterList() {
		super();
	}
	
	public CilMethodParameterList(List<CilMethodParameter> parameters) {
		super(parameters);
	}
	
}
