package soot.dava.toolkits.base.AST.interProcedural;

import soot.Scene;
import soot.util.Chain;

public class InterProceduralAnalyses {
	
	/*
	 * Method is invoked by postProcessDava in PackManager
	 * if the transformations flag is true
	 * 
	 * All interproceduralAnalyses should be applied in here
	 */
	public static void applyInterProceduralAnalyses(){
		Chain classes = Scene.v().getApplicationClasses();
		
		//System.out.println("\n\nInvoking redundantFielduseEliminator");
		(new RedundantFieldUseEliminator(classes)).applyAnalysis();
	}
}
