
package soot.jimple.toolkits.thread.mhp;

import soot.*;
import soot.toolkits.graph.*;
import soot.jimple.internal.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.invoke.*;
import soot.jimple.toolkits.thread.mhp.findobject.AllocNodesFinder;
import soot.jimple.toolkits.thread.mhp.pegcallgraph.PegCallGraph;
import soot.jimple.spark.*;
import soot.jimple.spark.pag.*;
import soot.util.*;
import java.util.*;
import java.io.*;
/**
 *
 */
 
// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30

public class MhpTransformer extends SceneTransformer{
	
	
	
	protected void internalTransform(String phaseName, Map options)
	{
		PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		PAG pag =null;
		if (pta instanceof PAG){
			pag = (PAG)pta;
		}
		else{
			System.err.println("Please add spark option when you run this program!");
			System.exit(1);
		}
		Hierarchy hierarchy = Scene.v().getActiveHierarchy();
		CallGraph callGraph = Scene.v().getCallGraph();
		SootMethod sootMethod= Scene.v().getMainClass().getMethodByName("main");
		Body body = sootMethod.retrieveActiveBody();
		long beginBuildPegTime = System.currentTimeMillis();
		PegCallGraph pcg = new PegCallGraph(callGraph);	
		MethodExtentBuilder meb = new MethodExtentBuilder(body, pcg, callGraph);     
		Set methodsNeedingInlining = meb.getMethodsNeedingInlining();
		Map synchObj = new HashMap();
		Map allocNodeToObj = new HashMap();
		AllocNodesFinder anf = new AllocNodesFinder(pcg, callGraph, pag);
		Set multiObjAllocNodes = anf.getMultiRunAllocNodes();
		ArrayList inlineSites = new ArrayList();
		PegGraph pegGraph = buildPeg( callGraph, hierarchy, pag, methodsNeedingInlining, 
			anf.getAllocNodes(), inlineSites, synchObj, anf.getMultiRunAllocNodes(), allocNodeToObj, body, sootMethod);	
		MethodInliner.inline(inlineSites);
		MonitorAnalysis a = new MonitorAnalysis(pegGraph );		
		long buildPegDuration = (System.currentTimeMillis() - beginBuildPegTime );
		System.err.println("Peg Duration: "+ buildPegDuration);
		System.err.println("Time for building PEG: " + buildPegDuration/100 + "."
				+buildPegDuration % 100 +" seconds");
		long beginMhpTime = System.currentTimeMillis();
		long mhpAnalysisDuration = (System.currentTimeMillis() - beginMhpTime);
		long beginSccTime = System.currentTimeMillis();	
		CompactStronglyConnectedComponents cscc = new CompactStronglyConnectedComponents(pegGraph);
		long sccDuration =  (System.currentTimeMillis() - beginSccTime);
		long beginSeqTime = System.currentTimeMillis();
		CompactSequentNodes csn = new CompactSequentNodes(pegGraph);
		long seqDuration = (System.currentTimeMillis() - beginSeqTime);
		long afterBeginMhpTime = System.currentTimeMillis();
		MhpAnalysis mhpAnalysisAfter = new MhpAnalysis(pegGraph); 
		mhpAnalysisDuration = (System.currentTimeMillis() - afterBeginMhpTime);		
		long duration = (System.currentTimeMillis() - beginBuildPegTime);
		System.err.println("Total time: " + duration );
		System.err.println(" SCC duration "+ sccDuration);
		System.err.println(" Seq duration "+ seqDuration);
		System.err.println("after compacting mhp duration: "+mhpAnalysisDuration);
	}
	
	protected static PegGraph buildPeg( CallGraph callGraph, Hierarchy hierarchy, PAG pag, Set methodsNeedingInlining, Set allocNodes, List inlineSites, Map synchObj, Set multiRunAllocNodes, Map allocNodeToObj, Body body, 
			SootMethod sm){
		
		PegGraph pG = new PegGraph( callGraph, hierarchy, pag, methodsNeedingInlining, allocNodes, inlineSites, synchObj, multiRunAllocNodes, allocNodeToObj, body,  sm, true,  false);
		return pG;
	}
}

