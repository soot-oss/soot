package soot.jimple.toolkits.thread.mhp;

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

public class Counter{
	private static int tagNo = 0;
	private static int objNo = 0;
	private static int threadNo = 0;
	Counter(){}
	protected static int getTagNo(){
		return tagNo++;
	}
	protected static int getObjNo(){
		return objNo++;
	}
	protected static int getThreadNo(){
		return threadNo++;
	}
	
}
