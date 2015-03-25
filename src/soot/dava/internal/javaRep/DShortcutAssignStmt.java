package soot.dava.internal.javaRep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Type;
import soot.UnitPrinter;

public class DShortcutAssignStmt extends DAssignStmt {

	private static final Logger logger =LoggerFactory.getLogger(DShortcutAssignStmt.class);
	Type type;
	
	public DShortcutAssignStmt(DAssignStmt assignStmt, Type type){
		super(assignStmt.getLeftOpBox(), assignStmt.getRightOpBox());
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
