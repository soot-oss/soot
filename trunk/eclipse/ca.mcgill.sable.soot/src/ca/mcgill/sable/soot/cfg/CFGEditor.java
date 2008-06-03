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

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.*;
import org.eclipse.gef.ui.parts.*;
import org.eclipse.gef.*;
import org.eclipse.gef.editparts.*;
import ca.mcgill.sable.soot.cfg.model.*;
import ca.mcgill.sable.soot.cfg.editParts.*;
//import org.eclipse.gef.palette.*;
import org.eclipse.jface.action.*;
import org.eclipse.gef.ui.actions.*;

public class CFGEditor extends GraphicalEditor {

	private CFGGraph cfgGraph;
	
	public CFGEditor() {
		DefaultEditDomain defaultEditDomain = new DefaultEditDomain(this);
		setEditDomain(defaultEditDomain);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	protected void initializeGraphicalViewer() {
		getGraphicalViewer().setContents(cfgGraph);		

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
		
		getSite().getKeyBindingService().registerAction(zoomIn);
		getSite().getKeyBindingService().registerAction(zoomOut);
	
	
		getGraphicalViewer().setEditPartFactory(new CFGPartFactory());
		getGraphicalViewer().setKeyHandler(new GraphicalViewerKeyHandler(getGraphicalViewer()));
		
		StopAction stop = new StopAction(this);
		getActionRegistry().registerAction(stop);
	 	this.getSelectionActions().add(stop.getId());
		
		UnStopAction unStop = new UnStopAction(this);
		getActionRegistry().registerAction(unStop);
	 	this.getSelectionActions().add(unStop.getId());
		
		
		CFGMenuProvider menuProvider = new CFGMenuProvider(getGraphicalViewer(), getActionRegistry(), this);
		getGraphicalViewer().setContextMenu(menuProvider);
		getSite().registerContextMenu(menuProvider, getGraphicalViewer());
	
	}
	
	// this is for zoom
	protected void createActions(){
	
		super.createActions();
		
	/*	System.out.println("creating actions");
		ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer)getGraphicalViewer();
		ScalableRootEditPart root = ((ScalableRootEditPart)viewer.getRootEditPart());
		ZoomManager zManager = root.getZoomManager();
		
		IAction zoomIn = new ZoomInAction(zManager);
		IAction zoomOut = new ZoomOutAction(((ScalableRootEditPart)getGraphicalViewer().getRootEditPart()).getZoomManager());

		getActionRegistry().registerAction(zoomIn);
		getActionRegistry().registerAction(zoomOut);
		
		getSite().getKeyBindingService().registerAction(zoomIn);
		getSite().getKeyBindingService().registerAction(zoomOut);
	*/
	
	}

	protected void setInput(IEditorInput input){
		super.setInput(input);
		if (input instanceof CFGGraph){
			setCfgGraph((CFGGraph)input);
		}
		// could also read from a dot file
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		/*System.out.println("saving cfgs");
		// idea is to save to dot file
		String fileNameBase = SootPlugin.getDefault().getCurrentProject().getFolder("sootOutput").getLocation().toOSString();
		
		AnnotatedCFGSaver saver = new AnnotatedCFGSaver(getCfgGraph(), fileNameBase, this.getTitle());
		saver.saveGraph();
		isSaved = true;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	*/
	}

	/*public void setContentsChanged(){
		isSaved = false;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}*/
	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
	 */
	public void gotoMarker(IMarker marker) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isDirty()
	 */
	public boolean isDirty() {
		// TODO Auto-generated method stub
		//return !isSaved;
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * @return
	 */
	public CFGGraph getCfgGraph() {
		return cfgGraph;
	}

	/**
	 * @param graph
	 */
	public void setCfgGraph(CFGGraph graph) {
		cfgGraph = graph;
	}

	public void setTitle(String name){
		super.setTitle(name);
	}
	
	public void setTitleTooltip(String text){
		super.setTitleToolTip(text);
	}
	
	public String getToolTipText(){
		return "cfg editor";
	}
	

	
	
}
