/*
 * Created on Jan 13, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class GraphEditor extends GraphicalEditor {

	private Graph graph;
	//private PaletteRoot root; 
	/**
	 * 
	 */
	public GraphEditor() {
		DefaultEditDomain defaultEditDomain = new DefaultEditDomain(this);
		
		setEditDomain(defaultEditDomain);
		//getEditDomain().setDefaultTool(new SimpleSelectTool());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	protected void initializeGraphicalViewer() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub

	}

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
		//registry.getAction(action.getId());
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
	

	
	
	
	
}
