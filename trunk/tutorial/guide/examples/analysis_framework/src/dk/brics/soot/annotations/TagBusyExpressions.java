package dk.brics.soot.annotations;

import dk.brics.soot.transformations.VeryBusyExpsTagger;

import soot.*;

public class TagBusyExpressions {
	public static void main(String[] args) {
		PackManager.v().getPack("jtp").add(new
				Transform("jtp." + VeryBusyExpsTagger.PHASE_NAME,
						VeryBusyExpsTagger.v()));
		
		Main.main(args);
	}
}
