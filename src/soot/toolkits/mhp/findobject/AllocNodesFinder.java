package soot.toolkits.mhp.findobject;

import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.toolkits.mhp.pegcallgraph.*;
import soot.jimple.toolkits.callgraph.*;
import soot.toolkits.mhp.*;
import soot.*;
import soot.jimple.*;
import soot.jimple.spark.pag.*;
import soot.util.*;
import java.util.*;

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


public class AllocNodesFinder{
	
	private Set allocNodes;
	private Set  multiObjAllocNodes;
	private Set multiCalledMethods;
	private HashMap methodsToMultiObjsSites;
	
	public  AllocNodesFinder(PegCallGraph pcg, CallGraph cg){
		//System.out.println("===inside AllocNodesFinder===");
		allocNodes = new HashSet();
		multiObjAllocNodes = new HashSet();
		multiCalledMethods = new HashSet();
		methodsToMultiObjsSites = new HashMap();
		MultiCalledMethods mcm = new MultiCalledMethods(pcg, multiCalledMethods);
		
		find(mcm.getMultiCalledMethods(), pcg, cg);
	}
	private void find(Set multiCalledMethods, PegCallGraph pcg, CallGraph callGraph){
		Set clinitMethods = pcg.getClinitMethods();
		Iterator it = pcg.iterator();
		while (it.hasNext()){
			// System.out.println("==inside it of AllocNodesFinder===");
			SootMethod sm = (SootMethod)it.next();
			//    System.out.println("sm in alloc: "+sm);
			if (!methodsToMultiObjsSites.containsKey(sm)){
				
				//UnitGraph graph = new ClassicCompleteUnitGraph(sm.getActiveBody());
				UnitGraph graph = new CompleteUnitGraph(sm.getActiveBody());
				Iterator iterator = graph.iterator();
				if (multiCalledMethods.contains(sm)){
					while (iterator.hasNext()){
						Unit unit = (Unit)iterator.next();
						//System.out.println("unit: "+unit);
						if (clinitMethods.contains(sm)  && unit instanceof AssignStmt){
							Value rightOp = ((AssignStmt)unit).getRightOp();
							
							Type type = ((NewExpr)rightOp).getType();
							AllocNode allocNode = Arguments.getPag().makeAllocNode(
									PointsToAnalysis.STRING_NODE,
									RefType.v( "java.lang.String" ), null );
							//AllocNode allocNode = Arguments.getPag().makeAllocNode((NewExpr)rightOp, type, sm);
							//  System.out.println("make alloc node: "+allocNode);
							allocNodes.add(allocNode);
							multiObjAllocNodes.add(allocNode);
							
						}
						
						else if (unit instanceof DefinitionStmt ){
							Value rightOp = ((DefinitionStmt)unit).getRightOp();
							if (rightOp instanceof NewExpr){
								Type type = ((NewExpr)rightOp).getType();
								AllocNode allocNode = Arguments.getPag().makeAllocNode((NewExpr)rightOp, type, sm);
								//System.out.println("make alloc node: "+allocNode);
								allocNodes.add(allocNode);
								multiObjAllocNodes.add(allocNode);
							}
						}
					} 
				}
				
				else{
					// MultiRunStatementsFinder finder = new MultiRunStatementsFinder(graph, sm);     
					MultiRunStatementsFinder finder = new MultiRunStatementsFinder(graph, sm, multiCalledMethods, callGraph);
					FlowSet fs = finder.getMultiRunStatements();
					//methodsToMultiObjsSites.put(sm, fs);
					//     PatchingChain  pc = sm.getActiveBody().getUnits();
					
					while (iterator.hasNext()){
						Unit unit = (Unit)iterator.next();
						//System.out.println("unit: "+unit);
						
						if (clinitMethods.contains(sm)  && unit instanceof AssignStmt){
							AllocNode allocNode = Arguments.getPag().makeAllocNode(
									PointsToAnalysis.STRING_NODE,
									RefType.v( "java.lang.String" ), null );
							//   AllocNode allocNode = Arguments.getPag().makeAllocNode((NewExpr)rightOp, type, sm);
							//System.out.println("make alloc node: "+allocNode);
							allocNodes.add(allocNode);
							/*if (fs.contains(unit)){
							 multiRunAllocNodes.add(unit);
							 }*/
						}
						else if (unit instanceof DefinitionStmt ){
							
							Value rightOp = ((DefinitionStmt)unit).getRightOp();
							if (rightOp instanceof NewExpr){
								Type type = ((NewExpr)rightOp).getType();
								AllocNode allocNode = Arguments.getPag().makeAllocNode((NewExpr)rightOp, type, sm);
								//System.out.println("make alloc node: "+allocNode);
								allocNodes.add(allocNode);
								if (fs.contains(unit)){
									//System.out.println("fs contains: "+unit);
									multiObjAllocNodes.add(allocNode);
								}
							}
						}
					}
				}
				
			}
		}
		
		
		
		Arguments.setAllocNodes((Set)allocNodes);
		Arguments.setMultiRunAllocNodes((Set)multiObjAllocNodes);
		//System.out.println("end ==multiAllocNodes: "+multiRunAllocNodes);
	}
	public Set getMultiObjAllocNodes(){
		return  (Set)multiObjAllocNodes;
	}
	public Set getMultiCalledMethods()
	{
		return (Set) multiCalledMethods;
	}
}
