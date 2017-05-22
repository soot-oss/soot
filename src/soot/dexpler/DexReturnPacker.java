package soot.dexpler;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;

/**
 * This transformer is the inverse of the DexReturnInliner. It looks for
 * unnecessary duplicates of return statements and removes them.
 * 
 * @author Steven Arzt
 *
 */
public class DexReturnPacker extends BodyTransformer {
	
	public static DexReturnPacker v() {
		return new DexReturnPacker();
	}
	
	@Override
	protected void internalTransform(Body b, String phaseName,
			Map<String, String> options) {
		// Look for consecutive return statements
		Unit lastUnit = null;
		for (Iterator<Unit> unitIt = b.getUnits().iterator(); unitIt.hasNext(); ) {
			Unit curUnit = unitIt.next();
			
			if (curUnit instanceof ReturnStmt || curUnit instanceof ReturnVoidStmt) {
				// Check for duplicates
				if (lastUnit != null && isEqual(lastUnit, curUnit)) {
					curUnit.redirectJumpsToThisTo(lastUnit);
					unitIt.remove();
				}
				else
					lastUnit = curUnit;
			}
			else {
				// Start over
				lastUnit = null;
			}
		}
	}
	
	/**
	 * Checks whether the two given units are semantically equal
	 * @param unit1 The first unit
	 * @param unit2 The second unit
	 * @return True if the two given units are semantically equal, otherwise
	 * false
	 */
	private boolean isEqual(Unit unit1, Unit unit2) {
		// Trivial case
		if (unit1 == unit2 || unit1.equals(unit2))
			return true;
		
		// Semantic check
		if (unit1.getClass() == unit2.getClass()) {
			if (unit1 instanceof ReturnVoidStmt)
				return true;
			else if (unit1 instanceof ReturnStmt)
				return ((ReturnStmt) unit1).getOp() == ((ReturnStmt) unit2).getOp();
		}
		
		return false;
	}

}
