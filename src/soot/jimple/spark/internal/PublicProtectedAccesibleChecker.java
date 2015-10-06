package soot.jimple.spark.internal;

import soot.SootField;
import soot.SootMethod;

public class PublicProtectedAccesibleChecker implements AccessibleChecker{

	@Override
	public boolean isAccessible(SootMethod method) {
		return method.isPublic() || method.isProtected();
	}

	@Override
	public boolean isAccessible(SootField field) {
		return field.isPublic() || field.isProtected();
	}
	
	

}
