/*
 * Created on Jan 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg;

import soot.toolkits.graph.DirectedGraph;
import ca.mcgill.sable.soot.cfg.model.*;
import java.util.*;
import ca.mcgill.sable.soot.*;
import org.eclipse.ui.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;


/**
 * @author jlhotak
 *
 * this class has to either get a soot dg or talk
 * with soot for the parts of a soot dg
 */

public class ModelCreator {

	private DirectedGraph sootGraph;
	private CFGGraph model;
	private IResource resource;
	
	/**
	 * 
	 */
	public ModelCreator() {
	}
	
	public void buildModel(CFGGraph cfgGraph){
		System.out.println("creating model");
		cfgGraph.setResource(getResource());
		Iterator nodesIt = getSootGraph().iterator();
		HashMap nodeMap = new HashMap();
		ArrayList nodeList = new ArrayList();
		ArrayList edgeList = new ArrayList();
		 
		while (nodesIt.hasNext()){
			Object node = nodesIt.next();
			CFGNode cfgNode;
			if (!nodeMap.containsKey(node)){
				cfgNode = new CFGNode();
				//cfgGraph.addChild(cfgNode);
				initializeNode(node, cfgNode);
				//if (node instanceof soot.toolkits.graph.Block)
				//cfgNode.setWidth(node.toString().length() * 7);
				//cfgNode.setText(node.toString());
				nodeMap.put(node, cfgNode);
				cfgGraph.addChild(cfgNode);
				//nodeList.add(cfgNode);
			}
			else {
				cfgNode = (CFGNode)nodeMap.get(node);
			}
			Iterator succIt = getSootGraph().getSuccsOf(node).iterator();
			while (succIt.hasNext()){
				Object succ = succIt.next();
				CFGNode cfgSucc;
				if (!nodeMap.containsKey(succ)){
					cfgSucc = new CFGNode();
					//cfgGraph.addChild(cfgSucc);
					initializeNode(succ, cfgSucc);	
					//cfgSucc.setWidth(succ.toString().length() * 7);
					//System.out.println("succ text: "+succ.toString());
					//cfgSucc.setText(succ.toString());
					nodeMap.put(succ, cfgSucc);
					cfgGraph.addChild(cfgSucc);
					//nodeList.add(cfgSucc);
				}
				else {
					cfgSucc = (CFGNode)nodeMap.get(succ);
				}
				CFGEdge cfgEdge = new CFGEdge(cfgNode, cfgSucc);
				//cfgGraph.getEdges().add(cfgEdge);
				//edgeList.add(cfgEdge);
			}
		}
		
		Iterator headsIt = getSootGraph().getHeads().iterator();
		while (headsIt.hasNext()){
			Object next = headsIt.next();
			CFGNode node = (CFGNode)nodeMap.get(next);
			node.setHead(true);
		}
		
		Iterator tailsIt = getSootGraph().getTails().iterator();
		while (tailsIt.hasNext()){
			Object next = tailsIt.next();
			CFGNode node = (CFGNode)nodeMap.get(next);
			node.setTail(true);
		}
		
		//cfgGraph.setNodes(nodeList);
		//cfgGraph.setEdges(edgeList);
		setModel(cfgGraph);
		
	}
	
	private void initializeNode(Object sootNode, CFGNode cfgNode){
		ArrayList textList = new ArrayList();
		int width = 0;
		if (sootNode instanceof soot.toolkits.graph.Block){
			soot.toolkits.graph.Block block = (soot.toolkits.graph.Block)sootNode;
			Iterator it = block.iterator();
			while (it.hasNext()){
				soot.Unit u = (soot.Unit)it.next();
				if (width < u.toString().length()){
					width = u.toString().length();
				}
				textList.add(u.toString());
			}
		}
		else {
			textList.add(sootNode.toString());
			width = sootNode.toString().length();
		}
		cfgNode.setText(textList);
		cfgNode.setWidth(width*7);
	}

	public void displayModel(){
		System.out.println("displaying model");
		IWorkbenchPage page = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try{
			CFGGraph cfgGraph = new CFGGraph();
			IEditorPart part = page.openEditor(cfgGraph, "ca.mcgill.sable.soot.cfg.CFGEditor");
			buildModel(cfgGraph);
			//page.activate(part);
			//System.out.println(part.getEditorInput().getAdapter(IResource.class));
		}
		catch (CoreException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 */
	public DirectedGraph getSootGraph() {
		return sootGraph;
	}

	/**
	 * @param graph
	 */
	public void setSootGraph(DirectedGraph graph) {
		sootGraph = graph;
	}

	/**
	 * @return
	 */
	public CFGGraph getModel() {
		return model;
	}

	/**
	 * @param graph
	 */
	public void setModel(CFGGraph graph) {
		model = graph;
	}

	/**
	 * @return
	 */
	public IResource getResource() {
		return resource;
	}

	/**
	 * @param resource
	 */
	public void setResource(IResource resource) {
		this.resource = resource;
	}

}
