package soot.dexpler;

import soot.RefLikeType;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.ConditionExpr;
import soot.jimple.EqExpr;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.NeExpr;
import soot.jimple.NullConstant;

/**
 * Abstract base class for {@link DexNullTransformer} and {@link DexIfTransformer}.
 * 
 * @author Steven Arzt
 */
public abstract class AbstractNullTransformer extends DexTransformer {

	/**
	 * Examine expr if it is a comparison with 0.
	 *
	 * @param expr
	 *            the ConditionExpr to examine
	 */
	protected boolean isZeroComparison(ConditionExpr expr) {
		if (expr instanceof EqExpr || expr instanceof NeExpr) {
			if (expr.getOp2() instanceof IntConstant
					&& ((IntConstant) expr.getOp2()).value == 0)
				return true;
			if (expr.getOp2() instanceof LongConstant
					&& ((LongConstant) expr.getOp2()).value == 0)
				return true;
		}
		return false;
	}
	
	/**
	 * Replace 0 with null in the given unit.
	 *
	 * @param u
	 *            the unit where 0 will be replaced with null.
	 */
	protected void replaceWithNull(Unit u) {
		if (u instanceof IfStmt) {
			ConditionExpr expr = (ConditionExpr) ((IfStmt) u).getCondition();
			if (isZeroComparison(expr)) {
				expr.setOp2(NullConstant.v());
				Debug.printDbg("[null] replacing with null in ", u);
				Debug.printDbg(" new u: ", u);
			}
		} else if (u instanceof AssignStmt) {
			AssignStmt s = (AssignStmt) u;
			Value v = s.getRightOp();
			if ((v instanceof IntConstant && ((IntConstant) v).value == 0)
					|| (v instanceof LongConstant && ((LongConstant) v).value == 0)) {
				// If this is a field assignment, double-check the type. We
				// might have a.f = 2 with a being a null candidate, but a.f
				// being an int.
				if (!(s.getLeftOp() instanceof InstanceFieldRef)
						|| ((InstanceFieldRef) s.getLeftOp()).getField().getType()
							instanceof RefLikeType) {
					s.setRightOp(NullConstant.v());
					Debug.printDbg("[null] replacing with null in ", u);
					Debug.printDbg(" new u: ", u);
				}
			}
		}
	}
	
	protected boolean isObject(Type t) {
		return t instanceof RefLikeType;
	}

}
