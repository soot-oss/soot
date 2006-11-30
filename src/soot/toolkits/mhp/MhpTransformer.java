
package soot.toolkits.mhp;

import soot.*;
import soot.toolkits.graph.*;
import soot.toolkits.mhp.pegcallgraph.*;
import soot.toolkits.mhp.findobject.*;
import soot.jimple.internal.*;
import soot.toolkits.mhp.stmt.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.invoke.*;
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
		//System.out.println("entering internalTransform");
		
//		Arguments arg = new Arguments();
		
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
		Arguments.setHierarchy(hierarchy);
		Arguments.setCallGraph(callGraph);
		Arguments.setPag(pag);
		SootMethod sootMethod= Scene.v().getMainClass().getMethodByName("main");
		
		
		Body body = sootMethod.retrieveActiveBody();
		
		long beginBuildPegTime = System.currentTimeMillis();
		
		PegCallGraph pcg = new PegCallGraph(callGraph);	
		MethodExtentBuilder meb = new MethodExtentBuilder(body, pcg, callGraph);     
		Set methodsNeedingInlining = meb.getMethodsNeedingInlining();
		Arguments.setMethodsNeedingInlining(methodsNeedingInlining);
		//System.out.println("finish build methodsNeedingInlining!");
		
		Map synchObj = new HashMap();
		Arguments.setSynchObj(synchObj);
		Map allocNodeToObj = new HashMap();
		Arguments.setAllocNodeToObj(allocNodeToObj);
//		System.out.println("mhp: allocNodes: "+Arguments.getAllocNodes());
		
		AllocNodesFinder anf = new AllocNodesFinder(pcg, callGraph);
		Set multiObjAllocNodes = anf.getMultiObjAllocNodes();
		Arguments.setMultiRunAllocNodes(multiObjAllocNodes);
		ArrayList inlineSites = new ArrayList();
		Arguments.setInlineSites(inlineSites);
		PegGraph pegGraph = buildPeg( body, sootMethod);	
		//PegToDotFile printer = new PegToDotFile(pegGraph, false, "main");
		//System.err.println("finish build Peg!");	
		
		//testExtendingPoints(inlineSites);
		//---------------------------------
		MethodInliner.inline(inlineSites);
		//PegToDotFile printer1 = new PegToDotFile(pegGraph, false, "inline");
		//System.err.println("**number of nodes: "+pegGraph.size());
		//pegGraph.computeEdgeAndThreadNo();
		
		//------------------------------------------- 
		/*	LoopFinder lf = new LoopFinder(pegGraph);
		 System.err.println("finish loopfinder!");
		 */	
		//------------------------------------------
		
		MonitorAnalysis a = new MonitorAnalysis(pegGraph );		
		long buildPegDuration = (System.currentTimeMillis() - beginBuildPegTime );
		System.err.println("Peg Duration: "+ buildPegDuration);
		System.err.println("Time for building PEG: " + buildPegDuration/100 + "."
				+buildPegDuration % 100 +" seconds");
		//compute mhp before compacting graph
		
		long beginMhpTime = System.currentTimeMillis();
		
		//MhpAnalysis mhpAnalysis = new MhpAnalysis(pegGraph);
		long mhpAnalysisDuration = (System.currentTimeMillis() - beginMhpTime);
		/*System.err.println("Time for  Mhp: " + mhpAnalysisDuration /100+"."+
		 mhpAnalysisDuration%100 +" seconds");
		 System.err.println("mhp duration: "+mhpAnalysisDuration);
		 */
		//long dur = (System.currentTimeMillis() - beginBuildPegTime);
		//System.err.println("time: "+  dur);
		
//		Map unitToMBefore = mhpAnalysis.getUnitToM();
		
		//----------------------------------------------
		long beginSccTime = System.currentTimeMillis();	
		CompactStronglyConnectedComponents cscc = new CompactStronglyConnectedComponents(pegGraph);
		long sccDuration =  (System.currentTimeMillis() - beginSccTime);
		//----------------------------------------------
		
		long beginSeqTime = System.currentTimeMillis();
		CompactSequentNodes csn = new CompactSequentNodes(pegGraph);
		long seqDuration = (System.currentTimeMillis() - beginSeqTime);
		//---------------------------------------------
		
		//System.err.println("=========finish compact====!");
		//System.out.println("=========finish compact====!");	
		
		//----------------------------------------------
		
		//System.err.println("**number of nodes: "+pegGraph.size());
		//pegGraph.computeEdgeAndThreadNo();
		
		//----------------------------------------------
		
		//compute mhp after compacting graph
		long afterBeginMhpTime = System.currentTimeMillis();
		//pegGraph.testMonitor();
		MhpAnalysis mhpAnalysisAfter = new MhpAnalysis(pegGraph); 
		mhpAnalysisDuration = (System.currentTimeMillis() - afterBeginMhpTime);
		//---------------------------------------------
		
		//Map unitToMAfter = mhpAnalysisAfter.getUnitToM();
		//CheckMSet cms = new CheckMSet(unitToMBefore, unitToMAfter);
		
		long duration = (System.currentTimeMillis() - beginBuildPegTime);
		System.err.println("Total time: " + duration );
		
		//-----------------------------------------------
		System.err.println(" SCC duration "+ sccDuration);
		System.err.println(" Seq duration "+ seqDuration);
		
		//System.err.println("Time for  Mhp analysis " + mhpAnalysisDuration /100+"."+
		//		   mhpAnalysisDuration%100 +" seconds");
		System.err.println("after compacting mhp duration: "+mhpAnalysisDuration);
		
	}
	
	protected static PegGraph buildPeg( Body body, 
			SootMethod sm){
		
		PegGraph pG = new PegGraph( body,  sm, true,  false);
		
		
		
		
		//transform the PEG to dot file to view the results
		//PegToDotFile printer = new PegToDotFile(pG, false);	
		
		return pG;
	}
	private void testExtendingPoints(List extendingPoints){
		System.out.println("=====test inlineSites begin===");
		Iterator it = extendingPoints.iterator();
		while (it.hasNext()){
			ArrayList element = (ArrayList)it.next();
			JPegStmt stmt = (JPegStmt)element.get(0);
			
			PegGraph p1 = (PegGraph)element.get(2);
			PegGraph p2 = (PegGraph)element.get(3);
			//System.out.println("***stmt: "+stmt);
			System.out.println("===unit: "+stmt.getUnit());
//			System.out.println("----p1 contains: "+p1.getMainPegChain().contains(stmt));
			// System.out.println("==p1=="+p1);
			
			//System.out.println("==p2=="+p2);
		}
		System.out.println("=====test extendingPoints end===");
	}
	
}

