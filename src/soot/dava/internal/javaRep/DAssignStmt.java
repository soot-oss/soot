package soot.dava.internal.javaRep;

import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.dava.Dava;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;
import soot.jimple.internal.AbstractDefinitionStmt;
import soot.jimple.internal.JAssignStmt;

public class DAssignStmt extends AbstractDefinitionStmt implements AssignStmt {

	public DAssignStmt(ValueBox left, ValueBox right){
		this.leftBox = left;
		this.rightBox = right;
	}
	
	public Object clone() {
        return new DAssignStmt(leftBox, rightBox);
	}

	public void setLeftOp(Value variable) {		
		this.leftBox.setValue(variable);
	}

	public void setRightOp(Value rvalue) {
		this.rightBox.setValue(rvalue);
	}

	public void toString(UnitPrinter up) {
		leftBox.toString(up);
		up.literal(" = ");
		rightBox.toString(up);
	}
	
	
	public String toString()
    {
        return leftBox.getValue().toString() + " = " + rightBox.getValue().toString();
    }

}
