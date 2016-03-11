package soot.cil.ast;

import java.util.List;

/**
 * Class representing an instruction in a CIL method
 * 
 * @author Tobias Kuﬂmaul
 * @author Steven Arzt
 *
 */
public class CilInstruction {
	private String opcode;
	private List<String> parameters;
	private String label;
	
	public CilInstruction(String opcode, List<String> param, String label) {
		this.opcode = opcode;
		this.parameters = param;
		this.label = label;
	}

	public String getOpcode() {
		return opcode;
	}

	public void setOpcode(String opcode) {
		this.opcode = opcode;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		String s = label + ": " + opcode;
		if (!parameters.isEmpty()) {
			s += "(";
			for (int i = 0; i < parameters.size(); i++) {
				if (i > 0)
					s += ", ";
				s += parameters.get(i);
			}
			s += ")";
		}
		return s;
	}
	
}
