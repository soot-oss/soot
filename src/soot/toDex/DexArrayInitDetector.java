package soot.toDex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.NewArrayExpr;

/**
 * Detector class that identifies array initializations and packs them into a
 * single instruction:
 * 
 * 		a = new char[2];
 * 		a[0] = 42;
 * 		a[1] = 3;
 * 
 * In dex, this can be expressed in a more concise way:
 * 
 * 		a = new char[2];
 * 		fill(a, ...)
 * 
 * @author Steven Arzt
 *
 */
public class DexArrayInitDetector {
	
	private Map<Unit, List<Value>> arrayInitToFillValues = new HashMap<Unit, List<Value>>();
	private Set<Unit> ignoreUnits = new HashSet<Unit>();
	
	/**
	 * Constructs packed array initializations from the individual element
	 * assignments in the given body
	 * @param body The body in which to look for element assignments
	 */
	public void constructArrayInitializations(Body body) {
		// Find an array construction followed by consecutive element
		// assignments
		Unit arrayInitStmt = null;
		List<Value> arrayValues = null;
		Set<Unit> curIgnoreUnits = null;
		int arraySize = 0;
		for (Unit u : body.getUnits()) {
			if (!(u instanceof AssignStmt)) {
				arrayValues = null;
				continue;
			}
			
			AssignStmt assignStmt = (AssignStmt) u;
			if (assignStmt.getRightOp() instanceof NewArrayExpr) {
				NewArrayExpr newArrayExp = (NewArrayExpr) assignStmt.getRightOp();
				if (newArrayExp.getSize() instanceof IntConstant) {
					IntConstant intConst = (IntConstant) newArrayExp.getSize();
					arrayValues = new ArrayList<Value>();
					arraySize = intConst.value;
					curIgnoreUnits = new HashSet<Unit>();
				}
				else {
					arrayValues = null;
				}
			}
			else if (assignStmt.getLeftOp() instanceof ArrayRef
					&& assignStmt.getRightOp() instanceof IntConstant /*NumericConstant*/
					&& arrayValues != null) {
				ArrayRef aref = (ArrayRef) assignStmt.getLeftOp();
				if (aref.getIndex() instanceof IntConstant) {
					IntConstant intConst = (IntConstant) aref.getIndex();
					if (intConst.value == arrayValues.size()) {
						arrayValues.add(assignStmt.getRightOp());
						if (intConst.value == 0)
							arrayInitStmt = u;
						else if (intConst.value == arraySize - 1) {
							curIgnoreUnits.add(u);
							checkAndSave(arrayInitStmt, arrayValues, arraySize, curIgnoreUnits);
							arrayValues = null;
						}
						else
							curIgnoreUnits.add(u);
					}
					else {
						arrayValues = null;
					}
				}
				else {
					arrayValues = null;
				}
			}
			else {
				arrayValues = null;
			}
		}
	}

	private void checkAndSave(Unit arrayInitStmt, List<Value> arrayValues,
			int arraySize, Set<Unit> curIgnoreUnits) {
		// If we already have all assignments, construct the filler
		if (arrayValues != null && arrayValues.size() == arraySize
				&& arrayInitStmt != null) {
			arrayInitToFillValues.put(arrayInitStmt, arrayValues);
			ignoreUnits.addAll(curIgnoreUnits);
		}
	}
	
	public List<Value> getValuesForArrayInit(Unit arrayInit) {
		return arrayInitToFillValues.get(arrayInit);
	}
	
	public Set<Unit> getIgnoreUnits() {
		return ignoreUnits;
	}

}
