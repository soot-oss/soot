package soot.jimple.toolkits.scalar;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Singletons;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.Jimple;
import soot.jimple.Stmt;

public class FieldStaticnessCorrector extends BodyTransformer {

    public FieldStaticnessCorrector( Singletons.Global g ) {}
    public static FieldStaticnessCorrector v() { return G.v().soot_jimple_toolkits_scalar_FieldStaticnessCorrector(); }

    @Override
	protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
        // Some apps reference static fields as instance fields. We need to fix
		// this for not breaking the client analysis.
		for (Iterator<Unit> unitIt = b.getUnits().iterator(); unitIt.hasNext(); ) {
			Stmt s = (Stmt) unitIt.next();
			if (s.containsFieldRef()) {
				FieldRef ref = s.getFieldRef();
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
