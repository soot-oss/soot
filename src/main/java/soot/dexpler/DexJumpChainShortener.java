package soot.dexpler;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;

/**
 * Transformer for reducing goto chains. If there is a chain of jumps in the
 * code before the final target is reached, we collapse this chain into a direct
 * jump to the target location.
 * 
 * @author Steven Arzt
 *
 */
public class DexJumpChainShortener extends BodyTransformer {
	
	public static DexJumpChainShortener v() {
		return new DexJumpChainShortener();
	}
	
	@Override
	protected void internalTransform(Body b, String phaseName,
			Map<String, String> options) {
		
		for (Iterator<Unit> unitIt = b.getUnits().snapshotIterator(); unitIt.hasNext(); ) {
			Unit u = unitIt.next();
			if (u instanceof GotoStmt) {
				GotoStmt stmt = (GotoStmt) u;
				while (stmt.getTarget() instanceof GotoStmt) {
					GotoStmt nextTarget = (GotoStmt) stmt.getTarget();
					stmt.setTarget(nextTarget.getTarget());
				}
			}
			else if (u instanceof IfStmt) {
				IfStmt stmt = (IfStmt) u;
				while (stmt.getTarget() instanceof GotoStmt) {
					GotoStmt nextTarget = (GotoStmt) stmt.getTarget();
					stmt.setTarget(nextTarget.getTarget());
				}
			}
		}
	}

}
