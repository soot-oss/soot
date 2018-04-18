package soot.dava.internal.javaRep;

import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.internal.AbstractDefinitionStmt;

public class DAssignStmt extends AbstractDefinitionStmt implements AssignStmt {

	public DAssignStmt(ValueBox left, ValueBox right)
	{
		super(left, right);
	}
	
	public Object clone() 
	{
        return new DAssignStmt(leftBox, rightBox);
	}

	@Override
	public void setLeftOp(Value variable) 
	{		
		leftBox.setValue(variable);
	}

	@Override
	public void setRightOp(Value rvalue) 
	{
		rightBox.setValue(rvalue);
	}

	public void toString(UnitPrinter up) 
	{
		leftBox.toString(up);
		up.literal(" = ");
		rightBox.toString(up);
	}
	
	
	public String toString()
    {
        return leftBox.getValue().toString() + " = " + rightBox.getValue().toString();
    }

}
