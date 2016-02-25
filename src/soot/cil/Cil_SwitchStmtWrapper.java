package soot.cil;

import java.util.List;

import soot.Unit;
import soot.Value;

class Cil_SwitchStmtWrapper {
	private List<String> targetLabels;
	private Unit placeholder;
	private String defaultTarget;
	private Value variable;
	private String label;
	
	public Cil_SwitchStmtWrapper(List<String> targetLabels, String defaultTarget, Unit placeholder, Value variable, String label) {
		this.targetLabels = targetLabels;
		this.placeholder = placeholder;
		this.defaultTarget = defaultTarget;
		this.variable = variable;
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Value getVariable() {
		return variable;
	}

	public void setVariable(Value variable) {
		this.variable = variable;
	}
	public Unit getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(Unit placeholder) {
		this.placeholder = placeholder;
	}

	public String getDefaultTarget() {
		return defaultTarget;
	}

	public void setDefaultTarget(String defaultTarget) {
		this.defaultTarget = defaultTarget;
	}

	public void setTargetLabels(List<String> targetLabels) {
		this.targetLabels = targetLabels;
	}

	public List<String> getTargetLabels() {
		return targetLabels;
	}

}
