package soot.jimple.spark.internal;

import soot.SootField;
import soot.SootMethod;

public interface AccessibleChecker {
	
	public boolean isAccessible(SootMethod method);
	
	public boolean isAccessible(SootField field);

}
