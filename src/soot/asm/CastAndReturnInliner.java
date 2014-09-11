package soot.asm;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Trap;
import soot.Unit;
import soot.UnitBox;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.GotoStmt;
import soot.jimple.ReturnStmt;

/**
 * Transformers that inlines returns that cast and return an object. We take
 * a = ..
 * goto l0;
 * 
 * l0:
 * 	b = (B) a;
 * 	return b;
 * 
 * and transform it into
 * a = ..
 * return a;
 * 
 * This makes it easier for the local splitter to split distinct uses of the
 * same variable. Imagine that "a" can come from different parts of the code
 * and have different types. To be able to find a valid typing at all, we
 * must break apart the uses of "a".
 * 
 * @author Steven Arzt
 */
public class CastAndReturnInliner extends BodyTransformer {

	@Override
	protected void internalTransform(Body body, String phaseName,
			Map<String, String> options) {
		Iterator<Unit> it = body.getUnits().snapshotIterator();
		while (it.hasNext()) {
			Unit u = it.next();
			if (u instanceof GotoStmt) {
				GotoStmt gtStmt = (GotoStmt) u;
				if (gtStmt.getTarget() instanceof AssignStmt) {
					AssignStmt assign = (AssignStmt) gtStmt.getTarget();
					if (assign.getRightOp() instanceof CastExpr) {
						CastExpr ce = (CastExpr) assign.getRightOp();
						// We have goto that ends up at a cast statement
						Unit nextStmt = body.getUnits().getSuccOf(assign);
						if (nextStmt instanceof ReturnStmt) {
							ReturnStmt retStmt = (ReturnStmt) nextStmt;
							if (retStmt.getOp() == assign.getLeftOp()) {
								// We need to replace the GOTO with the return
								ReturnStmt newStmt = (ReturnStmt) retStmt.clone();
								newStmt.setOp(ce.getOp());

								for (Trap t : body.getTraps())
									for (UnitBox ubox : t.getUnitBoxes())
										if (ubox.getUnit() == gtStmt)
											ubox.setUnit(newStmt);
								
								while (!gtStmt.getBoxesPointingToThis().isEmpty())
									gtStmt.getBoxesPointingToThis().get(0).setUnit(newStmt);
								body.getUnits().swapWith(gtStmt, newStmt);
							}
						}
					}
				}
			}
		}
	}

}
