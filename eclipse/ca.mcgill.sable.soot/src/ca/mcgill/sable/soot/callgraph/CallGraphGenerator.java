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

/*
 * Created on Mar 5, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.callgraph;
import org.eclipse.ui.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import ca.mcgill.sable.graph.*;
import ca.mcgill.sable.graph.model.*;
import org.eclipse.core.runtime.*;
import java.util.*;
import java.lang.reflect.*;
import soot.jimple.toolkits.annotation.callgraph.*;
import soot.*;
import soot.tagkit.*;
import ca.mcgill.sable.soot.interaction.*;
import ca.mcgill.sable.soot.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.plugin.*;
import soot.toolkits.graph.interaction.*;
import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.*;
import org.eclipse.ui.texteditor.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CallGraphGenerator {
	
	private CallGraphInfo info;
	private Graph graph;
	private InteractionController controller;
	private ArrayList centerList;
	
	/**
	 * 
	 */
	public CallGraphGenerator() {
	    System.out.println("creating call graph generator");
    }
	
	public void run(){
		
		IWorkbench workbench = SootPlugin.getDefault().getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();;
		
		try{
			setGraph(new Graph());
			IEditorPart part = page.openEditor(graph, "ca.mcgill.sable.graph.GraphEditor");
			((GraphEditor)part).setPartFactory(new CallGraphPartFactory());
			//CGMenuProvider provider = new CGMenuProvider(((GraphEditor)part).getGraphEditorGraphicalViewer(), ((GraphEditor)part).getGraphEditorActionRegistry());
			addActions((GraphEditor)part);	
			((GraphEditor)part).setMenuProvider(new CGMenuProvider(((GraphEditor)part).getGraphEditorGraphicalViewer(), ((GraphEditor)part).getGraphEditorActionRegistry(), part));
			buildModel();
		}
		catch (PartInitException e3){
			e3.printStackTrace();
		}
		/*catch (CoreException e){
			e.printStackTrace();
		}*/
		catch (Exception e2){
			e2.printStackTrace();
		}
	}
	
	public void addActions(GraphEditor part){
		ShowCodeAction showCode = new ShowCodeAction((IWorkbenchPart)part);
		part.getGraphEditorActionRegistry().registerAction(showCode);
		part.getGraphEditorSelectionActions().add(showCode.getId());
	
		ExpandAction expand = new ExpandAction((IWorkbenchPart)part);
		part.getGraphEditorActionRegistry().registerAction(expand);
		part.getGraphEditorSelectionActions().add(expand.getId());
		
		CollapseAction collapse = new CollapseAction((IWorkbenchPart)part);
		part.getGraphEditorActionRegistry().registerAction(collapse);
		part.getGraphEditorSelectionActions().add(collapse.getId());
			
	}
	
	public void buildModel(){
		CallGraphNode cgn = new CallGraphNode();
		getGraph().addChild(cgn);
		cgn.setGenerator(this);
		cgn.setData(getInfo().getCenter());

		//addToCenterList(getInfo().getCenter());
		cgn.setExpand(false);
		makeCons(getInfo(), cgn);
			
	}
	
	private CallGraphNode getNodeForMethod(SootMethod meth){
		CallGraphNode node = null;
		Iterator it = getGraph().getChildren().iterator();
		while (it.hasNext()){
			CallGraphNode next = (CallGraphNode)it.next();
			if (next.getData().equals(meth)){
				node = next;
			}
		} 
		if (node == null){
			node = new CallGraphNode();
			getGraph().addChild(node);
			node.setData(meth);
		}
		return node;
	}
	
	private void makeCons(CallGraphInfo info, CallGraphNode center){
		Iterator it1 = info.getInputs().iterator();
		while (it1.hasNext()){
			SootMethod sm = ((MethInfo)it1.next()).method();
			CallGraphNode inNode = getNodeForMethod(sm);
			inNode.setGenerator(this);
			Edge inEdge = new Edge(inNode, center);
		}
		
		Iterator it2 = info.getOutputs().iterator();
		while (it2.hasNext()){
			SootMethod sm = ((MethInfo)it2.next()).method();
			//System.out.println("making target connection for: "+sm);
			CallGraphNode outNode = getNodeForMethod(sm);
			outNode.setGenerator(this);
			Edge inEdge = new Edge(center, outNode);
		}	
	}
	
	public void collapseGraph(CallGraphNode node){
		System.out.println("should remove unwanted nodes");
		// need to undo (remove in and out nodes
		// who are not in center list)
		ArrayList inputsToRemove = new ArrayList();
		ArrayList outputsToRemove = new ArrayList();
		ArrayList nodesToRemove = new ArrayList();
		
		if (node.getInputs() != null){
			Iterator inIt = node.getInputs().iterator();
			while (inIt.hasNext()){
				Edge next = (Edge)inIt.next();
				CallGraphNode src = (CallGraphNode)next.getSrc();
				//System.out.println("next to remove: "+next.getClass());
				//if (!getCenterList().contains(src.getData())){
				if (src.isLeaf()){
					//System.out.println("removing: "+src);
					inputsToRemove.add(next);
					nodesToRemove.add(src);
				}
			}
		}
		
		if (node.getOutputs() != null){
			Iterator outIt = node.getOutputs().iterator();
			while (outIt.hasNext()){
				Edge next = (Edge)outIt.next();
				CallGraphNode tgt = (CallGraphNode)next.getTgt();
				//if (!getCenterList().contains(tgt.getData())){
				if (tgt.isLeaf()){
					//System.out.println("removing: "+tgt);
					outputsToRemove.add(next);
					nodesToRemove.add(tgt);
				}
			}
		}
		
		Iterator inRIt = inputsToRemove.iterator();
		while (inRIt.hasNext()){
			Edge temp = (Edge)inRIt.next();
			//System.out.println("removing edge: src: "+temp.getSrc().getData()+" tgt: "+temp.getTgt().getData());
			node.removeInput(temp);
		}
		
		Iterator outRIt = outputsToRemove.iterator();
		while (outRIt.hasNext()){
			Edge temp = (Edge)outRIt.next();
			//System.out.println("removing edge: src: "+temp.getSrc().getData()+" tgt: "+temp.getTgt().getData());
			
			node.removeInput(temp);
		}
		
		Iterator nodeRIt = nodesToRemove.iterator();
		while (nodeRIt.hasNext()){
			CallGraphNode temp = (CallGraphNode)nodeRIt.next();
			//System.out.println("removing node: "+temp.getData());
			temp.removeAllInputs();
			temp.removeAllOutputs();
			getGraph().removeChild(temp);
		}
		
		node.setExpand(true);
		//
	}
	
	public void expandGraph(CallGraphNode node){
		System.out.println("should expand graph");
	
			getController().setEvent(new InteractionEvent(IInteractionConstants.CALL_GRAPH_NEXT_METHOD, node.getData()));
			getController().handleEvent();
	
	}
	
	public void showInCode(CallGraphNode node){
		SootMethod meth = (SootMethod)node.getData();
		String sootClassName = meth.getDeclaringClass().getName();
		sootClassName = sootClassName.replaceAll("\\.", System.getProperty("file.separator"));
		sootClassName = sootClassName + ".java";
		String sootMethName = meth.getName();
		
		System.out.println("soot classname to find: "+sootClassName);
		IProject [] progs = SootPlugin.getWorkspace().getRoot().getProjects();
		IResource fileToOpen = null;
		for (int i = 0; i < progs.length; i++){
			IProject project = progs[i];
			
			IJavaProject jProj = JavaCore.create(project);
			try {
	
				IPackageFragmentRoot [] roots = jProj.getAllPackageFragmentRoots();
				for (int j = 0; j < roots.length; j++){
				//System.out.println(roots[i].getResource());
					if (!(roots[j].getResource() instanceof IContainer)) continue;
					fileToOpen = ((IContainer)roots[j].getResource()).findMember(sootClassName);
					if (fileToOpen == null) continue;
					else break;		
				}
			}
			catch(Exception e){
			}
			System.out.println("project: "+project);
			
			if (fileToOpen != null) break;
		}
		
		
		
		IWorkbench workbench = SootPlugin.getDefault().getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();;
		
		try{
			System.out.println("file to open: "+fileToOpen);
			IEditorPart part = page.openEditor((IFile)fileToOpen);
			SourceLnPosTag methTag = (SourceLnPosTag)meth.getTag("SourceLnPosTag");
			if (methTag != null){
				
				int selOffset = ((AbstractTextEditor)part).getDocumentProvider().getDocument(part.getEditorInput()).getLineOffset(methTag.startLn()-1);
			
				((AbstractTextEditor)SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()).selectAndReveal(selOffset, 0);
				//((AbstractTextEditor)SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()).setHighlightRange(selOffset, 1, true);
			}
					
		}
		catch (PartInitException e3){
			e3.printStackTrace();
		}
		/*catch (CoreException e){
			e.printStackTrace();
		}*/
		catch (Exception e2){
			e2.printStackTrace();
		}
	}
	
	public void addToGraph(Object info){
		CallGraphInfo cgInfo = (CallGraphInfo)info;
		
		SootMethod center = cgInfo.getCenter();
		
		System.out.println("adding to graph: "+center.getName());
		// find the center who is already in the graph
		CallGraphNode centerNode = getNodeForMethod(cgInfo.getCenter());
		//addToCenterList(cgInfo.getCenter());
		centerNode.setExpand(false);
		// make connections to all the children
		makeCons(cgInfo, centerNode);
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

	/**
	 * @return
	 */
	public CallGraphInfo getInfo() {
		return info;
	}

	/**
	 * @param info
	 */
	public void setInfo(CallGraphInfo info) {
		this.info = info;
	}

	/**
	 * @return
	 */
	public InteractionController getController() {
		return controller;
	}

	/**
	 * @param controller
	 */
	public void setController(InteractionController controller) {
		this.controller = controller;
	}
	
	public void addToCenterList(Object obj){
		if (getCenterList() == null){
			setCenterList(new ArrayList());
		}
		getCenterList().add(obj);
	}

	/**
	 * @return
	 */
	public ArrayList getCenterList() {
		return centerList;
	}

	/**
	 * @param list
	 */
	public void setCenterList(ArrayList list) {
		centerList = list;
	}

}
