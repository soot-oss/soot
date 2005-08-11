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


package ca.mcgill.sable.graph;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.*;
import org.eclipse.gef.ui.parts.*;
import org.eclipse.gef.*;
import org.eclipse.gef.editparts.*;
import ca.mcgill.sable.graph.model.*;
import ca.mcgill.sable.graph.editparts.*;

import org.eclipse.gef.palette.*;
import org.eclipse.jface.action.*;
import org.eclipse.gef.ui.actions.*;
import ca.mcgill.sable.graph.actions.*;

import java.util.*;


public class GraphEditor extends GraphicalEditor {

	private Graph graph;
	
	/**
	 * 
	 */
	public GraphEditor() {
		DefaultEditDomain defaultEditDomain = new DefaultEditDomain(this);
		setEditDomain(defaultEditDomain);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	protected void initializeGraphicalViewer() {
		getGraphicalViewer().setContents(graph);		
	}
	
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		ScalableRootEditPart root = new ScalableRootEditPart();
		getGraphicalViewer().setRootEditPart(root);
		
		ZoomManager zManager = root.getZoomManager();
		double [] zoomLevels = new double[10];
		for (int i = 0; i < zoomLevels.length; i++){
			zoomLevels[i] = (i + 1) * 0.25;
		}
		zManager.setZoomLevels(zoomLevels);
		IAction zoomIn = new ZoomInAction(zManager);
		IAction zoomOut = new ZoomOutAction(((ScalableRootEditPart)getGraphicalViewer().getRootEditPart()).getZoomManager());

		getActionRegistry().registerAction(zoomIn);
		getActionRegistry().registerAction(zoomOut);
		
		IAction printAction = new PrintAction(this);
		getActionRegistry().registerAction(printAction);
		
		getSite().getKeyBindingService().registerAction(zoomIn);
		getSite().getKeyBindingService().registerAction(zoomOut);
	
		getGraphicalViewer().setEditPartFactory(new PartFactory());
		getGraphicalViewer().setKeyHandler(new GraphicalViewerKeyHandler(getGraphicalViewer()));
		
	}
	
	public void setMenuProvider(ContextMenuProvider prov){
		getGraphicalViewer().setContextMenu(prov);
		getSite().registerContextMenu(prov, getGraphicalViewer());
	}
	
	public void setPartFactory(PartFactory factory){
		getGraphicalViewer().setEditPartFactory(factory);
	}
	
	protected void setInput(IEditorInput input){
		super.setInput(input);
		if (input instanceof Graph){
			setGraph((Graph)input);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
	 */
	public void gotoMarker(IMarker marker) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isDirty()
	 */
	public boolean isDirty() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
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
	public void setGraph(Graph g) {
		graph = g;
	}

	public void setTitle(String name){
		super.setTitle(name);
	}
	
	public void setTitleTooltip(String text){
		super.setTitleToolTip(text);
	}
	
	public void createActions(){
		super.createActions();
		ActionRegistry registry = getActionRegistry();
		IAction action = new SimpleSelectAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());
	}
	
	public ActionRegistry getGraphEditorActionRegistry(){
		return getActionRegistry();
	}
	
	public GraphicalViewer getGraphEditorGraphicalViewer(){
		return getGraphicalViewer();
	}
	
	public List getGraphEditorSelectionActions(){
		return getSelectionActions();
	}
	

	public String getToolTipText(){
		return getTitle();
	}
	
	public String getTitleToolTip(){
		return super.getTitleToolTip();
	}

	
}
