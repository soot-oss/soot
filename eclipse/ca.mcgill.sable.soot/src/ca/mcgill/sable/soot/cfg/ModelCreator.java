/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package ca.mcgill.sable.soot.cfg;

import soot.toolkits.graph.*;
import ca.mcgill.sable.soot.cfg.model.*;
import java.util.*;
import ca.mcgill.sable.soot.*;
import org.eclipse.ui.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import soot.toolkits.graph.interaction.*;
import soot.toolkits.scalar.*;
import soot.*;



public class ModelCreator {

	private DirectedGraph sootGraph;
	private CFGGraph model;
	private IResource resource;
	private String edName = "CFG Editor";
	private HashMap nodeMap = new HashMap();
	
	
	public ModelCreator() {
	}
	
	public void buildModel(CFGGraph cfgGraph){
		cfgGraph.setResource(getResource());
		Iterator nodesIt = getSootGraph().iterator();
		ArrayList nodeList = new ArrayList();
		ArrayList edgeList = new ArrayList();
		 
		boolean isExceptions = false; 
		ArrayList exceptHeads = null;
		if (getSootGraph().getHeads().size() > 1) {
			isExceptions = true;
		} 
		// handle graphs that have exceptions
		if (getSootGraph() instanceof UnitGraph){
			UnitGraph unitGraph = (UnitGraph)getSootGraph();
			if (isExceptions){
				exceptHeads = findExceptionBlockHeads(unitGraph.getBody());
			}
		}
		while (nodesIt.hasNext()){
			Object node = nodesIt.next();
			CFGNode cfgNode;
			if (!getNodeMap().containsKey(node)){
				cfgNode = new CFGNode();
				initializeNode(node, cfgNode, cfgGraph);
				getNodeMap().put(node, cfgNode);
			}
			else {
				cfgNode = (CFGNode)getNodeMap().get(node);
			}
			Iterator succIt = getSootGraph().getSuccsOf(node).iterator();
			while (succIt.hasNext()){
				Object succ = succIt.next();
				CFGNode cfgSucc;
				if (!getNodeMap().containsKey(succ)){
					cfgSucc = new CFGNode();
					initializeNode(succ, cfgSucc, cfgGraph);	
					getNodeMap().put(succ, cfgSucc);
				}
				else {
					cfgSucc = (CFGNode)getNodeMap().get(succ);
				}
				CFGEdge cfgEdge = new CFGEdge(cfgNode, cfgSucc);
				
			}
		}
		Iterator headsIt = getSootGraph().getHeads().iterator();
		while (headsIt.hasNext()){
			Object next = headsIt.next();
			CFGNode node = (CFGNode)getNodeMap().get(next);
			if ((exceptHeads != null) && exceptHeads.contains(next)) continue;
			node.getData().setHead(true);
		}
		Iterator tailsIt = getSootGraph().getTails().iterator();
		while (tailsIt.hasNext()){
			Object next = tailsIt.next();
			CFGNode node = (CFGNode)getNodeMap().get(next);
			node.getData().setTail(true);
		}
		
		setModel(cfgGraph);
		
	}
	
	private boolean canFit(CFGPartialFlowData pFlow, int length){
		Iterator it = pFlow.getChildren().iterator();
		int total = 0;
		while (it.hasNext()){
			String next = ((CFGFlowInfo)it.next()).getText();
			total += next.length();
		}
		if (total + length < 60) return true;
		return false;
	}
	
