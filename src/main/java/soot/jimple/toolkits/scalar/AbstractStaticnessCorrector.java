package soot.jimple.toolkits.scalar;

import soot.BodyTransformer;
import soot.RefType;
import soot.SootClass;
import soot.Type;

/**
 * Abstract base class for all transformers that fix wrong code that declares
 * something as static, but uses it like an instance or vice versa.
 * 
 * @author Steven Arzt
 *
 */
public abstract class AbstractStaticnessCorrector extends BodyTransformer {

	protected boolean isClassLoaded(SootClass sc) {
		return sc.resolvingLevel() >= SootClass.SIGNATURES;
	}

	protected boolean isTypeLoaded(Type tp) {
		if (tp instanceof RefType) {
			RefType rt = (RefType) tp;
			if (rt.hasSootClass())
				return isClassLoaded(rt.getSootClass());
		}
		return false;
	}

}
