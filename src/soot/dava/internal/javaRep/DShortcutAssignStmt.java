package soot.dava.internal.javaRep;

import soot.Type;
import soot.UnitPrinter;
import soot.ValueBox;

public class DShortcutAssignStmt extends DAssignStmt {
	Type type;
	
	public DShortcutAssignStmt(DAssignStmt assignStmt, Type type){
		super(assignStmt.getLeftOpBox(),assignStmt.getRightOpBox());
		this.type = type;
	}
	
	public void toString(UnitPrinter up) {
		up.type(type);
		up.literal(" ");
		super.toString(up);
	}
	
	
	public String toString()
    {
        return type.toString()+" "+leftBox.getValue().toString() + " = " + rightBox.getValue().toString();
    }


	
}
