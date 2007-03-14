
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


public class NotifiedEntryStmt extends JPegStmt
{
	
	public NotifiedEntryStmt(String obj, String ca, SootMethod sm)
	{
		this.object = obj;
		this.name = "notified-entry";
		this.caller = ca;
		this.sootMethod = sm;
	}
	
	
	
	
	
}
