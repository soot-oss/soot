/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Sable Research Group
 *
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
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

/* Reference Version: $SootVersion: 1.2.5.dev.1 $ */
package soot.jimple.toolkits.thread.mhp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.toolkits.graph.*;
//import soot.toolkits.mhp.*;
import soot.util.dot.*;
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

public class PegCallGraphToDot {

	private static final Logger logger =LoggerFactory.getLogger(PegCallGraphToDot.class);
	
	/* make all control fields public, allow other soot class dump 
	 * the graph in the middle */
	
	
	public static boolean isBrief      = false;
	private static final Map<Object, String> listNodeName = new HashMap<Object, String>();
	
	/* in one page or several pages of 8.5x11 */
	public static boolean onepage      = true;
	
	public PegCallGraphToDot(DirectedGraph graph, boolean onepage, String name) {
		
		PegCallGraphToDot.onepage = onepage;
		toDotFile(name, graph,"PegCallGraph");
		
		
		
	}
	
	
	
	/*public PegToDotFile(PegGraph graph, boolean onepage, String name) {
	 this.onepage = onepage;
	 toDotFile(name, graph,"Simple graph");
	 }
	 */
	
	private static int nodecount = 0;
	
	/**
	 * Generates a dot format file for a DirectedGraph
	 * @param methodname, the name of generated dot file
	 * @param graph, a directed control flow graph (UnitGraph, BlockGraph ...)
	 * @param graphname, the title of the graph
	 */
	public static void toDotFile(String methodname, 
			DirectedGraph graph, 
			String graphname) {
		int sequence=0;
		// this makes the node name unique
		nodecount = 0; // reset node counter first.
		Hashtable nodeindex = new Hashtable(graph.size());
		
		// file name is the method name + .dot
		DotGraph canvas = new DotGraph(methodname);
		//logger.info("onepage is:"+onepage);
		if (!onepage) {
			canvas.setPageSize(8.5, 11.0);
		}
		
		canvas.setNodeShape(DotGraphConstants.NODE_SHAPE_BOX);
		canvas.setGraphLabel(graphname);
		
		Iterator nodesIt = graph.iterator();
		
		{
			while (nodesIt.hasNext()){
				Object node = nodesIt.next();
				
				if (node instanceof List){
					String listName = "list" + (new Integer(sequence++)).toString();
					String nodeName = makeNodeName(getNodeOrder(nodeindex, listName));
					listNodeName.put(node, listName);
//					logger.info("put node: "+node +"into listNodeName");
					
				}
			}
		}
		
		nodesIt = graph.iterator();
		while (nodesIt.hasNext()) {
			Object node = nodesIt.next();
			String nodeName = null;
			if (node instanceof List){
				
				nodeName = makeNodeName(getNodeOrder(nodeindex,  listNodeName.get(node)));   
			}
			else{
				
				nodeName = makeNodeName(getNodeOrder(nodeindex, node));
			}
			Iterator succsIt = graph.getSuccsOf(node).iterator();
			
			while (succsIt.hasNext()) {
				Object s= succsIt.next();
				String succName = null;
				if (s instanceof List){
					succName = makeNodeName(getNodeOrder(nodeindex,  listNodeName.get(s)));
				}
				else{
					Object succ = s;
					//	logger.info("$$$$$$succ: "+succ);
//					nodeName = makeNodeName(getNodeOrder(nodeindex, tag+" "+node));
					succName = makeNodeName(getNodeOrder(nodeindex, succ));
					//  logger.info("node is :" +node);
//					logger.info("find start node in pegtodotfile:"+node);
				}
				
				
				canvas.drawEdge(nodeName, succName);
			}     
			
		}
		
		
		
		
		// set node label
		if (!isBrief) {
			nodesIt = nodeindex.keySet().iterator();
			while (nodesIt.hasNext()) {
				Object node = nodesIt.next();
//				logger.info("node is:"+node);
				if (node != null){
					//logger.info("node: "+node);
					String nodename = makeNodeName(getNodeOrder(nodeindex, node));
					//logger.info("nodename: "+ nodename);
					DotGraphNode dotnode = canvas.getNode(nodename);
					//logger.info("dotnode: "+dotnode);
					if (dotnode != null)	dotnode.setLabel(node.toString());
				}
			}
		}
		
		canvas.plot("pecg.dot");
		
		//clean up
		listNodeName.clear();
	} 
	
	private static int getNodeOrder(Hashtable<Object,Integer> nodeindex, Object node){
		
		Integer index = nodeindex.get(node);
		if (index == null) {
			index = new Integer(nodecount++);
			nodeindex.put(node, index);
		}
//		logger.info("order is:"+index.intValue());
		return index.intValue();
	}
	
	private static String makeNodeName(int index){
		return "N"+index;
	}
}
