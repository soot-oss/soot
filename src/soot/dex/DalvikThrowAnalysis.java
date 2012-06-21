package soot.dex;

import soot.baf.ReturnInst;
import soot.baf.ReturnVoidInst;
import soot.toolkits.exceptions.ThrowableSet;
import soot.toolkits.exceptions.UnitThrowAnalysis;

public class DalvikThrowAnalysis extends UnitThrowAnalysis {

	@Override
	protected ThrowableSet defaultResult() {
		return mgr.EMPTY;
	}
	
	@Override
	protected UnitSwitch unitSwitch() {
		return new UnitThrowAnalysis.UnitSwitch() {
			@Override
			public void caseReturnInst(ReturnInst i) {
			}
			@Override
			public void caseReturnVoidInst(ReturnVoidInst i) {
			}
		};
	}
	
}
