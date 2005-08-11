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


package ca.mcgill.sable.graph.testing;
import org.eclipse.ui.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import ca.mcgill.sable.graph.*;
import ca.mcgill.sable.graph.model.*;
import org.eclipse.core.runtime.*;
import java.util.*;
import java.lang.reflect.*;


public class GraphGenerator implements IWorkbenchWindowActionDelegate {

	private ArrayList children = new ArrayList();
	private Graph graph;
	
	
	public GraphGenerator() {
	}
	
	public void run(IAction action){
		IWorkbenchPage page = GraphPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try{
			setGraph(new Graph());
			IEditorPart part = page.openEditor(graph, "ca.mcgill.sable.graph.GraphEditor");
			handleChildren();
		}
		catch (PartInitException e3){
			e3.printStackTrace();
		}
		catch (Exception e2){
			e2.printStackTrace();
		}
	}
	
	public void buildModel(){
		
		GraphPlugin.getDefault().setGenerator(this);
		TestNode p1 = new TestNode();
		p1.setData("P1");
		
		TestNode p2 = new TestNode();
		p2.setData("P2");
		
		
		p1.addOutput(p2);
		
		TestNode c1 = new TestNode();
		TestNode c2 = new TestNode();
		TestNode c3 = new TestNode();
		
		p1.addChild(c1);
		p1.addChild(c2);
		p1.addChild(c3);
		
		c1.addOutput(c2);
		c1.addOutput(c3);
		c1.addOutput(p2);
		c3.addOutput(p2);
		
		c1.setData("C1");
		c2.setData("C2");
		c3.setData("C3");
		
		TestNode c4 = new TestNode();
		TestNode c5 = new TestNode();
		p2.addChild(c4);
		p2.addChild(c5);
		
		c4.addOutput(c5);
		c1.addOutput(c4);
		c3.addOutput(c4);
		p1.addOutput(c4);
		
		c4.setData("C4");
		c5.setData("C5");
		
		
		getChildren().add(p1);
		getChildren().add(p2);
		
		handleChildren();
	}
	
	HashMap map;
	
	private void handleChildren(){
		getGraph().removeAllChildren();
		map = new HashMap();
		Iterator it = getChildren().iterator();
		while (it.hasNext()){
			TestNode tn = (TestNode)it.next();
			
			
			SimpleNode sn = new SimpleNode();
			getGraph().addChild(sn);
			sn.setData(tn.getData());
			sn.setChildren(tn.getChildren());
			
			map.put(tn, sn);
		}
		
		Iterator it2 = getChildren().iterator();
		while (it2.hasNext()){
			TestNode tn = (TestNode)it2.next();
			
			
			SimpleNode src = (SimpleNode)map.get(tn);
			Iterator eIt = tn.getOutputs().iterator();
			
			while (eIt.hasNext()){
				Object endTn = eIt.next();
				SimpleNode tgt = (SimpleNode)map.get(endTn);
				if (tgt != null){
					Edge e = new Edge(src, tgt);
				}		
			}
		}
	}
	
	public void expandGraph(SimpleNode node){
		Iterator it = getChildren().iterator();
		TestNode tn = null;
		while (it.hasNext()){
			tn = (TestNode)it.next();
			if (map.get(tn).equals(node)) break;
		}
		if (tn != null){
			if (tn.getChildren().size() > 0){
				getChildren().remove(tn);
			}
		}
		Iterator childIt = node.getChildren().iterator();
		while (childIt.hasNext()){
			TestNode test = (TestNode)childIt.next();
			getChildren().add(test);
		}
		handleChildren();
	}
	
	public void dispose(){
	}
	
	public void selectionChanged(IAction action, ISelection sel){
	}
	
	public void init(IWorkbenchWindow window){
	}
	/**
	 * @return
	 */
	public ArrayList getChildren() {
		return children;
	}

	/**
	 * @param list
	 */
	public void setChildren(ArrayList list) {
		children = list;
	}

	/**
	 * @return
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * @param graph
	 */
	public void setGraph(Graph graph) {
		this.graph = graph;
	}

}
