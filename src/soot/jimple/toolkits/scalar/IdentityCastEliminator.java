package soot.jimple.toolkits.scalar;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.Singletons;
import soot.Type;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;

/**
 * Transformer that removes unnecessary identity casts such as
 * 
 * 		$i3 = (int) $i3
 * 
 * when $i3 is already of type "int".
 * 
 * @author Steven Arzt
 *
 */
public class IdentityCastEliminator extends BodyTransformer {

	public IdentityCastEliminator( Singletons.Global g ) {}
	public static IdentityCastEliminator v() { return G.v().soot_jimple_toolkits_scalar_IdentityCastEliminator(); }

	@Override
	protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
		for (Iterator<Unit> unitIt = b.getUnits().iterator(); unitIt.hasNext(); ) {
			Unit curUnit = unitIt.next();
			if (curUnit instanceof AssignStmt) {
				AssignStmt assignStmt = (AssignStmt) curUnit;
				if (assignStmt.getLeftOp() instanceof Local
						&& assignStmt.getRightOp() instanceof CastExpr) {
					CastExpr ce = (CastExpr) assignStmt.getRightOp();
					
					Type orgType = ce.getOp().getType();
					Type newType = ce.getCastType();
					
					// If this a cast such as  a = (X) a, we can remove the whole line.
					// Otherwise, if only the types match, we can replace the typecast
					// with a normal assignment.
					if (orgType == newType) {
						if (assignStmt.getLeftOp() == ce.getOp())
							unitIt.remove();
						else
							assignStmt.setRightOp(ce.getOp());
					}
				}
			}
		}
	}

}
