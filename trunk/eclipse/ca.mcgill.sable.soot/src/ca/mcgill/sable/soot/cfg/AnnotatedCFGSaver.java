/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Jennifer Lhotak
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
import soot.util.dot.*;
import ca.mcgill.sable.soot.cfg.model.*;
import java.util.*;

public class AnnotatedCFGSaver {

	private CFGGraph graph;
	private String fileNameBase;
	private String title;

	public AnnotatedCFGSaver(CFGGraph graph, String fileNameBase, String title) {
		this.graph = graph;
		this.fileNameBase = fileNameBase;
		this.title = title;
	}

	public void saveGraph(){
		DotGraph canvas = initGraph();
		HashMap nodes = makeNodes(canvas);
		makeEdges(canvas, nodes);
		formatNames(canvas, nodes);
		String fileName = formFileName();
		System.out.println("cfg fileName: "+fileName);
		canvas.plot(fileName);
	}
	
	private DotGraph initGraph(){
		DotGraph canvas = new DotGraph(title);
		canvas.setGraphLabel(title);
		canvas.setGraphSize(8.0, 10.0);
		canvas.setOrientation(DotGraphConstants.GRAPH_ORIENT_PORTRAIT);
		canvas.setNodeShape(DotGraphConstants.NODE_SHAPE_PLAINTEXT);
		return canvas;
	}
	
	private HashMap makeNodes(DotGraph canvas){
		HashMap nodeMap = new HashMap();
		Iterator it = graph.getChildren().iterator();
		int count = 0;
		while (it.hasNext()){
			CFGNode next = (CFGNode)it.next();
			String nodeName = "n"+count;
			nodeMap.put(next, nodeName);
			canvas.drawNode(nodeName);
			count++;
		}
		return nodeMap;
	}
	
	private void makeEdges(DotGraph canvas, HashMap nodeMap){
		Iterator it = nodeMap.keySet().iterator();
		while (it.hasNext()){
			CFGNode node = (CFGNode)it.next();
			String nodeName = (String)nodeMap.get(node);
			Iterator inputs = node.getInputs().iterator();
			while (inputs.hasNext()){
				CFGEdge edge = (CFGEdge)inputs.next();
				CFGNode src = edge.getFrom();
				String srcName = (String)nodeMap.get(src);
				canvas.drawEdge(srcName, nodeName);
			}
			
		}
	}
	
	private void formatNames(DotGraph canvas, HashMap nodeMap){
		Iterator it = nodeMap.keySet().iterator();
		while (it.hasNext()){
			CFGNode node = (CFGNode)it.next();
			String nodeName = (String)nodeMap.get(node);
			DotGraphNode dgNode = canvas.getNode(nodeName);
			dgNode.setHTMLLabel(getNodeLabel(node));
		}
	}
	
	private String getNodeLabel(CFGNode node){
		StringBuffer sb = new StringBuffer();
		sb.append("<<TABLE BORDER=\"0\">");
		sb.append("<TR><TD>");
		if (node.getBefore() != null) {
			
			Iterator before = node.getBefore().getChildren().iterator();
			while (before.hasNext()){
				Iterator pFlowData = ((CFGPartialFlowData)before.next()).getChildren().iterator();
				while (pFlowData.hasNext()){
					CFGFlowInfo info = (CFGFlowInfo)pFlowData.next();
					String temp = info.getText();
					temp = soot.util.StringTools.replaceAll(temp, "<", "&lt;");
					temp = soot.util.StringTools.replaceAll(temp, ">", "&gt;");
					sb.append(temp);
					
				}
			}
		}
		sb.append("</TD></TR>");
		sb.append("<TR><TD>");
		sb.append("<TABLE>");
		sb.append("<TR><TD>");
		Iterator data = node.getData().getText().iterator();
		while (data.hasNext()){
			String temp = data.next().toString();
			temp = soot.util.StringTools.replaceAll(temp, "<", "&lt;");
			temp = soot.util.StringTools.replaceAll(temp, ">", "&gt;");
			sb.append(temp);
		}
		sb.append("</TD></TR>");
		sb.append("</TABLE>");
		sb.append("</TD></TR>");
		sb.append("<TR><TD>");
		if (node.getAfter() != null){
			Iterator after = node.getAfter().getChildren().iterator();
			while (after.hasNext()){
				Iterator pFlowData = ((CFGPartialFlowData)after.next()).getChildren().iterator();
				while (pFlowData.hasNext()){
					CFGFlowInfo info = (CFGFlowInfo)pFlowData.next();
					String temp = info.getText();
					temp = soot.util.StringTools.replaceAll(temp, "<", "&lt;");
					temp = soot.util.StringTools.replaceAll(temp, ">", "&gt;");
					sb.append(temp);
				}
			}
		}
		sb.append("</TD></TR>");
		sb.append("</TABLE>>");
		System.out.println("node data string: "+sb.toString());
		return sb.toString();
	}
	
	private String formFileName(){
		StringBuffer sb = new StringBuffer();
		sb.append(fileNameBase);
		String sep = System.getProperty("file.separator");
		sb.append(sep);
		sb.append(title);
		sb.append(".cfg");
		return sb.toString();
	}
}

