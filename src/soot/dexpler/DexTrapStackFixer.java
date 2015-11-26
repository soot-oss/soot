package soot.dexpler;

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.Trap;
import soot.Unit;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.IdentityStmt;
import soot.jimple.Jimple;
import soot.jimple.Stmt;

/**
 * Transformer to ensure that all exception handlers pull the exception object.
 * In other words, if an exception handler must always have a unit like
 * 
 * 		$r10 = @caughtexception
 * 
 * This is especially important if the dex code is later to be translated into
 * Java bytecode. If no one ever accesses the exception object, it will reside
 * on the stack forever, potentially leading to mismatching stack heights.
 * 
 * @author Steven Arzt
 *
 */
public class DexTrapStackFixer extends BodyTransformer {

	public static DexTrapStackFixer v() {
		return new DexTrapStackFixer();
	}
	
	@Override
	protected void internalTransform(Body b, String phaseName,
			Map<String, String> options) {
		for (Trap t : b.getTraps()) {
			// If the first statement already catches the exception, we're fine
			if (isCaughtExceptionRef(t.getHandlerUnit()))
				continue;
			
			// Add the exception reference
			Local l = new LocalGenerator(b).generateLocal(t.getException().getType());
			Stmt caughtStmt = Jimple.v().newIdentityStmt(l, Jimple.v().newCaughtExceptionRef());
			b.getUnits().add(caughtStmt);
			b.getUnits().add(Jimple.v().newGotoStmt(t.getHandlerUnit()));
			t.setHandlerUnit(caughtStmt);
		}
	}
	
	/**
	 * Checks whether the given statement stores an exception reference
	 * @param handlerUnit The statement to check
	 * @return True if the given statement stores an exception reference,
	 * otherwise false
	 */
	private boolean isCaughtExceptionRef(Unit handlerUnit) {
		if (!(handlerUnit instanceof IdentityStmt))
			return false;
		IdentityStmt stmt = (IdentityStmt) handlerUnit;
		return stmt.getRightOp() instanceof CaughtExceptionRef;
	}

}
