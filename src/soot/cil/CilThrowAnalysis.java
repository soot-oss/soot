package soot.cil;

import soot.G;
import soot.Singletons;
import soot.baf.EnterMonitorInst;
import soot.baf.ReturnInst;
import soot.baf.ReturnVoidInst;
import soot.jimple.AssignStmt;
import soot.jimple.ClassConstant;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.StringConstant;
import soot.toolkits.exceptions.ThrowableSet;
import soot.toolkits.exceptions.UnitThrowAnalysis;

public class CilThrowAnalysis extends UnitThrowAnalysis {
	/**
	 * Constructs a <code>CilThrowAnalysis</code> for inclusion in Soot's global
	 * variable manager, {@link G}.
	 *
	 * @param g
	 *            guarantees that the constructor may only be called from
	 *            {@link Singletons}.
	 */
	public CilThrowAnalysis(Singletons.Global g) {
	}

	/**
	 * Returns the single instance of <code>CilThrowAnalysis</code>.
	 *
	 * @return Soot's <code>UnitThrowAnalysis</code>.
	 */
	public static CilThrowAnalysis v() {
		return G.v().soot_cil_CilThrowAnalysis();
	}

	@Override
	protected ThrowableSet defaultResult() {
		return mgr.EMPTY;
	}

	@Override
	protected UnitSwitch unitSwitch() {
		return new UnitThrowAnalysis.UnitSwitch() {

			// Dalvik does not throw an exception for this instruction
			@Override
			public void caseReturnInst(ReturnInst i) {
			}

			// Dalvik does not throw an exception for this instruction
			@Override
			public void caseReturnVoidInst(ReturnVoidInst i) {
			}

			@Override
			public void caseEnterMonitorInst(EnterMonitorInst i) {
				result = result.add(mgr.NULL_POINTER_EXCEPTION);
				result = result.add(mgr.ILLEGAL_MONITOR_STATE_EXCEPTION);
			}

			@Override
			public void caseEnterMonitorStmt(EnterMonitorStmt s) {
				result = result.add(mgr.NULL_POINTER_EXCEPTION);
				result = result.add(mgr.ILLEGAL_MONITOR_STATE_EXCEPTION);
				result = result.add(mightThrow(s.getOp()));
			}

			@Override
			public void caseAssignStmt(AssignStmt s) {
				// Dalvik only throws ArrayIndexOutOfBounds and
				// NullPointerException which are both handled through the
				// ArrayRef expressions. There is no ArrayStoreException in
				// Dalvik.
				result = result.add(mightThrow(s.getLeftOp()));
				result = result.add(mightThrow(s.getRightOp()));
			}

		};
	}

	@Override
	protected ValueSwitch valueSwitch() {
		return new UnitThrowAnalysis.ValueSwitch() {

			@Override
			public void caseStringConstant(StringConstant c) {
			}

			@Override
			public void caseClassConstant(ClassConstant c) {

			}
		};
	}
}