package soot.cil;

import soot.Unit;

public class Cil_BranchStmt {
	String label;
	String targetLabel;
	Unit stmt;
	
	public Cil_BranchStmt(String label, String targetLabel, Unit stmt) {
		this.label = label;
		this.targetLabel = targetLabel;
		this.stmt = stmt;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getTargetLabel() {
		return targetLabel;
	}
	public void setTargetLabel(String gotLabel) {
		this.targetLabel = gotLabel;
	}
	public Unit getUnit() {
		return stmt;
	}
	public void setStmt(Unit stmt) {
		this.stmt = stmt;
	}
}