	public void highlightNode(soot.Unit u){
		Iterator it = getNodeMap().keySet().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next.equals(u)){
				CFGNode node = (CFGNode)getNodeMap().get(next);
				node.handleHighlightEvent(next);
			}
		}
	}
	
	public ArrayList findExceptionBlockHeads(Body b){
		ArrayList exceptHeads = new ArrayList();
		Iterator trapsIt = b.getTraps().iterator();
		while (trapsIt.hasNext()){
			Trap trap = (Trap)trapsIt.next();
			exceptHeads.add(trap.getBeginUnit());
		}		
		return exceptHeads;
	}
	
	public void updateNode(FlowInfo fi){
		Iterator it = getNodeMap().keySet().iterator();
		while (it.hasNext()){
			Object next = it.next();
			if (next.equals(fi.unit())){
				CFGNode node = (CFGNode)getNodeMap().get(next);
				getModel().newFlowData();
				CFGFlowData data = new CFGFlowData();
				if (fi.isBefore()){
					node.setBefore(data);
				}
				else{
					node.setAfter(data);
				}
				if (fi.info() instanceof FlowSet){
					FlowSet fs = (FlowSet)fi.info();
					Iterator fsIt = fs.iterator();
					CFGFlowInfo startBrace = new CFGFlowInfo();
					CFGPartialFlowData nextFlow = new CFGPartialFlowData();
					data.addChild(nextFlow);
					nextFlow.addChild(startBrace);
					startBrace.setText("{");
					
					while (fsIt.hasNext()){
						Object elem = fsIt.next();
						CFGFlowInfo info = new CFGFlowInfo();
						if (canFit(nextFlow, elem.toString().length())){
							nextFlow.addChild(info);
						}
						else {
							nextFlow = new CFGPartialFlowData();
							data.addChild(nextFlow);
							nextFlow.addChild(info);
						}
						
						info.setText(elem.toString());
						if(fsIt.hasNext()){
							CFGFlowInfo comma = new CFGFlowInfo();
							nextFlow.addChild(comma);
							comma.setText(", ");
						}
					}
					CFGFlowInfo endBrace = new CFGFlowInfo();
					nextFlow.addChild(endBrace);
					endBrace.setText("}");
				}
				else {
					String text = fi.info().toString();
					ArrayList textGroups = new ArrayList();
					int last = 0;
					for (int i = 0; i < text.length()/50; i++){
						if (last+50 < text.length()){
							int nextComma = text.indexOf(",", last+50);
							if (nextComma != -1){
								textGroups.add(text.substring(last, nextComma+1));
								last = nextComma+2;
							}
						}
					}
					if (last < text.length()){
						textGroups.add(text.substring(last));
					}
					
					Iterator itg = textGroups.iterator();
					while (itg.hasNext()){
						String nextGroup = (String)itg.next();
						CFGFlowInfo info = new CFGFlowInfo();
						CFGPartialFlowData pFlow = new CFGPartialFlowData();
						data.addChild(pFlow);
						pFlow.addChild(info);
						info.setText(nextGroup);
					}
				}
				
			}
		}
	}
	
	private void initializeNode(Object sootNode, CFGNode cfgNode, CFGGraph cfgGraph){
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
				textList.add(u);
			}
		}
		else {
			textList.add(sootNode);
			width = sootNode.toString().length();
		}
		
		cfgGraph.addChild(cfgNode);
		
		CFGNodeData nodeData = new CFGNodeData();
		cfgNode.setData(nodeData);
		
		nodeData.setText(textList);
	}
	
	IEditorPart part;

	public void displayModel(){
		IWorkbenchPage page = SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try{
			CFGGraph cfgGraph = new CFGGraph();
			cfgGraph.setName("cfgGraph");
			
			part = page.openEditor(cfgGraph, "ca.mcgill.sable.soot.cfg.CFGEditor");
            if (part instanceof CFGEditor){
				((CFGEditor)part).setTitle(getEdName());
				((CFGEditor)part).setTitleTooltip(getEdName());
			}
			buildModel(cfgGraph);
		}
        catch (PartInitException ex){
        	System.out.println("error message: "+ex.getMessage());
            System.out.println("part kind: "+part.getClass());
			ex.printStackTrace();
		}
        catch(Exception e){
        	System.out.println("exception error msg: "+e.getMessage());
        	System.out.println("error type: "+e.getClass());
        	e.printStackTrace();
        }
	}
	
	public void setEditorName(String name){
		edName = name;
	}
	
	public String getEditorName(){
		return edName;
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

	/**
	 * @return
	 */
	public String getEdName() {
		return edName;
	}

	/**
	 * @return
	 */
	public HashMap getNodeMap() {
		return nodeMap;
	}

	/**
	 * @param string
	 */
	public void setEdName(String string) {
		edName = string;
	}

	/**
	 * @param map
	 */
	public void setNodeMap(HashMap map) {
		nodeMap = map;
	}

}
