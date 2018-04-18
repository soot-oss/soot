/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville, Raja Vallee-Rais
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 */


package soot.jimple.toolkits.thread.mhp;


import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.thread.mhp.stmt.JPegStmt;
import soot.jimple.toolkits.thread.mhp.stmt.StartStmt;
import soot.toolkits.scalar.*;
import soot.jimple.spark.pag.*;
import soot.toolkits.graph.*;
import soot.jimple.toolkits.callgraph.*;
import soot.util.*;

import java.util.*;
import java.io.*;

//add for add tag
import soot.tagkit.*;

/**
 *  Oct. 7, 2003  modify buildPegChain() for building chain without inliner. 
 *  June 19, 2003 add begin node to peg
 *  June 18, 2003 modify the iterator() to be iterator for all nodes of PEG 
 *                and mainIterator() to be the iterator for main chain.
 *  June 12, 2003 add monitor Map, 
 *                    notifyAll Map,
 *                    waitingNodes Map.
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

// NOTE that this graph builder will only run to completion if all virtual
// method calls can be resolved to a single target method.  This is a severely
// limiting caveat.

public class PegGraph implements DirectedGraph
//public class PegGraph extends SimplePegGraph
{
	private List heads;
	private List tails;
	//    private long numberOfEdge = 0;
	protected HashMap<Object,List> unitToSuccs;
	protected HashMap<Object,List> unitToPreds;   
	private HashMap unitToPegMap;
	public HashMap<JPegStmt,List> startToThread;
	public HashMap startToAllocNodes;
	private HashMap<String, FlowSet> waitingNodes;
	private Map startToBeginNodes;
	private HashMap<String, Set<JPegStmt>> notifyAll;
	private Set methodsNeedingInlining;
	private boolean needInlining;
	private Set<List> synch;
	private Set<JPegStmt> specialJoin;   
	private Body body;
	private Chain unitChain;
	private Chain mainPegChain;
	private FlowSet allNodes;
	private Map<String, FlowSet> monitor;
	private Set canNotBeCompacted;
	private Set threadAllocSites;
	private File logFile;
	private FileWriter fileWriter;
	private Set<Object> monitorObjs;
	private Set<Unit> exceHandlers;
	protected Map threadNo;//add for print to graph
	protected Map  threadNameToStart;
	protected Map<AllocNode, String> allocNodeToObj;
	protected Map<AllocNode, PegChain> allocNodeToThread;
	protected Map<JPegStmt, Chain> joinStmtToThread;
//	protected int count=0;

	Set allocNodes;
	List<List> inlineSites;
	Map<SootMethod, String> synchObj;
	Set multiRunAllocNodes;
	
	/**
	 *   Constructs  a graph for the units found in the provided
	 *   Body instance. Each node in the graph corresponds to
	 *   a unit. The edges are derived from the control flow.
	 *   
	 *   @param Body               The underlying body of main thread
	 *   @param addExceptionEdges  If true then the control flow edges associated with
	 *                             exceptions are added.
	 *   @param Hierarchy          Using class hierarchy analysis to find the run method of started thread
	 *   @param PointsToAnalysis   Using point to analysis (SPARK package) to improve the precision of results
	 */
	
	public PegGraph(CallGraph callGraph, Hierarchy hierarchy, PAG pag, Set<Object> methodsNeedingInlining, Set<AllocNode> allocNodes, List inlineSites, Map synchObj, Set<AllocNode> multiRunAllocNodes, Map allocNodeToObj, Body unitBody, 
			SootMethod sm,
			boolean addExceptionEdges,
			boolean dontAddEdgeFromStmtBeforeAreaOfProtectionToCatchBlock) {
		
		/* public PegGraph( Body unitBody, Hierarchy hierarchy,  boolean addExceptionEdges,
		 boolean dontAddEdgeFromStmtBeforeAreaOfProtectionToCatchBlock) {
		 */
		
		this( callGraph, hierarchy, pag, methodsNeedingInlining, allocNodes, inlineSites, synchObj, multiRunAllocNodes, allocNodeToObj, unitBody, "main",  sm, addExceptionEdges, 
				dontAddEdgeFromStmtBeforeAreaOfProtectionToCatchBlock);
	}
	
	
	
	/**
	 *   Constructs  a graph for the units found in the provided
	 *   Body instance. Each node in the graph corresponds to
	 *   a unit. The edges are derived from the control flow.
	 *   
	 *   @param body               The underlying body we want to make a
	 *                             graph for.
	 *   @param addExceptionEdges  If true then the control flow edges associated with
	 *                             exceptions are added.
	 *   @param dontAddEdgeFromStmtBeforeAreaOfProtectionToCatchBlock This was added for Dava.
	 *                             If true, edges are not added from statement before area of
	 *                             protection to catch. If false, edges ARE added. For Dava,
	 *                             it should be true. For flow analyses, it should be false.
	 *   @param Hierarchy          Using class hierarchy analysis to find the run method of started thread
	 *   @param PointsToAnalysis   Using point to analysis (SPARK package) to improve the precision of results
	 */
	public PegGraph(CallGraph callGraph, Hierarchy hierarchy, PAG pag, Set methodsNeedingInlining, Set allocNodes, List<List> inlineSites, Map<SootMethod, String> synchObj, Set multiRunAllocNodes, Map<AllocNode, String> allocNodeToObj, Body unitBody,String threadName,
			SootMethod sm,boolean addExceEdge,
			boolean dontAddEdgeFromStmtBeforeAreaOfProtectionToCatchBlock) {
		this.allocNodeToObj = allocNodeToObj;
		this.multiRunAllocNodes = multiRunAllocNodes;
		this.synchObj = synchObj;
		this.inlineSites = inlineSites;
		this.allocNodes = allocNodes;
		this.methodsNeedingInlining = methodsNeedingInlining;
		logFile = new File("log.txt");
		try{
			fileWriter = new FileWriter(logFile);
		}
		catch (IOException io){
			System.err.println("Errors occur during create FileWriter !");
//			throw io;
		}
		
		body = unitBody;
		synch = new HashSet<List>();
		exceHandlers = new HashSet<Unit>();
		needInlining = true;
		monitorObjs = new HashSet<Object>();
		startToBeginNodes = new HashMap();
		unitChain = body.getUnits();
		int size = unitChain.size();
		//initial unitToSuccs, unitToPreds, unitToPegMap, and startToThread
		unitToSuccs = new HashMap(size*2+1,0.7f);
		unitToPreds = new HashMap(size*2+1,0.7f);
		//unitToPegMap is the map of a chain to its corresponding (cfg node --> peg node ) map.
		unitToPegMap = new HashMap(size*2+1,0.7f);
		startToThread = new HashMap(size*2+1,0.7f);
		startToAllocNodes = new HashMap(size*2+1,0.7f);
		waitingNodes = new HashMap<String, FlowSet>(size*2+1,0.7f);
		joinStmtToThread = new HashMap<JPegStmt, Chain>();
		threadNo = new HashMap();
		threadNameToStart = new HashMap();
		this.allocNodeToObj = new HashMap<AllocNode, String>(size*2+1,0.7f);
		allocNodeToThread = new HashMap<AllocNode, PegChain>(size*2+1,0.7f);
		notifyAll = new HashMap<String, Set<JPegStmt>>(size*2+1,0.7f);
		
		methodsNeedingInlining = new HashSet();
		allNodes = new ArraySparseSet();
		canNotBeCompacted = new HashSet();
		threadAllocSites =  new HashSet();
		specialJoin = new HashSet<JPegStmt>();
		//       if(Main.isVerbose)
		//   System.out.println("     Constructing PegGraph...");
		
		//if(Main.isProfilingOptimization)
		//   Main.graphTimer.start();
		//make a peg for debug
		/*	
		 mainPegChain = new HashChain();
		 specialTreatment1();
		 */
		//end make a peg
		
		
		UnitGraph mainUnitGraph = new CompleteUnitGraph(body);
		//	mainPegChain = new HashChain();
		mainPegChain = new PegChain( callGraph, hierarchy, pag, threadAllocSites, methodsNeedingInlining, allocNodes, inlineSites, synchObj, multiRunAllocNodes, allocNodeToObj, body, sm, threadName,  true, this);
				
		//testPegChain();
		
//		System.out.println("finish building chain");
		//testStartToThread();
		//buildSuccessor(mainUnitGraph, mainPegChain,addExceptionEdges);
		//	buildSuccessorForExtendingMethod(mainPegChain);
		//testSet(exceHandlers, "exceHandlers");
		buildSuccessor(mainPegChain);
		//System.out.println("finish building successors");
		//unmodifiableSuccs(mainPegChain);
		//testUnitToSucc );
		buildPredecessor(mainPegChain);
		//System.out.println("finish building predcessors");
		//unmodifiablePreds(mainPegChain);
		//testSynch();
		addMonitorStmt();
		addTag();
		//	System.out.println(this.toString());
		buildHeadsAndTails();
		
		//testIterator();
		//testWaitingNodes();
		
		//	System.out.println("finish building heads and tails");
		
		//testSet(canNotBeCompacted, "canNotBeCompacted");
		//	computeEdgeAndThreadNo();
		//	testExtendingPoints();
		//	testUnitToSucc();
		
		//testPegChain();
		/*	if (print) {
		 PegToDotFile printer1 = new PegToDotFile(this, false, sm.getName());
		 }
		 */  
		try{
			fileWriter.flush();
			fileWriter.close();
		}
		catch (IOException io){
			System.err.println("Errors occur during close file  "+logFile.getName());
			//        throw io;
		}
		//System.out.println("==threadAllocaSits==\n"+threadAllocSites.toString());
		
	} 
	protected Map getStartToBeginNodes(){
		return startToBeginNodes;
	}
	protected Map<JPegStmt, Chain> getJoinStmtToThread(){
		return joinStmtToThread;
	}
	protected Map getUnitToPegMap(){
		return unitToPegMap;
	}
	
	
	// This method adds the monitorenter/exit statements into whichever pegChain contains the corresponding node statement
	protected void addMonitorStmt(){
		//System.out.println("====entering addMonitorStmt");	
		if (synch.size()>0){
			// System.out.println("synch: "+synch);
			Iterator<List> it = synch.iterator();
			while (it.hasNext()){
				List list = it.next();
				
				JPegStmt node = (JPegStmt)list.get(0);
				JPegStmt enter = (JPegStmt)list.get(1);
				JPegStmt exit = (JPegStmt)list.get(2);
				//		System.out.println("monitor node: "+node);
				//System.out.println("monitor enter: "+enter);
				//System.out.println("monitor exit: "+exit);
				//add for test
				//System.out.println("allNodes contains node: "+allNodes.contains(node));
				//end add for test
				
				{
					if (!mainPegChain.contains(node)){
						
						boolean find = false;
						//System.out.println("main chain does not contain node");
						Set maps = startToThread.entrySet();
						//System.out.println("size of startToThread: "+startToThread.size());
						for(Iterator iter=maps.iterator(); iter.hasNext();){
							Map.Entry entry = (Map.Entry)iter.next();
							Object startNode = entry.getKey();
							Iterator runIt  = ((List)entry.getValue()).iterator();
							while (runIt.hasNext()){
								Chain chain=(Chain)runIt.next();
								//	testPegChain(chain);
								if (chain.contains(node)) {
									find = true;
									//System.out.println("---find it---");
									chain.add(enter);
									chain.add(exit);
									break;
								}
							}
						}
						if (!find){
							throw new RuntimeException("fail to find stmt: "+node+" in chains!"); 
						}
						
						//this.toString();
					}
					else{
						mainPegChain.add(enter);
						mainPegChain.add(exit);
					}
				}		
				
				allNodes.add(enter);
				allNodes.add(exit);
				
				insertBefore(node, enter);
				insertAfter(node, exit);
			}
		}
		// add for test
		/*
		 {
		 // System.out.println("===main peg chain===");
		  //testPegChain(mainPegChain);
		   //System.out.println("===end main peg chain===");
		    Set maps = startToThread.entrySet();
		    for(Iterator iter=maps.iterator(); iter.hasNext();){
		    Map.Entry entry = (Map.Entry)iter.next();
		    Object startNode = entry.getKey();
		    Iterator runIt  = ((List)entry.getValue()).iterator();
		    while (runIt.hasNext()){
		    Chain chain=(Chain)runIt.next();
		    testPegChain(chain);
		    }
		    }
		    }
		    */
		//	System.out.println(this.toString());
		//end add for test
	}
	private void insertBefore(JPegStmt node, JPegStmt enter){
		
		//build preds of before
		
		List predOfBefore = new ArrayList();
		predOfBefore.addAll(getPredsOf(node));
		unitToPreds.put(enter,predOfBefore );
		
		//	System.out.println("put into unitToPreds enter: "+enter);
		//	System.out.println("put into unitToPreds value: "+predOfBefore);
		Iterator predsIt = getPredsOf(node).iterator();
		
		//build succs of former preds of node
		while (predsIt.hasNext()){
			Object pred = predsIt.next();
			
			List succ = getSuccsOf(pred);
			succ.remove(node);
			succ.add(enter);
			//	    System.out.println("in unitToPred pred: "+pred);
			//	    System.out.println("in unitToPred value is: "+succ);
		}
		
		
		List succOfBefore = new ArrayList();
		succOfBefore.add(node);
		unitToSuccs.put(enter, succOfBefore);
		//	System.out.println("put into unitToSuccs enter: "+enter);
		//System.out.println("put into unitToSuccs value: "+succOfBefore);
		
		List predOfNode = new ArrayList();
		predOfNode.add(enter);
		unitToPreds.put(node, predOfNode);
		//System.out.println("put into unitToPreds enter: "+node);
		//System.out.println("put into unitToPreds value: "+predOfNode);
		//buildPreds();
		
	}
	private void insertAfter(JPegStmt node, JPegStmt after){
		//System.out.println("node: "+node);
		//System.out.println("after: "+after);
//		System.out.println("succs of node: "+getSuccsOf(node));
		
		// this must be done first because the succs of node will be chanaged lately
		List succOfAfter = new ArrayList();
		succOfAfter.addAll(getSuccsOf(node));
		unitToSuccs.put(after,succOfAfter );
		
		
		Iterator succsIt = getSuccsOf(node).iterator();	
		while (succsIt.hasNext()){
			Object succ = succsIt.next();
			List pred = getPredsOf(succ);
			pred.remove(node);
			pred.add(after);
		}
		
		List succOfNode = new ArrayList();
		succOfNode.add(after);
		unitToSuccs.put(node, succOfNode);
		
		List predOfAfter = new ArrayList();
		predOfAfter.add(node);
		unitToPreds.put(after, predOfAfter);
		
		
		
		//	buildPredecessor(Chain pegChain);
	}
	
	private void buildSuccessor(Chain pegChain)	
	{
		
		// Add regular successors
		{
			
			HashMap unitToPeg =(HashMap)unitToPegMap.get(pegChain);
			Iterator pegIt = pegChain.iterator();
			JPegStmt currentNode, nextNode;
			currentNode = pegIt.hasNext() ? (JPegStmt) pegIt.next(): null;
			//June 19 add for begin node
			if (currentNode != null){
				//System.out.println("currentNode: "+currentNode);
				//if the unit is "begin" node
				nextNode = pegIt.hasNext() ? (JPegStmt) pegIt.next(): null;
				
				if (currentNode.getName().equals("begin")){
					List<JPegStmt> successors = new ArrayList<JPegStmt>();
					successors.add(nextNode);
					unitToSuccs.put(currentNode, successors);
					
					
					currentNode = nextNode;
				}
				//end June 19 add for begin node
				
				while(currentNode != null) {
					//		    System.out.println("currentNode: "+currentNode);
					/* If unitToSuccs contains currentNode, it is the point to inline methods,
					 * we need not compute its successors again
					 */
					
					if (unitToSuccs.containsKey(currentNode) && !currentNode.getName().equals("wait")){
						currentNode = pegIt.hasNext() ? (JPegStmt) pegIt.next(): null;
						continue;
					}
					List<JPegStmt> successors = new ArrayList<JPegStmt>();
					Unit unit = currentNode.getUnit();
					
					
					UnitGraph unitGraph = currentNode.getUnitGraph();
					List unitSucc = unitGraph.getSuccsOf(unit);
					Iterator succIt = unitSucc.iterator();
					while (succIt.hasNext()){
						Unit un = (Unit)succIt.next();
						
						//Don't build the edge from "monitor exit" to exception handler
						
						if (unit instanceof ExitMonitorStmt && exceHandlers.contains(un) ){
							//System.out.println("====find it! unit: "+unit+"\n un: "+un);
							continue;
						}
						
						else if ( unitToPeg.containsKey(un) ){
							JPegStmt pp= (JPegStmt)(unitToPeg.get(un));
							if (pp !=null && !successors.contains(pp) )
								successors.add(pp);
						}
						
					}//end while
					
					if (currentNode.getName().equals("wait")){
						while ( !(currentNode.getName().equals("notified-entry"))){
							currentNode = pegIt.hasNext() ? (JPegStmt) pegIt.next(): null;  
						}
						unitToSuccs.put(currentNode, successors);
						//System.out.println("put key: "+currentNode+" into unitToSucc");
					}
					else{
						unitToSuccs.put(currentNode, successors);
					}
					if (currentNode.getName().equals("start")){
						
//						System.out.println("-----build succ for start----");
						
						if (startToThread.containsKey(currentNode)){
							List runMethodChainList = startToThread.get(currentNode);
							Iterator possibleMethodIt = runMethodChainList.iterator();
							while (possibleMethodIt.hasNext()){
								
								Chain subChain = (Chain)possibleMethodIt.next();
								if ( subChain != null){
									//System.out.println("build succ for subChain");
									// buildSuccessor(subGraph, subChain, addExceptionEdges);
									buildSuccessor(subChain);
								}
								else
									System.out.println("*********subgraph is null!!!");
							}
						}
						
					}
					
					
					
					currentNode = pegIt.hasNext() ? (JPegStmt) pegIt.next(): null;
				}//while
				
				//June 19 add for begin node
			}	    
			//end June 19 add for begin node
		}
		
		
		
	}
	
	/*
	 private void deleteExitToException(){
	 Iterator it = iterator();
	 while (it.hasNext()){
	 JPegStmt stmt = (JPegStmt)it.next();
	 Unit unit = stmt.getUnit();
	 UnitGraph unitGraph = stmt.getUnitGraph();
	 if (unit instanceof ExitMonitorStmt){
	 Iterator succIt = unitGraph.getSuccsOf(unit).iterator();
	 while(succIt.next
	 && exceHandlers.contains(un) ){
	 System.out.println("====find it! unit: "+unit+"\n un: "+un);
	 continue;
	 }
	 }
	 }
	 */
	private void buildPredecessor(Chain pegChain){
		//System.out.println("==building predcessor===");
		
		// initialize the pred sets to empty
		{
			JPegStmt s=null;
			Iterator unitIt = pegChain.iterator();
			while(unitIt.hasNext()){
				
				s = (JPegStmt)unitIt.next();
				
				unitToPreds.put(s, new ArrayList());
				
			}
			
		}
		//	System.out.println("==finish init of unitToPred===");
		{
			Iterator unitIt = pegChain.iterator();
			
			while(unitIt.hasNext()){
				
				Object s =  unitIt.next();
				//		System.out.println("s is: "+s);
				
				
				// Modify preds set for each successor for this statement
				if (unitToSuccs.containsKey(s)){
					List succList = unitToSuccs.get(s);
					Iterator succIt = succList.iterator();
					//		    System.out.println("unitToSuccs contains "+s);
					//		    System.out.println("succList is: "+succList);
					while(succIt.hasNext()){
						
						//Object successor =  succIt.next();
						JPegStmt successor = (JPegStmt)succIt.next();
						//			System.out.println("successor is: "+successor);
						List<Object> predList = unitToPreds.get(successor);
						//			System.out.println("predList is: "+predList);
						if (predList != null && !predList.contains(s)) {
							try {
								predList.add(s);
								/*
								 Tag tag1 = (Tag)((JPegStmt)s).getTags().get(0);
								 System.out.println("add "+tag1+" "+s+" to predListof");
								 Tag tag2 = (Tag)((JPegStmt)successor).getTags().get(0);
								 System.out.println(tag2+" "+successor);
								 */
							} catch(NullPointerException e) {
								System.out.println(s + "successor: " + successor);
								throw e;
							}
							// if (((JPegStmt)successor).getName().equals("start")){
							if (successor instanceof StartStmt){
								List runMethodChainList = startToThread.get(successor);
								if (runMethodChainList == null){
									throw new RuntimeException("null runmehtodchain: \n"+successor.getUnit());
								}
								Iterator possibleMethodIt = runMethodChainList.iterator();
								while (possibleMethodIt.hasNext()){
									
									Chain subChain = (Chain)possibleMethodIt.next();
									
									buildPredecessor(subChain);
								}
							}
						}
						else{
							System.err.println("predlist of "+s +" is null");
//							System.exit(1);
						}
						//			unitToPreds.put(successor, predList);
						
						
					}
					
				}
				else{
					throw new RuntimeException("unitToSuccs does not contains key"+s);
				}
			}
		}
		
	}
	
	// Make pred lists unmodifiable.
	
	private void buildHeadsAndTails(){
		
		List tailList = new ArrayList();
		List headList = new ArrayList();
		
		// Build the sets
		{
			Iterator unitIt = mainPegChain.iterator();
			
			while(unitIt.hasNext())
			{
				JPegStmt s = (JPegStmt) unitIt.next();
				
				List succs = unitToSuccs.get(s);
				if(succs.size() == 0)
					tailList.add(s);
				if (!unitToPreds.containsKey(s)){
					throw new RuntimeException("unitToPreds does not contain key: "+s);
				}
				List preds = unitToPreds.get(s);
				if(preds.size() == 0)
					headList.add(s);
				// System.out.println("head is:");
			}
		}
		tails = (List)tailList;
		heads = (List)headList;
		//	tails = Collections.unmodifiableList(tailList);
		//heads = Collections.unmodifiableList(headList);
		
		Iterator tmpIt =heads.iterator();
		
		while (tmpIt.hasNext()){
			Object temp = tmpIt.next();
			//System.out.println(temp);
		}
		
		buildPredecessor(mainPegChain); 
	}
	
	
	public boolean addPeg(PegGraph pg,Chain chain){
		if (!pg.removeBeginNode()) return false;
//		System.out.println("adding one peg into another");
		
//		System.out.println("after removeBeginNode===");
		//	pg.testPegChain();
		//System.out.println(pg);
		
		//put every node of peg into this
		Iterator mainIt = pg.mainIterator();
		
		while (mainIt.hasNext()){
			JPegStmt s = (JPegStmt)mainIt.next();
//			System.out.println("add to mainPegChain: "+s);
			mainPegChain.addLast(s);
//			if (chain.contains(s)){
//				System.err.println("error! chain contains: "+s);
//				System.exit(1);
//			}
//			else
//				chain.addLast(s);
			
		}
		Iterator it = pg.iterator();
		while (it.hasNext()){
			JPegStmt s = (JPegStmt)it.next();
			//System.out.println("add to allNodes: "+s);
			if (allNodes.contains(s)){
				throw new RuntimeException("error! allNodes contains: "+s);
			}
			else
				allNodes.add(s);
			
		}
		//	testPegChain();
		//	testIterator();
		unitToSuccs.putAll(pg.getUnitToSuccs());
		unitToPreds.putAll(pg.getUnitToPreds());
		//	testUnitToSucc();
		//testUnitToPred();
//		buildMaps(pg); // RLH
		return true;
	}
	
	private boolean removeBeginNode(){
		List heads = getHeads();
		if (heads.size() != 1){
			// System.out.println("heads: "+heads);
			//System.out.println("Error: the size of heads is not equal to 1!");
			return false;
			//	    System.exit(1);
		}
		
		else{
			JPegStmt head =(JPegStmt)heads.get(0);
			//System.out.println("test head: "+head);
			if (!head.getName().equals("begin")){
				throw new RuntimeException("Error: the head is not begin node!");
			}
			//remove begin node from heads list
			heads.remove(0);
			//set the preds list of the succs of head to a new list and put succs of head into heads
			Iterator succOfHeadIt = getSuccsOf(head).iterator();
			while (succOfHeadIt.hasNext()){
				
				JPegStmt succOfHead = (JPegStmt)succOfHeadIt.next();
				
				unitToPreds.put(succOfHead, new ArrayList());
				//put succs of head into heads
				heads.add(succOfHead);
			}
			//remove begin node from inlinee Peg
			if (!mainPegChain.remove(head)) {
				throw new RuntimeException("fail to remove begin node in from mainPegChain!");
			}
			if (!allNodes.contains(head)){
				throw new RuntimeException("fail to find begin node in FlowSet allNodes!");
			}
			else{
				allNodes.remove(head);
			}
			
			//remove begin node from unitToSuccs
			if (unitToSuccs.containsKey(head)){
				unitToSuccs.remove(head);
			}
		}
		
		return true;
	}
	protected void buildSuccsForInlining(JPegStmt stmt, Chain chain, PegGraph inlinee){
		//System.out.println("entering buildSuccsForInlining...");
		Tag tag = (Tag)stmt.getTags().get(0);
		//System.out.println("stmt is: "+tag+" "+stmt);
		/*connect heads of inlinee with the preds of invokeStmt and
		 * delete stmt from the succs list from the preds
		 */
		
		
		Iterator predIt = getPredsOf(stmt).iterator();
		//System.out.println("preds list: "+getPredsOf(stmt));
		//System.out.println("preds size: "+getPredsOf(stmt).size());
		Iterator headsIt = inlinee.getHeads().iterator();
		{
			//System.out.println("heads: "+inlinee.getHeads());
			while (predIt.hasNext()){
				JPegStmt pred = (JPegStmt)predIt.next();
				//System.out.println("pred: "+pred);
				List succList = (List)getSuccsOf(pred);
				//System.out.println("succList of pred: "+succList);
				int pos = succList.indexOf(stmt);
				//System.out.println("remove : "+stmt + " from succList: \n"+succList+ "\n of pred" );
				//remove invokeStmt
				succList.remove(pos);
				
				
				while (headsIt.hasNext()){
					succList.add(headsIt.next());
				}
				unitToSuccs.put(pred, succList); 
				
			}
			
			{
				
				while (headsIt.hasNext()){
					Object head = headsIt.next();
					List predsOfHeads = new ArrayList();
					predsOfHeads.addAll(getPredsOf(head));
					unitToPreds.put(head, predsOfHeads);
					
				}
			}
			/*
			 {
			 predIt = getPredsOf(stmt).iterator();
			 while (predIt.hasNext()){
			 JPegStmt s = (JPegStmt)predIt.next();
			 if (unitToSuccs.containsKey(s)){
			 Iterator succIt = ((List) unitToSuccs.get(s)).iterator();
			 while(succIt.hasNext()){
			 
			 //Object successor =  succIt.next();
			  JPegStmt successor = (JPegStmt)succIt.next();
			  List predList = (List) unitToPreds.get(successor);
			  if (predList != null) {
			  try {
			  predList.add(s);
			  
			  } catch(NullPointerException e) {
			  System.out.println(s + "successor: " + successor);
			  throw e;
			  }
			  }
			  }
			  }
			  }
			  
			  
			  
			  
			  }*/
			
			
		}
		
		
		/*connect tails of inlinee with the succ of invokeStmt and
		 * delete stmt from the 
		 */
		
		
		Iterator tailsIt = inlinee.getTails().iterator();
		{	
			//System.out.println("tails: "+inlinee.getTails());
			while (tailsIt.hasNext()){
				Iterator succIt = getSuccsOf(stmt).iterator();
				JPegStmt tail = (JPegStmt)tailsIt.next();
				List succList = null;
				if (unitToSuccs.containsKey(tail)){
					//System.out.println("error: unitToSucc containsKey: "+tail);
					succList = (List)getSuccsOf(tail);
					//System.out.println("succList: "+succList);
				}
				else{
					
					succList = new ArrayList();
				}
				while (succIt.hasNext()){
					JPegStmt succ = (JPegStmt)succIt.next();
					succList.add(succ);
					//System.out.println("succ: "+succ);
					//remove stmt from the preds list of the succs of itself.
					List predListOfSucc = getPredsOf(succ);
					if (predListOfSucc == null){
						throw new RuntimeException("Error: predListOfSucc is null!");
					}
					else{
						if (predListOfSucc.size() != 0){
							
							int pos = predListOfSucc.indexOf(stmt);
							if (pos > 0 || pos == 0 ){
								
								//	System.out.println("remove stmt: "+stmt+" from the preds list"+predListOfSucc+" of the succ");
								predListOfSucc.remove(pos);
							}
							
							//		System.out.println("remove(from PRED): ");
						}
					}
					unitToPreds.put(succ,predListOfSucc); 
					
				}
				unitToSuccs.put(tail, succList);
				//System.out.println("put: "+tail);
				//System.out.println("succList: "+succList+ "into unitToSucc");
				
				
			}
		}
		
		//add Nov 1
		{
			tailsIt = inlinee.getTails().iterator();
			while (tailsIt.hasNext()){
				JPegStmt s = (JPegStmt)tailsIt.next();
				if (unitToSuccs.containsKey(s)){
					Iterator succIt = unitToSuccs.get(s).iterator();
					while(succIt.hasNext()){
						
						//Object successor =  succIt.next();
						JPegStmt successor = (JPegStmt)succIt.next();
						List<JPegStmt> predList = unitToPreds.get(successor);
						if (predList != null && !predList.contains(s)) {
							try {
								predList.add(s);
								/*
								 Tag tag = (Tag)successor.getTags().get(0);
								 System.out.println("add "+s+" to predlist of "+tag+" "+successor);
								 */
							} catch(NullPointerException e) {
								System.out.println(s + "successor: " + successor);
								throw e;
							}
						}
					}
				}
			}
			
			
			
		}
		//end add Nov 1
		
		//System.out.println("stmt: "+stmt);
		//remove stmt from allNodes and mainPegChain
		//System.out.println("mainPegChain contains stmt: "+mainPegChain.contains(stmt));
//		testPegChain();
		
		if (!allNodes.contains(stmt)){
			throw new RuntimeException("fail to find begin node in  allNodes!");
		}
		else{
			allNodes.remove(stmt);
			// System.out.println("remove from allNode: "+stmt);
		}
		
		if (!chain.contains(stmt)){
			throw new RuntimeException("Error! Chain does not contains stmt (extending point)!");
			
		}
		else{
			if (!chain.remove(stmt)){
				throw new RuntimeException("fail to remove invoke stmt in from Chain!");
			}
		}
		/*
		 if (!mainPegChain.contains(stmt)){
		 boolean find = false;
		 //System.out.println("main chain does not contain AFTER");
		  Set maps = startToThread.entrySet();
		  for(Iterator iter=maps.iterator(); iter.hasNext();){
		  Map.Entry entry = (Map.Entry)iter.next();
		  Object startNode = entry.getKey();
		  Iterator runIt  = ((List)entry.getValue()).iterator();
		  while (runIt.hasNext()){
		  Chain chain=(Chain)runIt.next();
		  if (chain.contains(stmt)) {
		  find = true;
		  if (!chain.remove(stmt)){
		  System.err.println("fail to remove begin node in from mainPegChain!");
		  System.exit(1);
		  }
		  break;
		  }
		  }
		  if (find == false){
		  System.err.println("fail to find stmt: "+stmt+" in chains!");
		  System.exit(1);
		  }
		  }
		  //this.toString();
		   }
		   else{
		   if (!mainPegChain.remove(stmt)) {
		   System.err.println("fail to remove begin node in from mainPegChain!");
		   System.exit(1);
		   }
		   else{
		   // System.out.println("remove(from mainchain): "+stmt);
		    }
		    }
		    */
		//remove stmt from unitToSuccs and unitToPreds
		if (unitToSuccs.containsKey(stmt)) {
			unitToSuccs.remove(stmt);
		}
		if (unitToPreds.containsKey(stmt)) {
			unitToPreds.remove(stmt);
		}
		
		
		
	}
	
	protected void buildMaps(PegGraph pg){
		exceHandlers.addAll(pg.getExceHandlers());
		startToThread.putAll(pg.getStartToThread());
		startToAllocNodes.putAll(pg.getStartToAllocNodes());
		startToBeginNodes.putAll(pg.getStartToBeginNodes());
		waitingNodes.putAll(pg.getWaitingNodes());
		notifyAll.putAll(pg.getNotifyAll());
		canNotBeCompacted.addAll(pg.getCanNotBeCompacted());	
		synch.addAll(pg.getSynch());
		threadNameToStart.putAll(pg.getThreadNameToStart());
		specialJoin.addAll(pg.getSpecialJoin());
		joinStmtToThread.putAll(pg.getJoinStmtToThread());
		threadAllocSites.addAll(pg.getThreadAllocSites());
		allocNodeToThread.putAll(pg.getAllocNodeToThread());
	}
	
	protected void buildPreds(){
		buildPredecessor(mainPegChain);
		Set maps = getStartToThread().entrySet();
		for(Iterator iter=maps.iterator(); iter.hasNext();){ 
			Map.Entry entry = (Map.Entry)iter.next();
			List runMethodChainList = (List)entry.getValue();
			Iterator it = runMethodChainList.iterator();
			while (it.hasNext()){
				Chain chain=(Chain)it.next();
				//	System.out.println("chain is null: "+(chain == null));
				buildPredecessor(chain);
				
			}
		}
	}
	public void computeMonitorObjs(){
		Set maps = monitor.entrySet();
		for(Iterator iter=maps.iterator(); iter.hasNext();){
			Map.Entry entry = (Map.Entry)iter.next();
			
			FlowSet fs  = (FlowSet)entry.getValue();
			Iterator it = fs.iterator();
			while (it.hasNext()){
				Object obj = it.next();
				if (!monitorObjs.contains(obj)) monitorObjs.add(obj);
			}
		}
	}
	protected boolean getNeedInlining(){
		//System.out.println("return needInlining: "+ needInlining);
		return needInlining;
	}
	protected FlowSet getAllNodes(){
		return (FlowSet)allNodes;
	}
	protected  HashMap getUnitToSuccs(){
		return (HashMap)unitToSuccs;
	}
	
	protected HashMap getUnitToPreds(){
		return (HashMap)unitToPreds;
	}
	
	public Body getBody()
	{
		return body;
	}
	
	
	/* DirectedGraph implementation */
	public List getHeads()
	{
		return heads;
	}
	
	public List getTails()
	{
		return tails;
	}
	
	public List getPredsOf(Object s)
	{
		if(!unitToPreds.containsKey(s))
			throw new RuntimeException("Invalid stmt" + s);
		
		return unitToPreds.get(s);
	}
	
	public List getSuccsOf(Object s)
	{
		
		if(!unitToSuccs.containsKey(s))
		{
			return new ArrayList();
//			throw new RuntimeException("Invalid stmt:" + s);
		}
		
		return unitToSuccs.get(s);
	}
	public Set getCanNotBeCompacted(){
		return (Set)canNotBeCompacted;
	}
	public int size()
	{
		return allNodes.size();
//		return pegSize;
		
	}  
	
	public Iterator mainIterator()
	{ 
		return mainPegChain.iterator();
	} 
	public Iterator iterator()
	{
		
		return allNodes.iterator();
	}
	public String toString() 
	{
		Iterator it = iterator();
		StringBuffer buf = new StringBuffer();
		while(it.hasNext()) {
			JPegStmt u = (JPegStmt) it.next();
			buf.append("u is: "+u+"\n");
			List l = new ArrayList(); l.addAll(getPredsOf(u));
			buf.append("preds: "+l+"\n");
			//buf.append(u.toString() + '\n');
			l = new ArrayList(); l.addAll(getSuccsOf(u));
			buf.append("succs: "+l+"\n");
		}
		
		return buf.toString();
	}
	protected Set<Unit> getExceHandlers(){
		return (Set<Unit>)exceHandlers;
	}
	protected void setMonitor(Map<String, FlowSet> m){
		monitor = m;
	}
	
	public Map<String, FlowSet> getMonitor(){
		return (Map<String, FlowSet>)monitor;
	}
	public Set<Object> getMonitorObjs(){
		return (Set<Object>)monitorObjs;
	}
	protected Set getThreadAllocSites(){
		return (Set)threadAllocSites;
	}
	protected Set<JPegStmt> getSpecialJoin(){
		return (Set<JPegStmt>)specialJoin;
	}
	public HashSet<List> getSynch(){
		return (HashSet<List>)synch;
	}
	public Map<JPegStmt,List> getStartToThread(){
		return startToThread;
	}
	public Map getStartToAllocNodes()
	{
		return (Map)startToAllocNodes;
	}
	protected Map<String, FlowSet> getWaitingNodes(){
		return (Map<String, FlowSet>)waitingNodes;
	}
	public Map<String, Set<JPegStmt>> getNotifyAll(){
		return (Map<String, Set<JPegStmt>>)notifyAll;
	}
	protected Map<AllocNode, String> getAllocNodeToObj(){
		return (Map<AllocNode, String>)allocNodeToObj;
	}
	public Map<AllocNode, PegChain> getAllocNodeToThread(){
		return (Map<AllocNode, PegChain>)allocNodeToThread;
	}
	protected Map getThreadNameToStart(){
		return (Map)threadNameToStart;
	}
	public PegChain getMainPegChain(){
		return (PegChain)mainPegChain;
	}
	public Set getMethodsNeedingInlining(){
		return (Set)methodsNeedingInlining;
	}
	//helper function
	protected void testIterator(){
		System.out.println("********begin test iterator*******");
		Iterator testIt = iterator();
		while (testIt.hasNext()){
			System.out.println(testIt.next());
		}
		System.out.println("********end test iterator*******");
		System.out.println("=======size is: "+size());
	}
	public void testWaitingNodes(){
		System.out.println("------waiting---begin");
		Set maps = waitingNodes.entrySet();
		for(Iterator iter=maps.iterator(); iter.hasNext();){
			Map.Entry entry = (Map.Entry)iter.next();
			System.out.println("---key=  "+entry.getKey());
			FlowSet fs = (FlowSet)entry.getValue();
			if (fs.size()>0){
				
				System.out.println("**waiting nodes set:");
				Iterator it = fs.iterator();
				while (it.hasNext()){
					JPegStmt  unit =(JPegStmt)it.next();
					
					System.out.println(unit.toString());
				}
			}
		}
		System.out.println("------------waitingnodes---ends--------");	
	}
	
	protected void testStartToThread(){
		System.out.println("=====test startToThread ");
		Set maps = startToThread.entrySet();
		for(Iterator iter=maps.iterator(); iter.hasNext();){
			Map.Entry entry = (Map.Entry)iter.next();
			JPegStmt key = (JPegStmt)entry.getKey();
			Tag tag = (Tag)key.getTags().get(0);
			System.out.println("---key=  "+tag+" "+key);
			/*	    List list = (List)entry.getValue();
			 if (list.size()>0){
			 
			 System.out.println("**thread set:");
			 Iterator it = list.iterator();
			 while (it.hasNext()){
			 Chain chain =(Chain)it.next();
			 Iterator chainIt = chain.iterator();
			 
			 System.out.println("the size of chain is: "+chain.size());
			 while (chainIt.hasNext()){
			 JPegStmt stmt = (JPegStmt)chainIt.next();
			 System.out.println(stmt);
			 }
			 }
			 }
			 */
		}
		System.out.println("=========startToThread--ends--------");	
	}
	protected void testUnitToPeg(HashMap unitToPeg){
		System.out.println("=====test unitToPeg ");
		Set maps = unitToPeg.entrySet();
		for(Iterator iter=maps.iterator(); iter.hasNext();){
			Map.Entry entry = (Map.Entry)iter.next();
			System.out.println("---key=  "+entry.getKey());
			JPegStmt s= (JPegStmt)entry.getValue();
			System.out.println("--value= "+s);
		}
		System.out.println("=========unitToPeg--ends--------");	
	}
	
	protected void testUnitToSucc(){
		System.out.println("=====test unitToSucc ");
		Set maps = unitToSuccs.entrySet();
		for(Iterator iter=maps.iterator(); iter.hasNext();){
			Map.Entry entry = (Map.Entry)iter.next();
			JPegStmt key = (JPegStmt)entry.getKey();
			Tag tag = (Tag)key.getTags().get(0);
			System.out.println("---key=  "+tag+" "+key);
			List list = (List)entry.getValue();
			if (list.size()>0){
				
				System.out.println("**succ set: size: "+list.size());
				Iterator it = list.iterator();
				while (it.hasNext()){
					JPegStmt stmt = (JPegStmt)it.next();
					Tag tag1 = (Tag)stmt.getTags().get(0);
					System.out.println(tag1+" "+stmt);
					
					
				}
				
			}
		}
		System.out.println("=========unitToSucc--ends--------");	
	}
	protected void testUnitToPred(){
		System.out.println("=====test unitToPred ");
		Set maps = unitToPreds.entrySet();
		for(Iterator iter=maps.iterator(); iter.hasNext();){
			Map.Entry entry = (Map.Entry)iter.next();
			JPegStmt key = (JPegStmt)entry.getKey();
			Tag tag = (Tag)key.getTags().get(0);
			System.out.println("---key=  "+tag+" "+key);
			List list = (List)entry.getValue();
			//	    if (list.size()>0){
			
			System.out.println("**pred set: size: "+list.size());
			Iterator it = list.iterator();
			while (it.hasNext()){
				JPegStmt stmt = (JPegStmt)it.next();
				Tag tag1 = (Tag)stmt.getTags().get(0);
				System.out.println(tag1+" "+stmt);
				
				
			}
			
			//  }
		}
		System.out.println("=========unitToPred--ends--------");	
	}
	protected void addTag(){
		//add tag for each stmt
		Iterator it = iterator();
//		int count = 0;
		
		while (it.hasNext()){
			JPegStmt stmt = (JPegStmt)it.next();
			int count = Counter.getTagNo();
//			count++;
			StringTag t = new StringTag(Integer.toString(count));
			stmt.addTag(t);
		}
	}
	protected void testSynch(){
		Iterator<List> it = synch.iterator();
		System.out.println("========test synch======");
		while (it.hasNext()){
			//JPegStmt s = (JPegStmt)it.next();
			//Tag tag = (Tag)s.getTags().get(0);
			// System.out.println(tag+" "+s);
			System.out.println(it.next());
		}
		System.out.println("========end test synch======");
	}
	protected void testThreadNameToStart(){
		System.out.println("=====test ThreadNameToStart");
		Set maps = threadNameToStart.entrySet();
		for(Iterator iter=maps.iterator(); iter.hasNext();){
			Map.Entry entry = (Map.Entry)iter.next();
			Object key = entry.getKey();
			
			System.out.println("---key=  "+key);
			JPegStmt stmt  = (JPegStmt)entry.getValue();
			Tag tag1 = (Tag)stmt.getTags().get(0);
			System.out.println("value: "+tag1+" "+stmt);
			
			
		}
		System.out.println("=========ThreadNameToStart--ends--------");
	}
	protected void testJoinStmtToThread(){
		System.out.println("=====test JoinStmtToThread");
		Set maps = threadNameToStart.entrySet();
		for(Iterator iter=maps.iterator(); iter.hasNext();){
			Map.Entry entry = (Map.Entry)iter.next();
			Object key = entry.getKey();
			
			System.out.println("---key=  "+key);
			
			System.out.println("value: "+entry.getValue());
			
			
		}
		System.out.println("=========JoinStmtToThread--ends--------");	
	}
	protected void testPegChain(Chain chain)
	{
		System.out.println("******** chain********");
		Iterator it = chain.iterator();
		while (it.hasNext()) {
			/*Object o = it.next();
			 System.out.println(o);
			 if (!(o instanceof JPegStmt))  System.out.println("not instanceof JPegStmt: "+o);
			 JPegStmt s = (JPegStmt)o;
			 */
			JPegStmt stmt =(JPegStmt)it.next(); 
			System.out.println(stmt.toString());
			/*if (stmt.getName().equals("start")){
			 
			 System.out.println("find start method in : " + stmt.toString() );
			 List list =(List)startToThread.get(stmt);
			 Iterator chainIt = list.iterator();
			 while (chainIt.hasNext()){
			 Chain chain = (Chain)chainIt.next();
			 Iterator subit = chain.iterator();
			 while (subit.hasNext()){
			 System.out.println("**" + ((JPegStmt)subit.next()).toString());
			 }
			 }
			 System.out.println("$$$$$$returing to main chain");
			 }
			 */
		}
		
	}
	protected void computeEdgeAndThreadNo(){
		Iterator it = iterator();
		int numberOfEdge = 0;
		while (it.hasNext()){
			List succList =(List)getSuccsOf(it.next());
			
			numberOfEdge = numberOfEdge + succList.size();
			
		}
		numberOfEdge = numberOfEdge + startToThread.size();
		
		System.err.println("**number of edges: "+numberOfEdge);
		
		System.err.println("**number of threads: "+ (startToThread.size()+ 1 ));
		
		
		
		
		/*	Set keySet = startToThread.keySet();
		 Iterator keyIt = keySet.iterator();
		 while (keyIt.hasNext()){
		 List list = (List)startToThread.get(keyIt.next());
		 System.out.println("********start thread:");
		 Iterator itit = list.iterator();
		 while (itit.hasNext()){
		 System.out.println(it.next());
		 }
		 }
		 */
		
	}
	
	
	protected void testList(List list){
//		System.out.println("test list");
		Iterator listIt = list.iterator();
		while (listIt.hasNext()){
			System.out.println(listIt.next());
		}
	}
	
	protected void testSet(Set set, String name){
		System.out.println("$test set "+name);
		Iterator setIt = set.iterator();
		while (setIt.hasNext()){
			Object s = setIt.next();
			// JPegStmt s = (JPegStmt)setIt.next();
			//Tag tag = (Tag)s.getTags().get(0);
			System.out.println(s);
		}
	}
	
	public void testMonitor(){
		System.out.println("=====test monitor size: "+monitor.size());
		Set maps = monitor.entrySet();
		for(Iterator iter=maps.iterator(); iter.hasNext();){
			Map.Entry entry = (Map.Entry)iter.next();
			String key = (String)entry.getKey();
			
			System.out.println("---key=  "+key);
			FlowSet list = (FlowSet)entry.getValue();
			if (list.size()>0){
				
				System.out.println("**set:  "+list.size());
				Iterator it = list.iterator();
				while (it.hasNext()){
					Object obj = it.next();
					if (obj instanceof JPegStmt){
						JPegStmt stmt = (JPegStmt)obj;
						Tag tag1 = (Tag)stmt.getTags().get(0);
						System.out.println(tag1+" "+stmt);
					}
					else{
						System.out.println("---list---");
						Iterator listIt = ((List)obj).iterator();
						while (listIt.hasNext()){
							Object oo = listIt.next();
							if (oo instanceof JPegStmt){
								JPegStmt  unit = (JPegStmt)oo;
								Tag tag = (Tag)unit.getTags().get(0);
								System.out.println(tag+" "+unit);
							}
							else
								System.out.println(oo);
						}
						System.out.println("---list--end-");
					}
					
				}
				
			}
		}
		System.out.println("=========monitor--ends--------");
	} 
	
	
}
