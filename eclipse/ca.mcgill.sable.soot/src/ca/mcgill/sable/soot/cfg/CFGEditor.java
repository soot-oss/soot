/*
 * Created on Jan 13, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ca.mcgill.sable.soot.cfg;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.*;
import org.eclipse.gef.ui.parts.*;
import org.eclipse.gef.*;
import org.eclipse.gef.editparts.*;
import ca.mcgill.sable.soot.cfg.model.*;
import ca.mcgill.sable.soot.cfg.editParts.*;
import org.eclipse.gef.palette.*;
import org.eclipse.jface.action.*;
import org.eclipse.gef.ui.actions.*;

import java.util.*;

/**
 * @author jlhotak
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CFGEditor extends GraphicalEditor {

	private CFGGraph cfgGraph;
	//private PaletteRoot root; 
	/**
	 * 
	 */
	public CFGEditor() {
		DefaultEditDomain defaultEditDomain = new DefaultEditDomain(this);
		setEditDomain(defaultEditDomain);
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	protected void initializeGraphicalViewer() {
		// TODO Auto-generated method stub
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
		
	}
	
	// this is for zoom
	/*protected void createActions(){
	
		super.createActions();
		
		System.out.println("creating actions");
		ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer)getGraphicalViewer();
		ScalableRootEditPart root = ((ScalableRootEditPart)viewer.getRootEditPart());
		ZoomManager zManager = root.getZoomManager();
		
		IAction zoomIn = new ZoomInAction(zManager);
		IAction zoomOut = new ZoomOutAction(((ScalableRootEditPart)getGraphicalViewer().getRootEditPart()).getZoomManager());

		getActionRegistry().registerAction(zoomIn);
		getActionRegistry().registerAction(zoomOut);
		
		getSite().getKeyBindingService().registerAction(zoomIn);
		getSite().getKeyBindingService().registerAction(zoomOut);
	}*/

	protected void setInput(IEditorInput input){
		super.setInput(input);
		if (input instanceof CFGGraph){
			setCfgGraph((CFGGraph)input);
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
	
	
	
}
