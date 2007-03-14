
package soot.jimple.toolkits.thread.mhp.stmt;


import soot.tagkit.*;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.util.*;
import soot.toolkits.graph.*;
import java.util.*;

// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30


public class JoinStmt extends JPegStmt

{
	
	public JoinStmt(String obj, String ca, Unit un, UnitGraph ug, SootMethod sm)
	{
		this.object = obj;
		this.name = "join";
		this.caller = ca;
		this.unit = un;
		this.unitGraph = ug;
		this.sootMethod = sm;
	}
	
	
	
	
	
}
