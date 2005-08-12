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
import org.eclipse.ui.part.*;

public class CallGraphGenerator {
	
	private CallGraphInfo info;
	private Graph graph;
	private InteractionController controller;
	private ArrayList centerList;
	

	public CallGraphGenerator() {
	}
	
	public void run(){
		
		IWorkbench workbench = SootPlugin.getDefault().getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();;
		
		try{
			if (graph == null){
				setGraph(new Graph());
				graph.setName("CallGraph");
			}
			else{
				graph.removeAllChildren();
			}
			IEditorPart part = page.openEditor(graph, "ca.mcgill.sable.graph.GraphEditor", true);
			((GraphEditor)part).setPartFactory(new CallGraphPartFactory());
			addActions((GraphEditor)part);	
			((GraphEditor)part).setMenuProvider(new CGMenuProvider(((GraphEditor)part).getGraphEditorGraphicalViewer(), ((GraphEditor)part).getGraphEditorActionRegistry(), part));
			
			buildModel();
		}
		catch (PartInitException e3){
			e3.printStackTrace();
		}
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
			MethInfo mInfo = (MethInfo)it1.next();
			SootMethod sm = mInfo.method();
			CallGraphNode inNode = getNodeForMethod(sm);
			inNode.setGenerator(this);
			Edge inEdge = new Edge(inNode, center);
			inEdge.setLabel(mInfo.edgeKind().name());
		}
		
		Iterator it2 = info.getOutputs().iterator();
		while (it2.hasNext()){
			MethInfo mInfo = (MethInfo)it2.next();
			SootMethod sm = mInfo.method();
			CallGraphNode outNode = getNodeForMethod(sm);
			outNode.setGenerator(this);
			Edge inEdge = new Edge(center, outNode);
			inEdge.setLabel(mInfo.edgeKind().name());
		}	
	}
	
	public void collapseGraph(CallGraphNode node){
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
				if (src.isLeaf()){
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
				if (tgt.isLeaf()){
					outputsToRemove.add(next);
					nodesToRemove.add(tgt);
				}
			}
		}
		
		Iterator inRIt = inputsToRemove.iterator();
		while (inRIt.hasNext()){
			Edge temp = (Edge)inRIt.next();
			node.removeInput(temp);
		}
		
		Iterator outRIt = outputsToRemove.iterator();
		while (outRIt.hasNext()){
			Edge temp = (Edge)outRIt.next();
			node.removeInput(temp);
		}
		
		Iterator nodeRIt = nodesToRemove.iterator();
		while (nodeRIt.hasNext()){
			CallGraphNode temp = (CallGraphNode)nodeRIt.next();
			temp.removeAllInputs();
			temp.removeAllOutputs();
			getGraph().removeChild(temp);
		}
		
		node.setExpand(true);
	}
	
	public void expandGraph(CallGraphNode node){
		getController().setEvent(new InteractionEvent(IInteractionConstants.CALL_GRAPH_NEXT_METHOD, node.getData()));
		getController().handleEvent();
	
	}
	
	public void showInCode(CallGraphNode node){
		SootMethod meth = (SootMethod)node.getData();
		String sootClassName = meth.getDeclaringClass().getName();
		sootClassName = sootClassName.replaceAll("\\.", System.getProperty("file.separator"));
		sootClassName = sootClassName + ".java";
		String sootMethName = meth.getName();
		
		IProject [] progs = SootPlugin.getWorkspace().getRoot().getProjects();
		IResource fileToOpen = null;
		for (int i = 0; i < progs.length; i++){
			IProject project = progs[i];
			
			IJavaProject jProj = JavaCore.create(project);
			try {
	
				IPackageFragmentRoot [] roots = jProj.getAllPackageFragmentRoots();
				for (int j = 0; j < roots.length; j++){
					if (!(roots[j].getResource() instanceof IContainer)) continue;
					fileToOpen = ((IContainer)roots[j].getResource()).findMember(sootClassName);
					if (fileToOpen == null) continue;
					else break;		
				}
			}
			catch(Exception e){
			}
		
			if (fileToOpen != null) break;
		}
		
		
		
		IWorkbench workbench = SootPlugin.getDefault().getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();;
		
		try{
			IEditorPart part = page.openEditor(new FileEditorInput((IFile)fileToOpen), org.eclipse.jdt.ui.JavaUI.ID_CU_EDITOR);
			SourceLnPosTag methTag = (SourceLnPosTag)meth.getTag("SourceLnPosTag");
			if (methTag != null){
				
				int selOffset = ((AbstractTextEditor)part).getDocumentProvider().getDocument(part.getEditorInput()).getLineOffset(methTag.startLn()-1);
			
				((AbstractTextEditor)SootPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()).selectAndReveal(selOffset, 0);
			}
					
		}
		catch (PartInitException e3){
			e3.printStackTrace();
		}
		
		catch (Exception e2){
			e2.printStackTrace();
		}
	}
	
	public void addToGraph(Object info){
		CallGraphInfo cgInfo = (CallGraphInfo)info;
		
		SootMethod center = cgInfo.getCenter();
		
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
