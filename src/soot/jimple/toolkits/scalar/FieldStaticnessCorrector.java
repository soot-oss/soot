package soot.jimple.toolkits.scalar;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.G;
import soot.Singletons;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.Jimple;
import soot.jimple.Stmt;

/**
 * Transformer that checks whether a static field is used like an instance
 * field. If this is the case, all instance references are replaced by static
 * field references.
 * 
 * @author Steven Arzt
 *
 */
public class FieldStaticnessCorrector extends AbstractStaticnessCorrector {

	public FieldStaticnessCorrector(Singletons.Global g) {
	}

	public static FieldStaticnessCorrector v() {
		return G.v().soot_jimple_toolkits_scalar_FieldStaticnessCorrector();
	}

	@Override
	protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
		// Some apps reference static fields as instance fields. We need to fix
		// this for not breaking the client analysis.
		for (Iterator<Unit> unitIt = b.getUnits().iterator(); unitIt.hasNext();) {
			Stmt s = (Stmt) unitIt.next();
			if (s.containsFieldRef()) {
				FieldRef ref = s.getFieldRef();
				// Make sure that the target class has already been loaded
				if (isTypeLoaded(ref.getFieldRef().type())) {
					if (ref instanceof InstanceFieldRef && ref.getField().isStatic()) {
						if (s instanceof AssignStmt) {
							AssignStmt assignStmt = (AssignStmt) s;
							if (assignStmt.getLeftOp() == ref)
								assignStmt.setLeftOp(Jimple.v().newStaticFieldRef(ref.getField().makeRef()));
							else if (assignStmt.getRightOp() == ref)
								assignStmt.setRightOp(Jimple.v().newStaticFieldRef(ref.getField().makeRef()));
						}
					}
				}
			}
		}
	}

}
